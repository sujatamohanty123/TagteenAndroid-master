package in.tagteen.tagteen.chatting.socket;

import in.tagteen.tagteen.chatting.model.ReceivedMessage;
import in.tagteen.tagteen.chatting.model.SentMessage;
import in.tagteen.tagteen.chatting.room.Message;

/**
 * Created by tony00 on 3/30/2019.
 */
public interface IEvents extends MainThreadExecutor{

    void onSocketConnected();

    void onSocketDisconnected();

    SentMessage onMessageSent(Object ...args);

    ReceivedMessage onNewMessage(Object ...args);

    void onTyping(Object ...args);

    void onStopTyping(Object... args);

    void onUserLeft(Object ...args);

    void onUserDisconnected(Object ...args);

    void onMessageSeen(Object ...args);

    void onMessageDelivered(Object ...args);

    void onOnlineUsersUpdated(Object ...args);
}
