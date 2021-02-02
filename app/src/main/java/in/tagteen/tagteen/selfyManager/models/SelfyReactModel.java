package in.tagteen.tagteen.selfyManager.models;

/**
 * Created by lovekushvishwakarma on 06/01/18.
 */

public class SelfyReactModel {

    String _id, timestamp, user_id;

    public SelfyReactModel(String _id, String timestamp, String user_id) {
        this._id = _id;
        this.timestamp = timestamp;
        this.user_id = user_id;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
