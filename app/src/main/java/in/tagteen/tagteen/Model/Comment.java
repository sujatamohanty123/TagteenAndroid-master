package in.tagteen.tagteen.Model;

/**
 * Created by Mathivanan on 23-05-2017.
 */

public class Comment {
    private String comment_id;
    private String comment,name,post_time,tagnumber;
    private String imag;
    private boolean islike;
    public Comment() {
    }

    public Comment(String comment, String name, String post_time, String tagnumber, String imag,
                   boolean islike) {
        this.comment = comment;
        this.name = name;
        this.post_time = post_time;
        this.tagnumber = tagnumber;
        this.imag = imag;
        this.islike = islike;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPost_time() {
        return post_time;
    }

    public void setPost_time(String post_time) {
        this.post_time = post_time;
    }

    public String getImag() {
        return imag;
    }

    public void setImag(String imag) {
        this.imag = imag;
    }

    public String getTagnumber() {
        return tagnumber;
    }

    public void setTagnumber(String tagnumber) {
        this.tagnumber = tagnumber;
    }

    public boolean islike() {
        return islike;
    }

    public void setIslike(boolean islike) {
        this.islike = islike;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }
}
