package app.com.example.ecs193.prostatecancercare;

/**
 * Created by liray on 3/6/2016.
 */
public class Appointment {
    public String date;
    public String type;
    public String note;
    public String key;

    public Appointment() {

    }

    public Appointment(String date, String type, String note, String key) {
        this.date = date;
        this.type = type;
        this.note = note;
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public String getNote() { return note; }
}
