package in.tagteen.tagteen.chatting.room;

import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

import in.tagteen.tagteen.chatting.utils.ChatSessionManager;

/**
 * Created by tony00 on 7/13/2019.
 */
public class DBQueryHandler {

    public static final String TAG = DBQueryHandler.class.getCanonicalName()+".TAG";

    @WorkerThread
    public static List<Integer> getTotalMessagesIds(@NonNull String clientId,
                                                    int offset) {
        Log.d(TAG, "Retrieving Total Message Ids...");
        MessageDao dao = MessageDatabase.getInstance().getMessageDao();
        String ownerId = ChatSessionManager.getInstance().getSenderId();
        return dao.getTotalMessageIds(ownerId, clientId, offset);
    }

    @WorkerThread
    public static List<Message> getMessages(@NonNull String clientId,
                                            int startId,
                                            int endId) {
        Log.d(TAG, "Retrieving Messages...");
        MessageDao dao = MessageDatabase.getInstance().getMessageDao();
        String ownerId = ChatSessionManager.getInstance().getSenderId();
        return dao.getMessages(ownerId, clientId, startId, endId);
    }

    @WorkerThread
    public static LiveData<Message> getMessagesObserver() {
        MessageDao dao = MessageDatabase.getInstance().getMessageDao();
        String ownerId = ChatSessionManager.getInstance().getSenderId();
        return dao.getMessage(ownerId);
    }

    @WorkerThread
    public static Message getLastMessage(@NonNull String clientId) {
        Log.d(TAG, "Retrieving Last Message...");
        MessageDao dao = MessageDatabase.getInstance().getMessageDao();
        String ownerId = ChatSessionManager.getInstance().getSenderId();
        return dao.getMessageByReceiverId(ownerId, clientId);
    }

    @WorkerThread
    public static void addMessage(@NonNull Message message) {
        Log.d(TAG, "Saving new Message : "+message.toString());
        MessageDao dao = MessageDatabase.getInstance().getMessageDao();
        dao.addMessage(message);
    }

    @WorkerThread
    public static void updateMessage(@NonNull String serverMessageId,
                                     long serverDate,
                                     @Message.Status int status,
                                     @NonNull String messageId,
                                     @NonNull String clientId) {
        Log.d(TAG, "Updating Message...");
        MessageDao dao = MessageDatabase.getInstance().getMessageDao();
        String ownerId = ChatSessionManager.getInstance().getSenderId();
        dao.updateMessage(serverMessageId,
                serverDate,
                status,
                messageId,
                ownerId,
                clientId);
    }

    @WorkerThread
    public static void updateMessageStatus(@Message.Status int status,
                                           @NonNull String clientId,
                                           @NonNull String... serverMessageIds) {
        Log.d(TAG, "Updating Message Status : " + status);
        MessageDao dao = MessageDatabase.getInstance().getMessageDao();
        String ownerId = ChatSessionManager.getInstance().getSenderId();
        dao.updateMessageStatus(status,
                ownerId,
                clientId,
                serverMessageIds);
    }

    @WorkerThread
    public static List<String> getUnseenMessageIds(@NonNull String clientId) {
        Log.d(TAG, "Retrieving Unseen Message ids...");
        MessageDao dao = MessageDatabase.getInstance().getMessageDao();
        String ownerId = ChatSessionManager.getInstance().getSenderId();
        return dao.getUnseenMessageIds(ownerId, clientId, Message.MESSAGE_IN, Message.DELIVERED);
    }

    @WorkerThread
    public static int getUnseenMessageCount(@NonNull String clientId) {
        Log.d(TAG, "Counting Unseen Messages...");
        MessageDao dao = MessageDatabase.getInstance().getMessageDao();
        String ownerId = ChatSessionManager.getInstance().getSenderId();
        return dao.getUnseenInMessageCount(ownerId, clientId, Message.MESSAGE_IN, Message.SEEN);
    }

    @WorkerThread
    public static boolean isMessagePresent(@NonNull Message message) {
        Log.d(TAG, "Checking Message Present...");
        MessageDao dao = MessageDatabase.getInstance().getMessageDao();
        String ownerId = ChatSessionManager.getInstance().getSenderId();
        String messageId = dao.getMessageId(message.getMsgId(), ownerId, message.getClientId());
        return !TextUtils.isEmpty(messageId);
    }
}
