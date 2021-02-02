package in.tagteen.tagteen.util;

import android.content.Context;
import android.graphics.PointF;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;

public class CustomLayoutManager extends LinearLayoutManager {
    private static final float MILLISECONDS_PER_INCH = 200f;
    private Context mContext;

    public CustomLayoutManager(Context context, int orientation) {
        super(context, orientation, false);
        mContext = context;
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView,
                                       RecyclerView.State state, final int position) {

        LinearSmoothScroller smoothScroller = new LinearSmoothScroller(mContext) {

            @Override
            public PointF computeScrollVectorForPosition
                    (int targetPosition) {
                return CustomLayoutManager.this
                        .computeScrollVectorForPosition(targetPosition);
            }

            @Override
            protected float calculateSpeedPerPixel
            (DisplayMetrics displayMetrics) {
                return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
            }
        };

        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
    }
}