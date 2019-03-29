package rayan.rayanapp.Services.firebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import rayan.rayanapp.Activities.GroupsActivity;
import rayan.rayanapp.Activities.MainActivity;
import rayan.rayanapp.R;

public class FbMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyAndroidFCMService";
    private static final String ACTIVITY_MAIN = "mainactivity";
    private static final String ACTIVITY_GROUP = "groupsactivity";
    private static final String DESTINATION_TYPE_URL = "url";
    private static final String DESTINATION_TYPE_ACTIVITY = "activity";
    private static final String DESTINATION_TYPE = "destination_type";
    private static final String DESTINATION = "destination";
    //    private static final String NOTIFICATION_TYPE = "type";
//    private static final String NOTIFICATION_TYPE_NORMAL = "normal";
//    private static final String NOTIFICATION_TYPE_BIG = "big";
    private static final String IMAGE_URL = "image";
    private boolean is_image = false;
Bitmap bitmap;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Message data payload: " + remoteMessage.getData());
            Map<String, String> data = remoteMessage.getData();
            if (data.get(IMAGE_URL)!=null){
                bitmap=getBitmapfromUrl(data.get(IMAGE_URL));
                is_image=true;
            }
            switch (data.get(DESTINATION_TYPE)) {
                case DESTINATION_TYPE_ACTIVITY:
                    switch (data.get(DESTINATION)) {
                        case ACTIVITY_GROUP:
                            Intent groupIntent = new Intent(this, GroupsActivity.class);
                            groupIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            // sendNotification(remoteMessage.getNotification().getBody(),groupIntent);
                            createNotification(this, remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(),bitmap, groupIntent);
                            break;
                        case ACTIVITY_MAIN:
                            Intent mainIntent = new Intent(this, MainActivity.class);
                            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            //  sendNotification(remoteMessage.getNotification().getBody(),mainIntent);
                            createNotification(this, remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(),bitmap, mainIntent);
                            break;
                    }
                    break;
                case DESTINATION_TYPE_URL:
                    String url = data.get(DESTINATION);
                    Intent showUrl = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    showUrl.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //     sendNotification(remoteMessage.getNotification().getBody(),Intent.createChooser(showUrl,"انتخاب مرورگر"));
                    createNotification(this, remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(),bitmap, Intent.createChooser(showUrl, "انتخاب مرورگر"));
                    break;
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
//            NotificationManager notificationManager =
//                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//            notificationManager.notify(0, mNotificationBuilder.build());

        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    @Override
    public void onNewToken(String token) {
        Log.e(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(token);
    }

    private NotificationManager notifManager;

    public void createNotification(Context context, String title, String body, Bitmap bitmap, Intent intent) {
        final int NOTIFY_ID = 0; // ID of notification
        String id = "channel-01";
        PendingIntent pendingIntent;
        NotificationCompat.Builder builder;
        if (notifManager == null) {
            notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notifManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, title, importance);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notifManager.createNotificationChannel(mChannel);
            }

                        builder = new NotificationCompat.Builder(context, id);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                        builder.setContentTitle(title)                            // required
                                .setSmallIcon(android.R.drawable.ic_popup_reminder)   // required
                                .setContentText(body) // required
                                .setDefaults(Notification.DEFAULT_ALL)
                                .setAutoCancel(true)
                                .setContentIntent(pendingIntent)
                                .setTicker(title)
                                .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

        } else {

                builder = new NotificationCompat.Builder(context, id);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                builder.setContentTitle(title)                            // required
                        .setSmallIcon(android.R.drawable.ic_popup_reminder)   // required
                        .setContentText(body) // required
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setTicker(title)
                        .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                        .setPriority(Notification.PRIORITY_HIGH);


        }
        Notification notification = builder.build();
        notifManager.notify(NOTIFY_ID, notification);
    }

    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
}