package app.com.example.ecs193.prostatecancercare;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Calendar;
import java.util.Map;



public class ProfileSetup extends AppCompatActivity {
    Firebase fbRef;
    private Calendar calendar;
    private int year, month, day;
    private Button dateButton, doneButton;
    private boolean invalidFlag;
    private boolean dateFlag;
    private boolean fieldsFlag;
    private Firebase biopsy;

    public String getDateStr(int month, int day, int year){
        String str = "";
        switch (month) {
            case 0:  str = "January";
                break;
            case 1:  str = "February";
                break;
            case 2:  str = "March";
                break;
            case 3:  str = "April";
                break;
            case 4:  str = "May";
                break;
            case 5:  str = "June";
                break;
            case 6:  str = "July";
                break;
            case 7:  str = "August";
                break;
            case 8:  str = "September";
                break;
            case 9: str = "October";
                break;
            case 10: str = "November";
                break;
            case 11: str = "December";
                break;
        }
        str+=" "+day+", "+year;
        return str;
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            ProfileSetup activity = (ProfileSetup) getActivity();
            activity.year = year;
            activity.month = month;
            activity.day = day;
            activity.dateButton.setText(activity.getDateStr(month, day, year));
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public String convertDate(String date){
        return getDateStr(Integer.parseInt(date.substring(4, 6)) - 1,
                Integer.parseInt(date.substring(6, 8)),
                Integer.parseInt(date.substring(0, 4)));
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);
        Firebase.setAndroidContext(this);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        dateButton = (Button) findViewById(R.id.dateButton);
        //Set Today's Date on Button
        dateButton.setText(this.getDateStr(month, day, year));
        dateButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDatePickerDialog(v);
                    }
                }
        );

        fbRef = new Firebase("https://boiling-heat-3817.firebaseio.com/");
        AuthData authData = fbRef.getAuth();
        final Firebase profile = fbRef.child("users").child(authData.getUid()).child("profile");
        profile.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ((EditText) findViewById(R.id.firstnameEdit)).setText(dataSnapshot.child("firstname").getValue().toString());
                    ((EditText) findViewById(R.id.lastnameEdit)).setText(dataSnapshot.child("lastname").getValue().toString());
                    ((EditText) findViewById(R.id.ethnicityEdit)).setText(dataSnapshot.child("ethnicity").getValue().toString());
                    if(!dataSnapshot.child("dob").getValue().toString().isEmpty()) {
                        ((Button) findViewById(R.id.dateButton)).setText(convertDate(dataSnapshot.child("dob").getValue().toString()));
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });



        Button createButton = (Button) findViewById(R.id.createButton);
        createButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                                        String dob = String.format("%1$4d%2$02d%3$02d", year, month + 1, day);
                                        String firstname = ((EditText) findViewById(R.id.firstnameEdit)).getText().toString();
                                        String lastname = ((EditText) findViewById(R.id.lastnameEdit)).getText().toString();
                                        String ethnicity = ((EditText) findViewById(R.id.ethnicityEdit)).getText().toString();
                                        profile.child("firstname").setValue(firstname);
                                        profile.child("lastname").setValue(lastname);
                                        profile.child("dob").setValue(dob);
                                        profile.child("ethnicity").setValue(ethnicity);

                                        finish();
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.homeMenuButton:
                Intent intent = new Intent(this, HomePageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
