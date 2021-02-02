package in.tagteen.tagteen.util;

import android.content.Context;
import android.graphics.Canvas;

import androidx.annotation.Nullable;

import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by lovekushvishwakarma on 11/10/17.
 */

public class BorderTextView  extends TextView {

    public BorderTextView(Context context) {
        super(context);
    }

    public BorderTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BorderTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BorderTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void draw(Canvas canvas) {
        for (int i = 0; i < 5; i++) {
            super.draw(canvas);
        }
    }
}