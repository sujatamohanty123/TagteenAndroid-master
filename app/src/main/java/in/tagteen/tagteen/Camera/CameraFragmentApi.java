package in.tagteen.tagteen.Camera;

import androidx.annotation.Nullable;

import in.tagteen.tagteen.Camera.listeners.CameraFragmentControlsListener;
import in.tagteen.tagteen.Camera.listeners.CameraFragmentResultListener;
import in.tagteen.tagteen.Camera.listeners.CameraFragmentStateListener;
import in.tagteen.tagteen.Camera.listeners.CameraFragmentVideoRecordTextListener;

/*
 * Created by florentchampigny on 16/01/2017.
 */

public interface CameraFragmentApi {

    void takePhotoOrCaptureVideo(CameraFragmentResultListener resultListener);

    void takePhotoOrCaptureVideo(CameraFragmentResultListener resultListener, @Nullable String directoryPath, @Nullable String fileName);

    void openSettingDialog();

    void switchCameraTypeFrontBack();

    void switchActionPhotoVideo();

    void toggleFlashMode();

    void setStateListener(CameraFragmentStateListener cameraFragmentStateListener);

    void setTextListener(CameraFragmentVideoRecordTextListener cameraFragmentVideoRecordTextListener);

    void setControlsListener(CameraFragmentControlsListener cameraFragmentControlsListener);

    void setResultListener(CameraFragmentResultListener cameraFragmentResultListener);

}
