package in.tagteen.tagteen;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import in.tagteen.tagteen.Camera.CameraFragment;
import in.tagteen.tagteen.Camera.CameraFragmentApi;
import in.tagteen.tagteen.Camera.configuration.Configuration;
import in.tagteen.tagteen.Camera.listeners.CameraFragmentControlsAdapter;
import in.tagteen.tagteen.Camera.listeners.CameraFragmentResultListener;
import in.tagteen.tagteen.Camera.listeners.CameraFragmentStateAdapter;
import in.tagteen.tagteen.Camera.listeners.CameraFragmentVideoRecordTextAdapter;
import in.tagteen.tagteen.Camera.widgets.CameraSwitchView;
import in.tagteen.tagteen.Camera.widgets.FlashSwitchView;
import in.tagteen.tagteen.Camera.widgets.MediaActionSwitchView;
import in.tagteen.tagteen.Camera.widgets.RecordButton;
import in.tagteen.tagteen.CarouselPicker.CarouselPickerTextView;
import in.tagteen.tagteen.Filter.activity.ActivityGallery;
import in.tagteen.tagteen.TagteenInterface.GalleryClickedPosition;
import in.tagteen.tagteen.configurations.ApplicationConstants;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;

/**
 * Created by user on 16-09-2017.
 */

public class ChatCameraActivity extends AppCompatActivity {

    private Activity mCurrentActivity;
    public static final String FRAGMENT_TAG = "camera";
    private static final int REQUEST_CAMERA_PERMISSIONS = 931;
    private static final int REQUEST_PREVIEW_CODE = 1001;
    public GalleryClickedPosition delegate = null;
    FlashSwitchView flashSwitchView;
    CameraSwitchView cameraSwitchView;
    RecordButton recordButton;
    MediaActionSwitchView mediaActionSwitchView;
    TextView recordDurationText;
    TextView recordSizeText;
    View cameraLayout;
    GridView sdcardImages;
    RelativeLayout cameraLayoutrere;
    ImageView closeSelfiFragment;
    private static final String EXTRA_ANIMAL_ITEM = "animal_item";
    private static final String EXTRA_TRANSITION_NAME = "transition_name";
    boolean isCameraRequired, isVideoRequired;
    CarouselPickerTextView selfiCarouselPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(in.tagteen.tagteen.R.layout.tab_fragment);

        closeSelfiFragment = (ImageView)findViewById(in.tagteen.tagteen.R.id.closs_selfi_view);
        flashSwitchView    = (FlashSwitchView)findViewById(in.tagteen.tagteen.R.id.flash_switch_view);
        cameraSwitchView   = (CameraSwitchView) findViewById(in.tagteen.tagteen.R.id.front_back_camera_switcher);
        recordButton       = (RecordButton) findViewById(in.tagteen.tagteen.R.id.record_button);

        mediaActionSwitchView = (MediaActionSwitchView) findViewById(in.tagteen.tagteen.R.id.photo_video_camera_switcher);
        recordDurationText    = (TextView)findViewById(in.tagteen.tagteen.R.id.record_duration_text);
        recordSizeText        = (TextView)findViewById(in.tagteen.tagteen.R.id.record_size_mb_text);
        selfiCarouselPicker   = (CarouselPickerTextView)findViewById(in.tagteen.tagteen.R.id.carousel_selfi_component);
        sdcardImages = (GridView)findViewById(in.tagteen.tagteen.R.id.gridview);
        cameraLayoutrere = (RelativeLayout)findViewById(in.tagteen.tagteen.R.id.camera_Layout);
        final String st = "gallery ";
        List<CarouselPickerTextView.PickerItem> textItems = new ArrayList<>();
        textItems.add(new CarouselPickerTextView.TextItem("GIFLU",12));
        textItems.add(new CarouselPickerTextView.TextItem("CAMERA",12));
        textItems.add(new CarouselPickerTextView.TextItem("VIDEO",12));
        CarouselPickerTextView.CarouselViewAdapter textAdapter = new CarouselPickerTextView.CarouselViewAdapter(this, textItems,0);
        selfiCarouselPicker.setAdapter(textAdapter);

        onAddCameraClicked();

        closeSelfiFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRecordButtonClicked();
            }
        });

        flashSwitchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFlashSwitcClicked();
            }
        });

        cameraSwitchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSwitchCameraClicked();
            }
        });

        mediaActionSwitchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMediaActionSwitchClicked();
            }
        });

        selfiCarouselPicker.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                //position of the selected
                if(position==0){
                    onMediaActionSwitchClicked();
                } else if(position==1){
                    onMediaActionSwitchClicked();
                }else{
                    onMediaActionSwitchClicked();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        selfiCarouselPicker.setCurrentItem(1);
    }

    @Override
    protected void onStart() {
        super.onStart();
        cameraLayoutrere.setVisibility(View.VISIBLE);
    }

    @RequiresPermission(android.Manifest.permission.CAMERA)
    public void addCamera() {

        final Configuration.Builder builder = new Configuration.Builder();
        builder
                .setCamera(Configuration.CAMERA_FACE_FRONT)
                .setFlashMode(Configuration.FLASH_MODE_AUTO);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        final CameraFragment cameraFragment = CameraFragment.newInstance(builder.build());
        getSupportFragmentManager().beginTransaction()
                .replace(in.tagteen.tagteen.R.id.content, cameraFragment, FRAGMENT_TAG)
                .commit();

        if (cameraFragment != null) {

            cameraFragment.setResultListener(new CameraFragmentResultListener() {
                @Override
                public void onVideoRecorded(String filePath) {
                }

                @Override
                public void onPhotoTaken(byte[] bytes, String filePath) {

                    Intent it =new Intent(ChatCameraActivity.this,ActivityGallery.class );
                    SharedPreferenceSingleton.getInstance().init(ChatCameraActivity.this);
                    SharedPreferenceSingleton.getInstance().writeStringPreference(ApplicationConstants.CAPTURED_IMAGE_PATH,filePath);
                    startActivity(it);
                }
            });

            cameraFragment.setStateListener(new CameraFragmentStateAdapter() {

                @Override
                public void onCurrentCameraBack() {
                    cameraSwitchView.setVisibility(View.GONE);
                }

                @Override
                public void onCurrentCameraFront() {
                    // cameraSwitchView.displayFrontCamera();
                    cameraSwitchView.setImageDrawable(null);
                    cameraSwitchView.setVisibility(View.GONE);
                }

                @Override
                public void onFlashAuto() {
                    flashSwitchView.displayFlashAuto();
                    flashSwitchView.setVisibility(View.GONE);
                }

                @Override
                public void onFlashOn() {
                    flashSwitchView.displayFlashOn();
                    flashSwitchView.setVisibility(View.GONE);
                }

                @Override
                public void onFlashOff() {
                    flashSwitchView.displayFlashOff();
                    flashSwitchView.setVisibility(View.GONE);
                }

                @Override
                public void onCameraSetupForPhoto() {
                    mediaActionSwitchView.displayActionWillSwitchVideo();
                    recordButton.displayPhotoState();
                   /* flashSwitchView.setVisibility(View.VISIBLE);*/
                }

                @Override
                public void onCameraSetupForVideo() {
                    mediaActionSwitchView.displayActionWillSwitchPhoto();
                    recordButton.displayVideoRecordStateReady();
                    flashSwitchView.setVisibility(View.GONE);
                }

                @Override
                public void shouldRotateControls(int degrees) {
                    ViewCompat.setRotation(cameraSwitchView, degrees);
                    ViewCompat.setRotation(mediaActionSwitchView, degrees);
                    ViewCompat.setRotation(flashSwitchView, degrees);
                    ViewCompat.setRotation(recordDurationText, degrees);
                    ViewCompat.setRotation(recordSizeText, degrees);
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
                    recordSizeText.setVisibility(View.GONE);
                    //cameraSwitchView.setVisibility(View.VISIBLE);
                   /* settingsView.setVisibility(View.VISIBLE);*/
                }

                @Override
                public void onStartVideoRecord(File outputFile) {
                }
            });

            cameraFragment.setControlsListener(new CameraFragmentControlsAdapter() {
                @Override
                public void lockControls() {
                    cameraSwitchView.setEnabled(false);
                    recordButton.setEnabled(false);
                   /* settingsView.setEnabled(false);*/
                    flashSwitchView.setEnabled(false);
                }

                @Override
                public void unLockControls() {
                    cameraSwitchView.setEnabled(true);
                    recordButton.setEnabled(true);
                   /* settingsView.setEnabled(true);*/
                    flashSwitchView.setEnabled(true);
                }

                @Override
                public void allowCameraSwitching(boolean allow) {
                    cameraSwitchView.setVisibility(allow ? View.VISIBLE : View.GONE);
                }

                @Override
                public void allowRecord(boolean allow) {
                    recordButton.setEnabled(allow);
                }

                @Override
                public void setMediaActionSwitchVisible(boolean visible) {
                    mediaActionSwitchView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
                }
            });

            cameraFragment.setTextListener(new CameraFragmentVideoRecordTextAdapter() {
                @Override
                public void setRecordSizeText(long size, String text) {
                    recordSizeText.setText(text);
                }

                @Override
                public void setRecordSizeTextVisible(boolean visible) {
                    recordSizeText.setVisibility(visible ? View.VISIBLE : View.GONE);
                }

                @Override
                public void setRecordDurationText(String text) {
                    recordDurationText.setText(text);
                }

                @Override
                public void setRecordDurationTextVisible(boolean visible) {
                    recordDurationText.setVisibility(visible ? View.VISIBLE : View.GONE);
                }
            });
        }
    }


    public void onFlashSwitcClicked() {
        final CameraFragmentApi cameraFragment = getCameraFragment();
        if (cameraFragment != null) {
            cameraFragment.toggleFlashMode();
        }
    }


    public void onSwitchCameraClicked() {
        final CameraFragmentApi cameraFragment = getCameraFragment();
        if (cameraFragment != null) {
            cameraFragment.switchCameraTypeFrontBack();
        }
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


    public void onSettingsClicked() {
        final CameraFragmentApi cameraFragment = getCameraFragment();
        if (cameraFragment != null) {
            cameraFragment.openSettingDialog();
        }
    }


    public void onMediaActionSwitchClicked() {
        final CameraFragmentApi cameraFragment = getCameraFragment();
        if (cameraFragment != null) {
            cameraFragment.switchActionPhotoVideo();
        }
    }


    public void onAddCameraClicked() {
        if (Build.VERSION.SDK_INT > 15) {
            final String[] permissions = {
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE};

            final List<String> permissionsToRequest = new ArrayList<>();
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionsToRequest.add(permission);
                }
            }
            if (!permissionsToRequest.isEmpty()) {
                ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[permissionsToRequest.size()]), REQUEST_CAMERA_PERMISSIONS);
            } else addCamera();
        } else {
            addCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length != 0) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            addCamera();
        }
    }

    private CameraFragmentApi getCameraFragment() {
        return (CameraFragmentApi) this.getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
    }




}