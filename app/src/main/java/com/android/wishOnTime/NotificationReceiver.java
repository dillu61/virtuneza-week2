package com.android.wishOnTime;

import static androidx.core.content.ContextCompat.checkSelfPermission;
import static androidx.core.content.ContextCompat.getSystemService;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String s=intent.getStringExtra("name");
        String channelId = "my_channel_id";
        int notificationId = 1;
        Intent inte=new Intent(context,birthdayshow.class);
        inte.putExtra("name",s);
        Log.e("Notification set",s);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // Only needed for Android 8.0+
            // Unique ID for your channel
            CharSequence channelName = "My Channel";
            String channelDescription = "Channel for notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
        inte.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                inte,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        Bitmap large= BitmapFactory.decodeResource(context.getResources(), R.drawable.wishontime);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.notify)// Replace with your own app icon
                .setLargeIcon(large)
                .setContentTitle(s+"\'s Birthday\uD83C\uDF89")
                .setContentText("Let\'s wish.")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);  // Removes the notification when clicked
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // Android 13+ requires permission check before sending notifications
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(context,android.Manifest.permission.POST_NOTIFICATIONS) ==
                    android.content.pm.PackageManager.PERMISSION_GRANTED) {
                notificationManager.notify(notificationId, builder.build());
            }
        } else {
            notificationManager.notify(notificationId, builder.build());
        }
    }

}
