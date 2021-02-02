package in.tagteen.tagteen.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Sujata on 22-06-2018.
 */
public class NotificationModel {
    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public boolean getNotification() {
        return notification;
    }

    public void setNotification(boolean notification) {
        this.notification = notification;
    }

    @SerializedName("user_id")
    @Expose
    private String user_id;

    @SerializedName("notification")
    @Expose
    private boolean notification;
}
