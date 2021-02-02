package in.tagteen.tagteen.Adapters.knowledge;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.tagteen.tagteen.Model.knowledge.KnowledgeCategories;
import in.tagteen.tagteen.R;

public class KnowledgeCategoriesAdapter extends RecyclerView.Adapter<KnowledgeCategoriesAdapter.ItemRowHolder> {
    private Context context;
    private List<KnowledgeCategories.Category> categories;
    private int selectedCategoryIndex = 0;

    private OnItemClickListener onItemClickListener;

    public KnowledgeCategoriesAdapter(Context context, List<KnowledgeCategories.Category> categories) {
        this.context = context;
        this.categories = categories;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.knowledge_cty_row, parent, false);
        return new ItemRowHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemRowHolder holder, int position) {
        final KnowledgeCategories.Category category = this.categories.get(position);
        if (category == null) {
            return;
        }

        holder.lblCategoryName.setText(category.getName());
        if (selectedCategoryIndex == position) {
            holder.lblCategoryName.setBackgroundTintList(
                    this.context.getResources().getColorStateList(R.color.colorPrimary));
            holder.lblCategoryName.setTextColor(this.context.getResources().getColor(R.color.white));
        } else {
            holder.lblCategoryName.setBackgroundTintList(
                    this.context.getResources().getColorStateList(R.color.white));
            holder.lblCategoryName.setTextColor(this.context.getResources().getColor(R.color.full_black));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedCategoryIndex = position;
                notifyDataSetChanged();
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClicked(category.getId());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.categories.size();
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder {
        private TextView lblCategoryName;

        public ItemRowHolder(View view) {
            super(view);

            this.lblCategoryName = view.findViewById(R.id.lblCategoryName);
        }
    }

    public interface OnItemClickListener {
        void onItemClicked(String categoryId);
    }
}
