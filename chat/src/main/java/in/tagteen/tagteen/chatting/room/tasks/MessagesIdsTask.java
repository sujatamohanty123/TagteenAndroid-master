package in.tagteen.tagteen.chatting.room.tasks;

import androidx.annotation.NonNull;

import java.util.List;

import in.tagteen.tagteen.chatting.room.DBQueryHandler;
import in.tagteen.tagteen.chatting.socket.MainThreadExecutor;

/**
 * Created by tony00 on 6/1/2019.
 */
class MessagesIdsTask implements Runnable, MainThreadExecutor {

    private String receiverId;
    private OnMessagesFoundListener callback;

    MessagesIdsTask(@NonNull String receiverId, OnMessagesFoundListener callback) {
        this.receiverId = receiverId;
        this.callback = callback;
    }

    @Override
    public void run() {
        List<String> messageIds = DBQueryHandler.getUnseenMessageIds(receiverId);

        if (messageIds != null &&
                messageIds.size() > 0 &&
                callback != null)
            runOnMainThread(() -> callback.onMessagesFound(messageIds.toArray(new String[messageIds.size()])));
    }

    public interface OnMessagesFoundListener {
        void onMessagesFound(@NonNull String... messageIds);
    }
}
