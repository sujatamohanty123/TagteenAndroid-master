package in.tagteen.tagteen.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Jayattama Prusty on 07-Sep-17.
 */

public class GetAllCommentList {

    @SerializedName("data")
    @Expose
    private List<Commentmodel> commentlist = null;

    public List<Commentmodel> getCommentlist() {
        return commentlist;
    }

    public void setCommentlist(List<Commentmodel> commentlist) {
        this.commentlist = commentlist;
    }
    public static class Commentmodel{

        @SerializedName("first_name")
        @Expose
        private String first_name;

        @SerializedName("last_name")
        @Expose
        private String last_name;


        @SerializedName("profile_url")
        @Expose
        private String profile_url;


        @SerializedName("email")
        @Expose
        private String email;


        @SerializedName("school_name")
        @Expose
        private String school_name;


        @SerializedName("pincode")
        @Expose
        private String pincode;

        @SerializedName("tagged_number")
        @Expose
        private String tagged_number;

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        @SerializedName("user_id")
        @Expose
        private String user_id;


        @SerializedName("user_like")
        @Expose
        private boolean user_like;


        @SerializedName("like_count")
        @Expose
        private int like_count;

        @SerializedName("comment_id")
        @Expose
        private String id;

        @SerializedName("content")
        @Expose
        private String content;

        @SerializedName("date_created")
        @Expose
        private String date_created;


        public String getFirst_name() {
            return first_name;
        }

        public void setFirst_name(String first_name) {
            this.first_name = first_name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getDate_created() {
            return date_created;
        }

        public void setDate_created(String date_created) {
            this.date_created = date_created;
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

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getSchool_name() {
            return school_name;
        }

        public void setSchool_name(String school_name) {
            this.school_name = school_name;
        }

        public String getPincode() {
            return pincode;
        }



        public String getTagged_number() {
            return tagged_number;
        }

        public void setTagged_number(String tagged_number) {
            this.tagged_number = tagged_number;
        }

        public boolean isUser_like() {
            return user_like;
        }

        public void setUser_like(boolean user_like) {
            this.user_like = user_like;
        }

        public int getLike_count() {
            return like_count;
        }

        public void setLike_count(int like_count) {
            this.like_count = like_count;
        }
    }
}
