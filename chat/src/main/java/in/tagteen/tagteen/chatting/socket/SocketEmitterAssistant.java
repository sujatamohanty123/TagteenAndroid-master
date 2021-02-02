package in.tagteen.tagteen.chatting.socket;

import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import in.tagteen.tagteen.chatting.model.DeliveryMessageStatus;
import in.tagteen.tagteen.chatting.model.SeenMessageStatus;
import in.tagteen.tagteen.chatting.model.MessageStatus;
import in.tagteen.tagteen.chatting.model.NewMessage;
import in.tagteen.tagteen.chatting.model.TypingStatus;
import in.tagteen.tagteen.chatting.model.UserStatus;
import in.tagteen.tagteen.chatting.room.DBQueryExecutor;
import in.tagteen.tagteen.chatting.room.tasks.DBTaskFactory;
import in.tagteen.tagteen.chatting.room.tasks.NewMessageFactory;
import in.tagteen.tagteen.chatting.utils.ChatSessionManager;
import io.socket.client.Socket;

/**
 * Created by tony00 on 3/30/2019.
 */
class SocketEmitterAssistant implements IEmitter {

    private static final String TAG = SocketEmitterAssistant.class.getSimpleName();
//private static final String TAG = "GLOBAL TAG SOCKET..";

    private Socket socket;
    private String receiverId;

    private SocketEmitterAssistant(@NonNull Socket socket) {
        this.socket = socket;
    }

    public static SocketEmitterAssistant of(@NonNull Socket socket) {
        return new SocketEmitterAssistant(socket);
    }

    public void setReceiverId(String receiverId) {
        Log.d(TAG, "setReceiverId: " + receiverId);
        this.receiverId = receiverId;
    }

    @Override
    public void bindCreator() {
        Log.d(TAG, "Binding user(" + ChatSessionManager.getInstance().getSenderId() + ") with socket...");

        if (!isSocketConnected()) return;

        if (TextUtils.isEmpty(ChatSessionManager.getInstance().getSenderId())) {
            Log.d(TAG, "No user to bind");
            return;
        }

        socket.emit(SocketConstants.Event.INIT_USER,
                ChatSessionManager.getInstance().getSenderId(),
                socket.id());
    }

    @Override
    public void emitMessage(@NonNull NewMessage newMessage) {
        Log.d(TAG, "Sending Message... ");

        if (!isSocketConnected()) return;

        for (String receiverId : newMessage.getReceiverId())
            DBQueryExecutor.executeTask(DBTaskFactory.getRunnableTask(
                    new NewMessageFactory(NewMessage.toMessage(newMessage, receiverId))));

        socket.emit(SocketConstants.Event.NEW_MESSAGE,
                NetworkFactory.serializeJson(newMessage));
    }

    @Override
    public void emitTypingStatus(boolean isTyping) {
        Log.d(TAG, "Sending Typing Status... " + isTyping);

        if (!isReceiverBound() || !isSocketConnected()) return;

        TypingStatus typing = new TypingStatus(isTyping);
        typing.setReceiverId(receiverId);
        socket.emit(typing.getEvent(),
                NetworkFactory.serializeJson(typing));
    }

    @Override
    public void emitMessageStatus(@MessageStatus.Status int value,
                                  @NonNull String senderId,
                                  @NonNull String... messageId) {
        Log.d(TAG, "Sending Message Status... - " + value + " " + messageId);

        if (!isSocketConnected()) return;

        if (value == MessageStatus.SEEN) {
            SeenMessageStatus status = new SeenMessageStatus();
            status.setSenderId(new String[]{senderId});
            status.setServerChatId(messageId);
            socket.emit(status.getEvent(), NetworkFactory.serializeJson(status));
        } else {
            DeliveryMessageStatus status = new DeliveryMessageStatus();
            status.setSenderId(new String[]{senderId});
            status.setServerChatId(messageId[0]);
            socket.emit(status.getEvent(), NetworkFactory.serializeJson(status));
        }
    }

    @Override
    public void emitUserStatus(@UserStatus.Status int value) {
        Log.d(TAG, "Sending User Status... " + value);

        if (!isSocketConnected()) return;

        UserStatus status = new UserStatus(value);
        status.setReceiverId(receiverId);
        socket.emit(status.getEvent(), NetworkFactory.serializeJson(status));
    }

    private boolean isSocketConnected() {
        if (socket.connected())
            return true;
        Log.i(TAG, "Socket connection failure...");
        return false;
    }

    private boolean isReceiverBound() {
        if (TextUtils.isEmpty(receiverId)) {
            Log.i(TAG, "No receiver ids found");
            return false;
        }
        return true;
    }
}
