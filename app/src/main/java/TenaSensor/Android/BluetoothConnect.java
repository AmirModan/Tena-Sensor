package TenaSensor.Android;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Set;
import java.util.UUID;

/**
 * Created by User on 2/28/2017.
 */

public class BluetoothConnect extends Fragment {
    // GUI Components
    private TextView mSpeedBuffer, mSmoothnessBuffer, mTimeBuffer;
    private ImageView connectedImage, disconnectedImage;
    private BluetoothAdapter mBTAdapter;
    private Set<BluetoothDevice> mPairedDevices;
    private ArrayAdapter<String> mBTArrayAdapter;

    private Handler mHandler; // Our main handler that will receive callback notifications
    private BluetoothSocket mBTSocket = null; // bi-directional client-to-client data path
    //private SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);

    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier


    // #defines for identifying shared types between calling functions
    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status


    private static final String TAG = "Tab2Fragment";

    Activity activity;
    private TextView status;
    private Button bluetoothButton, exerciseButton, calibrateButton;
    private static boolean connected = false;
    public static String speed = "";

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
        Intent intent = getActivity().getIntent();
        Bundle bundle = intent.getExtras();
        mSpeedBuffer = (TextView) view.findViewById(R.id.speedBuffer);
        mSmoothnessBuffer = (TextView) view.findViewById(R.id.smoothnessBuffer);
        mTimeBuffer = (TextView) view.findViewById(R.id.timeBuffer);
        EditText text = view.findViewById(R.id.ip);
        text.setText(ip);
        EditText textPort = view.findViewById(R.id.port);
        textPort.setText(Integer.toString(port));

        super.onCreate(savedInstanceState);

        mBTArrayAdapter = new ArrayAdapter<String>(this.getActivity(),android.R.layout.simple_list_item_1);
        mBTAdapter = BluetoothAdapter.getDefaultAdapter(); // get a handle on the bluetooth radio

        mHandler = new Handler();
        activity = this.getActivity();
        mStatusChecker.run();

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

        bluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().startService(new Intent(getContext(), BluetoothService.class));
            }
        });

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

        calibrateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent calibrateIntent = new Intent(getContext(), SensorCalibration.class);
                startActivity(calibrateIntent);
            }
        });

        if(bundle != null && !connected){
            name = bundle.getString(nameKey);
            address = bundle.getString(addressKey);
            getContext().startService(new Intent(getContext(), BluetoothService.class));
        }
    }

    public static void updateView(boolean deviceConnected) {
        connected = deviceConnected;
    }
    public static String getExercise() {
        return exercise;
    }
}
