package in.tagteen.tagteen.chatting.room.tasks;

import androidx.annotation.NonNull;

/**
 * Created by tony00 on 7/13/2019.
 */
public class MessagesIdsFactory implements AbstractRunnableFactory {
    private String receiverId;
    private MessagesIdsTask.OnMessagesFoundListener callback;

    public MessagesIdsFactory(@NonNull String receiverId, MessagesIdsTask.OnMessagesFoundListener callback) {
        this.receiverId = receiverId;
        this.callback = callback;
    }

    @Override
    public Runnable createRunnableTask() {
        return new MessagesIdsTask(receiverId, callback);
    }
}
