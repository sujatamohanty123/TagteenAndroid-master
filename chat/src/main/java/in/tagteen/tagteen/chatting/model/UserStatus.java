package in.tagteen.tagteen.chatting.model;

import androidx.annotation.IntDef;

import in.tagteen.tagteen.chatting.socket.SocketConstants;
import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class UserStatus extends Event {

    public static final int DISCONNECTED = 1;
    public static final int LEFT = 2;

    @IntDef({DISCONNECTED, LEFT})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Status {
    }

    private transient int status;

    @SerializedName("chat_receiver_id")
    private String receiverId;

    public UserStatus(@UserStatus.Status int value) {
        super(value == DISCONNECTED ?
                SocketConstants.Event.DISCONNECT : SocketConstants.Event.USER_LEFT);
        this.status = value;
    }

    public void setStatus(@UserStatus.Status int status) {
        setEvent(status == DISCONNECTED ?
                SocketConstants.Event.DISCONNECT : SocketConstants.Event.USER_LEFT);;
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    @Override
    public String toString() {
        return "UserStatus{" +
                "status=" + status +
                ", receiverId='" + receiverId + '\'' +
                '}';
    }
}
