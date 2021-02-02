package cn.jzvd.demo.CustomView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import cn.jzvd.JZVideoPlayerStandard;
import cn.jzvd.R;


//import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Just replace thumb from ImageView to SimpleDraweeView
 * Created by Nathen
 * On 2016/05/01 22:59
 */
public class JZVideoPlayerStandardFresco extends JZVideoPlayerStandard {
    //    public SimpleDraweeView thumbImageView;

    public JZVideoPlayerStandardFresco(Context context) {
        super(context);
    }

    public JZVideoPlayerStandardFresco(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void init(Context context) {
        super.init(context);
        loadingProgressBar = (ProgressBar) findViewById(R.id.loading);
        tinyBackImageView = (ImageView) findViewById(R.id.back_tiny);
        tinyBackImageView.setOnClickListener(this);

    }

    @Override
    public void setUp(String url, int screen, Object... objects) {
        super.setUp(url, screen, objects);
        if (objects.length == 0) return;
        if (currentScreen == SCREEN_WINDOW_FULLSCREEN) {
            fullscreenButton.setImageResource(R.drawable.jz_shrink);
            tinyBackImageView.setVisibility(View.INVISIBLE);
        } else if (currentScreen == SCREEN_LAYOUT_LIST) {
            fullscreenButton.setImageResource(R.drawable.jz_enlarge);
            tinyBackImageView.setVisibility(View.INVISIBLE);
        } else if (currentScreen == SCREEN_WINDOW_TINY) {
            tinyBackImageView.setVisibility(View.VISIBLE);
            setAllControlsVisible(View.INVISIBLE, View.INVISIBLE, View.INVISIBLE,
                    View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_standard_fresco;
    }


}
