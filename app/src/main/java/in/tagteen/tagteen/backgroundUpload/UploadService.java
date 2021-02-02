package in.tagteen.tagteen.backgroundUpload;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.androidstudy.networkmanager.Monitor;
import com.androidstudy.networkmanager.Tovuti;

import java.io.File;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import in.tagteen.tagteen.Fragments.VideoDataSender1;
import in.tagteen.tagteen.Fragments.youthtube.bean.VideoDataSender;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.compression.KplCompressor;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.videocompressor.CompressionProgressListener;
import in.tagteen.tagteen.videocompressor.Compressor;

import static com.amazonaws.mobileconnectors.s3.transferutility.TransferType.UPLOAD;


public class UploadService extends Service {

    public static String POST_TYPE = "uploadType";
    public static String ShowRoomVideoUpload = "ShowRoomVideoUpload";
    public static String ImageTeenFeedImageUpload = "ImageTeenFeedImageUpload";
    public static String ImageTeenFeedVideoUpload = "ImageTeenFeedVideoUpload";

    public static String ProgressInProgress = "ProgressInProgress";
    public static String ProgressDone = "ProgressDone";
    private int compressedPercent;

    //showroom upload
    private VideoDataSender1 videoDataSender;
    private ShowRoomVideoUploadHelper showRoomVideoUploadHelper;
    private VideoDataSender dataSedataSender;

    //TeenFeed Video Upload MOment
    private VideoUploadMomentHelper videoUploadMomentHelper;

    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    private boolean indeterminate;
    final int Notification_ID = 100;

    private RemoteViews notificationLayout;
    private NotificationManager mNotificationManager;
    private Notification notification;

    private boolean isInternetInterrupted = false;
    private String uploadType;

    @Override
    public void onCreate() {
        super.onCreate();

        Tovuti.from(this).monitor(new Monitor.ConnectivityListener() {
            @Override
            public void onConnectivityChanged(int connectionType, boolean isConnected, boolean isFast) {
                if (isConnected) {
                    if (isInternetInterrupted) {
                        resumeAllPendingUpload();
                    }
                    isInternetInterrupted = false;
                } else {
                    isInternetInterrupted = true;
                }
            }
        });
    }

    private void resumeAllPendingUpload() {
        try {
            final TransferUtility transferUtility =
                    TransferUtility.builder()
                            .context(getApplicationContext())
                            .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                            .s3Client(new AmazonS3Client(AWSMobileClient.getInstance()))
                            .build();

            List<TransferObserver> observerList = transferUtility.getTransfersWithType(UPLOAD);
            //TODO: Need to set listeners?

            Log.d("", "Upload transfers count: " + observerList.size());
            Log.d("", "Upload transfers: " + observerList.toString());

            for (TransferObserver observer : observerList) {
                observer.refresh();
                observer.setTransferListener(this.getTransferListener(transferUtility));
            }

            transferUtility.resumeAllWithType(UPLOAD);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private TransferListener getTransferListener(final TransferUtility transferUtility) {
        TransferListener transferListener =
                new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        if (state.equals(TransferState.COMPLETED)) {
                            if (uploadType.equalsIgnoreCase(ShowRoomVideoUpload)) {
                                if (ShowRoomVideoUploadHelper.thumbNailUploadInProgress) {
                                    showRoomVideoUploadHelper.uploadVideoFile(UploadService.this);
                                } else if (ShowRoomVideoUploadHelper.videoUploadInProgress) {
                                    showRoomVideoUploadHelper.callapiPost();
                                }
                            } else if (uploadType.equalsIgnoreCase(ImageTeenFeedImageUpload)) {
                                if (VideoUploadMomentHelper.thumbNailUploadInProcess) {
                                    videoUploadMomentHelper.videoUpload();
                                } else if (VideoUploadMomentHelper.videoUploadInProcess) {
                                    videoUploadMomentHelper.callapiPost();
                                }
                            }
                        } else if (state.equals(TransferState.FAILED)) {
                            deleteTransferRecord(transferUtility, id);
                        } else if (state.equals(TransferState.WAITING_FOR_NETWORK) ||
                                state.equals(TransferState.IN_PROGRESS)) {
                            transferUtility.resume(id);
                        } else {
                            deleteTransferRecord(transferUtility, id);
                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                        int per = (int) ((bytesCurrent * 100) / bytesTotal);
                        updateProgress(per, ProgressInProgress);
                    }

                    @Override
                    public void onError(int id, Exception ex) {
                        uploadFailed(transferUtility, id);
                    }
                };

        return transferListener;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        videoDataSender = new VideoDataSender1();
        dataSedataSender = new VideoDataSender();

        notificationLayout = new RemoteViews(getPackageName(), R.layout.notification_layout);

        uploadType = intent.getStringExtra(POST_TYPE);

        createNotificationChannel();
        Intent notificationIntent = new Intent(this, UploadService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Foreground Service")
                //  .setContentText
                .setOnlyAlertOnce(true)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentIntent(pendingIntent)
                .setCustomContentView(notificationLayout)
                .build();

        startForeground(Notification_ID, notification);
        //do heavy work on a background thread
        //stopSelf();
        uploadContent(this, notificationLayout, notification, uploadType, intent);

        return START_NOT_STICKY;
    }

    private void uploadContent(
            Context context, final RemoteViews notificationLayout, final Notification notification, final String uploadType, final Intent intent) {
        try {
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Bitmap bmp = ThumbnailUtils.extractThumbnail(ThumbnailUtils.createVideoThumbnail(
                            videoDataSender.getVideoPath(), MediaStore.Video.Thumbnails.MINI_KIND), 100, 100);
                    notificationLayout.setImageViewBitmap(R.id.imageThumb, bmp);
                    notifyNotificationUi();

                    updateProgress(0, ProgressInProgress);
                    if (uploadType.equalsIgnoreCase(ShowRoomVideoUpload)) {
                        try {
                            String srcPath = videoDataSender.getVideoPath();
                            File destDir = new File(Utils.getVideoCompressionPath());
                            final File cacheFile = new File(destDir,
                                    "VIDEO_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + ".mp4"
                            );
                            updateMessage("Preparing to upload...");
                            compressedPercent = 0;
                            boolean isConverted = Compressor.INSTANCE.compressVideo(srcPath, cacheFile.getPath(), new CompressionProgressListener() {
                                @Override
                                public void onProgressChanged(float percent) {
                                    int value = (int) percent;
                                    if (value > compressedPercent) {
                                        updateProgress(value, ProgressInProgress);
                                    }
                                    compressedPercent = value;
                                }
                            });
                            compressedPercent = 0;
                            updateProgress(0, ProgressInProgress);
                            updateMessage("Uploading in progress...");
                            if (isConverted) {
                                videoDataSender.setVideoPath(cacheFile.getPath());
                            } else {
                                videoDataSender.setVideoPath(srcPath);
                            }
                            TransferNetworkLossHandler.getInstance(context);
                            showRoomVideoUploadHelper = new ShowRoomVideoUploadHelper(UploadService.this, UploadService.this);
                        } catch (Exception e) {
                            e.printStackTrace();
                            uploadFailed(null, -1);
                        }
                    } else if (uploadType.equalsIgnoreCase(ImageTeenFeedImageUpload)) {

                        Bitmap bmp1 = ThumbnailUtils.extractThumbnail(ThumbnailUtils.createVideoThumbnail(dataSedataSender.getVideoPath(), MediaStore.Video.Thumbnails.MINI_KIND), 100, 100);
                        notificationLayout.setImageViewBitmap(R.id.imageThumb, bmp1);
                        notifyNotificationUi();

                        String PostType = intent.getStringExtra("PostType");

                        videoUploadMomentHelper = new VideoUploadMomentHelper(UploadService.this, UploadService.this, PostType);
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
            uploadFailed(null,-1);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);

        }
    }

    public void sendMessage(String result, String from) {
        Intent intent = new Intent("my-event");
        // add data
        intent.putExtra("result", result);
        intent.putExtra("from", from);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void updateProgress(int per, String type) {
        try {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                public void run() {
                    notificationLayout.setProgressBar(R.id.progressUpload, 100, per, false);
                    notificationLayout.setTextViewText(R.id.textUploadingStatus, per + "%");
                    notifyNotificationUi();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void uploadFailed(TransferUtility transferUtility, int id) {
        if (getApplicationContext() == null) {
            return;
        }
        deleteTransferRecord(transferUtility, id);
        Utils.showShortToast(getApplicationContext(), "Failed to upload video");
        stopSelf();
    }

    private void deleteTransferRecord(TransferUtility transferUtility, int id) {
        if (id == -1) {
            return;
        }
        if (transferUtility == null) {
            transferUtility =
                    TransferUtility.builder()
                            .context(getApplicationContext())
                            .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                            .s3Client(new AmazonS3Client(AWSMobileClient.getInstance()))
                            .build();
        }
        transferUtility.deleteTransferRecord(id);
    }

    private void updateMessage(final String msg) {
        try {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    notificationLayout.setTextViewText(R.id.textMessage, msg);
                    notifyNotificationUi();
                }
            });
        } catch (Exception e) {

        }
    }

    public void videoUploadedWaitingForAws() {
        try {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                public void run() {
                    notificationLayout.setProgressBar(R.id.progressUpload, 100, 100, true);
                    notificationLayout.setViewVisibility(R.id.textUploadingStatus, View.GONE);
                    notificationLayout.setViewVisibility(R.id.layoutIcon, View.GONE);
                    notificationLayout.setTextViewText(R.id.textMessage, "Preparing your post, please wait");
                    notifyNotificationUi();
                }
            });
            notifyNotificationUi();

            TransferUtility transferUtility =
                    TransferUtility.builder()
                            .context(getApplicationContext())
                            .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                            .s3Client(new AmazonS3Client(AWSMobileClient.getInstance()))
                            .build();
            // clear all transfer records
            List<TransferObserver> observerList = transferUtility.getTransfersWithType(UPLOAD);
            if (observerList != null) {
                for (TransferObserver observer : observerList) {
                    this.deleteTransferRecord(transferUtility, observer.getId());
                }
            }
            new CountDownTimer(10000, 1000) {

                public void onTick(long millisUntilFinished) {

                }

                public void onFinish() {
                    sendMessage(ProgressDone, "showrooms");
                    stopSelf();
                }

            }.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void notifyNotificationUi() {
        // update the notification
        final int api = Build.VERSION.SDK_INT;
        if (api < Build.VERSION_CODES.HONEYCOMB) {
            mNotificationManager.notify(Notification_ID, notification);
        } else if (api >= Build.VERSION_CODES.HONEYCOMB) {
            mNotificationManager.notify(Notification_ID, notification);
        }
    }
}
