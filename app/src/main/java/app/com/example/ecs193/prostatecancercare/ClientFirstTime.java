package app.com.example.ecs193.prostatecancercare;

import android.app.Activity;
import android.os.Bundle;

public class ClientFirstTime extends Activity {

    //Right now its manual text view and text fields but might want to change to list view

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_first_time);
    }
}
