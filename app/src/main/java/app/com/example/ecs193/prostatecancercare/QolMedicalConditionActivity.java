package app.com.example.ecs193.prostatecancercare;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ext.CoreXMLDeserializers;
import com.firebase.client.Firebase;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by liray on 5/12/2016.
 */
public class QolMedicalConditionActivity extends AppCompatActivity {
    private TextView mQuestion;
    private Button mBackButton;
    private Button mNextButton;
    private LinearLayout mLinearLayout;
    private String[] medicalConditions;
    private String[] treatments;
    private ProgressBar mProgressBar;
    private Firebase ref;
    private ScrollView mScrollView;

    private boolean[] hadTreatment;
    public String[] startDates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qol_medical_condition);
        ref = new Firebase("https://boiling-heat-3817.firebaseio.com/");
        Firebase.setAndroidContext(this);

        mScrollView = (ScrollView) findViewById(R.id.scrollView);
        mQuestion = (TextView) findViewById(R.id.question_textview);
        mBackButton = (Button) findViewById(R.id.back_button);
        mNextButton = (Button) findViewById(R.id.next_button);
        mLinearLayout = (LinearLayout) findViewById(R.id.medical_conditions_container);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setProgress(9);

        mQuestion.setText(getResources().getStringArray(R.array.qol_questions)[8]);
        medicalConditions = getResources().getStringArray(R.array.qol8_choices);
        treatments = getResources().getStringArray(R.array.qol9_choices);
        hadTreatment = new boolean[treatments.length];
        startDates = new String[treatments.length];
        Calendar c = GregorianCalendar.getInstance();
        for (int i = 0; i < startDates.length; i++) {
            startDates[i] = "" + (c.get(Calendar.MONTH) + 1) + "/" + (c.get(Calendar.YEAR) + 1);
        }

        for (int i = 0; i < medicalConditions.length; i++) {
            CheckBox checkBox = new CheckBox(getApplicationContext());
            checkBox.setText(medicalConditions[i]);
            checkBox.setId(8888 + i);
            checkBox.setTextColor(Color.parseColor("#000000"));
            mLinearLayout.addView(checkBox);
        }

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Firebase fbref = ref.child("users").child(ref.getAuth().getUid()).child("qol").child("medical conditions");
                for (int i = 0; i < medicalConditions.length; i++) {
                    fbref.child(medicalConditions[i]).setValue(((CheckBox) findViewById(8888 + i)).isChecked());
                }
                ((ViewGroup)mBackButton.getParent()).removeView(mBackButton);
                mQuestion.setText(getResources().getStringArray(R.array.qol_questions)[9]);
                mNextButton.setText("Submit");
                RelativeLayout relativeLayout = (RelativeLayout) mScrollView.getParent();
                relativeLayout.removeView(mScrollView);
                mProgressBar.setProgress(10);
                ListView listView = (ListView)getLayoutInflater().inflate(R.layout.treatments_container, null);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ListView.LayoutParams.WRAP_CONTENT, ListView.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_LEFT, R.id.question_textview);
                params.addRule(RelativeLayout.BELOW, R.id.question_textview);
                params.addRule(RelativeLayout.ABOVE, R.id.next_button);
                listView.setLayoutParams(params);
                relativeLayout.addView(listView);
                CustomAdapter adapter = new CustomAdapter(QolMedicalConditionActivity.this, treatments, hadTreatment, startDates);
                listView.setAdapter(adapter);

                changeOnClickListener();
            }
        });
    }

    private void changeOnClickListener() {
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Firebase fbref = ref.child("users").child(ref.getAuth().getUid()).child("qol").child("treatments");
                for (int i = 0; i < treatments.length; i++) {
                    if (hadTreatment[i]) {
                        fbref.child(treatments[i]).setValue(startDates[i]);
                    } else {
                        fbref.child(treatments[i]).setValue(null);
                    }
                }
                Intent intent = new Intent(QolMedicalConditionActivity.this, HomePageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // call this to finish the current activity
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
