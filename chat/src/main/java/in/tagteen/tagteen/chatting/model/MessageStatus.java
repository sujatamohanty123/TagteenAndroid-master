package in.tagteen.tagteen.chatting.model;

import androidx.annotation.IntDef;

import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import in.tagteen.tagteen.chatting.socket.SocketConstants;
import in.tagteen.tagteen.chatting.utils.ChatSessionManager;

public class MessageStatus extends Event {

    public static final int SENT = 1;
    public static final int DELIVERED = 2;
    public static final int SEEN = 3;

    @IntDef({SENT, DELIVERED, SEEN})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Status {
    }

    private transient int status;

    @SerializedName("chat_creator_id")
    private String[] senderId;

    @SerializedName("chat_receiver_id")
    private String receiverId;

    public MessageStatus(@MessageStatus.Status int value) {
        super(value == SEEN ?
                SocketConstants.Event.SEEN_STATUS : SocketConstants.Event.DELIVERY_STATUS);
        this.status = value;
        receiverId = ChatSessionManager.getInstance().getSenderId();
    }

    public void setStatus(@MessageStatus.Status int status) {
        setEvent(status == SEEN ?
                SocketConstants.Event.SEEN_STATUS : SocketConstants.Event.DELIVERY_STATUS);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public String[] getSenderId() {
        return senderId;
    }

    public void setSenderId(String[] senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }
}
