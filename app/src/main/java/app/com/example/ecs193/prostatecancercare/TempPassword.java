package app.com.example.ecs193.prostatecancercare;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

public class TempPassword extends Activity {

    private EditText newPasswordEdit;
    private EditText verifyPasswordEdit;
    private Button submitNewPassword;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_password);

        newPasswordEdit = (EditText) findViewById(R.id.newPasswordEdit);
        verifyPasswordEdit = (EditText) findViewById(R.id.verifyPasswordEdit);
        submitNewPassword = (Button) findViewById(R.id.submitNewPassword);

        bundle = getIntent().getExtras();

        final Firebase fbRef = new Firebase("https://boiling-heat-3817.firebaseio.com/");
        final AuthData authData = fbRef.getAuth();

        submitNewPassword.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(newPasswordEdit.getText().toString().equals(verifyPasswordEdit.getText().toString())){
                            fbRef.changePassword(authData.getProviderData().get("email").toString(), bundle.getString("oldpassword"), newPasswordEdit.getText().toString(), new Firebase.ResultHandler() {
                                @Override
                                public void onSuccess() {
                                    Intent intent = new Intent(TempPassword.this, HomePageActivity.class);
                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void onError(FirebaseError firebaseError) {

                                }
                            });
                        }

                        else{
                            Toast.makeText(TempPassword.this, "Please make sure your passwords match", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

    }
}
