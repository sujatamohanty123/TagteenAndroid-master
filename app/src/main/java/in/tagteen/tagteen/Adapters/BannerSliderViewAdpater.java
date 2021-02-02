package in.tagteen.tagteen.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.smarteist.autoimageslider.SliderViewAdapter;

import in.tagteen.tagteen.R;

public class BannerSliderViewAdpater extends SliderViewAdapter<BannerSliderViewAdpater.ViewHolder> {
    private Context context;

    public BannerSliderViewAdpater(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.banner_slider_row, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        switch (position) {
            case 0:
                viewHolder.imgBanner.setImageResource(R.drawable.banner_knowledge);
                break;

            case 1:
                viewHolder.imgBanner.setImageResource(R.drawable.banner_rockstar);
                break;

            case 2:
                viewHolder.imgBanner.setImageResource(R.drawable.banner_selfie);
                break;

            case 3:
                viewHolder.imgBanner.setImageResource(R.drawable.banner_webshow);
                break;
        }
    }

    @Override
    public int getCount() {
        //slider view count could be dynamic size
        return 4;
    }

    class ViewHolder extends SliderViewAdapter.ViewHolder {
        private View itemView;
        private ImageView imgBanner;

        public ViewHolder(View view) {
            super(view);
            this.imgBanner = view.findViewById(R.id.imgBanner);
            this.itemView = view;
        }
    }
}
