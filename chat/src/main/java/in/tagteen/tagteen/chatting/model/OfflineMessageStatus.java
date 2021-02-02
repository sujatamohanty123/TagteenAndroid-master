package in.tagteen.tagteen.chatting.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by tony00 on 6/9/2019.
 */
public class OfflineMessageStatus implements Serializable {

    @SerializedName("chat_creator_id")
    private String creator;

    @SerializedName("chat_receiver_id")
    private String receiverId;

    @SerializedName("date_created")
    private long serverDate;

    @SerializedName("_id")
    private String id;

    @SerializedName("msg_timer")
    private String date;

    @SerializedName("server_chat_id")
    private String serverChatId;

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreator() {
        return creator;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getsChat() {
        return id;
    }

    public void setsChat(String sChat) {
        this.id = sChat;
    }
}
