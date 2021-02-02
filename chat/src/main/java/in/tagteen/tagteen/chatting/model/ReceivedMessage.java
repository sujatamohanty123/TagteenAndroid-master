package in.tagteen.tagteen.chatting.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import in.tagteen.tagteen.chatting.room.Message;

public class ReceivedMessage extends MessageInfo {

    @SerializedName("chat_creator_id")
    private Friend creator;

    @SerializedName("chat_receiver_id")
    private String receiverId;

    @SerializedName("date_created")
    private long serverDate;

    @SerializedName("_id")
    private String serverChatId;

    @SerializedName("is_received")
    private boolean isReceived;

    public Friend getCreator() {
        return creator;
    }

    public void setCreator(Friend creator) {
        this.creator = creator;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
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

    public static Message toMessage(@NonNull ReceivedMessage receivedMessage){
        Message ms = new Message();
        ms.setMsgId(receivedMessage.getClientChatId());
        ms.setServerMsgId(receivedMessage.getServerChatId());
        ms.setMessage(receivedMessage.getMessage());
        ms.setDate(System.currentTimeMillis());
        ms.setServerDate(receivedMessage.getServerDate() * 1000);
        ms.setMessageType(Message.MESSAGE_IN);
        ms.setChatType(receivedMessage.getChatType());
        ms.setMessageStatus(Message.DELIVERED);
        ms.setClientId(receivedMessage.getCreator().getId());
        ms.setOwnerId(receivedMessage.getReceiverId());
        return ms;
    }
}
