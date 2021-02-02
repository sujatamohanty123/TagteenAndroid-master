package in.tagteen.tagteen.chatting.socket;

import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ExecutionException;

import in.tagteen.tagteen.chatting.ActivityChat;
import in.tagteen.tagteen.chatting.model.DeliveryMessageStatus;
import in.tagteen.tagteen.chatting.model.MessageStatus;
import in.tagteen.tagteen.chatting.model.ReceivedMessage;
import in.tagteen.tagteen.chatting.model.SeenMessageStatus;
import in.tagteen.tagteen.chatting.model.SentMessage;
import in.tagteen.tagteen.chatting.model.TypingStatus;
import in.tagteen.tagteen.chatting.model.UserStatus;
import in.tagteen.tagteen.chatting.notification.NotificationManager;
import in.tagteen.tagteen.chatting.room.DBQueryExecutor;
import in.tagteen.tagteen.chatting.room.Message;
import in.tagteen.tagteen.chatting.room.tasks.DBTaskFactory;
import in.tagteen.tagteen.chatting.room.tasks.MessageStatusFactory;
import in.tagteen.tagteen.chatting.room.tasks.NewMessageFactory;
import in.tagteen.tagteen.chatting.room.tasks.UpdateMessageFactory;
import in.tagteen.tagteen.chatting.utils.ScreenTracker;

/**
 * Created by tony00 on 3/30/2019.
 */
public class SocketEventsAssistant implements IEvents {

    private static final String TAG = SocketEventsAssistant.class.getSimpleName();

    private String senderId;
    private SocketEventListener eventListener;

    private SocketEventsAssistant(
            @Nullable SocketEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public static SocketEventsAssistant of(
            @Nullable SocketEventListener eventListener) {
        return new SocketEventsAssistant(eventListener);
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
        Log.d(TAG, "setSenderId: " + senderId);
    }

    public void setEventListener(SocketEventListener eventListener) {
        this.eventListener = eventListener;
    }

    @Override
    public void onSocketConnected() {
        Log.d(TAG, "Socket Connected");
        if (eventListener != null) eventListener.onSocketConnectionChanged(true);
    }

    @Override
    public void onSocketDisconnected() {
        Log.d(TAG, "Socket Disconnected");
        if (eventListener != null) eventListener.onSocketConnectionChanged(false);
    }

    @Override
    public SentMessage onMessageSent(Object... args) {
        Log.i(TAG, "Message sent to server " + ((JSONObject) args[0]).toString());

        SentMessage message = NetworkFactory
                .deserializeJson((JSONObject) args[0], SentMessage.class);

        DBQueryExecutor.executeTask(DBTaskFactory.getRunnableTask(
                new UpdateMessageFactory(message.getClientChatId(),
                        message.getServerChatId(),
                        message.getReceiverId(),
                        message.getServerDate() * 1000)));

        runOnMainThread(() -> {
            if (eventListener != null && canInvokeCallback(message.getReceiverId())) {
                MessageStatus mStatus = new MessageStatus(MessageStatus.SENT);
                mStatus.setSenderId(new String[]{message.getSenderId()});
                mStatus.setReceiverId(message.getReceiverId());

                eventListener.onMessageStatusChanged(mStatus, message.getServerChatId());
            }
        });
        return message;
    }

    @Override
    public ReceivedMessage onNewMessage(Object... args) {
        Log.i(TAG, "New Message Received " + ((JSONArray) args[0]).toString());

        Type listType = new TypeToken<List<ReceivedMessage>>() {
        }.getType();

        List<ReceivedMessage> received = NetworkFactory
                .deserializeJson((JSONArray) args[0], listType);

        ReceivedMessage message = received.get(0);

        Message msg = ReceivedMessage.toMessage(message);
        if (isSenderBound() && message.getCreator().getId().equals(senderId)) {
            msg.setMessageStatus(Message.SEEN);
        }

        DBQueryExecutor.executeTask(DBTaskFactory.getRunnableTask(new NewMessageFactory(msg)));

        runOnMainThread(() -> {
            if (eventListener != null) {
                if (canInvokeCallback(msg.getClientId()))
                    eventListener.onNewMessageReceived(msg);
            }

            String currentScreen = ScreenTracker.getInstance().getCurrentScreen();
            if (currentScreen == null ||
                    !currentScreen.equals(ActivityChat.class.getSimpleName())) {
                if (!(isSenderBound() && message.getCreator().getId().equals(senderId)))
                    NotificationManager.pushLocal(
                            ChatProcessLifecycleManager.getApplicationContext(), message);
            }
        });
        return message;
    }

    @Override
    public void onTyping(Object... args) {
        Log.i(TAG, "Typing Received " + ((JSONObject) args[0]).toString());

        TypingStatus status = NetworkFactory.
                deserializeJson((JSONObject) args[0], TypingStatus.class);
        status.setTyping(true);

        runOnMainThread(() -> {
            if (eventListener != null && canInvokeCallback(status.getSenderId()))
                eventListener.onTypingStatusChanged(status);
        });
    }


    @Override
    public void onStopTyping(Object... args) {
        Log.i(TAG, "Stopped Typing " + ((JSONObject) args[0]).toString());

        TypingStatus status = NetworkFactory.
                deserializeJson((JSONObject) args[0], TypingStatus.class);
        status.setTyping(false);

        runOnMainThread(() -> {
            if (eventListener != null && canInvokeCallback(status.getSenderId()))
                eventListener.onTypingStatusChanged(status);
        });
    }

    @Override
    public void onUserLeft(Object... args) {
        Log.i(TAG, "User has been left " + ((JSONObject) args[0]).toString());

        UserStatus status = NetworkFactory
                .deserializeJson((JSONObject) args[0], UserStatus.class);
        status.setStatus(UserStatus.LEFT);

        runOnMainThread(() -> {
            if (eventListener != null)
                eventListener.onUserStatusChanged(status);
        });
    }

    @Override
    public void onUserDisconnected(Object... args) {
        Log.i(TAG, "User disconnected " + ((JSONObject) args[0]).toString());

        UserStatus status = NetworkFactory
                .deserializeJson((JSONObject) args[0], UserStatus.class);
        status.setStatus(UserStatus.DISCONNECTED);

        runOnMainThread(() -> {
            if (eventListener != null)
                eventListener.onUserStatusChanged(status);
        });
    }

    @Override
    public void onMessageSeen(Object... args) {
        Log.i(TAG, "Message seen " + ((JSONObject) args[0]).toString());

        SeenMessageStatus status = NetworkFactory
                .deserializeJson((JSONObject) args[0], SeenMessageStatus.class);

        String clientId = status.getReceiverId();

        try {
            String[] messageIds = DBQueryExecutor.submitTask(
                    DBTaskFactory.getRunnableTask(new MessageStatusFactory(clientId,
                            Message.SEEN,
                            status.getServerChatId())), status.getServerChatId());

            runOnMainThread(() -> {
                if (eventListener != null /*&& canInvokeCallback(clientId)*/)
                    eventListener.onMessageStatusChanged(status, messageIds);
            });
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageDelivered(Object... args) {
        Log.i(TAG, "Message Delivered " + ((JSONObject) args[0]).toString());

        DeliveryMessageStatus status = NetworkFactory
                .deserializeJson((JSONObject) args[0], DeliveryMessageStatus.class);

        String clientId = status.getReceiverId();

        try {
            String messageId = DBQueryExecutor.submitTask(DBTaskFactory.getRunnableTask(
                    new MessageStatusFactory(clientId,
                            Message.DELIVERED,
                            status.getServerChatId())), status.getServerChatId());

            runOnMainThread(() -> {
                if (eventListener != null /*&& canInvokeCallback(clientId)*/)
                    eventListener.onMessageStatusChanged(status, messageId);
            });
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onOnlineUsersUpdated(Object... args) {
        Log.i(TAG, "Online Friends " + ((JSONObject) args[0]).toString());
    }

    private boolean canInvokeCallback(String clientId) {
        if (isSenderBound()) {
            if (senderId.equals(clientId)) {
                return true;
            }
            return false;
        }
        return true;
    }

    private boolean isSenderBound() {
        if (TextUtils.isEmpty(senderId)) {
            Log.i(TAG, "No receiver ids found");
            return false;
        }
        return true;
    }
}
