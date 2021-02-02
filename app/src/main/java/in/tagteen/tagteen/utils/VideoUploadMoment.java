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
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import in.tagteen.tagteen.Fragments.MomentsFeed;
import in.tagteen.tagteen.Fragments.youthtube.bean.VideoDataSender;
import in.tagteen.tagteen.Model.CreatePostJsonInputModel;
import in.tagteen.tagteen.Model.CreatePostJsonResponseModel;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.configurations.AWSUtility;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static in.tagteen.tagteen.configurations.AWSUtility.getAmazonS3Client;

/**
 * Created by Sujata on 22-01-2018.
 */

public class VideoUploadMoment {
    private Activity activity;
    private ProgressBar progressBar;
    private TextView textPer;
    private RelativeLayout layoutProgress;
    private VideoDataSender dataSedataSender;
    private String userid, finalVideoThumbPath;
    private MomentsFeed momentsfeed;
    private OnVideoUploadListener onVideoUploadListener;

    public VideoUploadMoment(Activity activity, RelativeLayout layoutProgress, ProgressBar progressBar, TextView textPer, MomentsFeed momentsfeed) {
        this.activity = activity;
        this.progressBar = progressBar;
        this.textPer = textPer;
        this.layoutProgress = layoutProgress;
        this.momentsfeed = momentsfeed;
        this.dataSedataSender = new VideoDataSender();
        String filePath = this.dataSedataSender.getVideoPath();
        this.userid = SharedPreferenceSingleton.getInstance().getStringPreferenceNull(RegistrationConstants.USER_ID);

        new GetVideoThub(filePath).execute();
    }

    public VideoUploadMoment(Activity activity, RelativeLayout layoutProgress, ProgressBar progressBar, TextView textPer) {
        this.activity = activity;
        this.progressBar = progressBar;
        this.textPer = textPer;
        this.layoutProgress = layoutProgress;
        this.dataSedataSender = new VideoDataSender();
        String filePath = this.dataSedataSender.getVideoPath();
        this.userid = SharedPreferenceSingleton.getInstance().getStringPreferenceNull(RegistrationConstants.USER_ID);

        new GetVideoThub(filePath).execute();
    }

    public void setOnVideoUploadListener(OnVideoUploadListener onVideoUploadListener) {
        this.onVideoUploadListener = onVideoUploadListener;
    }

    void uploadProfileImage(
            final File file,
            Context context,
            String S3Folder,
            String userid,
            final int vdHeight,
            final int vdWidth,
            final String desc,
            final int video_duration,
            final String videoView_to) {
        final String value;
        String bucket = AWSUtility.getBucketName();
        String fileName = file.getName();
        if (fileName.length() > 20) {
            fileName = fileName.substring(fileName.length() - 20, fileName.length());
        }

        final String OBJECT_KEY = S3Folder + new Date().getTime() + "_" + userid + "_" + fileName;
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
                    callapiPost(value, desc, vdHeight, vdWidth,video_duration,videoView_to);
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                int per = (int) ((bytesCurrent * 100) / bytesTotal);
                progressBar.setProgress(per);
                textPer.setText(per + " %");
            }

            @Override
            public void onError(int id, Exception ex) {
                ex.printStackTrace();
                layoutProgress.setVisibility(View.GONE);
                Utils.showShortToast(activity, Constants.UPLOAD_FAILED_MSG);
                dataSedataSender.setIsCall(false);
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

        mUploadMediaAwsObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state.equals(TransferState.COMPLETED)) {
                    File file_for_selectedPath = new File(dataSedataSender.getVideoPath());
                    uploadProfileImage(
                            file_for_selectedPath,
                            activity,
                            RegistrationConstants.POST_IMAGE,
                            userid,
                            dataSedataSender.getVideoHeight(),
                            dataSedataSender.getVideoWidth(),
                            dataSedataSender.getVideoDesc(),
                            dataSedataSender.getVideo_duration(),
                            dataSedataSender.getVideoView_to());
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

    private void callapiPost(
            String selectpath, final String textstring, final int vdHeight, final int vdWidth, int video_duration, String videoView_to) {
        CreatePostJsonInputModel json = new CreatePostJsonInputModel();
        String userid = SharedPreferenceSingleton.getInstance().getStringPreferenceNull(RegistrationConstants.USER_ID);
        String token = SharedPreferenceSingleton.getInstance().getStringPreferenceNull(RegistrationConstants.TOKEN);
        json.setPostCreatorId(userid);
        json.setContent(textstring);
        if (this.momentsfeed != null) {
            json.setType(1);
        } else {
            // campus live post
            json.setType(4);
        }
        json.setCategorie_id(1);
        if (Integer.parseInt(videoView_to)==0) {
            videoView_to = String.valueOf(1);
            json.setView_to(Integer.parseInt(videoView_to));
        } else {
            json.setView_to(Integer.parseInt(videoView_to));
        }
        // FIXME: for now share it all public
        json.setView_to(1);

        // add video data
        CreatePostJsonInputModel.Videodata videoData = new CreatePostJsonInputModel.Videodata();
        if (selectpath != null) {
            videoData.setHeight(String.valueOf(vdHeight));
            videoData.setWidth(String.valueOf(vdWidth));
            videoData.setUrl(selectpath);
            //videoData.setVideo_duration(String.valueOf(video_duration));
            json.setVideo(videoData);
        }
        json.setVideoDuration(String.valueOf(video_duration));
        // add image data
        CreatePostJsonInputModel.ImageDataAllToSend imagedata = new CreatePostJsonInputModel.ImageDataAllToSend();
        List<CreatePostJsonInputModel.ImageDataAllToSend> imagedataall=new ArrayList<>();
        imagedata.setUrl(finalVideoThumbPath);
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
                    layoutProgress.setVisibility(View.GONE);
                    if (momentsfeed != null) {
                        SharedPreferenceSingleton.getInstance().writeBoolPreference(Constants.IS_MOMENTS_UPLOAD_IN_PROGRESS, false);
                        momentsfeed.pullToRefresh();
                    }
                    if (onVideoUploadListener != null) {
                        SharedPreferenceSingleton.getInstance().writeBoolPreference(Constants.IS_CAMPUSLIVE_UPLOAD_IN_PROGRESS, false);
                        onVideoUploadListener.OnVideoUploaded();
                    }
                    Utils.showShortToast(activity,"Post uploaded successfully...");
                } else {
                    if (momentsfeed != null) {
                        SharedPreferenceSingleton.getInstance().writeBoolPreference(Constants.IS_MOMENTS_UPLOAD_IN_PROGRESS, false);
                    }
                    if (onVideoUploadListener != null) {
                        SharedPreferenceSingleton.getInstance().writeBoolPreference(Constants.IS_CAMPUSLIVE_UPLOAD_IN_PROGRESS, false);
                    }
                }
            }

            @Override
            public void onFailure(Call<CreatePostJsonResponseModel> call, Throwable t) {
                if (momentsfeed != null) {
                    SharedPreferenceSingleton.getInstance().writeBoolPreference(Constants.IS_MOMENTS_UPLOAD_IN_PROGRESS, false);
                }
                if (onVideoUploadListener != null) {
                    SharedPreferenceSingleton.getInstance().writeBoolPreference(Constants.IS_CAMPUSLIVE_UPLOAD_IN_PROGRESS, false);
                }
                layoutProgress.setVisibility(View.GONE);
                if (t.getMessage() != null) {
                    Log.e("VideoUploadMoment", t.getMessage());
                    t.printStackTrace();
                }
                dataSedataSender.setIsCall(false);
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
            Bitmap videoThumbBtm = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Video.Thumbnails.MINI_KIND);

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

        public VideoCompress(String vSrcPath, int width, int height, String desc) {
            this.vSrcPath = vSrcPath;
            this.desc = desc;
            this.height = height;
            this.width = width;
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
            uploadProfileImage(file_for_selectedPath, activity, RegistrationConstants.POST_IMAGE, userid, height, width, desc, dataSedataSender.getVideo_duration(), dataSedataSender.getVideoView_to());

        }
    }

    public interface OnVideoUploadListener {
        void OnVideoUploaded();
    }
}
