package in.tagteen.tagteen.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Jayattama Prusty on 01-Sep-17.
 */

public class CreatePostJsonResponseModel {
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

        @SerializedName("__v")
        @Expose
        private Integer v;
        @SerializedName("post_creator_id")
        @Expose
        private String postCreatorId;
        @SerializedName("content")
        @Expose
        private String content;
        @SerializedName("date_created")
        @Expose
        private String dateCreated;
        @SerializedName("last_date_updated")
        @Expose
        private String lastDateUpdated;
        @SerializedName("is_deleted")
        @Expose
        private Boolean isDeleted;
        @SerializedName("_id")
        @Expose
        private String id;
        @SerializedName("like")
        @Expose
        private List<Object> like = null;
        @SerializedName("image")
        @Expose
        private List<Image> image = null;
        @SerializedName("conversation")
        @Expose
        private List<Object> conversation = null;

        public Integer getV() {
            return v;
        }

        public void setV(Integer v) {
            this.v = v;
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

        public String getLastDateUpdated() {
            return lastDateUpdated;
        }

        public void setLastDateUpdated(String lastDateUpdated) {
            this.lastDateUpdated = lastDateUpdated;
        }

        public Boolean getIsDeleted() {
            return isDeleted;
        }

        public void setIsDeleted(Boolean isDeleted) {
            this.isDeleted = isDeleted;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public List<Object> getLike() {
            return like;
        }

        public void setLike(List<Object> like) {
            this.like = like;
        }

        public List<Image> getImage() {
            return image;
        }

        public void setImage(List<Image> image) {
            this.image = image;
        }

        public List<Object> getConversation() {
            return conversation;
        }

        public void setConversation(List<Object> conversation) {
            this.conversation = conversation;
        }

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
}
