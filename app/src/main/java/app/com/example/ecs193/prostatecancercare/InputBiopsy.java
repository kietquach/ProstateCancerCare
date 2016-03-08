package app.com.example.ecs193.prostatecancercare;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.List;

public class InputBiopsy extends Activity {
    private Firebase fbRef;
    private int rows = 0;
    private List<EditText> editTextList = new ArrayList<EditText>();
    Firebase mriEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_biopsy);
        Firebase.setAndroidContext(this);
        fbRef = new Firebase("https://boiling-heat-3817.firebaseio.com/");

        final Button addButton = (Button) findViewById(R.id.addButton);
        addButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AuthData authData = fbRef.getAuth();
                        if (authData != null) {
                            // user authenticated
                            Firebase biopsy = fbRef.child("users").child(authData.getUid()).child("biopsy");
                            String date = ((EditText) findViewById(R.id.dateEdit)).getText().toString();
                            mriEntry = biopsy.child(date);

                            String coresPositive = ((EditText) findViewById(R.id.coresPositiveEdit)).getText().toString();
                            rows = Integer.parseInt(coresPositive);
                            mriEntry.child("corespositive").setValue(coresPositive);
                            mriEntry.child("corestaken").setValue(((EditText) findViewById(R.id.coresTakenEdit)).getText().toString());

                            addTable(rows);
                            addButton.setOnClickListener(null);
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
                        for (int i = 0; i < rows; i++) {
                            Firebase mriLesion = mriEntry.child("postivecore").child((editTextList.get(i * 4).getText().toString()));
                            mriLesion.child("cancer").setValue(editTextList.get(i * 4 + 1).getText().toString());
                            mriLesion.child("gleason").setValue(editTextList.get(i * 4 + 2).getText().toString() + "+" + editTextList.get(i * 4 + 3).getText().toString());
                        }
                        Intent intent = new Intent(InputBiopsy.this, InputData.class);
                        startActivity(intent);
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
            editText.setText(Integer.toString(Integer.parseInt(index) / 4 + 1), TextView.BufferType.EDITABLE);
        }
        if(Integer.parseInt(index) % 4 == 3){
            editText.setGravity(Gravity.LEFT);
        }
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});

        editTextList.add(editText);
        return editText;
    }
}
