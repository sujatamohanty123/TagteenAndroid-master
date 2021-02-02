package in.tagteen.tagteen.chatting.room;

import android.os.AsyncTask;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import in.tagteen.tagteen.chatting.model.ChatHistory;
import in.tagteen.tagteen.chatting.model.DeleteChatHistoryIn;
import in.tagteen.tagteen.chatting.model.OfflineMessageStatus;
import in.tagteen.tagteen.chatting.model.ReceivedMessage;
import in.tagteen.tagteen.chatting.room.tasks.DBTaskFactory;
import in.tagteen.tagteen.chatting.room.tasks.MessageStatusFactory;
import in.tagteen.tagteen.chatting.room.tasks.NewOfflineMessageFactory;

/**
 * Created by tony00 on 5/25/2019.
 */
public class ChatOfflineMessageExecutor extends AsyncTask<ChatHistory, Void, DeleteChatHistoryIn> {

    private MessageExecutedListener listener;

    @Override
    protected DeleteChatHistoryIn doInBackground(ChatHistory... offlineMessages) {
        List<ReceivedMessage> messages = offlineMessages[0].getMessages();
        List<OfflineMessageStatus> deliveredMessages = offlineMessages[0].getDeliveredMessages();
        List<OfflineMessageStatus> seenMessages = offlineMessages[0].getSeenMessages();

        List<String> newMessageIds = null;

        try {
            newMessageIds = updateNewMessages(messages);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        updateMessageStatus(deliveredMessages, Message.DELIVERED);

        List<String> seenMessageIds = updateMessageStatus(seenMessages, Message.SEEN);

        if (newMessageIds != null || seenMessageIds != null) {
            DeleteChatHistoryIn deleteChatHistoryIn = new DeleteChatHistoryIn();
            deleteChatHistoryIn.setServerMessageIds(newMessageIds);
            deleteChatHistoryIn.setSeenServerMessageIds(seenMessageIds);
            return deleteChatHistoryIn;
        }

        return null;
    }

    @Override
    protected void onPostExecute(DeleteChatHistoryIn input) {
        super.onPostExecute(input);
        if (listener != null) listener.onExecuted(input);
    }

    private List<String> updateNewMessages(List<ReceivedMessage> messages)
            throws ExecutionException, InterruptedException {
        if (messages != null && messages.size() > 0) {
            List<String> messageIds = new ArrayList<>();
            for (ReceivedMessage message : messages) {
                messageIds.add(message.getServerChatId());
                DBQueryExecutor.submitTask(DBTaskFactory.getRunnableTask(
                        new NewOfflineMessageFactory(ReceivedMessage.toMessage(message))), true);
            }
            return messageIds;
        }
        return null;
    }

    private List<String> updateMessageStatus(List<OfflineMessageStatus> offlineMessages,
                                             @Message.Status int status) {
        if (offlineMessages != null && offlineMessages.size() > 0) {
            Map<String, List<String>> messages = new HashMap<>();
            List<String> messageToDelete = new ArrayList<>();

            for (OfflineMessageStatus message : offlineMessages) {
                String receiver = message.getReceiverId();
                List<String> messagesIds = messages.get(receiver);
                if (messagesIds == null) {
                    List<String> ids = new ArrayList<>();
                    ids.add(message.getServerChatId());
                    messages.put(receiver, ids);
                } else {
                    messagesIds.add(message.getServerChatId());
                }
            }

            for (Map.Entry<String, List<String>> entry : messages.entrySet()) {
                String receiverId = entry.getKey();
                List<String> messageIds = entry.getValue();

                messageToDelete.addAll(messageIds);

                DBQueryExecutor.executeTask(DBTaskFactory.getRunnableTask(
                        new MessageStatusFactory(receiverId,
                                status,
                                messageIds.toArray(new String[messageIds.size()]))));
            }

            return messageToDelete;
        }

        return null;
    }

    public void setListener(@Nullable MessageExecutedListener listener) {
        this.listener = listener;
    }

    public interface MessageExecutedListener {
        void onExecuted(@Nullable DeleteChatHistoryIn deleteChatHistoryIn);
    }
}
