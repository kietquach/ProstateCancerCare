package app.com.example.ecs193.prostatecancercare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class HomePageActivity extends AppCompatActivity {

    private ListView sideList;
    private ArrayAdapter<String> adapter;
    private TextView welcomeText;
    private Firebase fbRef;
    private Firebase childRef;
    private String user;
    private TextView daysLeft;
    private Button viewDataButton;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        fbRef = new Firebase("https://boiling-heat-3817.firebaseio.com/");

        sideList = (ListView) findViewById(R.id.sideList);
        String[] menuList = {"Profile", "Input Data", "Visualization", "Appointments", "Settings"};
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, menuList);
        sideList.setAdapter(adapter);

        //Might want to change to fragments later on - more efficient
        sideList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        switch (position) {
                            case 0:
                                intent = new Intent(HomePageActivity.this, ProfileSetup.class);
                                startActivity(intent);
                                break;
                            case 1:
                                intent = new Intent(HomePageActivity.this, InputData.class);
                                startActivity(intent);
                                break;
                            case 2:
                                intent = new Intent(HomePageActivity.this, VisualizeData.class);
                                startActivity(intent);
                                break;
                            case 3:
                                intent = new Intent(HomePageActivity.this, EditAppointmentsActivity.class);
                                startActivity(intent);
                                break;
                            case 4:
                                intent = new Intent(HomePageActivity.this, TempPassword.class);
                                startActivity(intent);
                                break;
                        }

                    }
                }
        );

        welcomeText = (TextView) findViewById(R.id.welcomeText);

        AuthData authData = fbRef.getAuth();
        if (authData != null) {
            // user authenticated
            user = authData.getUid();
            System.out.println(user);
            childRef = fbRef.child("users").child(user);
            childRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    // do some stuff once
                    welcomeText.setText("Hi, " + snapshot.child("profile").child("firstname").getValue().toString());
                    ////////////////////////////////////////////////////////
                    //Do days left code here - use simple date format
                    ////////////////////////////////////////////////////////
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    System.out.println("cancelled");
                }
            });

        } else {
            // no user authenticated
        }

        viewDataButton = (Button) findViewById(R.id.viewDataButton);
        viewDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this, LogOutActivity.class);
                startActivity(intent);
            }
        });

    }
}