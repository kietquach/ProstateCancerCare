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
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;


import java.util.Map;

import static android.view.View.*;

/**
 * Created by Kiet Quach on 1/26/2016.
 */
public class MainActivity extends Activity{

    private Button loginButton;
    private Button signUpButton;
    private EditText emailEdit;
    private EditText passwordEdit;
    private SharedPreferences mSharedPreferences;
    public MainActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Firebase.setAndroidContext(this);
        final Firebase fbRef = new Firebase("https://boiling-heat-3817.firebaseio.com/");

        //try logging in with saved email and password.
        mSharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        String email = mSharedPreferences.getString("email", "");
        String password = mSharedPreferences.getString("password", "");
        fbRef.authWithPassword(email, password, new Firebase.AuthResultHandler() {
            public void onAuthenticated(AuthData authData) {
                Intent intent = new Intent(MainActivity.this, HomePageActivity.class);
                startActivity(intent);
                finish();
            }

            public void onAuthenticationError(FirebaseError firebaseError) {
                setContentView(R.layout.activity_main);

                emailEdit = (EditText) findViewById(R.id.emailEdit);
                passwordEdit = (EditText) findViewById(R.id.passwordEdit);

                loginButton = (Button) findViewById(R.id.loginButton);
                loginButton.setOnClickListener(
                        new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                //NOTE: Put uncomment below fo email authentication
                                fbRef.authWithPassword(emailEdit.getText().toString(), passwordEdit.getText().toString(), new Firebase.AuthResultHandler() {
                                    public void onAuthenticated(AuthData authData) {
                                        if((boolean)authData.getProviderData().get("isTemporaryPassword")){
                                            Intent intent = new Intent(MainActivity.this, TempPassword.class);
                                            intent.putExtra("oldpassword", passwordEdit.getText().toString());
                                            startActivity(intent);
                                            finish();
                                        } else{
                                            SharedPreferences.Editor editor = mSharedPreferences.edit();
                                            editor.putString("email", emailEdit.getText().toString());
                                            editor.putString("password", passwordEdit.getText().toString());
                                            editor.commit();
                                            Intent intent = new Intent(MainActivity.this, HomePageActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }

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
        });
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
