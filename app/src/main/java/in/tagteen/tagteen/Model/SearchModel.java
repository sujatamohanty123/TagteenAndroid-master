package in.tagteen.tagteen.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Jayattama Prusty on 29-Aug-17.
 */

public class SearchModel {

    @SerializedName("success")
    @Expose
    private Boolean success;

    @SerializedName("data")
    @Expose
    private ArrayList<GetAllUserFriendlist.FriendsUserList> datalist = null;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }


    public ArrayList<GetAllUserFriendlist.FriendsUserList> getDatalist() {
        return datalist;
    }

    public void setDatalist(ArrayList<GetAllUserFriendlist.FriendsUserList> datalist) {
        this.datalist = datalist;
    }
}
