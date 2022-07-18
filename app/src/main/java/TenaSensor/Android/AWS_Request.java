package TenaSensor.Android;

import java.util.ArrayList;
import java.util.List;

public class AWS_Request {
    private List<Double> recorded_data = new ArrayList<>();
    private String exercise;
    private int trial;
    private int id;

    public void setData(List<Double> data, String exercise, int trial, int id) {
        this.recorded_data = data;
        this.exercise = exercise;
        this.trial = trial;
        this.id = id;
    }

    public List<Double> getData() {
        return recorded_data;
    }

    public String getExercise() {
        return exercise;
    }

    public AWS_Request(List<Double> data, String exercise, int trial, int id) {
        this.recorded_data = data;
        this.exercise = exercise;
        this.trial = trial;
        this.id = id;
    }

    public AWS_Request() {
    }
}
