package in.tagteen.tagteen.teenfeed.model;

/**
 * Created by lovekushvishwakarma on 10/12/17.
 */

public class SearchArticleModel {

    String categorie_id, categorie_name, content, date_created, email, first_name, image, last_name, pincode, post_creator_id, post_type_id, profile_url,
            school_name, tagged_number, user_cool, _id;
    boolean is_selfie, user_like;
    int conversation_count, cool_count, like_count;

    public SearchArticleModel(String categorie_id, String categorie_name, String content, String date_created, String email, String first_name, String image, String last_name, String pincode, String post_creator_id, String post_type_id, String profile_url, String school_name, String tagged_number, String user_cool, String _id, boolean is_selfie, boolean user_like, int conversation_count, int cool_count, int like_count) {
        this.categorie_id = categorie_id;
        this.categorie_name = categorie_name;
        this.content = content;
        this.date_created = date_created;
        this.email = email;
        this.first_name = first_name;
        this.image = image;
        this.last_name = last_name;
        this.pincode = pincode;
        this.post_creator_id = post_creator_id;
        this.post_type_id = post_type_id;
        this.profile_url = profile_url;
        this.school_name = school_name;
        this.tagged_number = tagged_number;
        this.user_cool = user_cool;
        this._id = _id;
        this.is_selfie = is_selfie;
        this.user_like = user_like;
        this.conversation_count = conversation_count;
        this.cool_count = cool_count;
        this.like_count = like_count;
    }

    public String getCategorie_id() {
        return categorie_id;
    }

    public void setCategorie_id(String categorie_id) {
        this.categorie_id = categorie_id;
    }

    public String getCategorie_name() {
        return categorie_name;
    }

    public void setCategorie_name(String categorie_name) {
        this.categorie_name = categorie_name;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getPost_creator_id() {
        return post_creator_id;
    }

    public void setPost_creator_id(String post_creator_id) {
        this.post_creator_id = post_creator_id;
    }

    public String getPost_type_id() {
        return post_type_id;
    }

    public void setPost_type_id(String post_type_id) {
        this.post_type_id = post_type_id;
    }

    public String getProfile_url() {
        return profile_url;
    }

    public void setProfile_url(String profile_url) {
        this.profile_url = profile_url;
    }

    public String getSchool_name() {
        return school_name;
    }

    public void setSchool_name(String school_name) {
        this.school_name = school_name;
    }

    public String getTagged_number() {
        return tagged_number;
    }

    public void setTagged_number(String tagged_number) {
        this.tagged_number = tagged_number;
    }

    public String getUser_cool() {
        return user_cool;
    }

    public void setUser_cool(String user_cool) {
        this.user_cool = user_cool;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public boolean isIs_selfie() {
        return is_selfie;
    }

    public void setIs_selfie(boolean is_selfie) {
        this.is_selfie = is_selfie;
    }

    public boolean isUser_like() {
        return user_like;
    }

    public void setUser_like(boolean user_like) {
        this.user_like = user_like;
    }

    public int getConversation_count() {
        return conversation_count;
    }

    public void setConversation_count(int conversation_count) {
        this.conversation_count = conversation_count;
    }

    public int getCool_count() {
        return cool_count;
    }

    public void setCool_count(int cool_count) {
        this.cool_count = cool_count;
    }

    public int getLike_count() {
        return like_count;
    }

    public void setLike_count(int like_count) {
        this.like_count = like_count;
    }




}
