package app.com.example.ecs193.prostatecancercare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by liray on 2/4/2016.
 */
public class NotificationActivity extends Activity {
    private Button mHomeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notification);
        mHomeButton = (Button) findViewById(R.id.button_home);
        mHomeButton.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(NotificationActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
        );
    }
}
