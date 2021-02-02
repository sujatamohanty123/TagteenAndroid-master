package in.tagteen.tagteen.Fragments.youthtube.adapter;

import android.content.Context;
import android.graphics.Typeface;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import in.tagteen.tagteen.R;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.utils.MyBounceInterpolator;


public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ImageViewHolder> {

    private ArrayList<CategoryBean> categoryList;
    private Context mContext;
    private final Animation myAnim;
    private boolean isLike = false;
    private String actionType = "";
    private OnItemClickListener mOnItemClickListener;
    private int selectedCategory;

    public interface OnItemClickListener {
        void onItemClick(View view, int clickedPosition);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public CategoriesAdapter(ArrayList<CategoryBean> categoryList, Context mContext, String type) {
        this.categoryList = categoryList;
        this.mContext = mContext;
        this.myAnim = AnimationUtils.loadAnimation(mContext, R.anim.bounce);
        this.actionType = type;
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        this.myAnim.setInterpolator(interpolator);
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.category_itemtext, parent, false));
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, final int position) {
        CategoryBean data = categoryList.get(position);
        holder.lable.setText(data.getCategoryName());

        if(!actionType.equalsIgnoreCase("NoClick")) {
            if (selectedCategory == position) {
                String ctyName = data.getCategoryName();
                int textColor = Utils.getCategoryColor(mContext, ctyName);

                holder.lable.setTextColor(textColor);
                selectCategoryButton(categoryList.get(position).getId(), holder);
            } else {
                deselectCategoryButton(categoryList.get(position).getId(), holder);
                holder.lable.setTypeface(Typeface.DEFAULT_BOLD);
                holder.lable.setTextColor(mContext.getResources().getColor(R.color.grey_800));
            }
        } else {
            holder.lable.setTextColor(mContext.getResources().getColor(R.color.grey_800));
            deselectCategoryButton(categoryList.get(position).getId(), holder);
        }
        holder.category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedCategory =position;
                if(!actionType.equalsIgnoreCase("NoClick")) {
                    mOnItemClickListener.onItemClick(v, categoryList.get(selectedCategory).getId());
                }
                notifyDataSetChanged();
            }
        });
    }

    private void deselectCategoryButton(int _id, ImageViewHolder holder) {
        if (_id == 0) {
            holder.btn.setImageResource(R.drawable.ic_all_active);
        } else if(_id == 1) {
            holder.btn.setImageResource(R.drawable.ic_acting_new);
        } else if(_id == 2) {
            holder.btn.setImageResource(R.drawable.ic_animation_new);
        } else if(_id == 3) {
            holder.btn.setImageResource(R.drawable.ic_beauty_new);
        } else if(_id == 4) {
            holder.btn.setImageResource(R.drawable.ic_career_new);
        } else if(_id == 5) {
            holder.btn.setImageResource(R.drawable.ic_carbike_new);
        } else if(_id == 6) {
            holder.btn.setImageResource(R.drawable.ic_gadgets_new);
        } else if(_id == 7) {
            holder.btn.setImageResource(R.drawable.ic_foodcook_new);
        } else if(_id == 8) {
            holder.btn.setImageResource(R.drawable.ic_dance_new);
        } else if(_id == 9) {
            holder.btn.setImageResource(R.drawable.ic_dj_new);
        } else if(_id == 10) {
            holder.btn.setImageResource(R.drawable.ic_enterprenuer_new);
        } else if(_id == 11) {
            holder.btn.setImageResource(R.drawable.ic_fashion_new);
        } else if(_id == 12) {
            holder.btn.setImageResource(R.drawable.ic_miccomedy_new);
        } else if(_id == 13) {
            holder.btn.setImageResource(R.drawable.ic_game_new);
        } else if(_id == 14) {
            holder.btn.setImageResource(R.drawable.ic_sports_inactive_new);
        } else if(_id == 15) {
            holder.btn.setImageResource(R.drawable.ic_lifestyle_new);
        } else if(_id == 16) {
            holder.btn.setImageResource(R.drawable.ic_jurnalist_new);
        } else if(_id == 17) {
            holder.btn.setImageResource(R.drawable.ic_magic_new);
        } else if(_id == 18) {
            holder.btn.setImageResource(R.drawable.ic_marketting_new);
        } else if(_id == 19) {
            holder.btn.setImageResource(R.drawable.ic_militry_new);
        } else if(_id == 20) {
            holder.btn.setImageResource(R.drawable.ic_movie);
        } else if(_id == 21) {
            holder.btn.setImageResource(R.drawable.ic_music_new);
        } else if(_id == 22) {
            holder.btn.setImageResource(R.drawable.ic_painting_new);
        } else if(_id == 23) {
            holder.btn.setImageResource(R.drawable.ic_photography_new);
        } else if(_id == 24) {
            holder.btn.setImageResource(R.drawable.ic_politics_new);
        } else if(_id == 25) {
            holder.btn.setImageResource(R.drawable.ic_selfimprovement_new);
        } else if(_id == 26) {
            holder.btn.setImageResource(R.drawable.ic_shopping_new);
        } else if(_id == 27) {
            holder.btn.setImageResource(R.drawable.ic_sport_new);
        } else if(_id == 28) {
            holder.btn.setImageResource(R.drawable.ic_education_new);
        } else if(_id == 29) {
            holder.btn.setImageResource(R.drawable.ic_travel_inactive);
        } else if(_id == 30) {
            holder.btn.setImageResource(R.drawable.ic_writing);
        }
    }

    private void selectCategoryButton(int _id, ImageViewHolder holder) {
        if(_id==0) {
                holder.btn.setImageResource(R.drawable.ic_all_inactive);
        }
        if(_id==1) {
                holder.btn.setImageResource(R.drawable.ic_acting_active);
        }
        if(_id==2) {
                holder.btn.setImageResource(R.drawable.ic_animation_active);
        }
        if(_id==3) {
                holder.btn.setImageResource(R.drawable.ic_beauty_active);
        }
        if(_id==4) {
                holder.btn.setImageResource(R.drawable.ic_career_active);
        }
        if(_id==5) {
                holder.btn.setImageResource(R.drawable.ic_carbike_active);
        }
        if(_id==6) {//----computer------------
           holder.btn.setImageResource(R.drawable.ic_gadgets_active);
        }
        if(_id==7) {
                holder.btn.setImageResource(R.drawable.ic_foodcook_active);
        }
        if(_id==8) {
                holder.btn.setImageResource(R.drawable.ic_dance_active);
        }
        if(_id==9) {
                holder.btn.setImageResource(R.drawable.ic_dj_active);
        }
        if(_id==10) {
                holder.btn.setImageResource(R.drawable.ic_enterprenuer_active);
        }
        if(_id==11) {
                holder.btn.setImageResource(R.drawable.ic_fashion_active);
        }
        if(_id==12) {//----funny_commics--------
                holder.btn.setImageResource(R.drawable.ic_miccomedy_active);
        }
        if(_id==13) {
                holder.btn.setImageResource(R.drawable.ic_game_active);
        }
        if(_id==14) {
                holder.btn.setImageResource(R.drawable.ic_sport_active_new);
        }
        if(_id==15) {//-----home------
                holder.btn.setImageResource(R.drawable.ic_lifestyle_active);
        }
        if(_id==16) {
                holder.btn.setImageResource(R.drawable.ic_jurnalist_active);
        }
        if(_id==17) {
                holder.btn.setImageResource(R.drawable.ic_magic_active);
        }
        if(_id==18) {
                holder.btn.setImageResource(R.drawable.ic_marketting_active);
        }
        if(_id==19) {
                holder.btn.setImageResource(R.drawable.ic_militry_active);
        }
        if(_id==20) {
                holder.btn.setImageResource(R.drawable.ic_movie_active);
        }
        if(_id==21) {
                holder.btn.setImageResource(R.drawable.ic_music_active);
        }
        if(_id==22) {
                holder.btn.setImageResource(R.drawable.ic_painting_active);
        }
        if(_id==23) {
                holder.btn.setImageResource(R.drawable.ic_photography_active);
        }
        if(_id==24) {
                holder.btn.setImageResource(R.drawable.ic_politics_active);
        }
        if(_id==25) {
                holder.btn.setImageResource(R.drawable.ic_selfimprovement_active);
        }
        if(_id==26) {
                holder.btn.setImageResource(R.drawable.ic_shopping_active);
        }
        if(_id==27) {
                holder.btn.setImageResource(R.drawable.ic_sport_active);
        }
        if(_id==28) {
                holder.btn.setImageResource(R.drawable.ic_education_active);
        }
        if(_id==29) {
                holder.btn.setImageResource(R.drawable.ic_travel_active);
        }
        if(_id==30) {//-----writing-----
                holder.btn.setImageResource(R.drawable.ic_writing_active);
        }
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {

        private TextView lable;
        ImageView btn,select_btn;
        RelativeLayout category;

        public ImageViewHolder(View itemView) {
            super(itemView);
            lable = (TextView) itemView.findViewById(R.id.lable);
            btn = (ImageView) itemView.findViewById(R.id.btn);
            select_btn = (ImageView) itemView.findViewById(R.id.select_btn);
            category=(RelativeLayout)itemView.findViewById(R.id.category);
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