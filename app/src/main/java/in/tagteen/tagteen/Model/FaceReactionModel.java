package in.tagteen.tagteen.Model;


public class FaceReactionModel {
    private String id;
    private String reactionName;
    private String reactionLocalPath;
    private String reactionURL;

 public FaceReactionModel(String id, String reactionName, String reactionLocalPath, String reactionURL){
    this.id=id;
    this.reactionName=reactionName;
    this.reactionLocalPath=reactionLocalPath;
    this.reactionURL=reactionURL;
}

    public FaceReactionModel() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReactionName() {
        return reactionName;
    }

    public void setReactionName(String reactionName) {
        this.reactionName = reactionName;
    }

    public String getReactionLocalPath() {
        return reactionLocalPath;
    }

    public void setReactionLocalPath(String reactionLocalPath) {
        this.reactionLocalPath = reactionLocalPath;
    }

    public String getReactionURL() {
        return reactionURL;
    }

    public void setReactionURL(String reactionURL) {
        this.reactionURL = reactionURL;
    }



}
