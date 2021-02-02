package in.tagteen.tagteen.Camera.internal.controller.view;

import androidx.annotation.Nullable;
import android.view.View;

import in.tagteen.tagteen.Camera.configuration.Configuration;
import in.tagteen.tagteen.Camera.internal.utils.Size;
import in.tagteen.tagteen.Camera.listeners.CameraFragmentResultListener;

/*
 * Created by memfis on 7/6/16.
 */
public interface CameraView {

    void updateCameraPreview(Size size, View cameraPreview);

    void updateUiForMediaAction(@Configuration.MediaAction int mediaAction);

    void updateCameraSwitcher(int numberOfCameras);

    void onPhotoTaken(byte[] bytes, @Nullable CameraFragmentResultListener callback);

    void onVideoRecordStart(int width, int height);

    void onVideoRecordStop(@Nullable CameraFragmentResultListener callback);

    void releaseCameraPreview();

}
