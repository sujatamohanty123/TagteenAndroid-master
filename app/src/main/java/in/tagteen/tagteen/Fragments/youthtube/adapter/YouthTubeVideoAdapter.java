package in.tagteen.tagteen.Fragments.youthtube.adapter;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Handler;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;;
import in.tagteen.tagteen.Fragments.youthtube.FullScreenVideoFragment;
import in.tagteen.tagteen.Fragments.youthtube.MyCustomLayoutManager;
import in.tagteen.tagteen.Fragments.youthtube.VideoDetailListActivity;
import in.tagteen.tagteen.Model.GetPostResponseModel;
import in.tagteen.tagteen.Model.SectionDataModel;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.util.VideoTimerUtils;
import in.tagteen.tagteen.utils.FontStyles;
import in.tagteen.tagteen.utils.MyBounceInterpolator;

import static in.tagteen.tagteen.Fragments.youthtube.YouthTubeVideoList.moveTo;


public class YouthTubeVideoAdapter extends RecyclerView.Adapter<YouthTubeVideoAdapter.ImageViewHolder> {

    private ArrayList<GetPostResponseModel.PostDetails> youthList;
    private Context mContext;
    private final Animation myAnim;
    private RecyclerView recyclerView;
    private MyCustomLayoutManager layoutmanager;
    private Runnable onEverySecond;
    private VideoDetailListActivity videoDetailListActivity;
    private static VideoView tempVideoview;
    private List<SectionDataModel> momentsvideo = new ArrayList<>();
    private int flag = 0;

    private int selectedIndex = -1;

    public YouthTubeVideoAdapter(int flag, ArrayList<GetPostResponseModel.PostDetails> youthList, Context mContext, RecyclerView recyclerView, MyCustomLayoutManager layoutmanager, VideoDetailListActivity videoDetailListActivity) {
        this.youthList = youthList;
        this.mContext = mContext;
        this.recyclerView = recyclerView;
        this.videoDetailListActivity = videoDetailListActivity;
        myAnim = AnimationUtils.loadAnimation(mContext, R.anim.bounce);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        moveTo = 0;
        this.layoutmanager = layoutmanager;
        this.flag=flag;
    }

    public YouthTubeVideoAdapter(int flag,List<SectionDataModel> momentsvideo, Context mContext, RecyclerView recyclerView, MyCustomLayoutManager layoutmanager, VideoDetailListActivity videoDetailListActivity) {
        this.momentsvideo = momentsvideo;
        this.mContext = mContext;
        this.recyclerView = recyclerView;
        this.videoDetailListActivity = videoDetailListActivity;
        myAnim = AnimationUtils.loadAnimation(mContext, R.anim.bounce);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        moveTo = 0;
        this.layoutmanager = layoutmanager;
        this.flag=flag;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.youth_tube_iteam, parent, false));
    }

    @Override
    public int getItemCount() {
        int size=0;
        if(flag==1) {
            size= youthList.size();
        }else {
            size=momentsvideo.size();
        }
        return size;
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, int position) {
        if(flag==1) {
            holder.progressVideo.setVisibility(View.GONE);
            final GetPostResponseModel.PostDetails data = youthList.get(position);

            holder.userName.setText(data.getFirst_name() + " " + data.getLast_name());
            holder.userTag.setText(data.getTagged_number());
            holder.feedTime.setText("" + Utils.getRelativeTime(data.getDateCreated()));
            holder.textRokCount.setText(data.getLikeCount() + "");
            holder.textCommentCount.setText(data.getConversationCount() + " Comments");
            holder.viewCount.setText(data.getCoolCount() + " Views");
            holder.feedDesc.setText(data.getContent());

            holder.rokImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.rokImage.startAnimation(myAnim);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (data.getUserLike()) {
                                data.setUserLike(false);
                                holder.rokImage.setImageResource(R.drawable.ic_bottom_youthube);

                            } else {
                                data.setUserLike(true);
                                holder.rokImage.setImageResource(R.drawable.ic_bottom_youthube);
                            }
                        }
                    }, 300);
                }
            });

            holder.textBeAFnn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                /*if (animalItem.getBeAfnn()==0) {
                    animalItem.setBeAfnn(1);
                    holder.textBeAFnn.setBackgroundResource(R.drawable.fnn_select);
                    holder.textBeAFnn.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
                } else {
                    animalItem.setBeAfnn(0);
                    holder.textBeAFnn.setTextColor(mContext.getResources().getColor(R.color.white));
                    holder.textBeAFnn.setBackgroundResource(R.drawable.fnn_unselect);
                }*/
                }
            });

            int height = data.getVideo().getHeight();
            int width = data.getVideo().getWidth();
            float ht_px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, mContext.getResources().getDisplayMetrics());
            float wt_px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, mContext.getResources().getDisplayMetrics());
            holder.videoPlayer.getLayoutParams().height = (int) ht_px;
            //holder.videoPlayer.getLayoutParams().width = (int) wt_px;
            holder.videoPlayer.requestLayout();


            String rrr = data.getVideo().getUrl();
            holder.progressVideo.setVisibility(View.VISIBLE);
            holder.videoPlayer.setVideoPath(rrr);
            holder.videoPlayer.start();
            onEverySecond = new Runnable() {

                @Override
                public void run() {

                    if (holder.seekabrview != null) {
                        holder.seekabrview.setProgress(holder.videoPlayer.getCurrentPosition());

                        String totalDuration = VideoTimerUtils.milliSecondsToTimer(holder.videoPlayer.getDuration());
                        String currentPosition = VideoTimerUtils.milliSecondsToTimer(holder.videoPlayer.getCurrentPosition());
                        holder.textTotaltime.setText("" + totalDuration);
                        holder.textTimeupdate.setText("" + currentPosition);
                    }

                    if (holder.videoPlayer.isPlaying()) {
                        holder.seekabrview.postDelayed(onEverySecond, 1000);
                    }

                }
            };
            holder.seekabrview.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    holder.videoPlayer.seekTo(seekBar.getProgress());
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });


            holder.videoPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    holder.videoThumb.setVisibility(View.GONE);
                    holder.progressVideo.setVisibility(View.GONE);
                    mediaPlayer.setLooping(true);


                    holder.seekabrview.setMax(holder.videoPlayer.getDuration());
                    if (onEverySecond != null)
                        holder.seekabrview.postDelayed(onEverySecond, 1000);


                }
            });

            holder.videoPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if (youthList.size() != 1) {
                        //recyclerView.scrollToPosition(moveTo + 1);
                        recyclerView.smoothScrollToPosition(moveTo + 1);
                    }
                }
            });
            Utils.loadProfilePic(mContext, holder.profileImage, data.getProfile_url());
            if(data.getImage().isEmpty()){
                holder.videoThumb.setVisibility(View.GONE);
            }else {
                Picasso.with(this.mContext).load(data.getImage().get(0).getUrl()).into(holder.videoThumb);
            }

            // holder.makeVideofullscren.setVisibility(View.VISIBLE);
            holder.makeVideofullscren.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    videoDetailListActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    FragmentManager fragmentManager = ((AppCompatActivity) mContext).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.framlayout, new FullScreenVideoFragment(holder.videoPlayer, videoDetailListActivity));
                    fragmentTransaction.commit();
                    fragmentTransaction.addToBackStack("Full");
                }
            });
        }else {
            holder.progressVideo.setVisibility(View.GONE);
            final SectionDataModel data = momentsvideo.get(position);

            holder.userName.setText(data.getPost_creator_name());
            holder.userTag.setText(data.getPosted_creator_tagged_number());
            holder.feedTime.setText("" + Utils.getRelativeTime(data.getPost_created_date_time()));
            holder.textRokCount.setText(data.getLikecount() + "");
            String commentCount = data.getCommentcount() + " ";
            if (data.getCommentcount() > 1) {
                commentCount += "Comments";
            } else {
                commentCount += "Comment";
            }
            holder.textCommentCount.setText(commentCount);
            holder.viewCount.setText(data.getCoolcount() + " Views");
            holder.feedDesc.setText(data.getText_description());

            holder.textBeAFnn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                /*if (animalItem.getBeAfnn()==0) {
                    animalItem.setBeAfnn(1);
                    holder.textBeAFnn.setBackgroundResource(R.drawable.fnn_select);
                    holder.textBeAFnn.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
                } else {
                    animalItem.setBeAfnn(0);
                    holder.textBeAFnn.setTextColor(mContext.getResources().getColor(R.color.white));
                    holder.textBeAFnn.setBackgroundResource(R.drawable.fnn_unselect);
                }*/
                }
            });

            int height = data.getPost_video_height();
            int width = data.getPost_video_width();
            float ht_px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, mContext.getResources().getDisplayMetrics());
            float wt_px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width, mContext.getResources().getDisplayMetrics());
            holder.videoPlayer.getLayoutParams().height = (int) ht_px;
            //holder.videoPlayer.getLayoutParams().width = (int) wt_px;
            holder.videoPlayer.requestLayout();


            String rrr = data.getPost_video_url();
            holder.progressVideo.setVisibility(View.VISIBLE);
            holder.videoPlayer.setVideoPath(rrr);
            if (position == 0) {
                holder.framTrans.setVisibility(View.GONE);
                holder.videoPlayer.setVisibility(View.VISIBLE);
                holder.videoPlayer.start();
            } else {
                holder.framTrans.setVisibility(View.VISIBLE);
            }

            onEverySecond = new Runnable() {

                @Override
                public void run() {

                    if (holder.seekabrview != null) {
                        holder.seekabrview.setProgress(holder.videoPlayer.getCurrentPosition());

                        String totalDuration = VideoTimerUtils.milliSecondsToTimer(holder.videoPlayer.getDuration());
                        String currentPosition = VideoTimerUtils.milliSecondsToTimer(holder.videoPlayer.getCurrentPosition());
                        holder.textTotaltime.setText("" + totalDuration);
                        holder.textTimeupdate.setText("" + currentPosition);
                    }

                    if (holder.videoPlayer.isPlaying()) {
                        holder.seekabrview.postDelayed(onEverySecond, 1000);
                    }

                }
            };
            holder.seekabrview.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    holder.videoPlayer.seekTo(seekBar.getProgress());
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });


            holder.videoPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    holder.videoThumb.setVisibility(View.GONE);
                    holder.progressVideo.setVisibility(View.GONE);
                    mediaPlayer.setLooping(true);


                    holder.seekabrview.setMax(holder.videoPlayer.getDuration());
                    if (onEverySecond != null)
                        holder.seekabrview.postDelayed(onEverySecond, 1000);


                }
            });

            holder.videoPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if (youthList.size() >1) {
                        recyclerView.smoothScrollToPosition(moveTo + 2);
                    }
                }
            });
            Utils.loadProfilePic(mContext, holder.profileImage, data.getPost_creator_profilepic());
            Picasso.with(this.mContext).load(data.getPost_image_createdby_creator_url().get(0)).into(holder.videoThumb);

            // holder.makeVideofullscren.setVisibility(View.VISIBLE);
            holder.makeVideofullscren.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    videoDetailListActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    FragmentManager fragmentManager = ((AppCompatActivity) mContext).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.framlayout, new FullScreenVideoFragment(holder.videoPlayer, videoDetailListActivity));
                    fragmentTransaction.commit();
                    fragmentTransaction.addToBackStack("Full");
                }
            });
        }
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        private ImageView rokImage, imagecomment, imageShare, profileImage, moreOptions, videoThumb, makeVideofullscren;
        private TextView userName, userTag, feedDesc, textShareCount, textCommentCount, textRokCount,
                viewCount, textBeAFnn, feedTime, textTimeupdate, textTotaltime;
        private VideoView videoPlayer;
        FrameLayout framTrans;
        ProgressBar progressVideo;
        SeekBar seekabrview;

        public ImageViewHolder(View itemView) {
            super(itemView);
            videoPlayer = (VideoView) itemView.findViewById(R.id.videoPlayer);
            profileImage = (ImageView) itemView.findViewById(R.id.profileImage);
            moreOptions = (ImageView) itemView.findViewById(R.id.moreOptions);
            imageShare = (ImageView) itemView.findViewById(R.id.imageShare);
            rokImage = (ImageView) itemView.findViewById(R.id.rokImage);
            imagecomment = (ImageView) itemView.findViewById(R.id.imagecomment);
            textShareCount = (TextView) itemView.findViewById(R.id.textShareCount);
            textCommentCount = (TextView) itemView.findViewById(R.id.textCommentCount);
            textRokCount = (TextView) itemView.findViewById(R.id.textRokCount);
            viewCount = (TextView) itemView.findViewById(R.id.viewCount);
            textBeAFnn = (TextView) itemView.findViewById(R.id.textBeAFnn);
            feedTime = (TextView) itemView.findViewById(R.id.feedTime);
            framTrans = (FrameLayout) itemView.findViewById(R.id.framTrans);
            videoThumb = (ImageView) itemView.findViewById(R.id.videoThumb);
            makeVideofullscren = (ImageView) itemView.findViewById(R.id.makeVideofullscren);

            textTimeupdate = (TextView) itemView.findViewById(R.id.textTimeupdate);
            textTotaltime = (TextView) itemView.findViewById(R.id.textTotaltime);
            userName = (TextView) itemView.findViewById(R.id.userName);
            userTag = (TextView) itemView.findViewById(R.id.userTag);
            feedDesc = (TextView) itemView.findViewById(R.id.feedDesc);
            progressVideo = (ProgressBar) itemView.findViewById(R.id.progressVideo);
            seekabrview = (SeekBar) itemView.findViewById(R.id.seekabrview);

            //setFonts
            userName.setTypeface(FontStyles.font4Profile(mContext));
            userTag.setTypeface(FontStyles.font4Profile(mContext));
            feedDesc.setTypeface(FontStyles.OpenSansLight(mContext));
        }
    }

    public static void playWhenFullScrnClose() {
        try {
            tempVideoview.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}