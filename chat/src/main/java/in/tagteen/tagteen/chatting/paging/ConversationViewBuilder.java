package in.tagteen.tagteen.chatting.paging;

import androidx.annotation.NonNull;

import in.tagteen.tagteen.chatting.model.MessageStatus;
import in.tagteen.tagteen.chatting.room.Message;
import in.tagteen.tagteen.chatting.socket.SocketConstants;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by tony00 on 11/16/2018.
 */
class ConversationViewBuilder {

    private ConversationModel cModel = new ConversationModel();
    private List<Message> messages = new ArrayList<>();
    private long lastMessageDate;
    private boolean areMoreMessagesAvailable;

    void submitMessages(List<Message> messages, boolean areMoreMessagesAvailable) {
        this.areMoreMessagesAvailable = areMoreMessagesAvailable;
        for (int i = 0; i < messages.size(); i++) {
            this.messages.add(i, messages.get(i));
        }
        build();
    }

    void addMessage(@NonNull Message message, boolean isDrafted) {
        if (message == null)
            return;

        if (isDrafted) {
            int pos = InterpolationSearch.findMessageAdapterPosition(messages, message.getId());
            if (pos != -1) {
                messages.set(pos, message);
                cModel.replace(message);
            }
//            for (Message msg : this.messages) {
//                if (msg.getMsgId().equals(message.getMsgId())) {
//                    this.messages.set(this.messages.indexOf(msg), message);
//                    cModel.replace(message);
//                    return;
//                }
//            }
        } else {
            this.messages.add(message);
            if (needDayItem(message.getDate(), lastMessageDate)) {
                lastMessageDate = message.getDate();
                cModel.add(new ConversationModel.Day(message.getDate()));
            }

            if (message.getChatType() == SocketConstants.MessageType.TEXT)
                cModel.add(new ConversationModel.Content(message));
            else if (message.getChatType() == SocketConstants.MessageType.SOUND_EMOJI)
                cModel.add(new ConversationModel.SoundEmoji(message));
        }
    }

    public void addMessages(List<Message> messages) {
        this.messages.addAll(messages);
//        build();
        for (Message msg : messages) {
            if (needDayItem(msg.getDate(), lastMessageDate)) {
                cModel.add(new ConversationModel.Day(msg.getDate()));
                lastMessageDate = msg.getDate();
            }
            if (msg.getChatType() == SocketConstants.MessageType.TEXT)
                cModel.add(new ConversationModel.Content(msg));
            else if (msg.getChatType() == SocketConstants.MessageType.SOUND_EMOJI)
                cModel.add(new ConversationModel.SoundEmoji(msg));
        }
    }

    public void updateMessage(MessageStatus status, String... messageIds) {
        for (String id : messageIds) {
            for (Message msg : this.messages) {
                if (id.equals(msg.getServerMsgId())) {
                    msg.setMessageStatus(status.getStatus());
                    cModel.updateMessage(status, id);
                    break;
                }
            }
        }
    }

    private void build() {
        if (messages.size() > 0) {
            cModel.clear();

            if (areMoreMessagesAvailable)
                cModel.add(new ConversationModel.Progress());

            lastMessageDate = messages.get(0).getDate();
            cModel.add(new ConversationModel.Day(messages.get(0).getDate()));

            for (Message msg : messages) {
                if (needDayItem(msg.getDate(), lastMessageDate)) {
                    cModel.add(new ConversationModel.Day(msg.getDate()));
                    lastMessageDate = msg.getDate();
                }

                if (msg.getChatType() == SocketConstants.MessageType.TEXT)
                    cModel.add(new ConversationModel.Content(msg));
                else if (msg.getChatType() == SocketConstants.MessageType.SOUND_EMOJI)
                    cModel.add(new ConversationModel.SoundEmoji(msg));
            }
//            lastMessageDate = messages.get(messages.size() - 1).getDate();
        }
    }

    private boolean needDayItem(long currMessage, long prevMessage) {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date cDate = new Date(currMessage);
            cDate = formatter.parse(formatter.format(cDate));

            Date pDate = new Date(prevMessage);
            pDate = formatter.parse(formatter.format(pDate));

            return cDate.after(pDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    ConversationModel getModel() {
        return cModel;
    }

    List<Message> getMessages() {
        return messages;
    }
}
