package TenaSensor.Android;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.CountDownTimer;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ExercisePerform extends AppCompatActivity {

    private static int trial;

    private Button stopBtn, stopExerciseBtn, contExerciseBtn, homeBtn;
    private TextView status, reps;
    private ImageView newton, check, hand;
    private AnimationDrawable newtonAnimation;
    private static boolean recording = false;
    private static boolean complete = false;

    private ProgressBar mProgressBar, mProgressBar1, repBar1, repBar2;

    //Declare timer
    CountDownTimer cTimer = null;
    CountDownTimer finishTimer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_data);

        trial = 1;

        //mTextField = (TextView)findViewById(R.id.countdown);
        status = (TextView)findViewById(R.id.countdown_status);
        reps = (TextView)findViewById(R.id.repetitions);
        stopBtn = (Button)findViewById(R.id.stop);
        stopExerciseBtn = (Button)findViewById(R.id.stopExercise);
        contExerciseBtn = (Button)findViewById(R.id.continueExercise);
        newton = (ImageView) findViewById(R.id.newton);
        newton.setBackgroundResource(R.drawable.newton_list);
        newtonAnimation = (AnimationDrawable) newton.getBackground();
        check = (ImageView) findViewById(R.id.check);
        hand = (ImageView) findViewById(R.id.hand_position);
        if(ExerciseSelection.getExercise().equals("FingerToNose")) {
            hand.setImageResource(R.drawable.hand_down);
        }
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.header);


        mProgressBar = (ProgressBar) findViewById(R.id.progressbar_timerview);
        mProgressBar1 = (ProgressBar) findViewById(R.id.progressbar1_timerview);
        repBar1 = (ProgressBar) findViewById(R.id.progressbar1_repview);
        repBar2 = (ProgressBar) findViewById(R.id.progressbar_repview);
        repBar1.setMax(5);

        startTimer();

        homeBtn = (Button)findViewById(R.id.home_button);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExercisePerform.this, MainActivity.class);
                ExercisePerform.this.startActivity(intent);
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newton.setVisibility(View.INVISIBLE);
                newtonAnimation.stop();
                stopBtn.setVisibility(View.GONE);
                if(trial < 5) {
                    stopExerciseBtn.setVisibility(View.VISIBLE);
                    contExerciseBtn.setVisibility(View.VISIBLE);
                    status.setText("Today's Repetitions");
                    reps.setVisibility(View.VISIBLE);
                    reps.setText(trial + " / 5");
                    repBar2.setVisibility(View.VISIBLE);
                    repBar1.setVisibility(View.VISIBLE);
                    repBar1.setProgress(trial);
                } else {
                    status.setText("You are all done with this exercise for today");
                    check.setVisibility(View.VISIBLE);
                    complete = true;
                    finishTimer = new CountDownTimer(3000, 10) {
                        public void onTick(long millisUntilFinished) {}
                        public void onFinish() {
                            Intent intent = new Intent(ExercisePerform.this, ExerciseSelection.class);
                            ExercisePerform.this.startActivity(intent);
                        }
                    };
                    finishTimer.start();
                }
                trial++;
                recording = false;
            }
        });

        stopExerciseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent continueIntent = new Intent(ExercisePerform.this, MainActivity.class);
                ExercisePerform.this.startActivity(continueIntent);
            }
        });

        contExerciseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopExerciseBtn.setVisibility(View.GONE);
                contExerciseBtn.setVisibility(View.GONE);
                reps.setVisibility(View.GONE);
                repBar1.setVisibility(View.GONE);
                repBar2.setVisibility(View.GONE);
                startTimer();
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
        status.setText("Please keep still");
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBar1.setVisibility(View.VISIBLE);
        mProgressBar1.setMax(3000);
        hand.setVisibility(View.VISIBLE);
        //mTextField.setVisibility(View.VISIBLE);
        newton.setVisibility(View.INVISIBLE);
        cTimer = new CountDownTimer(3000, 10) {
            public void onTick(long millisUntilFinished) {
                //mTextField.setText(millisUntilFinished / 1000 + ":" + (millisUntilFinished / 10) % 100);
                mProgressBar1.setProgress((int) (millisUntilFinished));
            }
            public void onFinish() {
                //mTextField.setVisibility(View.INVISIBLE);
                hand.setVisibility(View.INVISIBLE);
                newton.setVisibility(View.VISIBLE);
                newtonAnimation.start();
                mProgressBar.setVisibility(View.GONE);
                mProgressBar1.setVisibility(View.GONE);
                stopBtn.setVisibility(View.VISIBLE);
                status.setText("Data collection in progress");
                recording = true;
            }
        };
        cTimer.start();
    }


    //cancel timer
    void cancelTimer() {
        if(cTimer!=null)
            cTimer.cancel();
    }

    public static boolean isRecording() {
        return recording;
    }
    public static boolean isComplete() {
        return complete;
    }
    public static int getTrial() { return trial; }
}