package in.tagteen.tagteen.utils;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.appbar.AppBarLayout;
import java.lang.ref.WeakReference;

public class AppBarScrollHelper {

  private final WeakReference<SwipeRefreshLayout> mSwipeRefreshLayout;
  private final WeakReference<AppBarLayout> mAppBarLayout;
  private final WeakReference<RecyclerView> mRecyclerView;
  private boolean mRvOnTopMost;
  private boolean mAppBarOnTopMost;

  public static void init(SwipeRefreshLayout swipeRefreshLayout, AppBarLayout appBarLayout,
      RecyclerView recyclerView) {
    new AppBarScrollHelper(swipeRefreshLayout, appBarLayout, recyclerView);
  }

  private AppBarScrollHelper(SwipeRefreshLayout swipeRefreshLayout, AppBarLayout appBarLayout,
      RecyclerView recyclerView) {
    mSwipeRefreshLayout = new WeakReference<>(swipeRefreshLayout);
    mAppBarLayout = new WeakReference<>(appBarLayout);
    mRecyclerView = new WeakReference<>(recyclerView);

    mRecyclerView.get().addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        int offset = mRecyclerView.get().computeVerticalScrollOffset();
        int extent = mRecyclerView.get().computeVerticalScrollExtent();
        int range = mRecyclerView.get().computeVerticalScrollRange();
        int percentage = (int) (100.0 * offset / (float) (range - extent));
        mRvOnTopMost = percentage == 0;
        mSwipeRefreshLayout.get().setEnabled(mRvOnTopMost && mAppBarOnTopMost);
      }
    });

    mAppBarLayout.get().addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
      @Override public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        mAppBarOnTopMost = verticalOffset == 0;
        mSwipeRefreshLayout.get().setEnabled(mAppBarOnTopMost && mRvOnTopMost);
      }
    });
  }
}
