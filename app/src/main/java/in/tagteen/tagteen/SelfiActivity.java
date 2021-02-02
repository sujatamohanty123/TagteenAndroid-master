package in.tagteen.tagteen;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import in.tagteen.tagteen.TagteenInterface.GalleryClickedPosition;
import in.tagteen.tagteen.configurations.ApplicationConstants;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.selfyManager.AddSelfy;
import in.tagteen.tagteen.utils.ScalingUtilities;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class SelfiActivity extends AppCompatActivity {
    private Activity mCurrentActivity;
    public static final String FRAGMENT_TAG = "camera";
    private static final int REQUEST_CAMERA_PERMISSIONS = 931;
    private static final int REQUEST_PREVIEW_CODE = 1001;
    public GalleryClickedPosition delegate = null;
    private FlashSwitchView flashSwitchView;
    private CameraSwitchView cameraSwitchView;
    private RecordButton recordButton;
    private MediaActionSwitchView mediaActionSwitchView;
    private TextView recordDurationText;
    private TextView recordSizeText;
    private View cameraLayout;
    private GridView sdcardImages;
    private RelativeLayout cameraLayoutrere;
    private ImageView closeSelfiFragment;
    private static final String EXTRA_ANIMAL_ITEM = "animal_item";
    private static final String EXTRA_TRANSITION_NAME = "transition_name";
    boolean isCameraRequired, isVideoRequired;
    private CarouselPickerTextView selfiCarouselPicker;
    private CarouselPickerTextView.CarouselViewAdapter textAdapter;
    private Bitmap bm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(in.tagteen.tagteen.R.layout.tab_fragment);
        closeSelfiFragment = (ImageView) findViewById(in.tagteen.tagteen.R.id.closs_selfi_view);
        flashSwitchView = (FlashSwitchView) findViewById(in.tagteen.tagteen.R.id.flash_switch_view);
        cameraSwitchView = (CameraSwitchView) findViewById(in.tagteen.tagteen.R.id.front_back_camera_switcher);
        recordButton = (RecordButton) findViewById(in.tagteen.tagteen.R.id.record_button);
        mediaActionSwitchView = (MediaActionSwitchView) findViewById(in.tagteen.tagteen.R.id.photo_video_camera_switcher);
        recordDurationText = (TextView) findViewById(in.tagteen.tagteen.R.id.record_duration_text);
        recordSizeText = (TextView) findViewById(in.tagteen.tagteen.R.id.record_size_mb_text);
        selfiCarouselPicker = (CarouselPickerTextView) findViewById(in.tagteen.tagteen.R.id.carousel_selfi_component);

        closeSelfiFragment.setImageDrawable(getResources().getDrawable(R.drawable.ic_cross));
        sdcardImages = (GridView) findViewById(in.tagteen.tagteen.R.id.gridview);
        cameraLayoutrere = (RelativeLayout) findViewById(in.tagteen.tagteen.R.id.camera_Layout);
        SharedPreferenceSingleton.getInstance().init(this);
        SharedPreferenceSingleton.getInstance().writeStringPreference(RegistrationConstants.cameraReotation, RegistrationConstants.SelfiActivity);
        List<CarouselPickerTextView.PickerItem> textItems = new ArrayList<>();
        textItems.add(new CarouselPickerTextView.TextItem("SELFIE", 16));
        textItems.add(new CarouselPickerTextView.TextItem("CLIPPIE", 16));
        textAdapter = new CarouselPickerTextView.CarouselViewAdapter(this, textItems, 0);
        selfiCarouselPicker.setAdapter(textAdapter);
        textAdapter.notifyDataSetChanged();
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
                //recordButton.setEnabled(false);
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
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                //position of the selected
                if (position == 0) {
                    onMediaActionSwitchClicked();
                } else {
                    onMediaActionSwitchClicked();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        cameraLayoutrere.setVisibility(View.VISIBLE);
        textAdapter.notifyDataSetChanged();
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    public void addCamera() {

        final Configuration.Builder builder = new Configuration.Builder();
        builder.setCamera(Configuration.CAMERA_FACE_FRONT)
                .setFlashMode(Configuration.FLASH_MODE_AUTO);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
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
                   /* Intent it = new Intent(SelfiActivity.this, ClipiPreviewActivity.class);
                    SharedPreferenceSingleton.getInstance().init(SelfiActivity.this);
                    SharedPreferenceSingleton.getInstance().writeStringPreference(ApplicationConstants.CAPTURED_IMAGE_PATH, filePath);
                    startActivity(it);*/
                    Intent intent = new Intent(SelfiActivity.this, AddSelfy.class);
                    intent.putExtra("path", filePath);
                    startActivity(intent);
                    mediaActionSwitchView.setVisibility(View.GONE);
                }

                @Override
                public void onPhotoTaken(byte[] bytes, String filePath) {
                    recordButton.setEnabled(false);
                    setupImageDisplay(bytes);
                    try {
                        FileOutputStream outStream = new FileOutputStream(filePath);
                        bm.compress(Bitmap.CompressFormat.JPEG, 90, outStream);
                        outStream.write(bytes);
                        outStream.close();

                        Intent it = new Intent(SelfiActivity.this, SelfiCameraPreview.class);
                        SharedPreferenceSingleton.getInstance().init(SelfiActivity.this);
                        SharedPreferenceSingleton.getInstance().writeStringPreference(ApplicationConstants.CAPTURED_IMAGE_PATH, filePath);
                        startActivity(it);
                    } catch (Exception e){
                        recordButton.setEnabled(true);
                        e.printStackTrace();
                    }
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
                    mediaActionSwitchView.displayActionWillSwitchVideo();
                    recordButton.displayPhotoState();
                    flashSwitchView.setVisibility(View.VISIBLE);
                    mediaActionSwitchView.setVisibility(View.GONE);
                }

                @Override
                public void onCameraSetupForVideo() {
                    mediaActionSwitchView.displayActionWillSwitchPhoto();
                    recordButton.displayVideoRecordStateReady();
                    flashSwitchView.setVisibility(View.GONE);
                    mediaActionSwitchView.setVisibility(View.GONE);
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
                    mediaActionSwitchView.setVisibility(View.GONE);
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
                    //cameraSwitchView.setVisibility(allow ? View.VISIBLE : View.GONE);
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
                    if (text.contains(":")) {
                        String segments[] = text.split(":");
                        String time = segments[segments.length - 1];
                        int a = Integer.parseInt(time);
                        recordDurationText.setText("" + (10 - a));
                        if (10 - a == 0) {
                            onRecordButtonClicked();
                            recordDurationText.setText("");
                        }
                    }
                }

                @Override
                public void setRecordDurationTextVisible(boolean visible) {
                    recordDurationText.setVisibility(visible ? View.VISIBLE : View.GONE);
                }
            });
        }
    }

    private void setupImageDisplay(byte[] data) {
        bm = BitmapFactory.decodeByteArray(data, 0, data.length);
        bm = scaleDown(bm, true);//scaling down bitmap
        // imageview_photo.setImageBitmap(photo);
    }

    private Bitmap scaleDown(Bitmap realImage, boolean filter) {
        int screenWidth = getResources().getDisplayMetrics().widthPixels;;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;;

        Bitmap scaled;
        if (getResources().getConfiguration().orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT) {
            scaled = ScalingUtilities.createScaledBitmap(bm, screenHeight, screenWidth, ScalingUtilities.ScalingLogic.FIT);
            int w = scaled.getWidth();
            int h = scaled.getHeight();
            // Setting post rotate to 90
            Matrix mtx = new Matrix();
            mtx.postRotate(90);
            // Rotating Bitmap
            float[] mirrorY = {-1, 0, 0, 0, 1, 0, 0, 0, 1};
            mtx = new Matrix();
            Matrix matrixMirrorY = new Matrix();
            matrixMirrorY.setValues(mirrorY);

            mtx.postConcat(matrixMirrorY);

            mtx.preRotate(270);
            realImage = Bitmap.createBitmap(scaled, 0, 0, w, h, mtx, true);
        } else {// LANDSCAPE MODE
            //No need to reverse width and height
            scaled = ScalingUtilities.createScaledBitmap(realImage, screenHeight, screenWidth, ScalingUtilities.ScalingLogic.FIT);
            int w = scaled.getWidth();
            int h = scaled.getHeight();
            // Setting post rotate to 90

            Matrix mtx = new Matrix();

            float[] mirrorY = {-1, 0, 0, 0, 1, 0, 0, 0, 1};
            Matrix matrixMirrorY = new Matrix();
            matrixMirrorY.setValues(mirrorY);

            mtx.postConcat(matrixMirrorY);
            mtx.postRotate(180);
            // Rotating Bitmap
            realImage = Bitmap.createBitmap(scaled, 0, 0, w, h, mtx, filter);
       }
        return realImage;
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
                    Log.e("bytes", "" + bytes);
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
        mediaActionSwitchView.setVisibility(View.GONE);
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
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
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