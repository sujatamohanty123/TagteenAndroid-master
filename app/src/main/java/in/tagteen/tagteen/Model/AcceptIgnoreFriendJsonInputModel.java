package in.tagteen.tagteen.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jayattama Prusty on 04-Sep-17.
 */

public class AcceptIgnoreFriendJsonInputModel {

    @SerializedName("request_user_id")
    @Expose
    private String request_user_id;

    @SerializedName("friend_user_id")
    @Expose
    private String friend_user_id;

    @SerializedName("request_status")
    @Expose
    private boolean request_status;

    public String getRequest_user_id() {
        return request_user_id;
    }

    public void setRequest_user_id(String request_user_id) {
        this.request_user_id = request_user_id;
    }

    public String getFriend_user_id() {
        return friend_user_id;
    }

    public void setFriend_user_id(String friend_user_id) {
        this.friend_user_id = friend_user_id;
    }

    public boolean isRequest_status() {
        return request_status;
    }

    public void setRequest_status(boolean request_status) {
        this.request_status = request_status;
    }
}
