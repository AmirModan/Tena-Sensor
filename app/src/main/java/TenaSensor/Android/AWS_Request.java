package TenaSensor.Android;

public class AWS_Request {
    String firstName;
    String lastName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public AWS_Request(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public AWS_Request() {
    }
}
