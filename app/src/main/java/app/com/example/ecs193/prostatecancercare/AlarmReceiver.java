package app.com.example.ecs193.prostatecancercare;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import static android.app.PendingIntent.FLAG_CANCEL_CURRENT;
import static android.app.PendingIntent.getActivity;

/**
 * Created by liray on 3/5/2016.
 */
public class AlarmReceiver extends BroadcastReceiver {

    private static final int NOTIFICATION_ID = 9596;

    @Override
    public void onReceive(Context context, Intent intent) {
        //some condition
        Intent homeIntent = new Intent(context, EditAppointmentsActivity.class);
        PendingIntent pendingIntent = getActivity(context, 0, homeIntent, FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_add_black_24dp)
                .setContentTitle("Appointment Reminder")
                .setContentText("You have a " + intent.getStringExtra("type") + " test on "
                        + (intent.getIntExtra("month", 1) + 1) + "/" + intent.getIntExtra("day", 1) + "/" + intent.getIntExtra("year", 1) + ".")
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
