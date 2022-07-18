package TenaSensor.Android;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;

import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunctionException;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaInvokerFactory;
import com.amazonaws.regions.Regions;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BluetoothService extends Service {

    final int handlerState = 0;                        //used to identify handler message
    Handler bluetoothIn;
    private BluetoothAdapter btAdapter = null;

    private ConnectingThread mConnectingThread;
    private ConnectedThread mConnectedThread;

    private boolean stopThread;
    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    // String for MAC address
    private static String MAC_ADDRESS;

    private String filename;

    private File file, path;
    private FileOutputStream stream;

    private StringBuilder recDataString = new StringBuilder();

    private BufferedReader inputStream;
    private DataOutputStream outputStream;
    private Socket socket;

    private List<String> recorded_data = new ArrayList<>();
    private List<Integer> data = new ArrayList<>();
    private String messageFragment = "";
    private static String speed = "";
    private static String smoothness = "";
    private static String time = "";

    private SignalDetector detector = new SignalDetector();
    private List<Double> accx = new ArrayList<>();
    private List<Double> accy = new ArrayList<>();
    private List<Double> accz = new ArrayList<>();
    private List<Double> gyrx = new ArrayList<>();
    private List<Double> gyry = new ArrayList<>();
    private List<Double> gyrz = new ArrayList<>();
    private double[] calib_flat = {0,0,0,0,0,0};
    private double[] calib_side = {0,0,0,0,0,0};
    private int lag = 30;
    private double threshold = 5;
    private double influence = 0;

    public static SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("BT SERVICE", "SERVICE CREATED");
        stopThread = false;

        if(BluetoothSelection.getAddress() != null) {
            MAC_ADDRESS = BluetoothSelection.getAddress();
        }
        else {
            sharedPreferences = getSharedPreferences("MAC", MODE_PRIVATE);
            Map<String, ?> savedDevices = sharedPreferences.getAll();
            MAC_ADDRESS = (String) savedDevices.get("1");
        }
        //MAC_ADDRESS = "34:AB:95:E8:E4:36";
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("BT SERVICE", "SERVICE STARTED");
        bluetoothIn = new Handler() {

            public void handleMessage(Message msg) {
                Log.d("DEBUG", "handleMessage");
                if (msg.what == handlerState) {                       //if message is what we want
                    String readMessage = (String) msg.obj;                                                                // msg.arg1 = bytes from connect thread
                    recDataString.append(readMessage);
                    BluetoothConnect.updateView(true);
                    Log.d("RECORDED", recDataString.toString());
                    // Do stuff here with your data, like adding it to the database
                    if(ExercisePerform.isRecording()) {
                        if(stream == null) {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy-hh-mm-ss");
                            String format = simpleDateFormat.format(new Date());
                            try {
                                filename = ExerciseSelection.getExercise() + ExercisePerform.getTrial() + format + ".txt";
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (filename != null) {
                                path = getExternalFilesDir(null);
                                file = new File(path, filename);
                                try {
                                    stream = new FileOutputStream(file);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                                Log.d("Writing to file ", filename);
                            }


                        }
                        if(stream != null) {
                            try {
                                stream.write(readMessage.getBytes());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        recorded_data.add(readMessage);

                        for(String message :  readMessage.split("\n")) {
                            if(!messageFragment.isEmpty()) {
                                message = messageFragment + message;
                                messageFragment = "";
                            }
                            String[] splitMessage = message.split(", ");
                            if(splitMessage.length == 7 && message.length() >= 60 && message.charAt(0) == ',') {
                                double[] sample = new double[splitMessage.length];
                                for(int i = 0; i < splitMessage.length; i++){
                                    try {
                                        sample[i] = Double.parseDouble(splitMessage[i]);
                                    } catch (NumberFormatException e) {
                                        e.printStackTrace();
                                    }
                                }
                                accx.add(sample[1]);
                                accy.add(sample[2]);
                                accz.add(sample[3]);
                                gyrx.add(sample[4]);
                                gyry.add(sample[5]);
                                gyrz.add(sample[6]);

                            } else if(splitMessage.length > 7) {
                            }
                            else {
                                messageFragment = message;
                            }
                        }

                    }
                    else if(ExercisePerform.isComplete()) {
                        ExercisePerform.setComplete(false);
                        // Create an instance of CognitoCachingCredentialsProvider
                        CognitoCachingCredentialsProvider cognitoProvider = new CognitoCachingCredentialsProvider(getApplicationContext(), "us-east-1:68a62898-4649-4512-ba39-cdeae41bca18", Regions.US_EAST_1);

                        // Create LambdaInvokerFactory, to be used to instantiate the Lambda proxy.
                        LambdaInvokerFactory factory = new LambdaInvokerFactory(getApplicationContext(),
                                Regions.US_EAST_1, cognitoProvider);

                        // Create the Lambda proxy object with a default Json data binder.
                        // You can provide your own data binder by implementing
                        // LambdaDataBinder.
                        final AWS_Interface myInterface = factory.build(AWS_Interface.class);

                        AWS_Request request = new AWS_Request(accx, ExerciseSelection.getExercise(),1,1);
                        // The Lambda function invocation results in a network call.
                        // Make sure it is not called from the main thread.
                        new AsyncTask<AWS_Request, Void, AWS_Response>() {
                            @Override
                            protected AWS_Response doInBackground(AWS_Request... params) {
                                // invoke "echo" method. In case it fails, it will throw a
                                // LambdaFunctionException.
                                try {
                                    return myInterface.TenaFunction(params[0]);
                                } catch (LambdaFunctionException lfe) {
                                    Log.e("Tag", "Failed to invoke echo", lfe);
                                    return null;
                                }
                            }

                            @Override
                            protected void onPostExecute(AWS_Response result) {
                                if (result == null) {
                                    return;
                                }

                                // Sends statistics to home page class
                                BluetoothConnect.setStats(Float.toString(result.getSpeed()), Float.toString(result.getSmoothness()), Float.toString(result.getTime()));
                            }
                        }.execute(request);

                        new AsyncTask<Void,Void,Void>(){
                            @Override
                            protected Void doInBackground(Void... params) {
                                try {
                                    socket = new Socket(BluetoothConnect.ip , BluetoothConnect.port);
                                    inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                                    outputStream = new DataOutputStream(socket.getOutputStream());

                                    outputStream.writeBytes("Start,");
                                    for(String data : recorded_data) {
                                        outputStream.writeBytes(data);
                                    }
                                    recorded_data = new ArrayList<>();
                                    outputStream.writeBytes("Stop");
                                    while(speed.isEmpty()) {
                                        String input = inputStream.readLine();
                                        if(input != null) {
                                            Log.d("Acknowledgment: ", input);
                                            String[] serverOutput = input.split(",");
                                            if (serverOutput.length == 3) {
                                                speed = serverOutput[0];
                                                smoothness = serverOutput[1];
                                                time = serverOutput[2];
                                            }
                                        }
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }
                        }.execute();
                        accx = new ArrayList<>();
                        accy = new ArrayList<>();
                        accz = new ArrayList<>();
                        gyrx = new ArrayList<>();
                        gyry = new ArrayList<>();
                        gyrz = new ArrayList<>();
                    } else if(SensorCalibration.isCalibrating() != 0) {
                        for(String message :  readMessage.split("\n")) {
                            if(!messageFragment.isEmpty()) {
                                message = messageFragment + message;
                                messageFragment = "";
                            }
                            String[] splitMessage = message.split(", ");
                            if(splitMessage.length == 7 && message.length() >= 60 && message.charAt(0) == ',') {
                                double[] sample = new double[splitMessage.length];
                                for(int i = 0; i < splitMessage.length; i++){
                                    try {
                                        sample[i] = Double.parseDouble(splitMessage[i]);
                                    } catch (NumberFormatException e) {
                                        e.printStackTrace();
                                    }
                                }
                                accx.add(sample[1]);
                                accy.add(sample[2]);
                                accz.add(sample[3]);
                                gyrx.add(sample[4]);
                                gyry.add(sample[5]);
                                gyrz.add(sample[6]);

                            } else if(splitMessage.length > 7) {
                            }
                            else {
                                messageFragment = message;
                            }
                        }

                        if(accx.size() >= 300) {

                            Map<String, List> accxMap = detector.analyzeDataForSignals(accx, lag, threshold, influence);
                            Map<String, List> acczMap = detector.analyzeDataForSignals(accz, lag, threshold, influence);
                            Map<String, List> gyrzMap = detector.analyzeDataForSignals(gyrz, lag, threshold, influence);

                            accx = new ArrayList<>();
                            accz = new ArrayList<>();
                            gyrz = new ArrayList<>();

                            boolean calibration_failed = false;

                            for(int element : (List<Integer>) accxMap.get("signals")){
                                if(element != 0) {
                                    calibration_failed = true;
                                    break;
                                }
                            }
                            for(int element : (List<Integer>) acczMap.get("signals")){
                                if(element != 0) {
                                    calibration_failed = true;
                                    break;
                                }
                            }
                            for(double element : (List<Double>) gyrzMap.get("stdFilter")){
                                if(element > 1.0) {
                                    calibration_failed = true;
                                    break;
                                }
                            }
                            for(Object element : accxMap.get("stdFilter")){
                                if((Double) element > 0.10) {
                                    calibration_failed = true;
                                    break;
                                }
                            }
                            SensorCalibration.stopCalibration(calibration_failed);
                            if(!calibration_failed) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    if(SensorCalibration.isCalibrating() == 1) {
                                        calib_flat[0] = accx.stream().mapToDouble(val -> val).average().orElse(0.0);
                                        calib_flat[1] = accy.stream().mapToDouble(val -> val).average().orElse(0.0);
                                        calib_flat[2] = accz.stream().mapToDouble(val -> val).average().orElse(0.0);
                                        calib_flat[3] = gyrx.stream().mapToDouble(val -> val).average().orElse(0.0);
                                        calib_flat[4] = gyry.stream().mapToDouble(val -> val).average().orElse(0.0);
                                        calib_flat[5] = gyrz.stream().mapToDouble(val -> val).average().orElse(0.0);
                                    } else if(SensorCalibration.isCalibrating() == 2) {
                                        calib_side[0] = accx.stream().mapToDouble(val -> val).average().orElse(0.0);
                                        calib_side[1] = accy.stream().mapToDouble(val -> val).average().orElse(0.0);
                                        calib_side[2] = accz.stream().mapToDouble(val -> val).average().orElse(0.0);
                                        calib_side[3] = gyrx.stream().mapToDouble(val -> val).average().orElse(0.0);
                                        calib_side[4] = gyry.stream().mapToDouble(val -> val).average().orElse(0.0);
                                        calib_side[5] = gyrz.stream().mapToDouble(val -> val).average().orElse(0.0);
                                    }
                                }
                            }

                        }





                    }

                }
                recDataString.delete(0, recDataString.length());                    //clear all string data
            }
        };


        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bluetoothIn.removeCallbacksAndMessages(null);
        stopThread = true;
        if(stream != null) {
            try {
                stream.close();
                stream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mConnectedThread != null) {
            mConnectedThread.closeStreams();
        }
        if (mConnectingThread != null && socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mConnectingThread.closeSocket();
        }
        BluetoothSelection.deviceAccepted(false);
        BluetoothConnect.updateView(false);
        Log.d("SERVICE", "onDestroy");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //Checks that the Android device Bluetooth is available and prompts to be turned on if off
    private void checkBTState() {

        if (btAdapter == null) {
            Log.d("BT SERVICE", "BLUETOOTH NOT SUPPORTED BY DEVICE, STOPPING SERVICE");
            BluetoothConnect.updateView(false);
            stopSelf();
        } else {
            if (btAdapter.isEnabled()) {
                Log.d("DEBUG BT", "BT ENABLED! BT ADDRESS : " + btAdapter.getAddress() + " , BT NAME : " + btAdapter.getName());
                try {
                    BluetoothDevice device = btAdapter.getRemoteDevice(MAC_ADDRESS);
                    Log.d("DEBUG BT", "ATTEMPTING TO CONNECT TO REMOTE DEVICE : " + MAC_ADDRESS);
                    mConnectingThread = new ConnectingThread(device);
                    mConnectingThread.start();
                } catch (IllegalArgumentException e) {
                    Log.d("DEBUG BT", "PROBLEM WITH MAC ADDRESS : " + e.toString());
                    Log.d("BT SEVICE", "ILLEGAL MAC ADDRESS, STOPPING SERVICE");
                    BluetoothConnect.updateView(false);
                    stopSelf();
                }
            } else {
                Log.d("BT SERVICE", "BLUETOOTH NOT ON, STOPPING SERVICE");
                BluetoothConnect.updateView(false);
                stopSelf();
            }
        }
    }

    // New Class for Connecting Thread
    private class ConnectingThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectingThread(BluetoothDevice device) {
            Log.d("DEBUG BT", "IN CONNECTING THREAD");
            mmDevice = device;
            BluetoothSocket temp = null;
            Log.d("DEBUG BT", "MAC ADDRESS : " + MAC_ADDRESS);
            Log.d("DEBUG BT", "BT UUID : " + BTMODULEUUID);
            try {
                temp = mmDevice.createRfcommSocketToServiceRecord(BTMODULEUUID);
                Log.d("DEBUG BT", "SOCKET CREATED : " + temp.toString());
            } catch (IOException e) {
                Log.d("DEBUG BT", "SOCKET CREATION FAILED :" + e.toString());
                Log.d("BT SERVICE", "SOCKET CREATION FAILED, STOPPING SERVICE");
                stopSelf();
            }
            mmSocket = temp;
        }

        @Override
        public void run() {
            super.run();
            Log.d("DEBUG BT", "IN CONNECTING THREAD RUN");
            // Establish the Bluetooth socket connection.
            // Cancelling discovery as it may slow down connection
            btAdapter.cancelDiscovery();
            try {
                mmSocket.connect();
                Log.d("DEBUG BT", "BT SOCKET CONNECTED");
                mConnectedThread = new ConnectedThread(mmSocket);
                mConnectedThread.start();
                Log.d("DEBUG BT", "CONNECTED THREAD STARTED");
                //I send a character when resuming.beginning transmission to check device is connected
                //If it is not an exception will be thrown in the write method and finish() will be called
                mConnectedThread.write("x");
                BluetoothSelection.deviceAccepted(true);
            } catch (IOException e) {
                try {
                    Log.d("DEBUG BT", "SOCKET CONNECTION FAILED : " + e.toString());
                    Log.d("BT SERVICE", "SOCKET CONNECTION FAILED, STOPPING SERVICE");
                    mmSocket.close();
                    stopSelf();
                } catch (IOException e2) {
                    Log.d("DEBUG BT", "SOCKET CLOSING FAILED :" + e2.toString());
                    Log.d("BT SERVICE", "SOCKET CLOSING FAILED, STOPPING SERVICE");
                    stopSelf();
                    //insert code to deal with this
                }
            } catch (IllegalStateException e) {
                Log.d("DEBUG BT", "CONNECTED THREAD START FAILED : " + e.toString());
                Log.d("BT SERVICE", "CONNECTED THREAD START FAILED, STOPPING SERVICE");
                stopSelf();
            }
        }

        public void closeSocket() {
            try {
                //Don't leave Bluetooth sockets open when leaving activity
                mmSocket.close();
            } catch (IOException e2) {
                //insert code to deal with this
                Log.d("DEBUG BT", e2.toString());
                Log.d("BT SERVICE", "SOCKET CLOSING FAILED, STOPPING SERVICE");
                stopSelf();
            }
        }
    }

    // New Class for Connected Thread
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            Log.d("DEBUG BT", "IN CONNECTED THREAD");
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.d("DEBUG BT", e.toString());
                Log.d("BT SERVICE", "UNABLE TO READ/WRITE, STOPPING SERVICE");
                stopSelf();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            Log.d("DEBUG BT", "IN CONNECTED THREAD RUN");
            byte[] buffer = new byte[256];
            int bytes;

            // Keep looping to listen for received messages
            while (true && !stopThread) {
                try {
                    bytes = mmInStream.read(buffer);            //read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    Log.d("DEBUG BT PART", "CONNECTED THREAD " + readMessage);
                    // Send the obtained bytes to the UI Activity via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    Log.d("DEBUG BT", e.toString());
                    Log.d("BT SERVICE", "UNABLE TO READ/WRITE, STOPPING SERVICE");
                    stopSelf();
                    break;
                }
            }
        }

        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                Log.d("DEBUG BT", "UNABLE TO READ/WRITE " + e.toString());
                Log.d("BT SERVICE", "UNABLE TO READ/WRITE, STOPPING SERVICE");
                stopSelf();
            }
        }

        public void closeStreams() {
            try {
                //Don't leave Bluetooth sockets open when leaving activity
                mmInStream.close();
                mmOutStream.close();
            } catch (IOException e2) {
                //insert code to deal with this
                Log.d("DEBUG BT", e2.toString());
                Log.d("BT SERVICE", "STREAM CLOSING FAILED, STOPPING SERVICE");
                stopSelf();
            }
        }
    }
    public static String getOutputSpeed(){
        return speed;
    }

    public static String getOutputSmoothness(){
        return smoothness;
    }

    public static String getOutputTime(){
        return time;
    }
}