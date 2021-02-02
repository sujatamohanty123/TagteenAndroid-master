package in.tagteen.tagteen.ZomatoFilters;

import android.content.Context;
import android.graphics.Bitmap;

import in.tagteen.tagteen.R;

import java.util.ArrayList;
import java.util.List;


public final class ThumbnailsManager {
    private static List<ThumbnailItem> filterThumbs = new ArrayList<ThumbnailItem>(10);
    private static List<ThumbnailItem> processedThumbs = new ArrayList<ThumbnailItem>(10);

    private ThumbnailsManager() {
    }

    public static void addThumb(ThumbnailItem thumbnailItem) {
        filterThumbs.add(thumbnailItem);
    }

    public static List<ThumbnailItem> processThumbs(Context context) {
        for (ThumbnailItem thumb : filterThumbs) {
            // scaling down the image
            float size = context.getResources().getDimension(R.dimen.thumbnail_size);
            thumb.image = Bitmap.createScaledBitmap(thumb.image, 150, 200, false);
            thumb.image = thumb.filter.processFilter(thumb.image);
            //cropping circle
            /*thumb.image = GeneralUtils.generateCircularBitmap(thumb.image);*/
            processedThumbs.add(thumb);
        }

        return processedThumbs;
    }

    public static void clearThumbs() {
        filterThumbs = new ArrayList<>();
        processedThumbs = new ArrayList<>();
    }
}
