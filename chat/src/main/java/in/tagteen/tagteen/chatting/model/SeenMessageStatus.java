package in.tagteen.tagteen.chatting.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import in.tagteen.tagteen.chatting.socket.SocketConnection;
import in.tagteen.tagteen.chatting.socket.SocketConstants;

/**
 * Created by tony00 on 6/9/2019.
 */
//tony14
public class SeenMessageStatus extends MessageStatus {

    @SerializedName("_id")
    private String[] serverChatId;

    public SeenMessageStatus() {
        super(SEEN);
    }

    public void setServerChatId(String[] serverChatId) {
        this.serverChatId = serverChatId;
    }

    public String[] getServerChatId() {
        return serverChatId;
    }
}