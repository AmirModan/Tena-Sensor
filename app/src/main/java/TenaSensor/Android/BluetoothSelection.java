package TenaSensor.Android;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class BluetoothSelection extends Activity {

    // GUI Components
    private BluetoothAdapter mBTAdapter;
    private Set<BluetoothDevice> mPairedDevices;
    private ArrayAdapter<String> mBTArrayAdapter;
    private ListView mDevicesListView;
    private TextView tv;
    private LinearLayout cv, iv, bv;
    private Button successBtn;

    private CountDownTimer btTimer;

    private static String name, address;
    private static short accepted = 2;

    public static String nameKey = "Name Key";
    public static String addressKey = "Address Key";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_bluetooth);

        tv = (TextView) findViewById(R.id.BT_Success_Message);
        cv = (LinearLayout) findViewById(R.id.ConnectingView);
        iv = (LinearLayout) findViewById(R.id.BT_Success_Image);
        bv = (LinearLayout) findViewById(R.id.BT_Success_Button);
        successBtn = (Button) findViewById(R.id.FinishBT);

        mBTArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        mBTAdapter = BluetoothAdapter.getDefaultAdapter(); // get a handle on the bluetooth radio

        listPairedDevices();

        mDevicesListView = (ListView) findViewById(R.id.devicesListView);
        mDevicesListView.setAdapter(mBTArrayAdapter); // assign model to view
        mDevicesListView.setOnItemClickListener(mDeviceClickListener);

        successBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selectIntent = new Intent(BluetoothSelection.this, MainActivity.class);
                BluetoothSelection.this.startActivity(selectIntent);
            }
        });
    }
/*
    private void discover(View view) {
        // Check if the device is already discovering
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (mBTAdapter.isDiscovering()) {
            mBTAdapter.cancelDiscovery();
            Toast.makeText(getApplicationContext(), "Discovery stopped", Toast.LENGTH_SHORT).show();
        } else {
            if (mBTAdapter.isEnabled()) {
                mBTArrayAdapter.clear(); // clear items
                mBTAdapter.startDiscovery();
                Toast.makeText(getApplicationContext(), "Discovery started", Toast.LENGTH_SHORT).show();
                registerReceiver(blReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            } else {
                Toast.makeText(getApplicationContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
            }
        }
    }

    final BroadcastReceiver blReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name to the list
                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                mBTArrayAdapter.notifyDataSetChanged();
            }
        }
    };*/

    private void listPairedDevices() {
        mPairedDevices = mBTAdapter.getBondedDevices();
        if(mBTAdapter.isEnabled()) {
            // put it's one to the adapter
            for (BluetoothDevice device : mPairedDevices)
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
        }
    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            address = info.substring(info.length() - 17);
            name = info.substring(0,info.length() - 17);

            startService(new Intent(getApplicationContext(), BluetoothService.class));

            Toast.makeText(getApplicationContext(), "Connecting to " + name, Toast.LENGTH_SHORT).show();

            btTimer = new CountDownTimer(5000, 10) {
                public void onTick(long millisUntilFinished) {
                    if(accepted == 1) {
                        /*
                        SharedPreferences sharedPreferences = getSharedPreferences("MAC", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("1", address);
                        editor.commit();
                         */
                        cv.setVisibility(View.GONE);
                        tv.setVisibility(View.VISIBLE);
                        iv.setVisibility(View.VISIBLE);
                        bv.setVisibility(View.VISIBLE);
                    }
                    else if(accepted == 0) {
                        this.cancel();
                        Toast.makeText(getApplicationContext(), "Device Not Found", Toast.LENGTH_SHORT).show();
                    }
                }
                public void onFinish() {
                    if(accepted != 1) {
                        Toast.makeText(getApplicationContext(), "Device Not Found", Toast.LENGTH_SHORT).show();
                    }
                }
            };
            btTimer.start();
        }
    };

    public static String getAddress() {
        return address;
    }
    public static void deviceAccepted(boolean deviceAccepted) {
        if(deviceAccepted) {
            accepted = 1;

        }
        else {
            accepted = 0;
        }
    }
}