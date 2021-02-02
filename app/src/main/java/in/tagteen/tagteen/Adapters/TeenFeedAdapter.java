package in.tagteen.tagteen.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.tagteen.tagteen.CommentLikeActivity_new;
import in.tagteen.tagteen.Interfaces.OnLoadMoreListener;
import in.tagteen.tagteen.Model.SectionDataModel;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.TeenfeedPreviewFragment;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.configurations.RequestConstants;
import in.tagteen.tagteen.networkEngine.AsyncResponse;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;


public class TeenFeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements AsyncResponse {

    private ArrayList<SectionDataModel> dataList;
    private Context mContext;
    private boolean isTaggedUser = SharedPreferenceSingleton.getInstance().getBoolPreference(RegistrationConstants.IS_TAGGED_USER);
    private String userId = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);

    private static final int FLAG_COMMENT = 0;
    private static final int FLAG_REACTS = 1;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnLoadMoreListener onLoadMoreListener;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;

    public TeenFeedAdapter(Context context, ArrayList<SectionDataModel> dataList, RecyclerView recyclerView) {
        this.dataList = dataList;
        this.mContext = context;

        final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = layoutManager.getItemCount();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    isLoading = true;
                    if (onLoadMoreListener != null) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                onLoadMoreListener.onLoadMore();
                            }
                        }, 200);
                    }
                }
            }
        });
    }

    public void setLoaded() {
        isLoading = false;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    @Override
    public int getItemViewType(int position) {
        return this.dataList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.teen_feed_item_new, null);
            return new ItemRowHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.load_more, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int i) {
        if (holder instanceof ItemRowHolder) {
            final ItemRowHolder itemRowHolder = (ItemRowHolder) holder;
            final SectionDataModel postdata = this.dataList.get(i);
            // load article image
            List<String> imagePaths = postdata.getPost_image_createdby_creator_url();
            if (imagePaths != null && imagePaths.isEmpty() == false) {
                Utils.loadImageUsingGlide(
                        this.mContext, itemRowHolder.imgPostPlaceholder, imagePaths.get(0));
            }

            itemRowHolder.imgPostPlaceholder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragment = new TeenfeedPreviewFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("postlistdata", postdata);
                    itemRowHolder.imgPostPlaceholder.setDrawingCacheEnabled(true);
                    Bitmap bitmap = itemRowHolder.imgPostPlaceholder.getDrawingCache();
                    bundle.putParcelable(Constants.IMAGE_BITMAP, bitmap);
                    fragment.setArguments(bundle);

                    FragmentManager fragmentManager = ((AppCompatActivity) mContext).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.anim.frag_fade_in, R.anim.frag_fade_out, R.anim.frag_fade_in, R.anim.frag_fade_out);
                    fragmentTransaction.replace(R.id.main_content, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });

            if (postdata.getPost_image_createdby_creator_url() != null && postdata.getPost_image_createdby_creator_url().size() > 1) {
                itemRowHolder.lblImageCount.setText("1/" + postdata.getPost_image_createdby_creator_url().size());
                itemRowHolder.lblImageCount.setVisibility(View.VISIBLE);
            } else {
                itemRowHolder.lblImageCount.setVisibility(View.GONE);
            }

            String ctyName = postdata.getCategory();
            if (ctyName != null) {
                if (ctyName.equalsIgnoreCase(Constants.ALL)) {
                    itemRowHolder.lblCategory.setText(Constants.FOR_YOU);
                } else {
                    itemRowHolder.lblCategory.setText(ctyName);
                }
            }
            int textColor = Utils.getCategoryColor(this.mContext, ctyName);
            itemRowHolder.lblCategory.setTextColor(textColor);

            String strdesc = postdata.getText_description();
            if (strdesc.contains("|")) {
                String array[] = strdesc.split("\\|");
                String title = array[0];
                //String desc = array[1];
                itemRowHolder.lblPostTitle.setText(title);
            } else {
                itemRowHolder.lblPostTitle.setText(strdesc);
            }

            int reactsCount = postdata.getLikecount();
            reactsCount += postdata.getCoolcount();
            reactsCount += postdata.getNerdcount();
            reactsCount += postdata.getSwegcount();
            reactsCount += postdata.getDabcount();
            postdata.setReactsCount(reactsCount);
        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    private void moveToComments(SectionDataModel postdata, int flag) {
        if (this.isTaggedUser) {
            Intent in = new Intent(this.mContext, CommentLikeActivity_new.class);
            Bundle bundle = new Bundle();
            bundle.putInt("comment_select_flag", flag);
            bundle.putInt("keypadshow", 1);
            bundle.putString("postid", postdata.getPostid());
            bundle.putString("type", "teenfeed");
            bundle.putString("react_type", "");
            bundle.putInt(Constants.COMMENTS_COUNT, postdata.getCommentcount());
            bundle.putInt(Constants.REACTS_COUNT, postdata.getReactsCount());
            in.putExtras(bundle);
            this.mContext.startActivity(in);
        } else {
            Utils.showUnverifiedUserDialog(this.mContext);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder {
        private TextView lblCategory;
        private TextView lblPostTitle;

        private TextView lblImageCount;
        private ImageView imgPostPlaceholder;

        public ItemRowHolder(View view) {
            super(view);

            this.lblCategory = (TextView) view.findViewById(R.id.lblCategory);
            this.lblPostTitle = (TextView) view.findViewById(R.id.lblPostTitle);

            this.lblImageCount = (TextView) view.findViewById(R.id.lblImageCount);
            this.imgPostPlaceholder = (ImageView) view.findViewById(R.id.imgPostPlaceholder);
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        }
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void ReceivedResponseFromServer(String output, String REQUEST_NUMBER) {
        if (RequestConstants.GET_ARTICLE_DETAIL == REQUEST_NUMBER) {
            try {
                JSONObject jsonObject = new JSONObject(output);
                JSONObject data = jsonObject.getJSONObject("data");
                String postId = data.getString("_id");
                boolean userLike = data.getBoolean("user_like");
                //this.dataMap.put(postId, output);
            } catch (Exception e) {
                // do nothing
            }
        }
    }
}