package in.tagteen.tagteen.chatting.room.tasks;

import androidx.annotation.NonNull;

import java.util.concurrent.Callable;

import in.tagteen.tagteen.chatting.room.DBQueryHandler;
import in.tagteen.tagteen.chatting.room.Message;

/**
 * Created by tony00 on 6/1/2019.
 */
class FetchLastMessageTask implements Callable<Message> {

    private String receiverId;

    FetchLastMessageTask(@NonNull String receiverId) {
        this.receiverId = receiverId;
    }

    @Override
    public Message call() throws Exception {
        return DBQueryHandler.getLastMessage(receiverId);
    }
}
