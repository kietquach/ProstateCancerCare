package app.com.example.ecs193.prostatecancercare;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

import java.util.Calendar;

public class InputPsa extends Activity {
    Firebase fbRef;

    private DatePicker datePicker;
    private Calendar calendar;
    private TextView dateView;
    private int year, month, day;
    private Button dateButton;
    private Button doneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_psa);
        Firebase.setAndroidContext(this);

        fbRef = new Firebase("https://boiling-heat-3817.firebaseio.com/");


        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        dateButton = (Button) findViewById(R.id.dateButton);
        doneButton = (Button) findViewById(R.id.doneButton);
        dateButton.setText(Integer.toString(month+1)+"/"+Integer.toString(day)+"/"+Integer.toString(year));
        dateButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //dateButton.setText();
                    }
                }
        );

        doneButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AuthData authData = fbRef.getAuth();
                        if (authData != null) {
                            // user authenticated
                            Firebase psa = fbRef.child("users").child(authData.getUid()).child("psa");

                            String date = ((EditText) findViewById(R.id.dateEdit)).getText().toString();
                            //does not  save empty data
                            if(!date.isEmpty()) {
                                Firebase psaEntry = psa.child(date);
                                //does not save empty data
                                if (((EditText) findViewById(R.id.psaEdit)).getText().toString() != "") {
                                    psaEntry.child("psa").setValue(((EditText) findViewById(R.id.psaEdit)).getText().toString());
                                }
                                if (((EditText) findViewById(R.id.densityEdit)).getText().toString() != "") {
                                    psaEntry.child("density").setValue(((EditText) findViewById(R.id.densityEdit)).getText().toString());
                                }
                                if (((EditText) findViewById(R.id.volumeEdit)).getText().toString() != "") {
                                    psaEntry.child("prostatevolume").setValue(((EditText) findViewById(R.id.volumeEdit)).getText().toString());
                                }

                            }
                            Intent intent = new Intent(InputPsa.this, InputData.class);
                            startActivity(intent);
                        } else {
                            // no user authenticated
                        }
                    }
                }
        );
    }


}
