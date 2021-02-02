package in.tagteen.tagteen.Adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.tagteen.tagteen.AntonyChanges;
import in.tagteen.tagteen.Fragments.youthtube.adapter.CategoryBean;
import in.tagteen.tagteen.HobbiesActivity2_new;
import in.tagteen.tagteen.R;

/**
 * Created by Sujata on 23-05-2018.
 */
public class CategorySingupAdapter1 extends RecyclerView.Adapter<CategorySingupAdapter1.ItemRowHolder> {

    private Context mContext;
    private ArrayList<CategoryBean> catList;
    private List<CategoryBean> selectedCategories = new ArrayList<CategoryBean>();
    private HobbiesActivity2_new hobbies;
    private ArrayList<String> selectedCategoryIds;
    //ArrayList<Integer> resourceIds = new ArrayList<>();
    //ArrayList<Integer> resourceIds_select = new ArrayList<>();
    //ArrayList<String> categoryNameList = new ArrayList<>();


    @AntonyChanges
    public CategorySingupAdapter1(
            Context context, ArrayList<CategoryBean> catList, HobbiesActivity2_new hobbies, ArrayList<String> selectedCategoryIds) {
        this.catList = catList;
        this.mContext = context;
        this.hobbies = hobbies;
        this.selectedCategoryIds = selectedCategoryIds;
        this.initCategorySelection();
    }

    public void initCategorySelection() {
        if (this.catList != null && this.selectedCategoryIds != null) {
            for (CategoryBean cty : this.catList) {
                if (this.selectedCategoryIds.contains("" + cty.getId())) {
                    cty.setSelected(true);
                    this.selectedCategories.add(cty);
                }
            }
            if (this.selectedCategories.size() >= 5) {
                this.hobbies.startAnim();
            }
        }
    }

    private void deselectCategory(int _id, ItemRowHolder holder) {
        if (_id == 0) {
            holder.image.setImageResource(R.drawable.ic_all_active);
        } else if(_id == 1) {
            holder.image.setImageResource(R.drawable.ic_acting_new);
        } else if(_id == 2) {
            holder.image.setImageResource(R.drawable.ic_animation_new);
        } else if(_id == 3) {
            holder.image.setImageResource(R.drawable.ic_beauty_new);
        } else if(_id == 4) {
            holder.image.setImageResource(R.drawable.ic_career_new);
        } else if(_id == 5) {
            holder.image.setImageResource(R.drawable.ic_carbike_new);
        } else if(_id == 6) {
            holder.image.setImageResource(R.drawable.ic_gadgets_new);
        } else if(_id == 7) {
            holder.image.setImageResource(R.drawable.ic_foodcook_new);
        } else if(_id == 8) {
            holder.image.setImageResource(R.drawable.ic_dance_new);
        } else if(_id == 9) {
            holder.image.setImageResource(R.drawable.ic_dj_new);
        } else if(_id == 10) {
            holder.image.setImageResource(R.drawable.ic_enterprenuer_new);
        } else if(_id == 11) {
            holder.image.setImageResource(R.drawable.ic_fashion_new);
        } else if(_id == 12) {
            holder.image.setImageResource(R.drawable.ic_miccomedy_new);
        } else if(_id == 13) {
            holder.image.setImageResource(R.drawable.ic_game_new);
        } else if(_id == 14) {
            holder.image.setImageResource(R.drawable.ic_sports_inactive_new);
        } else if(_id == 15) {
            holder.image.setImageResource(R.drawable.ic_lifestyle_new);
        } else if(_id == 16) {
            holder.image.setImageResource(R.drawable.ic_jurnalist_new);
        } else if(_id == 17) {
            holder.image.setImageResource(R.drawable.ic_magic_new);
        } else if(_id == 18) {
            holder.image.setImageResource(R.drawable.ic_marketting_new);
        } else if(_id == 19) {
            holder.image.setImageResource(R.drawable.ic_militry_new);
        } else if(_id == 20) {
            holder.image.setImageResource(R.drawable.ic_movie);
        } else if(_id == 21) {
            holder.image.setImageResource(R.drawable.ic_music_new);
        } else if(_id == 22) {
            holder.image.setImageResource(R.drawable.ic_painting_new);
        } else if(_id == 23) {
            holder.image.setImageResource(R.drawable.ic_photography_new);
        } else if(_id == 24) {
            holder.image.setImageResource(R.drawable.ic_politics_new);
        } else if(_id == 25) {
            holder.image.setImageResource(R.drawable.ic_selfimprovement_new);
        } else if(_id == 26) {
            holder.image.setImageResource(R.drawable.ic_shopping_new);
        } else if(_id == 27) {
            holder.image.setImageResource(R.drawable.ic_sport_new);
        } else if(_id == 28) {
            holder.image.setImageResource(R.drawable.ic_education_new);
        } else if(_id == 29) {
            holder.image.setImageResource(R.drawable.ic_travel_inactive);
        } else if(_id == 30) {
            holder.image.setImageResource(R.drawable.ic_writing);
        }
    }

    private void selectCategory(int _id, ItemRowHolder holder) {
        if(_id==0) {
            holder.image.setImageResource(R.drawable.ic_all_inactive);
        }
        if(_id==1) {
            holder.image.setImageResource(R.drawable.ic_acting_active);
        }
        if(_id==2) {
            holder.image.setImageResource(R.drawable.ic_animation_active);
        }
        if(_id==3) {
            holder.image.setImageResource(R.drawable.ic_beauty_active);
        }
        if(_id==4) {
            holder.image.setImageResource(R.drawable.ic_career_active);
        }
        if(_id==5) {
            holder.image.setImageResource(R.drawable.ic_carbike_active);
        }
        if(_id==6) {//----computer------------
            holder.image.setImageResource(R.drawable.ic_gadgets_active);
        }
        if(_id==7) {
            holder.image.setImageResource(R.drawable.ic_foodcook_active);
        }
        if(_id==8) {
            holder.image.setImageResource(R.drawable.ic_dance_active);
        }
        if(_id==9) {
            holder.image.setImageResource(R.drawable.ic_dj_active);
        }
        if(_id==10) {
            holder.image.setImageResource(R.drawable.ic_enterprenuer_active);
        }
        if(_id==11) {
            holder.image.setImageResource(R.drawable.ic_fashion_active);
        }
        if(_id==12) {//----funny_commics--------
            holder.image.setImageResource(R.drawable.ic_miccomedy_active);
        }
        if(_id==13) {
            holder.image.setImageResource(R.drawable.ic_game_active);
        }
        if(_id==14) {
            holder.image.setImageResource(R.drawable.ic_sport_active_new);
        }
        if(_id==15) {//-----home------
            holder.image.setImageResource(R.drawable.ic_lifestyle_active);
        }
        if(_id==16) {
            holder.image.setImageResource(R.drawable.ic_jurnalist_active);
        }
        if(_id==17) {
            holder.image.setImageResource(R.drawable.ic_magic_active);
        }
        if(_id==18) {
            holder.image.setImageResource(R.drawable.ic_marketting_active);
        }
        if(_id==19) {
            holder.image.setImageResource(R.drawable.ic_militry_active);
        }
        if(_id==20) {
            holder.image.setImageResource(R.drawable.ic_movie_active);
        }
        if(_id==21) {
            holder.image.setImageResource(R.drawable.ic_music_active);
        }
        if(_id==22) {
            holder.image.setImageResource(R.drawable.ic_painting_active);
        }
        if(_id==23) {
            holder.image.setImageResource(R.drawable.ic_photography_active);
        }
        if(_id==24) {
            holder.image.setImageResource(R.drawable.ic_politics_active);
        }
        if(_id==25) {
            holder.image.setImageResource(R.drawable.ic_selfimprovement_active);
        }
        if(_id==26) {
            holder.image.setImageResource(R.drawable.ic_shopping_active);
        }
        if(_id==27) {
            holder.image.setImageResource(R.drawable.ic_sport_active);
        }
        if(_id==28) {
            holder.image.setImageResource(R.drawable.ic_education_active);
        }
        if(_id==29) {
            holder.image.setImageResource(R.drawable.ic_travel_active);
        }
        if(_id==30) {//-----writing-----
            holder.image.setImageResource(R.drawable.ic_writing_active);
        }
    }

    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(this.mContext).inflate(R.layout.categoty_grid_singleitem1, null);
        ItemRowHolder mh = new ItemRowHolder(v);
        return mh;
    }

    private void removeCategory(CategoryBean cty) {
        if (cty != null && this.selectedCategories != null) {
            for (CategoryBean selectedCty : this.selectedCategories) {
                if (selectedCty.getId() == cty.getId()) {
                    this.selectedCategories.remove(selectedCty);
                    break;
                }
            }
        }
    }

    @Override
    public void onBindViewHolder(ItemRowHolder itemRowHolder, final int i) {
        final CategoryBean category = catList.get(i);
        itemRowHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (category.isSelected()) {
                    removeCategory(category);
                    category.setSelected(false);
                } else {
                    category.setSelected(true);
                    selectedCategories.add(category);
                }
                if (selectedCategories.size() < 5) {
                    hobbies.endAnim();
                }
                if (selectedCategories.size() == 5) {
                    hobbies.startAnim();
                }
                notifyDataSetChanged();
            }
        });

        if (category.isSelected()) {
            this.selectCategory(category.getId(), itemRowHolder);
        } else {
            this.deselectCategory(category.getId(), itemRowHolder);
        }
        itemRowHolder.category_name.setText(category.getCategoryName());
    }

    @Override
    public int getItemCount() {
        return catList.size();
    }

    public List<CategoryBean> getSelectedCategories() {
        return this.selectedCategories;
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView category_name;

        public ItemRowHolder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.imageView);
            category_name = (TextView) view.findViewById(R.id.category_name);
        }
    }
}
