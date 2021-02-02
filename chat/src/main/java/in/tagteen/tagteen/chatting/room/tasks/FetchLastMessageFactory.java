package in.tagteen.tagteen.chatting.room.tasks;

import androidx.annotation.NonNull;

import java.util.concurrent.Callable;

import in.tagteen.tagteen.chatting.room.Message;

/**
 * Created by tony00 on 7/13/2019.
 */
public class FetchLastMessageFactory implements AbstractCallableFactory<Message> {
    private String receiverId;

    public FetchLastMessageFactory(@NonNull String receiverId) {
        this.receiverId = receiverId;
    }

    @Override
    public Callable<Message> createCallableTask() {
        return new FetchLastMessageTask(receiverId);
    }
}
