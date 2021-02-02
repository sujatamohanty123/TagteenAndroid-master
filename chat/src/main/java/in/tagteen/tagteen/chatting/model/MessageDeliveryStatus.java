package in.tagteen.tagteen.chatting.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by tony00 on 5/5/2019.
 */
public class MessageDeliveryStatus {

    @SerializedName("delivery_chat")
    private List<MessageDelivery> messages;

    public void setMessages(List<MessageDelivery> messages) {
        this.messages = messages;
    }

    public List<MessageDelivery> getMessages() {
        return messages;
    }

    public static class MessageDelivery {
        @SerializedName("chat_receiver_id")
        private String receiverId;

        @SerializedName("chat_creator_id")
        private String senderId;

        @SerializedName("_id")
        private String serverChatId;

        public String getReceiverId() {
            return receiverId;
        }

        public void setReceiverId(String receiverId) {
            this.receiverId = receiverId;
        }

        public String getSenderId() {
            return senderId;
        }

        public void setSenderId(String senderId) {
            this.senderId = senderId;
        }

        public String getServerChatId() {
            return serverChatId;
        }

        public void setServerChatId(String serverChatId) {
            this.serverChatId = serverChatId;
        }
    }
}

