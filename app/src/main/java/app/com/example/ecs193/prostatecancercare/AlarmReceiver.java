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

    @Override
    public void onReceive(Context context, Intent intent) {
        //some condition
        Intent homeIntent = new Intent(context, EditAppointmentsActivity.class);
        PendingIntent pendingIntent = getActivity(context, 0, homeIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_add_white_48dp)
                .setContentTitle("Appointment Reminder")
                .setContentText("You have a " + intent.getStringExtra("type") + " test on "
                        + (intent.getIntExtra("month", 1) + 1) + "/" + intent.getIntExtra("day", 1) + "/" + intent.getIntExtra("year", 1) + ".")
                .setAutoCancel(true);

        System.out.println(Integer.parseInt("" + getTypeId(intent.getStringExtra("type")) + intent.getIntExtra("year", 1) + intent.getIntExtra("month", 1) + intent.getIntExtra("day", 1)));
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(Integer.parseInt("" + getTypeId(intent.getStringExtra("type")) + intent.getIntExtra("year", 1) + intent.getIntExtra("month", 1) + intent.getIntExtra("day", 1)), builder.build());
    }

    private int getTypeId(String s) {
        if (s.equals("PSA")) return 1;
        else if (s.equals("Biopsy")) return 2;
        else if (s.equals("Genomics")) return 3;
        else if(s.equals("MRI")) return 4;

        return -1;
    }
}
