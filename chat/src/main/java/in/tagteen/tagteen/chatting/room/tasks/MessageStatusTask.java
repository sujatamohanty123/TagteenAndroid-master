package in.tagteen.tagteen.chatting.room.tasks;

import androidx.annotation.NonNull;

import in.tagteen.tagteen.chatting.room.DBQueryHandler;
import in.tagteen.tagteen.chatting.room.Message;
import in.tagteen.tagteen.chatting.socket.MainThreadExecutor;

/**
 * Created by tony00 on 6/18/2019.
 */
class MessageStatusTask implements Runnable, MainThreadExecutor {

    private String receiverId;
    private String[] serverMessageIds;
    private int status;

    MessageStatusTask(@NonNull String receiverId,
                             @Message.Status int status,
                             @NonNull String... serverMessageIds) {
        this.receiverId = receiverId;
        this.serverMessageIds = serverMessageIds;
        this.status = status;
    }

    @Override
    public void run() {
        DBQueryHandler.updateMessageStatus(status,
                receiverId,
                serverMessageIds);
    }
}
