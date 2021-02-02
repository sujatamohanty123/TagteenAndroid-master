package in.tagteen.tagteen.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jayattama Prusty on 02-Sep-17.
 */

public class InsertCoolModel {

    @SerializedName("flag")
    @Expose
    private int flag;

    @SerializedName("post_id")
    @Expose
    private String post_id;

    @SerializedName("friend_user_id")
    @Expose
    private String friend_user_id;


    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getFriend_user_id() {
        return friend_user_id;
    }

    public void setFriend_user_id(String friend_user_id) {
        this.friend_user_id = friend_user_id;
    }
}
