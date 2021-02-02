package in.tagteen.tagteen.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.alexvasilkov.gestures.views.GestureImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

import in.tagteen.tagteen.R;
import in.tagteen.tagteen.util.Utils;

/**
 * Created by Mathivanan on 19-01-2018.
 */

public class MyCustomPagerAdapter2 extends PagerAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<String> pathlist=null;
    private MyCustomPagerAdapter2.OnItemClickListener mOnItemClickListener;
    private int flag = 0;
    private Bitmap loadedBitmap;
    private byte[] loadedBitmapAsBytes;

    public interface OnItemClickListener {
        void onItemClick(View view, String imagePath, int position);
    }

    public void setOnItemClickListener(final MyCustomPagerAdapter2.OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public MyCustomPagerAdapter2(Context context, Bitmap loadedBitmap, ArrayList<String> pathlist, int flag) {
        this.context = context;
        this.loadedBitmap = loadedBitmap;
        this.loadedBitmapAsBytes = Utils.getBitmapAsByteArray(this.loadedBitmap);
        this.pathlist = pathlist;
        this.flag = flag;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return pathlist.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = layoutInflater.inflate(R.layout.item, container, false);
        GestureImageView imageView = (GestureImageView) itemView.findViewById(R.id.imageView);
        final ProgressBar loading_spinner = (ProgressBar) itemView.findViewById(R.id.loading_spinner);

        if (position == 0 && this.loadedBitmap != null) {
            loading_spinner.setVisibility(View.GONE);
            Glide.with(this.context)
                    .load(this.loadedBitmapAsBytes)
                    .fitCenter()
                    .into(imageView);
        } else {
            Glide.with(context)
                    .load(pathlist.get(position))
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            loading_spinner.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            loading_spinner.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .fitCenter()
                    .into(imageView);
        }

        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}