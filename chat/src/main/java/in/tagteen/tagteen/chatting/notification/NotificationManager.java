package in.tagteen.tagteen.chatting.notification;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import in.tagteen.tagteen.chatting.ActivityChatDetails;
import in.tagteen.tagteen.chatting.model.Friend;
import in.tagteen.tagteen.chatting.model.MessageDeliveryStatus;
import in.tagteen.tagteen.chatting.model.ReceivedMessage;
import in.tagteen.tagteen.chatting.room.DBQueryExecutor;
import in.tagteen.tagteen.chatting.room.Message;
import in.tagteen.tagteen.chatting.room.tasks.DBTaskFactory;
import in.tagteen.tagteen.chatting.room.tasks.NewMessageFactory;
import in.tagteen.tagteen.chatting.socket.NetworkFactory;
import in.tagteen.tagteen.chatting.socket.SocketConstants;
import in.tagteen.tagteen.chatting.utils.ChatSessionManager;
import in.tagteen.tagteen.chatting.volley.HttpCommunicator;
import in.tagteen.tagteen.chatting.volley.RequestConfiguration;

import static in.tagteen.tagteen.chatting.ActivityChatDetails.KEY_NEW_MESSAGES;

/**
 * Created by tony00 on 4/15/2019.
 */
public class NotificationManager {

    private static final int REQUEST_EMIT_DELIVERY = 0x2fd;

    private static ReceivedMessage parseMessage(@NonNull JSONArray data) {
        Type listType = new TypeToken<List<ReceivedMessage>>() {
        }.getType();

        List<ReceivedMessage> received = NetworkFactory
                .deserializeJson(data, listType);
        return received.get(0);
    }

    @WorkerThread
    public static void push(@NonNull Context context,
                            @NonNull JSONArray data) {
        ReceivedMessage message = parseMessage(data);

//        Intent intent = new Intent(context, NotificationService.class);
//        intent.putExtra(NotificationService.EXTRA_DATA_MESSAGE, message);
//        NotificationService.enqueueWork(context, intent);

        saveMessage(message);
        showNotification(context, message, false);
        emitDeliveryStatus(message);
    }

    public static void pushLocal(@NonNull Context context,
                                 @NonNull ReceivedMessage message) {
        showNotification(context, message, true);
    }

    private static void showNotification(@NonNull Context context,
                                    @NonNull ReceivedMessage message,
                                    boolean isLocal){
        NotificationCreator.NotificationMessageData data =
                NotificationCreator.NotificationMessageData.fromMessage(message,
                getChatIntent(context, message.getCreator()));
        data.setLocalNotification(isLocal);

        NotificationCreator.newInstance(context).create(data);
    }

    private static Intent getChatIntent(@NonNull Context context,
                                        @NonNull Friend friend) {
        Intent intent = new Intent(context, ActivityChatDetails.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(ActivityChatDetails.KEY_FRIEND, friend);
        intent.putExtra(KEY_NEW_MESSAGES, true);
        return intent;
    }

    @WorkerThread
    private static void saveMessage(@NonNull ReceivedMessage message) {
        Message msg = ReceivedMessage.toMessage(message);
        DBQueryExecutor.executeTask(DBTaskFactory.getRunnableTask(new NewMessageFactory(msg)));
    }

    @WorkerThread
    private static void emitDeliveryStatus(@NonNull ReceivedMessage message) {
        MessageDeliveryStatus deliveryStatus = new MessageDeliveryStatus();

        List<MessageDeliveryStatus.MessageDelivery> messages = new ArrayList<>();
            MessageDeliveryStatus.MessageDelivery delivery = new MessageDeliveryStatus.MessageDelivery();
            delivery.setSenderId(message.getCreator().getId());
            delivery.setReceiverId(message.getReceiverId());
            delivery.setServerChatId(message.getServerChatId());
            messages.add(delivery);

        deliveryStatus.setMessages(messages);

        RequestConfiguration config = new RequestConfiguration(
                SocketConstants.CHAT_DELIVERY,
                REQUEST_EMIT_DELIVERY,
                deliveryStatus,
                null);

        HttpCommunicator comm = new HttpCommunicator();
        comm.addRequest(config, ChatSessionManager.getInstance().getToken());
    }
}
