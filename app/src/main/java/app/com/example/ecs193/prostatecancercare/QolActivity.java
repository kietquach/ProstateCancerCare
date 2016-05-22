package app.com.example.ecs193.prostatecancercare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

/**
 * Created by liray on 5/11/2016.
 */
public class QolActivity extends AppCompatActivity {
    private TextView mQuestion;
    private Button mBackButton;
    private Button mNextButton;
    private ProgressBar mProgressBar;
    private RadioGroup mRadioGroup;
    private String[] questions;
    String[][] choices;
    int questionNumber;
    String answers[];
    int answersNum[];
    Firebase ref;

    public class ButtonListener implements View.OnClickListener {
        private int change;

        public ButtonListener(int change) {
            this.change = change;
        }

        @Override
        public void onClick(View v) {
            if (mRadioGroup.getCheckedRadioButtonId() == -1) {
                Toast.makeText(getApplicationContext(), "Please select one of the options", Toast.LENGTH_SHORT).show();
                return;
            }
            if (questionNumber + change < 0) {
                return;
            }

            if (questionNumber + change >= 8) {
                Firebase fbref = ref.child("users").child(ref.getAuth().getUid()).child("qol");
                for (int i = 0; i < choices.length; i++) {
                    fbref.child("q" + i).setValue(answers[i]);
                }
                Intent intent = new Intent(QolActivity.this, QolMedicalConditionActivity.class);
                startActivity(intent);
                return;
            }

            questionNumber += change;
            mQuestion.setText(questions[questionNumber]);
            changeRadioButtons(questionNumber);
            if (answersNum[questionNumber] > 0) {
                mRadioGroup.check(getId(questionNumber, answersNum[questionNumber]));
            }
            mProgressBar.setProgress(questionNumber + 1);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qol);
        ref = new Firebase("https://boiling-heat-3817.firebaseio.com/");
        Firebase.setAndroidContext(this);

        mQuestion = (TextView) findViewById(R.id.question_textview);
        mBackButton = (Button) findViewById(R.id.back_button);
        mBackButton.setOnClickListener(new ButtonListener(-1));
        mNextButton = (Button) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new ButtonListener(1));
        mRadioGroup = (RadioGroup) findViewById(R.id.qol_radiogroup);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        questionNumber = 0;
        questions = getResources().getStringArray(R.array.qol_questions);
        choices = new String[8][];
        choices[0] = getResources().getStringArray(R.array.qol0_choices);
        choices[1] = getResources().getStringArray(R.array.qol1_choices);
        choices[2] = getResources().getStringArray(R.array.qol2_choices);
        choices[3] = getResources().getStringArray(R.array.qol3_choices);
        choices[4] = getResources().getStringArray(R.array.qol4_choices);
        choices[5] = getResources().getStringArray(R.array.qol5_choices);
        choices[6] = getResources().getStringArray(R.array.qol6_choices);
        choices[7] = getResources().getStringArray(R.array.qol7_choices);
        answers = new String[8];
        answersNum = new int[8];
        for (int i = 0; i < answersNum.length; i++) {
            answersNum[i] = -1;
        }

        mQuestion.setText(questions[questionNumber]);
        changeRadioButtons(questionNumber);
        mProgressBar.setProgress(questionNumber + 1);
    }

    private class RadioButtonClickListener implements View.OnClickListener {
        private int questionNumber;
        private int answerNum;

        public RadioButtonClickListener(int questionNumber, int answerNum) {
            this.questionNumber = questionNumber;
            this.answerNum = answerNum;
        }

        @Override
        public void onClick(View v) {
            answers[questionNumber] = choices[questionNumber][answerNum];
            answersNum[questionNumber] = answerNum;
        }
    }

    private int getId(int questionNumber, int choiceNum) {
        return questionNumber * 1000 + choiceNum;
    }

    private void changeRadioButtons(final int questionNumber) {
        mRadioGroup.removeAllViews();
        mRadioGroup.clearCheck();
        RadioGroup.LayoutParams params_rb = new RadioGroup.LayoutParams(getBaseContext(), null);
        params_rb.setMargins(10, 0, 0, 0);
        for (int i = 0; i < choices[questionNumber].length; i++) {
            RadioButton button = new RadioButton(this);
            button.setText(choices[questionNumber][i]);
            button.setId(getId(questionNumber, i));
            button.setLayoutParams(params_rb);
            button.setOnClickListener(new RadioButtonClickListener(questionNumber, i));
            mRadioGroup.addView(button);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
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
