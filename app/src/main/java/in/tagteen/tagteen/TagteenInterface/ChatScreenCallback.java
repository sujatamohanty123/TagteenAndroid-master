package in.tagteen.tagteen.TagteenInterface;

/**
 * Created by user on 09-06-2017.
 */

public interface ChatScreenCallback {

    void isPasswordMatched(boolean isPasswordMatched);

    void gallerySelectedImage(boolean send);

    void cameraClickedImage(String imagePath);

    void audioMusicFile(String imagePath);

    void cameraClose(boolean isClicked);

}
