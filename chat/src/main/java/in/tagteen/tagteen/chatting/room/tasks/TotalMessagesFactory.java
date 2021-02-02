package in.tagteen.tagteen.chatting.room.tasks;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by tony00 on 7/13/2019.
 */
public class TotalMessagesFactory implements AbstractCallableFactory<List<Integer>> {
    private String receiverId;
    private int offset;

    public TotalMessagesFactory(@NonNull String receiverId, int offset) {
        this.receiverId = receiverId;
        this.offset = offset;
    }

    @Override
    public Callable<List<Integer>> createCallableTask() {
        return new TotalMessagesTask(receiverId, offset);
    }
}
