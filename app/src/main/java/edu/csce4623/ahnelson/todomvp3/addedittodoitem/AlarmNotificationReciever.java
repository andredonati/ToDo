package edu.csce4623.ahnelson.todomvp3.addedittodoitem;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import edu.csce4623.ahnelson.todomvp3.R;

import static android.content.Intent.getIntent;

public class AlarmNotificationReciever extends BroadcastReceiver {

    String ALARM_CHANNEL_ID = "alarm_channel";
    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("Title") + " is Due";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder alarm_builder = new NotificationCompat.Builder(context, ALARM_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setContentText(intent.getStringExtra("Content"))
                .setChannelId(ALARM_CHANNEL_ID);

        Intent resultIntent = new Intent(context, AddEditToDoItemActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(AddEditToDoItemActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        alarm_builder.setContentIntent(resultPendingIntent);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String channelId = ALARM_CHANNEL_ID;
            NotificationChannel channel = new NotificationChannel(channelId, "AlarmNotifications", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
            alarm_builder.setChannelId(channelId);
        }
        notificationManager.notify(0, alarm_builder.build());


    }
}
