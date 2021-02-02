package in.tagteen.tagteen.chatting.room.tasks;

import androidx.annotation.NonNull;

import java.util.concurrent.Callable;

/**
 * Created by tony00 on 7/13/2019.
 */
public class MessagesCountFactory implements AbstractCallableFactory<Integer> {
    private String receiverId;

    public MessagesCountFactory(@NonNull String receiverId) {
        this.receiverId = receiverId;
    }

    @Override
    public Callable<Integer> createCallableTask() {
        return new MessagesCountTask(receiverId);
    }
}
