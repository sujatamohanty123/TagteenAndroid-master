package in.tagteen.tagteen;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

import java.util.ArrayList;
import java.util.List;

import in.tagteen.tagteen.Adapters.EpisodesAdapter;
import in.tagteen.tagteen.Fragments.youthtube.WebShowsActivity;
import in.tagteen.tagteen.Fragments.youthtube.adapter.WebShowsAdapter;
import in.tagteen.tagteen.Interfaces.OnLoadMoreListener;
import in.tagteen.tagteen.Model.WebshowModel;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WebShowsDetailsActivity extends AppCompatActivity {
    private RecyclerView recyclerRelatedVideos;
    private ImageView imgThumbnail;
    //private TextView lblPlayVideo;
    private TextView lblTitle;
    private TextView lblVideoDuration;
    private TextView lblDescription;
    private TextView lblCast;

    private WebshowModel.WebshowDetails selectedPostData;
    private int webshowTypeId;
    private String userId;
    private int page = 1;
    private int limit = 10;
    private boolean apiBlocker = false;
    private boolean isLoadingData = false;

    private EpisodesAdapter adapter;
    private ArrayList<WebshowModel.WebshowDetails> webshowsList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webshows_details);

        this.initWidgets();
        this.bindEvents();

        this.loadData();
    }

    private void initWidgets() {
        this.userId = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
        // get arguments
        Bundle bundle = getIntent().getExtras();
        if (bundle.getSerializable(Constants.SHOWROOM_POST_DATA) != null) {
            if (bundle.getSerializable(Constants.SHOWROOM_POST_DATA) instanceof WebshowModel.WebshowDetails) {
                this.selectedPostData = (WebshowModel.WebshowDetails) bundle.getSerializable(Constants.SHOWROOM_POST_DATA);
            }
        }
        this.webshowTypeId = bundle.getInt(Constants.WEBSHOW_TYPE_ID, 0);

        //this.lblPlayVideo = findViewById(R.id.lblPlayVideo);
        this.lblTitle = findViewById(R.id.lblShowName);
        this.lblVideoDuration = findViewById(R.id.lblVideoDuration);
        this.lblDescription = findViewById(R.id.lblDescription);
        this.imgThumbnail = findViewById(R.id.imgThumbnail);
        this.lblCast = findViewById(R.id.lblCast);
        this.recyclerRelatedVideos = findViewById(R.id.recyclerRelatedVideos);


        if (this.selectedPostData != null &&
                this.selectedPostData.getVideoThumbnailUrl() != null &&
                this.selectedPostData.getVideoThumbnailUrl().isEmpty() == false) {
            Utils.loadImageUsingGlide(
                    this, this.imgThumbnail, this.selectedPostData.getVideoThumbnailUrl());
        }

        this.recyclerRelatedVideos.setLayoutManager(new GridLayoutManager(this, 3));

        if (this.selectedPostData.getTitle() != null) {
            this.lblTitle.setText(this.selectedPostData.getTitle());
        }

        if (this.selectedPostData.getDescription() != null) {
            this.lblDescription.setText(this.selectedPostData.getDescription());
        }

        if (this.selectedPostData.getHostedBy() != null) {
            this.lblCast.setText("Cast : " + this.selectedPostData.getHostedBy());
        }

        if (this.selectedPostData.getDuration() != null) {
            this.lblVideoDuration.setText(this.selectedPostData.getDuration());
        }
    }

    private void bindEvents() {
        this.imgThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToPreview();
            }
        });
    }

    private void loadData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getRelatedVideos();
            }
        }, 500);
    }

    private void moveToPreview() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.SHOWROOM_POST_DATA, this.selectedPostData);

        Intent intent = new Intent(this, WebShowPreviewActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void getRelatedVideos() {
        if (this.webshowTypeId == 0) {
            return;
        }
        if (this.webshowsList == null) {
            this.webshowsList = new ArrayList<>();
        }
        Apimethods methods = API_Call_Retrofit.getretrofit(this).create(Apimethods.class);
        Call<WebshowModel> call = methods.getWebshowVideos(this.page, this.limit, this.webshowTypeId, this.userId);

        call.enqueue(new Callback<WebshowModel>() {
            @Override
            public void onResponse(Call<WebshowModel> call, Response<WebshowModel> response) {
                int statuscode = response.code();
                apiBlocker = true;

                if (page == 1) {
                    webshowsList.clear();
                }
                if (statuscode == 200) {
                    page++;
                    WebshowModel getresponsemodel = response.body();
                    ArrayList<WebshowModel.WebshowDetails> responseData =
                            (ArrayList<WebshowModel.WebshowDetails>) getresponsemodel.getData();
                    if (responseData != null && !responseData.isEmpty()) {
                        // ignore the selected one
                        ArrayList<WebshowModel.WebshowDetails> webshows = new ArrayList<>();
                        for (WebshowModel.WebshowDetails webshow : responseData) {
                            if (!webshow.getWebshowId().equals(selectedPostData.getWebshowId())) {
                                webshows.add(webshow);
                            }
                        }
                        int itemAddedIndex = webshowsList.size() - 1;
                        if (webshows.isEmpty()) {
                            apiBlocker = false;
                        } else {
                            webshowsList.addAll(webshows);
                        }

                        if (adapter == null) {
                            adapter = new EpisodesAdapter(WebShowsDetailsActivity.this, webshowsList, recyclerRelatedVideos);
                            recyclerRelatedVideos.setAdapter(adapter);
                            adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                                @Override
                                public void onLoadMore() {
                                    if (apiBlocker && isLoadingData == false) {
                                        apiBlocker = false;
                                        getRelatedVideos();
                                    }
                                }
                            });
                        } else {
                            if (webshows.isEmpty() == false) {
                                adapter.notifyItemRangeInserted(itemAddedIndex, webshows.size());
                            }
                            adapter.setLoaded();
                        }
                        isLoadingData = false;
                    }

                    if (webshowsList.isEmpty()) {
                        recyclerRelatedVideos.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<WebshowModel> call, Throwable t) {

            }
        });
    }
}
