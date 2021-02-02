package in.tagteen.tagteen.chatting.socket;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import in.tagteen.tagteen.chatting.model.Friend;
import in.tagteen.tagteen.chatting.model.MessageStatus;
import in.tagteen.tagteen.chatting.model.TypingStatus;
import in.tagteen.tagteen.chatting.model.UserStatus;
import in.tagteen.tagteen.chatting.room.Message;

import java.util.List;

public interface SocketEventListener {

    default void onSocketConnectionChanged(boolean isConnected){}

    default void onNewMessageReceived(@NonNull Message message){}

    default void onTypingStatusChanged(@NonNull TypingStatus typing){}

    default void onUserStatusChanged(@NonNull UserStatus userStatus){}

    default void onMessageStatusChanged(@NonNull MessageStatus msgStatus,
                                @Nullable String... messageIds){}

    default void onOnlineUsersChanged(@Nullable List<Friend> friends){}

    default void onOtherEventReceived(@NonNull String event, Object data){}

}
