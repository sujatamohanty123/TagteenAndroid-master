package in.tagteen.tagteen.chatting.notification;

import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import in.tagteen.tagteen.chatting.R;
import in.tagteen.tagteen.chatting.model.ReceivedMessage;
import in.tagteen.tagteen.chatting.socket.SocketConstants;

/**
 * Created by tony00 on 7/7/2019.
 */
public class NotificationCreator extends AsyncTask<NotificationCreator.NotificationMessageData, Void, Void> {

    private static final String TAG = NotificationCreator.class.getCanonicalName() + ".TAG";

    private static final int NOTIFICATION_MESSAGE_ID = 0x1942;
    private static final String CHANNEL_ID = "24363636";
    private static final String NOTIFICATION_GROUP_MESSAGE = "in.tagteen.tagteen.chatting.NotificationCreator.NotificationGroupMessage";

    private WeakReference<Context> context;

    private NotificationCreator(@NonNull Context context) {
        this.context = new WeakReference<>(context);
    }

    public static NotificationCreator newInstance(@NonNull Context context) {
        return new NotificationCreator(context);
    }

    public void create(@NonNull NotificationMessageData data) {
        execute(data);
    }

    @Override
    protected Void doInBackground(NotificationMessageData... data) {
        build(data[0]);
        return null;
    }

    @WorkerThread
    private void build(NotificationMessageData data) {
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.addLine(data.getMessage());

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context.get(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_chat_notifi)
                .setAutoCancel(true)
                .setContentTitle(data.getTitle())
                .setStyle(inboxStyle)
                .setWhen(data.getTime())
//                .setLargeIcon(getBitmapFromURL(data.getImage()))
                .setContentText(data.getMessage())
                .setGroup(NOTIFICATION_GROUP_MESSAGE)
                .setGroupSummary(true)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setContentIntent(getPendingIntent(data))
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        if (!data.isLocalNotification) {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + context.get().getPackageName() + "/raw/notification");
            builder.setSound(alarmSound);
        } else {
            builder.setSound(null);
        }

        Log.d(TAG, "showNotification: ");

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context.get());
        notificationManager.notify(NOTIFICATION_MESSAGE_ID, builder.build());
    }

    @WorkerThread
    private PendingIntent getPendingIntent(NotificationMessageData data) {
        PendingIntent pendingIntent = null;

        if (data.isLocalNotification) {
            pendingIntent = PendingIntent.getActivity(
                    context.get(),
                    NOTIFICATION_MESSAGE_ID,
                    data.getIntent(),
                    PendingIntent.FLAG_UPDATE_CURRENT
            );
        } else {
            pendingIntent = TaskStackBuilder.create(context.get())
                    .addNextIntentWithParentStack(data.getIntent())
                    .getPendingIntent(NOTIFICATION_MESSAGE_ID, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        return pendingIntent;
    }

    @WorkerThread
    private Bitmap getBitmapFromURL(String strURL) {
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

    public static class NotificationMessageData {
        private String title;
        private String message;
        private String image;
        private long time;
        private Intent intent;
        private boolean isLocalNotification;

        public NotificationMessageData(String title) {
            this.title = title;
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

        public void setLocalNotification(boolean localNotification) {
            isLocalNotification = localNotification;
        }

        public boolean isLocalNotification() {
            return isLocalNotification;
        }

        public static NotificationMessageData fromMessage(@NonNull ReceivedMessage receivedMessage,
                                                          @NonNull Intent intent) {
            NotificationMessageData notification =
                    new NotificationMessageData(receivedMessage.getCreator().getName());
            notification.setMessage(receivedMessage.getChatType() == SocketConstants.MessageType.TEXT
                    ? receivedMessage.getMessage() : "Sound Emoji");
            notification.setTime(receivedMessage.getServerDate()* 1000);
            notification.setImage(receivedMessage.getCreator().getProfileImage());
            notification.setIntent(intent);
            return notification;
        }
    }
}
