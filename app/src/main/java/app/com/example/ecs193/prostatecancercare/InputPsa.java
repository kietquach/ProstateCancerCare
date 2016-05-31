package app.com.example.ecs193.prostatecancercare;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class InputPsa extends FragmentActivity {
    Firebase fbRef;
    private Calendar calendar;
    private int year, month, day;
    private Button dateButton, doneButton;
    private boolean invalidFlag;
    private boolean dateFlag;

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
            InputPsa activity = (InputPsa) getActivity();
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
        final List<String> dateEntryList = new ArrayList<>();

        //Get all current date entries for this user store in dateEntryList, to check if the date they are entering for already exists
        Query q = fbRef.child("users").child(fbRef.getAuth().getUid()).child("psa");
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dateEntry : dataSnapshot.getChildren()){
                    dateEntryList.add(dateEntry.getKey());
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        dateButton.setText(this.getDateStr(month, day, year));
        dateButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDatePickerDialog(v);
                    }
                }
        );

        doneButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AuthData authData = fbRef.getAuth();
                        invalidFlag = false;
                        dateFlag = false;
                        if (authData != null) {
                            Firebase psa = fbRef.child("users").child(authData.getUid()).child("psa");
                            final String date = String.format("%1$4d%2$02d%3$02d",  year, month + 1, day);

                            for(String dateEntry : dateEntryList){
                                if(0 == date.compareTo(dateEntry)){
                                    dateFlag = true;
                                }
                            }
                            //TODO: Add drop down values for psa, volume, and density if there is a range, or add option for range checking
                            Firebase psaEntry = psa.child(date);
                            if (((EditText) findViewById(R.id.psaEdit)).getText().toString().length() != 0) {
                                psaEntry.child("psa").setValue(((EditText) findViewById(R.id.psaEdit)).getText().toString());
                            } else {
                                invalidFlag = true;
                            }
                            if (((EditText) findViewById(R.id.densityEdit)).getText().toString().length() != 0) {
                                psaEntry.child("density").setValue(((EditText) findViewById(R.id.densityEdit)).getText().toString());
                            } else {
                                invalidFlag = true;
                            }
                            if (((EditText) findViewById(R.id.volumeEdit)).getText().toString().length() != 0) {
                                psaEntry.child("volume").setValue(((EditText) findViewById(R.id.volumeEdit)).getText().toString());
                            } else {
                                invalidFlag = true;
                            }

                            if (invalidFlag) {
                                Toast.makeText(InputPsa.this, "Please fill out all the fields.", Toast.LENGTH_SHORT).show();
                            }
                            if(dateFlag) {
                                    Toast.makeText(InputPsa.this, "This date already has an entry. Pick another date, or edit the entry.", Toast.LENGTH_LONG).show();
                            }
                            if(!invalidFlag && !dateFlag) {
                                Intent intent = new Intent(InputPsa.this, InputData.class);
                                startActivity(intent);
                            }


                        } else {
                            // no user authenticated
                        }
                    }
                }
        );
    }


}
