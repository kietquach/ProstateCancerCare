package app.com.example.ecs193.prostatecancercare;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class AddAppointmentActivity extends FragmentActivity implements View.OnClickListener {
    private Firebase fbRef;
    private Button mSubmitButton, mDateButton;
    private EditText mReminderEditText, mNoteEditText;
    private Spinner mSpinner;
    private String appointmentType = "PSA";
    private int year, month, day;

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            AddAppointmentActivity activity = (AddAppointmentActivity)getActivity();
            activity.year = c.get(Calendar.YEAR);
            activity.month = c.get(Calendar.MONTH);
            activity.day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, activity.year, activity.month, activity.day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            AddAppointmentActivity activity = (AddAppointmentActivity)getActivity();
            activity.year = year;
            activity.month = month;
            activity.day = day;
            System.out.println("year=" + year + " month="+ month + " day=" + day);
            activity.mDateButton.setText((month + 1) + "/" + day + "/" + year);
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onClick(View v) {
        AuthData authData = fbRef.getAuth();
        if(authData != null) {
            System.out.println(year + "/" + month + "/" + day);

            String yearStr = year + "", monthStr = month + "", dayStr = day + "";
            if(month < 9) {
                monthStr = "0" + (month + 1);
            }
            if(day < 10) {
                dayStr = "0" + day;
            }

            if (mReminderEditText.getText() == null || mReminderEditText.getText().length() == 0) {
                Toast.makeText(this, "Please enter a number for reminder", Toast.LENGTH_SHORT).show();
                return;
            }

            //add appointment to firebase.
            Firebase appointments = fbRef.child("users").child(authData.getUid()).child("Appointments").push();
            appointments.child("date").setValue(yearStr + monthStr + dayStr);
            appointments.child("type").setValue(appointmentType);
            appointments.child("note").setValue(mNoteEditText.getText().toString());

            //create an alarm to show an notification 2 weeks before appointment
            Intent alarmIntent = new Intent(this, AlarmReceiver.class);
            alarmIntent.putExtra("year", year);
            alarmIntent.putExtra("month", month);
            alarmIntent.putExtra("day", day);
            alarmIntent.putExtra("type", appointmentType);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            GregorianCalendar appointmentDate = new GregorianCalendar(year, month, day, 14, 14, 0);
            appointmentDate.add(Calendar.DAY_OF_MONTH, -1 * Integer.parseInt(mReminderEditText.getText().toString()));
            alarmManager.set(AlarmManager.RTC_WAKEUP, appointmentDate.getTimeInMillis(), pendingIntent);

            //go back to EditAppointment activity.
            finish();
        } else {
            Toast.makeText(AddAppointmentActivity.this, "Account unauthenticated", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_appointment2);
        Firebase.setAndroidContext(this);
        fbRef = new Firebase("https://boiling-heat-3817.firebaseio.com/");

        mSubmitButton = (Button)findViewById(R.id.submit_appointment_button);
        mDateButton = (Button)findViewById(R.id.aptDateButton);
        mReminderEditText = (EditText)findViewById(R.id.reminderEditText);
        mNoteEditText = (EditText)findViewById(R.id.noteEditText);
        mSpinner = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.appointment_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                appointmentType = parent.getItemAtPosition(position).toString();
            }

            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }

        });

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        mDateButton.setText((month + 1) + "/" + day + "/" + year);

        mSubmitButton.setOnClickListener(this);
    }
}
