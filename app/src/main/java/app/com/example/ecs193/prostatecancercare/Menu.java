package app.com.example.ecs193.prostatecancercare;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.firebase.client.Firebase;

public class Menu extends Activity {
    Firebase fbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Firebase.setAndroidContext(this);

        fbRef = new Firebase("https://boiling-heat-3817.firebaseio.com/");

        Button inputdataButton = (Button) findViewById(R.id.inputdataButton);
        inputdataButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Menu.this, InputData.class);
                        startActivity(intent);
                    }
                }
        );

        Button visualizedataButton = (Button) findViewById(R.id.visualizedataButton);
        visualizedataButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Menu.this, VisualizeData.class);
                        startActivity(intent);
                    }
                }
        );

 /*       Button feedbackButton = (Button) findViewById(R.id.inputdataButton);
        feedbackButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Menu.this, Feedback.class);
                        startActivity(intent);
                    }
                }
        );

        Button faqButton = (Button) findViewById(R.id.inputdataButton);
        faqButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Menu.this, Faq.class);
                        startActivity(intent);
                    }
                }
        );
*/
    }
}
