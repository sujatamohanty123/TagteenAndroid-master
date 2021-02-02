package in.tagteen.tagteen.FilePicker.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import in.tagteen.tagteen.R;

import java.util.List;



public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterHolder>{
    private Context context;
    private List<String> itemlist;
    public FilterAdapter(Context context,List<String > itemlist){
        this.context=context;
        this.itemlist=itemlist;
    }
    @Override
    public FilterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.filepicker_item_filter, parent, false);
        return new FilterHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FilterHolder holder, int position) {
        String filter = itemlist.get(position);
        holder.txtName.setText(filter);
    }

    @Override
    public int getItemCount() {
        return itemlist.size();
    }

    public class FilterHolder extends RecyclerView.ViewHolder{
        TextView txtName;
        public FilterHolder(View itemView) {
            super(itemView);
            txtName = (TextView)itemView.findViewById(R.id.txtName);
        }
    }
}
