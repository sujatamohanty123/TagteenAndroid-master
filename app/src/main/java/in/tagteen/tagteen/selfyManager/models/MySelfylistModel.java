package in.tagteen.tagteen.selfyManager.models;

import java.util.ArrayList;

/**
 * Created by lovekushvishwakarma on 06/01/18.
 */

public class MySelfylistModel {
    String date_created, last_date_updated, post_creator_id, post_type_id, title, _id,mediaUrl;
    ArrayList<SelfyReactModel> selfyReactModel;
    boolean is_selfie;

    public MySelfylistModel(String date_created, String last_date_updated, String post_creator_id,
                            String post_type_id, String title, String _id, ArrayList<SelfyReactModel> selfyReactModel, boolean is_selfie,String mediaUrl) {
        this.date_created = date_created;
        this.last_date_updated = last_date_updated;
        this.post_creator_id = post_creator_id;
        this.post_type_id = post_type_id;
        this.title = title;
        this._id = _id;
        this.selfyReactModel = selfyReactModel;
        this.is_selfie = is_selfie;
        this.mediaUrl=mediaUrl;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public ArrayList<SelfyReactModel> getSelfyReactModel() {
        return selfyReactModel;
    }

    public void setSelfyReactModel(ArrayList<SelfyReactModel> selfyReactModel) {
        this.selfyReactModel = selfyReactModel;
    }

    public boolean isIs_selfie() {
        return is_selfie;
    }

    public void setIs_selfie(boolean is_selfie) {
        this.is_selfie = is_selfie;
    }
}
