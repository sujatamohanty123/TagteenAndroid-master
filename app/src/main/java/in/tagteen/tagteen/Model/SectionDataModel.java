package in.tagteen.tagteen.Model;



import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SectionDataModel implements Serializable{
    private String postid;
    private String post_type_id;
    private  int view_to;
    private String post_creator_name;
    private String posted_creator_tagged_number;
    private String post_creator_profilepic;
    private long post_created_date_time;
    private String owner_post_creator_id;
    private String owner_post_id;
    private String owner_tagged_number;

    private String original_post_date;
    private String owner_post_creator_profilepic;
    private String owner_post_creator_name;
    private boolean share_to;
    private int action_flag=0;

    private String text_description;
    private String posted_header_name;
    private String headerTitle;
    private String category;
    private int view_count;

    private int category_id;
    private String post_userid;
    private String post_video_url;
    private String owner_post_type_id;

    private String post_video_id;
    private int post_video_height;
    private int post_video_width;

    private boolean isSelected;
    private boolean userLike;

    @SerializedName("like")
    @Expose
    private List<LikeModel> like = null;

    @SerializedName("cool")
    @Expose
    private List<LikeModel> cool = null;
    private boolean isPost;
    private int likecount;
    private int coolcount;
    private int swegcount;
    private int nerdcount;
    private int dabcount;
    private int commentcount;
    private int reactsCount;

    private List<String> post_image_createdby_creator_height = new ArrayList<>();
    private List<String> post_image_createdby_creator_weidth = new ArrayList<>();
    private List<String> post_video_thumb_createdby_creator = new ArrayList<>();
    private List<String> post_video_thumb_createdby_creator_height = new ArrayList<>();
    private List<String> post_video_thumb_createdby_creator_weidth = new ArrayList<>();
    private ArrayList<SingleItemModel> allItemsInSection;

    public SectionDataModel() {

    }

    public SectionDataModel(String headerTitle, ArrayList<SingleItemModel> allItemsInSection, boolean isPost) {
        this.headerTitle = headerTitle;
        this.allItemsInSection = allItemsInSection;
        this.isPost=isPost;
    }

    public String getPost_type_id() {
        return post_type_id;
    }

    public void setPost_type_id(String post_type_id) {
        this.post_type_id = post_type_id;
    }

    public int getView_to() {
        return view_to;
    }

    public void setView_to(int view_to) {
        this.view_to = view_to;
    }

    public String getOwner_post_creator_id() {
        return owner_post_creator_id;
    }

    public void setOwner_post_creator_id(String owner_post_creator_id) {
        this.owner_post_creator_id = owner_post_creator_id;
    }

    public String getOwner_post_creator_profilepic() {
        return owner_post_creator_profilepic;
    }

    public void setOwner_post_creator_profilepic(String owner_post_creator_profilepic) {
        this.owner_post_creator_profilepic = owner_post_creator_profilepic;
    }

    public String getOwner_post_creator_name() {
        return owner_post_creator_name;
    }

    public void setOwner_post_creator_name(String owner_post_creator_name) {
        this.owner_post_creator_name = owner_post_creator_name;
    }

    public String getOwner_post_id() {
        return owner_post_id;
    }

    public void setOwner_post_id(String owner_post_id) {
        this.owner_post_id = owner_post_id;
    }

    public String getOriginal_post_date() {
        return original_post_date;
    }

    public void setOriginal_post_date(String original_post_date) {
        this.original_post_date = original_post_date;
    }

    public String getOwner_tagged_number() {
        return owner_tagged_number;
    }

    public void setOwner_tagged_number(String owner_tagged_number) {
        this.owner_tagged_number = owner_tagged_number;
    }

    public boolean isShare_to() {
        return share_to;
    }

    public void setShare_to(boolean share_to) {
        this.share_to = share_to;
    }

    public int getView_count() {
        return view_count;
    }

    public void setView_count(int view_count) {
        this.view_count = view_count;
    }

    public int getAction_flag() {
        return action_flag;
    }

    public void setAction_flag(int action_flag) {
        this.action_flag = action_flag;
    }

    private List<String> post_image_createdby_creator_url=new ArrayList<>();

    public List<String> getPost_image_createdby_creator_height() {
        return post_image_createdby_creator_height;
    }

    public void setPost_image_createdby_creator_height(List<String> post_image_createdby_creator_height) {
        this.post_image_createdby_creator_height = post_image_createdby_creator_height;
    }

    public List<String> getPost_image_createdby_creator_weidth() {
        return post_image_createdby_creator_weidth;
    }

    public void setPost_image_createdby_creator_weidth(List<String> post_image_createdby_creator_weidth) {
        this.post_image_createdby_creator_weidth = post_image_createdby_creator_weidth;
    }

    public List<String> getPost_video_thumb_createdby_creator_height() {
        return post_video_thumb_createdby_creator_height;
    }

    public void setPost_video_thumb_createdby_creator_height(List<String> post_video_thumb_createdby_creator_height) {
        this.post_video_thumb_createdby_creator_height = post_video_thumb_createdby_creator_height;
    }

    public List<String> getPost_video_thumb_createdby_creator_weidth() {
        return post_video_thumb_createdby_creator_weidth;
    }

    public void setPost_video_thumb_createdby_creator_weidth(List<String> post_video_thumb_createdby_creator_weidth) {
        this.post_video_thumb_createdby_creator_weidth = post_video_thumb_createdby_creator_weidth;
    }

    public List<String> getPost_video_thumb_createdby_creator() {
        return post_video_thumb_createdby_creator;
    }

    public void setPost_video_thumb_createdby_creator(List<String> post_video_thumb_createdby_creator) {
        this.post_video_thumb_createdby_creator = post_video_thumb_createdby_creator;
    }

    public int getViewCount() {
        return view_count;
    }

    public void setViewCount(int view_count) {
        this.view_count = view_count;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getOwner_post_type_id() {
        return owner_post_type_id;
    }

    public void setOwner_post_type_id(String owner_post_type_id) {
        this.owner_post_type_id = owner_post_type_id;
    }

    public String getPost_video_id() {
        return post_video_id;
    }

    public void setPost_video_id(String post_video_id) {
        this.post_video_id = post_video_id;
    }

    public String getPost_video_url() {
        return post_video_url;
    }

    public void setPost_video_url(String post_video_url) {
        this.post_video_url = post_video_url;
    }

    public int getPost_video_height() {
        return post_video_height;
    }

    public void setPost_video_height(int post_video_height) {
        this.post_video_height = post_video_height;
    }

    public int getPost_video_width() {
        return post_video_width;
    }

    public void setPost_video_width(int post_video_width) {
        this.post_video_width = post_video_width;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getSwegcount() {
        return swegcount;
    }

    public void setSwegcount(int swegcount) {
        this.swegcount = swegcount;
    }

    public int getNerdcount() {
        return nerdcount;
    }

    public void setNerdcount(int nerdcount) {
        this.nerdcount = nerdcount;
    }

    public int getDabcount() {
        return dabcount;
    }

    public void setDabcount(int dabcount) {
        this.dabcount = dabcount;
    }

    public int getReactsCount() {
        return reactsCount;
    }

    public void setReactsCount(int reactsCount) {
        this.reactsCount = reactsCount;
    }
    public void addReactsCount(int count) {
        this.reactsCount += count;
    }

    public String getPost_userid() {
        return post_userid;
    }

    public void setPost_userid(String post_userid) {
        this.post_userid = post_userid;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPost(boolean post) {
        isPost = post;
    }

    public boolean isPost() {
        return isPost;
    }

    public void setIsPost(boolean isPost) {
        this.isPost = isPost;
    }


    public String getHeaderTitle() {
        return headerTitle;
    }

    public void setHeaderTitle(String headerTitle) {
        this.headerTitle = headerTitle;
    }

    public ArrayList<SingleItemModel> getAllItemsInSection() {
        return allItemsInSection;
    }

    public void setAllItemsInSection(ArrayList<SingleItemModel> allItemsInSection) {
        this.allItemsInSection = allItemsInSection;
    }

    public String getText_description() {
        return text_description != null ? text_description : "";
    }

    public void setText_description(String text_description) {
        this.text_description = text_description;
    }

    public String getPosted_header_name() {
        return posted_header_name;
    }

    public void setPosted_header_name(String posted_header_name) {
        this.posted_header_name = posted_header_name;
    }

    public String getPost_creator_name() {
        return post_creator_name;
    }

    public void setPost_creator_name(String post_creator_name) {
        this.post_creator_name = post_creator_name;
    }

    public String getPosted_creator_tagged_number() {
        return posted_creator_tagged_number;
    }

    public void setPosted_creator_tagged_number(String posted_creator_tagged_number) {
        this.posted_creator_tagged_number = posted_creator_tagged_number;
    }

    public String getPost_creator_profilepic() {
        return post_creator_profilepic;
    }

    public void setPost_creator_profilepic(String post_creator_profilepic) {
        this.post_creator_profilepic = post_creator_profilepic;
    }

    public long getPost_created_date_time() {
        return post_created_date_time;
    }

    public void setPost_created_date_time(long post_created_date_time) {
        this.post_created_date_time = post_created_date_time;
    }

    public List<String> getPost_image_createdby_creator_url() {
        return post_image_createdby_creator_url;
    }

    public void setPost_image_createdby_creator_url(List<String> post_image_createdby_creator) {
        this.post_image_createdby_creator_url = post_image_createdby_creator;
    }


    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public List<LikeModel> getLike() {
        return like;
    }

    public void setLike(List<LikeModel> like) {
        this.like = like;
    }

    public List<LikeModel> getCool() {
        return cool;
    }

    public void setCool(List<LikeModel> cool) {
        this.cool = cool;
    }

    public int getLikecount() {
        return likecount;
    }

    public void setLikecount(int likecount) {
        this.likecount = likecount;
    }

    public int getCoolcount() {
        return coolcount;
    }

    public void setCoolcount(int coolcount) {
        this.coolcount = coolcount;
    }

    public int getCommentcount() {
        return commentcount;
    }

    public void setCommentcount(int commentcount) {
        this.commentcount = commentcount;
    }

    public boolean isUserLike() {
        return userLike;
    }

    public void setUserLike(boolean userLike) {
        this.userLike = userLike;
    }

    public static class LikeModel implements Serializable{

        String postid;
        String profile_pic_url;
        String userid;
        String tag_num;
        String liked_by_name;
        String timestamp_like;
        boolean islike;
        boolean iscool;


        public String getProfile_pic_url() {
            return profile_pic_url;
        }

        public void setProfile_pic_url(String profile_pic_url) {
            this.profile_pic_url = profile_pic_url;
        }

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getTag_num() {
            return tag_num;
        }

        public void setTag_num(String tag_num) {
            this.tag_num = tag_num;
        }

        public String getLiked_by_name() {
            return liked_by_name;
        }

        public void setLiked_by_name(String liked_by_name) {
            this.liked_by_name = liked_by_name;
        }

        public String getTimestamp_like() {
            return timestamp_like;
        }

        public void setTimestamp_like(String timestamp_like) {
            this.timestamp_like = timestamp_like;
        }

        public boolean islike() {
            return islike;
        }

        public void setIslike(boolean islike) {
            this.islike = islike;
        }

        public boolean iscool() {
            return iscool;
        }

        public void setIscool(boolean iscool) {
            this.iscool = iscool;
        }

        public String getPostid() {
            return postid;
        }

        public void setPostid(String postid) {
            this.postid = postid;
        }
    }
}