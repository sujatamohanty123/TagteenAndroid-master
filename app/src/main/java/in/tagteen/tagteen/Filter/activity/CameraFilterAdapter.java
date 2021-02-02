package in.tagteen.tagteen.Filter.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import in.tagteen.tagteen.Filter.GPUImageFilterTools;
import in.tagteen.tagteen.R;

import java.util.ArrayList;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageTwoInputFilter;


public class CameraFilterAdapter extends RecyclerView.Adapter<CameraFilterAdapter.ViewHolder> {

    private List<ModelClass> mFilters = new ArrayList<>();
    private GPUImageFilterTools.OnGpuImageFilterChosenListener listener;
    private Context context;
    private Bitmap mBitmap;
    private int percent = 0;
    private int  position = 0;
    Drawable normal,selected;
    FrameLayout selectedPosition = null;
    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView filterImages;
        TextView mTextView;
        FrameLayout mViewFrame;
        public List<ModelClass> mfilters = new ArrayList<>();

        ViewHolder(View view) {
            super(view);
            this.mTextView = (TextView) view.findViewById(R.id.filter_name);
            this.filterImages = (ImageView) view.findViewById(R.id.list_item_gpuimage);
            this.mViewFrame = (FrameLayout) view.findViewById(R.id.cont_filter);


        }
    }

    public CameraFilterAdapter(Context context, List<ModelClass> mfilters, GPUImageFilterTools.OnGpuImageFilterChosenListener listener) {

        this.mFilters = mfilters;
        this.context = context;
        this.listener = listener;
        mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.thumb_nail);

    }

    @Override
    public CameraFilterAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.filter_list_iteam, null);
        return new CameraFilterAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CameraFilterAdapter.ViewHolder holder, final int position) {
        ModelClass modelClass = mFilters.get(position);
        holder.mTextView.setText(modelClass.getmName());
        holder.filterImages.setImageBitmap(modelClass.getmBitmap());

        holder.mViewFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModelClass modelClass = mFilters.get(position);
                listener.onGpuImageFilterChosenListener(GPUImageFilterTools.createFilterForType(context,modelClass.getMfilterType(),0),0);

            }
        });
    }


    @Override
    public int getItemCount() {
        return mFilters.size();
    }


    private static GPUImageFilter createBlendFilter(Context context, Class<? extends GPUImageTwoInputFilter> filterClass) {
        try {
            GPUImageTwoInputFilter filter = filterClass.newInstance();
            filter.setBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.wall_custom19));
            return filter;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }





}
