package in.tagteen.tagteen.database;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import in.tagteen.tagteen.Model.ChatMessage;

public class TagTeenEventBus {
    private static final String BROADCAST_EVENT_ACTION = "com.broadcast.event.PCHAT";

    public static final int TYPE_REFRESH_DATA = 1;
    public static final int TYPE_NEW_MESSAGE = 2;
    public static final int TYPE_TYPING = 3;
    public static final int TYPE_PRESENCE = 4;
    public static final int TYPE_REFRESH_DATA_SHOW = 5;
    public static final int TYPE_REFRESH_NEW_EVENT = 6;
    public static final int TYPE_REFRESH_EVENT_DELIVERED_STATUS = 7;
    public static final int TYPE_REFRESH_INFO = 8;

    public static final int TYPE_MESSAGE_SEEN = 9;
    public static final int TYPE_MESSAGE_SENT = 10;
    public static final int TYPE_USER_LEFT = 11;
    public static final int TYPE_USER_ADDED = 12;
    public static final int TYPE_STOP_TYPING = 13;
    public static final int TYPE_MESSAGE_DELIVERED = 14;

    private static final String DATA_OBJ = "data";
    public static final String EVENT_TYPE = "type";
    private static final String EVENT_NOTIFY = "notify";
    private static final String EVENT_DATA = "data";

    private static void sendEvent(Context context, ChatMessage chatMessage, String data, boolean isOrdered, int eType) {
        Intent intent = new Intent(BROADCAST_EVENT_ACTION);
        intent.putExtra(DATA_OBJ, chatMessage);
        intent.putExtra(EVENT_TYPE, eType);
//        intent.putExtra(EVENT_DATA, data);

        if (isOrdered) {
            intent.putExtra(EVENT_NOTIFY, true);
            context.sendOrderedBroadcast(intent, null);
        } else {
            context.sendBroadcast(intent, null);
        }
    }

    public static void sendEvent(Context context, ChatMessage chatMessage, int eType) {
        sendEvent(context, chatMessage, null, false, eType);
    }

    public static void sendEvent(Context context, String json, int eType) {
        sendEvent(context, null, json, false, eType);
    }

    public static void sendOrderedEvent(Context context, ChatMessage chatMessage, int eType) {
        sendEvent(context, chatMessage, null, true, eType);
    }

    public static ChatMessage getDataObj(Intent intent) {
        try {
            return (ChatMessage) intent.getSerializableExtra(DATA_OBJ);
        } catch (Exception e) {
            return null;
        }
    }

    public static int getEventType(Intent intent) {
        return intent.getIntExtra(EVENT_TYPE, 0);

    }

    public static String getEventData(Intent intent) {
        return intent.getStringExtra(EVENT_DATA);

    }

    public static boolean isOrdered(Intent intent) {
        return intent.getBooleanExtra(EVENT_NOTIFY, false);

    }

    public static void registerEventBus(Activity activity, BroadcastReceiver receiver) throws Exception {
        IntentFilter filter = new IntentFilter(BROADCAST_EVENT_ACTION);
        activity.registerReceiver(receiver, filter);

    }

    public static void removeEventBus(Activity activity, BroadcastReceiver receiver) {
        try {
            activity.unregisterReceiver(receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static class EventListener extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ChatMessage chatVo = getDataObj(intent);

            if (chatVo != null && intent.getBooleanExtra(EVENT_NOTIFY, false)) {
                //notify
//                if (!chatVo.isDelay()) {
//                    NotificationHandler.show(context, chatVo);
//                }
            }
        }
    }

    public static void fireRefreshEvent(Context context, String user) {
        Intent intent = new Intent(BROADCAST_EVENT_ACTION);
        intent.putExtra(EVENT_DATA, user);
        intent.putExtra(TagTeenEventBus.EVENT_TYPE, TagTeenEventBus.TYPE_REFRESH_INFO);
        context.sendBroadcast(intent, null);
    }
}