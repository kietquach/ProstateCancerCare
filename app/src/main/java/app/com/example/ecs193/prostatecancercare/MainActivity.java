package app.com.example.ecs193.prostatecancercare;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import static android.view.View.*;

/**
 * Created by Kiet Quach on 1/26/2016.
 */
public class MainActivity extends Activity{

    private Button loginButton;
    private Button signUpButton;
    private EditText emailEdit;
    private EditText passwordEdit;

    public MainActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailEdit = (EditText) findViewById(R.id.emailEdit);
        passwordEdit = (EditText) findViewById(R.id.passwordEdit);

        Firebase.setAndroidContext(this);

        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        //Go into next activity depending on Patient account or Physician Account
                        //Intent intent = new Intent(MainActivity.this, ClientFirstTime.class);
                        //startActivity(intent);
                        final Firebase fbRef = new Firebase("https://boiling-heat-3817.firebaseio.com/");

                        //BUG: NOT WORKING, ALWAYS BRINGS UP PROFILE
                        /*SharedPreferences prefs = MainActivity.this.getSharedPreferences("FirstTime", 0);
                        SharedPreferences.Editor editor = prefs.edit();
                        Intent intent;
                        if (prefs.getBoolean("isInitialLogin", false))
                        {
                            intent = new Intent(MainActivity.this, HomePageActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            //Log in for the first time ever
                            editor.putBoolean("isInitialLogin", false);
                            editor.commit();
                            intent = new Intent(MainActivity.this, ProfileSetup.class);
                            startActivity(intent);
                            finish();
                        }*/

                        /*Intent intent = new Intent(MainActivity.this, HomePageActivity.class);
                        startActivity(intent);
                        finish();*/

                        //NOTE: Put uncomment below fo email authentication
                        fbRef.authWithPassword(emailEdit.getText().toString(), passwordEdit.getText().toString(), new Firebase.AuthResultHandler() {
                            @Override
                            public void onAuthenticated(AuthData authData) {
                                //System.out.println("User ID: " + authData.getUid() + ", Provider: " + authData.getProvider());
                                //Intent intent = new Intent(MainActivity.this, ClientFirstTime.class); //Change back
                                Intent intent = new Intent(MainActivity.this, HomePageActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            @Override
                            public void onAuthenticationError(FirebaseError firebaseError) {
                                // there was an error
                                Toast.makeText(MainActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }
        );

        signUpButton = (Button) findViewById(R.id.signupButton);
        signUpButton.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                        startActivity(intent);
                    }
                }
        );
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
