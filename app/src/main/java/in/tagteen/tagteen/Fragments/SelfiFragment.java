package in.tagteen.tagteen.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import in.tagteen.tagteen.Camera.CameraFragment;
import in.tagteen.tagteen.Camera.CameraFragmentApi;
import in.tagteen.tagteen.Camera.PreviewActivity;
import in.tagteen.tagteen.Camera.configuration.Configuration;
import in.tagteen.tagteen.Camera.listeners.CameraFragmentResultListener;
import in.tagteen.tagteen.Camera.widgets.CameraSwitchView;
import in.tagteen.tagteen.Camera.widgets.FlashSwitchView;
import in.tagteen.tagteen.Camera.widgets.MediaActionSwitchView;
import in.tagteen.tagteen.Camera.widgets.RecordButton;
import in.tagteen.tagteen.Filter.activity.ActivityGallery;
import in.tagteen.tagteen.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


import android.view.LayoutInflater;
import android.view.ViewGroup;


import in.tagteen.tagteen.Camera.listeners.CameraFragmentStateListener;
import in.tagteen.tagteen.Camera.widgets.CameraSettingsView;
import in.tagteen.tagteen.TagteenInterface.CameraInterface;
import in.tagteen.tagteen.configurations.ApplicationConstants;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;

import android.database.Cursor;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;


public class SelfiFragment extends Fragment {
    private Activity mCurrentActivity;
    public static final String FRAGMENT_TAG = "camera";
    private static final int REQUEST_CAMERA_PERMISSIONS = 931;
    private static final int REQUEST_PREVIEW_CODE = 1001;

    CameraSettingsView settingsView;
    FlashSwitchView flashSwitchView;
    CameraSwitchView cameraSwitchView;
    RecordButton recordButton;
    MediaActionSwitchView mediaActionSwitchView;
    TextView recordDurationText;
    TextView recordSizeText;
    View cameraLayout;
    View addCameraButton;
    private Cursor cursor;
    private int columnIndex;

    GridView sdcardImages;
    RelativeLayout cameraLayoutrere;
    LinearLayout galleryLayout;
    GridView gv_folder;
    private static final int REQUEST_PERMISSIONS = 100;
    ImageView closeSelfiFragment;
    CameraInterface cameraInterface;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.tab_fragment, container, false);
        closeSelfiFragment =(ImageView) rootView.findViewById(R.id.closs_selfi_view);
        cameraInterface=(CameraInterface) getActivity();
        cameraSwitchView=(CameraSwitchView)rootView.findViewById(R.id.front_back_camera_switcher);
        flashSwitchView =(FlashSwitchView) rootView.findViewById(R.id.flash_switch_view);
        recordButton = (RecordButton) rootView.findViewById(R.id.record_button);
        recordDurationText = (TextView) rootView.findViewById(R.id.record_duration_text);
        recordSizeText = (TextView) rootView.findViewById(R.id.record_size_mb_text);
        cameraLayout = (View) rootView.findViewById(R.id.camera_Layout);
        sdcardImages = (GridView) rootView.findViewById(R.id.gridview);
        cameraLayoutrere = (RelativeLayout) rootView.findViewById(R.id.camera_Layout);
        closeSelfiFragment.setImageDrawable(getResources().getDrawable(R.drawable.ic_cross));
        final String st = "gallery ";
        onAddCameraClicked();

        SharedPreferenceSingleton.getInstance().init(getActivity());
        SharedPreferenceSingleton.getInstance().writeStringPreference(RegistrationConstants.cameraReotation,RegistrationConstants.ChatBack);
        cameraSwitchView.setVisibility(View.VISIBLE);
        flashSwitchView.setVisibility(View.VISIBLE);
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRecordButtonClicked();
            }
        });
        cameraSwitchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSwitchCameraClicked();
            }
        });
        flashSwitchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFlashSwitcClicked();
            }
        });

        closeSelfiFragment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                cameraInterface.clickedPick(null,true);
            }});

        return rootView;

    }
    public void onRecordButtonClicked() {
        final CameraFragmentApi cameraFragment = getCameraFragment();
        if (cameraFragment != null) {
            cameraFragment.takePhotoOrCaptureVideo(new CameraFragmentResultListener() {
                @Override
                public void onVideoRecorded(String filePath) {

                }
                @Override
                public void onPhotoTaken(byte[] bytes, String filePath) {
                    Log.e("bytes",""+bytes);
                }
            });
        }
    }

    public void onAddCameraClicked() {

        if (Build.VERSION.SDK_INT > 15) {
            final String[] permissions = {
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE};

            final List<String> permissionsToRequest = new ArrayList<>();
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionsToRequest.add(permission);
                }
            }
            if (!permissionsToRequest.isEmpty()) {
                ActivityCompat.requestPermissions(getActivity(), permissionsToRequest.toArray(new String[permissionsToRequest.size()]), REQUEST_CAMERA_PERMISSIONS);
            } else addCamera();
        } else {
            addCamera();
        }
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    public void addCamera() {

        final Configuration.Builder builder = new Configuration.Builder();
        builder.setCamera(Configuration.CAMERA_FACE_FRONT).setFlashMode(Configuration.FLASH_MODE_AUTO);

        cameraLayout.setVisibility(View.VISIBLE);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        final CameraFragment cameraFragment = CameraFragment.newInstance(new Configuration.Builder()
                .setCamera(Configuration.CAMERA_FACE_REAR).build());
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, cameraFragment, FRAGMENT_TAG)
                .commitAllowingStateLoss();

        if (cameraFragment != null) {

            cameraFragment.setStateListener(new CameraFragmentStateListener() {

                @Override
                public void onCurrentCameraBack() {
                    SharedPreferenceSingleton.getInstance().init(getActivity());
                    SharedPreferenceSingleton.getInstance().writeStringPreference(RegistrationConstants.cameraReotation,RegistrationConstants.ChatBack);
                }

                @Override
                public void onCurrentCameraFront() {
                    SharedPreferenceSingleton.getInstance().init(getActivity());
                    SharedPreferenceSingleton.getInstance().writeStringPreference(RegistrationConstants.cameraReotation,RegistrationConstants.ChatFront);
                    cameraSwitchView.displayFrontCamera();
                }

                @Override
                public void onFlashAuto() {
                }

                @Override
                public void onFlashOn() {
                    flashSwitchView.displayFlashOn();
                }

                @Override
                public void onFlashOff() {
                    flashSwitchView.displayFlashOff();
                }

                @Override
                public void onCameraSetupForPhoto() {
                    recordButton.displayPhotoState();

                }

                @Override
                public void onCameraSetupForVideo() {
                    recordButton.displayVideoRecordStateReady();
                }

                @Override
                public void shouldRotateControls(int degrees) {
                }

                @Override
                public void onRecordStateVideoReadyForRecord() {
                    recordButton.displayVideoRecordStateReady();
                }

                @Override
                public void onRecordStateVideoInProgress() {
                    recordButton.displayVideoRecordStateInProgress();
                }

                @Override
                public void onRecordStatePhoto() {
                    recordButton.displayPhotoState();
                }

                @Override
                public void onStopVideoRecord() {
                }

                @Override
                public void onStartVideoRecord(File outputFile) {
                }
            });


            cameraFragment.setResultListener(new CameraFragmentResultListener() {

                @Override
                public void onVideoRecorded(String filePath) {
                    startActivityForResult(PreviewActivity.newIntentVideo(getActivity(), filePath));
                }

                @Override
                public void onPhotoTaken(byte[] bytes, String filePath) {

                    Intent it =new Intent(getActivity(),ActivityGallery.class );
                    SharedPreferenceSingleton.getInstance().init(getActivity());
                    SharedPreferenceSingleton.getInstance().writeStringPreference(ApplicationConstants.CAPTURED_IMAGE_PATH,filePath);
                    startActivityForResult(it, 4);
                }
            });
        }
    }
    public void onSwitchCameraClicked() {
        final CameraFragmentApi cameraFragment = getCameraFragment();
        if (cameraFragment != null) {
            cameraFragment.switchCameraTypeFrontBack();
        }
    }

    public void onFlashSwitcClicked() {
        final CameraFragmentApi cameraFragment = getCameraFragment();
        if (cameraFragment != null) {
            cameraFragment.toggleFlashMode();
        }
    }
    private void startActivityForResult(Intent intent) {

    }

    public void MoveButton(final View view) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)view.getLayoutParams();
        params.bottomMargin += 10;

        view.setLayoutParams(params);
        Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.selfi_button_animation);
        anim.setDuration(1500);
        view.startAnimation(anim);

        anim.setAnimationListener(new TranslateAnimation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationRepeat(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation)
            {
            }
        });
        view.startAnimation(anim);
    }

    private CameraFragmentApi getCameraFragment() {
        return (CameraFragmentApi) getActivity().getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
    }
}