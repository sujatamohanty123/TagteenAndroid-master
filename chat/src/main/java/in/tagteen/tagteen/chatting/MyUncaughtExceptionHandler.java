package in.tagteen.tagteen.chatting;

import android.app.Activity;
import android.content.Intent;

import in.tagteen.tagteen.chatting.socket.OfflineEmitterService;

public class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Activity activity;

    public MyUncaughtExceptionHandler(Activity a) {
        activity = a;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Intent intent = new Intent(activity, OfflineEmitterService.class);
        intent.putExtra(OfflineEmitterService.EXTRA_DATA, OfflineEmitterService.EVENT_LEFT_DISCONNECT);
        activity.startService(intent);
        activity.finish();
    }
}
