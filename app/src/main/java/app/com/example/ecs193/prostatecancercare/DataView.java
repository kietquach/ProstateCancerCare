package app.com.example.ecs193.prostatecancercare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;


public class DataView extends AppCompatActivity {

    Firebase fbRef = new Firebase("https://boiling-heat-3817.firebaseio.com/");

    public String getDateStr(int month, int day, int year){
        String str = "";
        switch (month) {
            case 0:  str = "January";
                break;
            case 1:  str = "February";
                break;
            case 2:  str = "March";
                break;
            case 3:  str = "April";
                break;
            case 4:  str = "May";
                break;
            case 5:  str = "June";
                break;
            case 6:  str = "July";
                break;
            case 7:  str = "August";
                break;
            case 8:  str = "September";
                break;
            case 9: str = "October";
                break;
            case 10: str = "November";
                break;
            case 11: str = "December";
                break;
        }
        str+=" "+day+", "+year;
        return str;
    }

    public String convertDate(String date){
        return getDateStr(Integer.parseInt(date.substring(4, 6)) - 1,
                Integer.parseInt(date.substring(6, 8)),
                Integer.parseInt(date.substring(0, 4)));
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_view);
        Firebase.setAndroidContext(this);

        AuthData authData = fbRef.getAuth();
        Firebase user = fbRef.child("users").child(authData.getUid()); //.child("psa");
        //Firebase psa = user.child("psa");
        Query q = user.child("psa");

        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                addTable("Psa", dataSnapshot);
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        q = user.child("mri");
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                addTable("Mri", dataSnapshot);
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        q = user.child("biopsy");
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                addTable("Biopsy", dataSnapshot);
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

    }

    private void addTable(String test, DataSnapshot dataSnapshot){
        final String type = test;
        TableLayout tableLayout =(TableLayout)findViewById(R.id.dateTable);
        /*
        if(type.equals("PSA")){
            tableLayout = (TableLayout)findViewById(R.id.psaTable);
        }else if(type.equals("MRI")){
            tableLayout = (TableLayout)findViewById(R.id.mriTable);
        }else{
            tableLayout = (TableLayout)findViewById(R.id.biopsyTable);
        }*/


        tableLayout.setStretchAllColumns(true);
        TableRow tableRow = new TableRow(this);
        TextView typeText = new TextView(this);
        typeText.setText(type);
        tableRow.addView(typeText);
        tableLayout.addView(tableRow);
        for (final DataSnapshot data: dataSnapshot.getChildren()) {
            tableRow = new TableRow(this);
            TextView button = new Button(this);
            button.setText(convertDate(data.getKey()));
            button.setOnClickListener(
                    new View.OnClickListener(){
                        @Override
                        public void onClick(View v){
                            Intent intent = new Intent(DataView.this, DataEditView.class);
                            intent.putExtra("type", type);
                            intent.putExtra("date", data.getKey());
                            startActivity(intent);

                        }
                    }
            );

            tableRow.setPadding(0, 10, 0, 0);
            tableRow.addView(button);
            tableLayout.addView(tableRow);
        }
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

