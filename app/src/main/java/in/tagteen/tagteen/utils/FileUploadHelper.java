package in.tagteen.tagteen.utils;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import in.tagteen.tagteen.Fragments.beans.FileDataSender;
import in.tagteen.tagteen.Interfaces.OnFileUploadListener;
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

public class FileUploadHelper {
    private Activity activity;
    private ProgressBar progressBar;
    private TextView textPercent;
    private RelativeLayout layoutProgress;

    private String userId;
    private int postType;
    private int categoryId;
    private String filePath;
    private List<String> filePathList;
    private int imageHeight = 0, imageWidth = 0;

    private OnFileUploadListener onFileUploadListener;

    public FileUploadHelper(
            Activity activity,
            RelativeLayout layoutProgress,
            ProgressBar progressBar,
            TextView textPercent,
            int postType,
            OnFileUploadListener onFileUploadListener) {
        this.activity = activity;
        this.progressBar = progressBar;
        this.textPercent = textPercent;
        this.layoutProgress = layoutProgress;
        this.postType = postType;
        this.categoryId = FileDataSender.CATEGORY_ID;
        this.filePath = FileDataSender.FILE_PATH;
        this.filePathList = FileDataSender.FILE_PATH_LIST;
        this.userId = SharedPreferenceSingleton.getInstance().getStringPreferenceNull(RegistrationConstants.USER_ID);
        this.onFileUploadListener = onFileUploadListener;

        if (this.filePath != null && this.filePath.trim().length() > 0) {
            this.uploadFile();
        } else if (this.filePathList != null && this.filePathList.isEmpty() == false) {
            this.uploadFiles();
        }
    }

    public FileUploadHelper(
            Activity activity,
            String awsFilePath,
            int postType,
            OnFileUploadListener onFileUploadListener) {
        this.activity = activity;
        this.postType = postType;
        this.categoryId = FileDataSender.CATEGORY_ID;
        this.userId = SharedPreferenceSingleton.getInstance().getStringPreferenceNull(RegistrationConstants.USER_ID);
        this.onFileUploadListener = onFileUploadListener;

        this.createPost(awsFilePath);
    }

    private void uploadFile() {
        File file = new File(this.filePath);
        String bucket =  "tagteen-images";//AWSUtility.getBucketName();
        String fileName = file.getName();

        final String OBJECT_KEY = /*new Date().getTime() + "_" +*/ fileName;
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
                    createPost(awsFilepath);
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

    private void createPost(String awsFilepath) {
        CreatePostJsonInputModel inputModel = new CreatePostJsonInputModel();
        String userId = SharedPreferenceSingleton.getInstance().getStringPreferenceNull(RegistrationConstants.USER_ID);
        String token = SharedPreferenceSingleton.getInstance().getStringPreferenceNull(RegistrationConstants.TOKEN);
        inputModel.setPostCreatorId(userId);
        inputModel.setContent(FileDataSender.DESCRIPTION);
        inputModel.setType(this.postType);
        inputModel.setCategorie_id(this.categoryId);
        inputModel.setView_to(FileDataSender.VIEW_TO);

        if (this.filePath != null) {
            List<CreatePostJsonInputModel.ImageDataAllToSend> imagedataall = new ArrayList<>();
            CreatePostJsonInputModel.ImageDataAllToSend imagedata = new CreatePostJsonInputModel.ImageDataAllToSend();
            this.getIMGSize(this.filePath);
            imagedata.setUrl(awsFilepath);
            imagedata.setHeight(String.valueOf(imageHeight));
            imagedata.setWidth(String.valueOf(imageWidth));
            imagedataall.add(imagedata);
            inputModel.setImage(imagedataall);
        } else if (awsFilepath != null) {
            List<CreatePostJsonInputModel.ImageDataAllToSend> imagedataall = new ArrayList<>();
            CreatePostJsonInputModel.ImageDataAllToSend imagedata = new CreatePostJsonInputModel.ImageDataAllToSend();
            imagedata.setUrl(awsFilepath);
            imagedataall.add(imagedata);
            inputModel.setImage(imagedataall);
        }

        Apimethods methods = API_Call_Retrofit.getretrofit(this.activity).create(Apimethods.class);
        Call<CreatePostJsonResponseModel> call = methods.setpost(inputModel, token);
        final String url = call.request().url().toString();
        call.enqueue(new Callback<CreatePostJsonResponseModel>() {
            @Override
            public void onResponse(Call<CreatePostJsonResponseModel> call, Response<CreatePostJsonResponseModel> response) {
                if (layoutProgress != null) {
                    layoutProgress.setVisibility(View.GONE);
                }
                if (response.code() == 200) {
                    if (filePath != null) {
                        Utils.showShortToast(activity, "Successfully updated");
                    }
                    if (onFileUploadListener != null) {
                        onFileUploadListener.OnFileUploaded();
                    }
                } else if (response.code() == 401) {
                    if (onFileUploadListener != null) {
                        onFileUploadListener.onFileUploadFailed();
                    }
                }
            }

            @Override
            public void onFailure(Call<CreatePostJsonResponseModel> call, Throwable t) {
                if (layoutProgress != null) {
                    layoutProgress.setVisibility(View.GONE);
                }
                if (onFileUploadListener != null) {
                    onFileUploadListener.onFileUploadFailed();
                }
                Log.d("Create post", "Failed url:" + url);
            }
        });
    }

    private void getIMGSize(String uri) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(uri, options);
        this.imageHeight = options.outHeight;
        this.imageWidth = options.outWidth;
    }

    private void uploadFiles() {
        CreatePostJsonInputModel json = new CreatePostJsonInputModel();
        String userid = SharedPreferenceSingleton.getInstance().getStringPreferenceNull(RegistrationConstants.USER_ID);
        String token = SharedPreferenceSingleton.getInstance().getStringPreferenceNull(RegistrationConstants.TOKEN);
        json.setPostCreatorId(userid);
        json.setContent(FileDataSender.DESCRIPTION);
        json.setType(this.postType);
        json.setCategorie_id(this.categoryId);

        int shareToSelected = FileDataSender.VIEW_TO;
        if (shareToSelected == 0) {
            shareToSelected = 1;
        }
        json.setView_to(shareToSelected);

        List<CreatePostJsonInputModel.ImageDataAllToSend> imagedataall = new ArrayList<>();
        if (this.filePathList != null) {
            for (int i = 0; i < this.filePathList.size(); i++) {
                CreatePostJsonInputModel.ImageDataAllToSend imagedata = new CreatePostJsonInputModel.ImageDataAllToSend();
                Log.d("CreatePost", "File path:" + this.filePathList.get(i));
                File file = new File(this.filePathList.get(i));
                getIMGSize(this.filePathList.get(i));
                String pathFromAws = AWSUtility.uploadProfileImage(
                        file, this.activity, RegistrationConstants.POST_IMAGE, userid);
                imagedata.setUrl(pathFromAws);
                imagedata.setHeight(String.valueOf(imageHeight));
                imagedata.setWidth(String.valueOf(imageWidth));
                imagedataall.add(imagedata);
            }
            json.setImage(imagedataall);
        }

        Apimethods methods = API_Call_Retrofit.getretrofit(this.activity).create(Apimethods.class);
        Call<CreatePostJsonResponseModel> call = methods.setpost(json, token);
        final String url = call.request().url().toString();
        call.enqueue(new Callback<CreatePostJsonResponseModel>() {
            @Override
            public void onResponse(Call<CreatePostJsonResponseModel> call, Response<CreatePostJsonResponseModel> response) {
                if (response.code() == 200) {
                    Utils.showShortToast(activity, "Successfully updated");
                    if (onFileUploadListener != null) {
                        onFileUploadListener.OnFileUploaded();
                    }
                } else if (response.code() == 401) {
                    if (onFileUploadListener != null) {
                        onFileUploadListener.onFileUploadFailed();
                    }
                }
            }

            @Override
            public void onFailure(Call<CreatePostJsonResponseModel> call, Throwable t) {
                Log.d("Create post", "Failed url:" + url);
                if (onFileUploadListener != null) {
                    onFileUploadListener.onFileUploadFailed();
                }
            }
        });
    }
}
