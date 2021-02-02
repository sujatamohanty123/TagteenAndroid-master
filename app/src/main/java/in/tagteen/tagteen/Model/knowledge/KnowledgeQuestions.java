package in.tagteen.tagteen.Model.knowledge;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class KnowledgeQuestions {
    @SerializedName("Status")
    @Expose
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isSuccess() {
        return this.status.equals("Success");
    }

    @SerializedName("Questions")
    @Expose
    private List<Question> questions;

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public class Question implements Serializable {
        @SerializedName("quest_id")
        @Expose
        private String questionId;

        public String getQuestionId() {
            return questionId;
        }

        public void setQuestionId(String questionId) {
            this.questionId = questionId;
        }

        @SerializedName("q_user_first_name")
        @Expose
        private String firstName;

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        @SerializedName("q_user_last_name")
        @Expose
        private String lastName;

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        @SerializedName("q_user_pic")
        @Expose
        private String profilePic;

        public String getProfilePic() {
            return profilePic;
        }

        public void setProfilePic(String profilePic) {
            this.profilePic = profilePic;
        }

        @SerializedName("q_user_id")
        @Expose
        private String userId;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        @SerializedName("q_title")
        @Expose
        private String title;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        @SerializedName("q_thumbnail")
        @Expose
        private String videoThumbnail;

        public String getVideoThumbnail() {
            return videoThumbnail;
        }

        public void setVideoThumbnail(String videoThumbnail) {
            this.videoThumbnail = videoThumbnail;
        }

        @SerializedName("q_media_link")
        @Expose
        private String videoUrl;

        public String getVideoUrl() {
            return videoUrl;
        }

        public void setVideoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
        }

        @SerializedName("q_aaded_time")
        @Expose
        private String createdOn;

        public String getCreatedOn() {
            return createdOn;
        }

        public void setCreatedOn(String createdOn) {
            this.createdOn = createdOn;
        }

        @SerializedName("cat_id")
        @Expose
        private String categoryId;

        public String getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(String categoryId) {
            this.categoryId = categoryId;
        }

        @SerializedName("View_count")
        @Expose
        private int viewCount;

        public int getViewCount() {
            return viewCount;
        }

        public void setViewCount(int viewCount) {
            this.viewCount = viewCount;
        }

        @SerializedName("answers")
        @Expose
        private List<KnowledgeAnswer> answers;

        public List<KnowledgeAnswer> getAnswers() {
            return answers;
        }

        public void setAnswers(List<KnowledgeAnswer> answers) {
            this.answers = answers;
        }
    }
}
