package app.com.example.ecs193.prostatecancercare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
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
                addTable("MRI", dataSnapshot);
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
            button.setText(data.getKey());
            //button.setBackgroundResource(R.layout);
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


}

