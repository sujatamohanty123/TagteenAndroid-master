package in.tagteen.tagteen.Fragments.youthtube.adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.ArrayList;

import in.tagteen.tagteen.R;
import in.tagteen.tagteen.utils.MyBounceInterpolator;



public class CategoriesHorizontalAdapter extends RecyclerView.Adapter<CategoriesHorizontalAdapter.ImageViewHolder> {

    private ArrayList<CategoryBean> categoryList;
    private Context mContext;
    final Animation myAnim;
    private boolean isLike = false;
    private String actionType = "";

    public CategoriesHorizontalAdapter(ArrayList<CategoryBean> categoryList, Context mContext, String type) {
        this.categoryList = categoryList;
        this.mContext = mContext;
        myAnim = AnimationUtils.loadAnimation(mContext, R.anim.bounce);
        actionType = type;
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.category_text, parent, false));
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, final int position) {
        holder.lable.setText(categoryList.get(position).getCategoryName());
        if (categoryList.get(position).isSelected()) {
            holder.lable.setBackgroundResource(R.drawable.cat_selected);
            holder.lable.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
        } else {
            holder.lable.setBackgroundResource(R.drawable.cat_unselected);
            holder.lable.setTextColor(mContext.getResources().getColor(R.color.white));
        }

        holder.lable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (actionType.equalsIgnoreCase("single")) {
                    makeSingleSelected(position);
                } else if(actionType.equalsIgnoreCase("multi")){
                    if (categoryList.get(position).isSelected()) {
                        categoryList.get(position).setSelected(false);
                    } else {
                        categoryList.get(position).setSelected(true);
                    }
                    notifyDataSetChanged();
                } else{

                }
            }
        });
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        private TextView lable;

        public ImageViewHolder(View itemView) {
            super(itemView);
            lable = (TextView) itemView.findViewById(R.id.lable);
            lable.setBackgroundResource(R.drawable.cat_unselected);
        }
    }

    public void makeSingleSelected(int positon) {
        for (int i = 0; i < categoryList.size(); i++) {
            if (i == positon) {
                categoryList.get(i).setSelected(true);
            } else {
                categoryList.get(i).setSelected(false);
            }
        }
        notifyDataSetChanged();
    }
}