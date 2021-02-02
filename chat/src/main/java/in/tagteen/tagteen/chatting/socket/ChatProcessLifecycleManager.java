package in.tagteen.tagteen.chatting.socket;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import android.util.Log;

public class ChatProcessLifecycleManager implements LifecycleObserver {

    private static final String TAG = ChatProcessLifecycleManager.class.getCanonicalName();
//    private static final String TAG = "GLOBAL TAG SOCKET..";

    private static Context context;
    private boolean isServiceRunning;

    private ChatProcessLifecycleManager(@NonNull Context context){
        this.context = context;
    }

    public static ChatProcessLifecycleManager createInstance(@NonNull Context context){
        return new ChatProcessLifecycleManager(context);
    }

    public static Context getApplicationContext() {
        return context;
    }

    public void initialize(){
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreate() {
        Log.i(TAG, "App created");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        Log.i(TAG, "App is on foreground");

        SocketConnection socketConn = SocketConnection.getConnection();
        socketConn.connect();

        stopService();
//        if (socConn.getMyStatus() == SocketConnection.OFFLINE)
//            socConn.bindUser();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        Log.i(TAG, "App is in background");
        startService();
    }

    private void stopService(){
        if (isServiceRunning) {
            Intent intent = new Intent(getApplicationContext(),
                    OfflineEmitterService.class);
//            intent.putExtra(OfflineEmitterService.EXTRA_DATA, OfflineEmitterService.EVENT_LEFT);
            getApplicationContext().stopService(intent);
            isServiceRunning=false;
        }
    }

    private void startService(){
        if (getApplicationContext() == null) {
            return;
        }
        Intent intent = new Intent(getApplicationContext(),
                OfflineEmitterService.class);
        intent.putExtra(OfflineEmitterService.EXTRA_DATA, OfflineEmitterService.EVENT_LEFT);
        getApplicationContext().startService(intent);
        isServiceRunning = true;
    }
}
