package in.tagteen.tagteen.Model;

import android.annotation.SuppressLint;
import android.os.Parcel;
import com.felipecsl.asymmetricgridview.library.model.AsymmetricItem;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jayattama Prusty on 01-Sep-17.
 */

public class GetPostResponseModel {

  @SerializedName("data")
  @Expose
  private List<PostDetails> data = null;

  public List<PostDetails> getData() {
    return data;
  }

  public void setData(List<PostDetails> data) {
    this.data = data;
  }

  public static class PostDetails implements Serializable {
    @SerializedName("is_selfie")
    boolean is_selfie;

    public int getView_count() {
      return view_count;
    }

    public void setView_count(int view_count) {
      this.view_count = view_count;
    }

    @SerializedName("view_count")
    @Expose
    private int view_count;

    public String getVideo_duration() {
      return video_duration != null ? video_duration : "0";
    }

    public void setVideo_duration(String video_duration) {
      this.video_duration = video_duration;
    }

    @SerializedName("video_duration")
    @Expose
    private String video_duration;

    @SerializedName("categorie_id")
    @Expose
    private int categorie_id;

    @SerializedName("categorie_name")
    @Expose
    private String categorie_name;

    public boolean getShareTo() {
      return shareTo;
    }

    public void setShareTo(boolean shareTo) {
      this.shareTo = shareTo;
    }

    @SerializedName("share_to")
    @Expose
    private boolean shareTo;

    @SerializedName("post_type_id")
    @Expose
    private String post_type_id;

    public int getView_to() {
      return view_to;
    }

    public void setView_to(int view_to) {
      this.view_to = view_to;
    }

    @SerializedName("view_to")
    @Expose
    private int view_to;

    public String getOwner_post_type_id() {
      return owner_post_type_id;
    }

    public void setOwner_post_type_id(String owner_post_type_id) {
      this.owner_post_type_id = owner_post_type_id;
    }

    @SerializedName("owner_post_type_id")
    @Expose
    private String owner_post_type_id;

    public String getOwner_post_id() {
      return owner_post_id;
    }

    public void setOwner_post_id(String owner_post_id) {
      this.owner_post_id = owner_post_id;
    }

    @SerializedName("original_post_id")
    @Expose
    private String owner_post_id;

    public String getOriginal_post_date() {
      return original_post_date;
    }

    public void setOriginal_post_date(String original_post_date) {
      this.original_post_date = original_post_date;
    }

    @SerializedName("original_post_time")
    @Expose
    private String original_post_date;

    @SerializedName("post_creator_id")
    @Expose
    private String postCreatorId;

    @SerializedName("owner_first_name")
    @Expose
    private String owner_first_name;

    @SerializedName("owner_last_name")
    @Expose
    private String owner_last_name;

    @SerializedName("owner_profile_url")
    @Expose
    private String owner_profile_url;

    private boolean isWebShow;

    public boolean isWebShow() {
      return isWebShow;
    }

    public void setWebShow(boolean webShow) {
      isWebShow = webShow;
    }

    public boolean isShareTo() {
      return shareTo;
    }

    public String getOwner_first_name() {
      return owner_first_name;
    }

    public void setOwner_first_name(String owner_first_name) {
      this.owner_first_name = owner_first_name;
    }

    public String getOwner_last_name() {
      return owner_last_name;
    }

    public void setOwner_last_name(String owner_last_name) {
      this.owner_last_name = owner_last_name;
    }

    public String getOwner_profile_url() {
      return owner_profile_url;
    }

    public void setOwner_profile_url(String owner_profile_url) {
      this.owner_profile_url = owner_profile_url;
    }

    public String getOwner_post_creator_id() {
      return owner_post_creator_id;
    }

    public void setOwner_post_creator_id(String owner_post_creator_id) {
      this.owner_post_creator_id = owner_post_creator_id;
    }

    @SerializedName("owner_post_creator_id")
    @Expose
    private String owner_post_creator_id;

    public String getOwner_tagged_number() {
      return owner_tagged_number;
    }

    public void setOwner_tagged_number(String owner_tagged_number) {
      this.owner_tagged_number = owner_tagged_number;
    }

    @SerializedName("owner_tagged_number")
    @Expose
    private String owner_tagged_number;

    @SerializedName("first_name")
    @Expose
    private String first_name;

    @SerializedName("last_name")
    @Expose
    private String last_name;

    @SerializedName("profile_url")
    @Expose
    private String profile_url;

    @SerializedName("tagged_number")
    @Expose
    private String tagged_number;

    @SerializedName("content")
    @Expose
    private String content;

    @SerializedName("is_my_fan")
    @Expose
    private boolean isMyFan;

    //@SerializedName("title")
    //@Expose
    //private String title;
    @SerializedName("date_created")
    @Expose
    private long dateCreated;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("image")
    @Expose
    private List<Image> image = new ArrayList<>();

    //public String getTitle() {
    //  return title;
    //}

    public List<Image> getVideoThumbnails() {
      return videoThumbnails;
    }

    public void setVideoThumbnails(List<Image> videoThumbnails) {
      this.videoThumbnails = videoThumbnails;
    }

    public Boolean getUser_swag() {
      return user_swag;
    }

    public void setUser_swag(Boolean user_swag) {
      this.user_swag = user_swag;
    }

    @SerializedName("video_thumbnail")
    @Expose
    private List<Image> videoThumbnails = new ArrayList<>();

    @SerializedName("video")
    @Expose
    private Video video;
    @SerializedName("user_like")
    @Expose
    private Boolean userLike;
    @SerializedName("like_count")
    @Expose
    private Integer likeCount;

    @SerializedName("user_cool")
    @Expose
    private Boolean user_cool;
    @SerializedName("user_swag")
    @Expose
    private Boolean user_swag;

    public Boolean getUser_sweg() {
      return user_swag;
    }

    public void setUser_sweg(Boolean user_sweg) {
      this.user_swag = user_sweg;
    }

    public Boolean getUser_nerd() {
      return user_nerd;
    }

    public void setUser_nerd(Boolean user_nerd) {
      this.user_nerd = user_nerd;
    }

    public Boolean getUser_dab() {
      return user_dab;
    }

    public void setUser_dab(Boolean user_dab) {
      this.user_dab = user_dab;
    }

    @SerializedName("user_nerd")
    @Expose
    private Boolean user_nerd;
    @SerializedName("user_dab")
    @Expose
    private Boolean user_dab;

    @SerializedName("cool_count")
    @Expose
    private Integer coolCount;
    @SerializedName("swag_count")
    @Expose
    private Integer swegCount;
    @SerializedName("nerd_count")
    @Expose
    private Integer nerdCount;

    public int getAction_flag() {
      return action_flag;
    }

    public void setAction_flag(int action_flag) {
      this.action_flag = action_flag;
    }

    private int action_flag = 0;

    public boolean is_selfie() {
      return is_selfie;
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

    @SerializedName("dab_count")
    @Expose
    private Integer dabCount;
    @SerializedName("conversation_count")
    @Expose
    private Integer conversationCount;

    public boolean isIs_selfie() {
      return is_selfie;
    }

    public void setIs_selfie(boolean is_selfie) {
      this.is_selfie = is_selfie;
    }

    public int getCategorie_id() {
      return categorie_id;
    }

    public void setCategorie_id(int categorie_id) {
      this.categorie_id = categorie_id;
    }

    public String getCategorie_name() {
      return categorie_name;
    }

    public void setCategorie_name(String categorie_name) {
      this.categorie_name = categorie_name;
    }

    public String getPost_type_id() {
      return post_type_id;
    }

    public void setPost_type_id(String post_type_id) {
      this.post_type_id = post_type_id;
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

    public long getDateCreated() {
      return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
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

    public Video getVideo() {
      return video;
    }

    public void setVideo(Video video) {
      this.video = video;
    }

    public Boolean getUserLike() {
      return userLike;
    }

    public void setUserLike(Boolean userLike) {
      this.userLike = userLike;
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

    public Integer getConversationCount() {
      return conversationCount;
    }

    public void setConversationCount(Integer conversationCount) {
      this.conversationCount = conversationCount;
    }

    public Boolean getUser_cool() {
      return user_cool;
    }

    public void setUser_cool(Boolean user_cool) {
      this.user_cool = user_cool;
    }

    public String getFirst_name() {
      return first_name;
    }

    public void setFirst_name(String first_name) {
      this.first_name = first_name;
    }

    public String getLast_name() {
      return last_name;
    }

    public void setLast_name(String last_name) {
      this.last_name = last_name;
    }

    public String getProfile_url() {
      return profile_url;
    }

    public void setProfile_url(String profile_url) {
      this.profile_url = profile_url;
    }

    public String getTagged_number() {
      return tagged_number;
    }

    public void setTagged_number(String tagged_number) {
      this.tagged_number = tagged_number;
    }

    public boolean isMyFan() {
      return isMyFan;
    }

    public void setMyFan(boolean myFan) {
      isMyFan = myFan;
    }

    public class Image implements Serializable {

      @SerializedName("url")
      @Expose
      private String url;
      @SerializedName("_id")
      @Expose
      private String id;

      @SerializedName("height")
      @Expose
      private int width;

      @SerializedName("width")
      @Expose
      private int height;

      public Image(String url) {
        this.url = url;
      }

      public Image() {

      }

      public String getUrl() {
        return url;
      }

      public void setUrl(String url) {
        this.url = url;
      }

      public String getId() {
        return id;
      }

      public void setId(String id) {
        this.id = id;
      }

      public int getWidth() {
        return width;
      }

      public void setWidth(int width) {
        this.width = width;
      }

      public int getHeight() {
        return height;
      }

      public void setHeight(int height) {
        this.height = height;
      }
    }

    public class Video implements Serializable {
      @SerializedName("url")
      @Expose
      private String url;
      @SerializedName("_id")
      @Expose
      private String id;

      @SerializedName("height")
      @Expose
      private int width;

      @SerializedName("width")
      @Expose
      private int height;

      public String getUrl() {
        return url;
      }

      public void setUrl(String url) {
        this.url = url;
      }

      public String getId() {
        return id;
      }

      public void setId(String id) {
        this.id = id;
      }

      public int getWidth() {
        return width;
      }

      public void setWidth(int width) {
        this.width = width;
      }

      public int getHeight() {
        return height;
      }

      public void setHeight(int height) {
        this.height = height;
      }
    }
  }

  public static class LikeModel implements Serializable {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("timestamp")
    @Expose
    private String timestamp;
    @SerializedName("user_id")
    @Expose
    private UserId userId;

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getTimestamp() {
      return timestamp;
    }

    public void setTimestamp(String timestamp) {
      this.timestamp = timestamp;
    }

    public UserId getUserId() {
      return userId;
    }

    public void setUserId(UserId userId) {
      this.userId = userId;
    }

    public class UserId {

      @SerializedName("_id")
      @Expose
      private String id;
      @SerializedName("first_name")
      @Expose
      private String firstName = "";
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

      public String getId() {
        return id;
      }

      public void setId(String id) {
        this.id = id;
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
    }
  }
}
