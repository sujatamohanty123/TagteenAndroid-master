package in.tagteen.tagteen.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;

import java.io.File;

import in.tagteen.tagteen.configurations.AWSUtility;

public class AWSUploadService extends Service {
    private TransferUtility transferUtility;
    final static String INTENT_KEY_NAME = "key";
    final static String INTENT_FILE = "file";

    @Override
    public void onCreate() {
        super.onCreate();

        this.transferUtility = AWSUtility.getTransferUtility();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final String key = intent.getStringExtra(INTENT_KEY_NAME);
        final File file = (File) intent.getSerializableExtra(INTENT_FILE);
        TransferObserver transferObserver = this.transferUtility.upload(key, file);
        transferObserver.setTransferListener(new UploadListener());

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class UploadListener implements TransferListener {

        @Override
        public void onStateChanged(int id, TransferState state) {

        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

        }

        @Override
        public void onError(int id, Exception ex) {

        }
    }
}
