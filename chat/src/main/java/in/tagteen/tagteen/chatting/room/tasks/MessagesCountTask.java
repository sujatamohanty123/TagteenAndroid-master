package in.tagteen.tagteen.chatting.room.tasks;

import androidx.annotation.NonNull;

import java.util.concurrent.Callable;

import in.tagteen.tagteen.chatting.room.DBQueryHandler;

/**
 * Created by tony00 on 6/1/2019.
 */
class MessagesCountTask implements Callable<Integer> {

    private String receiverId;

    MessagesCountTask(@NonNull String receiverId) {
        this.receiverId = receiverId;
    }

    @Override
    public Integer call() throws Exception {
        return DBQueryHandler.getUnseenMessageCount(receiverId);
    }
}
