package app.com.example.ecs193.prostatecancercare;

import android.app.Activity;
import android.content.Intent;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Button;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

public class InputData extends Activity {
    private Firebase fbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_data);
        Firebase.setAndroidContext(this);

        ImageButton psaButton = (ImageButton) findViewById(R.id.psaButton);
        psaButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(InputData.this, InputPsa.class);
                        startActivity(intent);
                    }
                }
        );

        ImageButton mriButton = (ImageButton) findViewById(R.id.mriButton);
        mriButton.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        Intent intent = new Intent(InputData.this, InputMri.class);
                        startActivity(intent);
                    }
                }
        );

        ImageButton biopsyButton = (ImageButton)findViewById(R.id.biopsyButton);
        biopsyButton.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        Intent intent = new Intent(InputData.this, InputBiopsy.class);
                        startActivity(intent);

                    }
                }
        );
    }
}
