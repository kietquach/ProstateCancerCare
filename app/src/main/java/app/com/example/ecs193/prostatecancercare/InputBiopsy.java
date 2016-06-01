package app.com.example.ecs193.prostatecancercare;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
import java.util.Calendar;
import java.util.List;

public class InputBiopsy extends AppCompatActivity  {
    private Firebase fbRef;
    private int rows = 0;
    private List<EditText> editTextList = new ArrayList<EditText>();
    private Firebase biopsyEntry;

    private Calendar calendar;
    private int year, month, day;
    private Button dateButton, doneButton;
    private boolean invalidFlag;
    private boolean dateFlag;
    private boolean fieldsFlag;
    private Firebase biopsy;

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

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            InputBiopsy activity = (InputBiopsy) getActivity();
            activity.year = year;
            activity.month = month;
            activity.day = day;
            activity.dateButton.setText(activity.getDateStr(month, day, year));
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_biopsy);
        Firebase.setAndroidContext(this);
        fbRef = new Firebase("https://boiling-heat-3817.firebaseio.com/");


        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        dateButton = (Button) findViewById(R.id.dateButton);
        doneButton = (Button) findViewById(R.id.doneButton);
        final List<String> dateEntryList = new ArrayList<>();
        fieldsFlag = false;

        //Get all current date entries for this user store in dateEntryList, to check if the date they are entering for already exists
        Query q = fbRef.child("users").child(fbRef.getAuth().getUid()).child("biopsy");
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dateEntry : dataSnapshot.getChildren()){
                    dateEntryList.add(dateEntry.getKey());
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        //Set Today's Date on Button
        dateButton.setText(this.getDateStr(month, day, year));
        dateButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDatePickerDialog(v);
                    }
                }
        );

        final Button createfieldsButton = (Button) findViewById(R.id.createfieldsButton);
        createfieldsButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AuthData authData = fbRef.getAuth();
                        if (authData != null) {
                            // user authenticated
                            biopsy = fbRef.child("users").child(authData.getUid()).child("biopsy");
                            final String date = String.format("%1$4d%2$02d%3$02d", year, month + 1, day);
                            dateFlag = false;

                            String coresPositive = ((EditText) findViewById(R.id.coresPositiveEdit)).getText().toString();
                            String coresTaken = ((EditText) findViewById(R.id.coresTakenEdit)).getText().toString();

                            if(!coresPositive.isEmpty() && !coresTaken.isEmpty() &&
                                    Integer.parseInt(coresPositive) <= Integer.parseInt(coresTaken)){
                                rows = Integer.parseInt(coresPositive);
                                addTable(rows);
                                createfieldsButton.setOnClickListener(null);
                                fieldsFlag = true;
                            }else{
                                if(date.isEmpty() || coresPositive.isEmpty() || coresTaken.isEmpty()) {
                                    Toast.makeText(InputBiopsy.this, "Please fill out all the fields", Toast.LENGTH_SHORT).show();
                                }
                                if(!coresPositive.isEmpty() && !coresTaken.isEmpty() && Integer.parseInt(coresPositive) > Integer.parseInt(coresTaken)){
                                    Toast.makeText(InputBiopsy.this, "Cores Positive should be less than or equal to Cores Taken", Toast.LENGTH_LONG).show();
                                }

                            }

                        } else {
                            // no user authenticated
                        }
                    }
                }
        );

        Button doneButton = (Button) findViewById(R.id.doneButton);
        doneButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean validFlag = false;
                        dateFlag = false;
                        final String date = String.format("%1$4d%2$02d%3$02d", year, month + 1, day);

                        for (String dateEntry : dateEntryList) {
                            if (0 == date.compareTo(dateEntry)) {
                                dateFlag = true;
                            }
                        }
                        //Check if Create Fields Button was pressed and all fields are filled
                        if(fieldsFlag && !((EditText) findViewById(R.id.coresPositiveEdit)).getText().toString().equals("0")){
                            for (int i = 0; i < rows; i++) {
                                if (editTextList.get(i * 4).getText().toString().isEmpty() ||
                                        editTextList.get(i * 4 + 1).getText().toString().isEmpty() ||
                                        editTextList.get(i * 4 + 2).getText().toString().isEmpty() ||
                                        editTextList.get(i * 4 + 3).getText().toString().isEmpty() ){
                                    validFlag = true;
                                }
                            }
                        }
                        String coresPositive = ((EditText) findViewById(R.id.coresPositiveEdit)).getText().toString();
                        String coresTaken = ((EditText) findViewById(R.id.coresTakenEdit)).getText().toString();

                        if(dateFlag) {
                            Toast.makeText(InputBiopsy.this, "This date already has an entry. Pick another date, or edit the entry.", Toast.LENGTH_LONG).show();
                        }else if(coresTaken.isEmpty() || coresPositive.isEmpty() || validFlag) {
                            //If number of lesions field is empty or lesion fields are empty: ask to fill out all fields
                            Toast.makeText(InputBiopsy.this, "Please fill out all the fields.", Toast.LENGTH_SHORT).show();
                        }else if(( !coresPositive.equals("0") || !coresTaken.toString().equals("0")) && !fieldsFlag){
                            Toast.makeText(InputBiopsy.this, "Please click on the Create Fields button.", Toast.LENGTH_SHORT).show();
                        }else {
                            biopsyEntry = biopsy.child(date);
                            biopsyEntry.child("corespositive").setValue(coresPositive);
                            biopsyEntry.child("corestaken").setValue(coresTaken);
                            for (int i = 0; i < rows; i++) {
                                Firebase mriLesion = biopsyEntry.child("positivecore").child((editTextList.get(i * 4).getText().toString()));
                                mriLesion.child("cancer").setValue(editTextList.get(i * 4 + 1).getText().toString());
                                mriLesion.child("gleason").setValue(editTextList.get(i * 4 + 2).getText().toString() + "+" + editTextList.get(i * 4 + 3).getText().toString());
                            }
                            finish();
                            //Intent intent = new Intent(InputBiopsy.this, InputData.class);
                            //startActivity(intent);
                        }
                    }
                }
        );


    }

    private void addTable(int rows){
        TableLayout tableLayout = (TableLayout)findViewById(R.id.biopsytl2);
        tableLayout.setStretchAllColumns(true);
        TableRow tableRow = new TableRow(this);
        tableRow.setPadding(0, 10, 0, 0);

        for(int col = 0; col < 3; col++){
            TextView textView = new TextView(this);
            switch (col){
                case 0:
                    textView.setText("Positive Core", TextView.BufferType.NORMAL);
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

        for (int i = 0; i < rows; i++) {
            tableLayout.addView(createRow(i));
        }
    }

    private TableRow createRow(int row) {
        TableRow tableRow = new TableRow(this);
        tableRow.setPadding(0, 10, 0, 0);
        for (int col = 0; col < 4; col++) {
            tableRow.addView(editText(String.valueOf(row * 4 + col)));
            if(col == 2){
                TextView textView = new TextView(this);
                textView.setText("+", TextView.BufferType.NORMAL);
                textView.setGravity(Gravity.CENTER);
                tableRow.addView(textView);
            }
        }
        return tableRow;
    }

    private EditText editText(String index) {
        EditText editText = new EditText(this);
        editText.setId(Integer.valueOf(index));
        if(Integer.parseInt(index) % 4 == 0){
            editText.setText(Integer.toString(Integer.parseInt(index) / 4 + 1));
            editText.setFocusable(false);
        }
        if(Integer.parseInt(index) % 4 == 3){
            editText.setGravity(Gravity.LEFT);
        }
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});

        editTextList.add(editText);
        return editText;
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