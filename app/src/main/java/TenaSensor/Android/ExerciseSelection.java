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

    private Button blkBtn, wtrBtn, cupBtn, rodBtn, homeBtn;
    private static String exercise = "Exercise";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        blkBtn = (Button)findViewById(R.id.block_button);
        wtrBtn = (Button)findViewById(R.id.water_button);
        cupBtn = (Button)findViewById(R.id.cup_button);
        rodBtn = (Button)findViewById(R.id.rod_button);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.header);

        homeBtn = (Button)findViewById(R.id.home_button);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ExerciseSelection.this, MainActivity.class);
                ExerciseSelection.this.startActivity(intent);
            }
        });

        blkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exercise = "BlockPlacing";
                Intent intent = new Intent(ExerciseSelection.this, ExerciseInstructions.class);
                intent.putExtra(BluetoothConnect.getExercise(), exercise);
                ExerciseSelection.this.startActivity(intent);
            }
        });

        wtrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exercise = "FingerToNose";
                Intent intent = new Intent(ExerciseSelection.this, ExerciseInstructions.class);
                intent.putExtra(BluetoothConnect.getExercise(), exercise);
                ExerciseSelection.this.startActivity(intent);
            }
        });

        cupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exercise = "CupDrinking";
                Intent intent = new Intent(ExerciseSelection.this, ExerciseInstructions.class);
                intent.putExtra(BluetoothConnect.getExercise(), exercise);
                ExerciseSelection.this.startActivity(intent);
            }
        });

        rodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exercise = "RodPlacing";
                Intent intent = new Intent(ExerciseSelection.this, ExerciseInstructions.class);
                intent.putExtra(BluetoothConnect.getExercise(), exercise);
                ExerciseSelection.this.startActivity(intent);
            }
        });
    }

    public static String getExercise() {
        return exercise;
    }
}