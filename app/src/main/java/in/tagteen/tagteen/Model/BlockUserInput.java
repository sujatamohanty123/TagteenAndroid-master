package in.tagteen.tagteen.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lovekushvishwakarma on 05/11/17.
 */

public class BlockUserInput {

    @SerializedName("friend_id")
    @Expose
    private String friend_id;

    @SerializedName("user_id")
    @Expose
    private String user_id;


    public String getFriend_id() {
        return friend_id;
    }

    public void setFriend_id(String friend_id) {
        this.friend_id = friend_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
