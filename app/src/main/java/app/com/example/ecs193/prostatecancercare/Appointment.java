package app.com.example.ecs193.prostatecancercare;

/**
 * Created by liray on 3/6/2016.
 */
public class Appointment {
    public String date;
    public String type;

    public Appointment() {

    }

    public Appointment(String date, String type) {
        this.date = date;
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public String getType() {
        return type;
    }
}
