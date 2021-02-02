package in.tagteen.tagteen.backgroundUpload;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import in.tagteen.tagteen.Fragments.VideoDataSender1;
import in.tagteen.tagteen.Model.CreatePostJsonInputModel;
import in.tagteen.tagteen.Model.CreatePostJsonResponseModel;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.configurations.AWSUtility;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static in.tagteen.tagteen.backgroundUpload.UploadService.ProgressInProgress;

/**
 * Created by lovekushvishwakarma on 08/10/17.
 */

public class ShowRoomVideoUploadHelper {

  private Context activity;
  private VideoDataSender1 dataSedataSender;
  private String category_id;
  private String languageTypeId;
  private UploadService uploadService;

  public static boolean thumbNailUploadInProgress = false;
  public static boolean videoUploadInProgress = false;

  public static String awsImagepath, awsVideoPath;

  public ShowRoomVideoUploadHelper(Context activity, UploadService uploadService) {
    this.activity = activity;
    this.uploadService = uploadService;

    dataSedataSender = new VideoDataSender1();
    category_id = dataSedataSender.getVideoCategoryId();
    this.languageTypeId = dataSedataSender.getLanguageId();

    if (dataSedataSender.getThumbnailPath() != null
            && dataSedataSender.getThumbnailPath().trim().length() > 0) {
      uploadVideoThumb(activity);
    } else {
      new GetVideoThub().execute();
    }
  }

  public void uploadVideoFile(final Context context) {
    String S3Folder = RegistrationConstants.POST_IMAGE;
    final String userid = SharedPreferenceSingleton.getInstance()
            .getStringPreferenceNull(RegistrationConstants.USER_ID);
    File file = new File(dataSedataSender.getVideoPath());

    String bucket = "tagteen-input-videos";//AWSUtility.getBucketName();
    String fileName = file.getName();
    if (fileName.length() > 20) {
      fileName = fileName.substring(fileName.length() - 20, fileName.length());
    }

    final String OBJECT_KEY = S3Folder + new Date().getTime() + "_" + userid + "_" + fileName;
    awsVideoPath = "https://" + bucket + ".s3.ap-south-1.amazonaws.com/" + OBJECT_KEY;
    videoUploadInProgress = true;
    TransferNetworkLossHandler.getInstance(context);
    final TransferObserver mUploadMediaAwsObserver = AWSUtility.getTransferUtility().upload(
            bucket,     // The bucket to upload to
            OBJECT_KEY,   //  The key for the uploaded object
            file       //  The file where the data to upload exists
    );

    mUploadMediaAwsObserver.setTransferListener(new TransferListener() {
      @Override
      public void onStateChanged(int id, TransferState state) {

        if (state.equals(TransferState.COMPLETED)) {
          videoUploadInProgress = false;
          callapiPost();
        } else if (state.equals(TransferState.FAILED)) {
          uploadService.uploadFailed(null, id);
        }
      }

      @Override
      public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
        int per = (int) ((bytesCurrent * 100) / bytesTotal);
        uploadService.updateProgress(per, ProgressInProgress);
      }

      @Override
      public void onError(int id, Exception ex) {
        uploadService.uploadFailed(null, id);
      }
    });
  }

  public void uploadVideoThumb(Context context) {
    String S3Folder = RegistrationConstants.POST_IMAGE;
    final String userid = SharedPreferenceSingleton.getInstance()
            .getStringPreferenceNull(RegistrationConstants.USER_ID);
    final File file = new File(dataSedataSender.getThumbnailPath());
    String bucket = "tagteen-images";//AWSUtility.getBucketName();
    String fileName = file.getName();
    if (fileName.length() > 20) {
      fileName = fileName.substring(fileName.length() - 20);
    }

    final String OBJECT_KEY = S3Folder + new Date().getTime() + "_" + userid + "_" + fileName;
    awsImagepath = "https://" + bucket + ".s3.ap-south-1.amazonaws.com/" + OBJECT_KEY;
    thumbNailUploadInProgress = true;

    TransferObserver mUploadMediaAwsObserver = AWSUtility.getTransferUtility().upload(
            bucket,     // The bucket to upload to
            OBJECT_KEY,   //  The key for the uploaded object
            file       //  The file where the data to upload exists
    );

    mUploadMediaAwsObserver.setTransferListener(new TransferListener() {
      @Override
      public void onStateChanged(int id, TransferState state) {
        if (state.equals(TransferState.COMPLETED)) {
          thumbNailUploadInProgress = false;
          uploadVideoFile(activity);
        }
      }

      @Override
      public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
      }

      @Override
      public void onError(int id, Exception ex) {
        ex.printStackTrace();
      }
    });
  }

  //    final CountDownTimer timer = new CountDownTimer(60000, 1000) {
  //
  //        public void onTick(long millisUntilFinished) {
  //
  //        }
  //
  //        public void onFinish() {
  //            Utils.clearVideoCompressionFolder();
  //            uploadService.sendMessage(ProgressDone, "showrooms");
  //            uploadService.stopSelf();
  //            timer.cancel();
  //        }
  //
  //    };

  public void callapiPost() {

    int vdHeight = dataSedataSender.getVideoHeight();
    int vdWidth = dataSedataSender.getVideoWidth();
    String textstring = dataSedataSender.getVideoDesc();
    int video_duration = dataSedataSender.getVideo_duration();
    //Uncomment below line to pass title
    //String title = dataSedataSender.getVideoTitle();

    CreatePostJsonInputModel json = new CreatePostJsonInputModel();
    String userid = SharedPreferenceSingleton.getInstance()
            .getStringPreferenceNull(RegistrationConstants.USER_ID);
    String token = SharedPreferenceSingleton.getInstance()
            .getStringPreferenceNull(RegistrationConstants.TOKEN);
    json.setPostCreatorId(userid);
    json.setContent(textstring);
    json.setType(3);
    //Uncomment below line to pass title
    //json.setTitle(title);
    json.setCategorie_id(Integer.valueOf(category_id));
    json.setLanguageId(Integer.valueOf(this.languageTypeId));
    json.setView_to(1);

    // add video data
    CreatePostJsonInputModel.Videodata videoData = new CreatePostJsonInputModel.Videodata();
    if (awsVideoPath != null) {
      videoData.setHeight("" + vdHeight);
      videoData.setWidth("" + vdWidth);
      videoData.setUrl(awsVideoPath);
      json.setVideo(videoData);
    }
    json.setVideoDuration(String.valueOf(video_duration));
    // add image data
    CreatePostJsonInputModel.ImageDataAllToSend imagedata =
            new CreatePostJsonInputModel.ImageDataAllToSend();
    List<CreatePostJsonInputModel.ImageDataAllToSend> imagedataall = new ArrayList<>();
    imagedata.setUrl(awsImagepath);
    imagedata.setHeight("" + vdHeight);
    imagedata.setWidth("" + vdWidth);
    imagedataall.add(imagedata);
    json.setVideo_thumbnail(imagedataall);

    Apimethods methods = API_Call_Retrofit.getretrofit(activity).create(Apimethods.class);
    Call<CreatePostJsonResponseModel> call = methods.setpost(json, token);
    call.enqueue(new Callback<CreatePostJsonResponseModel>() {
      @Override
      public void onResponse(Call<CreatePostJsonResponseModel> call,
                             Response<CreatePostJsonResponseModel> response) {
        if (response.code() == 200) {
          SharedPreferenceSingleton.getInstance()
                  .writeBoolPreference(Constants.IS_SHOWROOW_UPLOAD_IN_PROGRESS, false);
          SharedPreferenceSingleton.getInstance()
                  .writeStringPreference(Constants.SHOWROOM_LAST_UPLOADED_AT,
                          Utils.getNowInDateString());

          Utils.clearVideoCompressionFolder();
          uploadService.videoUploadedWaitingForAws();
          //                    Utils.clearVideoCompressionFolder();
          //                    uploadService.sendMessage(ProgressDone, "showrooms");
          //                    uploadService.stopSelf();
        } else {
          SharedPreferenceSingleton.getInstance()
                  .writeBoolPreference(Constants.IS_SHOWROOW_UPLOAD_IN_PROGRESS, false);
          SharedPreferenceSingleton.getInstance()
                  .writeStringPreference(Constants.SHOWROOM_LAST_UPLOADED_AT, null);
        }
      }

      @Override
      public void onFailure(Call<CreatePostJsonResponseModel> call, Throwable t) {
        //                SharedPreferenceSingleton.getInstance().writeBoolPreference(Constants.IS_SHOWROOW_UPLOAD_IN_PROGRESS, false);
        //                SharedPreferenceSingleton.getInstance().writeStringPreference(Constants.SHOWROOM_LAST_UPLOADED_AT, null);
        //                dataSedataSender.setIsCall(false);
        //                Utils.clearVideoCompressionFolder();
      }
    });
  }

  class GetVideoThub extends AsyncTask<Void, String, String> {

    @Override
    protected String doInBackground(Void... voids) {
      Bitmap videoThumbBtm = ThumbnailUtils.createVideoThumbnail(dataSedataSender.getVideoPath(),
              MediaStore.Video.Thumbnails.MINI_KIND);

      String root = Environment.getExternalStorageDirectory().toString();
      File myDir = new File(root + "/saved_images");
      myDir.mkdirs();
      Random generator = new Random();
      int n = 10000;
      n = generator.nextInt(n);
      String fname = "Image-" + n + ".jpg";
      File file = new File(myDir, fname);
      if (file.exists()) file.delete();
      try {
        FileOutputStream out = new FileOutputStream(file);
        videoThumbBtm.compress(Bitmap.CompressFormat.JPEG, 100, out);
        out.flush();
        out.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
      return file.toString();
    }

    @Override
    protected void onPostExecute(String videoThuumbpath) {
      super.onPostExecute(videoThuumbpath);
      dataSedataSender.setThumbnailPath(videoThuumbpath);
      // File fileThumb = new File(videoThuumbpath);

      uploadVideoThumb(activity);
    }
  }
}
