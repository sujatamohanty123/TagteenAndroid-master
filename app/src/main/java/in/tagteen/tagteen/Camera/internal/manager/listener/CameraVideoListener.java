package in.tagteen.tagteen.Camera.internal.manager.listener;

import in.tagteen.tagteen.Camera.internal.utils.Size;
import in.tagteen.tagteen.Camera.listeners.CameraFragmentResultListener;

import java.io.File;

/*
 * Created by memfis on 8/14/16.
 */
public interface CameraVideoListener {
    void onVideoRecordStarted(Size videoSize);

    void onVideoRecordStopped(File videoFile, CameraFragmentResultListener callback);

    void onVideoRecordError();
}
