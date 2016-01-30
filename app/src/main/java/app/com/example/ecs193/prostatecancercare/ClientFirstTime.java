package app.com.example.ecs193.prostatecancercare;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class ClientFirstTime extends Activity {

    //Right now its manual text view and text fields but might want to change to list view

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_first_time);
    }

    //the onclick function for the submit button in activity_client_first_time.xml.
    public void onSubmit(View v) {
        switch(v.getId()) {
            case R.id.Button_submit:
                EditText ageEditView = (EditText) findViewById(R.id.EditText_age);
                if(ageEditView.getText() == null) {
                    return;
                }

                Double age = Double.parseDouble(ageEditView.getText().toString());
                if(age > 20) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Abnormal PSA value. Please go see a doctor.");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
        }
    }
}
