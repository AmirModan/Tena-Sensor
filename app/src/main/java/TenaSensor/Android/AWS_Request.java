package TenaSensor.Android;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Amir Modan (amir5modan@gmail.com)
 * Class which contains the object that handles Requests made by the app to AWS
 *
 * Data sent to AWS include:
 *  accx (List of Doubles) - A List of Acceleration Samples collected by the sensor in the x-axis
 *  accy (List of Doubles) - A List of Acceleration Samples collected by the sensor in the y-axis
 *  accz (List of Doubles) - A List of Acceleration Samples collected by the sensor in the z-axis
 *  gyrx (List of Doubles) - A List of Gyroscope Samples collected by the sensor in the x-axis
 *  gyry (List of Doubles) - A List of Gyroscope Samples collected by the sensor in the y-axis
 *  gyrz (List of Doubles) - A List of Gyroscope Samples collected by the sensor in the z-axis
 *  exercise (String) - The Name of the exercise being performed
 *  trial (Integer) - The current trial being performed, ranging from 1-5
 *  id (Integer) - The ID of the user performing the trial
 *  unixTime (Long) - The time at which the trial was performed, represented in UNIX Time
 */
public class AWS_Request {
    private List<Double> accx = new ArrayList<>();
    private List<Double> accy = new ArrayList<>();
    private List<Double> accz = new ArrayList<>();
    private List<Double> gyrx = new ArrayList<>();
    private List<Double> gyry = new ArrayList<>();
    private List<Double> gyrz = new ArrayList<>();
    private String exercise;
    private int trial;
    private int id;
    private long unixTime;

    public void setData(List<Double> accx, List<Double> accy, List<Double> accz, List<Double> gyrx, List<Double> gyry, List<Double> gyrz, String exercise, int trial, int id, long unixTime) {
        this.accx = accx;
        this.accy = accy;
        this.accz = accz;
        this.gyrx = gyrx;
        this.gyry = gyry;
        this.gyrz = gyrz;
        this.exercise = exercise;
        this.trial = trial;
        this.id = id;
        this.unixTime = unixTime;
    }

    public List<Double> getData() {
        return accx;
    }

    public String getExercise() {
        return exercise;
    }

    public AWS_Request(List<Double> accx, List<Double> accy, List<Double> accz, List<Double> gyrx, List<Double> gyry, List<Double> gyrz, String exercise, int trial, int id, long unixTime) {
        this.accx = accx;
        this.accy = accy;
        this.accz = accz;
        this.gyrx = gyrx;
        this.gyry = gyry;
        this.gyrz = gyrz;
        this.exercise = exercise;
        this.trial = trial;
        this.id = id;
        this.unixTime = unixTime;
    }

    public AWS_Request() {
    }
}