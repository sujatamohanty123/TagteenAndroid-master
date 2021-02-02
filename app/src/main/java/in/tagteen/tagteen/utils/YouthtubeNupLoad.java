package in.tagteen.tagteen.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import in.tagteen.tagteen.Fragments.VideoDataSender1;
import in.tagteen.tagteen.Fragments.youthtube.LatestShowroomFragment;
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
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lovekushvishwakarma on 08/10/17.
 */

public class YouthtubeNupLoad {

  private Activity activity;
  private ProgressBar progressBar;
  private TextView textPer;
  private RelativeLayout layoutProgress;
  private VideoDataSender1 dataSedataSender;
  private String userid, finalVideoThumbPath, category_id;
  private String languageTypeId;
  private LatestShowroomFragment fragment;

  private String description;
  private int videoHeight;
  private int videoWidth;
  private int videoDuration;
  private String mVideoTitle;

  public YouthtubeNupLoad(
      Activity activity,
      RelativeLayout layoutProgress,
      ProgressBar progressBar,
      TextView textPer,
      LatestShowroomFragment fragment) {
    this.activity = activity;
    this.progressBar = progressBar;
    this.textPer = textPer;
    this.layoutProgress = layoutProgress;
    this.fragment = fragment;
    dataSedataSender = new VideoDataSender1();
    category_id = dataSedataSender.getVideoCategoryId();
    this.languageTypeId = dataSedataSender.getLanguageId();
    this.description = dataSedataSender.getVideoDesc();
    //Uncomment below line to pass title
    //mVideoTitle = dataSedataSender.getVideoTitle();
    String filePath = dataSedataSender.getVideoPath();
    this.videoHeight = dataSedataSender.getVideoHeight();
    this.videoWidth = dataSedataSender.getVideoWidth();
    this.videoDuration = dataSedataSender.getVideo_duration();

    userid = SharedPreferenceSingleton.getInstance()
        .getStringPreferenceNull(RegistrationConstants.USER_ID);

    if (dataSedataSender.getThumbnailPath() != null
        && dataSedataSender.getThumbnailPath().trim().length() > 0) {
      File fileThumb = new File(dataSedataSender.getThumbnailPath());
      uploadVideoThumb(fileThumb, activity, RegistrationConstants.POST_IMAGE, userid);
    } else {
      new GetVideoThub(filePath).execute();
    }
  }

  void uploadVideoFile(
      final File file,
      final Context context,
      String S3Folder,
      String userid,
      final int vdHeight,
      final int vdWidth,
      final String desc,
      final int video_duration,
      final String videoTitle) {
    final String value;
    String bucket = AWSUtility.getBucketName();
    String fileName = file.getName();
    if (fileName.length() > 20) {
      fileName = fileName.substring(fileName.length() - 20, fileName.length());
    }

    final String OBJECT_KEY = S3Folder + new Date().getTime() + "_" + userid + "_" + fileName;
    value = "https://" + bucket + ".s3.amazonaws.com/" + OBJECT_KEY;

    final TransferObserver mUploadMediaAwsObserver = AWSUtility.getTransferUtility().upload(
        bucket,     // The bucket to upload to
        OBJECT_KEY,   //  The key for the uploaded object
        file       //  The file where the data to upload exists
    );

    mUploadMediaAwsObserver.setTransferListener(new TransferListener() {
      @Override
      public void onStateChanged(int id, TransferState state) {
        if (state.equals(TransferState.COMPLETED)) {
          callapiPost(value, desc, vdHeight, vdWidth, video_duration, videoTitle);
        } else if (state.equals(TransferState.FAILED)) {
          if (fragment != null) {
            SharedPreferenceSingleton.getInstance()
                .writeStringPreference(Constants.SHOWROOM_LAST_UPLOADED_AT, null);
            fragment.onVideoUploadFailed(id);
          }
        }
      }

      @Override
      public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
        int per = (int) ((bytesCurrent * 100) / bytesTotal);
        if (fragment != null) {
          fragment.setUploadingPostId(id);
          fragment.setUploadedProgress(per);
        }
      }

      @Override
      public void onError(int id, Exception ex) {
        if (fragment != null) {
          fragment.onVideoUploadFailed(id);
        }
      }
    });
  }

  public void resumeVideoUpload(final String selectedFilePath,
      TransferObserver mUploadMediaAwsObserver) {
    mUploadMediaAwsObserver.setTransferListener(new TransferListener() {
      @Override
      public void onStateChanged(int id, TransferState state) {
        if (state.equals(TransferState.COMPLETED)) {
          callapiPost(selectedFilePath, description, videoHeight, videoWidth, videoDuration,
              mVideoTitle);
        } else if (state.equals(TransferState.FAILED)) {
          if (fragment != null) {
            fragment.onVideoUploadFailed(id);
          }
        }
      }

      @Override
      public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
        int per = (int) ((bytesCurrent / bytesTotal) * 100);
        if (fragment != null) {
          fragment.setUploadedProgress(per);
        }
      }

      @Override
      public void onError(int id, Exception ex) {
        if (fragment != null) {
          fragment.onVideoUploadFailed(id);
        }
      }
    });
  }

  void uploadVideoThumb(final File file, Context context, String S3Folder, final String userid) {

    String bucket = AWSUtility.getBucketName();
    String fileName = file.getName();
    if (fileName.length() > 20) {
      fileName = fileName.substring(fileName.length() - 20, fileName.length());
    }

    final String OBJECT_KEY = S3Folder + new Date().getTime() + "_" + userid + "_" + fileName;
    finalVideoThumbPath = "https://" + bucket + ".s3.amazonaws.com/" + OBJECT_KEY;

    TransferObserver mUploadMediaAwsObserver = AWSUtility.getTransferUtility().upload(
        bucket,     // The bucket to upload to
        OBJECT_KEY,   //  The key for the uploaded object
        file       //  The file where the data to upload exists
    );

    final String finalFileName = fileName;
    mUploadMediaAwsObserver.setTransferListener(new TransferListener() {
      @Override
      public void onStateChanged(int id, TransferState state) {
        if (state.equals(TransferState.COMPLETED)) {
          File videoFile = new File(dataSedataSender.getVideoPath());
          Log.e("VideoUpload", videoFile.length() / 1024 + " kb");
          uploadVideoFile(
              videoFile,
              activity,
              RegistrationConstants.POST_IMAGE,
              userid,
              dataSedataSender.getVideoHeight(),
              dataSedataSender.getVideoWidth(),
              dataSedataSender.getVideoDesc(),
              dataSedataSender.getVideo_duration(),
              "");// Replace empty string "" with dataSedataSender.getVideoTitle()) to pass title
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

  private void callapiPost(String selectpath, final String textstring, final int vdHeight,
      final int vdWidth, int video_duration, final String title) {
    CreatePostJsonInputModel json = new CreatePostJsonInputModel();
    String userid = SharedPreferenceSingleton.getInstance()
        .getStringPreferenceNull(RegistrationConstants.USER_ID);
    String token = SharedPreferenceSingleton.getInstance()
        .getStringPreferenceNull(RegistrationConstants.TOKEN);
    json.setPostCreatorId(userid);
    json.setContent(textstring);
    json.setType(3);
    json.setCategorie_id(Integer.valueOf(category_id));
    json.setLanguageId(Integer.valueOf(this.languageTypeId));
    json.setView_to(1);

    // add video data
    CreatePostJsonInputModel.Videodata videoData = new CreatePostJsonInputModel.Videodata();
    if (selectpath != null) {
      videoData.setHeight("" + vdHeight);
      videoData.setWidth("" + vdWidth);
      videoData.setUrl(selectpath);
      json.setVideo(videoData);
    }
    json.setVideoDuration(String.valueOf(video_duration));
    // add image data
    CreatePostJsonInputModel.ImageDataAllToSend imagedata =
        new CreatePostJsonInputModel.ImageDataAllToSend();
    List<CreatePostJsonInputModel.ImageDataAllToSend> imagedataall = new ArrayList<>();
    imagedata.setUrl(finalVideoThumbPath);
    imagedata.setHeight("" + vdHeight);
    imagedata.setWidth("" + vdWidth);
    imagedataall.add(imagedata);
    json.setVideo_thumbnail(imagedataall);

    Apimethods methods = API_Call_Retrofit.getretrofit(activity).create(Apimethods.class);
    Call<CreatePostJsonResponseModel> call = methods.setpost(json, token);
    call.enqueue(new Callback<CreatePostJsonResponseModel>() {
      @Override
      public void onResponse(@NotNull Call<CreatePostJsonResponseModel> call,
          @NotNull Response<CreatePostJsonResponseModel> response) {
        if (response.code() == 200) {
          SharedPreferenceSingleton.getInstance()
              .writeBoolPreference(Constants.IS_SHOWROOW_UPLOAD_IN_PROGRESS, false);
          SharedPreferenceSingleton.getInstance().writeStringPreference(
              Constants.SHOWROOM_LAST_UPLOADED_AT, Utils.getNowInDateString());
          layoutProgress.setVisibility(View.GONE);
          fragment.pullToRefresh();
          Utils.showToast(activity, "Your talent video has been added...");
          Utils.clearVideoCompressionFolder();
        } else {
          SharedPreferenceSingleton.getInstance()
              .writeBoolPreference(Constants.IS_SHOWROOW_UPLOAD_IN_PROGRESS, false);
          SharedPreferenceSingleton.getInstance()
              .writeStringPreference(Constants.SHOWROOM_LAST_UPLOADED_AT, null);
        }
      }

      @Override
      public void onFailure(Call<CreatePostJsonResponseModel> call, Throwable t) {
        SharedPreferenceSingleton.getInstance()
            .writeBoolPreference(Constants.IS_SHOWROOW_UPLOAD_IN_PROGRESS, false);
        SharedPreferenceSingleton.getInstance()
            .writeStringPreference(Constants.SHOWROOM_LAST_UPLOADED_AT, null);
        layoutProgress.setVisibility(View.GONE);
        dataSedataSender.setIsCall(false);
        Utils.clearVideoCompressionFolder();
      }
    });
  }

  class GetVideoThub extends AsyncTask<Void, String, String> {
    private String videoPath;

    public GetVideoThub(String videoPath) {
      this.videoPath = videoPath;
    }

    @Override
    protected String doInBackground(Void... voids) {
      Bitmap videoThumbBtm =
          ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Video.Thumbnails.MINI_KIND);

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
      File fileThumb = new File(videoThuumbpath);
      uploadVideoThumb(fileThumb, activity, RegistrationConstants.POST_IMAGE, userid);
    }
  }

  class VideoCompress extends AsyncTask<Void, String, String> {
    String vSrcPath;
    String filePath = "";
    int width = 0;
    int height = 0;
    String desc = "";
    String videoTitle = "";

    public VideoCompress(String vSrcPath, int width, int height, String desc, String title) {
      this.vSrcPath = vSrcPath;
      this.desc = desc;
      this.height = height;
      this.width = width;
      this.videoTitle = title;
    }

    @Override
    protected String doInBackground(Void... voids) {
      try {
        //filePath = SiliCompressor.with(activity).compressVideo(vSrcPath, Environment.getExternalStorageDirectory().toString());
      } catch (Exception e) {
        e.printStackTrace();
      }

      return filePath;
    }

    @Override
    protected void onPostExecute(String compressVideoPath) {
      super.onPostExecute(compressVideoPath);
      File file_for_selectedPath = new File(compressVideoPath);
      uploadVideoFile(file_for_selectedPath, activity, RegistrationConstants.POST_IMAGE, userid,
          height, width, desc, dataSedataSender.getVideo_duration(), videoTitle);
    }
  }
}
