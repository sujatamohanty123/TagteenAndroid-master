package in.tagteen.tagteen.utils.reyclerutils;

import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

public class LineDividerItemDecoration extends RecyclerView.ItemDecoration {
  private final Drawable mDivider;

  private final int mLineHeightPx;

  private final boolean mIsTopmostDrawn;
  private final boolean mIsBottommostDrawn;

  /**
   * Create a new line divider item decoration with specific {@code color} that only draws line
   * between each item.
   *
   * @see LineDividerItemDecoration#LineDividerItemDecoration(int, int, boolean, boolean)
   */
  public LineDividerItemDecoration(int color, int heightPx) {
    this(color, heightPx, false, false);
  }

  /**
   * Create a new line divider item decoration with specific {@code color}.
   *
   * @param heightPx A height for this line divider
   * @param isTopmostDrawn Whether this line will be drawn above of the topmost view.
   * @param isBottommostDrawn Whether this line will be drawn below the bottommost view.
   */
  public LineDividerItemDecoration(int color, int heightPx, boolean isTopmostDrawn,
      boolean isBottommostDrawn) {
    mDivider = new ShapeDrawable(new RectShape());
    mDivider.setColorFilter(color, PorterDuff.Mode.SRC_IN);
    mLineHeightPx = heightPx >= 0 ? heightPx : 0;

    mIsTopmostDrawn = isTopmostDrawn;
    mIsBottommostDrawn = isBottommostDrawn;
  }

  @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
      RecyclerView.State state) {
    outRect.top = shouldDrawTopLine(parent, view) ? mLineHeightPx : 0;
    outRect.bottom = shouldDrawBottomLine(parent, view) ? mLineHeightPx : 0;
  }

  @Override public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
    int left = parent.getPaddingLeft();
    int right = parent.getWidth() - parent.getPaddingRight();

    int childCount = parent.getChildCount();
    for (int i = 0; i < childCount; i++) {
      final View child = parent.getChildAt(i);
      final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
      int leftPoint = left + child.getPaddingLeft();
      int rightPoint = right - child.getPaddingRight();
      if (shouldDrawTopLine(parent, child)) {
        drawLine(c, leftPoint, child.getTop() - params.topMargin - mLineHeightPx, rightPoint);
      }

      if (shouldDrawBottomLine(parent, child)) {
        drawLine(c, leftPoint, child.getBottom() + params.bottomMargin, rightPoint);
      }
    }
  }

  /** @return true if top line should be drawn for {@code view}. */
  private boolean shouldDrawTopLine(RecyclerView parent, View view) {
    final int itemAdapterPosition = parent.getChildAdapterPosition(view);
    final boolean isTopmostItem = itemAdapterPosition == 0;
    return isTopmostItem && mIsTopmostDrawn;
  }

  /** @return true if bottom line should be drawn for {@code view}. */
  private boolean shouldDrawBottomLine(RecyclerView parent, View view) {
    final int itemAdapterPosition = parent.getChildAdapterPosition(view);
    final int itemCount = parent.getAdapter() == null ? 0 : parent.getAdapter().getItemCount();
    final boolean isBottommostItem = itemAdapterPosition == itemCount - 1;
    return !isBottommostItem || mIsBottommostDrawn;
  }

  private void drawLine(Canvas c, int left, int top, int right) {
    final int bottom = top + mLineHeightPx;
    mDivider.setBounds(left, top, right, bottom);
    mDivider.draw(c);
  }
}
