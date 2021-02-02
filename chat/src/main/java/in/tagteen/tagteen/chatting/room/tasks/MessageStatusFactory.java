package in.tagteen.tagteen.chatting.room.tasks;

import androidx.annotation.NonNull;

import in.tagteen.tagteen.chatting.room.Message;

/**
 * Created by tony00 on 7/13/2019.
 */
public class MessageStatusFactory implements AbstractRunnableFactory {
    private String receiverId;
    private String[] serverMessageIds;
    private int status;

    public MessageStatusFactory(@NonNull String receiverId,
                             @Message.Status int status,
                             @NonNull String... serverMessageIds) {
        this.receiverId = receiverId;
        this.serverMessageIds = serverMessageIds;
        this.status = status;
    }

    @Override
    public Runnable createRunnableTask() {
        return new MessageStatusTask(receiverId, status, serverMessageIds);
    }
}
