package in.tagteen.tagteen.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Sujata on 08-06-2018.
 */
public class DeleteCommentModel {
    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    @SerializedName("comment_id")
    @Expose
    private String comment_id;
}
