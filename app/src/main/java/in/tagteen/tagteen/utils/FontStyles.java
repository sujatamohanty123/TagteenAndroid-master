package in.tagteen.tagteen.utils;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by lovekushvishwakarma on 24/09/17.
 */

public class FontStyles {

    public static Typeface rockWellBold(Context mContext){
        Typeface font = Typeface.createFromAsset(mContext.getAssets(), "fonts/rockwell_bold.ttf");
        return font;
    }

    public static Typeface rockWelStd(Context mContext){
        Typeface font = Typeface.createFromAsset(mContext.getAssets(), "fonts/rockwellstd.ttf");
        return font;
    }


    public static Typeface OpenSansLight(Context mContext){
        Typeface font = Typeface.createFromAsset(mContext.getAssets(), "fonts/OpenSans-Light.ttf");
        return font;
    }


    public static Typeface font4Profile(Context mContext){
        Typeface font = Typeface.createFromAsset(mContext.getAssets(), "fonts/OpenSans-Regular.ttf");
        return font;
    }
}
