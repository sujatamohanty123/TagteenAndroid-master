package in.tagteen.tagteen.utils.reyclerutils;

import android.content.Context;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewUtils {

  private RecyclerViewUtils() {
  }

  public static RecyclerView.ItemDecoration newDividerItemDecoration(@NonNull final Context context,
      @ColorRes final int color) {
    return new LineDividerItemDecoration(ContextCompat.getColor(context, color),
        context.getResources().getDimensionPixelSize(in.tagteen.tagteen.R.dimen.line_1), false,
        true);
  }

  public static RecyclerView.ItemDecoration newDividerItemDecoration(@NonNull final Context context,
      @ColorRes final int color, @DimenRes final int dividerSize, boolean topMostDrawn,
      boolean bottomMostDrawn) {
    return new LineDividerItemDecoration(ContextCompat.getColor(context, color),
        context.getResources().getDimensionPixelSize(dividerSize), topMostDrawn, bottomMostDrawn);
  }

  public static RecyclerView.ItemDecoration newVerticalSpacingItemDecoration(
      @NonNull Context context, @DimenRes final int spacingSizeResId, boolean firstSpacing,
      boolean lastSpacing) {
    return new VerticalSpacingItemDecoration(
        context.getResources().getDimensionPixelOffset(spacingSizeResId), firstSpacing,
        lastSpacing);
  }
}
