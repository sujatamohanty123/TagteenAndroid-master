package in.tagteen.tagteen.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.tagteen.tagteen.Fragments.teenfeed.PostAnArticle;
import in.tagteen.tagteen.Fragments.youthtube.bean.VideoDataSender;
import in.tagteen.tagteen.Model.AddPostTeenFeedModel;
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
 * Created by lovekushvishwakarma on 08/10/17.
 */

public class TeenFeedUpload {

    private Activity activity;
    private ProgressBar progressBar;
    private TextView textPer;
    private RelativeLayout layoutProgress;
    private VideoDataSender dataSedataSender;
    private String userid, finalVideoThumbPath,category_id;

    public static String feedTitle,feedDesc,feedCat,view_to;
    private static int imageHeight,imageWidth;
    public static boolean iscall = false;
    public static ArrayList<String> selectedPathList = new ArrayList<>();

    public TeenFeedUpload(Activity activity, RelativeLayout layoutProgress, ProgressBar progressBar, TextView textPer) {
        this.activity = activity;
        this.progressBar = progressBar;
        this.textPer = textPer;
        this.layoutProgress = layoutProgress;

        userid = SharedPreferenceSingleton.getInstance().getStringPreferenceNull(RegistrationConstants.USER_ID);

        callapiPost(feedDesc,feedTitle, view_to);
        /*if (selectedPathList.isEmpty()){

        } else{
            layoutProgress.setVisibility(View.VISIBLE);
            File file_for_selectedPath = new File(feedPicUrl);
            uploadVideoFile(file_for_selectedPath, activity, RegistrationConstants.POST_IMAGE, userid, imageHeight,imageWidth,feedDesc,view_to);
        }*/

        iscall=false;
    }


    void uploadProfileImage(final File file, Context context, String S3Folder, String userid, final int vdHeight, final int vdWidth, final String desc, String view_to) {

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

        final String finalFileName = fileName;
        mUploadMediaAwsObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state.equals(TransferState.COMPLETED)) {
                    //callapiPost(value, feedDesc,feedTitle, vdHeight, vdWidth, TeenFeedUpload.view_to);
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
                myToast(Constants.UPLOAD_FAILED_MSG);
                dataSedataSender.setIsCall(false);
            }
        });
    }

    private void callapiPost(final String feedDesc, String title, String view_to) {
        AddPostTeenFeedModel json = new AddPostTeenFeedModel();
        String userid = SharedPreferenceSingleton.getInstance().getStringPreferenceNull(RegistrationConstants.USER_ID);
        String token = SharedPreferenceSingleton.getInstance().getStringPreferenceNull(RegistrationConstants.TOKEN);
        json.setPostCreatorId(userid);
        json.setContent(feedDesc);
        json.setType(2);
        String catId=SharedPreferenceSingleton.getInstance().getStringPreference(PostAnArticle.ARTICLE_CATEGORY_ID);
        if (catId == null || catId.trim().length() == 0) {
            catId = "0";
        }
        json.setCategorie_id(Integer.parseInt(catId));
        if (title == null) {
            title = " ";
        }
        json.setTitle(title);
        if(view_to.equalsIgnoreCase("public")) {
            json.setView_to(1);
        } else if(view_to.equalsIgnoreCase("private")) {
            json.setView_to(5);
        } else if(view_to.equalsIgnoreCase("friends")) {
            json.setView_to(2);
        } else if(view_to.equalsIgnoreCase("BFF")) {
            json.setView_to(4);
        } else if(view_to.equalsIgnoreCase("Fans")) {
            json.setView_to(3);
        } else{
            json.setView_to(1);
        }

        // add image data
        if (selectedPathList != null && selectedPathList.isEmpty() == false) {
            List<AddPostTeenFeedModel.ImageDataAllToSend> imagedataall = new ArrayList<>();
            for (int i = 0; i < selectedPathList.size(); i++) {
                AddPostTeenFeedModel.ImageDataAllToSend imagedata = new AddPostTeenFeedModel.ImageDataAllToSend();
                File fileForSelectedPath = new File(selectedPathList.get(i));
                Uri selected_image_uri=Uri.fromFile(fileForSelectedPath);
                getIMGSize(selectedPathList.get(i));
                String path_from_aws = AWSUtility.uploadProfileImage(fileForSelectedPath, activity, RegistrationConstants.POST_IMAGE, userid);
                imagedata.setUrl(path_from_aws);
                imagedata.setHeight(String.valueOf(imageHeight));
                imagedata.setWidth(String.valueOf(imageWidth));
                imagedataall.add(imagedata);
            }
            json.setImage(imagedataall);
        }

        Apimethods methods = API_Call_Retrofit.getretrofit(activity).create(Apimethods.class);
        Call<CreatePostJsonResponseModel> call = methods.setpostTeenFeed(json, token);
        String url = call.request().url().toString();
        Log.d("url", "url=" + url);
        call.enqueue(new Callback<CreatePostJsonResponseModel>() {
            @Override
            public void onResponse(Call<CreatePostJsonResponseModel> call, Response<CreatePostJsonResponseModel> response) {
                if (response.code() == 200) {
                    layoutProgress.setVisibility(View.GONE);
                    myToast("Post uploaded successfully...");
                    PostAnArticle.clearDraft();
                } else if (response.code() == 401) {

                }
            }

            @Override
            public void onFailure(Call<CreatePostJsonResponseModel> call, Throwable t) {
                layoutProgress.setVisibility(View.GONE);
                dataSedataSender.setIsCall(false);
            }
        });
    }

    private void getIMGSize(String uri) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(uri, options);
        imageHeight = options.outHeight;
        imageWidth = options.outWidth;
    }

    private void myToast(String msg) {
        Utils.showShortToast(activity, msg);
    }
}
