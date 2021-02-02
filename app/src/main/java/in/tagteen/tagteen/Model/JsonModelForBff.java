package in.tagteen.tagteen.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Jayattama Prusty on 30-Aug-17.
 */

public class JsonModelForBff {

    @SerializedName("user_id")
    @Expose
    private String user_id;

    @SerializedName("friend_user_id")
    @Expose
    private String friend_user_id;



    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getFriend_user_id() {
        return friend_user_id;
    }

    public void setFriend_user_id(String friend_user_id) {
        this.friend_user_id = friend_user_id;
    }


}
