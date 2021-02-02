package in.tagteen.tagteen.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mathivanan on 08-02-2018.
 */

public class videoCreatePostJsonResponseModel {
    @SerializedName("data")
    @Expose
    private Data data;
    @SerializedName("success")
    @Expose
    private Boolean success;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public class Data {

        @SerializedName("is_selfie")
        boolean is_selfie;

        @SerializedName("categorie_id")
        @Expose
        private int categorie_id;

        @SerializedName("video_duration")
        @Expose
        private String videoDuration;


        @SerializedName("categorie_name")
        @Expose
        private String categorie_name;

        @SerializedName("post_type_id")
        @Expose
        private String post_type_id;

        @SerializedName("post_creator_id")
        @Expose
        private String postCreatorId;

        @SerializedName("first_name")
        @Expose
        private String first_name;

        @SerializedName("last_name")
        @Expose
        private String last_name;

        @SerializedName("profile_url")
        @Expose
        private String profile_url;

        public String getTagged_number() {
            return tagged_number;
        }

        public void setTagged_number(String tagged_number) {
            this.tagged_number = tagged_number;
        }

        public boolean is_selfie() {
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

        public Boolean getUser_cool() {
            return user_cool;
        }

        public void setUser_cool(Boolean user_cool) {
            this.user_cool = user_cool;
        }

        public Boolean getUser_sweg() {
            return user_sweg;
        }

        public void setUser_sweg(Boolean user_sweg) {
            this.user_sweg = user_sweg;
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

        public Integer getConversationCount() {
            return conversationCount;
        }

        public void setConversationCount(Integer conversationCount) {
            this.conversationCount = conversationCount;
        }

        @SerializedName("tagged_number")
        @Expose
        private String tagged_number;


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
        private List<Image> image = new ArrayList<>();

        public String getVideoDuration() {
            return videoDuration;
        }

        public void setVideoDuration(String videoDuration) {
            this.videoDuration = videoDuration;
        }

        public List<Image> getVideo_thumbnail() {
            return video_thumbnail;
        }

        public void setVideo_thumbnail(List<Image> video_thumbnail) {
            this.video_thumbnail = video_thumbnail;
        }

        @SerializedName("video_thumbnail")
        @Expose
        private List<Image> video_thumbnail = new ArrayList<>();

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
        @SerializedName("user_sweg")
        @Expose
        private Boolean user_sweg;
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
        @SerializedName("dab_count")
        @Expose
        private Integer dabCount;
        @SerializedName("conversation_count")
        @Expose
        private Integer conversationCount;
    }

    public class Image {

        @SerializedName("url")
        @Expose
        private String url;
        @SerializedName("height")
        @Expose
        private String height;
        @SerializedName("width")
        @Expose
        private String width;
        @SerializedName("_id")
        @Expose
        private String id;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getHeight() {
            return height;
        }

        public void setHeight(String height) {
            this.height = height;
        }

        public String getWidth() {
            return width;
        }

        public void setWidth(String width) {
            this.width = width;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

    }

    public class Video {


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
