package app.com.example.ecs193.prostatecancercare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;


public class VisualizeData extends AppCompatActivity {
//    @Override
    Firebase fbRef = new Firebase("https://boiling-heat-3817.firebaseio.com/");
    boolean noData = false;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualize_data);

        Button psaButton = (Button) findViewById(R.id.psaButton);

        psaButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AuthData authData = fbRef.getAuth();
                        Firebase psa = fbRef.child("users").child(authData.getUid()).child("psa");

                        psa.addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            //Flip this around, add the graphing stuff inside this onDataChange, because thats when we have the data to graph. Cant
                            //save here and then do it cuz async
                            public void onDataChange(DataSnapshot snapshot) {
                                for (DataSnapshot child : snapshot.getChildren()) {
                                    if (!child.exists()) {
                                        noData = true;
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {
                            }
                        });
                        if(noData){
                            Toast.makeText(VisualizeData.this, "No Data To Visualize", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Intent intent = new Intent(VisualizeData.this, PsaGraph.class);
                            startActivity(intent);
                        }

                    }
                }
        );
    }
}
