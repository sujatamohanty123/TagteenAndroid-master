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

import in.tagteen.tagteen.Filter.GPUImageFilterTools;
import in.tagteen.tagteen.R;

import java.util.ArrayList;
import java.util.List;


public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolder> {

    private List<ModelClass> mFilters = new ArrayList<>();
    private GPUImageFilterTools.OnGpuImageFilterChosenListener listener;
    private Context context;
    private Bitmap mBitmap;
    private int percent = 0;
    private int  selectedposition = 0;
    FrameLayout mselectedView;

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView filterImages;
        TextView mTextView;
        FrameLayout mViewFrame;

        ViewHolder(View view) {
            super(view);
            this.mTextView = (TextView) view.findViewById(R.id.filter_name);
            this.filterImages = (ImageView) view.findViewById(R.id.list_item_gpuimage);
            this.mViewFrame = (FrameLayout) view.findViewById(R.id.cont_filter);

        }
    }


    public FilterAdapter(Context context, List<ModelClass> mfilters, GPUImageFilterTools.OnGpuImageFilterChosenListener listener) {
        this.mFilters = mfilters;
        this.context = context;
        this.listener = listener;
        mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.thumb_nail);
    }

    @Override
    public FilterAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.filter_list_iteam, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mViewFrame.setSelected(holder.mViewFrame.isSelected()?true:false);
        ModelClass modelClass = mFilters.get(position);
        holder.mTextView.setText(modelClass.getmName());
        holder.filterImages.setImageBitmap(modelClass.getmBitmap());
       /* ByteArrayOutputStream stream = new ByteArrayOutputStream();
        modelClass.getmBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream);
        Glide.with(context).load(stream.toByteArray())
                .asBitmap().fitCenter().centerCrop().into(holder.filterImages);*/

        if(position==selectedposition){
            mselectedView = holder.mViewFrame;
            holder.mViewFrame.setBackground(context.getResources().getDrawable(R.drawable.selected_filter_background));
        }else{
            holder.mViewFrame.setBackground(context.getResources().getDrawable(R.drawable.filter_backgroung));
        }
        holder.mViewFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mselectedView.setBackground(context.getResources().getDrawable(R.drawable.filter_backgroung));
                ModelClass modelClass = mFilters.get(position);
                selectedposition=position;
                GPUImageFilterTools jGpuImageFilterTools=new GPUImageFilterTools();
                listener.onGpuImageFilterChosenListener(jGpuImageFilterTools.createFilterForType(context, modelClass.getMfilterType(),modelClass.getDrawableValue()),modelClass.getmFilterPercent());
                holder.mViewFrame.setBackground(context.getResources().getDrawable(R.drawable.selected_filter_background));
                mselectedView=holder.mViewFrame;
            }
        });
    }


    @Override
    public int getItemCount() {
        return mFilters.size();
    }

}
