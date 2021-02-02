package in.tagteen.tagteen.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostResponseModel {
    @SerializedName("data")
    @Expose
    private GetPostResponseModel.PostDetails data = null;

    public GetPostResponseModel.PostDetails getData() {
        return data;
    }

    public void setData(GetPostResponseModel.PostDetails data) {
        this.data = data;
    }
}
