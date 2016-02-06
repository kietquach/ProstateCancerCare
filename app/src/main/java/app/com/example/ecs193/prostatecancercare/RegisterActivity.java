package app.com.example.ecs193.prostatecancercare;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

public class RegisterActivity extends Activity {
    private EditText newEmailInput;
    private EditText newPasswordInput;
    private Button registerButton;

    public void scheduleNotification()
    {
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(),  3 * 60 * 60 * 1000, pendingIntent);
    }

    @Override
    //TODO: VALIDATE REGISTRATION, NOT JUST CREATE ACCOUNT RIGHT AWAY
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        newEmailInput = (EditText) findViewById(R.id.newEmailInput);
        newPasswordInput = (EditText) findViewById(R.id.newPasswordInput);
        registerButton = (Button) findViewById(R.id.registerButton);

        registerButton.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        if(newEmailInput.getText().toString().equals("") || newPasswordInput.getText().toString().equals("")){
                            Toast.makeText(RegisterActivity.this, "Please fill out the email and password fields", Toast.LENGTH_SHORT).show();
                            scheduleNotification();
                        }
                        else{
                            Firebase ref = new Firebase("https://crackling-heat-562.firebaseio.com");
                            ref.createUser(newEmailInput.getText().toString(), newPasswordInput.getText().toString(), new Firebase.ValueResultHandler<Map<String, Object>>() {
                                @Override
                                public void onSuccess(Map<String, Object> result) {
                                    System.out.println("Successfully created user account with uid: " + result.get("uid"));
                                }
                                @Override
                                public void onError(FirebaseError firebaseError) {
                                    Toast.makeText(RegisterActivity.this, "There was an error", Toast.LENGTH_SHORT).show();
                                }
                            });

                            finish();
                        }
                    }
                }
        );
    }
}
