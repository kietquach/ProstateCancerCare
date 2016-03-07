package app.com.example.ecs193.prostatecancercare;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by liray on 3/5/2016.
 */
public class AddAppointmentActivity extends Activity implements View.OnClickListener {
    private Firebase fbRef;
    private Button mSubmitButton;
    private DatePicker mDatePicker;
    private String appointmentType;

    @Override
    public void onClick(View v) {
        AuthData authData = fbRef.getAuth();
        if(authData != null) {
            int year = mDatePicker.getYear();
            int month = mDatePicker.getMonth(); //months start at 0 instead of 1.
            int day = mDatePicker.getDayOfMonth();
            System.out.println(year + "/" + month + "/" + day);

            String yearStr = year + "", monthStr = month + "", dayStr = day + "";
            if(month < 9) {
                monthStr = "0" + (month + 1);
            }
            if(day < 10) {
                dayStr = "0" + day;
            }

            //add appointment to firebase.
            Firebase appointments = fbRef.child("users").child(authData.getUid()).child("Appointments").push();
            appointments.child("date").setValue(yearStr + monthStr + dayStr);
            appointments.child("type").setValue(appointmentType);
            Intent intent = new Intent(AddAppointmentActivity.this, EditAppointmentsActivity.class);

            //create an alarm to show an notification 2 weeks before appointment
            Intent alarmIntent = new Intent(this, AlarmReceiver.class);
            alarmIntent.putExtra("year", year);
            alarmIntent.putExtra("month", month);
            alarmIntent.putExtra("day", day);
            alarmIntent.putExtra("type", appointmentType);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            GregorianCalendar appointmentDate = new GregorianCalendar(year, month, day, 14, 14, 0);
            appointmentDate.add(Calendar.DAY_OF_MONTH, -14);
            alarmManager.set(AlarmManager.RTC_WAKEUP, appointmentDate.getTimeInMillis(), pendingIntent);

            //go back to EditAppointment activity.
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(AddAppointmentActivity.this, "Account unauthenticated", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_appointment);
        Firebase.setAndroidContext(this);
        fbRef = new Firebase("https://boiling-heat-3817.firebaseio.com/");

        mSubmitButton = (Button)findViewById(R.id.submit_appointment_button);
        mDatePicker = (DatePicker)findViewById(R.id.datePicker);

        mSubmitButton.setOnClickListener(this);
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        System.out.println("RADIO BUTTON CLICKED");
        switch(view.getId()) {
            case R.id.PSARadioButton:
                if(checked)
                    appointmentType = "PSA";
                break;
            case R.id.QQLRadioButton:
                if(checked)
                    appointmentType = "QQL";
                break;
            case R.id.BIOPSYRadioButton:
                if(checked)
                    appointmentType = "Biopsy";
                break;
            case R.id.GENERadioButton:
                if(checked)
                    appointmentType = "Gene";
                break;
            case R.id.MRIRadioButton:
                if(checked)
                    appointmentType = "MRI";
                break;
            case R.id.GENOMICSRadioButton:
                if(checked)
                    appointmentType = "Genmoics";
                break;
        }
    }
}
