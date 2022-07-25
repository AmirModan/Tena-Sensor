package TenaSensor.Android;

import java.util.List;

public class AWS_Request {
    private List<List<Double>> recorded_data;
    private String exercise;
    private int trial;
    private int id;

    public void setData(List<List<Double>> data, String exercise, int trial, int id) {
        this.recorded_data = data;
        this.exercise = exercise;
        this.trial = trial;
        this.id = id;
    }

    public List<List<Double>> getData() {
        return recorded_data;
    }

    public String getExercise() {
        return exercise;
    }

    public AWS_Request(List<List<Double>> data, String exercise, int trial, int id) {
        this.recorded_data = data;
        this.exercise = exercise;
        this.trial = trial;
        this.id = id;
    }

    public AWS_Request() {
    }
}
