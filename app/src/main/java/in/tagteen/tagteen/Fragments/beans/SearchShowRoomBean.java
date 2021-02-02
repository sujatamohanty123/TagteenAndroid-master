
package in.tagteen.tagteen.Fragments.beans;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchShowRoomBean {

    @SerializedName("data")
    @Expose
    private List<ShowRooms> data = null;
    @SerializedName("success")
    @Expose
    private Boolean success;

    public List<ShowRooms> getData() {
        return data;
    }

    public void setData(List<ShowRooms> data) {
        this.data = data;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

}
