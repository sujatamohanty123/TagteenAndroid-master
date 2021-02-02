package in.tagteen.tagteen.chatting.socket;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;

/**
 * Created by tony00 on 6/18/2019.
 */
public interface MainThreadExecutor {

    default void runOnMainThread(@NonNull Runnable task){
        new Handler(Looper.getMainLooper()).post(task);
    }

}
