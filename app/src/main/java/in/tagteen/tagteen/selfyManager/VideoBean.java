package in.tagteen.tagteen.selfyManager;

/**
 * Created by BSETEC on 19-04-2016.
 */
public class VideoBean {
    String videopath;
    long videoTime;

    public VideoBean(long videoTime, String videopath) {
        this.videoTime = videoTime;
        this.videopath = videopath;
    }

    public long getVideoTime() {
        return videoTime;
    }

    public void setVideoTime(long videoTime) {
        this.videoTime = videoTime;
    }

    public String getVideopath() {
        return videopath;
    }

    public void setVideopath(String videopath) {
        this.videopath = videopath;
    }
}
