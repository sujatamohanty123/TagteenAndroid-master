package in.tagteen.tagteen.configurations;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import in.tagteen.tagteen.TagteenInterface.AWSFaceractionResponse;
import in.tagteen.tagteen.TagteenInterface.MediaUploadDone;
import in.tagteen.tagteen.utils.TagteenApplication;
import java.io.File;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import org.json.JSONException;
import org.json.JSONObject;

public class AWSUtility {
  private static File mCapturedImageFile;
  private static String mAwsFileUrl;

  private static AmazonS3Client mS3Client;
  private static AWSCredentialsProvider sMobileClient;
  private static TransferUtility mTransferUtility;
  private static File file;
  private static Context mContext;
  private static String S3Folder;
  private static MediaUploadDone mediaUploadDone;

  public static String PROFILE_IMAGE = "Profile/profileImages";
  public static String FACE_REACTION_IMAGE = "Self_Reaction";
  public static String CHAT_GALLERY_IMAGE = "Profile/Images";
  public static String CHAT_DOCUMENTS = "Profile/Doc";
  public static String CHAT_VIDEO = "Profile/Video";
  public static String CHAT_AUDIO = "Profile/Audio";

  private static ProgressDialog pDialog;

    /*public static void uploadProfileImage(final File file, Context context, String S3Folder, String userid,
                                          final MediaUploadDone mediaUploadDone, final boolean isImage, final boolean isvideo, final boolean isDoc) {
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
        String value = null;
        String bucket = getBucketName();
        String fileName = file.getName();
        mCapturedImageFile = file;
        if (fileName.length() > 20) {
            fileName = fileName.substring(fileName.length() - 20, fileName.length());
        }

        final String OBJECT_KEY = S3Folder + new Date().getTime() + "_" + userid + "_" + fileName;
        mAwsFileUrl = "https://" + bucket + ".s3.amazonaws.com/" + OBJECT_KEY;
        value = mAwsFileUrl;

        mS3Client = getAmazonS3Client();
        mS3Client.setRegion(Region.getRegion(Regions.fromName(getAWSRegionKey())));

        //mTransferUtility = new TransferUtility(mS3Client, context);
        mTransferUtility = TransferUtility.builder().s3Client(mS3Client).context(context).build();

        TransferObserver mUploadMediaAwsObserver = mTransferUtility.upload(
                bucket,     // The bucket to upload to
                OBJECT_KEY,   //  The key for the uploaded object
                mCapturedImageFile       //  The file where the data to upload exists
        );

        Log.e("uploded", mAwsFileUrl);
        final String finalFileName = fileName;
        mUploadMediaAwsObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                 *//*    if (state.equals(TransferState.COMPLETED)){
                      mediaDownloadDone.mediaDownloadDone(isImage,isvideo,isDoc, finalFileName,mAwsFileUrl);
                     }else{
                     }*//*
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                int per = (int) ((bytesCurrent * 100) / bytesTotal);
*//*
                    mediaUploadDone.mediaUploadonProgress(isImage,isvideo,isDoc,file);
*//*
                pDialog.setMessage(per + " " + "Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.hide();
            }

            @Override
            public void onError(int id, Exception ex) {
                ex.printStackTrace();
                pDialog.setMessage("error in uploading..");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.show();

            }
        });
    }*/

  public static String uploadProfileImage(final File file, Context context, String S3Folder,
      String userid) {
    final String value;
    String bucket = "tagteen-images";// getBucketName();
    String fileName = file.getName();
    mCapturedImageFile = file;
    if (fileName.length() > 20) {
      fileName = fileName.substring(fileName.length() - 20, fileName.length());
    }

    final String OBJECT_KEY = S3Folder + new Date().getTime() + "_" + userid + "_" + fileName;
    mAwsFileUrl = "https://" + bucket + ".s3.ap-south-1.amazonaws.com/" + OBJECT_KEY;
    value = mAwsFileUrl;

    mS3Client = getAmazonS3Client();
    mS3Client.setRegion(Region.getRegion(Regions.fromName(getAWSRegionKey())));

    //mTransferUtility = new TransferUtility(mS3Client, context);
    mTransferUtility = TransferUtility.builder().s3Client(mS3Client).context(context).build();

    TransferObserver mUploadMediaAwsObserver = mTransferUtility.upload(
        bucket,     // The bucket to upload to
        OBJECT_KEY,   //  The key for the uploaded object
        mCapturedImageFile       //  The file where the data to upload exists
    );

    Log.e("uploded", mAwsFileUrl);
    final String finalFileName = fileName;
    mUploadMediaAwsObserver.setTransferListener(new TransferListener() {
      @Override
      public void onStateChanged(int id, TransferState state) {
        if (state.equals(TransferState.COMPLETED)) {

        } else {

        }
      }

      @Override
      public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
        int per = (int) ((bytesCurrent * 100) / bytesTotal);
      }

      @Override
      public void onError(int id, Exception ex) {
        ex.printStackTrace();
      }
    });
    return value;
  }

  public static void uploadFaceReactionImage(final File file, Context context, String userid,
      final String Reaction, final AWSFaceractionResponse awsFaceractionResponse) {
    pDialog = new ProgressDialog(context);
    pDialog.setMessage("Please wait...");
    pDialog.setIndeterminate(false);
    pDialog.setCancelable(false);
    pDialog.show();
    String value = null;
    String bucket = getBucketName();
    String fileName = file.getName();
    mCapturedImageFile = file;
    if (fileName.length() > 20) {
      fileName = fileName.substring(fileName.length() - 20, fileName.length());
    }

    final String OBJECT_KEY = S3Folder + new Date().getTime() + "_" + userid + "_" + fileName;
    //final String OBJECT_KEY = FACE_REACTION_IMAGE +"_"+ userid +"_"+Reaction;
    mAwsFileUrl = "https://" + bucket + ".s3.amazonaws.com/" + OBJECT_KEY;
    value = mAwsFileUrl;

    mS3Client = getAmazonS3Client();
    mS3Client.setRegion(Region.getRegion(Regions.fromName(getAWSRegionKey())));

    //mTransferUtility = new TransferUtility(mS3Client, context);
    mTransferUtility = TransferUtility.builder().s3Client(mS3Client).context(context).build();

    TransferObserver mUploadMediaAwsObserver = mTransferUtility.upload(
        bucket,     // The bucket to upload to
        OBJECT_KEY,   //  The key for the uploaded object
        mCapturedImageFile       //  The file where the data to upload exists
    );

    Log.e("uploded", mAwsFileUrl);
    final String finalFileName = fileName;
    mUploadMediaAwsObserver.setTransferListener(new TransferListener() {
      @Override
      public void onStateChanged(int id, TransferState state) {
        if (state.equals(TransferState.COMPLETED)) {
          awsFaceractionResponse.sentReaction(mAwsFileUrl, Reaction);
        } else {
        }
      }

      @Override
      public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
        int per = (int) ((bytesCurrent * 100) / bytesTotal);
        awsFaceractionResponse.onUploadReaction(mAwsFileUrl, Reaction, per);
        pDialog.setMessage(per + " " + "Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.hide();
      }

      @Override
      public void onError(int id, Exception ex) {
        ex.printStackTrace();
        awsFaceractionResponse.onerror(mAwsFileUrl, Reaction);
        pDialog.setMessage("error in uploading..");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
      }
    });
  }

  public static AmazonS3Client getAmazonS3Client() {
    if (mS3Client == null) {
      Context context = TagteenApplication.getContext();
      mS3Client = new AmazonS3Client(
          getCredentialsProvider(context),
          Region.getRegion(Regions.fromName(getAWSRegionKey())));
      try {
        String regionString = new AWSConfiguration(context)
            .optJsonObject("S3TransferUtility").getString("Region");
        mS3Client.setRegion(Region.getRegion(regionString));
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
        /*return new AmazonS3Client(
                new BasicAWSCredentials(getAWSAccessKey(), getAWSSecreteKey()),
                Region.getRegion(Regions.fromName(AWSUtility.getAWSRegionKey())));*/
    return mS3Client;
  }

  private static AWSCredentialsProvider getCredentialsProvider(Context context) {
    if (context == null) {
      context = TagteenApplication.getContext();
    }
    if (sMobileClient == null) {
      final CountDownLatch latch = new CountDownLatch(1);
      AWSMobileClient.getInstance().initialize(context, new Callback<UserStateDetails>() {
        @Override
        public void onResult(UserStateDetails result) {
          latch.countDown();
        }

        @Override
        public void onError(Exception e) {
          latch.countDown();
        }
      });
      try {
        latch.await();
        sMobileClient = AWSMobileClient.getInstance();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    return sMobileClient;
  }

  public static TransferUtility getTransferUtility() {
    if (mTransferUtility == null) {
      Context context = TagteenApplication.getContext();
      mTransferUtility = TransferUtility.builder()
          .context(context)
          .s3Client(getAmazonS3Client())
          .awsConfiguration(new AWSConfiguration(context))
          .build();
    }
    return mTransferUtility;
  }

  public static String getAWSRegionKey() {
    String regionString = null;
    try {
      AWSConfiguration config = new AWSConfiguration(TagteenApplication.getContext());
      JSONObject s3Obj = config.optJsonObject("S3TransferUtility");
      regionString = s3Obj.getString("Region");
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return regionString;
  }

  public static String getBucketName() {
      //try {
    //    AWSConfiguration config = new AWSConfiguration(TagteenApplication.getContext());
    //    JSONObject s3Obj = config.optJsonObject("S3TransferUtility");
    //    bucketString = s3Obj.getString("Bucket");
    //} catch (JSONException e) {
    //    e.printStackTrace();
    //
    return "tagteen-images";
  }
}
