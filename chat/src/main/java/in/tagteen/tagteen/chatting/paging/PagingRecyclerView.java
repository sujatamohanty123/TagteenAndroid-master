package in.tagteen.tagteen.chatting.paging;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.AttributeSet;

import in.tagteen.tagteen.chatting.room.Message;

import java.util.List;

/**
 * Created by tony00 on 11/17/2018.
 */
public class PagingRecyclerView extends RecyclerView implements MessageLoadedListener {

    private MessageDataSource dataSource;
    private ConversationAdapter cAdapter;
    private OnScrollDownListener onScrollDownListener;
    private int lastVisibleItemPosition;
    private MessageLoadedListener loadedListener;

    public PagingRecyclerView(Context context) {
        this(context, null);
    }

    public PagingRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public PagingRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setLayoutManager(new LinearLayoutManager(context));
        initAdapter();
        addOnScrollListener(onScrollListener);

    }

    public void setDataSource(MessageDataSource dataSource) {
        this.dataSource = dataSource;
        ((AppCompatActivity) getContext()).getLifecycle().addObserver(this.dataSource);
        this.dataSource.setMessageLoadedListener(this);
        this.dataSource.loadInitial();
    }

    public void reset(){
        dataSource = null;
        initAdapter();
        lastVisibleItemPosition = -1;
    }

    private void initAdapter() {
        cAdapter = new ConversationAdapter(getContext());
        setAdapter(cAdapter);
    }

    public ConversationAdapter getAdapter() {
        return cAdapter;
    }

    public void setOnScrollDownListener(OnScrollDownListener onScrollDownListener) {
        this.onScrollDownListener = onScrollDownListener;
    }

    public void setOnMessageLoadedListener(MessageLoadedListener loadedListener) {
        this.loadedListener = loadedListener;
    }

    @Override
    public void onLoadedAfter(@NonNull List<Message> messages) {
        ((AppCompatActivity) getContext()).runOnUiThread(() -> {
            cAdapter.addMessages(messages);
                scrollToEnd();
                if(loadedListener!=null)
                    loadedListener.onLoadedAfter(messages);
        });
    }

    @Override
    public void onNewMessage(@NonNull Message message, boolean isDrafted) {
        boolean isLastItemVisible = isLastItemVisible();
        cAdapter.addMessage(message, isDrafted);
        if (!(message.getMessageType() == Message.MESSAGE_IN
                && !isLastItemVisible))
            scrollToEnd();
        if(loadedListener!=null)
            loadedListener.onNewMessage(message, isDrafted);
    }

    @Override
    public void onLoadedBefore(@NonNull List<Message> messages, boolean areMoreMessagesAvailable) {
        int scrollPos = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
        ((AppCompatActivity) getContext()).runOnUiThread(() -> {
            cAdapter.submitList(messages, areMoreMessagesAvailable);
            scrollToPosition(scrollPos + messages.size() - 1);
            if(loadedListener!=null)
                loadedListener.onLoadedBefore(messages, areMoreMessagesAvailable);
        });
    }

    private OnScrollListener onScrollListener = new OnScrollListener() {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (dataSource == null)
                throw new NullPointerException("MessageDataSource must not be null");

            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

            if (layoutManager.findFirstVisibleItemPosition() == 0)
                dataSource.loadBefore();

            if (isLastItemVisible() && onScrollDownListener != null)
                onScrollDownListener.onScrolledDown();

            lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        }

    };

//    public void addMessage(Message message) {
//        int scrollPos = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
//        int msgCount = cAdapter.getItemCount();
//        cAdapter.addMessage(message);
//        dataSource.invalidateSelf();
//        if (scrollPos == msgCount - 1)
//            scrollToPosition(cAdapter.getItemCount() - 1);
//    }

    public void scrollToEnd() {
        scrollToPosition(cAdapter.getItemCount() - 1);
    }

    public boolean isLastItemVisible() {
        int scrollPos = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
        return scrollPos == cAdapter.getItemCount() - 1;
    }

    public int getLastVisibleItemPosition() {
        return lastVisibleItemPosition;
    }

    public interface OnScrollDownListener {
        void onScrolledDown();
    }

}
