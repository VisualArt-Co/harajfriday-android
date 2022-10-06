package com.benAbdelWahed.utils;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.Html;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.benAbdelWahed.R;
import com.benAbdelWahed.activities.ChatRoomActivity;
import com.benAbdelWahed.activities.HarajDetailsActivity;
import com.benAbdelWahed.activities.MainActivity;
import com.benAbdelWahed.activities.MazadDetailsActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Objects;

import static com.benAbdelWahed.utils.StaticMembers.OTHER_ID;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    static int i = 0;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String title, message;
        title = Objects.requireNonNull(remoteMessage.getData()).get("title");
        message = remoteMessage.getData().get("message");
        if (title != null && message != null)
            showNotification(title, message, remoteMessage.getData());
    }

    int id = 0;
    void showNotification(String title, String message, Map<String, String> data) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, MainActivity.class);
        Intent backIntent = new Intent(this, MainActivity.class);
        backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        String uid = data.get("userId");
        if (uid != null) {
            intent = new Intent(this, ChatRoomActivity.class);
            id = Integer.parseInt(uid);
            intent.putExtra(OTHER_ID, id);
        }
        else{
            String notificationType= data.get("notificationType");
            if ("mazad".equals(notificationType)) {
                intent = new Intent(this, MazadDetailsActivity.class);
                intent.putExtra(StaticMembers.MAZAD_ID, data.get("mazad_id"));
            } else if ("haraj".equals(notificationType)) {
                intent = new Intent(this, HarajDetailsActivity.class);
                intent.putExtra(StaticMembers.HARAJ_ID, data.get("product_id"));
            }
        }
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0,  new Intent[] {intent}, PendingIntent.FLAG_ONE_SHOT);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant")
            NotificationChannel notificationChannel = new NotificationChannel(StaticMembers.HARAJ, getString(R.string.app_name), NotificationManager.IMPORTANCE_MAX);
            notificationChannel.setDescription("description");
            notificationChannel.setName("Channel Name");
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, getString(R.string.app_name))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(Html.fromHtml(title))
                .setContentText(Html.fromHtml(message))
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSound(soundUri)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true).setChannelId(StaticMembers.HARAJ);

        notificationBuilder.setLights(0xff00ff00, 2000, 2000);
        notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(getBaseContext().getResources(), R.mipmap.ic_launcher));

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
            notificationBuilder.setColor(getResources().getColor(R.color.colorAccent));
        } else {
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        }
        if (notificationManager != null) {
            notificationManager.notify(id, notificationBuilder.build());
        }
    }
/*
    private void sendMyNotification(String title, String message, Notification notification) {
        Intent i = new Intent(StaticMembers.ZARI_PACKAGE);
        i.putExtra(StaticMembers.NOTIFICATION_ID, NOTIFICATION_CODE);
        sendBroadcast(i);
        if (notification != null) {
            if (notification.image() != null && !notification.image().isEmpty()) {
                FutureTarget futureTarget = Glide.with(getBaseContext()).asBitmap().apply(RequestOptions.circleCropTransform().priority(Priority.HIGH)).load(notification.image()).submit();
                Bitmap bitmap = null;
                try {
                    bitmap = (Bitmap) futureTarget.get();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    showNotification(title, message, bitmap, notification);
                    Glide.with(this).clear(futureTarget);
                }
            } else showNotification(title, message, null, notification);
        } else showNotification(title, message, null, null);
    }

    */
}
