package TenaSensor.Android;

import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

public class SensorCalibration extends AppCompatActivity {

    private Button calibrateBtn, homeBtn;
    private TextView instruction;
    private ImageView calibrationImg;
    private boolean timeout = false;
    private boolean flat_complete = false;
    private static short calibrating = 0;
    private static boolean calibration_failed = false;

    //Declare timer
    private CountDownTimer cTimer, finishTimer;

    Runnable myRunnable = new Runnable() {
        @Override
        public void run() {
            while (calibrating != 0) {
                /*if (timeout) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Calibration timed out, please try again", Toast.LENGTH_SHORT).show();
                            timeout = false;
                            calibrateBtn.setEnabled(true);
                            calibrateBtn.setText("Calibrate Sensor");
                            calibrating = 0;
                        }
                    });
                    return;
                }*/
            }
            cancelTimer();
            if(calibration_failed) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"Calibration failed, please try again",Toast.LENGTH_SHORT).show();
                        calibrateBtn.setEnabled(true);
                        calibrateBtn.setText("Calibrate Sensor");
                    }
                });
            } else if(!flat_complete) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        flat_complete = true;
                        instruction.setText("Place the sensor on its side.\nWhen ready, tap Calibrate Sensor");
                        calibrateBtn.setEnabled(true);
                        calibrateBtn.setText("Calibrate Sensor");
                        calibrationImg.setImageResource(R.drawable.calibrationside);
                    }
                });
            } else {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"Calibration Successful",Toast.LENGTH_SHORT).show();
                        instruction.setText("Your T'ena Sensor is now calibrated");
                        calibrateBtn.setVisibility(View.GONE);
                        finishTimer = new CountDownTimer(3000, 10) {
                            public void onTick(long millisUntilFinished) {

                            }
                            public void onFinish() {
                                Intent continueIntent = new Intent(SensorCalibration.this, MainActivity.class);
                                SensorCalibration.this.startActivity(continueIntent);
                            }
                        };
                        finishTimer.start();
                    }
                });
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration);
        calibrateBtn = (Button)findViewById(R.id.calibrate);
        instruction = (TextView)findViewById(R.id.calibrate_instructions);
        calibrationImg = (ImageView)findViewById(R.id.calibrate_image);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.header);

        homeBtn = (Button)findViewById(R.id.home_button);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SensorCalibration.this, MainActivity.class);
                SensorCalibration.this.startActivity(intent);
            }
        });

        calibrateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //calibrateBtn.setVisibility(GONE);
                calibrateBtn.setEnabled(false);
                calibrateBtn.setText("Calibrating… Do not move sensor");
                if(flat_complete) {
                    calibrating = 2;
                } else {
                    calibrating = 1;
                }
                calibration_failed = false;
                timeout = false;
                startTimer();

                Thread myThread = new Thread(myRunnable);
                myThread.start();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelTimer();
    }

    //start timer function
    void startTimer() {
        cTimer = new CountDownTimer(10000, 10) {
            public void onTick(long millisUntilFinished) {

            }
            public void onFinish() {
                timeout = true;
            }
        };
        cTimer.start();
    }


    //cancel timer
    void cancelTimer() {
        if(cTimer!=null)
            cTimer.cancel();
    }

    public static short isCalibrating() {
        return calibrating;
    }
    public static void stopCalibration(boolean failed) {
        calibrating = 0;
        calibration_failed = failed;
    }
}