package in.tagteen.tagteen.chatting.socket;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import android.util.Log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import in.tagteen.tagteen.chatting.model.MessageStatus;
import in.tagteen.tagteen.chatting.model.NewMessage;
import in.tagteen.tagteen.chatting.model.ReceivedMessage;
import in.tagteen.tagteen.chatting.model.UserStatus;
import in.tagteen.tagteen.chatting.room.DBQueryExecutor;
import in.tagteen.tagteen.chatting.room.Message;
import in.tagteen.tagteen.chatting.room.tasks.DBTaskFactory;
import in.tagteen.tagteen.chatting.room.tasks.MessageStatusFactory;
import in.tagteen.tagteen.chatting.room.tasks.MessagesIdsFactory;
import in.tagteen.tagteen.chatting.utils.ChatSessionManager;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Responsible for handling Socket Communication
 */
public class SocketConnection implements IEmitter {

    private static final String TAG = SocketConnection.class.getSimpleName();
//private static final String TAG = "GLOBAL TAG SOCKET..";

    public static final int ONLINE = 1;
    public static final int OFFLINE = 2;

    @IntDef({ONLINE, OFFLINE})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface MyStatus {
    }

    private static SocketConnection socketConn;
    private Socket socket;
    private ExecutorService executor;
    private SocketEmitterAssistant eventEmitter;
    private SocketEventsAssistant eventObserver;
    private SocketEventListener eventListener;
    private String receiverId;
    private int userStatus;

    private SocketConnection() {
        executor = Executors.newSingleThreadExecutor();
//        setMyStatus(OFFLINE);
    }

    public synchronized static SocketConnection getConnection() {
        if (socketConn == null) {
            Log.d(TAG, "getConnection: New Socket object created");
            socketConn = new SocketConnection();
        }
        return socketConn;
    }

    /**
     * Initializing socket connection. Connection will be alive until or unless
     * the application gets terminated or closed. Every activity or any other
     * android component will use the same socket instance.
     */
    public void connect() {
        Log.d(TAG, "Socket Connecting...");

        if (isSocketConnected()) {
            return;
        }

        try {
            socket = IO.socket(SocketConstants.HOST);
            socket.on(Socket.EVENT_CONNECT, onSocketConnected);
            socket.on(Socket.EVENT_DISCONNECT, onSocketDisconnected);
            socket.connect();

            eventEmitter = SocketEmitterAssistant.of(socket);
            eventObserver = SocketEventsAssistant.of(eventListener);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Log.e(TAG, "Exception arose while establishing socket connection" +
                    e.getMessage());
        }
    }

    public void disconnect() {
        Log.d(TAG, "Disconnecting Socket ... ");
        if (socket != null)
            socket.disconnect();
    }

    /**
     * Registering all the events with socket to listen incoming events.
     * Should be called as soon as the socket connection established
     */
    private void registerEventsToListenSocket() {
        if (socket.hasListeners(SocketConstants.Event.NEW_MESSAGE)) {
            Log.d(TAG, "Events are already registered with sockets... ");
            return;
        }

        if (!isSocketConnected()) return;

        Log.d(TAG, "Registering Socket Events... ");
        executor.execute(() -> {
            socket.on(SocketConstants.Event.NEW_MESSAGE, onNewMessage);
            socket.on(SocketConstants.Event.SERVER_SENDER, onServerSender);
            socket.on(SocketConstants.Event.TYPING, onTyping);
            socket.on(SocketConstants.Event.STOP_TYPING, onStopTyping);
            socket.on(SocketConstants.Event.DELIVERY_STATUS, onMessageDelivered);
            socket.on(SocketConstants.Event.SEEN_STATUS, onMessageSeen);
            socket.on(SocketConstants.Event.DISCONNECT, onUserDisconnect);
            socket.on(SocketConstants.Event.USER_LEFT, onUserLeft);
            socket.on(SocketConstants.Event.ONLINE_FRIENDS, onFriendsUpdated);
        });
    }

    /**
     * Un Registering all the events from socket.
     * Should be called before terminating or closing the application
     */
    private void unregisterEventsFromListeningSocket() {
//        isEventsRegistered = false;
        if (!isSocketConnected()) return;
        Log.d(TAG, "Un registering Socket Events... ");
        executor.submit(() -> {
            socket.off(SocketConstants.Event.NEW_MESSAGE, onNewMessage);
            socket.off(SocketConstants.Event.SERVER_SENDER, onServerSender);
            socket.off(SocketConstants.Event.TYPING, onTyping);
            socket.off(SocketConstants.Event.STOP_TYPING, onStopTyping);
            socket.off(SocketConstants.Event.DELIVERY_STATUS, onMessageDelivered);
            socket.off(SocketConstants.Event.SEEN_STATUS, onMessageSeen);
            socket.off(SocketConstants.Event.DISCONNECT, onUserDisconnect);
            socket.off(SocketConstants.Event.USER_LEFT, onUserLeft);
        });
    }

    @Override
    public void bindCreator() {
//        setMyStatus(ONLINE);
//        if (canEmit())
        eventEmitter.bindCreator();
    }

    @Override
    public void emitMessage(@NonNull NewMessage newMessage) {
//        if (canEmit())
        eventEmitter.emitMessage(newMessage);
    }

    @Override
    public void emitTypingStatus(boolean isTyping) {
//        if (canEmit())
        eventEmitter.emitTypingStatus(isTyping);
    }

    @Override
    public void emitMessageStatus(@MessageStatus.Status int value,
                                  @NonNull String senderId,
                                  @NonNull String... messageId) {
//        if (canEmit())
        eventEmitter.emitMessageStatus(value,
                senderId,
                messageId);
    }

    @Override
    public void emitUserStatus(@UserStatus.Status int value) {
//        setMyStatus(OFFLINE);
//        if (canEmit())
        if (eventEmitter != null) {
            eventEmitter.emitUserStatus(value);
        }
        if (value == UserStatus.DISCONNECTED) {
            unregisterEventsFromListeningSocket();
            disconnect();
        }
    }

    private final Emitter.Listener onSocketConnected = args -> {
        Log.d(TAG, "Socket Connected");
        registerEventsToListenSocket();

        if (ChatSessionManager.getInstance().getSenderId() != null)
            bindCreator();

        eventObserver.onSocketConnected();
    };

    private final Emitter.Listener onSocketDisconnected = args -> {
        Log.d(TAG, "Socket Disconnected");
        eventObserver.onSocketDisconnected();
    };

    private final Emitter.Listener onServerSender = args -> eventObserver.onMessageSent(args);

    private final Emitter.Listener onNewMessage = args -> {
        ReceivedMessage message = eventObserver.onNewMessage(args);

        emitMessageStatus(MessageStatus.DELIVERED,
                message.getCreator().getId(),
                message.getServerChatId());

        if (isReceiverBound() && message.getCreator().getId().equals(receiverId)) {
            emitMessageStatus(MessageStatus.SEEN,
                    message.getCreator().getId(),
                    message.getServerChatId());
        }
    };

    private final Emitter.Listener onTyping = args -> eventObserver.onTyping(args);

    private final Emitter.Listener onStopTyping = args -> eventObserver.onStopTyping(args);

    private final Emitter.Listener onUserLeft = args -> eventObserver.onUserLeft(args);

    private final Emitter.Listener onUserDisconnect = args -> eventObserver.onUserDisconnected(args);

    private final Emitter.Listener onMessageSeen = args -> eventObserver.onMessageSeen(args);

    private final Emitter.Listener onMessageDelivered = args -> eventObserver.onMessageDelivered(args);

    private final Emitter.Listener onFriendsUpdated = args -> eventObserver.onOnlineUsersUpdated(args);

    /**
     * Sending seen status for all the messages which are yet to be seen
     */
    public void makeAllMessagesSeen() {
        if (isSocketConnected() && isReceiverBound()) {
            Runnable idsTask = DBTaskFactory.getRunnableTask(
                    new MessagesIdsFactory(receiverId, messageIds -> {
                        DBQueryExecutor.executeTask(DBTaskFactory.getRunnableTask(
                                new MessageStatusFactory(
                                        receiverId,
                                        Message.SEEN,
                                        messageIds
                                )));
                        emitMessageStatus(MessageStatus.SEEN, receiverId, messageIds);
                    }));
            DBQueryExecutor.executeTask(idsTask);
        }
    }

    /**
     * Returns true if socket is being connected with server.
     *
     * @return true if socket connected
     */
    public boolean isSocketConnected() {
        if (socket != null && socket.connected())
            return true;
        Log.i(TAG, "Socket connection failure...");
        return false;
    }

    /**
     * Register SocketEventListener to receive events callback from socket.
     * should be called registered by any activity, fragment or any other component
     *
     * @param eventListener - instance of SocketEventListener
     */
    public void registerSocketEventHandler(SocketEventListener eventListener) {
        Log.d(TAG, "Registering Component Event Listener");
        this.eventListener = eventListener;
        if (eventObserver != null)
            eventObserver.setEventListener(eventListener);
    }

    /**
     * Should be un register the SocketEventListener before destroying the
     * activity, fragment or any other component
     */
    public void unregisterSocketEventHandler(SocketEventListener eventListener) {
        if (this.eventListener == eventListener) {
            Log.d(TAG, "UnRegistering Component Event Listener");
            this.eventListener = null;
            if (eventObserver != null)
                eventObserver.setEventListener(null);
        }
    }

    public void unbindReceiver() {
        this.receiverId = null;
        Log.d(TAG, "unbindReceiver: ");

        if (eventEmitter != null)
            eventEmitter.setReceiverId(null);

        if (eventObserver != null)
            eventObserver.setSenderId(null);
    }

    /**
     * Receiver id should be set before sending any event
     *
     * @param receiverId
     */
    public void bindReceiver(String receiverId) {
        this.receiverId = receiverId;

        if (eventEmitter != null)
            eventEmitter.setReceiverId(receiverId);

        if (eventObserver != null)
            eventObserver.setSenderId(receiverId);
    }

    /**
     * Check if receiver is found or not.
     *
     * @return
     */
    private boolean isReceiverBound() {
        if (receiverId == null) {
            Log.i(TAG, "No receiver ids found");
            return false;
        }
        return true;
    }

    private boolean canEmit() {
        if (eventEmitter == null)
            throw new NoEmitterInstantiatedException();
        return true;
    }

    public void setMyStatus(@MyStatus int userStatus) {
        this.userStatus = userStatus;
    }

    public int getMyStatus() {
        return userStatus;
    }

}