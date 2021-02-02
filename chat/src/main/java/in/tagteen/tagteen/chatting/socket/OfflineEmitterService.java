package in.tagteen.tagteen.chatting.socket;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Process;
import androidx.annotation.Nullable;
import android.util.Log;

import in.tagteen.tagteen.chatting.model.UserStatus;

public class OfflineEmitterService extends Service {

    private static final String TAG = OfflineEmitterService.class.getCanonicalName();
//    private static final String TAG = "GLOBAL TAG SOCKET..";

    public static final String EXTRA_DATA = OfflineEmitterService.class.getCanonicalName()+".ExtraData";

    public static final int EVENT_LEFT = 0x283;
    public static final int EVENT_LEFT_DISCONNECT = 0x284;

    private Reminder reminder;
    private SocketConnection socketConnection;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service Created");
        reminder = new Reminder();
        socketConnection = SocketConnection.getConnection();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Service Started");

        int event = 0;
        if (intent != null) {
            event = intent.getIntExtra(EXTRA_DATA, 0);
        }
        if(event == EVENT_LEFT_DISCONNECT) {
           SocketConnection.getConnection().emitUserStatus(UserStatus.DISCONNECTED);
        }
        if (this.reminder == null) {
            this.reminder = new Reminder();
        }
        this.reminder.start();
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.i(TAG, "TaskRunnable Removed");
//        reminder.stop();
//        SocketConnection.getConnection().emitUserStatus(UserStatus.DISCONNECTED);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Service Stopped");
        reminder.stop();
    }

    private class Reminder implements Runnable {

        static final int WAITING_INTERVAL = 15000;

        private Handler handler;
        private boolean isRunning;
//        private boolean isRescheduled;

        Reminder() {
            HandlerThread handlerThread = new HandlerThread("OfflineStarter",
                    Process.THREAD_PRIORITY_BACKGROUND);
            handlerThread.start();
            handler = new Handler(handlerThread.getLooper());
        }

        void start() {
            Log.i(TAG, "Timer Started");
            handler.postDelayed(this, WAITING_INTERVAL);
            isRunning = true;
        }

        void stop() {
            if (isRunning) {
                Log.i(TAG, "Timer Stopped");
                handler.removeCallbacks(this);
            }
            isRunning = false;
//            isRescheduled = false;
        }

//        void reSchedule() {
//            start();
//            isRescheduled = true;
//        }

        @Override
        public void run() {
            stop();
            new Handler(getMainLooper()).post(()->{
                Log.d(TAG, "executeTask: "+SocketConnection.getConnection());
                socketConnection.emitUserStatus(UserStatus.LEFT);
                socketConnection.emitUserStatus(UserStatus.DISCONNECTED);
            });
            stopSelf();
//            if (!isRescheduled) {
//                Log.i(TAG, "Emitted Offline");
//                SocketConnection.getConnection(getApplicationContext()).emitUserStatus(UserStatus.LEFT);
//                reSchedule();
//            } else {
//                Log.i(TAG, "Emitted left");
//                SocketConnection.getConnection(getApplicationContext()).emitUserStatus(UserStatus.LEFT);
//                isRescheduled = false;
//                stopSelf();
//            }
        }
    }
}
