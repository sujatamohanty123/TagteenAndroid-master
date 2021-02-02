package in.tagteen.tagteen.chatting.room.tasks;

import androidx.annotation.NonNull;

import in.tagteen.tagteen.chatting.room.DBQueryHandler;
import in.tagteen.tagteen.chatting.room.Message;

/**
 * Created by tony00 on 6/1/2019.
 */
class UpdateMessageTask implements Runnable {

    private String messageId;
    private String serverMessageId;
    private String receiverId;
    private long serverDate;

    UpdateMessageTask(@NonNull String messageId,
                             @NonNull String serverMessageId,
                             @NonNull String receiverId,
                             long serverDate) {
        this.messageId = messageId;
        this.serverMessageId = serverMessageId;
        this.receiverId = receiverId;
        this.serverDate = serverDate;
    }

    @Override
    public void run() {
        DBQueryHandler.updateMessage(serverMessageId,
                serverDate,
                Message.SENT,
                messageId,
                receiverId);
    }
}
