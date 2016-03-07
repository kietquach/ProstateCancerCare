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

public class InputBiopsy extends Activity {
    Firebase fbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_biopsy);
        Firebase.setAndroidContext(this);

        fbRef = new Firebase("https://boiling-heat-3817.firebaseio.com/");
/*
        Button doneButton = (Button) findViewById(R.id.doneButton);
        doneButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AuthData authData = fbRef.getAuth();
                        if (authData != null) {
                            // user authenticated
                            Firebase biopsy = fbRef.child("users").child(authData.getUid()).child("biopsy");
                            String date = ((EditText) findViewById(R.id.dateEdit)).getText().toString();
                            Firebase biopsyEntry = biopsy.child(date);
                           // biopsyEntry.child("corestaken").setValue(((EditText) findViewById(R.id.corestakenEdit)).getText().toString());
                            //biopsyEntry.child("positivecorescount").setValue(((EditText) findViewById(R.id.positivecorescountEdit)).getText().toString());

                            //biopsyEntry.child("positivecore").setValue(((EditText) findViewById(R.id.postivecoreEdit)).getText().toString());
                            //biopsyEntry.child("cancer").setValue(((EditText) findViewById(R.id.cancerEdit)).getText().toString());
                            //biopsyEntry.child("gleason").setValue(((EditText) findViewById(R.id.gleasonEdit)).getText().toString());

                            Intent intent = new Intent(InputBiopsy.this, InputData.class);
                            startActivity(intent);
                        } else {
                            // no user authenticated
                        }
                    }
                }
        );*/
    }
}

