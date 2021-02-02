package in.tagteen.tagteen.Model.knowledge;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class KnowledgePostResponse {
    @SerializedName("Status")
    @Expose
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isSuccess() {
        return this.status.equals("Success");
    }
}
