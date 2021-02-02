package in.tagteen.tagteen.chatting.model;

import com.google.gson.annotations.SerializedName;

public class OnMessageStatus extends MessageStatus {

    @SerializedName("chat_receiver_id")
    private String receiverId;

    public OnMessageStatus(int value) {
        super(value);
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverId() {
        return receiverId;
    }

}
