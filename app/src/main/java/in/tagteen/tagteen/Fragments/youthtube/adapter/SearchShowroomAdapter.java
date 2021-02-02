package in.tagteen.tagteen.Fragments.youthtube.adapter;

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
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import in.tagteen.tagteen.Fragments.beans.Image;
import in.tagteen.tagteen.Fragments.beans.ShowRooms;
import in.tagteen.tagteen.Model.GetPostResponseModel;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;


public class SearchShowroomAdapter extends RecyclerView.Adapter<SearchShowroomAdapter.MyViewHolder> {
    ArrayList<ShowRooms> searchList;
    Context context;
    String userid;
    String token;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView videoducation, videoDesc, textRocks, textViewCount, textTime, txtname;
        public ImageView videoThumb, imagePic;
        RelativeLayout relative1;

        public MyViewHolder(View view) {
            super(view);
            txtname = (TextView) view.findViewById(R.id.txtname);
            videoducation = (TextView) view.findViewById(R.id.videoducation);
            textRocks = (TextView) view.findViewById(R.id.textRocks);
            videoDesc = (TextView) view.findViewById(R.id.videoDesc);
            textViewCount = (TextView) view.findViewById(R.id.textViewCount);
            textTime = (TextView) view.findViewById(R.id.textTime);
            videoThumb = (ImageView) view.findViewById(R.id.iconId);
            imagePic = (ImageView) view.findViewById(R.id.imagePic);
            relative1=(RelativeLayout) view.findViewById(R.id.relative1);
        }
    }

    public SearchShowroomAdapter(Context context, ArrayList<ShowRooms> searchList) {
        this.context = context;
        this.searchList = searchList;
        SharedPreferenceSingleton.getInstance().init(context);
        userid = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
        token = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TOKEN);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_profile_video_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final ShowRooms data = searchList.get(position);

        try {
            holder.textTime.setText("" + Utils.getRelativeTime(Long.parseLong(data.getDateCreated())));
            holder.textRocks.setText("" + data.getLikeCount());
            holder.textViewCount.setText("" + data.getViewCount() + " Viewed");
            holder.videoDesc.setText("" + data.getContent());
            holder.txtname.setText("" + data.getFirstName() + " " + data.getLastName());
            Glide.with(context).load(data.getVideo_thumbnail().get(0).getUrl()).into(holder.videoThumb);
            Utils.loadProfilePic(context, holder.imagePic, data.getProfileUrl());
            holder.relative1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hidekeypad();
                    Utils.moveToVideoDetails(context, data);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private GetPostResponseModel.PostDetails getPostData(ShowRooms data) {
        GetPostResponseModel.PostDetails ret = new GetPostResponseModel.PostDetails();
        ret.setConversationCount(data.getConversationCount());
        ret.setLikeCount(data.getLikeCount());
        ret.setCategorie_id(data.getCategorieId());
        ret.setCategorie_name(data.getCategorieName());
        ret.setContent(data.getContent());
        ret.setCoolCount(data.getCoolCount());
        ret.setLikeCount(data.getLikeCount());
        ret.setDabCount(data.getDabCount());
        ret.setNerdCount(data.getNerdCount());
        ret.setSwegCount(data.getSwegCount());
        ret.setDateCreated(Long.parseLong(data.getDateCreated()));
        ret.setFirst_name(data.getFirstName());
        ret.setLast_name(data.getLastName());
        ret.setTagged_number(data.getTaggedNumber());
        ret.setId(data.getId());

        List<GetPostResponseModel.PostDetails.Image> images = new ArrayList<>();
        if (data.getImage() != null) {
            for (Image image : data.getImage()) {
                GetPostResponseModel.PostDetails.Image imageData = ret.new Image();
                imageData.setHeight(Integer.parseInt(image.getHeight()));
                imageData.setId(image.getId());
                imageData.setUrl(image.getUrl());
                imageData.setWidth(Integer.parseInt(image.getWidth()));
                images.add(imageData);
            }
        }
        ret.setImage(images);

        if (data.getVideo() != null) {
            GetPostResponseModel.PostDetails.Video video = ret.new Video();
            video.setHeight(Integer.parseInt(data.getVideo().getHeight()));
            //video.setId(data.getVideo().ge);
            video.setUrl(data.getVideo().getUrl());
            video.setWidth(Integer.parseInt(data.getVideo().getWidth()));
            ret.setVideo(video);
        }

        List<GetPostResponseModel.PostDetails.Image> thumbnails = new ArrayList<>();
        if (data.getVideo_thumbnail() != null) {
            for (Image image : data.getVideo_thumbnail()) {
                GetPostResponseModel.PostDetails.Image imageData = ret.new Image();
                imageData.setHeight(Integer.parseInt(image.getHeight()));
                imageData.setId(image.getId());
                imageData.setUrl(image.getUrl());
                imageData.setWidth(Integer.parseInt(image.getWidth()));
                thumbnails.add(imageData);
            }
        }
        ret.setVideoThumbnails(thumbnails);

        ret.setProfile_url(data.getProfileUrl());
        ret.setUserLike(data.getUserLike());
        ret.setView_count(data.getViewCount());
        ret.setPostCreatorId(data.getPostCreatorId());
        return ret;
    }

    private void hidekeypad() {
        View view = ((AppCompatActivity)context).getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)((AppCompatActivity)context).getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
