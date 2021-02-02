package in.tagteen.tagteen.selfyManager;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Date;

import in.tagteen.tagteen.Fragments.MomentsFeed;
import in.tagteen.tagteen.configurations.AWSUtility;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.configurations.RequestConstants;
import in.tagteen.tagteen.configurations.ServerConnector;
import in.tagteen.tagteen.networkEngine.AsyncResponse;
import in.tagteen.tagteen.networkEngine.AsyncWorker;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;

import static in.tagteen.tagteen.configurations.AWSUtility.getAmazonS3Client;

/**
 * Created by Sujata on 18-06-2018.
 */

public class VideoUploadMoment_selfy implements AsyncResponse {
    private Activity activity;
    private ProgressBar progressBar;
    private TextView textPer;
    private RelativeLayout layoutProgress;
    private VideoDataSender_selfy dataSedataSender;
    String userid, finalVideoThumbPath;
    MomentsFeed momentsfeed;

    public VideoUploadMoment_selfy(Activity activity, RelativeLayout layoutProgress, ProgressBar progressBar, TextView textPer, MomentsFeed momentsfeed) {
        this.activity = activity;
        this.progressBar = progressBar;
        this.textPer = textPer;
        this.layoutProgress = layoutProgress;
        this.momentsfeed=momentsfeed;
        dataSedataSender = new VideoDataSender_selfy();
        String filePath = dataSedataSender.getVideoPath();

        userid = SharedPreferenceSingleton.getInstance().getStringPreferenceNull(RegistrationConstants.USER_ID);

       // new VideoUploadMoment_selfy.GetVideoThub(filePath).execute();
        File file_for_selectedPath = new File(dataSedataSender.getVideoPath());
        uploadProfileImage(file_for_selectedPath, activity,userid, dataSedataSender.getVideoDesc());

    }

    void uploadProfileImage(final File file, Context context, final String userid, final String desc) {
        final String S3Folder = RegistrationConstants.POST_IMAGE;
        String bucket ="tagteen-images";// AWSUtility.getBucketName();
        String fileName = file.getName();
        if (fileName.length() > 20) {
            fileName = fileName.substring(fileName.length() - 20, fileName.length());
        }

        final String OBJECT_KEY = S3Folder + new Date().getTime() + "_" + userid + "_" + fileName;
        final String url = "https://" + bucket + ".s3.ap-south-1.amazonaws.com/" + OBJECT_KEY;

        TransferObserver mUploadMediaAwsObserver = AWSUtility.getTransferUtility().upload(
                bucket,     // The bucket to upload to
                OBJECT_KEY,   //  The key for the uploaded object
                file       //  The file where the data to upload exists
        );
        mUploadMediaAwsObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state.equals(TransferState.COMPLETED)) {
                    uploadSelfy(url,desc);
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                int per = (int) ((bytesCurrent * 100) / bytesTotal);
                System.out.print("Process " + per + " %");
                progressBar.setProgress(per);
                textPer.setText(per + " %");
            }

            @Override
            public void onError(int id, Exception ex) {
                ex.printStackTrace();
                layoutProgress.setVisibility(View.GONE);
                myToast("Uploading failed, connection timed out");
                dataSedataSender.setIsCall(false);
            }
        });
    }

    public void uploadSelfy(String value, String desc) {
        AsyncWorker mWorker = new AsyncWorker(activity);
        mWorker.delegate = this;
        mWorker.delegate = this;
        JSONObject BroadcastObject = new JSONObject();
        String userid = SharedPreferenceSingleton.getInstance().getStringPreferenceNull(RegistrationConstants.USER_ID);
        String token = SharedPreferenceSingleton.getInstance().getStringPreferenceNull(RegistrationConstants.TOKEN);
        try {

            JSONObject video = new JSONObject();
           // JSONObject image = new JSONObject();

            video.put("url", value);
            video.put("height", "521");
            video.put("width", "512");

            BroadcastObject.put("postCreatorId", userid);
            BroadcastObject.put("content", desc);
            BroadcastObject.put("title", desc);
            BroadcastObject.put("video", video);
            //BroadcastObject.put("image", image);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        mWorker.execute(ServerConnector.REQUEST_ADD_SELFY, BroadcastObject.toString(), RequestConstants.POST_REQUEST, RequestConstants.HEADER_NO, RequestConstants.REQUEST_FOR_LOGIN);
    }
    @Override
    public void onRefresh() {

    }

    @Override
    public void ReceivedResponseFromServer(String output, String REQUEST_NUMBER) {
        System.out.print(output);
        try {
            JSONObject jsonObject = new JSONObject(output);
            boolean success = jsonObject.getBoolean("success");
            if (success) {
                layoutProgress.setVisibility(View.GONE);
                momentsfeed.pullToRefresh();
                myToast("Clippie has been added to moments");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void myToast(String msg) {
        Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
    }
}
