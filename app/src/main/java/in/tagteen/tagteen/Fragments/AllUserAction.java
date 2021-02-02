package in.tagteen.tagteen.Fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import in.tagteen.tagteen.Adapters.CommentAdapter;
import in.tagteen.tagteen.Interfaces.PagerListener;
import in.tagteen.tagteen.Model.GetAllLReact;
import in.tagteen.tagteen.Model.GetAllLike_Cool_forpost;
import in.tagteen.tagteen.Model.GetAllLike_Cool_forpost1;
import in.tagteen.tagteen.Model.ReactionInputJson;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AllUserAction extends Fragment {
    private List<GetAllLike_Cool_forpost1.Cool> reactlist = new ArrayList<>();
    private List<GetAllLike_Cool_forpost.Data.Cool> likelist = new ArrayList<>();
    private List<GetAllLReact.data.d_like> allreactlist = new ArrayList<>();
    private RecyclerView recyclerView;
    private CommentAdapter mAdapter;
    private String postid="",type="";
    private int page = 1;
    private int pagelimit = 100;
    private LinearLayoutManager mLayoutManager;
    private LinearLayout insert_comment;
    private View view1;
    private ReactionInputJson reactjsoninputmodel=new ReactionInputJson();
    private int mPage;

    private PagerListener pagerListener;
    private LinearLayout layoutProgress;

    private static final String TAG = "AllUserAction";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt("ARG_PAGE");
        postid=getArguments().getString("postid");
        type=getArguments().getString("type");
    }

    public Fragment newInstance(int i, String postid, String type) {
        Bundle args = new Bundle();
        args.putInt("ARG_PAGE", i);
        args.putString("postid", postid);
        args.putString("type", type);
        AllUserAction fragment = new AllUserAction();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_one, container, false);
        insert_comment = (LinearLayout) view.findViewById(R.id.insert_comment);
        view1= (View) view.findViewById(R.id.view1);
        insert_comment.setVisibility(View.GONE);
        view1.setVisibility(View.GONE);

        recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addOnScrollListener(recyclerViewOnScrollListener);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), mLayoutManager.getOrientation());
        recyclerView.addItemDecoration(mDividerItemDecoration);
        this.layoutProgress = (LinearLayout) view.findViewById(R.id.layoutProgress);
        if(mPage==0){
            allreactlist.clear();
            callApiAllReact();
        }else if(mPage==1){
            callApiAllLike();
        }else {
            callApiAllCool();
        }
        return view;
    }

    private void callApiAllReact() {
        String token = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TOKEN);
        Apimethods methods = API_Call_Retrofit.getretrofit(getActivity()).create(Apimethods.class);
        Call<GetAllLReact> call = methods.getAllReact(postid,page,pagelimit, token);

        call.enqueue(new Callback<GetAllLReact>() {
            @Override
            public void onResponse(Call<GetAllLReact> call, Response<GetAllLReact> response) {
                int statuscode = response.code();
                if (response.code() == 200) {
                    layoutProgress.setVisibility(View.GONE);
                    GetAllLReact list = response.body();
                    if (page == 1) {
                        for (int i=0;i<list.getData().getD_like().size();i++){
                            list.getData().getD_like().get(i).setFlag("like");
                            allreactlist.add(list.getData().getD_like().get(i));
                        }
                        for (int i=0;i<list.getData().getD_cool().size();i++){
                            list.getData().getD_cool().get(i).setFlag("cool");
                            allreactlist.add(list.getData().getD_cool().get(i));
                        }
                        for (int i=0;i<list.getData().getD_dab().size();i++){
                            list.getData().getD_dab().get(i).setFlag("dab");
                            allreactlist.add(list.getData().getD_dab().get(i));
                        }
                        for (int i=0;i<list.getData().getD_nerd().size();i++){
                            list.getData().getD_nerd().get(i).setFlag("nerd");
                            allreactlist.add(list.getData().getD_nerd().get(i));
                        }
                        for (int i=0;i<list.getData().getD_swag().size();i++){
                            list.getData().getD_swag().get(i).setFlag("swag");
                            allreactlist.add(list.getData().getD_swag().get(i));
                        }
                        mAdapter = new CommentAdapter(getContext(), allreactlist, "all_react",1);
                        recyclerView.setAdapter(mAdapter);
                    }
                    mAdapter.notifyDataSetChanged();

                } else if (response.code() == 401) {
                }
            }

            @Override
            public void onFailure(Call<GetAllLReact> call, Throwable t) {
                Log.d(TAG,"Failed : " + call.request().url().toString() + " post " + postid);
                t.printStackTrace();
            }
        });
    }

    public void setPagerListener(PagerListener pagerListener) {
        this.pagerListener = pagerListener;
    }

    private void callApiAllCool() {
        String token = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TOKEN);
        Apimethods methods = API_Call_Retrofit.getretrofit(getActivity()).create(Apimethods.class);
        Call<GetAllLike_Cool_forpost1> call =null;
        reactjsoninputmodel.setPost_id(postid);
        reactjsoninputmodel.setPage(page);
        reactjsoninputmodel.setLimit(pagelimit);
        /*if(mPage==0) {
            call = methods.getAllCool(reactjsoninputmodel, token);
        }*/
        if(mPage==2) {
            call = methods.getAllCool(reactjsoninputmodel, token);
        }
        if(mPage==5){
            call = methods.getAllSweg(reactjsoninputmodel, token);
        }
        if(mPage==4){
            call = methods.getAllNerd(reactjsoninputmodel, token);
        }
        if(mPage==3){
            call = methods.getAllDab(reactjsoninputmodel, token);
        }
        call.enqueue(new Callback<GetAllLike_Cool_forpost1>() {
            @Override
            public void onResponse(Call<GetAllLike_Cool_forpost1> call, Response<GetAllLike_Cool_forpost1> response) {
                int statuscode = response.code();
                if (response.code() == 200) {
                    layoutProgress.setVisibility(View.GONE);
                    GetAllLike_Cool_forpost1 list = response.body();
                        reactlist=list.getData();
                    if (page == 1) {
                       /* if(mPage==0) {
                            mAdapter = new CommentAdapter(getContext(), reactlist, "cool","");
                        }*/
                        if(mPage==2) {
                            mAdapter = new CommentAdapter(getContext(), reactlist, "cool","");
                        }
                        if(mPage==5) {
                            mAdapter = new CommentAdapter(getContext(), reactlist, "swag","");
                        }
                        if(mPage==4) {
                            mAdapter = new CommentAdapter(getContext(), reactlist, "nerd","");
                        }
                        if(mPage==3) {
                            mAdapter = new CommentAdapter(getContext(), reactlist, "dab","");
                        }
                        recyclerView.setAdapter(mAdapter);
                    }
                    mAdapter.notifyDataSetChanged();

                } else if (response.code() == 401) {

                }
            }

            @Override
            public void onFailure(Call<GetAllLike_Cool_forpost1> call, Throwable t) {
                Log.d(TAG, "Failed : " + call.request().url().toString());
            }
        });
    }
    void callApiAllLike() {
        String token = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TOKEN);
        Apimethods methods = API_Call_Retrofit.getretrofit(getActivity()).create(Apimethods.class);
        Call<GetAllLike_Cool_forpost> call = methods.getAllLike(postid,page,pagelimit, token);
        call.enqueue(new Callback<GetAllLike_Cool_forpost>() {
            @Override
            public void onResponse(Call<GetAllLike_Cool_forpost> call, Response<GetAllLike_Cool_forpost> response) {
                int statuscode = response.code();
                if (response.code() == 200) {
                    layoutProgress.setVisibility(View.GONE);
                    GetAllLike_Cool_forpost list = response.body();
                    if (list != null && list.getData() != null) {
                        likelist = list.getData().getLike();
                    }
                    if (pagerListener != null && likelist != null) {
                        pagerListener.setCount(likelist.size());
                    }

                    if (likelist != null && likelist.isEmpty() == false) {
                        Collections.reverse(likelist);
                    }
                    if (page == 1) {
                        mAdapter = new CommentAdapter(getContext(), likelist, "heart",1,type);
                        recyclerView.setAdapter(mAdapter);
                    } else {
                        mAdapter.notifyDataSetChanged();
                    }
                } else if (response.code() == 401) {

                }
            }

            @Override
            public void onFailure(Call<GetAllLike_Cool_forpost> call, Throwable t) {
                Log.d(TAG, "Failed : " + call.request().url().toString());
                t.getStackTrace();
            }
        });
    }

    // region Listeners
    private RecyclerView.OnScrollListener recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int visibleItemCount = mLayoutManager.getChildCount();
            int totalItemCount = mLayoutManager.getItemCount();
            int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0
                    && totalItemCount >= pagelimit) {
                page++;
                if (mPage==0){
                    callApiAllReact();
                } else if(mPage==1){
                    callApiAllLike();
                } else {
                    callApiAllCool();
                }
            }
        }
    };
}
