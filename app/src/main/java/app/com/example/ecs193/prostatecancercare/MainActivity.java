package app.com.example.ecs193.prostatecancercare;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
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
    private Button newUserButton;
    private EditText loginEmail;
    private EditText loginPassword;

    public MainActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginEmail = (EditText) findViewById(R.id.loginEmail);
        loginPassword = (EditText) findViewById(R.id.loginPassword);

        Firebase.setAndroidContext(this);

        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        //Go into next activity depending on Patient account or Physician Account
                        //Intent intent = new Intent(MainActivity.this, ClientFirstTime.class);
                        //startActivity(intent);
                        Firebase ref = new Firebase("https://crackling-heat-562.firebaseio.com");
                        Intent intent = new Intent(MainActivity.this, HomePageActivity.class); //Used to debugging
                        startActivity(intent);
                        /*ref.authWithPassword(loginEmail.getText().toString(), loginPassword.getText().toString(), new Firebase.AuthResultHandler() {
                            @Override
                            public void onAuthenticated(AuthData authData) {
                                //System.out.println("User ID: " + authData.getUid() + ", Provider: " + authData.getProvider());
                                //Intent intent = new Intent(MainActivity.this, ClientFirstTime.class); //Change back
                                Intent intent = new Intent(MainActivity.this, HomePageActivity.class);
                                startActivity(intent);
                            }
                            @Override
                            public void onAuthenticationError(FirebaseError firebaseError) {
                                // there was an error
                                Toast.makeText(MainActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                            }
                        });*/
                    }

                }
        );

        newUserButton = (Button) findViewById(R.id.newUserButton);
        newUserButton.setOnClickListener(
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
