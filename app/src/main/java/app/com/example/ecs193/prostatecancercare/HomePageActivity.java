package app.com.example.ecs193.prostatecancercare;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class HomePageActivity extends AppCompatActivity {

    private ListView sideList;
    private ArrayAdapter<String> adapter;
    private TextView welcomeText;
    private Firebase fbRef;
    private Firebase appointmentsRef;
    private Firebase childRef;
    private String user;
    private TextView daysLeftTextView;
    private Button logoutButton;
    private Intent intent;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        fbRef = new Firebase("https://boiling-heat-3817.firebaseio.com/");
        daysLeftTextView = (TextView) findViewById(R.id.daysLeftTextView);

        sideList = (ListView) findViewById(R.id.sideList);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        String[] menuList = {"Profile", "Input Data", "Visualization", "Appointments", "Settings", "QOL survey"};
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
                            case 5:
                                intent = new Intent(HomePageActivity.this, QolActivity.class);
                                startActivity(intent);
                                break;
                        }

                    }
                }
        );

        setupDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

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

            logoutButton = (Button) findViewById(R.id.viewDataButton);
            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HomePageActivity.this, LogOutActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        AuthData authData = fbRef.getAuth();
        if (authData != null) {
            appointmentsRef = childRef.child("Appointments");
            Calendar c = GregorianCalendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            String yearStr = year + "", monthStr = month + "", dayStr = day + "";
            if (month < 9) {
                monthStr = "0" + (month + 1);
            }
            if (day < 10) {
                dayStr = "0" + day;
            }
            Query queryRef = appointmentsRef.orderByChild("date").startAt(yearStr + monthStr + dayStr).limitToFirst(1);
            queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.getValue() == null) {
                        daysLeftTextView.setText("None");
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                }
            });

            queryRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (dataSnapshot == null) {

                    }
                    Appointment appointment = dataSnapshot.getValue(Appointment.class);
                    int year = Integer.parseInt(appointment.getDate().substring(0, 4));
                    int month = Integer.parseInt(appointment.getDate().substring(4, 6)) - 1;
                    int day = Integer.parseInt(appointment.getDate().substring(6, 8));
                    System.out.println("year month day " + year + " " + month + " " + day);
                    Calendar appointmentDate = new GregorianCalendar(year, month, day, 23, 0, 0);
                    Calendar now = Calendar.getInstance();
                    long difference = appointmentDate.getTimeInMillis() - now.getTimeInMillis();
                    long days = difference / (1000 * 60 * 60 * 24);
                    RelativeLayout rl = (RelativeLayout)findViewById(R.id.relativeLayout);
                    if(days <= 7 && days > 3) {
                        rl.setBackground(getResources().getDrawable(R.drawable.circleorange));
                    }
                    else if(days <= 3){
                        rl.setBackground(getResources().getDrawable(R.drawable.circlered));
                    }
                    daysLeftTextView.setText(String.valueOf(days));
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Appointment appointment = dataSnapshot.getValue(Appointment.class);
                    int year = Integer.parseInt(appointment.getDate().substring(0, 4));
                    int month = Integer.parseInt(appointment.getDate().substring(4, 6)) - 1;
                    int day = Integer.parseInt(appointment.getDate().substring(6, 8));
                    System.out.println("year month day " + year + " " + month + " " + day);
                    Calendar appointmentDate = new GregorianCalendar(year, month, day, 23, 0, 0);
                    Calendar now = Calendar.getInstance();
                    long difference = appointmentDate.getTimeInMillis() - now.getTimeInMillis();
                    long days = difference / (1000 * 60 * 60 * 24);
                    daysLeftTextView.setText(String.valueOf(days));
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
}
