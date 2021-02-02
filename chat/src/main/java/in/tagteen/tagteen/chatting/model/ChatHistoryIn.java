package in.tagteen.tagteen.chatting.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by tony00 on 5/25/2019.
 */
public class ChatHistoryIn {

    @SerializedName("chat_receiver_id")
    private String receiverId;

    public ChatHistoryIn(String receiverId){
        this.receiverId = receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverId() {
        return receiverId;
    }
}

