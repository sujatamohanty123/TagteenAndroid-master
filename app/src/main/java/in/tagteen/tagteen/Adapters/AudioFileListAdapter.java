package in.tagteen.tagteen.Adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import in.tagteen.tagteen.Model.SongMediaModel;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.TagteenInterface.AudioClickList;

import java.util.ArrayList;
import java.util.List;


public class AudioFileListAdapter extends RecyclerView.Adapter<in.tagteen.tagteen.Adapters.AudioFileListAdapter.ItemRowHolder> {

    private List<SongMediaModel> dataList = new ArrayList<SongMediaModel>();
    private Animation fadeIn, fadeOut;
    private Context mContext;
    AudioClickList audioClickList;

    public AudioFileListAdapter(Context context, List<SongMediaModel> dataList, AudioClickList audioClickList) {
        this.dataList = dataList;
        this.mContext = context;
        this.audioClickList=audioClickList;
    }

    @Override
    public in.tagteen.tagteen.Adapters.AudioFileListAdapter.ItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.tt_audio_file_iteam, null);
        in.tagteen.tagteen.Adapters.AudioFileListAdapter.ItemRowHolder mh = new in.tagteen.tagteen.Adapters.AudioFileListAdapter.ItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(in.tagteen.tagteen.Adapters.AudioFileListAdapter.ItemRowHolder itemRowHolder, int i) {
        SongMediaModel singleSectionItems = dataList.get(i);
        itemRowHolder.itemTitle.setText(singleSectionItems.getmSongTitle());
        itemRowHolder.fileSize.setText(timeFormate(Integer.parseInt(singleSectionItems.getmDuration())));
    }

    private String  timeFormate(int time){
        int minutes = (int) (time / 60000);
        int seconds = (int) (time / 1000) % 60;
        int milliseconds = (int) (time / 100) % 10;
        return (minutes+":"+(seconds < 10 ? "0"+seconds : seconds)+"."+milliseconds).toString();
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView itemTitle;
        private   ImageView postImage;
        private   TextView fileSize;
        private LinearLayout mainLayout;

        public ItemRowHolder(View view) {
            super(view);

            itemTitle  = (TextView) view.findViewById(R.id.title_audio);
            postImage  = (ImageView)view.findViewById(R.id.userViewImage);
            fileSize   = (TextView) view.findViewById(R.id.title_size);
            mainLayout = (LinearLayout)view.findViewById(R.id.main_content_audio);



            mainLayout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    int position = getAdapterPosition();
                    SongMediaModel re =dataList.get(position);
                    audioClickList.clickedMusicList(re.getmSongPath(),re.getmSongTitle());
                }
            });
        }

    }

}