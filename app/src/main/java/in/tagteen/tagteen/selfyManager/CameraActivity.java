package in.tagteen.tagteen.selfyManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
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
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.utils.ScalingUtilities;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vinoth on 08/09/18.
 */

public class CameraActivity extends FragmentActivity {
  private final String TAG = "CameraActivity";
  private static final int REQUEST_CAMERA_PERMISSIONS = 931;
  private FlashSwitchView flashSwitchView;
  private CameraSwitchView cameraSwitchView;
  private RecordButton recordButton;
  private MediaActionSwitchView mediaActionSwitchView;
  private TextView recordDurationText;
  private TextView recordSizeText;
  private RelativeLayout cameraLayout;
  private ImageView closeScreen;
  private CarouselPickerTextView selfiCarouselPicker;
  private CarouselPickerTextView.CarouselViewAdapter textAdapter;
  private Bitmap bm;
  private boolean disableVideo = false;
  private boolean disablePhoto = false;
  private int recordDuration;
  private boolean isFrontCamera = false;
  private boolean disableCameraSwitching = false;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(in.tagteen.tagteen.R.layout.tab_fragment);
    this.initWidgets();
    this.bindEvents();
  }


  private void initWidgets() {
    this.disablePhoto = getIntent().getBooleanExtra(Constants.DISABLE_PHOTO, false);
    this.disableVideo = getIntent().getBooleanExtra(Constants.DISABLE_VIDEO, false);
    this.isFrontCamera = getIntent().getBooleanExtra(Constants.DEFAULT_FRONT_CAMERA, false);
    this.disableCameraSwitching =
        getIntent().getBooleanExtra(Constants.DISABLE_CAMERA_SWITCHING, false);
    this.recordDuration = getIntent().getIntExtra(Constants.VIDEO_RECORD_DURATION, 59);

    this.closeScreen = (ImageView) findViewById(in.tagteen.tagteen.R.id.closs_selfi_view);
    this.flashSwitchView =
        (FlashSwitchView) findViewById(in.tagteen.tagteen.R.id.flash_switch_view);
    this.flashSwitchView.setVisibility(View.VISIBLE);
    this.cameraSwitchView =
        (CameraSwitchView) findViewById(in.tagteen.tagteen.R.id.front_back_camera_switcher);
    if (this.disableCameraSwitching) {
      this.cameraSwitchView.setVisibility(View.GONE);
    } else {
      this.cameraSwitchView.setVisibility(View.VISIBLE);
    }
    this.recordButton = (RecordButton) findViewById(in.tagteen.tagteen.R.id.record_button);
    this.mediaActionSwitchView =
        (MediaActionSwitchView) findViewById(in.tagteen.tagteen.R.id.photo_video_camera_switcher);
    this.recordDurationText = (TextView) findViewById(in.tagteen.tagteen.R.id.record_duration_text);
    this.recordSizeText = (TextView) findViewById(in.tagteen.tagteen.R.id.record_size_mb_text);
    this.selfiCarouselPicker =
        (CarouselPickerTextView) findViewById(in.tagteen.tagteen.R.id.carousel_selfi_component);

    this.closeScreen.setImageDrawable(getResources().getDrawable(R.drawable.ic_cross));
    this.cameraLayout = (RelativeLayout) findViewById(in.tagteen.tagteen.R.id.camera_Layout);
    SharedPreferenceSingleton.getInstance().init(this);

    List<CarouselPickerTextView.PickerItem> textItems = new ArrayList<>();
    if (this.disablePhoto == false) {
      textItems.add(new CarouselPickerTextView.TextItem("PICTURE", 16));
    }
    if (this.disableVideo == false) {
      textItems.add(new CarouselPickerTextView.TextItem("VIDEO", 16));
    }
    this.textAdapter = new CarouselPickerTextView.CarouselViewAdapter(this, textItems, 0);
    this.selfiCarouselPicker.setAdapter(textAdapter);
    this.onAddCameraClicked();
  }

  private void bindEvents() {
    this.closeScreen.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        finish();
      }
    });
    this.recordButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        onRecordButtonClicked();
      }
    });
    this.flashSwitchView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        onFlashSwitcClicked();
      }
    });
    this.cameraSwitchView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        onSwitchCameraClicked();
      }
    });
    this.mediaActionSwitchView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        onMediaActionSwitchClicked();
      }
    });
    this.selfiCarouselPicker.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
    this.cameraLayout.setVisibility(View.VISIBLE);
    this.textAdapter.notifyDataSetChanged();
  }

  @RequiresPermission(Manifest.permission.CAMERA)
  public void addCamera() {
    final Configuration.Builder builder = new Configuration.Builder();
    if (this.isFrontCamera) {
      builder.setCamera(Configuration.CAMERA_FACE_FRONT)
          .setFlashMode(Configuration.FLASH_MODE_AUTO);
    } else {
      builder.setCamera(Configuration.CAMERA_FACE_REAR)
          .setFlashMode(Configuration.FLASH_MODE_AUTO);
    }
    if (this.disablePhoto) {
      builder.setMediaAction(Configuration.MEDIA_ACTION_VIDEO);
    }

    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        != PackageManager.PERMISSION_GRANTED) {
      return;
    }
    final CameraFragment cameraFragment = CameraFragment.newInstance(builder.build());
    getSupportFragmentManager().beginTransaction()
        .replace(in.tagteen.tagteen.R.id.content, cameraFragment, TAG)
        .commit();

    if (cameraFragment != null) {
      cameraFragment.setResultListener(new CameraFragmentResultListener() {
        @Override
        public void onVideoRecorded(String filePath) {
          mediaActionSwitchView.setVisibility(View.GONE);
          Intent data = new Intent();
          data.putExtra(Constants.PATH_VIDEO_RECORDED, filePath);
          setResult(RESULT_OK, data);
          finish();
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
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      });

      cameraFragment.setStateListener(new CameraFragmentStateAdapter() {
        @Override
        public void onCurrentCameraBack() {
          //cameraSwitchView.setVisibility(View.GONE);
        }

        @Override
        public void onCurrentCameraFront() {
          //cameraSwitchView.setImageDrawable(null);
          //cameraSwitchView.setVisibility(View.GONE);
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
          flashSwitchView.setEnabled(false);
        }

        @Override
        public void unLockControls() {
          cameraSwitchView.setEnabled(true);
          recordButton.setEnabled(true);
          flashSwitchView.setEnabled(true);
        }

        @Override
        public void allowCameraSwitching(boolean allow) {
          if (!disableCameraSwitching) {
            cameraSwitchView.setVisibility(allow ? View.VISIBLE : View.GONE);
          }
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
            recordDurationText.setText("" + (recordDuration - a));
            if (recordDuration - a == 0) {
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

  private void setupImageDisplay(String imagePath, byte[] data) {
    bm = BitmapFactory.decodeByteArray(data, 0, data.length);
    this.bm = this.scalePic(imagePath, this.bm);
    //bm = scaleDown(bm, true);//scaling down bitmap
  }

  private Bitmap scalePic(String imagePath, Bitmap realImage) {
    File file = new File(imagePath);
    try {
      ExifInterface exif = new ExifInterface(file.toString());
      int orientation = exif.getAttributeInt(
          ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
      switch (orientation) {
        case ExifInterface.ORIENTATION_ROTATE_90:
          realImage = rotate(realImage, 90);
          break;

        case ExifInterface.ORIENTATION_ROTATE_180:
          realImage = rotate(realImage, 180);
          break;

        case ExifInterface.ORIENTATION_ROTATE_270:
          realImage = rotate(realImage, 270);
          break;

        default:
          realImage = rotate(realImage, 0);
          break;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return realImage;
  }

  private Bitmap scaleDown(Bitmap realImage, boolean filter) {
    int screenWidth = getResources().getDisplayMetrics().widthPixels;
    int screenHeight = getResources().getDisplayMetrics().heightPixels;

    Bitmap scaled;
    if (getResources().getConfiguration().orientation
        == android.content.res.Configuration.ORIENTATION_PORTRAIT) {
      Log.d(TAG, "Camera orientation portrait");
      scaled = ScalingUtilities.createScaledBitmap(bm, screenHeight, screenWidth,
          ScalingUtilities.ScalingLogic.FIT);
      int w = scaled.getWidth();
      int h = scaled.getHeight();
      // Setting post rotate to 90
      Matrix mtx = new Matrix();
            /*mtx.postRotate(90);
            // Rotating Bitmap
            float[] mirrorY = {-1, 0, 0, 0, 1, 0, 0, 0, 1};
            Matrix matrixMirrorY = new Matrix();
            matrixMirrorY.setValues(mirrorY);
            mtx.postConcat(matrixMirrorY);
            mtx.preRotate(270);*/
      mtx.preRotate(90);
      realImage = Bitmap.createBitmap(scaled, 0, 0, w, h, mtx, true);
    } else {// LANDSCAPE MODE
      //No need to reverse width and height
      Log.d(TAG, "Camera orientation landscape");
      scaled = ScalingUtilities.createScaledBitmap(realImage, screenHeight, screenWidth,
          ScalingUtilities.ScalingLogic.FIT);
      int w = scaled.getWidth();
      int h = scaled.getHeight();
      // Setting post rotate to 90

      Matrix mtx = new Matrix();

      // if (cameraFront==true) {
      float[] mirrorY = {-1, 0, 0, 0, 1, 0, 0, 0, 1};
      Matrix matrixMirrorY = new Matrix();
      matrixMirrorY.setValues(mirrorY);

      mtx.postConcat(matrixMirrorY);
      //  }
      mtx.postRotate(90);
      // Rotating Bitmap
      realImage = Bitmap.createBitmap(scaled, 0, 0, w, h, mtx, filter);
    }
    return realImage;
  }

  private Bitmap rotate(Bitmap bitmap, int degree) {
    int w = bitmap.getWidth();
    int h = bitmap.getHeight();

    Matrix mtx = new Matrix();
    mtx.setRotate(degree);
        /*if (isFrontCamera) {
            int px = (int) (w / 2f);
            int py = (int) (h / 2f);
            mtx.postScale(-1, 1, px, py);
        }*/
    return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
  }

  private void onFlashSwitcClicked() {
    final CameraFragmentApi cameraFragment = getCameraFragment();
    if (cameraFragment != null) {
      cameraFragment.toggleFlashMode();
    }
  }

  private void onSwitchCameraClicked() {
    final CameraFragmentApi cameraFragment = getCameraFragment();
    if (cameraFragment != null) {
      cameraFragment.switchCameraTypeFrontBack();
      isFrontCamera = !isFrontCamera;
    }
  }

  private void onRecordButtonClicked() {
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

  private void onSettingsClicked() {
    final CameraFragmentApi cameraFragment = getCameraFragment();
    if (cameraFragment != null) {
      cameraFragment.openSettingDialog();
    }
  }

  private void onMediaActionSwitchClicked() {
    final CameraFragmentApi cameraFragment = getCameraFragment();
    if (cameraFragment != null) {
      cameraFragment.switchActionPhotoVideo();
    }
    mediaActionSwitchView.setVisibility(View.GONE);
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
        if (ActivityCompat.checkSelfPermission(this, permission)
            != PackageManager.PERMISSION_GRANTED) {
          permissionsToRequest.add(permission);
        }
      }
      if (!permissionsToRequest.isEmpty()) {
        ActivityCompat.requestPermissions(
            this, permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
            REQUEST_CAMERA_PERMISSIONS);
      } else {
        this.addCamera();
      }
    } else {
      this.addCamera();
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (grantResults.length != 0) {
      if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
          != PackageManager.PERMISSION_GRANTED) {
        return;
      }
      this.addCamera();
    }
  }

  private CameraFragmentApi getCameraFragment() {
    return (CameraFragmentApi) this.getSupportFragmentManager().findFragmentByTag(TAG);
  }
}
