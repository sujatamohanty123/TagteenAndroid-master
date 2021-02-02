package in.tagteen.tagteen.chatting.room;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Handling Database communication
 */
public class DBQueryExecutor {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public static void executeTask(@NonNull Runnable task) {
        executor.execute(task);
    }

    @WorkerThread
    public static <T> T submitTask(@NonNull Callable<T> task) throws ExecutionException, InterruptedException {
        Future<T> future = executor.submit(task);
        return future.get();
    }

    @WorkerThread
    public static  <T> T submitTask(@NonNull Runnable task,
                            T result) throws ExecutionException, InterruptedException {
        Future<T> future = executor.submit(task, result);
        return future.get();
    }

    @WorkerThread
    public static <T> Future submit(@NonNull Runnable task, T result) {
        return executor.submit(task, result);
    }

}
