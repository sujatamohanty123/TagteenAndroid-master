package in.tagteen.tagteen.chatting.room.tasks;

import java.util.List;
import java.util.concurrent.Callable;

import in.tagteen.tagteen.chatting.room.DBQueryHandler;
import in.tagteen.tagteen.chatting.room.Message;

/**
 * Created by tony00 on 7/13/2019.
 */
class MessagesListTask implements Callable<List<Message>> {
    private String receiverId;
    private int startId;
    private int endId;

    MessagesListTask(String receiverId, int startId, int endId) {
        this.receiverId = receiverId;
        this.startId = startId;
        this.endId = endId;
    }

    @Override
    public List<Message> call() throws Exception {
        return DBQueryHandler.getMessages(receiverId, startId, endId);
    }
}
