package in.tagteen.tagteen.chatting.room.tasks;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.concurrent.Callable;

import in.tagteen.tagteen.chatting.room.DBQueryHandler;

/**
 * Created by tony00 on 7/13/2019.
 */
class TotalMessagesTask implements Callable<List<Integer>> {
    private String receiverId;
    private int offset;

    TotalMessagesTask(@NonNull String receiverId, int offset) {
        this.receiverId = receiverId;
        this.offset = offset;
    }

    @Override
    public List<Integer> call() throws Exception {
        return DBQueryHandler.getTotalMessagesIds(receiverId, offset);
    }
}
