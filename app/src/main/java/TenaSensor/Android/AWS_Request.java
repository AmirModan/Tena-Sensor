package TenaSensor.Android;

import java.util.ArrayList;
import java.util.List;

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