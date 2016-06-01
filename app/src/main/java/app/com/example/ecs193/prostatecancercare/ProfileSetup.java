package app.com.example.ecs193.prostatecancercare;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Map;

public class ProfileSetup extends AppCompatActivity {
    Firebase fbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);
        Firebase.setAndroidContext(this);

        fbRef = new Firebase("https://boiling-heat-3817.firebaseio.com/");
        AuthData authData = fbRef.getAuth();
        final Firebase profile = fbRef.child("users").child(authData.getUid()).child("profile");
        profile.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ((EditText) findViewById(R.id.firstnameEdit)).setText(dataSnapshot.child("firstname").getValue().toString());
                    ((EditText) findViewById(R.id.lastnameEdit)).setText(dataSnapshot.child("lastname").getValue().toString());
                    ((EditText) findViewById(R.id.dobEdit)).setText(dataSnapshot.child("dob").getValue().toString());
                    ((EditText) findViewById(R.id.ethnicityEdit)).setText(dataSnapshot.child("ethnicity").getValue().toString());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });



        Button createButton = (Button) findViewById(R.id.createButton);
        createButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                                        String dob = ((EditText) findViewById(R.id.dobEdit)).getText().toString();
                                        String firstname = ((EditText) findViewById(R.id.firstnameEdit)).getText().toString();
                                        String lastname = ((EditText) findViewById(R.id.lastnameEdit)).getText().toString();
                                        String ethnicity = ((EditText) findViewById(R.id.ethnicityEdit)).getText().toString();
                                        //TODO: Fix DOB, month day year all should be one string (ex. 01012016)

                                        profile.child("firstname").setValue(firstname);
                                        profile.child("lastname").setValue(lastname);
                                        profile.child("dob").setValue(dob);
                                        profile.child("ethnicity").setValue(ethnicity);

                                        finish();
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
