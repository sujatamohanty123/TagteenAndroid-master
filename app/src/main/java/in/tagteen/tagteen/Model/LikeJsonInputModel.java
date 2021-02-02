package in.tagteen.tagteen.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jayattama Prusty on 02-Sep-17.
 */

public class LikeJsonInputModel {

    @SerializedName("user_id")
    @Expose
    private String user_id;

    @SerializedName("post_id")
    @Expose
    private String post_id;


    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }
}
