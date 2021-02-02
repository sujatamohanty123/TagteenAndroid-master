package in.tagteen.tagteen.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserURockInputJson {
    @SerializedName("user_id")
    @Expose
    private String userId;

    @SerializedName("api_select")
    @Expose
    private int apiSelect;

    public UserURockInputJson() {
        this.apiSelect = 1;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getApiSelect() {
        return apiSelect;
    }

    public void setApiSelect(int apiSelect) {
        this.apiSelect = apiSelect;
    }
}
