package app.com.example.ecs193.prostatecancercare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.client.ValueEventListener;
import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.DataSnapshot;

public class PsaGraph extends AppCompatActivity {

    Firebase fbRef = new Firebase("https://boiling-heat-3817.firebaseio.com/");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_psa_graph);
//
        AuthData authData = fbRef.getAuth();
        Firebase psa = fbRef.child("users").child(authData.getUid()).child("psa");

        psa.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            //Flip this around, add the graphing stuff inside this onDataChange, because thats when we have the data to graph. Cant
            //save here and then do it cuz async
            public void onDataChange(DataSnapshot snapshot) {
                LineChart lineChart = (LineChart) findViewById(R.id.chart);
                ArrayList<Entry> entries = new ArrayList<>();
                ArrayList<String> labels = new ArrayList<String>();
                int i = 0;
                for (DataSnapshot child : snapshot.getChildren()) {
                    if(child.exists()) {
                        labels.add(child.getKey());
                        PsaData data = child.getValue(PsaData.class);
                        entries.add(new Entry(data.getPsa(), i));
                        i++;
                    }

                }
                LineDataSet dataset = new LineDataSet(entries, "PSA Test Scores Over the Year: ng/mL");
                //arraylist of x-axis labels

                //LineData will have our labels and datasets
                LineData data = new LineData(labels, dataset);
                //curves our lines instead of points
                dataset.setDrawCubic(true);
                //fills in the area under the curve
                dataset.setDrawFilled(true);
                //set data into our chart field in xml
                lineChart.setData(data);
                lineChart.animateY(5000);
//                data = snapshot.getValue(PsaData.class);
//                System.out.println(snapshot.getValue());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
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
