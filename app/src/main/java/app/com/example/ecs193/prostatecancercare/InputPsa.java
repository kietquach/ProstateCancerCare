package app.com.example.ecs193.prostatecancercare;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
public class InputPsa extends AppCompatActivity {
    Firebase fbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_psa);
        Firebase.setAndroidContext(this);

        fbRef = new Firebase("https://boiling-heat-3817.firebaseio.com/");

        Button doneButton = (Button) findViewById(R.id.doneButton);
        doneButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AuthData authData = fbRef.getAuth();
                        if (authData != null) {
                            // user authenticated
                            Firebase psa = fbRef.child("users").child(authData.getUid()).child("psa");

                            String date = ((EditText) findViewById(R.id.dateEdit)).getText().toString();
                            //does not  save empty data
                            if(!date.isEmpty()) {
                                Firebase psaEntry = psa.child(date);
                                //does not save empty data
                                if (((EditText) findViewById(R.id.psaEdit)).getText().toString() != "") {
                                    psaEntry.child("psa").setValue(((EditText) findViewById(R.id.psaEdit)).getText().toString());
                                }
                                if (((EditText) findViewById(R.id.densityEdit)).getText().toString() != "") {
                                    psaEntry.child("density").setValue(((EditText) findViewById(R.id.densityEdit)).getText().toString());
                                }
                                if (((EditText) findViewById(R.id.prostatevolumeEdit)).getText().toString() != "") {
                                    psaEntry.child("prostatevolume").setValue(((EditText) findViewById(R.id.prostatevolumeEdit)).getText().toString());
                                }

                            }
                            Intent intent = new Intent(InputPsa.this, InputData.class);
                            startActivity(intent);
                        } else {
                            // no user authenticated
                        }
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.homeMenuButton:
                Intent intent = new Intent(this, HomePageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
