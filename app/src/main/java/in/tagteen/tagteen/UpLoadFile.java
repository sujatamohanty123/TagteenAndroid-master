package in.tagteen.tagteen;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.File;
import java.util.Date;

import in.tagteen.tagteen.LocalCasha.DataBaseHelper;
import in.tagteen.tagteen.LocalCasha.DatabaseContracts;
import in.tagteen.tagteen.TagteenInterface.MediaUploadDone;
import in.tagteen.tagteen.configurations.AWSUtility;


public class UpLoadFile {



    SQLiteDatabase db;
    DataBaseHelper dbHelper;

    public String uploadChatFiles(final File file , final String ClientDBId , Context context, final String messageType, String userId, final MediaUploadDone mediaUploadDone)  {
        dbHelper = new DataBaseHelper(context);
        db = dbHelper.getWritableDatabase();
         File mCapturedImageFile;
         final String mAwsFileUrl;
         AmazonS3Client mS3Client;
         TransferUtility mTransferUtility;
        final String value;
        String bucket = AWSUtility.getBucketName();
        String fileName = file.getName();

     //file, this, msgType, userId, new MediaUploadDone()

        mCapturedImageFile = file;
        if(fileName.length() > 20){
            fileName = fileName.substring(fileName.length() - 20,fileName.length());
        }

        final String OBJECT_KEY =getFileType(messageType) +new Date().getTime()+"_"+ userId +"_"+fileName;
        mAwsFileUrl = "https://"+bucket+".s3.amazonaws.com/" + OBJECT_KEY;
        value = mAwsFileUrl;

        mS3Client = AWSUtility.getAmazonS3Client();
        mS3Client.setRegion(Region.getRegion(Regions.fromName(AWSUtility.getAWSRegionKey())));

        TransferObserver mUploadMediaAwsObserver = AWSUtility.getTransferUtility().upload(
                bucket,     // The bucket to upload to
                OBJECT_KEY,   //  The key for the uploaded object
                mCapturedImageFile       //  The file where the data to upload exists
        );

        mUploadMediaAwsObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state.equals(TransferState.COMPLETED)){
                    updateMyChat(ClientDBId,mAwsFileUrl);
                    mediaUploadDone.mediaUploadDone(messageType,file.getPath(),mAwsFileUrl,ClientDBId);
                }else{

                }
            }
            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                int per = (int) ((bytesCurrent * 100) / bytesTotal);
                mediaUploadDone.mediaUploadonProgress(ClientDBId,per);
            }


            @Override
            public void onError(int id, Exception ex) {
                ex.printStackTrace();
                mediaUploadDone.mediaUploadError(ClientDBId);
            }
        });
        return value;
    }

    public String getFileType(String messageType){
        String folder=null;
        if (messageType.equals(DatabaseContracts.ISIMAGE + "")) {
            folder= AWSUtility.CHAT_GALLERY_IMAGE;
        } else if (messageType.equals(DatabaseContracts.ISAUDIO + "")) {
            folder= AWSUtility.CHAT_AUDIO;
        } else if (messageType.equals(DatabaseContracts.ISVEDIO + "")) {
            folder= AWSUtility.CHAT_VIDEO;
        } else if (messageType.equals(DatabaseContracts.ISDOC + "")) {
            folder= AWSUtility.CHAT_DOCUMENTS;
        }
        return folder;

    }
    private boolean updateMyChat(String id,String mAwsUrl) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContracts.ChatContractDataBase.MEDIA_UPLOAD_STATUS,DatabaseContracts.IS_TRUE);
        contentValues.put(DatabaseContracts.ChatContractDataBase.MEDIA_LINK,mAwsUrl);
        db.update( DatabaseContracts.ChatContractDataBase.CHAT_MESSANGER_TABLE, contentValues, DatabaseContracts.ChatContractDataBase._ID + "= ? ", new String[]{id});
        return true;
    }
}
