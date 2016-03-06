package app.com.example.ecs193.prostatecancercare;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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
                //read in data later, using fake data for now
                LineChart lineChart = (LineChart) findViewById(R.id.chart);
                //fake PSA SCORES in ng/mL
                //data entries must have floats
                ArrayList<Entry> entries = new ArrayList<>();
//                entries.add(new Entry(2.0f, 0));
//                entries.add(new Entry(2.9f, 1));
//                entries.add(new Entry(3.8f, 2));
//                entries.add(new Entry(8.0f, 3));
//                entries.add(new Entry(10.0f, 4));
//                entries.add(new Entry(1.4f, 5));
//                entries.add(new Entry(2.9f, 6));
//                entries.add(new Entry(2.5f, 7));
//                entries.add(new Entry(2.3f, 8));
//                entries.add(new Entry(6.7f, 9));
//                entries.add(new Entry(8.4f, 10));
//                entries.add(new Entry(4.2f, 11));
                //creating dataset from the data entries

//                System.out.println(snapshot.getValue());
//                String cac = snapshot.getValue().toString();
//                System.out.println(cac);
                ArrayList<String> labels = new ArrayList<String>();
//                labels.add("January");
//                labels.add("February");
//                labels.add("March");
//                labels.add("April");
//                labels.add("May");
//                labels.add("June");
//                labels.add("July");
//                labels.add("August");
//                labels.add("September");
//                labels.add("October");
//                labels.add("November");
//                labels.add("December");
                int i = 0;
                for (DataSnapshot child : snapshot.getChildren()) {
//                  System.out.print(child.getKey());
                    labels.add(child.getKey());
                    PsaData data = child.getValue(PsaData.class);
                    entries.add(new Entry(data.getPsa(), i));
                    i++;

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


//        //read in data later, using fake data for now
//        LineChart lineChart = (LineChart) findViewById(R.id.chart);
//        //fake PSA SCORES in ng/mL
//        //data entries must have floats
//        ArrayList<Entry> entries = new ArrayList<>();
//        entries.add(new Entry(2.0f, 0));
//        entries.add(new Entry(2.9f, 1));
//        entries.add(new Entry(3.8f, 2));
//        entries.add(new Entry(8.0f, 3));
//        entries.add(new Entry(10.0f, 4));
//        entries.add(new Entry(1.4f, 5));
//        entries.add(new Entry(2.9f, 6));
//        entries.add(new Entry(2.5f, 7));
//        entries.add(new Entry(2.3f, 8));
//        entries.add(new Entry(6.7f, 9));
//        entries.add(new Entry(8.4f, 10));
//        entries.add(new Entry(4.2f, 11));
//        //creating dataset from the data entries
//        LineDataSet dataset = new LineDataSet(entries, "PSA Test Scores Over the Year: ng/mL");
//        //arraylist of x-axis labels
//        ArrayList<String> labels = new ArrayList<String>();
//        labels.add("January");
//        labels.add("February");
//        labels.add("March");
//        labels.add("April");
//        labels.add("May");
//        labels.add("June");
//        labels.add("July");
//        labels.add("August");
//        labels.add("September");
//        labels.add("October");
//        labels.add("November");
//        labels.add("December");
//        //LineData will have our labels and datasets
//        LineData data = new LineData(labels, dataset);
//        //curves our lines instead of points
//        dataset.setDrawCubic(true);
//        //fills in the area under the curve
//        dataset.setDrawFilled(true);
//        //set data into our chart field in xml
//        lineChart.setData(data);
//        lineChart.animateY(5000);

    }
}
