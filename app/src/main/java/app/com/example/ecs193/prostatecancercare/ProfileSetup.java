package app.com.example.ecs193.prostatecancercare;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

public class ProfileSetup extends Activity {
    Firebase fbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);
        Firebase.setAndroidContext(this);

        fbRef = new Firebase("https://boiling-heat-3817.firebaseio.com/");

        Button createButton = (Button) findViewById(R.id.createButton);
        createButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                                        AuthData authData = fbRef.getAuth();
                                                                Firebase userRef = fbRef.child("users").child(authData.getUid());
                                        String dob = ((EditText) findViewById(R.id.dobEdit)).getText().toString();
                                        String firstname = ((EditText) findViewById(R.id.firstnameEdit)).getText().toString();
                                        String lastname = ((EditText) findViewById(R.id.lastnameEdit)).getText().toString();
                                        String ethnicity = ((EditText) findViewById(R.id.ethnicityEdit)).getText().toString();
                                        //TODO: Fix DOB, month day year all should be one string (ex. 01012016)


                                        Firebase profile = userRef.child("profile");
                                        profile.child("firstname").setValue(firstname);
                                        profile.child("lastname").setValue(lastname);
                                        profile.child("dob").setValue(dob);
                                        profile.child("ethnicity").setValue(ethnicity);

                                        finish();
                                        //Intent intent = new Intent(ProfileSetup.this, Menu.class);
                                        //startActivity(intent);




                    }
                }
        );
    }
}
