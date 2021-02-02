package in.tagteen.tagteen.Fragments.teenfeed;

import android.app.Activity;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import in.tagteen.tagteen.Fragments.youthtube.adapter.CategoryBean;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;

/**
 * Created by lovekushvishwakarma on 29/10/17.
 */

public class CategoryAdapter extends RecyclerView.Adapter {
    ArrayList<CategoryBean> categoryList;
    LayoutInflater inflater;
    Context mContext;
    TextView textCatSelct;
    String prev_selected;
    CategoryFragment categoryFragment;

    public CategoryAdapter(Context mContext, ArrayList<CategoryBean> categoryList, TextView textCatSelct, CategoryFragment categoryFragment) {
        this.categoryList = categoryList;
        this.mContext = mContext;
        this.textCatSelct = textCatSelct;
        this.categoryFragment = categoryFragment;
        prev_selected = SharedPreferenceSingleton.getInstance().getStringPreference(PostAnArticle.ARTICLE_CATEGORY_ID);
        inflater = ((Activity) mContext).getLayoutInflater();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MintTypeviewHelper vh;
        View convertView = inflater.inflate(R.layout.category_item_layout, null);
        vh = new MintTypeviewHelper(convertView);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder mHolder, final int position) {
        if (mHolder instanceof MintTypeviewHelper) {
            final MintTypeviewHelper holder = ((MintTypeviewHelper) mHolder);
            if (categoryList.get(position).isSelected() == true) {
                holder.catRadio.setChecked(true);
            } else {
                holder.catRadio.setChecked(false);
            }

            String name = categoryList.get(position).getCategoryName();

            holder.catName.setText(name);
            String catid = categoryList.get(position).getId() + "";
            if (catid.equalsIgnoreCase(prev_selected)) {
                holder.catRadio.setChecked(true);
            }

            holder.catRadio.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return true;
                }
            });

            holder.root_lay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean flag = categoryList.get(position).isSelected();
                    if (flag) {
                        flag = false;
                    } else {
                        flag = true;
                    }
                    makeSingleSelction(position, flag);
                    SharedPreferenceSingleton.getInstance().writeStringPreference(PostAnArticle.ARTICLE_CATEGORY_ID, categoryList.get(position).getId()+"");
                    SharedPreferenceSingleton.getInstance().writeStringPreference(PostAnArticle.ARTICLE_CATEGORY, categoryList.get(position).getCategoryName());
                    notifyDataSetChanged();
                    textCatSelct.setText(categoryList.get(position).getCategoryName());
                    categoryFragment.closeFragment();

                }
            });

        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }


    public class MintTypeviewHelper extends RecyclerView.ViewHolder {
        RadioButton catRadio;
        TextView catName;
        RelativeLayout root_lay;

        public MintTypeviewHelper(View itemView) {
            super(itemView);
            catRadio = (RadioButton) itemView.findViewById(R.id.radiocat);
            catName = (TextView) itemView.findViewById(R.id.catName);
            root_lay = (RelativeLayout) itemView.findViewById(R.id.root_lay);
        }
    }

    private void makeSingleSelction(int pos, boolean flag) {
        for (int i = 0; i < categoryList.size(); i++) {
            categoryList.get(i).setSelected(false);
        }
        categoryList.get(pos).setSelected(flag);
    }
}