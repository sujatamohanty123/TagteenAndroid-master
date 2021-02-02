package in.tagteen.tagteen.Filter.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import in.tagteen.tagteen.R;



public class ExFilterAdapter extends RecyclerView.Adapter<in.tagteen.tagteen.Filter.activity.ExFilterAdapter.ViewHolder> {

    int dra[]={R.drawable.wall_custom1,R.drawable.wall_custom2,
            R.drawable.wall_custom3,R.drawable.wall_custom4,
            R.drawable.wall_custom5,R.drawable.wall_custom6,
            R.drawable.wall_custom7,R.drawable.wall_custom8,
            R.drawable.wall_custom9,R.drawable.wall_custom10};

    private List<ModelClass> mFilters = new ArrayList<>();
    private ExColorDrawable listener;
    private Context context;
    private Bitmap mBitmap;
    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView filterImages;
        TextView mTextView;
        FrameLayout mViewFrame;

        ViewHolder(View view) {
            super(view);
            this.mTextView = (TextView) view.findViewById(R.id.filter_name);
            this.filterImages = (ImageView) view.findViewById(R.id.list_item_gpuimage);
            this.mViewFrame = (FrameLayout) view.findViewById(R.id.cont_filter);

            mViewFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int i = getAdapterPosition();
                    listener.drawableList(i);
                    }
            });
        }
    }


    public ExFilterAdapter(Context context, ExColorDrawable listener ) {

        this.context = context;
        this.listener = listener;
        mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.thumb_nail);

    }

    @Override
    public in.tagteen.tagteen.Filter.activity.ExFilterAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.filter_list_iteam, null);
        return new in.tagteen.tagteen.Filter.activity.ExFilterAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(in.tagteen.tagteen.Filter.activity.ExFilterAdapter.ViewHolder holder, int position) {


        holder.mTextView.setText(position);
        Bitmap  bitmap = BitmapFactory.decodeResource(context.getResources(),dra[position]);
        holder.filterImages.setImageBitmap(bitmap);
    }


    @Override
    public int getItemCount() {
        return dra.length;
    }
}