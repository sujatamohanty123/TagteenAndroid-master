package in.tagteen.tagteen.Fragments.youthtube.bean;

/**
 * Created by lovekushvishwakarma on 24/09/17.
 */

public class YouthDataItem {

    String userName,userTag,desc,videoUrl,videoThumb,userAvtar;
    int viewCount,rokCount,commentCount,shareCount,beAfnn;
    boolean isMypost,isRok;


    public YouthDataItem(String userName, String userTag, String desc, String videoUrl, String videoThumb, String userAvtar, int viewCount, int rokCount, int commentCount, int shareCount, int beAfnn, boolean isMypost,boolean isRok) {
        this.userName = userName;
        this.userTag = userTag;
        this.desc = desc;
        this.videoUrl = videoUrl;
        this.videoThumb = videoThumb;
        this.userAvtar = userAvtar;
        this.viewCount = viewCount;
        this.rokCount = rokCount;
        this.commentCount = commentCount;
        this.shareCount = shareCount;
        this.beAfnn = beAfnn;
        this.isMypost = isMypost;
        this.isRok=isRok;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserTag() {
        return userTag;
    }

    public void setUserTag(String userTag) {
        this.userTag = userTag;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoThumb() {
        return videoThumb;
    }

    public void setVideoThumb(String videoThumb) {
        this.videoThumb = videoThumb;
    }

    public String getUserAvtar() {
        return userAvtar;
    }

    public void setUserAvtar(String userAvtar) {
        this.userAvtar = userAvtar;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public int getRokCount() {
        return rokCount;
    }

    public void setRokCount(int rokCount) {
        this.rokCount = rokCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getShareCount() {
        return shareCount;
    }

    public void setShareCount(int shareCount) {
        this.shareCount = shareCount;
    }

    public int getBeAfnn() {
        return beAfnn;
    }

    public void setBeAfnn(int beAfnn) {
        this.beAfnn = beAfnn;
    }

    public boolean isMypost() {
        return isMypost;
    }

    public void setMypost(boolean mypost) {
        isMypost = mypost;
    }

    public boolean isRok() {
        return isRok;
    }

    public void setRok(boolean rok) {
        isRok = rok;
    }
}
