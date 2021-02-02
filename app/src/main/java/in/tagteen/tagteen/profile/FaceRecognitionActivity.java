package in.tagteen.tagteen.profile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.core.view.ViewCompat;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.LargestFaceFocusingProcessor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import in.tagteen.tagteen.Camera.CameraFragment;
import in.tagteen.tagteen.Camera.CameraFragmentApi;
import in.tagteen.tagteen.Camera.configuration.Configuration;
import in.tagteen.tagteen.Camera.listeners.CameraFragmentControlsAdapter;
import in.tagteen.tagteen.Camera.listeners.CameraFragmentResultListener;
import in.tagteen.tagteen.Camera.listeners.CameraFragmentStateAdapter;
import in.tagteen.tagteen.Camera.listeners.CameraFragmentVideoRecordTextAdapter;
import in.tagteen.tagteen.Camera.widgets.FlashSwitchView;
import in.tagteen.tagteen.Camera.widgets.RecordButton;
import in.tagteen.tagteen.CarouselPicker.CarouselPickerTextView;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;

public class FaceRecognitionActivity extends FragmentActivity {
    private final String TAG = FaceRecognitionActivity.class.getSimpleName();
    private static final int REQUEST_CAMERA_PERMISSIONS = 931;
    private FlashSwitchView flashSwitchView;
    private TextView recordDurationText;
    private TextView recordSizeText;
    private RelativeLayout cameraLayout;
    private ImageView closeScreen;
    private Bitmap bm;

    private int eyesBlinkCount = 0;

    private CameraSource cameraSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(in.tagteen.tagteen.R.layout.tab_fragment);

        this.initWidgets();
        this.bindEvents();
    }

    private void initWidgets() {
        this.closeScreen = (ImageView) findViewById(R.id.closs_selfi_view);
        this.flashSwitchView = (FlashSwitchView) findViewById(R.id.flash_switch_view);
        this.flashSwitchView.setVisibility(View.VISIBLE);
        RecordButton recordButton = (RecordButton) findViewById(R.id.record_button);
        recordButton.setVisibility(View.GONE);
        //this.mediaActionSwitchView = (MediaActionSwitchView) findViewById(in.tagteen.tagteen.R.id.photo_video_camera_switcher);
        this.recordDurationText = (TextView) findViewById(R.id.record_duration_text);
        this.recordSizeText = (TextView) findViewById(R.id.record_size_mb_text);
        CarouselPickerTextView selfiCarouselPicker = (CarouselPickerTextView) findViewById(R.id.carousel_selfi_component);
        selfiCarouselPicker.setVisibility(View.GONE);

        this.closeScreen.setImageDrawable(getResources().getDrawable(R.drawable.ic_cross));
        this.cameraLayout = (RelativeLayout) findViewById(in.tagteen.tagteen.R.id.camera_Layout);
        SharedPreferenceSingleton.getInstance().init(this);

        List<CarouselPickerTextView.PickerItem> textItems = new ArrayList<>();
        textItems.add(new CarouselPickerTextView.TextItem("PICTURE", 16));
        this.onAddCameraClicked();
    }

    private void bindEvents() {
        this.closeScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        this.flashSwitchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFlashSwitcClicked();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.cameraLayout.setVisibility(View.VISIBLE);
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    public void addCamera() {
        final Configuration.Builder builder = new Configuration.Builder();
        builder.setCamera(Configuration.CAMERA_FACE_FRONT)
                .setFlashMode(Configuration.FLASH_MODE_AUTO);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        final CameraFragment cameraFragment = CameraFragment.newInstance(builder.build());
        getSupportFragmentManager().beginTransaction()
                .replace(in.tagteen.tagteen.R.id.content, cameraFragment)
                .commit();

        if (cameraFragment != null) {
            cameraFragment.setResultListener(new CameraFragmentResultListener() {
                @Override
                public void onVideoRecorded(String filePath) {
                    // do nothing
                }

                @Override
                public void onPhotoTaken(byte[] bytes, String filePath) {
                    setupImageDisplay(filePath, bytes);
                    try {
                        FileOutputStream outStream = new FileOutputStream(filePath);
                        bm.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                        outStream.write(bytes);
                        outStream.close();

                        Intent data = new Intent();
                        data.putExtra(Constants.PATH_IMAGE_CAPTURED, filePath);
                        setResult(RESULT_OK, data);
                        finish();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

            cameraFragment.setStateListener(new CameraFragmentStateAdapter() {
                @Override
                public void onCurrentCameraBack() {
                }

                @Override
                public void onCurrentCameraFront() {
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
                    flashSwitchView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onCameraSetupForVideo() {
                    flashSwitchView.setVisibility(View.GONE);
                }

                @Override
                public void shouldRotateControls(int degrees) {
                    ViewCompat.setRotation(flashSwitchView, degrees);
                    ViewCompat.setRotation(recordDurationText, degrees);
                    ViewCompat.setRotation(recordSizeText, degrees);
                }

                @Override
                public void onRecordStateVideoReadyForRecord() {
                }

                @Override
                public void onRecordStateVideoInProgress() {
                }

                @Override
                public void onRecordStatePhoto() {

                }

                @Override
                public void onStopVideoRecord() {
                    recordSizeText.setVisibility(View.GONE);
                }

                @Override
                public void onStartVideoRecord(File outputFile) {
                }
            });

            cameraFragment.setControlsListener(new CameraFragmentControlsAdapter() {
                @Override
                public void lockControls() {
                    flashSwitchView.setEnabled(false);
                }

                @Override
                public void unLockControls() {
                    flashSwitchView.setEnabled(true);
                }

                @Override
                public void allowCameraSwitching(boolean allow) {
                }

                @Override
                public void allowRecord(boolean allow) {

                }

                @Override
                public void setMediaActionSwitchVisible(boolean visible) {
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

                }

                @Override
                public void setRecordDurationTextVisible(boolean visible) {
                    recordDurationText.setVisibility(visible ? View.VISIBLE : View.GONE);
                }
            });
        }

        this.createCameraSource();
    }

    private void setupImageDisplay(String imagePath, byte[] data) {
        this.bm = BitmapFactory.decodeByteArray(data, 0, data.length);
        this.bm = this.scalePic(imagePath, this.bm);
    }

    private Bitmap scalePic(String imagePath, Bitmap realImage) {
        File file = new File(imagePath);
        try {
            ExifInterface exif = new ExifInterface(file.toString());
            if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("6")){
                realImage = rotate(realImage, 90);
            } else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("8")){
                realImage = rotate(realImage, 270);
            } else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("3")){
                realImage = rotate(realImage, 180);
            } else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("0")){
                realImage = rotate(realImage, 90);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return realImage;
    }

    public static Bitmap rotate(Bitmap bitmap, int degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        mtx.setRotate(degree);
        int px = (int) (w / 2f);
        int py = (int) (h / 2f);
        mtx.postScale(-1, 1, px, py);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    private void onFlashSwitcClicked() {
        final CameraFragmentApi cameraFragment = getCameraFragment();
        if (cameraFragment != null) {
            cameraFragment.toggleFlashMode();
        }
    }

    private void captureImage() {
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

    private void onAddCameraClicked() {
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
                ActivityCompat.requestPermissions(
                        this, permissionsToRequest.toArray(new String[permissionsToRequest.size()]), REQUEST_CAMERA_PERMISSIONS);
            } else {
                this.addCamera();
            }
        } else {
            this.addCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length != 0) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            this.addCamera();
        }
    }

    private CameraFragmentApi getCameraFragment() {
        return (CameraFragmentApi) this.getSupportFragmentManager().findFragmentByTag(TAG);
    }

    private class EyesTracker extends Tracker<Face> {

        private final float THRESHOLD = 0.75f;

        private EyesTracker() {

        }

        @Override
        public void onUpdate(Detector.Detections<Face> detections, Face face) {
            if (face.getIsLeftEyeOpenProbability() > THRESHOLD || face.getIsRightEyeOpenProbability() > THRESHOLD) {
                Log.i(TAG, "onUpdate: Eyes Detected");
                if (eyesBlinkCount > 0) {
                    Utils.showShortToast(FaceRecognitionActivity.this, "Eyes blinked count " + eyesBlinkCount);
                }
            } else {
                eyesBlinkCount += 1;
            }
        }

        @Override
        public void onMissing(Detector.Detections<Face> detections) {
            super.onMissing(detections);
            Utils.showShortToast(FaceRecognitionActivity.this, "Face Not Detected yet!");
        }

        @Override
        public void onDone() {
            super.onDone();
        }
    }

    private class FaceTrackerFactory implements MultiProcessor.Factory<Face> {

        private FaceTrackerFactory() {

        }

        @Override
        public Tracker<Face> create(Face face) {
            return new EyesTracker();
        }
    }

    public void createCameraSource() {
        FaceDetector detector = new FaceDetector.Builder(this)
                .setTrackingEnabled(true)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setMode(FaceDetector.FAST_MODE)
                .build();
        detector.setProcessor(new LargestFaceFocusingProcessor(detector, new EyesTracker()));

        this.cameraSource = new CameraSource.Builder(this, detector)
                .setRequestedPreviewSize(1024, 768)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(30.0f)
                .build();

        try {
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
            this.cameraSource.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this.cameraSource != null) {
            try {
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
                this.cameraSource.start();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (this.cameraSource != null) {
            this.cameraSource.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.cameraSource != null) {
            this.cameraSource.release();
        }
    }
}
