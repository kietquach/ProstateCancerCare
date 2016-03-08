package app.com.example.ecs193.prostatecancercare;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.ui.FirebaseRecyclerAdapter;

/**
 * Created by liray on 3/5/2016.
 */
public class EditAppointmentsActivity extends AppCompatActivity {
    private Firebase appointmentsRef;
    private RecyclerView mRecyclerView;
    private TextView mTextView;

    private final Firebase fbRef = new Firebase("https://boiling-heat-3817.firebaseio.com/");

    public static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        ImageButton deleteButton;
        TextView dateTextView;
        TextView typeTextView;

        public AppointmentViewHolder(View v) {
            super(v);
            deleteButton = (ImageButton) v.findViewById(R.id.imageButton1);
            dateTextView = (TextView) v.findViewById(R.id.dateTextView);
            typeTextView = (TextView) v.findViewById(R.id.typeTextView);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_appointments);
        Firebase.setAndroidContext(this);
        AuthData authData = fbRef.getAuth();
        appointmentsRef = fbRef.child("users").child(authData.getUid()).child("Appointments");
        appointmentsRef.orderByChild("date");

        mTextView = (TextView) findViewById(R.id.numAppointmentsText);
        appointmentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() == 0) {
                    mTextView.setText("You do not have any scheduled appointment.");
                } else {
                    mTextView.setText("You have " + dataSnapshot.getChildrenCount() + " scheduled appointments.");
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Appointment, AppointmentViewHolder> adapter =
                new FirebaseRecyclerAdapter<Appointment, AppointmentViewHolder>(
                        Appointment.class,
                        R.layout.appointment_entry,
                        AppointmentViewHolder.class,
                        appointmentsRef.orderByChild("date")
                ) {
            @Override
            protected void populateViewHolder(AppointmentViewHolder appointmentViewHolder, Appointment appointment, int i) {
                String dateString = appointment.getDate();
                String newDateString = dateString.substring(4,6) + "/" + dateString.substring(6,8) + "/" + dateString.substring(0,4);
                appointmentViewHolder.dateTextView.setText(newDateString);
                appointmentViewHolder.typeTextView.setText(appointment.getType());
            }
        };

        mRecyclerView.setAdapter(adapter);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_appointment, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add_appointment) {
            Intent intent = new Intent(EditAppointmentsActivity.this, AddAppointmentActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
