package app.com.example.ecs193.prostatecancercare;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DataEditView extends AppCompatActivity {
    private Firebase fbRef = new Firebase("https://boiling-heat-3817.firebaseio.com/");
    private Firebase user;
    private List<EditText> editTextList = new ArrayList<EditText>();
    private List<TextView> textViewList = new ArrayList<TextView>();
    private String date;
    private int previousRows;
    private TableLayout tableLayout;

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
        setContentView(R.layout.activity_data_edit_view);
        Firebase.setAndroidContext(this);
        Intent i = getIntent();

        final String type = i.getStringExtra("type");
        date = i.getStringExtra("date");

        AuthData authData = fbRef.getAuth();
        user = fbRef.child("users").child(authData.getUid());


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
                    editPsaData(dataSnapshot);
                }else if(type.equals("Mri")){
                    editMriData(dataSnapshot);
                }else{
                    editBiopsyData(dataSnapshot);
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

    }


    private void editMriData(DataSnapshot dataSnapshot){
        LinearLayout ll = (LinearLayout)findViewById(R.id.mriLinear);
        TextView text = new TextView(this);
        text.setText(convertDate(date));
        ll.addView(text);
        tableLayout = (TableLayout)findViewById(R.id.mriTable);

        for (final DataSnapshot dates: dataSnapshot.getChildren()) {
            if(dates.getKey().toString().equals(date)){
                //For the date, create EditText fields data entered previously
                for(final DataSnapshot data : dates.getChildren()) {
                    if(data.getKey().equals("lesioncount")){

                        //Create label for Lesion Count and EditText field
                        text = new TextView(this);
                        text.setText("Lesion Count");
                        ll.addView(text);
                        EditText editText = new EditText(this);
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                        editText.setId(R.id.edit_lesioncount);
                        ll.addView(editText);
                        //Store previous value of lesioncount; so we know if it was changed later
                        previousRows = Integer.parseInt(data.getValue().toString());
                        editText.setText(data.getValue().toString());
                        addMriLabel(tableLayout);
                    }else{
                        for(final DataSnapshot lesions : data.getChildren()){
                            String key = lesions.getKey();
                            String pirads = null, cores = null, gleason1 = null, gleason2 = null, positive = null;
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

        Button updateButton = new Button(this);
        updateButton.setText("Update");
        updateButton.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        AuthData authData = fbRef.getAuth();
                        if (authData != null) {
                            Firebase mri = fbRef.child("users").child(authData.getUid()).child("mri");

                            if(!date.isEmpty()) {
                                Firebase mriEntry = mri.child(date);
                                String lesioncount = ((EditText) findViewById(R.id.edit_lesioncount)).getText().toString();
                                if(Integer.parseInt(lesioncount) != previousRows){
                                    deleteRows();
                                    for(int i = 0; i < Integer.parseInt(lesioncount); i++) {
                                        tableLayout.addView(createMriRow(Integer.toString(i+1), "", "", "", "", ""));
                                    }
                                    previousRows = Integer.parseInt(lesioncount);
                                    Toast.makeText(DataEditView.this, "Please fill out all the fields, then press Update again.", Toast.LENGTH_LONG).show();
                                }else{
                                    //Check if all entries are filled;
                                    boolean complete = true;
                                    for(EditText editText : editTextList){
                                        if(editText.getText().toString().compareTo("") == 0){
                                            complete = false;
                                        }
                                    }

                                    //If all fields are filled update database and exit
                                    if(complete &&  ((EditText) findViewById(R.id.edit_lesioncount)).getText().toString() != ""){
                                        //Set the number of lesions
                                        mriEntry.child("lesioncount").setValue(lesioncount);
                                        //Delete Entry
                                        deleteMriChild(mri, date);
                                        //Set the data for each lesion
                                        for(int i = 0; i < Integer.parseInt(lesioncount); i++) {
                                            Firebase mriLesion = mriEntry.child("lesions").child((editTextList.get(i * 6).getText().toString()));
                                            mriLesion.child("PIRADS").setValue(editTextList.get(i * 6 + 1).getText().toString());
                                            mriLesion.child("cores").setValue(editTextList.get(i * 6 + 2).getText().toString());
                                            mriLesion.child("positive").setValue(editTextList.get(i * 6 + 3).getText().toString());
                                            mriLesion.child("gleason").setValue(editTextList.get(i * 6 + 4).getText().toString() + "+" + editTextList.get(i * 6 + 5).getText().toString());
                                        }
                                        Toast.makeText(DataEditView.this, "Entry has been updated.", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(DataEditView.this, DataView.class);
                                        startActivity(intent);
                                    }else{
                                        Toast.makeText(DataEditView.this, "Please fill out all the fields.", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }

                        } else {
                            // no user authenticated
                        }

                    }
                }
        );

        ll.addView(updateButton);
        Button deleteButton = new Button(this);
        deleteButton.setText("Delete Entry");
        deleteButton.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        deleteDate("mri", date);
                        Toast.makeText(DataEditView.this, "Entry has been deleted.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(DataEditView.this, DataView.class);
                        startActivity(intent);
                    }
                }

        );
        ll.addView(deleteButton);

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

    private void deleteMriChild(Firebase mri, String date){
        mri.child(date).child("lesions").setValue(null);
    }

    private void deleteBiopsyChild(Firebase biopsy, String date){
        biopsy.child(date).child("lesions").setValue(null);
    }

    private void deleteDate(String type, String date){
        if(type.compareTo("psa") == 0){
            user.child("psa").child(date).setValue(null);
        }else if(type.compareTo("mri") == 0){
            user.child("mri").child(date).setValue(null);
        }else{
            user.child("biopsy").child(date).setValue(null);
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
                case 0:
                    tableRow.addView(editTextMri(String.valueOf(Integer.parseInt(row) * 6 + col), row));
                    break;
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
                    TextView textView = new TextView(this);
                    textView.setText("+", TextView.BufferType.NORMAL);
                    textView.setGravity(Gravity.CENTER);
                    textViewList.add(textView);
                    tableRow.addView(textView);
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
            editText.setText(Integer.toString(Integer.parseInt(index) / 6 + 1)); //TextView.BufferType.EDITABLE);
            editText.setFocusable(false);
        }
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setText(value);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});
        editTextList.add(editText);
        return editText;
    }


    private void editBiopsyData(DataSnapshot dataSnapshot){
        LinearLayout ll = (LinearLayout)findViewById(R.id.biopsyLinear);
        TextView text = new TextView(this);
        text.setText(convertDate(date));
        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
        text.setGravity(Gravity.CENTER);
        ll.addView(text);
        tableLayout = (TableLayout)findViewById(R.id.biopsyTable);
        for (final DataSnapshot dates: dataSnapshot.getChildren()) {
            if(dates.getKey().toString().equals(date)){
                //For the date, create EditText fields data entered previously
                for(final DataSnapshot data : dates.getChildren()) {

                    if(data.getKey().equals("corestaken")) {
                        text = new TextView(this);
                        text.setText("Cores Taken");
                        ll.addView(text);
                        EditText editText = new EditText(this);
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                        editText.setId(R.id.edit_corestaken);
                        ll.addView(editText);
                        editText.setText(data.getValue().toString());
                    }else if(data.getKey().equals("corespositive")){
                        Log.i("here", "positive");
                        text = new TextView(this);
                        text.setText("Cores Positive");
                        ll.addView(text);
                        EditText editText = new EditText(this);
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                        editText.setId(R.id.edit_corespositive);
                        ll.addView(editText);
                        //Store previous value so we know if it was changed later
                        previousRows = Integer.parseInt(data.getValue().toString());
                        editText.setText(data.getValue().toString());
                    }else{
                        addBiopsyLabel(tableLayout);
                        for(final DataSnapshot positivecore : data.getChildren()){
                            String key = positivecore.getKey();
                            String cancer = null, gleason1 = null, gleason2 = null;
                            for(final DataSnapshot values : positivecore.getChildren()){
                                if(values.getKey().equals("cancer")){
                                    cancer = values.getValue().toString();
                                }else if(values.getKey().equals("cores")){
                                    cancer = values.getValue().toString();
                                }else if(values.getKey().equals("gleason")){
                                    String gleason = values.getValue().toString();
                                    String delims = "[+]+";
                                    String[] tokens = gleason.split(delims);
                                    gleason1 = tokens[0];
                                    gleason2 = tokens[1];
                                }else{
                                    cancer = values.getValue().toString();
                                }
                            }
                            tableLayout.addView(createBiopsyRow(key, cancer, gleason1, gleason2));
                        }
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
                            Firebase biopsy = fbRef.child("users").child(authData.getUid()).child("biopsy");

                            if(!date.isEmpty()) {
                                Firebase biopsyEntry = biopsy.child(date);
                                String positive = ((EditText) findViewById(R.id.edit_corespositive)).getText().toString();
                                if(Integer.parseInt(positive) != previousRows){
                                    deleteRows();
                                    for(int i = 0; i < Integer.parseInt(positive); i++) {
                                        tableLayout.addView(createMriRow(Integer.toString(i+1), "", "", "", "", ""));
                                    }
                                    previousRows = Integer.parseInt(positive);
                                    Toast.makeText(DataEditView.this, "Please fill out all the fields, then press Update again.", Toast.LENGTH_LONG).show();
                                }else{
                                    //Check if all entries are filled;
                                    boolean complete = true;
                                    for(EditText editText : editTextList){
                                        if(editText.getText().toString().compareTo("") == 0){
                                            complete = false;
                                        }
                                    }

                                    //If all fields are filled update database and exit
                                    if(complete &&  ((EditText) findViewById(R.id.edit_corespositive)).getText().toString() != ""){
                                        //Set the number of lesions
                                        biopsyEntry.child("corespostive").setValue(positive);
                                        //Delete Entry
                                        deleteBiopsyChild(biopsy, date);
                                        //Set the data for each lesion
                                        for(int i = 0; i < Integer.parseInt(positive); i++) {
                                            Firebase biopsyCore = biopsyEntry.child("positivecore").child((editTextList.get(i * 4).getText().toString()));
                                            biopsyCore.child("cancer").setValue(editTextList.get(i * 4 + 1).getText().toString());
                                            biopsyCore.child("gleason").setValue(editTextList.get(i * 4 + 2).getText().toString() + "+" + editTextList.get(i * 6 + 3).getText().toString());
                                        }
                                        Toast.makeText(DataEditView.this, "Entry has been updated.", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(DataEditView.this, DataView.class);
                                        startActivity(intent);
                                    }else{
                                        Toast.makeText(DataEditView.this, "Please fill out all the fields.", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }

                        } else {
                            // no user authenticated
                        }

                    }
                }
        );

        ll.addView(updateButton);
        Button deleteButton = new Button(this);
        deleteButton.setText("Delete Entry");
        deleteButton.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        deleteDate("biopsy", date);
                        Toast.makeText(DataEditView.this, "Entry has been deleted.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(DataEditView.this, DataView.class);
                        startActivity(intent);
                    }
                }

        );
        ll.addView(deleteButton);

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

    private void deleteRows(){
        for(EditText editText : editTextList){
            editText.setVisibility(editText.GONE);
        }
        for(TextView textView : textViewList){
            textView.setVisibility(textView.GONE);
        }
        editTextList = null;
        textViewList = null;
        editTextList = new ArrayList<EditText>();
        textViewList = new ArrayList<TextView>();
    }


    private void addBiopsyLabel(TableLayout tableLayout){
        tableLayout.setStretchAllColumns(true);
        TableRow tableRow = new TableRow(this);
        tableRow.setPadding(0, 10, 0, 0);

        for(int col = 0; col < 5; col++){
            TextView textView = new TextView(this);
            switch (col){
                case 0:
                    textView.setText("Postive Core", TextView.BufferType.NORMAL );
                    break;
                case 1:
                    textView.setText("% Cancer", TextView.BufferType.NORMAL);
                    break;
                case 2:
                    textView.setText("Gleason", TextView.BufferType.NORMAL);
                    break;
            }
            tableRow.addView(textView);
        }
        tableLayout.addView(tableRow);
    }
    private TableRow createBiopsyRow(String row, String cancer, String gleason1, String gleason2) {
        TableRow tableRow = new TableRow(this);
        tableRow.setPadding(0, 10, 0, 0);
        for (int col = 0; col < 4; col++) {
            switch (col){
                case 0:
                    tableRow.addView(editTextBiopsy(String.valueOf(Integer.parseInt(row) * 6 + col), row));
                    break;
                case 1:
                    tableRow.addView(editTextBiopsy(String.valueOf(Integer.parseInt(row) * 6 + col), cancer));
                    break;
                case 2:
                    tableRow.addView(editTextBiopsy(String.valueOf(Integer.parseInt(row) * 6 + col), gleason1));
                    TextView textView = new TextView(this);
                    textView.setText("+", TextView.BufferType.NORMAL);
                    textView.setGravity(Gravity.CENTER);
                    textViewList.add(textView);
                    tableRow.addView(textView);
                    break;
                case 3:
                    tableRow.addView(editTextBiopsy(String.valueOf(Integer.parseInt(row) * 6 + col), gleason2));
                    break;

            }
        }
        return tableRow;
    }

    private EditText editTextBiopsy(String index, String value) {
        EditText editText = new EditText(this);
        editText.setId(Integer.valueOf(index));
        if(Integer.parseInt(index) % 4 == 0){
            editText.setText(Integer.toString(Integer.parseInt(index) / 6 + 1));
            editText.setFocusable(false);
        }
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setText(value);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});
        editTextList.add(editText);
        return editText;
    }

    private void editPsaData(DataSnapshot dataSnapshot){
        LinearLayout ll = (LinearLayout)findViewById(R.id.dataLinear);
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
                        editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        editText.setId(R.id.edit_psa);
                        ll.addView(editText);
                    }else if(d.getKey().equals("density")){
                        text = new TextView(this);
                        text.setText("Psa Density");
                        ll.addView(text);
                        EditText editText = new EditText(this);
                        editText.setText(d.getValue().toString());
                        editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        editText.setId(R.id.edit_density);
                        ll.addView(editText);
                    }else{
                        text = new TextView(this);
                        text.setText("Psa Volume");
                        ll.addView(text);
                        EditText editText = new EditText(this);
                        editText.setText(d.getValue().toString());
                        editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
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
                            Toast.makeText(DataEditView.this, "Entry has been updated.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(DataEditView.this, DataView.class);
                            startActivity(intent);
                        } else {
                            // no user authenticated
                        }

                    }
                }
        );
        ll.addView(updateButton);

        Button deleteButton = new Button(this);
        deleteButton.setText("Delete Entry");
        deleteButton.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        deleteDate("psa", date);
                        Toast.makeText(DataEditView.this, "Entry has been deleted.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(DataEditView.this, DataView.class);
                        startActivity(intent);
                    }
                }

        );
        ll.addView(deleteButton);

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
