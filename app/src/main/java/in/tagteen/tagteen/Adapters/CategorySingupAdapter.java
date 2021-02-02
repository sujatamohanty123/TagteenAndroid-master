package in.tagteen.tagteen.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import in.tagteen.tagteen.Fragments.youthtube.adapter.CategoryBean;
import in.tagteen.tagteen.HobbiesActivity2;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.util.Utils;


public class CategorySingupAdapter extends RecyclerView.Adapter<CategorySingupAdapter.ItemRowHolder> {

    private Context mContext;
    ArrayList<CategoryBean> catList;
    public static ArrayList<Integer> selectedList = new ArrayList<>();
    public static List<String> hobbyList = new ArrayList<>();
    HobbiesActivity2 hobbies;
    ArrayList<Integer> resourceIds = new ArrayList<>();


    public CategorySingupAdapter(Context context, ArrayList<CategoryBean> catList, HobbiesActivity2 hobbies) {
        this.catList = catList;
        this.mContext = context;
        this.hobbies = hobbies;
        selectedList.clear();
        hobbyList.clear();

        resourceIds.add(R.drawable.acting);
        resourceIds.add(R.drawable.animation_and_comics);
        resourceIds.add(R.drawable.beauty_and_makeup);
        resourceIds.add(R.drawable.careers);
        resourceIds.add(R.drawable.cars_bikes);
        resourceIds.add(R.drawable.computers_and_gadgets);
        resourceIds.add(R.drawable.food_and_cooking);
        resourceIds.add(R.drawable.dancing);
        resourceIds.add(R.drawable.funny_and_standupcomic);
        resourceIds.add(R.drawable.gaming);
        resourceIds.add(R.drawable.health_and_fitness);
        resourceIds.add(R.drawable.home_and_lifestyle);
        resourceIds.add(R.drawable.journalism);
        resourceIds.add(R.drawable.magictricks);
        resourceIds.add(R.drawable.marketing_and_business);
        resourceIds.add(R.drawable.military);
        resourceIds.add(R.drawable.movies_and_tvshows);
        resourceIds.add(R.drawable.music);
        resourceIds.add(R.drawable.painting);
        resourceIds.add(R.drawable.photography);
        resourceIds.add(R.drawable.politics);
        resourceIds.add(R.drawable.selfimprovement);
        resourceIds.add(R.drawable.shopping);
        resourceIds.add(R.drawable.sports);
        resourceIds.add(R.drawable.studies_and_education);
        resourceIds.add(R.drawable.travelling);
        resourceIds.add(R.drawable.writing);
        resourceIds.add(R.drawable.dj);
        resourceIds.add(R.drawable.entrepreneurs);
        resourceIds.add(R.drawable.fashion_and_modelling);

    }


    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.categoty_grid_singleitem, null);
        ItemRowHolder mh = new ItemRowHolder(v);
        return mh;
    }


    @Override
    public void onBindViewHolder(ItemRowHolder itemRowHolder, final int i) {

        itemRowHolder.textCatName.setText(catList.get(i).getCategoryName());
        if (catList.get(i).isSelected()) {
            itemRowHolder.layoutCatSelect.setVisibility(View.VISIBLE);
            // itemRowHolder.textCatName.setBackgroundResource(R.drawable.cat_singup_unselect);
            //itemRowHolder.textCatName.setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
            itemRowHolder.layoutCatSelect.setVisibility(View.GONE);
            //itemRowHolder.textCatName.setBackgroundResource(R.drawable.cat_singup_select);
           // itemRowHolder.textCatName.setTextColor(mContext.getResources().getColor(R.color.editbox_textcolor));
        }

        itemRowHolder.layoutCatSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                catList.get(i).setSelected(false);
                int pos = selectedList.indexOf(catList.get(i).getId());
                if (selectedList.contains(catList.get(i).getId())) {
                    selectedList.remove(pos);
                }
                int poshobby = hobbyList.indexOf(catList.get(i).getCategoryName());
                if (hobbyList.contains(catList.get(i).getCategoryName())) {
                    hobbyList.remove(poshobby);
                }

                if(selectedList.size()<5){
                    hobbies.endAnim();
                }
                if (selectedList.size() == 5) {
                    hobbies.startAnim();
                }
                notifyDataSetChanged();
            }
        });

        itemRowHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                catList.get(i).setSelected(true);
                selectedList.add(catList.get(i).getId());
                hobbyList.add(catList.get(i).getCategoryName());
                if (selectedList.size() == 5) {
                    hobbies.startAnim();
                }
                if(selectedList.size()<5){
                    hobbies.endAnim();
                }
                notifyDataSetChanged();
            }
        });

        Utils.loadImageUsingGlide(this.mContext, itemRowHolder.imageView, resourceIds.get(i));
    }

    @Override
    public int getItemCount() {
        return (null != catList ? catList.size() : 0);
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView textCatName;
        protected RelativeLayout layoutCatSelect;
        ImageView imageView;

        public ItemRowHolder(View view) {
            super(view);
            this.textCatName = (TextView) view.findViewById(R.id.textCatName);
            this.layoutCatSelect = (RelativeLayout) view.findViewById(R.id.layoutCatSelect);
            this.imageView = (ImageView) view.findViewById(R.id.imageView);
        }
    }
}




