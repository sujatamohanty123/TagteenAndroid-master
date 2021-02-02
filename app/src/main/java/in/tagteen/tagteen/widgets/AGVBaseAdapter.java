package in.tagteen.tagteen.widgets;

import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;

import com.felipecsl.asymmetricgridview.library.model.AsymmetricItem;

interface AGVBaseAdapter<T extends RecyclerView.ViewHolder> {
    int getActualItemCount();
    AsymmetricItem getItem(int position);
    void notifyDataSetChanged();
    int getItemViewType(int actualIndex);
    AsymmetricViewHolder<T> onCreateAsymmetricViewHolder(int position, ViewGroup parent, int viewType);
    void onBindAsymmetricViewHolder(AsymmetricViewHolder<T> holder, ViewGroup parent, int position);
}