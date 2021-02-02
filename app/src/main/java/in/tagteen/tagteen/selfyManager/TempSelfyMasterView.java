package in.tagteen.tagteen.selfyManager;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import in.tagteen.tagteen.Model.InsertCoolModel;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.apimodule_retrofit.CommonApicallModule;
import in.tagteen.tagteen.backgroundUpload.UrlUtils;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.configurations.RequestConstants;
import in.tagteen.tagteen.configurations.ServerConnector;
import in.tagteen.tagteen.networkEngine.AsyncResponse;
import in.tagteen.tagteen.networkEngine.AsyncWorker;
import in.tagteen.tagteen.selfyManager.models.MainPagerAdapter;
import in.tagteen.tagteen.selfyManager.models.SelfyReactModel;
import in.tagteen.tagteen.util.CountDownTimer;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.utils.FontStyles;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;

/**
 * Created by lovekushvishwakarma on 06/01/18.
 */

public class TempSelfyMasterView extends Activity implements AsyncResponse {
    private ViewPager viewPager;
    private String _id1, userpic = "", username, login_userid, gender, Accesstoken;
    private LayoutInflater inflater;
    private TextView totalSlefy;
    private MainPagerAdapter pagerAdapter;
    private int pageSize = 0;
    //private ProgressBar progressbar;
    private int progressStatus = 0;
    private Handler handler = new Handler();
    private static int SELFY_MAX_PROGRESS = 10 * 1000;
    private CountDownTimer cdt;
    private SimpleExoPlayerView videoTemp;
    //private Runnable onEverySecond;
    private String action_id, user_id, timestamp;
    private SimpleExoPlayerView videoview;
    private SimpleExoPlayer exoPlayer;
    private int stopPosition;

    private Map<Integer, String> videopathMap = new HashMap<Integer, String>();
    private boolean isVideoPaused = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tempselfymasterview);
        login_userid =
                SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
        Accesstoken =
                SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TOKEN);

        _id1 = getIntent().getStringExtra("_id");
        username = getIntent().getStringExtra("username");
        userpic = getIntent().getStringExtra("userpic");
        gender = getIntent().getStringExtra("gender");

        inflater = getLayoutInflater();
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        pagerAdapter = new MainPagerAdapter();
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(0);

        // load selfies
        this.getMYSelfies(_id1);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (exoPlayer != null) {
                    exoPlayer.setPlayWhenReady(false);
                }
                View view = pagerAdapter.getView(position);
                ProgressBar progressbar = (ProgressBar) view.findViewById(R.id.progressbar);
                progressbar.setProgress(0);
                TextView totalSlefy = (TextView) view.findViewById(R.id.totalSlefy);
                SimpleExoPlayerView videoview = (SimpleExoPlayerView) view.findViewById(R.id.videoview);
                ImageView mySelfyImage = (ImageView) view.findViewById(R.id.mySelfyImage);
                ProgressBar loading_spinner = (ProgressBar) view.findViewById(R.id.loading_spinner);
                TextView tittle_text = (TextView) view.findViewById(R.id.tittle);
                videoTemp = videoview;
                changeSelfy(progressbar, totalSlefy, videoview, mySelfyImage, tittle_text, loading_spinner);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void changeSelfy(
            final ProgressBar progressbar,
            final TextView totalSlefy,
            final SimpleExoPlayerView videoView,
            ImageView mySelfyImage,
            TextView tittle_text,
            final ProgressBar loading_spinner) {

        boolean isvideo = false;
        try {
            isvideo = (boolean) totalSlefy.getTag();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (cdt != null) {
            cdt.cancel();
        }

        if (isvideo) {
            videoView.setVisibility(View.VISIBLE);
            mySelfyImage.setVisibility(View.GONE);

            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelector trackSelector =
                    new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
            exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);

            String url = this.videopathMap.get(this.viewPager.getCurrentItem());
            Uri videoURI = Uri.parse(UrlUtils.getUpdatedVideoUrl(url));
            DefaultHttpDataSourceFactory dataSourceFactory =
                    new DefaultHttpDataSourceFactory("exoplayer_video");
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            if (videoURI != null) {
                MediaSource mediaSource =
                        new ExtractorMediaSource(videoURI, dataSourceFactory, extractorsFactory, null, null);
                videoView.setPlayer(exoPlayer);
                exoPlayer.prepare(mediaSource);
                exoPlayer.setPlayWhenReady(true);
            }

            this.exoPlayer.addListener(new Player.EventListener() {
                @Override
                public void onSeekProcessed() {
                }

                @Override
                public void onPositionDiscontinuity(int reason) {
                }

                @Override
                public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
                }

                @Override
                public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
                }

                @Override
                public void onRepeatModeChanged(int repeatMode) {
                }

                @Override
                public void onTracksChanged(TrackGroupArray trackGroups,
                                            TrackSelectionArray trackSelections) {
                }

                @Override
                public void onLoadingChanged(boolean isLoading) {
                }

                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    /*if (ExoPlayer.STATE_ENDED == playbackState) {
                        int currentpage = viewPager.getCurrentItem();
                        if ((pageSize - 1) == currentpage) {
                            finish();
                        } else if (currentpage < pageSize) {
                            viewPager.setCurrentItem(currentpage + 1);
                        }
                    }*/
                    if (ExoPlayer.STATE_READY == playbackState) {
                        loading_spinner.setVisibility(View.GONE);
                        int duration = (int) exoPlayer.getDuration();
                        setCountDown(duration, progressbar);
                        //progressbar.postDelayed(onEverySecond, 100);
                    }
                }

                @Override
                public void onPlayerError(ExoPlaybackException error) {
                }

                @Override
                public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
                }
            });
        } else {
            videoView.setVisibility(View.GONE);
            mySelfyImage.setVisibility(View.VISIBLE);
            this.setCountDown(SELFY_MAX_PROGRESS, progressbar);
        }
        pagerAdapter.notifyDataSetChanged();
    }

    private void setCountDown(final int maxProgress, final ProgressBar progressbar) {
        progressbar.setMax(maxProgress);
        this.cdt = new CountDownTimer(maxProgress, 1000) {
            public void onTick(long millisUntilFinished) {
                int val = progressbar.getProgress();
                val = val + 1000;
                progressbar.setProgress(val);
            }

            public void onFinish() {
                // after timer
                progressbar.setProgress(maxProgress);
                int currentpage = viewPager.getCurrentItem();
                if ((pageSize - 1) == currentpage) {
                    finish();
                } else if (currentpage < pageSize) {
                    viewPager.setCurrentItem(currentpage + 1);
                }
            }
        };
        this.cdt.start();
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void ReceivedResponseFromServer(String output, String REQUEST_NUMBER) {
        Log.e(output, "");
        if (REQUEST_NUMBER.equalsIgnoreCase(RequestConstants.GET_ALL_SELFIES)) {

            try {
                JSONObject jsonObject = new JSONObject(output);

                JSONArray data = jsonObject.getJSONArray("data");
                boolean success = jsonObject.getBoolean("success");
                if (success) {
                    pageSize = data.length();
                    this.videopathMap.clear();
                    final ArrayList<ArrayList> reactList_userid_array = new ArrayList<>();
                    for (int i = 0; i < pageSize; i++) {
                        boolean isVideo = false;
                        final boolean[] islike = {false};
                        String url;
                        JSONObject innerdata = data.getJSONObject(i);
                        String date_created = innerdata.getString("date_created");
                        String last_date_updated = innerdata.getString("last_date_updated");
                        final String post_creator_id = innerdata.getString("post_creator_id");
                        String post_type_id = innerdata.getString("post_type_id");
                        String title = innerdata.getString("title");
                        final String _id = innerdata.getString("_id");
                        boolean is_selfie = innerdata.getBoolean("is_selfie");

                        ArrayList<SelfyReactModel> reactList = new ArrayList<>();
                        final ArrayList<String> reactList_userid = new ArrayList<>();
                        JSONArray action_type = innerdata.getJSONArray("action_type");
                        for (int j = 0; j < action_type.length(); j++) {
                            JSONObject innerActionType = action_type.getJSONObject(j);
                            timestamp = innerActionType.getString("timestamp");
                            user_id = innerActionType.getString("user_id");
                            action_id = innerActionType.getString("_id");
                            reactList.add(new SelfyReactModel(action_id, timestamp, user_id));
                            reactList_userid.add(user_id);
                        }
                        reactList_userid_array.add(reactList_userid);
                        if (reactList_userid_array.get(i).contains(login_userid)) {
                            islike[0] = true;
                        } else {
                            islike[0] = false;
                        }

                        JSONArray images = innerdata.getJSONArray("image");

                        if (images.length() > 0) {
                            //image selfy
                            isVideo = false;
                            JSONObject image = images.getJSONObject(0);
                            url = image.getString("url");
                            String width = image.getString("width");
                            String image_id = image.getString("_id");
                        } else {
                            // video selfy
                            isVideo = true;
                            JSONObject video = innerdata.getJSONObject("video");
                            url = video.getString("url");
                        }

                        FrameLayout v0 = (FrameLayout) inflater.inflate(R.layout.singlestatus_view, null);
                        final ProgressBar loading_spinner = (ProgressBar) v0.findViewById(R.id.loading_spinner);
                        final ImageView mySelfyImage = (ImageView) v0.findViewById(R.id.mySelfyImage);
                        final ImageView imageSelfyAciton = (ImageView) v0.findViewById(R.id.imageSelfyAciton);
                        ImageView imageBack = (ImageView) v0.findViewById(R.id.imageBack);
                        ImageView imagePic = (ImageView) v0.findViewById(R.id.imagePic);
                        TextView name = (TextView) v0.findViewById(R.id.name);
                        TextView time = (TextView) v0.findViewById(R.id.time);
                        TextView tittle_text = (TextView) v0.findViewById(R.id.tittle);
                        totalSlefy = (TextView) v0.findViewById(R.id.totalSlefy);
                        totalSlefy.setTag(isVideo);
                        ProgressBar progressbar = (ProgressBar) v0.findViewById(R.id.progressbar);
                        videoview = (SimpleExoPlayerView) v0.findViewById(R.id.videoview);
                        totalSlefy.setText((i + 1) + "/" + data.length());

                        Utils.loadProfilePic(TempSelfyMasterView.this, imagePic, userpic);
                        name.setText("" + username);
                        time.setText(Utils.getRelativeTime(Long.parseLong(date_created)) + "");
                        if (title.equalsIgnoreCase(null) ||
                                title.equalsIgnoreCase("") ||
                                title.equalsIgnoreCase("-")) {
                            tittle_text.setVisibility(View.GONE);
                        } else {
                            tittle_text.setVisibility(View.VISIBLE);
                            tittle_text.setText(title);
                            tittle_text.setTypeface(FontStyles.font4Profile(TempSelfyMasterView.this));
                        }
                        if (isVideo) {
                            mySelfyImage.setVisibility(View.GONE);
                            loading_spinner.setVisibility(View.VISIBLE);
                            videoview.setVisibility(View.VISIBLE);
                            this.videopathMap.put(i, url);
                        } else {
                            videoview.setVisibility(View.GONE);
                            loading_spinner.setVisibility(View.VISIBLE);
                            Glide.with(TempSelfyMasterView.this).load(UrlUtils.getUpdatedImageUrl(url, "large"))
                                    .listener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                                    Target<Drawable> target, boolean isFirstResource) {
                                            loading_spinner.setVisibility(View.GONE);
                                            mySelfyImage.setVisibility(View.VISIBLE);
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Drawable resource, Object model,
                                                                       Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                            loading_spinner.setVisibility(View.GONE);
                                            mySelfyImage.setVisibility(View.VISIBLE);
                                            return false;
                                        }
                                    })
                                    .into(mySelfyImage);
                        }

                        if (post_creator_id.equalsIgnoreCase(login_userid)) {
                            if (!reactList.isEmpty()) {
                                gender_with_crown_image(imageSelfyAciton);
                            } else {
                                gender_with_out_crown_image(imageSelfyAciton);
                            }
                        } else {
                            gender_image_change_other_login(islike[0], imageSelfyAciton);
                        }
                        final MediaPlayer like =
                                MediaPlayer.create(TempSelfyMasterView.this, R.raw.like_teenfeed);
                        imageSelfyAciton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (post_creator_id.equalsIgnoreCase(login_userid)) {
                                    //@ant22
                                    cdt.pause();
                                    //loggedin user
                                    if (videoview != null && exoPlayer != null) {
                                        stopPosition = (int) exoPlayer.getCurrentPosition();
                                        //videoview.pause();
                                        exoPlayer.setPlayWhenReady(false);
                                        isVideoPaused = true;
                                    } else {
                                        cdt.pause();
                                    }
                                    new ReactionOnMySelfy(TempSelfyMasterView.this, _id, gender);
                                } else {
                                    // other user
                                    if (islike[0]) {
                                        if (like != null) like.start();
                                        //deleteaction_dialogshow(_id);
                                        RemoveReaction(_id, login_userid);
                                    } else {
                                        if (like != null) like.start();
                                        AddReaction(_id, login_userid);
                                    }
                                    islike[0] = !islike[0];
                                    gender_image_change_other_login(islike[0], imageSelfyAciton);
                                }
                            }
                        });
                        pagerAdapter.addView(v0, i);

                        imageBack.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                finish();
                            }
                        });
                        if (i == 0) {
                            changeSelfy(progressbar, totalSlefy, videoview, mySelfyImage, tittle_text,
                                    loading_spinner);
                        }
                    }

                    pagerAdapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (REQUEST_NUMBER.equalsIgnoreCase(RequestConstants.INSERT_ACTION_ONSELFY)) {
            try {
                JSONObject jsonObject = new JSONObject(output);
                boolean success = jsonObject.getBoolean("success");
                if (success) {
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteaction_dialogshow(final String _id) {
        final Dialog d = new Dialog(TempSelfyMasterView.this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setCanceledOnTouchOutside(true);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        d.setContentView(R.layout.confirmationpopup);
        final TextView msg = (TextView) d.findViewById(R.id.message);
        TextView name = (TextView) d.findViewById(R.id.yourname);
        TextView continueorder = (TextView) d.findViewById(R.id.confirm);
        TextView dismiss = (TextView) d.findViewById(R.id.dismiss);
        TextView confirm_ok_btn = (TextView) d.findViewById(R.id.confirm_ok_btn);
        confirm_ok_btn.setVisibility(View.GONE);
        name.setVisibility(View.GONE);
        msg.setText("Are you sure ?");
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        continueorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
                RemoveReaction(_id, login_userid);
            }
        });
        d.show();
    }

    private void gender_image_change_other_login(boolean islike, ImageView imageSelfyAciton) {
        if (islike) {
            gender_with_crown_image(imageSelfyAciton);
        } else {
            gender_with_out_crown_image(imageSelfyAciton);
        }
    }

    private void gender_with_out_crown_image(ImageView imageSelfyAciton) {
        if (gender.equalsIgnoreCase("male")) {
            imageSelfyAciton.setImageResource(R.drawable.king_without_crown);
        } else {
            imageSelfyAciton.setImageResource(R.drawable.princess_without_crown);
        }
    }

    private void gender_with_crown_image(ImageView imageSelfyAciton) {
        if (gender.equalsIgnoreCase("male")) {
            imageSelfyAciton.setImageResource(R.drawable.king_with_crown);
        } else {
            imageSelfyAciton.setImageResource(R.drawable.princess_with_crown);
        }
    }

    private void RemoveReaction(String _id, String post_creator_id) {
        InsertCoolModel json = new InsertCoolModel();
        json.setFlag(6);
        json.setPost_id(_id);
        json.setFriend_user_id(post_creator_id);
        CommonApicallModule.deleteCoolSwagDebNerd(json, Accesstoken, TempSelfyMasterView.this);
    }

    private void AddReaction(String id, String friendsId) {
        AsyncWorker mWorker = new AsyncWorker(TempSelfyMasterView.this);
        mWorker.delegate = this;
        mWorker.delegate = this;
        JSONObject BroadcastObject = new JSONObject();
        try {
            BroadcastObject.put("selfie_id", id);
            BroadcastObject.put("friend_user_id", friendsId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mWorker.execute(
                ServerConnector.REQUEST_ADD_SELFIE_REACTION,
                BroadcastObject.toString(),
                RequestConstants.POST_REQUEST,
                RequestConstants.HEADER_YES,
                RequestConstants.INSERT_ACTION_ONSELFY);
    }

    void getMYSelfies(String userid) {
        AsyncWorker mWorker = new AsyncWorker(TempSelfyMasterView.this);
        mWorker.delegate = this;
        mWorker.delegate = this;
        JSONObject BroadcastObject = new JSONObject();
        String url =
                ServerConnector.BASE_URL + "/write/api/v1.0/user/get_my_selfie_post/" + _id1 + "/1";
        mWorker.execute(url, BroadcastObject.toString(), RequestConstants.GET_REQUEST,
                RequestConstants.HEADER_YES, RequestConstants.GET_ALL_SELFIES);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (videoview != null && exoPlayer != null) {
            //exoPlayer.seekTo(stopPosition);
            exoPlayer.setPlayWhenReady(true);
            this.isVideoPaused = false;
        }
        if (cdt != null) {
            cdt.resume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (exoPlayer != null) {
            exoPlayer.release();
        }
    }
}
