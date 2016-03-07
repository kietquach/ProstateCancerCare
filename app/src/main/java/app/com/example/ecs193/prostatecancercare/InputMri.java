package app.com.example.ecs193.prostatecancercare;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class InputMri extends Activity {
    private Firebase fbRef;
    private int rows = 0;
    private List<EditText> editTextList = new ArrayList<EditText>();
    Firebase mriEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_mri);
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
                            Firebase mri = fbRef.child("users").child(authData.getUid()).child("mri");
                            String date = ((EditText) findViewById(R.id.dateEdit)).getText().toString();
                            mriEntry = mri.child(date);
                            String lesioncount = ((EditText) findViewById(R.id.lesioncountEdit)).getText().toString();
                            rows = Integer.parseInt(lesioncount);
                            mriEntry.child("lesioncount").setValue(lesioncount);
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
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        for(int i = 0; i < rows; i++) {
                            Firebase mriLesion = mriEntry.child("lesions").child((editTextList.get(i * 6).getText().toString()));
                            mriLesion.child("PIRADS").setValue(editTextList.get(i * 6 + 1).getText().toString());
                            mriLesion.child("cores").setValue(editTextList.get(i * 6 + 2).getText().toString());
                            mriLesion.child("positive").setValue(editTextList.get(i * 6 + 3).getText().toString());
                            mriLesion.child("gleason").setValue(editTextList.get(i * 6 + 4).getText().toString()+"+"+editTextList.get(i * 6 + 5).getText().toString());

                        }
                    }

                }
        );


    }

    private TableLayout addTable(int rows){
        TableLayout tableLayout = (TableLayout)findViewById(R.id.mri);
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

        for (int i = 0; i < rows; i++) {
            tableLayout.addView(createRow(i));
        }
        return tableLayout;
    }

    private TableRow createRow(int row) {
        TableRow tableRow = new TableRow(this);
        tableRow.setPadding(0, 10, 0, 0);
        for (int col = 0; col < 6; col++) {
            tableRow.addView(editText(String.valueOf(row * 6 + col)));
            if(col == 4){
                TextView textView = new TextView(this);
                textView.setText("+", TextView.BufferType.NORMAL);
                tableRow.addView(textView);
            }
        }
        return tableRow;
    }

    private EditText editText(String index) {
        EditText editText = new EditText(this);
        editText.setId(Integer.valueOf(index));
        if(Integer.parseInt(index) % 6 == 0){
            editText.setText(Integer.toString(Integer.parseInt(index) / 6 + 1), TextView.BufferType.EDITABLE);
        }
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});

        editTextList.add(editText);
        return editText;
    }
}
