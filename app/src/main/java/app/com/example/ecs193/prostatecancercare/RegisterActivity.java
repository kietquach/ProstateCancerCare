package app.com.example.ecs193.prostatecancercare;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;
import java.util.Random;

public class RegisterActivity extends Activity {
    private EditText newEmailInput;
    private EditText newPasswordInput;
    private Button registerButton;
    private Button resetButton;
    protected static String validCharacters;

    @Override

    //TODO: VALIDATE REGISTRATION, NOT JUST CREATE ACCOUNT RIGHT AWAY
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        validCharacters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_-0123456789";
        newEmailInput = (EditText) findViewById(R.id.newEmailInput);
        registerButton = (Button) findViewById(R.id.registerButton);
        resetButton = (Button) findViewById(R.id.resetButton);

        resetButton.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        if(newEmailInput.getText().toString().equals("")){
                            Toast.makeText(RegisterActivity.this, "Please fill in your email address", Toast.LENGTH_SHORT).show();
                        }

                        else{
                            final Firebase fbRef = new Firebase("https://boiling-heat-3817.firebaseio.com/");
                            fbRef.resetPassword(newEmailInput.getText().toString(), new Firebase.ResultHandler() {
                                @Override
                                public void onSuccess() {
                                    // password reset email sent
                                }
                                @Override
                                public void onError(FirebaseError firebaseError) {
                                    // error encountered
                                }
                            });
                        }
                    }
                }
        );

        registerButton.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        if(newEmailInput.getText().toString().equals("")){
                            Toast.makeText(RegisterActivity.this, "Please fill in your email address", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            String password = createPassword();
                            final Firebase fbRef = new Firebase("https://boiling-heat-3817.firebaseio.com/");
                            fbRef.createUser(newEmailInput.getText().toString(), password, new Firebase.ValueResultHandler<Map<String, Object>>() {
                                @Override
                                public void onSuccess(Map<String, Object> result) {
                                    System.out.println("Successfully created user account with uid: " + result.get("uid"));
                                    fbRef.resetPassword(newEmailInput.getText().toString(), new Firebase.ResultHandler() {
                                        @Override
                                        public void onSuccess() {
                                            System.out.println("Successfully reset password for " + newEmailInput);
                                        }

                                        @Override
                                        public void onError(FirebaseError firebaseError) {
                                            System.out.println("Error");
                                        }
                                    });
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

    private static String createPassword(){
        StringBuilder newPassword = new StringBuilder();
        Random r = new Random();

        for(int i = 0; i < 16; i ++){
            newPassword.append(validCharacters.charAt(r.nextInt(validCharacters.length())));
        }
        return newPassword.toString();
    }
}
