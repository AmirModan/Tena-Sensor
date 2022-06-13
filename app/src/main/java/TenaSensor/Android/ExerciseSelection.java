package TenaSensor.Android;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * @author Amir Modan (amir5modan@gmail.com)
 * Activity in which the user selects an exercise to perform
 *
 * Functions include:
 *  Selcting one out of four exercises to perform
 *  Exercises include:
 *      Block Placing
 *      Finger-to-Nose
 *      Cup Drinking
 *      Rod Placing
 */
public class ExerciseSelection extends AppCompatActivity {

    // Declare Exercise Buttons
    private Button blkBtn, ftnBtn, cupBtn, rodBtn, homeBtn;
    private static String exercise = "Exercise";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        // Assign button models to views
        blkBtn = (Button)findViewById(R.id.block_button);
        ftnBtn = (Button)findViewById(R.id.water_button);
        cupBtn = (Button)findViewById(R.id.cup_button);
        rodBtn = (Button)findViewById(R.id.rod_button);

        // Set Action Bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.header);

        // Assign home button model to view
        homeBtn = (Button)findViewById(R.id.home_button);

        // When Home Button is clicked
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Goes to Home Page
                Intent intent = new Intent(ExerciseSelection.this, MainActivity.class);
                ExerciseSelection.this.startActivity(intent);
            }
        });

        // When Block Exercise Button is clicked
        blkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save exercise as BlockPlacing
                exercise = "BlockPlacing";

                // Navigate to Instructions for Block Placing
                Intent intent = new Intent(ExerciseSelection.this, ExerciseInstructions.class);
                intent.putExtra(BluetoothConnect.getExercise(), exercise);
                ExerciseSelection.this.startActivity(intent);
            }
        });

        // When Finger-to-Nose Exercise Button is clicked
        ftnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save exercise as FingerToNose
                exercise = "FingerToNose";

                // Navigate to Instructions for Finger-to-Nose
                Intent intent = new Intent(ExerciseSelection.this, ExerciseInstructions.class);
                intent.putExtra(BluetoothConnect.getExercise(), exercise);
                ExerciseSelection.this.startActivity(intent);
            }
        });

        // When Cup Exercise Button is clicked
        cupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exercise = "CupDrinking";

                // Navigate to Instructions for Cup Drinking
                Intent intent = new Intent(ExerciseSelection.this, ExerciseInstructions.class);
                intent.putExtra(BluetoothConnect.getExercise(), exercise);
                ExerciseSelection.this.startActivity(intent);
            }
        });

        // When Rod Exercise Button is clicked
        rodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exercise = "RodPlacing";

                // Navigate to Instructions for Rod Placing
                Intent intent = new Intent(ExerciseSelection.this, ExerciseInstructions.class);
                intent.putExtra(BluetoothConnect.getExercise(), exercise);
                ExerciseSelection.this.startActivity(intent);
            }
        });
    }

    /**
     * Gets the exercise selected for this activity
     * @return A string representing the selected exercise
     */
    public static String getExercise() {
        return exercise;
    }
}