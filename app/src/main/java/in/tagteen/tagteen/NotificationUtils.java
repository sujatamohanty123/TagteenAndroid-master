package in.tagteen.tagteen;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import in.tagteen.tagteen.chatting.model.ReceivedMessage;

//tony00
public class NotificationUtils {

    private static String TAG = NotificationUtils.class.getSimpleName();
    private static final int NOTIFICATION_ID = 63824552;

    public static String CHANNEL_ID = "24363636";
    public static String CHANNEL_NAME = "in.tagteen.tagteen.NotificationUtils.ChannelName";
    public static String CHANNEL_DESC = "in.tagteen.tagteen.NotificationUtils.ChannelDescription";
    public static String NOTIFICATION_GROUP_MESSAGE = "in.tagteen.tagteen.NotificationUtils.NotificationGroupMessage";

    public static final int NOTIFICATION_BIG = 0x1b0;
    public static final int NOTIFICATION_SMALL = 0x1b1;

    @IntDef({NOTIFICATION_BIG, NOTIFICATION_SMALL})
    public @interface NotificationType {
    }

    public static int front(@NonNull Context context,
                            @NonNull NotificationData data,
                            @NotificationType int notificationType) {
        if (TextUtils.isEmpty(data.getMessage()))
            return -1;

        if (notificationType == NOTIFICATION_BIG) {
            if (data.getImage() != null &&
                    data.getImage().length() > 4 &&
                    Patterns.WEB_URL.matcher(data.getImage()).matches()) {
                Bitmap bitmap = getBitmapFromURL(data.getImage());

                NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
                bigPictureStyle.setBigContentTitle(data.getTitle());
                bigPictureStyle.setSummaryText(Html.fromHtml(data.getMessage()).toString());
                bigPictureStyle.bigPicture(bitmap);

                return showNotification(context, data, bigPictureStyle);
            }
        } else if (notificationType == NOTIFICATION_SMALL) {
            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.addLine(data.getMessage());

            return showNotification(context, data, inboxStyle);
        }

        return -1;
    }

    private static int showNotification(Context context,
                                        NotificationData data,
                                        NotificationCompat.Style style) {
        PendingIntent pendingIntent = null;

        if (data.getIntent() != null)
            pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    data.intent,
                    /* PendingIntent.FLAG_CANCEL_CURRENT*/
                    0
            );

        Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + context.getPackageName() + "/raw/notification");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.logo_small)
//                .setTicker(data.getTitle())
                .setAutoCancel(true)
                .setContentTitle(data.getTitle())
                .setSound(alarmSound)
                .setStyle(style)
                .setWhen(new Date().getTime())
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.logo_small))
                .setContentText(data.getMessage())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setGroup(NOTIFICATION_GROUP_MESSAGE)
                .setGroupSummary(true)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE);

        if (pendingIntent != null)
            builder.setContentIntent(pendingIntent);

        Log.d(TAG, "showNotification: ");

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
        return NOTIFICATION_ID;
    }

    /**
     * Downloading push notification image before displaying it in
     * the notification tray
     */
    private static Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Playing notification sound
    public static void playNotificationSound(@NonNull Context context) {
        try {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + context.getPackageName() + "/raw/notification");
            Ringtone r = RingtoneManager.getRingtone(context, alarmSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method checks if the app is in background or not
     */
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    // Clears notification tray messages
    private void clearNotifications(Context context) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancelAll();
    }

    private long getTimeMilliSec(String timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(timeStamp);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date().getTime();
    }

    public static class NotificationData {
        private String title;
        private String message;
        private String image;
        private long time;
        private Intent intent;
        private Object extraData;

        public NotificationData(String title, String message) {
            this.title = title;
            this.message = message;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public Intent getIntent() {
            return intent;
        }

        public void setIntent(Intent intent) {
            this.intent = intent;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public void setExtraData(Object extraData) {
            this.extraData = extraData;
        }

        public Object getExtraData() {
            return extraData;
        }

        public static NotificationData fromMessage(@NonNull ReceivedMessage receivedMessage,
                                                   @NonNull Intent intent) {
            NotificationData notification =
                    new NotificationData(receivedMessage.getCreator().getName(), receivedMessage.getMessage());
            notification.setTime(receivedMessage.getServerDate());
            notification.setImage(receivedMessage.getCreator().getProfileImage());
            notification.setIntent(intent);
            notification.setExtraData(receivedMessage);
            return notification;
        }
    }
}
