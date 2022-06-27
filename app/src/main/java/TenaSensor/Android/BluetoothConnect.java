package TenaSensor.Android;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Set;

import com.amazonaws.mobileconnectors.lambdainvoker.*;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;

/**
 * @author Amir Modan (amir5modan@gmail.com)
 * Fragment which acts as the home page for the T'ena Sensor App
 *
 * Functions include:
 *  Connecting to existing T'ena Sensor by clicking on red logo (No setup required)
 *  Connecting to new T'ena Sensor by clicking on settings icon (Setup required)
 *  Disconnecting to an already connected T'ena Sensor by clicking on green logo
 *  Initiating T'ena Sensor calibration process
 *  Navigating to exercise selection screen
 *  Displaying trial statistics after exercises have been completed
 */
public class BluetoothConnect extends Fragment {

    // GUI Components
    private TextView mSpeedBuffer, mSmoothnessBuffer, mTimeBuffer;
    private ImageView connectedImage, disconnectedImage;
    private BluetoothAdapter mBTAdapter;
    private Set<BluetoothDevice> mPairedDevices;
    private ArrayAdapter<String> mBTArrayAdapter;

    private Handler mHandler; // Our main handler that will receive callback notifications

    private TextView status;
    private Button bluetoothButton, exerciseButton, calibrateButton;
    private static boolean connected = false;

    private String name, address;
    private String filename;
    private String message = "";

    private File file, path;
    private FileOutputStream stream;

    private int mInterval = 500;

    private static String nameKey = "Name Key";
    private static String addressKey = "Address Key";
    private static String exercise = "Exercise";
    public static String ip = "192.168.1.133";
    public static int port = 8080;

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            if(BluetoothService.getOutputSpeed() != null) {
                mSpeedBuffer.setText(BluetoothService.getOutputSpeed());
                mSmoothnessBuffer.setText((BluetoothService.getOutputSmoothness()));
                mTimeBuffer.setText(BluetoothService.getOutputTime());
            }
            if(connected) {
                connectedImage.setVisibility(View.VISIBLE);
                disconnectedImage.setVisibility(View.INVISIBLE);
                status.setText("Your sensor is connected");
            } else {
                connectedImage.setVisibility(View.INVISIBLE);
                disconnectedImage.setVisibility(View.VISIBLE);
                status.setText("Click the icon to connect your sensor");
            }
            mHandler.postDelayed(mStatusChecker, mInterval);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View v = inflater.inflate(R.layout.activity_bluetooth, container, false);
        assert v != null;

        bluetoothButton = v.findViewById(R.id.ToggleBluetooth);
        exerciseButton = v.findViewById(R.id.ExerciseButton);
        calibrateButton = v.findViewById(R.id.CalibrateButton);
        connectedImage = v.findViewById(R.id.connected);
        disconnectedImage = v.findViewById(R.id.disconnected);
        status = v.findViewById(R.id.bt_status);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Gets extras from previous class
        Intent intent = getActivity().getIntent();
        Bundle bundle = intent.getExtras();

        // Instantiate GUI components
        mSpeedBuffer = (TextView) view.findViewById(R.id.speedBuffer);
        mSmoothnessBuffer = (TextView) view.findViewById(R.id.smoothnessBuffer);
        mTimeBuffer = (TextView) view.findViewById(R.id.timeBuffer);

        // Fields for entering IP and Port Number (not for final app)
        EditText text = view.findViewById(R.id.ip);
        text.setText(ip);
        EditText textPort = view.findViewById(R.id.port);
        textPort.setText(Integer.toString(port));

        super.onCreate(savedInstanceState);

        mBTArrayAdapter = new ArrayAdapter<String>(this.getActivity(),android.R.layout.simple_list_item_1);
        mBTAdapter = BluetoothAdapter.getDefaultAdapter(); // get a handle on the bluetooth radio

        mHandler = new Handler();
        mStatusChecker.run();

        // Create an instance of CognitoCachingCredentialsProvider
        CognitoCachingCredentialsProvider cognitoProvider = new CognitoCachingCredentialsProvider(this.getContext(), "us-east-1:68a62898-4649-4512-ba39-cdeae41bca18", Regions.US_EAST_1);

    // Create LambdaInvokerFactory, to be used to instantiate the Lambda proxy.
        LambdaInvokerFactory factory = new LambdaInvokerFactory(this.getContext(),
                Regions.US_EAST_1, cognitoProvider);

        // Create the Lambda proxy object with a default Json data binder.
        // You can provide your own data binder by implementing
        // LambdaDataBinder.
        final AWS_Interface myInterface = factory.build(AWS_Interface.class);

        AWS_Request request = new AWS_Request("Amir", "Modan");
        // The Lambda function invocation results in a network call.
        // Make sure it is not called from the main thread.
        new AsyncTask<AWS_Request, Void, AWS_Response>() {
            @Override
            protected AWS_Response doInBackground(AWS_Request... params) {
                // invoke "echo" method. In case it fails, it will throw a
                // LambdaFunctionException.
                try {
                    return myInterface.TenaFunction1(params[0]);
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

                // Do a toast
                Toast.makeText(BluetoothConnect.this.getContext(), result.getGreetings(), Toast.LENGTH_LONG).show();
            }
        }.execute(request);

        //
        try {
            filename = bundle.getString(exercise) + ".txt";
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(filename != null) {
            path = getContext().getExternalFilesDir(null);
            file = new File(path, filename);
            try {
                stream = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        // Toggle connection to T'ena Sensor
        bluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().startService(new Intent(getContext(), BluetoothService.class));
            }
        });

        // Navigate to ExerciseSelection.java, the exercise selection activity
        exerciseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!text.getText().toString().isEmpty()) {
                    ip = text.getText().toString();
                }
                if(!textPort.getText().toString().isEmpty()) {
                    port = Integer.parseInt(textPort.getText().toString());
                }
                Intent exerciseIntent = new Intent(getContext(), ExerciseSelection.class);
                startActivity(exerciseIntent);
            }
        });

        // Navigate to SensorCalibration.java, the sensor calibration activity
        calibrateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent calibrateIntent = new Intent(getContext(), SensorCalibration.class);
                startActivity(calibrateIntent);
            }
        });

        // When directed from T'ena Sensor setup activity (BluetoothSelection.java), retains Bluetooth connection
        if(bundle != null && !connected){
            name = bundle.getString(nameKey);
            address = bundle.getString(addressKey);
            getContext().startService(new Intent(getContext(), BluetoothService.class));
        }
    }

    /**
     * Updates the connection status of the T'ena Sensor
     * @param deviceConnected Boolean describing whether or not the sensor is connected
     */
    public static void updateView(boolean deviceConnected) {
        connected = deviceConnected;
    }

    /**
     * Gets the current exercise
     * @return A string representing the current exercise
     */
    public static String getExercise() {
        return exercise;
    }
}
