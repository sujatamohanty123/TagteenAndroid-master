package in.tagteen.tagteen.chatting.model;

import com.google.gson.annotations.SerializedName;

public class SentMessage extends MessageInfo {

    @SerializedName("chat_creator_id")
    private String senderId;

    @SerializedName("chat_receiver_id")
    private String receiverId;

    @SerializedName("date_created")
    private long serverDate;

    @SerializedName("_id")
    private String serverChatId;

    @SerializedName("is_received")
    private boolean isReceived;


    public SentMessage() {
        super();
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public long getServerDate() {
        return serverDate;
    }

    public void setServerDate(long serverDate) {
        this.serverDate = serverDate;
    }

    public String getServerChatId() {
        return serverChatId;
    }

    public void setServerChatId(String serverChatId) {
        this.serverChatId = serverChatId;
    }

    public boolean isReceived() {
        return isReceived;
    }

    public void setReceived(boolean received) {
        isReceived = received;
    }

    @Override
    public String toString() {
        return "SentMessage{" +
                "receiverId='" + receiverId + '\'' +
                ", serverDate=" + serverDate +
                ", serverChatId='" + serverChatId + '\'' +
                ", isReceived=" + isReceived +
                '}' + "\n" + super.toString();
    }
}
