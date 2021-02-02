package in.tagteen.tagteen.chatting.notification;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import in.tagteen.tagteen.chatting.model.MessageDeliveryStatus;
import in.tagteen.tagteen.chatting.model.ReceivedMessage;
import in.tagteen.tagteen.chatting.room.DBQueryExecutor;
import in.tagteen.tagteen.chatting.room.Message;
import in.tagteen.tagteen.chatting.room.tasks.DBTaskFactory;
import in.tagteen.tagteen.chatting.room.tasks.NewMessageFactory;
import in.tagteen.tagteen.chatting.socket.SocketConstants;
import in.tagteen.tagteen.chatting.utils.ChatSessionManager;
import in.tagteen.tagteen.chatting.volley.HttpCommunicator;
import in.tagteen.tagteen.chatting.volley.RequestConfiguration;
import in.tagteen.tagteen.chatting.volley.RequestResponse;

/**
 * Created by tony00 on 4/27/2019.
 */
public class NotificationService extends JobIntentService {

    private static final String TAG = NotificationService.class.getCanonicalName() + ".TAG";
    private static final int REQUEST_EMIT_DELIVERY = 0x2fd;

    public static final String EXTRA_DATA_MESSAGE = NotificationService.class.getCanonicalName() + ".Message";

    static final int JOB_ID = 1000;

    private static List<ReceivedMessage> messageList;

    /**
     * Convenience method for enqueuing work in to this service.
     */
    static void enqueueWork(@NonNull Context context, Intent work) {
        Log.d(TAG, "enqueWork: ");

        if (messageList == null)
            messageList = new ArrayList<>();

        ReceivedMessage message = (ReceivedMessage) work.getSerializableExtra(EXTRA_DATA_MESSAGE);
        messageList.add(message);
        enqueueWork(context, NotificationService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        ReceivedMessage message = (ReceivedMessage) intent.getSerializableExtra(EXTRA_DATA_MESSAGE);
        Log.i(TAG, "onHandleWork: " + message.toString());
        saveMessage(message);

        if (messageList.get(messageList.size() - 1).getServerChatId().equals(message.getServerChatId())) {
            Log.d(TAG, "onHandleWork Updating Delivery Status: ");
            emitDeliveryStatus();
        }
    }

    private void saveMessage(ReceivedMessage message) {
        if (message == null) return;

        Message msg = ReceivedMessage.toMessage(message);
        DBQueryExecutor.executeTask(DBTaskFactory.getRunnableTask(new NewMessageFactory(msg)));
    }

    private void emitDeliveryStatus() {
        MessageDeliveryStatus deliveryStatus = new MessageDeliveryStatus();
        List<MessageDeliveryStatus.MessageDelivery> messages = new ArrayList<>();

        for(ReceivedMessage message: messageList) {
            MessageDeliveryStatus.MessageDelivery delivery = new MessageDeliveryStatus.MessageDelivery();
            delivery.setSenderId(message.getCreator().getId());
            delivery.setReceiverId(message.getReceiverId());
            delivery.setServerChatId(message.getServerChatId());
            messages.add(delivery);
        }
        deliveryStatus.setMessages(messages);

        RequestConfiguration config = new RequestConfiguration(
                SocketConstants.CHAT_DELIVERY,
                REQUEST_EMIT_DELIVERY,
                deliveryStatus,
                null);

        HttpCommunicator comm = new HttpCommunicator(this::handleResponseData);
        comm.addRequest(config, ChatSessionManager.getInstance().getToken());
    }

    private void handleResponseData(@NonNull RequestResponse response) {
        Log.d(TAG, "handleResponseData: " + response.toString());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "NotificationService Destroyed ");
        messageList = null;
    }
}
