package in.tagteen.tagteen.chatting.paging;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import in.tagteen.tagteen.chatting.room.DBQueryExecutor;
import in.tagteen.tagteen.chatting.room.DBQueryHandler;
import in.tagteen.tagteen.chatting.room.Message;
import in.tagteen.tagteen.chatting.room.tasks.DBTaskFactory;
import in.tagteen.tagteen.chatting.room.tasks.TotalMessagesFactory;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by tony00 on 11/17/2018.
 */
public class MessageDataSource implements LifecycleObserver, IPager {

  private static final String TAG = MessageDataSource.class.getCanonicalName();

  private static final int PAGE_SIZE = 100;
  private static final int FIRST_ITEM = 1;
  private int LAST_ITEM;
  private int RECORDS_COUNT;
  private AtomicInteger PREVIOUS_PAGE = new AtomicInteger(-1);
  private int LAST_MESSAGE_OFFSET;

  private MessageLoadedListener messageLoaded;
  private ExecutorService executor;
  private String receiverId;
  private AtomicBoolean fetchingProgress = new AtomicBoolean(false);
  //private LiveData<Message> messageLiveData;
  private List<Integer> messageIds;

  private MessageDataSource(@NonNull AppCompatActivity context,
      @NonNull String receiverId) {
    this.receiverId = receiverId;

    executor = Executors.newSingleThreadExecutor();
    //executor.execute(() -> {
    //  messageLiveData = DBQueryHandler.getMessagesObserver();
    //  messageLiveData.observe(context, this::onMessageTableUpdated);
    //});

    initializeDataSource();
    initializeRecords();
  }

  public static MessageDataSource createInstanceOf(@NonNull AppCompatActivity context,
      @NonNull String receiverId) {
    if (context == null
        || receiverId == null
        || (receiverId != null && receiverId.trim().length() == 0)) {
      throw new NullPointerException("Activity context or receiver id is either null or empty");
    }

    return new MessageDataSource(context, receiverId);
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_START)
  public void onStart() {
    Log.d(TAG, "onStart: ");
    invalidate();
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
  public void onDestroy() {
    Log.d(TAG, "onDestroy: ");
    //        messageLiveData.removeObserver(this::onMessageTableUpdated);
    if (executor != null) {
      executor.shutdown();
    }
  }

  private void initializeDataSource() {
    //        Future<List<Integer>> future = executor.submit(() -> dB.getTotalMessageIds(receiverId, 0));
    try {
      messageIds = DBQueryExecutor.submitTask(DBTaskFactory.getCallableTask(
          new TotalMessagesFactory(receiverId, 0)));
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    }
  }

  private void initializeRecords() {
    RECORDS_COUNT = messageIds.size();
    LAST_ITEM = RECORDS_COUNT;
    Log.d(TAG, "Message Count: " + RECORDS_COUNT);
  }

  public void invalidate() {
    //        Future<List<Integer>> future = executor.submit(() -> dB.getTotalMessageIds(receiverId, id(LAST_ITEM-1)));
    try {
      messageIds = DBQueryExecutor.submitTask(DBTaskFactory.getCallableTask(
          new TotalMessagesFactory(receiverId, 0)));
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    }
    if (messageIds.size() != LAST_ITEM) {
      initializeRecords();
      loadAfter();
    }
  }

  private void load(int startId, int endId, boolean isLoadingAfter) {
    fetchingProgress.set(true);
    executor.execute(() -> {
      List<Message> messages =
          DBQueryHandler.getMessages(receiverId, id(startId - 1), id(endId - 1));

      if (!isLoadingAfter) {
        PREVIOUS_PAGE.set(endId == FIRST_ITEM ? -1 : endId - 1);
      }

      if (messageLoaded != null) {
        if (startId >= LAST_MESSAGE_OFFSET) {
          messageLoaded.onLoadedAfter(messages);
        } else {
          messageLoaded.onLoadedBefore(messages, areMoreItemsAvailable());
        }
      }
      fetchingProgress.set(false);
    });
  }

  @Override
  public void loadInitial() {
    if (RECORDS_COUNT == 0) {
      return;
    }

    if (!fetchingProgress.get()) {
      int startId = LAST_ITEM;
      int endId = RECORDS_COUNT > PAGE_SIZE ? LAST_ITEM - (PAGE_SIZE - 1) : FIRST_ITEM;
      LAST_MESSAGE_OFFSET = startId;

      Log.d(TAG, "INIT " + id(startId - 1) + " " + id(endId - 1));
      load(startId, endId, false);
    }
  }

  @Override
  public void loadBefore() {
    if (!fetchingProgress.get() && areMoreItemsAvailable()) {
      int startId = PREVIOUS_PAGE.get();
      int endId =
          PREVIOUS_PAGE.get() > PAGE_SIZE ? PREVIOUS_PAGE.get() - (PAGE_SIZE - 1) : FIRST_ITEM;

      Log.d(TAG, "BEFORE " + id(startId - 1) + " " + id(endId - 1));

      //            if (startId >= FIRST_ITEM)
      load(startId, endId, false);
    }
  }

  @Override
  public void loadAfter() {
    if (LAST_ITEM != LAST_MESSAGE_OFFSET) {
      int startId = LAST_ITEM;
      int endId = LAST_MESSAGE_OFFSET + 1;
      LAST_MESSAGE_OFFSET = startId;

      Log.d(TAG, "AFTER " + id(startId - 1) + " " + id(endId - 1));

      load(startId, endId, true);
    }
  }

  private int id(int position) {
    return messageIds.get(position);
  }

  public void onMessageTableUpdated(@Nullable Message message) {
    if (message == null || !message.getClientId().equals(receiverId)) {
      return;
    }

    if (InterpolationSearch.findMessagePosition(messageIds, message.getId()) == -1) {
      messageIds.add(message.getId());
      initializeRecords();
      LAST_MESSAGE_OFFSET = LAST_ITEM;

      if (messageLoaded != null) {
        messageLoaded.onNewMessage(message, false);
      }
    } else {
      if (messageLoaded != null) {
        messageLoaded.onNewMessage(message, true);
      }
    }
  }

  public boolean areMoreItemsAvailable() {
    return PREVIOUS_PAGE.get() != -1;
  }

  void setMessageLoadedListener(@Nullable MessageLoadedListener messageLoaded) {
    this.messageLoaded = messageLoaded;
  }
}
