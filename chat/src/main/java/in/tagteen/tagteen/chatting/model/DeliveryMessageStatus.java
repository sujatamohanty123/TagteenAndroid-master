package in.tagteen.tagteen.chatting.model;

import com.google.gson.annotations.SerializedName;

import in.tagteen.tagteen.chatting.socket.SocketConstants;

public class DeliveryMessageStatus extends MessageStatus {

    @SerializedName("_id")
    private String serverChatId;

    public DeliveryMessageStatus() {
        super(DELIVERED);
    }

    public void setServerChatId(String serverChatId) {
        this.serverChatId = serverChatId;
    }

    public String getServerChatId() {
        return serverChatId;
    }
}
