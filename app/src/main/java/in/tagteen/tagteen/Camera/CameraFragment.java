package in.tagteen.tagteen.Camera;

import android.Manifest;
import androidx.annotation.RequiresPermission;

import in.tagteen.tagteen.Camera.configuration.Configuration;
import in.tagteen.tagteen.Camera.internal.ui.BaseAnncaFragment;

public class CameraFragment extends BaseAnncaFragment {

    @RequiresPermission(Manifest.permission.CAMERA)
    public static CameraFragment newInstance(Configuration configuration) {
        return (CameraFragment) BaseAnncaFragment.newInstance(new CameraFragment(), configuration);
    }
}
