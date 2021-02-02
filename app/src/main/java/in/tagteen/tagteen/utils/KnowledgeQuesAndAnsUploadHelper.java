package in.tagteen.tagteen.utils;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;

import java.io.File;

import in.tagteen.tagteen.Fragments.beans.FileDataSender;
import in.tagteen.tagteen.Interfaces.OnFileUploadListener;
import in.tagteen.tagteen.Model.knowledge.AddQuestion;
import in.tagteen.tagteen.Model.knowledge.KnowledgePostResponse;
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

public class KnowledgeQuesAndAnsUploadHelper {
    private Activity activity;
    private ProgressBar progressBar;
    private TextView textPercent;
    private RelativeLayout layoutProgress;

    private String userId;
    private String firstName;
    private String lastName;
    private String profilePic;

    private String knowledgePostType;
    private int categoryId;
    private String questionId;
    private String filePath;

    private OnFileUploadListener onFileUploadListener;

    public KnowledgeQuesAndAnsUploadHelper(
            Activity activity,
            RelativeLayout layoutProgress,
            ProgressBar progressBar,
            TextView textPercent,
            String knowledgePostType,
            String questionId,
            OnFileUploadListener onFileUploadListener) {
        this.activity = activity;
        this.progressBar = progressBar;
        this.textPercent = textPercent;
        this.layoutProgress = layoutProgress;
        this.knowledgePostType = knowledgePostType;
        this.questionId = questionId;
        this.onFileUploadListener = onFileUploadListener;

        this.categoryId = FileDataSender.CATEGORY_ID;
        this.filePath = FileDataSender.FILE_PATH;
        SharedPreferenceSingleton sharedPref = SharedPreferenceSingleton.getInstance();
        this.userId = sharedPref.getStringPreferenceNull(RegistrationConstants.USER_ID);
        this.firstName = sharedPref.getStringPreference(RegistrationConstants.FIRST_NAME);
        this.lastName = sharedPref.getStringPreference(RegistrationConstants.LAST_NAME);
        this.profilePic = sharedPref.getStringPreference(RegistrationConstants.PROFILE_URL);

        if (this.filePath != null && this.filePath.trim().length() > 0) {
            this.uploadFile();
        }
    }

    private void uploadFile() {
        File file = new File(this.filePath);
        String bucket ="tagteen-input-videos";// AWSUtility.getBucketName();
        final String OBJECT_KEY = /*new Date().getTime() + "_" +*/ file.getName();
        final String awsFilepath = "https://" + bucket + ".s3.ap-south-1.amazonaws.com/" + OBJECT_KEY;

        TransferObserver mUploadMediaAwsObserver = AWSUtility.getTransferUtility().upload(
                bucket,     // The bucket to upload to
                OBJECT_KEY,   //  The key for the uploaded object
                file       //  The file where the data to upload exists
        );

        mUploadMediaAwsObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state.equals(TransferState.COMPLETED)) {
                    if (knowledgePostType.equalsIgnoreCase(Constants.KNOWLEDGE_QUESTION_TYPE)) {
                        addQuestion(awsFilepath);
                    } else {
                        addAnswer(awsFilepath);
                    }
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                int per = (int) ((bytesCurrent * 100) / bytesTotal);
                if (progressBar != null) {
                    progressBar.setProgress(per);
                }
                if (textPercent != null) {
                    textPercent.setText(per + " %");
                }
            }

            @Override
            public void onError(int id, Exception ex) {
                if (layoutProgress != null) {
                    layoutProgress.setVisibility(View.GONE);
                    Utils.showShortToast(activity, Constants.UPLOAD_FAILED_MSG);
                }
                FileDataSender.clear();
            }
        });
    }

    private void addQuestion(String awsFilepath) {
        AddQuestion question = new AddQuestion();
        question.setCategoryId(this.categoryId);
        question.setFirstName(this.firstName);
        question.setLastName(this.lastName);
        question.setMediaLink(awsFilepath);
        question.setThumbnail(Constants.DUMMY_THUMBNAIL_URL);
        question.setProfilePic(this.profilePic);
        question.setUserId(this.userId);
        question.setTitle(FileDataSender.DESCRIPTION);

        Apimethods methods = API_Call_Retrofit.getretrofit(this.activity).create(Apimethods.class);
        Call<KnowledgePostResponse> call =
                methods.addQuestion(
                        this.userId,
                        this.firstName,
                        this.lastName,
                        this.profilePic,
                        Constants.DUMMY_THUMBNAIL_URL,
                        awsFilepath,
                        this.categoryId,
                        FileDataSender.DESCRIPTION);
        call.enqueue(new Callback<KnowledgePostResponse>() {
            @Override
            public void onResponse(Call<KnowledgePostResponse> call, Response<KnowledgePostResponse> response) {
                requestSuccess(response);
            }

            @Override
            public void onFailure(Call<KnowledgePostResponse> call, Throwable t) {
                requestFailed();
            }
        });
    }

    private void addAnswer(String awsFilepath) {
        Apimethods methods = API_Call_Retrofit.getretrofit(this.activity).create(Apimethods.class);
        Call<KnowledgePostResponse> call = methods.addAnswer(
                this.userId, this.firstName, this.lastName, this.profilePic, Constants.DUMMY_THUMBNAIL_URL, awsFilepath, this.questionId);
        call.enqueue(new Callback<KnowledgePostResponse>() {
            @Override
            public void onResponse(Call<KnowledgePostResponse> call, Response<KnowledgePostResponse> response) {
                requestSuccess(response);
            }

            @Override
            public void onFailure(Call<KnowledgePostResponse> call, Throwable t) {
                requestFailed();
            }
        });
    }

    private void requestSuccess(Response<KnowledgePostResponse> response) {
        if (layoutProgress != null) {
            layoutProgress.setVisibility(View.GONE);
        }
        if (response.code() == 200) {
            if (response.body().isSuccess()) {
                Utils.showShortToast(activity, "Successfully updated");
                if (onFileUploadListener != null) {
                    onFileUploadListener.OnFileUploaded();
                }
            }
        } else if (response.code() == 401) {
            Utils.showShortToast(activity, "Failed to upload");
            Log.e("KnowledgeFileUpload", response.message());
            if (onFileUploadListener != null) {
                onFileUploadListener.onFileUploadFailed();
            }
        } else {
            Log.e("KnowledgeFileUpload", response.message());
            Utils.showShortToast(activity, "Failed to upload");
        }
    }

    private void requestFailed() {
        Utils.showShortToast(activity, "Failed to upload");
        if (layoutProgress != null) {
            layoutProgress.setVisibility(View.GONE);
        }
        if (onFileUploadListener != null) {
            onFileUploadListener.onFileUploadFailed();
        }
    }
}
