package in.tagteen.tagteen.chatting.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by tony00 on 5/23/2019.
 */
public class ChatHistory implements Serializable {

    @SerializedName("get_all_chat")
    private List<ReceivedMessage> messages;

    @SerializedName("get_delivery_chat")
    private List<OfflineMessageStatus> deliveredMessages;

    @SerializedName("get_seen_chat")
    private List<OfflineMessageStatus> seenMessages;

    public void setMessages(List<ReceivedMessage> messages) {
        this.messages = messages;
    }

    public List<ReceivedMessage> getMessages() {
        return messages;
    }

    public List<OfflineMessageStatus> getDeliveredMessages() {
        return deliveredMessages;
    }

    public void setDeliveredMessages(List<OfflineMessageStatus> deliveredMessages) {
        this.deliveredMessages = deliveredMessages;
    }

    public List<OfflineMessageStatus> getSeenMessages() {
        return seenMessages;
    }

    public void setSeenMessages(List<OfflineMessageStatus> seenMessages) {
        this.seenMessages = seenMessages;
    }
}
