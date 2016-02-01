package app.com.example.ecs193.prostatecancercare;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.firebase.client.Firebase;

import static android.view.View.*;

/**
 * Created by Kiet Quach on 1/26/2016.
 */
public class MainActivity extends Activity{

    private Button loginButton;
    private Button newUserButton;

    public MainActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Firebase.setAndroidContext(this);

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

        newUserButton = (Button) findViewById(R.id.newUserButton);
        newUserButton.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
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
