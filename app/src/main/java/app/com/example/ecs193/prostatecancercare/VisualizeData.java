package app.com.example.ecs193.prostatecancercare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.firebase.client.Firebase;


public class VisualizeData extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualize_data);

        Button psaButton = (Button) findViewById(R.id.psaButton);

        psaButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(VisualizeData.this, PsaGraph.class);
                        startActivity(intent);

                    }
                }
        );
    }
}
