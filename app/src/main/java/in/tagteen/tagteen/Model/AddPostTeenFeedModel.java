package in.tagteen.tagteen.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Jayattama Prusty on 01-Sep-17.
 */

public class AddPostTeenFeedModel {
    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("postCreatorId")
    @Expose
    private String postCreatorId;

    @SerializedName("content")
    @Expose
    private String content;

    @SerializedName("share_to")
    @Expose
    private String shareto;

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
    private List<ImageDataAllToSend> images;

    @SerializedName("post_type_id")
    @Expose
    private int post_type_id;

    @SerializedName("categorie_id")
    @Expose
    private int categorie_id;


    @SerializedName("video")
    @Expose
    private Videodata video;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPost_type_id() {
        return post_type_id;
    }

    public void setPost_type_id(int post_type_id) {
        this.post_type_id = post_type_id;
    }

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

    public List<ImageDataAllToSend> getImages() {
        return images;
    }

    public void setImage(List<ImageDataAllToSend> images) {
        this.images = images;
    }

    public String getShareto() {
        return shareto;
    }

    public void setShareto(String shareto) {
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
