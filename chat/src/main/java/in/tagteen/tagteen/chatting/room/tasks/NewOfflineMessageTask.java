package in.tagteen.tagteen.chatting.room.tasks;

import androidx.annotation.NonNull;

import in.tagteen.tagteen.chatting.room.DBQueryHandler;
import in.tagteen.tagteen.chatting.room.Message;

/**
 * Created by tony00 on 6/1/2019.
 */
class NewOfflineMessageTask implements Runnable {
    private Message message;

    NewOfflineMessageTask(@NonNull Message message) {
        this.message = message;
    }

    @Override
    public void run() {
        boolean isPresent = DBQueryHandler.isMessagePresent(message);
        if (!isPresent)
            DBQueryHandler.addMessage(message);
        else
            DBQueryHandler.updateMessage(message.getServerMsgId(),
                    message.getServerDate(),
                    message.getMessageStatus(),
                    message.getMsgId(),
                    message.getClientId());
    }
}
