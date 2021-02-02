package in.tagteen.tagteen.Adapters;

import android.app.Activity;
import android.graphics.Color;
import com.google.android.material.snackbar.Snackbar;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import in.tagteen.tagteen.MaterialEditTextUtils.ColorGenerator;
import in.tagteen.tagteen.Model.Friend;
import in.tagteen.tagteen.Model.SectionDataModel;
import in.tagteen.tagteen.Model.SingleItemModel;
import in.tagteen.tagteen.Model.UserModel;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.TagteenInterface.UserSelectionList;

import java.util.ArrayList;
import java.util.List;


public class New_Group_Adapter extends RecyclerView.Adapter<New_Group_Adapter.MyViewHolder> {

    private Activity mContext;
    private LayoutInflater inflater;
    private String[] mThumbIds;
    ImageView indicator;

    private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
    private Animation fadeIn, fadeOut;
    private List<Friend> group = new ArrayList<>();
    ArrayList<String> selectedStrings  = new ArrayList<String>();
    ArrayList<Integer> selectedImage   = new ArrayList<Integer>();
    ArrayList<String> selectedStrings1 = new ArrayList<String>();
    ArrayList<Integer> selectedImage1  = new ArrayList<Integer>();
    private ArrayList<SectionDataModel> dataList;

    ArrayList<SingleItemModel> itemsList= new ArrayList<SingleItemModel>();
    private RelativeLayout relative;
    int flag=1;
    private  UserSelectionList userSelectionList;
    ArrayList<UserModel>userModelsList=new ArrayList<UserModel>();
    public New_Group_Adapter(Activity mContext, List<Friend> group, ArrayList<SectionDataModel> dataList, UserSelectionList userSelectionList) {
        this.dataList = dataList;
        this.mContext = mContext;
        this.group = group;
        this.userSelectionList=userSelectionList;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView group_name, Designation;
        RoundedImageView im;
        CheckBox rb;
        protected RecyclerView recycler_view_list;
        protected RelativeLayout relativeLayout;
        private TextView border;

        public MyViewHolder(View view) {
            super(view);
            group_name = (TextView) view.findViewById(R.id.list_friend_name);
            Designation = (TextView) view.findViewById(R.id.teacherDesignation);
            im = (RoundedImageView) view.findViewById(R.id.userViewImage);
            rb = (CheckBox) view.findViewById(R.id.button);
            recycler_view_list = (RecyclerView) view.findViewById(R.id.recycler_view_list);
            relativeLayout = (RelativeLayout) view.findViewById(R.id.relative);
            relative = (RelativeLayout) view.findViewById(R.id.lest_iteam_id);
            rb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Friend friend = group.get(getLayoutPosition());
                    if(rb.isChecked()){
                    UserModel userModel=new UserModel();
                    userModel.setmName(friend.getName());
                    userModel.setmImage(friend.getImag());
                    userModel.setmTagId(friend.getTag());
                    userModelsList.add(userModel);
                    userSelectionList.selectionList(userModelsList);
                    }else{

                        String id = friend.getTag();
                        for(int i=0; i <= userModelsList.size(); i++){
                            UserModel userModel=userModelsList.get(i);
                            if(userModel.getmTagId() == id)
                                userModelsList.remove(userModelsList.get(i));
                        }
                        userSelectionList.selectionList(userModelsList);

                    }
                }
            });
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_group_row, parent, false);
        return new New_Group_Adapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder( MyViewHolder holder, final  int position) {

            final Friend friend = group.get(position);
            holder.group_name.setText(friend.getName());
            holder.Designation.setText(friend.getTag());
            holder.im.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder.im.setCornerRadius(60);
            fadeIn = AnimationUtils.loadAnimation(mContext, R.anim.fade_in);
            fadeOut = AnimationUtils.loadAnimation(mContext, R.anim.fade_out);

            Glide
                    .with(mContext)
                    .load(friend.getImag())
                    .fitCenter()
                    .into(holder.im);
            holder.rb.setOnCheckedChangeListener(null);
            if (friend.isSelected()) {
                holder.rb.setChecked(true);
            } else {
                holder.rb.setChecked(false);
            }

            holder.rb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    friend.setSelected(isChecked);
                    notifyDataSetChanged();
                    if (isChecked) {
                        selectedStrings.add(friend.getName().toString());
                        selectedImage.add(friend.getImag());
                        if (selectedStrings.size()>0){
                            SmackbarVCiew(Color.BLUE, "", R.mipmap.newgoup_icon);
                        }else {
                            SmackbarVCiew(Color.GRAY, "", R.mipmap.newgoup_icon);
                        }
                    } else {
                        selectedStrings.remove(friend.getName().toString());
                        selectedImage.remove(friend.getImag());
                        if (selectedStrings.size()>0){
                            SmackbarVCiew(Color.BLUE, "", R.mipmap.newgoup_icon);
                        }else {
                            SmackbarVCiew(Color.GRAY, "", R.mipmap.newgoup_icon);
                        }
                    }
                    System.out.println("slist" + selectedStrings + selectedImage);
                }
            });
        }


    public void SmackbarVCiew( int color,String message,int image) {

        Snackbar snackbar = Snackbar.make(relative, " ", Snackbar.LENGTH_LONG);
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(Color.WHITE);

        layout.findViewById(R.id.snackbar_text).setVisibility(View.INVISIBLE);
        layout.setPadding(0, 0, 0, 0);

        LayoutInflater inflater = LayoutInflater.from(relative.getContext());

        View snackView = inflater.inflate(R.layout.snack, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 0);
        snackView.setLayoutParams(params);


        LinearLayout l2 = (LinearLayout) snackView.findViewById(R.id.lin1);
        l2.setBackgroundColor(color);
        l2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        TextView txt = (TextView) snackView.findViewById(R.id.textView);
        txt.setText(message);
        ImageView imgageview = (ImageView) snackView.findViewById(R.id.img);
        imgageview.setImageResource(image);
        layout.addView(snackView, 0);
        snackbar.show();
    }

    @Override
    public int getItemCount() {
        return group.size();
    }
}