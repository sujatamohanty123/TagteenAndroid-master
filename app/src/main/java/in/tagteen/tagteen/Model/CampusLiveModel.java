package in.tagteen.tagteen.Model;

public class CampusLiveModel {
    private String itemId;
    private String content;
    private String createdBy;
    private String profilePic;

    private String postImageUrl;
    private String postVideoUrl;
    private String postVideoThumbnailUrl;
    private String postFileUrl;

    private long postedAgo;
    private int repliesCount;

    private String colorCode;

    public CampusLiveModel(String itemId) {
        this.itemId = itemId;
    }

    public String getItemId() {
        return itemId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getPostImageUrl() {
        return postImageUrl;
    }

    public void setPostImageUrl(String postImageUrl) {
        this.postImageUrl = postImageUrl;
    }

    public String getPostVideoUrl() {
        return postVideoUrl;
    }

    public void setPostVideoUrl(String postVideoUrl) {
        this.postVideoUrl = postVideoUrl;
    }

    public String getPostVideoThumbnailUrl() {
        return postVideoThumbnailUrl;
    }

    public void setPostVideoThumbnailUrl(String postVideoThumbnailUrl) {
        this.postVideoThumbnailUrl = postVideoThumbnailUrl;
    }

    public String getPostFileUrl() {
        return postFileUrl;
    }

    public void setPostFileUrl(String postFileUrl) {
        this.postFileUrl = postFileUrl;
    }

    public long getPostedAgo() {
        return postedAgo;
    }

    public void setPostedAgo(long postedAgo) {
        this.postedAgo = postedAgo;
    }

    public int getRepliesCount() {
        return repliesCount;
    }

    public void setRepliesCount(int repliesCount) {
        this.repliesCount = repliesCount;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }
}
