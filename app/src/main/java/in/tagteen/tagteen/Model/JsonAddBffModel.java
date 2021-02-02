package in.tagteen.tagteen.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Jayattama Prusty on 30-Aug-17.
 */

public class JsonAddBffModel {
    @SerializedName("friend_user_id")
    @Expose
    private ArrayList<String> frnduseridarray;

    @SerializedName("user_id")
    @Expose
    private String user_id;

    public ArrayList<String> getFrnduseridarray() {
        return frnduseridarray;
    }

    public void setFrnduseridarray(ArrayList<String> frnduseridarray) {
        this.frnduseridarray = frnduseridarray;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
