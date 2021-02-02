package in.tagteen.tagteen;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.view.WindowManager;

import in.tagteen.tagteen.Fragments.CustomCamera;

public class CustomCameraActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera_custom);
        getFragment(new CustomCamera());
    }

    private void getFragment(Fragment customCamera) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(in.tagteen.tagteen.R.id.camera_layout_container,customCamera );
        transaction.commit();
    }


}
