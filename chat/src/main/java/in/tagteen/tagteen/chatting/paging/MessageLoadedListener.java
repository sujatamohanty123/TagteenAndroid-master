package in.tagteen.tagteen.chatting.paging;

import androidx.annotation.NonNull;

import in.tagteen.tagteen.chatting.room.Message;

import java.util.List;

/**
 * Created by tony00 on 3/30/2019.
 */
public interface MessageLoadedListener {

    default void onLoadedBefore(@NonNull List<Message> messages,
                        boolean areMoreMessagesAvailable){};

    default void onLoadedAfter(@NonNull List<Message> messages){};

    default void onNewMessage(@NonNull Message message, boolean isDrafted){};

}
