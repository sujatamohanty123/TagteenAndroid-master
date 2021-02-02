package in.tagteen.tagteen.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by user on 04-09-2017.
 */

public class getChatJSONInputModel {
    @SerializedName("chat_receiver_id")
    @Expose
    private String chat_receiver_id;

    public String getChatReceiverId() {
        return chat_receiver_id;
    }
    public void setChatReceiverId(String chat_receiver_id) {
        this.chat_receiver_id = chat_receiver_id;
    }
}


