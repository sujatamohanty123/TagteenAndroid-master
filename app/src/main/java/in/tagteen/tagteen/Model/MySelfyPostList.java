package in.tagteen.tagteen.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Jayattama Prusty on 04-Sep-17.
 */

public class MySelfyPostList {

    @SerializedName("success")
    @Expose
    public boolean success;

    @SerializedName("data")
    @Expose
    public List<SelfyData> selfyData;

    public class SelfyData {

        @SerializedName("_id")
        @Expose
        public String _id;

        @SerializedName("post_creator_id")
        @Expose
        public String post_creator_id;

        @SerializedName("title")
        @Expose
        public String title;

        @SerializedName("date_created")
        @Expose
        public String date_created;

        @SerializedName("last_date_updated")
        @Expose
        public String last_date_updated;

        @SerializedName("is_deleted")
        @Expose
        public boolean is_deleted;

        @SerializedName("post_type_id")
        @Expose
        public int post_type_id;

        @SerializedName("is_selfie")
        @Expose
        public boolean is_selfie;

        @SerializedName("video")
        @Expose
        public List<VideoData> video;

        public class VideoData{
            @SerializedName("url")
            @Expose
            public int url;

            @SerializedName("height")
            @Expose
            public int height;

            @SerializedName("width")
            @Expose
            public int width;
        }

        @SerializedName("image")
        @Expose
        public List<ImageData> image;

        public class ImageData{
            @SerializedName("url")
            @Expose
            public int url;

            @SerializedName("height")
            @Expose
            public int height;

            @SerializedName("width")
            @Expose
            public int width;

            @SerializedName("_id")
            @Expose
            public int _id;

        }



        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getPost_creator_id() {
            return post_creator_id;
        }

        public void setPost_creator_id(String post_creator_id) {
            this.post_creator_id = post_creator_id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDate_created() {
            return date_created;
        }

        public void setDate_created(String date_created) {
            this.date_created = date_created;
        }

        public String getLast_date_updated() {
            return last_date_updated;
        }

        public void setLast_date_updated(String last_date_updated) {
            this.last_date_updated = last_date_updated;
        }

        public boolean isIs_deleted() {
            return is_deleted;
        }

        public void setIs_deleted(boolean is_deleted) {
            this.is_deleted = is_deleted;
        }

        public int getPost_type_id() {
            return post_type_id;
        }

        public void setPost_type_id(int post_type_id) {
            this.post_type_id = post_type_id;
        }

        public boolean isIs_selfie() {
            return is_selfie;
        }

        public void setIs_selfie(boolean is_selfie) {
            this.is_selfie = is_selfie;
        }

        public List<VideoData> getVideo() {
            return video;
        }

        public void setVideo(List<VideoData> video) {
            this.video = video;
        }

        public List<ImageData> getImage() {
            return image;
        }

        public void setImage(List<ImageData> image) {
            this.image = image;
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<SelfyData> getSelfyData() {
        return selfyData;
    }

    public void setSelfyData(List<SelfyData> selfyData) {
        this.selfyData = selfyData;
    }
}
