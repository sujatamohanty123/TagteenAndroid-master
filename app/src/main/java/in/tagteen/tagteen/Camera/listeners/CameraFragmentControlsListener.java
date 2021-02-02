package in.tagteen.tagteen.Camera.listeners;

/*
 * Created by florentchampigny on 13/01/2017.
 */

public interface CameraFragmentControlsListener {
    void lockControls();
    void unLockControls();
    void allowCameraSwitching(boolean allow);
    void allowRecord(boolean allow);
    void setMediaActionSwitchVisible(boolean visible);
}
