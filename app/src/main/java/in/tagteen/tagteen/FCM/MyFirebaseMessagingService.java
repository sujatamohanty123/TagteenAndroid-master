package in.tagteen.tagteen.FCM;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.tagteen.tagteen.CommentLikeActivity_new;
import in.tagteen.tagteen.MainDashboardActivity;
import in.tagteen.tagteen.NotificationUtils;
import in.tagteen.tagteen.chatting.notification.NotificationManager;
import in.tagteen.tagteen.util.Constants;

//tony00
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private static final int FLAG_COMMENT = 0;
    private static final int FLAG_REACTS = 1;

    private static final int ACTION_TYPE_MESSAGE = 4;
    private static final int ACTION_TYPE_TEST = 200;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "onMessageReceived: " + remoteMessage);

        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "NotificationsData Body: " + remoteMessage.getNotification().getBody());
            this.handleNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData() != null && remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());

                JSONObject data = json.getJSONObject("data");
                int actionType = data.getInt("action_type");

                if (actionType == ACTION_TYPE_TEST) {
                    handleMessageNotification(this, json);
                } else if (actionType == ACTION_TYPE_MESSAGE) {
                    handleMessageNotification(this, json);
                } else {
                    handleDataMessage(this, json);
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void handleMessageNotification(Context context, @NonNull JSONObject json) {
        try {
            JSONObject data = json.getJSONObject("data");
            JSONArray message = data.getJSONArray("chatInfo");

            if (message != null) {
//                Intent parentIntent = new Intent(this, MainDashboardActivity.class);
//                parentIntent.putExtra("fragmentload", "");
                NotificationManager.push(context, message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            System.out.println("message=" + message);
        } else {
            // If the app is in background, firebase itself handles the notification
        }
        Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
        pushNotification.putExtra("message", message);
//        LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
        // play notification sound
//        NotificationUtils notificationUtils = NotificationUtils.from(getApplicationContext());
        // notificationUtils.getBitmapFromURL()
//        notificationUtils.playNotificationSound();
    }

    private void handleDataMessage(Context context, JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());

        try {
            String post_id = "";
            JSONObject data1 = json.getJSONObject("data");
            String actionUserId = data1.optString("action_creator_user_id");
            Integer action_type = data1.getInt("action_type");
            if (action_type == 7 || action_type == 8 || action_type == 9) {
                post_id = "";
            } else {
                post_id = data1.getString("post_id");
            }
            JSONObject data = json.getJSONObject("notification");
            String title = data.getString("title");
            String body = data.getString("body");/*
            String message = data.getString("message");
            boolean isBackground = data.getBoolean("is_background");
            String imageUrl = data.getString("image");
            String timestamp = data.getString("timestamp");
            JSONObject payload = data.getJSONObject("payload");*/

            Log.e(TAG, "title: " + title);
           /* Log.e(TAG, "message: " + message);
            System.out.println("message="+message);
            Log.e(TAG, "isBackground: " + isBackground);
            Log.e(TAG, "payload: " + payload.toString());
            Log.e(TAG, "imageUrl: " + imageUrl);
            Log.e(TAG, "timestamp: " + timestamp);*/

            Intent intent = new Intent();
            switch (action_type) {
                case 11:
                    intent = this.getCommentLikeIntent(post_id, FLAG_REACTS, Constants.POST_TYPE_MOMENTS, Constants.REACT_COOL);
                    break;
                case 12:
                    intent = this.getCommentLikeIntent(post_id, FLAG_REACTS, Constants.POST_TYPE_MOMENTS, Constants.REACT_SWAG);
                    break;
                case 13:
                    intent = this.getCommentLikeIntent(post_id, FLAG_REACTS, Constants.POST_TYPE_MOMENTS, Constants.REACT_NERD);
                    break;
                case 14:
                    intent = this.getCommentLikeIntent(post_id, FLAG_REACTS, Constants.POST_TYPE_MOMENTS, Constants.REACT_DAB);
                    break;
                case 15:
                    intent = this.getCommentLikeIntent(post_id, FLAG_REACTS, Constants.POST_TYPE_MOMENTS, Constants.REACT_LIKE);
                    break;
                case 16:
                    intent = this.getCommentLikeIntent(post_id, FLAG_COMMENT, Constants.POST_TYPE_MOMENTS, "");
                    break;
                case 25:
                    intent = this.getCommentLikeIntent(post_id, FLAG_REACTS, Constants.POST_TYPE_TEENFEED, "");
                    break;
                case 26:
                    intent = this.getCommentLikeIntent(post_id, FLAG_COMMENT, Constants.POST_TYPE_TEENFEED, "");
                    break;
                case 35:
                    intent = this.getCommentLikeIntent(post_id, FLAG_REACTS, Constants.POST_TYPE_SHOWROOM, "");
                    break;
                case 36:
                    intent = this.getCommentLikeIntent(post_id, FLAG_COMMENT, Constants.POST_TYPE_SHOWROOM, "");
                    break;
                case 7:
                    intent = this.getMainDashboardIntent(Constants.OTHER_PROFILE, actionUserId);
                    break;
            }

            intent.putExtra("message", title);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            NotificationUtils.NotificationData notification =
                    new NotificationUtils.NotificationData(title, body);
            notification.setIntent(intent);

//            NotificationManager.getInstance().push(new JSONArray(msg));
            NotificationUtils.front(context, notification,
                    NotificationUtils.NOTIFICATION_SMALL);
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    private Intent getCommentLikeIntent(String post_id, int flag, String postType, String reactType) {
        Bundle bundle = new Bundle();
        Intent intent = new Intent(this, CommentLikeActivity_new.class);
        bundle.putInt("comment_select_flag", flag);
        bundle.putInt("keypadshow", 0);
        bundle.putString("postid", post_id);
        bundle.putString("type", postType);
        bundle.putString("react_type", reactType);
        bundle.putInt(Constants.COMMENTS_COUNT, 0);
        bundle.putInt(Constants.REACTS_COUNT, 0);
        intent.putExtras(bundle);
        return intent;
    }

    private Intent getMainDashboardIntent(String fragmentload, String otherUserId) {
        Bundle bundle = new Bundle();
        Intent intent = new Intent(this, MainDashboardActivity.class);
        bundle.putString("fragmentload", fragmentload);
        bundle.putString(Constants.USER_ID, otherUserId);
        intent.putExtras(bundle);
        return intent;
    }

}
