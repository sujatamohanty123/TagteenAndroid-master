package in.tagteen.tagteen.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jayattama Prusty on 01-Sep-17.
 */

public class CreatePostJsonInputModel {
    @SerializedName("postCreatorId")
    @Expose
    private String postCreatorId;

    public String getPostOwnerId() {
        return postOwnerId;
    }

    public void setPostOwnerId(String postOwnerId) {
        this.postOwnerId = postOwnerId;
    }

    public int getPost_type_id() {
        return post_type_id;
    }

    public void setPost_type_id(int post_type_id) {
        this.post_type_id = post_type_id;
    }

    public String getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(String videoDuration) {
        this.videoDuration = videoDuration;
    }

    @SerializedName("video_duration")
    @Expose
    private String videoDuration;

    @SerializedName("post_owner_id")
    @Expose
    private String postOwnerId;

    public String getOwnerpostId() {
        return OwnerpostId;
    }

    public void setOwnerpostId(String ownerpostId) {
        OwnerpostId = ownerpostId;
    }

    @SerializedName("original_post_id")
    @Expose
    private String OwnerpostId;

    public String getOriginalpostDate() {
        return OriginalpostDate;
    }

    public void setOriginalpostDate(String originalpostDate) {
        OriginalpostDate = originalpostDate;
    }

    @SerializedName("original_post_time")
    @Expose
    private String OriginalpostDate;

    @SerializedName("content")
    @Expose
    private String content;

    public String getOwner_post_type_id() {
        return owner_post_type_id;
    }

    public void setOwner_post_type_id(String owner_post_type_id) {
        this.owner_post_type_id = owner_post_type_id;
    }

    public boolean isShareto() {
        return shareto;
    }

    @SerializedName("owner_post_type_id")
    @Expose
    private String owner_post_type_id;

    @SerializedName("share_to")
    @Expose
    private boolean shareto;
    @SerializedName("title ")
    @Expose
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getView_to() {
        return view_to;
    }

    public void setView_to(int view_to) {
        this.view_to = view_to;
    }

    @SerializedName("view_to")
    @Expose
    private int view_to;

    @SerializedName("image")
    @Expose
    private List<ImageDataAllToSend> image;

    public List<ImageDataAllToSend> getVideo_thumbnail() {
        return video_thumbnail;
    }

    public void setVideo_thumbnail(List<ImageDataAllToSend> video_thumbnail) {
        this.video_thumbnail = video_thumbnail;
    }

    @SerializedName("video_thumbnail")
    @Expose
    private List<ImageDataAllToSend> video_thumbnail;

    @SerializedName("post_type_id")
    @Expose
    private int post_type_id;

    @SerializedName("categorie_id")
    @Expose
    private int categorie_id;

    @SerializedName("lang_type_id")
    @Expose
    private int languageId;

    @SerializedName("video")
    @Expose
    private Videodata video;

    public Videodata getVideo() {
        return video;
    }

    public void setVideo(Videodata video) {
        this.video = video;
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

    public List<ImageDataAllToSend> getImage() {
        return image;
    }

    public void setImage(List<ImageDataAllToSend> image) {
        this.image = image;
    }

    public boolean getShareto() {
        return shareto;
    }

    public void setShareto(boolean shareto) {
        this.shareto = shareto;
    }

    public int getType() {
        return post_type_id;
    }

    public void setType(int type) {
        this.post_type_id = type;
    }

    public int getCategorie_id() {
        return categorie_id;
    }

    public void setCategorie_id(int categorie_id) {
        this.categorie_id = categorie_id;
    }

    public int getLanguageId() {
        return languageId;
    }

    public void setLanguageId(int languageId) {
        this.languageId = languageId;
    }

    public static class ImageDataAllToSend{
        @SerializedName("url")
        @Expose
        private String url;

        @SerializedName("height")
        @Expose
        private String height;

        @SerializedName("width")
        @Expose
        private String width;

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
    }

    public static class Videodata{
        @SerializedName("url")
        @Expose
        private String url;

        @SerializedName("height")
        @Expose
        private String height;

        @SerializedName("width")
        @Expose
        private String width;

        @SerializedName("video_duration")
        @Expose
        private String video_duration;

        public String getVideo_duration() {
            return video_duration;
        }

        public void setVideo_duration(String video_duration) {
            this.video_duration = video_duration;
        }

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
    }
}
