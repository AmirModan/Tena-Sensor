package TenaSensor.Android;

import java.util.ArrayList;
import java.util.List;

public class AWS_Request {
    private List<Double> recorded_data = new ArrayList<>();

    public void setData(List<Double> data) { this.recorded_data = data; }

    public List<Double> getData() { return recorded_data; }

    public AWS_Request(List<Double> data) {
        this.recorded_data = data;
    }

    public AWS_Request() {
    }
}
