package in.tagteen.tagteen.teenfeed.adapter;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import in.tagteen.tagteen.R;
import in.tagteen.tagteen.TeenfeedPreviewFragment;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.teenfeed.model.SearchArticleModel;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;


public class SearchArticleAdapter extends RecyclerView.Adapter<SearchArticleAdapter.MyViewHolder> {
    ArrayList<SearchArticleModel> searchList;
    Context context;
    String userid;
    String token;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textTitle, textDesc, lblLikesCount;
        public ImageView image;
        RelativeLayout mainlayout;

        public MyViewHolder(View view) {
            super(view);
            textTitle = (TextView) view.findViewById(R.id.textTitle);
            lblLikesCount = (TextView) view.findViewById(R.id.lblLikesCount);
            textDesc = (TextView) view.findViewById(R.id.textDesc);
            image = (ImageView) view.findViewById(R.id.image);
            mainlayout = (RelativeLayout) view.findViewById(R.id.mainlayout);
        }
    }

    public SearchArticleAdapter(Context context, ArrayList<SearchArticleModel> searchList) {
        this.context = context;
        this.searchList = searchList;
        SharedPreferenceSingleton.getInstance().init(context);
        userid = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
        token = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TOKEN);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_article_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final SearchArticleModel data = searchList.get(position);

        try {
            Glide.with(context).load(data.getImage()).into(holder.image);
            String strdesc=data.getContent();
            if(strdesc.contains("|")){
                String array[] = data.getContent().split("\\|");
                String title = array[0];
                //String desc = array[0];
                holder.textTitle.setText(title);
                //holder.textDesc.setText(desc);
            }else{
                holder.textDesc.setText(strdesc);
            }
            holder.lblLikesCount.setText(data.getLike_count() + " Likes");

            holder.mainlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragment = new TeenfeedPreviewFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Constants.POST_ID, data.get_id());
                    fragment.setArguments(bundle);
                    FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.frag_fade_in, R.anim.frag_fade_out, R.anim.frag_fade_in, R.anim.frag_fade_out);
                    fragmentTransaction.replace(R.id.main_content, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            holder.textDesc.setText(data.getContent());
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        try {
            count = searchList.size();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }
}
