package in.tagteen.tagteen.chatting.room.tasks;

import java.util.List;
import java.util.concurrent.Callable;

import in.tagteen.tagteen.chatting.room.Message;

/**
 * Created by tony00 on 7/13/2019.
 */
public class MessagesListFactory implements AbstractCallableFactory<List<Message>> {
    private String receiverId;
    private int startId;
    private int endId;

    public MessagesListFactory(String receiverId, int startId, int endId) {
        this.receiverId = receiverId;
        this.startId = startId;
        this.endId = endId;
    }

    @Override
    public Callable<List<Message>> createCallableTask() {
        return new MessagesListTask(receiverId, startId, endId);
    }
}
