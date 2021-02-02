package in.tagteen.tagteen.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Mathivanan on 22-01-2018.
 */

public class ReactionInputJson {
    @SerializedName("post_id")
    @Expose
    private String post_id;

    @SerializedName("page")
    @Expose
    private int page;

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    @SerializedName("limit")
    @Expose
    private int limit;
}
