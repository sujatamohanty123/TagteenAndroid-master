package in.tagteen.tagteen.chatting.paging;

import in.tagteen.tagteen.chatting.model.MessageStatus;
import in.tagteen.tagteen.chatting.room.Message;

import java.util.ArrayList;
import java.util.List;

import static in.tagteen.tagteen.chatting.room.Message.MESSAGE_IN;

/**
 * Created by tony00 on 11/16/2018.
 */
class ConversationModel {

    private List<ConversationItemType> models;

    ConversationModel() {
        models = new ArrayList<>();
    }

    void add(ConversationItemType model) {
        models.add(model);
    }

    int count() {
        return models.size();
    }

    void clear() {
        models.clear();
    }

    ConversationItemType itemAt(int position) {
        return models.get(position);
    }

    int type(int position) {
        return itemAt(position).getType();
    }

    void replace(Message message) {
        for (ConversationItemType item : models) {
            if (item instanceof Content) {
                if (((Content) item).getMessage().getMsgId().equals(message.getMsgId())) {
                    ((Content) item).setMessage(message);
                    return;
                }
            }else if (item instanceof SoundEmoji) {
                if (((SoundEmoji) item).getMessage().getMsgId().equals(message.getMsgId())) {
                    ((SoundEmoji) item).setMessage(message);
                    return;
                }
            }
        }
    }

    public void updateMessage(MessageStatus status, String messageId) {
        for (ConversationItemType item : models) {
            Message message = null;
            if (item instanceof Content) {
                message = ((Content) item).getMessage();
            } else if (item instanceof SoundEmoji) {
                message = ((SoundEmoji) item).getMessage();
            }
            if (message != null && message.getMsgId().equals(messageId)) {
                message.setMessageStatus(status.getStatus());
                break;
            }
        }
    }

    static class Content implements ConversationItemType {

        private Message message;

        Content(Message message) {
            this.message = message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }

        @Override
        public int getType() {
            return message.getMessageType() == MESSAGE_IN ? TYPE_TEXT_INCOMING : TYPE_TEXT_OUTGOING;
        }

        public Message getMessage() {
            return message;
        }
    }

    static class Day implements ConversationItemType {

        private long date;

        Day(long date) {
            this.date = date;
        }

        @Override
        public int getType() {
            return TYPE_DAY;
        }

        public void setDate(long date) {
            this.date = date;
        }

        public long getDate() {
            return date;
        }
    }

    static class Progress implements ConversationItemType {

        @Override
        public int getType() {
            return TYPE_PROGRESS;
        }

    }

    static class SoundEmoji implements ConversationItemType {

        private Message message;

        SoundEmoji(Message message) {
            this.message = message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }

        @Override
        public int getType() {
            return message.getMessageType() == MESSAGE_IN ? TYPE_SOUND_EMOJI_INCOMING : TYPE_SOUND_EMOJI_OUTGOING;
        }

        public Message getMessage() {
            return message;
        }
    }

    interface ConversationItemType {

        int TYPE_TEXT_INCOMING = 0x64;
        int TYPE_TEXT_OUTGOING = 0x65;
        int TYPE_PROGRESS = 0x66;
        int TYPE_DAY = 0x67;
        int TYPE_SOUND_EMOJI_INCOMING = 0x68;
        int TYPE_SOUND_EMOJI_OUTGOING = 0x69;

        int getType();
    }
}
