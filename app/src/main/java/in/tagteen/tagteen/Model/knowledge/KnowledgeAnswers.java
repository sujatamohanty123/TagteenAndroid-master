package in.tagteen.tagteen.Model.knowledge;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class KnowledgeAnswers {
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

    @SerializedName("Answers")
    @Expose
    private List<KnowledgeAnswer> answers;

    public List<KnowledgeAnswer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<KnowledgeAnswer> answers) {
        this.answers = answers;
    }
}
