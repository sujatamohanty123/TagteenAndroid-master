package in.tagteen.tagteen.chatting.model;

import androidx.annotation.NonNull;

import in.tagteen.tagteen.chatting.utils.ChatSessionManager;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ChatId implements Serializable {

    private transient String event;

    @SerializedName("chat_creator_id")
    private String senderId;

    @SerializedName("chat_receiver_id")
    private String[] receiverId;

    ChatId(@NonNull String event) {
        this.event = event;
        this.senderId = ChatSessionManager.getInstance().getSenderId();
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setReceiverId(String[] receiverId) {
        this.receiverId = receiverId;
    }

    public String[] getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        setReceiverId(new String[]{receiverId});
    }
}
