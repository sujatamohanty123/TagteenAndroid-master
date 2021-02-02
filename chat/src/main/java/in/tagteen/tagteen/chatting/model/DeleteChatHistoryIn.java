package in.tagteen.tagteen.chatting.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Created by tony00 on 6/9/2019.
 */
public class DeleteChatHistoryIn implements Serializable {

    @SerializedName("server_chat_id")
    private List<String> serverMessageIds;

    @SerializedName("seen_server_chat_id")
    private List<String> seenServerMessageIds;

    public List<String> getServerMessageIds() {
        return serverMessageIds;
    }

    public void setServerMessageIds(List<String> serverMessageIds) {
        this.serverMessageIds = serverMessageIds;
    }

    public List<String> getSeenServerMessageIds() {
        return seenServerMessageIds;
    }

    public void setSeenServerMessageIds(List<String> seenServerMessageIds) {
        this.seenServerMessageIds = seenServerMessageIds;
    }
}
