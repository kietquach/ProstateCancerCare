package app.com.example.ecs193.prostatecancercare;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import static android.view.View.*;

/**
 * Created by Kiet Quach on 1/26/2016.
 */
public class MainActivity extends Activity{

    Button loginButton;

    public MainActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        //Go into next activity depending on Patient account or Physician Account
                        Intent intent = new Intent(MainActivity.this, ClientFirstTime.class);
                        startActivity(intent);
                    }

                }
        );
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
