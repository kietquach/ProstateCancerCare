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
                        //TODO: make sure username hasn't been taken yet, must be unique

                        final String email = ((EditText) findViewById(R.id.emailEdit)).getText().toString();
                        final String password = ((EditText) findViewById(R.id.passwordEdit)).getText().toString();
                        fbRef.createUser(email, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
                            @Override
                            public void onSuccess(Map<String, Object> result) {
                                System.out.println("Successfully created user account with uid: " + result.get("uid"));


                                Firebase.AuthResultHandler authResultHandler = new Firebase.AuthResultHandler() {
                                    @Override
                                    public void onAuthenticated(AuthData authData) {
                                        // Authenticated successfully with payload authData
                                        System.out.println("Successfully logged in user account with uid: " + authData.getUid());

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

                                        Intent intent = new Intent(ProfileSetup.this, Menu.class);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onAuthenticationError(FirebaseError firebaseError) {
                                        // Authenticated failed with error firebaseError
                                    }
                                };
                                fbRef.authWithPassword(email, password, authResultHandler);

                            }

                            @Override
                            public void onError(FirebaseError firebaseError) {
                                // there was an error
                            }
                        });
                    }
                }
        );
    }
}
