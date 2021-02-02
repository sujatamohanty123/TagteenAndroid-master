package in.tagteen.tagteen.chatting.room.tasks;

import androidx.annotation.NonNull;

import in.tagteen.tagteen.chatting.room.DBQueryHandler;
import in.tagteen.tagteen.chatting.room.Message;

/**
 * Created by tony00 on 6/1/2019.
 */
class NewMessageTask implements Runnable {

    private Message message;

    NewMessageTask(@NonNull Message message) {
        this.message = message;
    }

    @Override
    public void run() {
        DBQueryHandler.addMessage(message);
    }
}
