package in.tagteen.tagteen.chatting.room.tasks;

import androidx.annotation.NonNull;

/**
 * Created by tony00 on 7/13/2019.
 */
public class UpdateMessageFactory implements AbstractRunnableFactory {
    private String messageId;
    private String serverMessageId;
    private String receiverId;
    private long serverDate;

    public UpdateMessageFactory(@NonNull String messageId,
                             @NonNull String serverMessageId,
                             @NonNull String receiverId,
                             long serverDate) {
        this.messageId = messageId;
        this.serverMessageId = serverMessageId;
        this.receiverId = receiverId;
        this.serverDate = serverDate;
    }

    @Override
    public Runnable createRunnableTask() {
        return new UpdateMessageTask(messageId, serverMessageId, receiverId, serverDate);
    }
}
