package in.tagteen.tagteen.utils.reyclerutils;

import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;


public class VerticalSpacingItemDecoration extends RecyclerView.ItemDecoration {

  private final int mVerticalSpacing;
  private final boolean mShowFirstSpacing;
  private final boolean mShowLastSpacing;
  private final int mFirstSpacing;
  private final int mLastSpacing;

  public VerticalSpacingItemDecoration(int verticalSpacing) {
    this(verticalSpacing, false, false);
  }

  public VerticalSpacingItemDecoration(int verticalSpacing, boolean showFirstSpacing,
      boolean showLastSpacing) {
    mVerticalSpacing = verticalSpacing;
    mShowFirstSpacing = showFirstSpacing;
    mFirstSpacing = 0;
    mShowLastSpacing = showLastSpacing;
    mLastSpacing = 0;
  }

  public VerticalSpacingItemDecoration(int verticalSpacing, int firstSpacing, int lastSpacing) {
    mVerticalSpacing = verticalSpacing;
    mShowFirstSpacing = true;
    mFirstSpacing = firstSpacing;
    mShowLastSpacing = true;
    mLastSpacing = lastSpacing;
  }

  @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
      RecyclerView.State state) {
    final int position = parent.getChildAdapterPosition(view);
    if (mShowFirstSpacing && position == 0) {
      outRect.top = mFirstSpacing > 0 ? mFirstSpacing : mVerticalSpacing;
    }
    if (position != parent.getAdapter().getItemCount() - 1) {
      outRect.bottom = mVerticalSpacing;
    }
    if (mShowLastSpacing && position == parent.getAdapter().getItemCount() - 1) {
      outRect.bottom = mLastSpacing > 0 ? mLastSpacing : mVerticalSpacing;
    }
  }
}
