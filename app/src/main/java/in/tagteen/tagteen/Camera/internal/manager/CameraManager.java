package in.tagteen.tagteen.Camera.internal.manager;

import android.content.Context;

import in.tagteen.tagteen.Camera.configuration.Configuration;
import in.tagteen.tagteen.Camera.configuration.ConfigurationProvider;
import in.tagteen.tagteen.Camera.internal.manager.listener.CameraCloseListener;
import in.tagteen.tagteen.Camera.internal.manager.listener.CameraOpenListener;
import in.tagteen.tagteen.Camera.internal.manager.listener.CameraPhotoListener;
import in.tagteen.tagteen.Camera.internal.manager.listener.CameraVideoListener;
import in.tagteen.tagteen.Camera.internal.utils.Size;
import in.tagteen.tagteen.Camera.listeners.CameraFragmentResultListener;

import java.io.File;

/*
 * Created by memfis on 8/14/16.
 */
public interface CameraManager<CameraId, SurfaceListener> {

    void initializeCameraManager(ConfigurationProvider configurationProvider, Context context);

    void openCamera(CameraId cameraId, CameraOpenListener<CameraId, SurfaceListener> cameraOpenListener);

    void closeCamera(CameraCloseListener<CameraId> cameraCloseListener);

    void setFlashMode(@Configuration.FlashMode int flashMode);

    void takePhoto(File photoFile, CameraPhotoListener cameraPhotoListener, CameraFragmentResultListener callback);

    void startVideoRecord(File videoFile, CameraVideoListener cameraVideoListener);

    Size getPhotoSizeForQuality(@Configuration.MediaQuality int mediaQuality);

    void stopVideoRecord(CameraFragmentResultListener callback);

    void releaseCameraManager();

    CameraId getCurrentCameraId();

    CameraId getFaceFrontCameraId();

    CameraId getFaceBackCameraId();

    int getNumberOfCameras();

    int getFaceFrontCameraOrientation();

    int getFaceBackCameraOrientation();

    boolean isVideoRecording();

    CharSequence[] getVideoQualityOptions();

    CharSequence[] getPhotoQualityOptions();

    void setCameraId(CameraId currentCameraId);
}
