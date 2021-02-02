package in.tagteen.tagteen.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ShoutOutModel {
    @SerializedName("Status")
    @Expose
    private Boolean success;

    @SerializedName("ShoutText")
    @Expose
    private String shoutText;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getShoutText() {
        return shoutText;
    }

    public void setShoutText(String shoutText) {
        this.shoutText = shoutText;
    }
}
