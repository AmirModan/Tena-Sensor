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

/**
 * @author Amir Modan (amir5modan@gmail.com)
 * Activity in which 5 trials are recorded for a select exercise
 *
 * Functions include:
 *  Triggering the collection of data for 5 trials
 *  Before each trial, prompts the user 3 seconds in advance that data is going to be collected
 *  After each trial, allows the user to either advance to the next trial or return to the selection screen
 *  Once all 5 trials are complete, automatically directs the user to the exercise selection screen
 */
public class ExercisePerform extends AppCompatActivity {

    // Declare GUI Components
    private Button stopBtn, stopExerciseBtn, contExerciseBtn, homeBtn;
    private TextView status, reps;
    private ImageView newton, check, hand;
    private AnimationDrawable newtonAnimation;
    private ProgressBar mProgressBar, mProgressBar1, repBar1, repBar2;

    // Static variables which are meant to be accessed from BluetoothService.java for recording data
    private static boolean recording = false;
    private static boolean complete = false;
    private static int trial;


    //Declare timers
    private CountDownTimer cTimer, finishTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_data);

        trial = 1;

        // Assign models to views
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

        // Separate image used for Finger-to-Nose exercise
        if(ExerciseSelection.getExercise().equals("FingerToNose")) {
            hand.setImageResource(R.drawable.hand_down);
        }

        // Set Action Bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.header);
        homeBtn = (Button)findViewById(R.id.home_button);

        // Assign models to circular timers
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar_timerview);
        mProgressBar1 = (ProgressBar) findViewById(R.id.progressbar1_timerview);
        repBar1 = (ProgressBar) findViewById(R.id.progressbar1_repview);
        repBar2 = (ProgressBar) findViewById(R.id.progressbar_repview);
        repBar1.setMax(5);

        // Start timer for first trial
        startTimer();

        // When Home Button is clicked
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Goes to Home Page
                Intent intent = new Intent(ExercisePerform.this, MainActivity.class);
                ExercisePerform.this.startActivity(intent);
            }
        });

        // When Stop Recording Button is clicked during the recording of a trial
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Stop and remove Newton's Cradle animation
                newton.setVisibility(View.INVISIBLE);
                newtonAnimation.stop();

                // Remove stop button
                stopBtn.setVisibility(View.GONE);

                // When there are trials remaining
                if(trial < 5) {
                    // Display buttons for stopping or continuing exercise
                    stopExerciseBtn.setVisibility(View.VISIBLE);
                    contExerciseBtn.setVisibility(View.VISIBLE);

                    // Display number of trials completed
                    status.setText("Today's Repetitions");
                    reps.setVisibility(View.VISIBLE);
                    reps.setText(trial + " / 5");
                    repBar2.setVisibility(View.VISIBLE);
                    repBar1.setVisibility(View.VISIBLE);
                    repBar1.setProgress(trial);
                }
                // When all trials are completed
                else {
                    // Notify user
                    status.setText("You are all done with this exercise for today");

                    // Display checkmark image
                    check.setVisibility(View.VISIBLE);
                    complete = true;

                    // After 3 seconds, return to exercise selection screen
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

        // When Stop Exercise button is clicked after a trial is complete
        stopExerciseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Return to Exercise Selection screen
                Intent continueIntent = new Intent(ExercisePerform.this, MainActivity.class);
                ExercisePerform.this.startActivity(continueIntent);
            }
        });

        // When Continue Exercise button is clicked after a trail is complete
        contExerciseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Removes buttons
                stopExerciseBtn.setVisibility(View.GONE);
                contExerciseBtn.setVisibility(View.GONE);

                // Removes repetition progress view
                reps.setVisibility(View.GONE);
                repBar1.setVisibility(View.GONE);
                repBar2.setVisibility(View.GONE);

                // Starts timer for next trial
                startTimer();
            }
        });
    }

    /**
     * Destroys timer when called
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelTimer();
    }

    /**
     * Starts exercise timer when called
     */
    void startTimer() {
        // Notifies user that data is about to be recorded
        status.setText("Please keep still");
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBar1.setVisibility(View.VISIBLE);
        mProgressBar1.setMax(3000);
        hand.setVisibility(View.VISIBLE);
        //mTextField.setVisibility(View.VISIBLE);
        newton.setVisibility(View.INVISIBLE);
        cTimer = new CountDownTimer(3000, 10) {
            /**
             * Called after each countdown interval
             * @param millisUntilFinished The number of milliseconds until timer is finished
             */
            public void onTick(long millisUntilFinished) {
                // Update circular timer
                //mTextField.setText(millisUntilFinished / 1000 + ":" + (millisUntilFinished / 10) % 100);
                mProgressBar1.setProgress((int) (millisUntilFinished));
            }

            /**
             * Called when timer is finished
             */
            public void onFinish() {
                //mTextField.setVisibility(View.INVISIBLE);

                // Remove circular timer
                hand.setVisibility(View.INVISIBLE);
                mProgressBar.setVisibility(View.GONE);
                mProgressBar1.setVisibility(View.GONE);

                // Start animation and recording
                newton.setVisibility(View.VISIBLE);
                newtonAnimation.start();
                stopBtn.setVisibility(View.VISIBLE);
                status.setText("Data collection in progress");
                recording = true;
            }
        };
        cTimer.start();
    }


    /**
     * Stops timer when called
     */
    void cancelTimer() {
        if(cTimer!=null)
            cTimer.cancel();
    }

    /**
     * Method for getting status of recording
     * @return Boolean for whether data is being recorded
     */
    public static boolean isRecording() {
        return recording;
    }

    /**
     * Method for getting status of trial completion
     * @return Boolean for whether trial is complete
     */
    public static boolean isComplete() {
        return complete;
    }

    public static void setComplete(boolean isComplete) {
        complete = isComplete;
    }

    /**
     * Method for getting current trial
     * @return int for which trial is being performed
     */
    public static int getTrial() { return trial; }
}