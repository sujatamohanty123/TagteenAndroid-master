package in.tagteen.tagteen.chatting.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

import in.tagteen.tagteen.chatting.room.Message;
import in.tagteen.tagteen.chatting.utils.ChatFactory;
import in.tagteen.tagteen.chatting.utils.ChatSessionManager;

public class NewMessage extends MessageInfo {

    @SerializedName("chat_creator_id")
    private String senderId;

    @SerializedName("chat_receiver_id")
    private String[] receiverId;

    public NewMessage() {
        super();
        senderId = ChatSessionManager.getInstance().getSenderId();
    }

    public String getSenderId() {
        return senderId;
    }

    public void setReceiverId(String[] receiverId) {
        this.receiverId = receiverId;
    }

    public void setReceiverId(String receiverId) {
        setReceiverId(new String[]{receiverId});
    }

    public String[] getReceiverId() {
        return receiverId;
    }

    @Override
    public String toString() {
        return "NewMessage{" +
                "receiverId=" + Arrays.toString(receiverId) +
                '}' + "\n" + super.toString();
    }

    @NonNull
    public static Message toMessage(@NonNull NewMessage newMessage,
                              @NonNull String receiverId) {
        Message ms = new Message();
        ms.setMsgId(newMessage.getClientChatId());
        ms.setMessage(newMessage.getMessage());
        ms.setDate(ChatFactory.getFormattedDate(newMessage.getDate()));
        ms.setMessageType(Message.MESSAGE_OUT);
        ms.setChatType(newMessage.getChatType());
        ms.setMessageStatus(Message.NOT_SENT);
        ms.setClientId(receiverId);
        ms.setOwnerId(newMessage.getSenderId());
        return ms;
    }
}
