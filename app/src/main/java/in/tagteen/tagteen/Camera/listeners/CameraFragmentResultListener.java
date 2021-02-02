package in.tagteen.tagteen.Camera.listeners;

/*
 * Created by florentchampigny on 13/01/2017.
 */

import java.io.IOException;

public interface CameraFragmentResultListener {

    //Called when the video record is finished and saved
    void onVideoRecorded(String filePath);

    //called when the photo is taken and saved
    void onPhotoTaken(byte[] bytes, String filePath) throws IOException;
}
