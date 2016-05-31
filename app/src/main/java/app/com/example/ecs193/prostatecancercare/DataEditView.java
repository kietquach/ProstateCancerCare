package app.com.example.ecs193.prostatecancercare;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewDebug;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DataEditView extends AppCompatActivity {
    Firebase fbRef = new Firebase("https://boiling-heat-3817.firebaseio.com/");
    private List<EditText> editTextMriList = new ArrayList<EditText>();
    private String date;

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
        return getDateStr(Integer.parseInt(date.substring(4, 6)),
        Integer.parseInt(date.substring(6, 8)),
        Integer.parseInt(date.substring(0, 4)));
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_edit_view);
        Firebase.setAndroidContext(this);
        Intent i = getIntent();

        final String type = i.getStringExtra("type");
        date = i.getStringExtra("date");

        AuthData authData = fbRef.getAuth();
        Firebase user = fbRef.child("users").child(authData.getUid());


        Query q;
        if(type.equals("Psa")){
            q = user.child("psa");
        }else if(type.equals("Mri")){
            q = user.child("mri");
        }else{
            q = user.child("biopsy");
        }

        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(type.equals("Psa")){
                    editPsaData(dataSnapshot, date);
                }else if(type.equals("Mri")){
                    Log.i("MRI", "this");
                    editMriData(dataSnapshot, date);
                }else{
                    editBiopsyData(dataSnapshot, date);
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

    }


    private void editMriData(DataSnapshot dataSnapshot, String date){
        LinearLayout ll = (LinearLayout)findViewById(R.id.mriLinear);
        TextView text = new TextView(this);
        text.setText(convertDate(date));
        ll.addView(text);
        TableLayout tableLayout = (TableLayout)findViewById(R.id.mriTable);
        for (final DataSnapshot dates: dataSnapshot.getChildren()) {
            if(dates.getKey().toString().equals(date)){
                for(final DataSnapshot data : dates.getChildren()) {
                    if(data.getKey().equals("lesioncount")){
                        text = new TextView(this);
                        text.setText("Lesion Count");
                        ll.addView(text);
                        EditText editText = new EditText(this);
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                        ll.addView(editText);
                        editText.setText(data.getValue().toString());
                        addMriLabel(tableLayout);
                    }else{
                        System.out.println("LESION's number of children " + data.getChildrenCount());
                        for(final DataSnapshot lesions : data.getChildren()){
                            String key = lesions.getKey();
                            String pirads = null, cores = null, gleason1 = null, gleason2 = null, positive = null;
                            System.out.println("This LESION has  " + lesions.getChildrenCount() + " entries.");
                            for(final DataSnapshot values : lesions.getChildren()){
                                if(values.getKey().equals("PIRADS")){
                                    pirads = values.getValue().toString();
                                }else if(values.getKey().equals("cores")){
                                    cores = values.getValue().toString();
                                }else if(values.getKey().equals("gleason")){
                                    String gleason = values.getValue().toString();
                                    String delims = "[+]+";
                                    String[] tokens = gleason.split(delims);
                                    gleason1 = tokens[0];
                                    gleason2 = tokens[1];
                                }else{
                                    positive = values.getValue().toString();
                                }
                            }
                            tableLayout.addView(createMriRow(key, pirads, cores, gleason1, gleason2, positive));
                        }
                    }
                }

            }
        }
    }

    private void addMriLabel(TableLayout tableLayout){

        tableLayout.setStretchAllColumns(true);
        TableRow tableRow = new TableRow(this);
        tableRow.setPadding(0, 10, 0, 0);

        for(int col = 0; col < 5; col++){
            TextView textView = new TextView(this);
            switch (col){
                case 0:
                    textView.setText("Lesion", TextView.BufferType.NORMAL );
                    break;
                case 1:
                    textView.setText("PIRADS", TextView.BufferType.NORMAL);
                    break;
                case 2:
                    textView.setText("Cores", TextView.BufferType.NORMAL);
                    break;
                case 3:
                    textView.setText("Positive", TextView.BufferType.NORMAL);
                    break;
                case 4:
                    textView.setText("Gleason", TextView.BufferType.NORMAL);
                    break;
            }
            tableRow.addView(textView);
        }
        tableLayout.addView(tableRow);
    }

    private TableRow createMriRow(String row, String pirads, String cores, String gleason1, String gleason2, String positve) {
        TableRow tableRow = new TableRow(this);
        tableRow.setPadding(0, 10, 0, 0);
        for (int col = 0; col < 6; col++) {
            switch (col){
                case 1:
                    tableRow.addView(editTextMri(String.valueOf(Integer.parseInt(row) * 6 + col), pirads));
                    break;
                case 2:
                    tableRow.addView(editTextMri(String.valueOf(Integer.parseInt(row) * 6 + col), cores));
                    break;
                case 3:
                    tableRow.addView(editTextMri(String.valueOf(Integer.parseInt(row) * 6 + col), positve));
                    break;
                case 4:
                    tableRow.addView(editTextMri(String.valueOf(Integer.parseInt(row) * 6 + col), gleason1));
                    break;
                case 5:
                    tableRow.addView(editTextMri(String.valueOf(Integer.parseInt(row) * 6 + col), gleason2));
                    break;
            }
        }
        return tableRow;
    }

    private EditText editTextMri(String index, String value) {
        EditText editText = new EditText(this);
        editText.setId(Integer.valueOf(index));
        if(Integer.parseInt(index) % 6 == 0){
            editText.setText(Integer.toString(Integer.parseInt(index) / 6 + 1), TextView.BufferType.EDITABLE);
        }
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setText(value);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});
        editTextMriList.add(editText);
        return editText;
    }


    private void editBiopsyData(DataSnapshot dataSnapshot, String date){
        LinearLayout ll = (LinearLayout)findViewById(R.id.dataLinear);

        TableRow row = new TableRow(this);
        TextView text = new TextView(this);
        text.setText(convertDate(date));
        ll.addView(text);

        for (final DataSnapshot data: dataSnapshot.getChildren()) {

            if(data.getKey().toString().equals(date)){
                System.out.println(data.getChildrenCount());
                for(final DataSnapshot d : data.getChildren()) {
                    if(d.getKey().equals("psa")){
                        text = new TextView(this);
                        text.setText("Psa Level");
                        ll.addView(text);
                        EditText editText = new EditText(this);
                        editText.setText(d.getValue().toString());
                        ll.addView(editText);
                    }else if(d.getKey().equals("density")){
                        text = new TextView(this);
                        text.setText("Psa Density");
                        ll.addView(text);
                        EditText editText = new EditText(this);
                        editText.setText(d.getValue().toString());
                        ll.addView(editText);
                    }else{
                        text = new TextView(this);
                        text.setText("Psa Volume");
                        ll.addView(text);
                        EditText editText = new EditText(this);
                        editText.setText(d.getValue().toString());
                        ll.addView(editText);
                    }
                }

            }
        }
    }

    private void editPsaData(DataSnapshot dataSnapshot,final String date){
        LinearLayout ll = (LinearLayout)findViewById(R.id.dataLinear);
        TableRow row = new TableRow(this);
        TextView text = new TextView(this);
        text.setText(convertDate(date));
        ll.addView(text);

        for (final DataSnapshot data: dataSnapshot.getChildren()) {

            if(data.getKey().toString().equals(date)){
                System.out.println(data.getChildrenCount());
                for(final DataSnapshot d : data.getChildren()) {
                    if(d.getKey().equals("psa")){
                        text = new TextView(this);
                        text.setText("Psa Level");
                        ll.addView(text);
                        EditText editText = new EditText(this);
                        editText.setText(d.getValue().toString());
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                        editText.setId(R.id.edit_psa);
                        ll.addView(editText);
                    }else if(d.getKey().equals("density")){
                        text = new TextView(this);
                        text.setText("Psa Density");
                        ll.addView(text);
                        EditText editText = new EditText(this);
                        editText.setText(d.getValue().toString());
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                        editText.setId(R.id.edit_density);
                        ll.addView(editText);
                    }else{
                        text = new TextView(this);
                        text.setText("Psa Volume");
                        ll.addView(text);
                        EditText editText = new EditText(this);
                        editText.setText(d.getValue().toString());
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                        editText.setId(R.id.edit_volume);
                        ll.addView(editText);
                    }
                }

            }
        }
        Button updateButton = new Button(this);
        updateButton.setText("Update");
        updateButton.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        AuthData authData = fbRef.getAuth();
                        if (authData != null) {
                            Firebase psa = fbRef.child("users").child(authData.getUid()).child("psa");
                            if(!date.isEmpty()) {
                                Firebase psaEntry = psa.child(date);
                                //does not save empty data
                                if (((EditText) findViewById(R.id.edit_psa)).getText().toString() != "") {
                                    psaEntry.child("psa").setValue(((EditText) findViewById(R.id.edit_psa)).getText().toString());
                                }
                                if (((EditText) findViewById(R.id.edit_density)).getText().toString() != "") {
                                    psaEntry.child("density").setValue(((EditText) findViewById(R.id.edit_density)).getText().toString());
                                }
                                if (((EditText) findViewById(R.id.edit_volume)).getText().toString() != "") {
                                    psaEntry.child("volume").setValue(((EditText) findViewById(R.id.edit_volume)).getText().toString());
                                }

                            }
                            Intent intent = new Intent(DataEditView.this, DataView.class);
                            startActivity(intent);
                        } else {
                            // no user authenticated
                        }

                    }
                }
        );
        ll.addView(updateButton);
        Button backButton = new Button(this);
        backButton.setText("Back");
        backButton.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        Intent intent = new Intent(DataEditView.this, DataView.class);
                        startActivity(intent);
                    }
                }
        );
        ll.addView(backButton);
    }

}
