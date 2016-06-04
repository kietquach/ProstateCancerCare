package app.com.example.ecs193.prostatecancercare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
import java.text.DecimalFormat;
import java.lang.Math;

import java.util.ArrayList;


public class VisualizeData extends AppCompatActivity {
//    @Override
    Firebase fbRef = new Firebase("https://boiling-heat-3817.firebaseio.com/");
    boolean noData = false;
    private TextView densityText;
    private TextView doubleText;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualize_data);

        Button psaButton = (Button) findViewById(R.id.psaButton);
        AuthData authData = fbRef.getAuth();
        final Firebase psa = fbRef.child("users").child(authData.getUid()).child("psa");

        psa.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            //Flip this around, add the graphing stuff inside this onDataChange, because thats when we have the data to graph. Cant
            //save here and then do it cuz async
            public void onDataChange(DataSnapshot snapshot) {
                float volume = 0;
                float psaRecent = 0;
                float psaInitial = 0;
                String initialTime = "000000";
                String recentTime = "000000";
                boolean inLoop = false;
                boolean first = true;
                boolean snapDoesNotExist = true;
                //queue, when q length 3 pop then push
                ArrayList al = new ArrayList();
                for (DataSnapshot child : snapshot.getChildren()) {
                    inLoop = true;
//                    System.out.println("In Here");
                    if (child.exists()) {
                        al.add(child);
                        PsaData latestData = child.getValue(PsaData.class);
                        volume = Float.parseFloat(latestData.getVolume());
                        psaRecent = latestData.getPsa();
                        recentTime = child.getKey();
                        if(first) {
                            psaInitial = psaRecent;
                            initialTime = recentTime;
                            first = false;
                        }
                        String initialYear = "";
                        String initialMonth = "";
                        String recentYear = "";
                        String recentMonth = "";
                        for(int i = 0; i < 4; i ++){
                            initialYear += initialTime.charAt(i);
                            recentYear += recentTime.charAt(i);
                        }
                        for(int i = 4; i < 6; i++){
                            initialMonth += initialTime.charAt(i);
                            recentMonth += recentTime.charAt(i);
                        }
                        float duration = Float.parseFloat(recentYear) - Float.parseFloat(initialYear);
                        duration *= 12;
                        duration += (Float.parseFloat(recentMonth) - Float.parseFloat(initialMonth));
//                System.out.println(duration);

                        densityText = (TextView) findViewById(R.id.psaDensityText);
                        if(Double.isNaN(psaRecent / volume) || inLoop == false){
                            densityText.setText("PSA Density: " + "\nNo Data");
                        }
                        else {
                            //store back density
                            String d = new DecimalFormat("@@@").format((psaRecent / volume));
                            psa.child(child.getKey()).child("density").setValue(d);
                            densityText.setText("PSA Density: " + d );
                        }
                        doubleText = (TextView) findViewById(R.id.psaDoubleTime);
                        String dbt = new DecimalFormat("@@@").format(Math.log(duration)/(Math.log(psaRecent) - Math.log(psaInitial)));
                        double dbtF = Math.log(duration)/(Math.log(psaRecent) - Math.log(psaInitial));
                        if(initialTime == "000000" && recentTime =="000000" || inLoop == false ) {
                            doubleText.setText("PSA Doubling Time: " + "\nNo Data");
                        }
                        else if( Double.isNaN(dbtF) || Double.isInfinite(dbtF)){
                            doubleText.setText("PSA Doubling Time: \nNo Change");
                        }
                        else{
                            doubleText.setText("PSA Doubling Time: " + dbt);
                        }
                        snapDoesNotExist= false;
                    }
                    else{
                        snapDoesNotExist = true;
                    }
                }
                if(al.size() >= 3){
//                    System.out.println("INSIDE INSIDE INSIDE");
                    int last = al.size() - 1;
                    DataSnapshot c3 = (DataSnapshot) al.get(last);
                    DataSnapshot c2 = (DataSnapshot) al.get(last - 1);
                    DataSnapshot c = (DataSnapshot) al.get(last - 2);
                    String t3 = c3.getKey();
                    String t2 = c2.getKey();
                    String t1 = c.getKey();
                    PsaData p3 = c3.getValue(PsaData.class);
                    PsaData p2 = c2.getValue(PsaData.class);
                    PsaData p1 = c.getValue(PsaData.class);
                    String t3Year = "";
                    String t3Month = "";
                    String t2Year = "";
                    String t2Month = "";
                    String t1Year = "";
                    String t1Month = "";
                    for(int i = 0; i < 4; i ++){
                        t3Year += t3.charAt(i);
                        t2Year += t2.charAt(i);
                        t1Year += t1.charAt(i);
                    }
                    for(int i = 4; i < 6; i++){
                        t3Month += t3.charAt(i);
                        t2Month += t2.charAt(i);
                        t1Month += t1.charAt(i);
                    }
                    float duration = Float.parseFloat(t3Year) - Float.parseFloat(t1Year);
                    duration *= 12;
                    duration += (Float.parseFloat(t3Month)  - Float.parseFloat(t1Month));
                    doubleText = (TextView) findViewById(R.id.psaDoubleTime);
                    String dbt = new DecimalFormat("@@@").format(Math.abs(Math.log(duration)/(Math.log(p3.getPsa()) - Math.log(p2.getPsa()) - Math.log(p1.getPsa()))));
                    double dbtF = Math.log(duration)/(Math.log(p3.getPsa()) - Math.log(p2.getPsa()) - Math.log(p1.getPsa()));
                    System.out.println(dbtF);
                    System.out.println(dbt);
                    System.out.println(duration);
                    System.out.println(p3.getPsa());
                    System.out.println(p2.getPsa());
                    System.out.println(p1.getPsa());
                    if(initialTime == "000000" && recentTime =="000000" || inLoop == false ) {
                        doubleText.setText("PSA Doubling Time: " + "\nNo Data");
                    }
                    else if( Double.isNaN(dbtF) || Double.isInfinite(dbtF)){
                        doubleText.setText("PSA Doubling Time: \nNo Change");
                    }
                    else{
                        doubleText.setText("PSA Doubling Time: " + dbt);
                    }
                }
                if(snapDoesNotExist){
                    noData = true;
                    doubleText = (TextView) findViewById(R.id.psaDoubleTime);
                    if(initialTime == "000000" && recentTime =="000000" || inLoop == false ) {
                        doubleText.setText("PSA Doubling Time: " + "\nNo Data");
                    }
                    densityText = (TextView) findViewById(R.id.psaDensityText);
                    if(Double.isNaN(psaRecent / volume) || inLoop == false){
                        densityText.setText("PSA Density: " + "\nNo Data");
                    }
                }
//                String initialYear = "";
//                String initialMonth = "";
//                String recentYear = "";
//                String recentMonth = "";
//                for(int i = 0; i < 4; i ++){
//                    initialYear += initialTime.charAt(i);
//                    recentYear += recentTime.charAt(i);
//                }
//                for(int i = 4; i < 6; i++){
//                    initialMonth += initialTime.charAt(i);
//                    recentMonth += recentTime.charAt(i);
//                }
//                float duration = Float.parseFloat(recentYear) - Float.parseFloat(initialYear);
//                duration *= 12;
//                duration += (Float.parseFloat(recentMonth) - Float.parseFloat(initialMonth));
////                System.out.println(duration);
//
//                densityText = (TextView) findViewById(R.id.psaDensityText);
//                if(Double.isNaN(psaRecent / volume)){
//                    densityText.setText("PSA Density: " + "\nNo Data");
//                }
//                else {
//                    densityText.setText("PSA Density: " + (psaRecent / volume));
//                }
//                doubleText = (TextView) findViewById(R.id.psaDoubleTime);
//                String dbt = new DecimalFormat("@@@").format((Math.log(duration)/(Math.log(psaRecent) - Math.log(psaInitial))));
//                if(initialTime == "000000" && recentTime =="000000") {
//                    doubleText.setText("PSA Doubling Time: " + "\nNo Data");
//                }
//                else{
//                    doubleText.setText("PSA Doubling Time: " + dbt);
//                }
                //
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        psaButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        AuthData authData = fbRef.getAuth();
//                        Firebase psa = fbRef.child("users").child(authData.getUid()).child("psa");

//                        psa.addListenerForSingleValueEvent(new ValueEventListener() {
//
//                            @Override
//                            //Flip this around, add the graphing stuff inside this onDataChange, because thats when we have the data to graph. Cant
//                            //save here and then do it cuz async
//                            public void onDataChange(DataSnapshot snapshot) {
//                                for (DataSnapshot child : snapshot.getChildren()) {
//                                    if (!child.exists()) {
//                                        noData = true;
//                                        System.out.println("no child");
//                                    }
//                                    else{
//                                        noData = false;
//                                        System.out.println("child exist");
//                                    }
//
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(FirebaseError firebaseError) {
//                            }
//                        });
                        if(noData){
                            Toast.makeText(VisualizeData.this, "No Data To Visualize", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            System.out.println("WENT TO NEW PAGE");
                            System.out.println(noData);
                            Intent intent = new Intent(VisualizeData.this, PsaGraph.class);
                            startActivity(intent);
                        }

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
