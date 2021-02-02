package in.tagteen.tagteen.Fragments.youthtube;

import android.annotation.TargetApi;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.List;

import in.tagteen.tagteen.Fragments.youthtube.adapter.YouthTubeVideoAdapter;
import in.tagteen.tagteen.Model.GetPostResponseModel;
import in.tagteen.tagteen.Model.SectionDataModel;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lovekushvishwakarma on 24/09/17.
 */

public class YouthTubeVideoList extends Fragment {
    boolean apiCallBlocler = false;
    String userid;
    int pagenumber = 1;
    int  select_caregory_id=0;
    int flag=0;

    VideoDetailListActivity videoDetailListActivity;

    public YouthTubeVideoList(VideoDetailListActivity videoDetailListActivity, int flag, int select_caregory_id) {
        this.videoDetailListActivity = videoDetailListActivity;
        this.flag=flag;
        this.select_caregory_id=select_caregory_id;
    }

    RecyclerView rv_featureList;
    YouthTubeVideoAdapter adapter;
    ArrayList<GetPostResponseModel.PostDetails> youthList;
    List<SectionDataModel> momentsvideo=new ArrayList<>();
    private View currentFocusedLayout, oldFocusedLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.showroom_videolist, container, false);
    }

    public static int moveTo = 0;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rv_featureList = (RecyclerView) view.findViewById(R.id.recycler_view);

        userid = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);


        youthList = new ArrayList<>();
        if(flag==1) {
            //youthList.add(YouthFeedMainAdapter.sendObj);
            callapiforgetPost();
        }else {
           // momentsvideo.add(MomentslistAdapter.sendObj);
        }
        final MyCustomLayoutManager layoutManager = new MyCustomLayoutManager(getContext());
        rv_featureList.setLayoutManager(layoutManager);


        RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(getActivity()) {
            @Override
            protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };


        rv_featureList.addItemDecoration(new DividerItemDecoration(getContext(), ((LinearLayoutManager) layoutManager).getOrientation()));
        rv_featureList.setHasFixedSize(false);

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(rv_featureList.getContext(), layoutManager.getOrientation());
        rv_featureList.addItemDecoration(mDividerItemDecoration);

        if(flag==1) {
            adapter = new YouthTubeVideoAdapter(1,youthList, getActivity(), rv_featureList, layoutManager, videoDetailListActivity);
        }else {
            adapter = new YouthTubeVideoAdapter(2,momentsvideo, getActivity(), rv_featureList, layoutManager, videoDetailListActivity);
        }
        rv_featureList.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        rv_featureList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);


                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    //get the recyclerview position which is completely visible and first
                    final int positionView = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                    Log.i("VISISBLE", positionView + "");
                    if (positionView >= 0) {
                        if (oldFocusedLayout != null) {
                            //Stop the previous video playback after new scroll
                            FrameLayout framTrans = (FrameLayout) oldFocusedLayout.findViewById(R.id.framTrans);
                            framTrans.setVisibility(View.VISIBLE);
                            VideoView jz_video = (VideoView) oldFocusedLayout.findViewById(R.id.videoPlayer);
                            jz_video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    mp.pause();
                                }
                            });

                        }

                        moveTo = positionView;
                        currentFocusedLayout = ((LinearLayoutManager) recyclerView.getLayoutManager()).findViewByPosition(positionView);
                        FrameLayout framTrans = (FrameLayout) currentFocusedLayout.findViewById(R.id.framTrans);
                       framTrans.setVisibility(View.GONE);
                        VideoView jz_video = (VideoView) currentFocusedLayout.findViewById(R.id.videoPlayer);

                        try {
                            jz_video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mediaPlayer) {

                                    mediaPlayer.start();

                                }
                            });

                            jz_video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mediaPlayer) {
                                    if (youthList.size() != 1) {
                                        //recyclerView.scrollToPosition(moveTo + 1);
                                        rv_featureList.smoothScrollToPosition(moveTo + 1);
                                    }
                                }
                            });
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        oldFocusedLayout = currentFocusedLayout;

                    }
                } else {
                    //JZVideoPlayer.backPress();
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        //callapiforgetPost();

    }

    private void callapiforgetPost() {
        String token = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TOKEN);
        Apimethods methods = API_Call_Retrofit.getretrofit(getActivity()).create(Apimethods.class);
        Call<GetPostResponseModel> call = methods.getRelatedVideoPost(select_caregory_id, pagenumber, 20,userid, token);
        Log.d("url", "url=" + call.request().url().toString());
        call.enqueue(new Callback<GetPostResponseModel>() {
            @Override
            public void onResponse(Call<GetPostResponseModel> call, Response<GetPostResponseModel> response) {
                int statuscode = response.code();
                apiCallBlocler = true;
                if (statuscode == 200) {
                    pagenumber++;
                    GetPostResponseModel getresponsemodel = response.body();
                    //youthList = (ArrayList<GetPostResponseModel.PostDetails>) getresponsemodel.getData();
                    youthList.addAll(getresponsemodel.getData());

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    }, 10);

                }
                if (statuscode == 401) {
                    Toast.makeText(getActivity(), "no api related video", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetPostResponseModel> call, Throwable t) {
                //Toast.makeText(getActivity(),"fail",Toast.LENGTH_SHORT).show();
            }
        });
    }


}
