package in.tagteen.tagteen.backgroundUpload;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import in.tagteen.tagteen.Fragments.youthtube.bean.VideoDataSender;
import in.tagteen.tagteen.Model.CreatePostJsonInputModel;
import in.tagteen.tagteen.Model.CreatePostJsonResponseModel;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.configurations.AWSUtility;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static in.tagteen.tagteen.backgroundUpload.UploadService.ProgressDone;
import static in.tagteen.tagteen.backgroundUpload.UploadService.ProgressInProgress;

/**
 * Created by Sujata on 22-01-2018.
 */

public class VideoUploadMomentHelper {
    private Context activity;
    private VideoDataSender dataSedataSender;
    private UploadService uploadService;
    private String PostType;

    public static boolean thumbNailUploadInProcess = false;
    public static boolean videoUploadInProcess = false;

    public static String awsThumbPath, awsVideoPath;


    public VideoUploadMomentHelper(Context activity, UploadService uploadService, String PostType) {
        this.activity = activity;
        this.PostType = PostType;
        this.uploadService = uploadService;
        this.dataSedataSender = new VideoDataSender();

        new GetVideoThub().execute();
    }


    void videoUpload() {

        String S3Folder = RegistrationConstants.POST_IMAGE;
        File file = new File(dataSedataSender.getVideoPath());
        String bucket = "tagteen-input-videos";//AWSUtility.getBucketName();
        String fileName = file.getName();
        if (fileName.length() > 20) {
            fileName = fileName.substring(fileName.length() - 20, fileName.length());
        }

        String userid = SharedPreferenceSingleton.getInstance().getStringPreferenceNull(RegistrationConstants.USER_ID);

        final String OBJECT_KEY = S3Folder + new Date().getTime() + "_" + userid + "_" + fileName;
        awsVideoPath = "https://" + bucket + ".s3.ap-south-1.amazonaws.com/" + OBJECT_KEY;

        videoUploadInProcess = true;
        TransferObserver mUploadMediaAwsObserver = AWSUtility.getTransferUtility().upload(
                bucket,     // The bucket to upload to
                OBJECT_KEY,   //  The key for the uploaded object
                file       //  The file where the data to upload exists
        );

        mUploadMediaAwsObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state.equals(TransferState.COMPLETED)) {
                    videoUploadInProcess = false;
                    callapiPost();
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                int per = (int) ((bytesCurrent * 100) / bytesTotal);
                uploadService.updateProgress(per, ProgressInProgress);
            }

            @Override
            public void onError(int id, Exception ex) {
                ex.printStackTrace();
                // layoutProgress.setVisibility(View.GONE);
//                Utils.showShortToast(activity, Constants.UPLOAD_FAILED_MSG);
//                dataSedataSender.setIsCall(false);
            }
        });
    }

    void uploadVideoThumb() {
        String S3Folder = RegistrationConstants.POST_IMAGE;
        File file = new File(dataSedataSender.getVideoThumb());

        String bucket = "tagteen-images";//AWSUtility.getBucketName();
        String fileName = file.getName();
        if (fileName.length() > 20) {
            fileName = fileName.substring(fileName.length() - 20, fileName.length());
        }

        String userid = SharedPreferenceSingleton.getInstance().getStringPreferenceNull(RegistrationConstants.USER_ID);

        final String OBJECT_KEY = S3Folder + new Date().getTime() + "_" + userid + "_" + fileName;
        awsThumbPath = "https://" + bucket + ".s3.ap-south-1.amazonaws.com/" + OBJECT_KEY;
        thumbNailUploadInProcess = true;
        TransferObserver mUploadMediaAwsObserver = AWSUtility.getTransferUtility().upload(
                bucket,     // The bucket to upload to
                OBJECT_KEY,   //  The key for the uploaded object
                file       //  The file where the data to upload exists
        );

        mUploadMediaAwsObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state.equals(TransferState.COMPLETED)) {
                    thumbNailUploadInProcess = false;
                    videoUpload();
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

    public void callapiPost() {


        int vdHeight = dataSedataSender.getVideoHeight();
        int vdWidth = dataSedataSender.getVideoWidth();
        String textstring = dataSedataSender.getVideoDesc();
        int video_duration = dataSedataSender.getVideo_duration();
        String videoView_to = dataSedataSender.getVideoView_to();

        CreatePostJsonInputModel json = new CreatePostJsonInputModel();
        String userid = SharedPreferenceSingleton.getInstance().getStringPreferenceNull(RegistrationConstants.USER_ID);
        String token = SharedPreferenceSingleton.getInstance().getStringPreferenceNull(RegistrationConstants.TOKEN);
        json.setPostCreatorId(userid);
        json.setContent(textstring);
        if (PostType != null && PostType.equalsIgnoreCase("Moment")) {
            json.setType(1);
        } else {
            // campus live post
            json.setType(4);
        }
        json.setCategorie_id(1);
        if (Integer.parseInt(videoView_to) == 0) {
            videoView_to = String.valueOf(1);
            json.setView_to(Integer.parseInt(videoView_to));
        } else {
            json.setView_to(Integer.parseInt(videoView_to));
        }
        // FIXME: for now share it all public
        json.setView_to(1);

        // add video data
        CreatePostJsonInputModel.Videodata videoData = new CreatePostJsonInputModel.Videodata();
        if (awsVideoPath != null) {
            videoData.setHeight(String.valueOf(vdHeight));
            videoData.setWidth(String.valueOf(vdWidth));
            videoData.setUrl(awsVideoPath);
            //videoData.setVideo_duration(String.valueOf(video_duration));
            json.setVideo(videoData);
        }
        json.setVideoDuration(String.valueOf(video_duration));
        // add image data
        CreatePostJsonInputModel.ImageDataAllToSend imagedata = new CreatePostJsonInputModel.ImageDataAllToSend();
        List<CreatePostJsonInputModel.ImageDataAllToSend> imagedataall = new ArrayList<>();
        imagedata.setUrl(awsThumbPath);
        imagedata.setHeight("" + vdHeight);
        imagedata.setWidth("" + vdWidth);
        imagedataall.add(imagedata);
        json.setVideo_thumbnail(imagedataall);

        Apimethods methods = API_Call_Retrofit.getretrofit(activity).create(Apimethods.class);
        Call<CreatePostJsonResponseModel> call = methods.setpost(json, token);
        String url = call.request().url().toString();

        call.enqueue(new Callback<CreatePostJsonResponseModel>() {
            @Override
            public void onResponse(Call<CreatePostJsonResponseModel> call, Response<CreatePostJsonResponseModel> response) {
                if (response.code() == 200) {

                    uploadService.sendMessage(ProgressDone,"Moments");
                    uploadService.stopSelf();

//                    layoutProgress.setVisibility(View.GONE);
//                    if (momentsfeed != null) {
//                        SharedPreferenceSingleton.getInstance().writeBoolPreference(Constants.IS_MOMENTS_UPLOAD_IN_PROGRESS, false);
//                        momentsfeed.pullToRefresh();
//                    }
//                    if (onVideoUploadListener != null) {
//                        SharedPreferenceSingleton.getInstance().writeBoolPreference(Constants.IS_CAMPUSLIVE_UPLOAD_IN_PROGRESS, false);
//                        onVideoUploadListener.OnVideoUploaded();
//                    }
                    //Utils.showShortToast(activity, "Post uploaded successfully...");
                } else {
//                    if (momentsfeed != null) {
//                        SharedPreferenceSingleton.getInstance().writeBoolPreference(Constants.IS_MOMENTS_UPLOAD_IN_PROGRESS, false);
//                    }
//                    if (onVideoUploadListener != null) {
//                        SharedPreferenceSingleton.getInstance().writeBoolPreference(Constants.IS_CAMPUSLIVE_UPLOAD_IN_PROGRESS, false);
//                    }
                }
            }

            @Override
            public void onFailure(Call<CreatePostJsonResponseModel> call, Throwable t) {
//                if (momentsfeed != null) {
//                    SharedPreferenceSingleton.getInstance().writeBoolPreference(Constants.IS_MOMENTS_UPLOAD_IN_PROGRESS, false);
//                }
//                if (onVideoUploadListener != null) {
//                    SharedPreferenceSingleton.getInstance().writeBoolPreference(Constants.IS_CAMPUSLIVE_UPLOAD_IN_PROGRESS, false);
//                }
//                layoutProgress.setVisibility(View.GONE);
//                if (t.getMessage() != null) {
//                    Log.e("VideoUploadMoment", t.getMessage());
//                    t.printStackTrace();
//                }
                dataSedataSender.setIsCall(false);
            }
        });
    }

    class GetVideoThub extends AsyncTask<Void, String, String> {

        String filePath = dataSedataSender.getVideoPath();

        @Override
        protected String doInBackground(Void... voids) {
            Bitmap videoThumbBtm = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Video.Thumbnails.MINI_KIND);

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
            dataSedataSender.setVideoThumb(videoThuumbpath);
            uploadVideoThumb();
        }
    }


}
