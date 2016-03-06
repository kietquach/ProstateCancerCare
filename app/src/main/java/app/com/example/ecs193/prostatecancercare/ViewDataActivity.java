package app.com.example.ecs193.prostatecancercare;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class ViewDataActivity extends Activity {

    private Firebase fbRef;
    private Firebase childRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_data);

        fbRef = new Firebase("https://boiling-heat-3817.firebaseio.com/");

        AuthData authData = fbRef.getAuth();
        if (authData != null) {
            // user authenticated
            childRef = fbRef.child("users").child(authData.getUid());
            childRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    System.out.println("cancelled");
                }
            });

        } else {
            // no user authenticated
        }
    }
}
