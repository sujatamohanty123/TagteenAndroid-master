package in.tagteen.tagteen.TagteenInterface;

import in.tagteen.tagteen.Model.Friend;
import in.tagteen.tagteen.Model.UserModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 04-07-2017.
 */

public interface UserSelectionList {
    void selectionList(List<Friend> userList);
    void selectionList(ArrayList<UserModel>userList);
}
