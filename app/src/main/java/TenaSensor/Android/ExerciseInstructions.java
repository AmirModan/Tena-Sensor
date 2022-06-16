package TenaSensor.Android;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

/**
 * @author Amir Modan (amir5modan@gmail.com)
 * Activity in which instructions are given for the exercise to be performed
 *
 * Functions include:
 *  Displaying instructions for the selected exercise in text
 *  Playing a video of the exercise being performed
 */
public class ExerciseInstructions extends AppCompatActivity {

    // Declare GUI components
    private Button startBtn, homeBtn;

    // Key for accessing information from previous class, ExerciseSelection.java
    private String exercise = "Exercise";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        // Assign models to views
        VideoView videoView = (VideoView) findViewById(R.id.videoView);  //casting to VideoView is not Strictly required above API level 26
        TextView exerciseName = findViewById(R.id.ExerciseText);
        TextView exerciseInstructions = findViewById(R.id.ExerciseInstructions);

        // Get selected exercise from previous class, ExerciseSelection.java
        Bundle bundle = getIntent().getExtras();
        switch (bundle.getString(exercise)) {
            // When Block Placing was selected, display instructions for Block Placing
            case "BlockPlacing":
                exerciseName.setText("Block Placing");
                exerciseInstructions.setText(R.string.block_instructions);
                videoView.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.block_video); //set the path of the video that we need to use in our VideoView
                break;

            // When Finger-to-Nose was selected, display instructions for Finger-to-Nose
            case "FingerToNose":
                exerciseName.setText("Finger To Nose");
                exerciseInstructions.setText(R.string.water_instructions);
                videoView.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.block_video); //set the path of the video that we need to use in our VideoView
                break;

            // When Cup Drinking was selected, display instructions for Cup Drinking
            case "CupDrinking":
                exerciseName.setText("Cup Drinking");
                exerciseInstructions.setText(R.string.cup_instructions);
                videoView.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.block_video); //set the path of the video that we need to use in our VideoView
                break;

            // When Rod Placing was selected, display instructions for Rod Placing
            case "RodPlacing":
                exerciseName.setText("Rod Placing");
                exerciseInstructions.setText(R.string.rod_instructions);
                videoView.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.block_video); //set the path of the video that we need to use in our VideoView
                break;
        }

        // Start Playing Video
        videoView.start();

        // Grants user access to video controls
        MediaController mediaController = new MediaController(this);
        // Link mediaController to videoView
        mediaController.setAnchorView(videoView);
        // Allow mediaController to control our videoView
        videoView.setMediaController(mediaController);

        // Assign button models to views
        startBtn = (Button)findViewById(R.id.StartExerciseButton);

        // Set Action Bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.header);
        homeBtn = (Button)findViewById(R.id.home_button);

        // When Home Button is clicked
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Goes to Home Page
                Intent intent = new Intent(ExerciseInstructions.this, MainActivity.class);
                ExerciseInstructions.this.startActivity(intent);
            }
        });

        // When Start Button is clicked
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Goes to Exercise
                Intent intent = new Intent(ExerciseInstructions.this, ExercisePerform.class);
                ExerciseInstructions.this.startActivity(intent);
            }
        });
    }
}