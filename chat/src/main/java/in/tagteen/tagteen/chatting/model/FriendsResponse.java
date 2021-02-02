package in.tagteen.tagteen.chatting.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class FriendsResponse implements Serializable {

    @SerializedName("pending_user_list")
    private List<Friend> pendingFriends;

    @SerializedName("friends_user_list")
    private List<Friend> friends;

    public FriendsResponse(List<Friend> friends) {
        this.friends = friends;
    }

    public List<Friend> getPendingFriends() {
        return pendingFriends;
    }

    public List<Friend> getFriends() {
        return friends;
    }
}
