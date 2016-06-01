package app.com.example.ecs193.prostatecancercare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;


public class RemoveData extends AppCompatActivity {
    Firebase fbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_data);
        Firebase.setAndroidContext(this);

        fbRef = new Firebase("https://boiling-heat-3817.firebaseio.com/");

        Button doneButton = (Button)findViewById(R.id.doneButton);
        doneButton.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        AuthData authData = fbRef.getAuth();
                        if (authData != null) {
                            // user authenticated
                            Firebase user = fbRef.child("users").child(authData.getUid()); //.child("psa");

                            String date = ((EditText) findViewById(R.id.dateEdit)).getText().toString();
                            String type = ((EditText) findViewById(R.id.typeEdit)).getText().toString();

                                Firebase entry = user.child(type).child(date);
                                entry.removeValue();
                            
                            Intent intent = new Intent(RemoveData.this, InputData.class);
                            startActivity(intent);
                        } else {
                            // no user authenticated
                        }

                    }
                }
        );
    }
}
