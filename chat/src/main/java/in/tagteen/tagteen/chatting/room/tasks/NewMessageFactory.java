package in.tagteen.tagteen.chatting.room.tasks;

import androidx.annotation.NonNull;

import in.tagteen.tagteen.chatting.room.Message;

/**
 * Created by tony00 on 7/13/2019.
 */
public class NewMessageFactory implements AbstractRunnableFactory {

    private Message message;

    public NewMessageFactory(@NonNull Message message) {
        this.message = message;
    }

    @Override
    public Runnable createRunnableTask() {
        return new NewMessageTask(message);
    }
}
