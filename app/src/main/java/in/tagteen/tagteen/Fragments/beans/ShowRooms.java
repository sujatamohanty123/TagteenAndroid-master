
package in.tagteen.tagteen.Fragments.beans;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ShowRooms implements Serializable {

    @SerializedName("categorie_id")
    @Expose
    private Integer categorieId;
    @SerializedName("categorie_name")
    @Expose
    private String categorieName;
    @SerializedName("view_count")
    @Expose
    private Integer viewCount;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("profile_url")
    @Expose
    private String profileUrl;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("school_name")
    @Expose
    private String schoolName;
    @SerializedName("pincode")
    @Expose
    private String pincode;
    @SerializedName("tagged_number")
    @Expose
    private String taggedNumber;
    @SerializedName("post_creator_id")
    @Expose
    private String postCreatorId;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("date_created")
    @Expose
    private String dateCreated;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("image")
    @Expose
    private List<Image> image = null;

    public List<Image> getVideo_thumbnail() {
        return video_thumbnail;
    }

    public void setVideo_thumbnail(List<Image> video_thumbnail) {
        this.video_thumbnail = video_thumbnail;
    }

    @SerializedName("video_thumbnail")
    @Expose
    private List<Image> video_thumbnail = null;
    @SerializedName("post_type_id")
    @Expose
    private Integer postTypeId;
    @SerializedName("is_selfie")
    @Expose
    private Boolean isSelfie;
    @SerializedName("video")
    @Expose
    private Video video;
    @SerializedName("conversation_count")
    @Expose
    private Integer conversationCount;
    @SerializedName("user_like")
    @Expose
    private Boolean userLike;
    @SerializedName("user_cool")
    @Expose
    private Boolean userCool;
    @SerializedName("user_sweg")
    @Expose
    private Boolean userSweg;
    @SerializedName("user_nerd")
    @Expose
    private Boolean userNerd;
    @SerializedName("user_dab")
    @Expose
    private Boolean userDab;
    @SerializedName("like_count")
    @Expose
    private Integer likeCount;
    @SerializedName("cool_count")
    @Expose
    private Integer coolCount;
    @SerializedName("sweg_count")
    @Expose
    private Integer swegCount;
    @SerializedName("nerd_count")
    @Expose
    private Integer nerdCount;
    @SerializedName("dab_count")
    @Expose
    private Integer dabCount;

    public Integer getCategorieId() {
        return categorieId;
    }

    public void setCategorieId(Integer categorieId) {
        this.categorieId = categorieId;
    }

    public String getCategorieName() {
        return categorieName;
    }

    public void setCategorieName(String categorieName) {
        this.categorieName = categorieName;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getTaggedNumber() {
        return taggedNumber;
    }

    public void setTaggedNumber(String taggedNumber) {
        this.taggedNumber = taggedNumber;
    }

    public String getPostCreatorId() {
        return postCreatorId;
    }

    public void setPostCreatorId(String postCreatorId) {
        this.postCreatorId = postCreatorId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Image> getImage() {
        return image;
    }

    public void setImage(List<Image> image) {
        this.image = image;
    }

    public Integer getPostTypeId() {
        return postTypeId;
    }

    public void setPostTypeId(Integer postTypeId) {
        this.postTypeId = postTypeId;
    }

    public Boolean getIsSelfie() {
        return isSelfie;
    }

    public void setIsSelfie(Boolean isSelfie) {
        this.isSelfie = isSelfie;
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    public Integer getConversationCount() {
        return conversationCount;
    }

    public void setConversationCount(Integer conversationCount) {
        this.conversationCount = conversationCount;
    }

    public Boolean getUserLike() {
        return userLike;
    }

    public void setUserLike(Boolean userLike) {
        this.userLike = userLike;
    }

    public Boolean getUserCool() {
        return userCool;
    }

    public void setUserCool(Boolean userCool) {
        this.userCool = userCool;
    }

    public Boolean getUserSweg() {
        return userSweg;
    }

    public void setUserSweg(Boolean userSweg) {
        this.userSweg = userSweg;
    }

    public Boolean getUserNerd() {
        return userNerd;
    }

    public void setUserNerd(Boolean userNerd) {
        this.userNerd = userNerd;
    }

    public Boolean getUserDab() {
        return userDab;
    }

    public void setUserDab(Boolean userDab) {
        this.userDab = userDab;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getCoolCount() {
        return coolCount;
    }

    public void setCoolCount(Integer coolCount) {
        this.coolCount = coolCount;
    }

    public Integer getSwegCount() {
        return swegCount;
    }

    public void setSwegCount(Integer swegCount) {
        this.swegCount = swegCount;
    }

    public Integer getNerdCount() {
        return nerdCount;
    }

    public void setNerdCount(Integer nerdCount) {
        this.nerdCount = nerdCount;
    }

    public Integer getDabCount() {
        return dabCount;
    }

    public void setDabCount(Integer dabCount) {
        this.dabCount = dabCount;
    }

}
