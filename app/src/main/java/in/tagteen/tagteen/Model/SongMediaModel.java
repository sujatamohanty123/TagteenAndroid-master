package in.tagteen.tagteen.Model;


public class SongMediaModel {

    private String mSongID;
    private String mSongTitle;
    private String mSongPath;
    private String mArtist;
    private String mDisplayName;
    private String mDuration;


    private void SongMediaModel(String mSongID, String mSongTitle, String mSongPath, String mArtist, String mDisplayName, String mDuration) {
        this.mSongID = mSongID;
        this.mSongTitle = mSongTitle;
        this.mSongPath = mSongPath;
        this.mArtist = mArtist;
        this.mDisplayName = mDisplayName;
        this.mDuration = mDuration;
    }

    public String getmSongID() {
        return mSongID;
    }

    public void setmSongID(String mSongID) {
        this.mSongID = mSongID;
    }

    public String getmSongTitle() {
        return mSongTitle;
    }

    public void setmSongTitle(String mSongTitle) {
        this.mSongTitle = mSongTitle;
    }

    public String getmSongPath() {
        return mSongPath;
    }

    public void setmSongPath(String mSongPath) {
        this.mSongPath = mSongPath;
    }

    public String getmArtist() {
        return mArtist;
    }

    public void setmArtist(String mArtist) {
        this.mArtist = mArtist;
    }

    public String getmDisplayName() {
        return mDisplayName;
    }

    public void setmDisplayName(String mDisplayName) {
        this.mDisplayName = mDisplayName;
    }

    public String getmDuration() {
        return mDuration;
    }

    public void setmDuration(String mDuration) {
        this.mDuration = mDuration;
    }
}