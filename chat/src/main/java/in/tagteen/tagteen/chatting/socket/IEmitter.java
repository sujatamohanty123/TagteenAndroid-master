package in.tagteen.tagteen.chatting.socket;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import in.tagteen.tagteen.chatting.model.MessageStatus;
import in.tagteen.tagteen.chatting.model.NewMessage;
import in.tagteen.tagteen.chatting.model.UserStatus;

/**
 * Created by tony00 on 3/30/2019.
 */
public interface IEmitter {

    /**
     * The logged in user should be bound with socket as soon as
     * the connection established
     */
    void bindCreator();

    /**
     * Sending a new message to server
     *
     * @param newMessage - NewMessage instance
     */
    void emitMessage(@NonNull NewMessage newMessage);

    /**
     * Sending typing status to server. if true, it emits the event
     * SocketConstants.Events.TYPING or else emits SocketConstants.Events.STOP_TYPING
     *
     * @param isTyping - true if user typing
     */
    void emitTypingStatus(boolean isTyping);

    /**
     * Sending message status to server. it emits the event either
     * SocketConstants.Events.DELIVERY_STATUS or SocketConstants.Events.SEEN_STATUS based on the value.
     *
     * @param value - should be either MessageStatus.DELIVERED or MessageStatus.SEEN
     */
    void emitMessageStatus(@MessageStatus.Status int value,
                           @NonNull String senderId,
                           @NonNull String... messageId);

    /**
     * Sending User status to server. it emits the event either
     * SocketConstants.Events.USER_LEFT or SocketConstants.Events.DISCONNECTED base don the value.
     *
     * @param value - should be either UserStatus.LEFT or UserStatus.DISCONNECTED
     */
    void emitUserStatus(@UserStatus.Status int value);
}
