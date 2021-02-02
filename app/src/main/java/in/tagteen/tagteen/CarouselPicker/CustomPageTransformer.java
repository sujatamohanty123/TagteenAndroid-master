package in.tagteen.tagteen.CarouselPicker;

import android.content.Context;
import android.graphics.Color;
import androidx.viewpager.widget.ViewPager;
import android.view.View;



public class CustomPageTransformer implements ViewPager.PageTransformer {

    private ViewPager viewPager;

    public CustomPageTransformer(Context context) {
    }

    public void transformPage(View view, float position) {
        if (viewPager == null) {
            viewPager = (ViewPager) view.getParent();
            viewPager.setBackgroundColor(Color.TRANSPARENT);
        }
        view.setScaleY(1-Math.abs(position));
        view.setScaleX(1-Math.abs(position));
    }

}
