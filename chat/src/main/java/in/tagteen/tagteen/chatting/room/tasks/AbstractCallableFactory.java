package in.tagteen.tagteen.chatting.room.tasks;

import java.util.concurrent.Callable;

/**
 * Created by tony00 on 7/13/2019.
 */
public interface AbstractCallableFactory<T> {

    Callable<T> createCallableTask();

}
