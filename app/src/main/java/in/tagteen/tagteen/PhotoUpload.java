package in.tagteen.tagteen;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.theartofdev.edmodo.cropper.CropImage;
import in.tagteen.tagteen.base.AppBaseActivity;
import in.tagteen.tagteen.configurations.AWSUtility;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.selfyManager.CameraActivity;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.utils.AndroidPermissionMarshMallo;
import in.tagteen.tagteen.utils.ChooseImageDialog;
import in.tagteen.tagteen.utils.CircleTransform;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import life.knowledge4.videotrimmer.utils.AbsoluteFilePath;

import static in.tagteen.tagteen.utils.ChooseImageDialog.GALLERY_PIC_REQUEST;

public class PhotoUpload extends AppBaseActivity {
  private TextView skipText;
  private Intent it;
  private String path = "";
  private ImageView imageRound;
  private ImageView imageEditPic;
  private RelativeLayout containerProudIndian;
  private RelativeLayout containerAgreeToTerms;
  private ImageView imgProudIndian;
  private ImageView imgAgreeToTerms;
  private Button btnUploadPhoto;
  private LinearLayout containerTerms;
  private Uri mCropImageUri;
  FaceDetector detector;
  SharedPreferences preferences;
  File mImageFile = null;

  private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(in.tagteen.tagteen.R.layout.imageuploading);
    initComponent();
    //File file = new File(Environment.getExternalStorageDirectory(),
    //    "file" + String.valueOf(System.currentTimeMillis()) + ".jpg");
    //Uri uri = Uri.fromFile(file);

    AndroidPermissionMarshMallo permissionMarshMallo =
        new AndroidPermissionMarshMallo(PhotoUpload.this);
    containerTerms = findViewById(R.id.container_terms);
    containerAgreeToTerms = findViewById(R.id.container_agree_terms);
    containerProudIndian = findViewById(R.id.container_proud_indian);
    imgAgreeToTerms = findViewById(R.id.img_agree_terms);
    imgProudIndian = findViewById(R.id.img_proud_indian);
    imageRound = (ImageView) findViewById(in.tagteen.tagteen.R.id.imageView1);
    imageEditPic = (ImageView) findViewById(R.id.imageEditPic);
    btnUploadPhoto = (Button) findViewById(R.id.profile_upload_button);
    skipText = (TextView) findViewById(in.tagteen.tagteen.R.id.upload_skip);
    preferences = PreferenceManager.getDefaultSharedPreferences(this);
    String mImageUri = preferences.getString("image", null);
    if (mImageUri != null) {
      path = AbsoluteFilePath.getPath(PhotoUpload.this, Uri.parse(mImageUri));
      Glide.with(PhotoUpload.this)
          .load(Uri.parse(mImageUri)) // add your image url
          .transform(new CircleTransform(PhotoUpload.this)) // applying the image transformer
          .into(imageRound);
      editIconVisible(View.VISIBLE);
    } else {
      Glide.with(PhotoUpload.this)
          .load(R.drawable.default_userpic) // add your image url
          .transform(new CircleTransform(PhotoUpload.this)) // applying the image transformer
          .into(imageRound);
      editIconVisible(View.GONE);
    }
    SharedPreferenceSingleton.getInstance().init(this);
    initClickListeners();
  }

  private void initClickListeners() {
    btnUploadPhoto.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (mImageFile != null && imgAgreeToTerms.isSelected() && imgProudIndian.isSelected()) {
          detectImageFromFile(mImageFile, true);
        } else if (!imgAgreeToTerms.isSelected() || !imgProudIndian.isSelected()) {
          Toast.makeText(PhotoUpload.this, "Agree to terms and policies before uploading",
              Toast.LENGTH_SHORT)
              .show();
        } else {
          Toast.makeText(PhotoUpload.this, "Take Picture", Toast.LENGTH_SHORT).show();
        }
      }
    });

    skipText.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        it = new Intent(PhotoUpload.this, SignUpActivity.class);
        startActivity(it);
      }
    });

    imageEditPic.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

      }
    });
    imageRound.setOnClickListener(new View.OnClickListener() {
      @RequiresApi(api = Build.VERSION_CODES.M)
      @Override
      public void onClick(View view) {
        if (CropImage.isExplicitCameraPermissionRequired(PhotoUpload.this)) {
          requestPermissions(new String[] {android.Manifest.permission.CAMERA},
              CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE);
        } else {
          openCamera();
        }
      }
    });
    containerAgreeToTerms.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        imgAgreeToTerms.setSelected(!imgAgreeToTerms.isSelected());
      }
    });
    containerProudIndian.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        imgProudIndian.setSelected(!imgProudIndian.isSelected());
      }
    });
  }

  private void initComponent() {
    FaceDetectorOptions realTimeOpts =
        new FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
            .build();
    detector = FaceDetection.getClient(realTimeOpts);
  }

  private void openCamera() {
    Intent intent = new Intent(this, CameraActivity.class);
    intent.putExtra(Constants.DISABLE_PHOTO, false);
    intent.putExtra(Constants.DISABLE_VIDEO, true);
    intent.putExtra(Constants.DEFAULT_FRONT_CAMERA, true);
    intent.putExtra(Constants.DISABLE_CAMERA_SWITCHING, true);
    startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
        /*try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
            *//*String root = Environment.getExternalStorageDirectory().toString();
            File outPutFile = new File(root + "/tagteen");
            if (!outPutFile.exists()) {
                outPutFile.mkdirs();
            }
            Uri capturedImageUri = Uri.fromFile(new File(outPutFile + "/tagteen" + System.currentTimeMillis() + ".jpg"));
            intent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri);*//*
            startActivityForResult(intent, ChooseImageDialog.CAMERA_PIC_REQUEST);
        } catch (Exception e) {

        }*/
  }

  @Override
  public void finish() {
    super.finish();
  }

  @Override
  protected void onPostResume() {
    super.onPostResume();
  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
      if (resultCode == RESULT_OK) {
        String imagePath = data.getStringExtra(Constants.PATH_IMAGE_CAPTURED);
        if (imagePath != null) {
          path = imagePath;
          Glide.with(PhotoUpload.this)
              .load(path) // add your image url
              .transform(
                  new CircleTransform(PhotoUpload.this)) // applying the image transformer
              .into(imageRound);
          editIconVisible(View.VISIBLE);
          imageRound.setImageURI(Uri.parse(imagePath));
          imageRound.invalidate();
          mImageFile = new File(path);
          detectImageFromFile(mImageFile, false);
        }
      }
    } else if (requestCode == GALLERY_PIC_REQUEST) {
      if (resultCode == RESULT_OK) {
        if (data != null) {
          try {
            final Uri imageUri = data.getData();
            gotoCrop(imageUri);
          } catch (Exception e) {
            e.printStackTrace();
          }
          editIconVisible(View.VISIBLE);
        }
      } else if (resultCode == Activity.RESULT_CANCELED) {
        Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
      }
    } else if (requestCode == ChooseImageDialog.CAMERA_PIC_REQUEST
        && resultCode == Activity.RESULT_OK) {
      Bundle extras = data.getExtras();
      Bitmap imageBitmap = (Bitmap) extras.get("data");
      Uri picUri = Utils.getImageUri(this, imageBitmap);
      //Uri picUri = CropImage.getPickImageResultUri(this, data);
      gotoCrop(picUri);
    } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
      CropImage.ActivityResult result = CropImage.getActivityResult(data);
      if (result == null) {
        //selectedFunction(file, type, 0);
      }
      if (resultCode == RESULT_OK) {
        Uri resultUri = null;
        if (result != null) {
          resultUri = result.getUri();
          path = AbsoluteFilePath.getPath(PhotoUpload.this, resultUri);
          Glide.with(PhotoUpload.this)
              .load(path) // add your image url
              .transform(
                  new CircleTransform(PhotoUpload.this)) // applying the image transformer
              .into(imageRound);
          editIconVisible(View.VISIBLE);

          SharedPreferences preferences =
              PreferenceManager.getDefaultSharedPreferences(this);
          //SharedPreferences.Editor editor = preferences.edit();
          //editor.putString("image", String.valueOf(resultUri));
          //editor.commit();
          imageRound.setImageURI(resultUri);
          imageRound.invalidate();
        }
      } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
        if (result != null) {
          Exception error = result.getError();
        }
      }
    } else if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE
        && resultCode == Activity.RESULT_OK) {
      Uri imageUri = CropImage.getPickImageResultUri(this, data);

      // For API >= 23 we need to check specifically that we have permissions to read external storage.
      if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
        // request permissions and handle the result in onRequestPermissionsResult()
        mCropImageUri = imageUri;
        requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
            CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
      } else {
        // no permissions required or already granted, can start crop image activity
        gotoCrop(imageUri);
      }
    }
  }

  private void gotoCrop(Uri picUri) {
    CropImage.activity(picUri).start(PhotoUpload.this);
  }

  public void onRequestPermissionsResult(int requestCode, String permissions[],
      int[] grantResults) {
    if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        openCamera();
      } else {
        Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG)
            .show();
      }
    }
    if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
      if (mCropImageUri != null
          && grantResults.length > 0
          && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        // required permissions granted, start crop image activity
        gotoCrop(mCropImageUri);
      } else {
        Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG)
            .show();
      }
    }
  }

  private void editIconVisible(int visiblity) {
    imageEditPic.setVisibility(visiblity);
    btnUploadPhoto.setVisibility(visiblity);
    containerTerms.setVisibility(visiblity);
  }

  void uploadProfileImage(final File file, Context context, String S3Folder) {
    final ProgressDialog dialog = new ProgressDialog(PhotoUpload.this);
    dialog.setTitle("Uploading Image ...");
    dialog.setMessage("Please wait uploading image ...");
    dialog.show();
    final String value;
    String bucket = AWSUtility.getBucketName();
    String fileName = file.getName();
    if (fileName.length() > 20) {
      fileName = fileName.substring(fileName.length() - 20, fileName.length());
    }

    final String OBJECT_KEY = S3Folder + new Date().getTime() + "_" + "010101" + "_" + fileName;
    value = "https://" + bucket + ".s3.amazonaws.com/" + OBJECT_KEY;

    TransferObserver mUploadMediaAwsObserver = AWSUtility.getTransferUtility().upload(
        bucket,     // The bucket to upload to
        OBJECT_KEY,   //  The key for the uploaded object
        file       //  The file where the data to upload exists
    );

    mUploadMediaAwsObserver.setTransferListener(new TransferListener() {
      @Override
      public void onStateChanged(int id, TransferState state) {
        if (state.equals(TransferState.COMPLETED)) {
          //callapiPost(value, desc, vdHeight, vdWidth);
          if (dialog != null) {
            dialog.dismiss();
          }
          SharedPreferenceSingleton.getInstance()
              .writeStringPreference(RegistrationConstants.PROFILE_URL, value);
          //  Intent it = new Intent(PhotoUpload.this, PasswordActivity.class);
          Intent it = new Intent(PhotoUpload.this, SignUpActivity.class);
          startActivity(it);
        }
      }

      @Override
      public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
        int per = (int) ((bytesCurrent * 100) / bytesTotal);
      }

      @Override
      public void onError(int id, Exception ex) {
        if (PhotoUpload.this != null) {
          dialog.dismiss();
        }
      }
    });
  }

  private void detectImageFromFile(File mImageFile, boolean uploadPhoto) {
    if (mImageFile != null) {
      try {
        showProgressDialog();
        InputImage image = InputImage.fromFilePath(this, Uri.fromFile(mImageFile));
        detector.process(image).addOnSuccessListener(new OnSuccessListener<List<Face>>() {
          @Override public void onSuccess(List<Face> faces) {
            dismissProgressDialog();
            if (faces.size() > 0 && uploadPhoto) {
              SharedPreferences.Editor editor = preferences.edit();
              editor.putString("image", path);
              editor.apply();
              uploadProfileImage(mImageFile, PhotoUpload.this,
                  RegistrationConstants.POST_IMAGE);
            } else if (faces.size() == 0) {
              Toast.makeText(PhotoUpload.this,
                  R.string.imageupload_error_noface, Toast.LENGTH_SHORT)
                  .show();
            }
          }
        }).addOnFailureListener(new OnFailureListener() {
          @Override public void onFailure(@NonNull Exception e) {
            dismissProgressDialog();
            Toast.makeText(PhotoUpload.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
          }
        })
            .addOnCanceledListener(new OnCanceledListener() {
              @Override public void onCanceled() {
                dismissProgressDialog();
                Toast.makeText(PhotoUpload.this, "Cancelled", Toast.LENGTH_SHORT).show();
              }
            });
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}



