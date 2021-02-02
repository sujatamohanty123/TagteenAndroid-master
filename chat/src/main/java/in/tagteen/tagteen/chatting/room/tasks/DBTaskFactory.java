package in.tagteen.tagteen.chatting.room.tasks;

import androidx.annotation.NonNull;

import java.util.concurrent.Callable;

/**
 * Created by tony00 on 7/13/2019.
 */
public final class DBTaskFactory {

    public static Runnable getRunnableTask(@NonNull AbstractRunnableFactory factory) {
        return factory.createRunnableTask();
    }

    public static <T> Callable<T> getCallableTask(@NonNull AbstractCallableFactory<T> factory) {
        return factory.createCallableTask();
    }

}
