package in.tagteen.tagteen.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class WebshowModel {
    @SerializedName("data")
    @Expose
    private List<WebshowDetails> data = null;

    public List<WebshowDetails> getData() {
        return data;
    }

    public void setData(List<WebshowDetails> data) {
        this.data = data;
    }

    public class WebshowDetails implements Serializable {
        @SerializedName("_id")
        @Expose
        private String webshowId;

        public String getWebshowId() {
            return webshowId;
        }

        public void setWebshowId(String webshowId) {
            this.webshowId = webshowId;
        }

        @SerializedName("title")
        @Expose
        private String title;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        @SerializedName("description")
        @Expose
        private String description;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        @SerializedName("cast")
        @Expose
        private String hostedBy;

        public String getHostedBy() {
            return hostedBy;
        }

        public void setHostedBy(String hostedBy) {
            this.hostedBy = hostedBy;
        }

        @SerializedName("duration")
        @Expose
        private String duration;

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        @SerializedName("video_thumbnail")
        @Expose
        private String videoThumbnailUrl;

        public String getVideoThumbnailUrl() {
            return videoThumbnailUrl;
        }

        public void setVideoThumbnailUrl(String videoThumbnailUrl) {
            this.videoThumbnailUrl = videoThumbnailUrl;
        }

        @SerializedName("video_file")
        @Expose
        private String videoFileUrl;

        public String getVideoFileUrl() {
            return videoFileUrl;
        }

        public void setVideoFileUrl(String videoFileUrl) {
            this.videoFileUrl = videoFileUrl;
        }

        @SerializedName("season_number")
        @Expose
        private String seasonNumber;

        public String getSeasonNumber() {
            return seasonNumber;
        }

        public void setSeasonNumber(String seasonNumber) {
            this.seasonNumber = seasonNumber;
        }

        @SerializedName("webshows_type_id")
        @Expose
        private int webshowTypeId;

        public int getWebshowTypeId() {
            return webshowTypeId;
        }

        public void setWebshowTypeId(int webshowTypeId) {
            this.webshowTypeId = webshowTypeId;
        }

        @SerializedName("date_created")
        @Expose
        private long createdAt;

        public long getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(long createdAt) {
            this.createdAt = createdAt;
        }

        @SerializedName("webshow_date")
        @Expose
        private String webshowDate;

        public String getWebshowDate() {
            return webshowDate;
        }

        public void setWebshowDate(String webshowDate) {
            this.webshowDate = webshowDate;
        }

        @SerializedName("show_status")
        @Expose
        private String status;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        @SerializedName("user_id")
        @Expose
        private String userId;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        @SerializedName("token")
        @Expose
        private String token;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        @SerializedName("channelName")
        @Expose
        private String channelName;

        public String getChannelName() {
            return channelName;
        }

        public void setChannelName(String channelName) {
            this.channelName = channelName;
        }

        @SerializedName("uid")
        @Expose
        private String uid;

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        @SerializedName("user_paid")
        @Expose
        private boolean userPaid;

        public boolean isUserPaid() {
            return userPaid;
        }

        public void setUserPaid(boolean userPaid) {
            this.userPaid = userPaid;
        }
    }
}
