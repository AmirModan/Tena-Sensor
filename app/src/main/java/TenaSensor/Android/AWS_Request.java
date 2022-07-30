package TenaSensor.Android;

import java.util.ArrayList;
import java.util.List;

public class AWS_Request {
    private List<Double> recorded_data = new ArrayList<>();
    private String exercise;
    private int trial;
    private int id;
    private int year;
    private int month;
    private int day;
    private float hour;

    public void setData(List<Double> data, String exercise, int trial, int id, int year, int month, int day, float hour) {
        this.recorded_data = data;
        this.exercise = exercise;
        this.trial = trial;
        this.id = id;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
    }

    public List<Double> getData() {
        return recorded_data;
    }

    public String getExercise() {
        return exercise;
    }

    public AWS_Request(List<Double> data, String exercise, int trial, int id, int year, int month, int day, float hour) {
        this.recorded_data = data;
        this.exercise = exercise;
        this.trial = trial;
        this.id = id;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
    }

    public AWS_Request() {
    }
}