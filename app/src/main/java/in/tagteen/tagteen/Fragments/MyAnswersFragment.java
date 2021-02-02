package in.tagteen.tagteen.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import in.tagteen.tagteen.Adapters.knowledge.MyAnswersAdapter;
import in.tagteen.tagteen.Interfaces.OnCallbackListener;
import in.tagteen.tagteen.Interfaces.OnConfirmDialogListener;
import in.tagteen.tagteen.Interfaces.OnPostDeleteListener;
import in.tagteen.tagteen.Model.knowledge.KnowledgeAnswer;
import in.tagteen.tagteen.Model.knowledge.KnowledgeAnswers;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyAnswersFragment extends Fragment {
    private static final String TAG = "MyAnswersFragment";

    private RecyclerView recyclerMyAnswers;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout layoutProgress;
    private TextView lblNoAnswers;

    private String userId;
    private List<KnowledgeAnswer> answersList;
    private MyAnswersAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_answers, container, false);

        this.initWidgets(view);
        this.bindEvents();

        this.loadData();

        return view;
    }

    private void initWidgets(View view) {
        this.userId = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);

        this.recyclerMyAnswers = view.findViewById(R.id.recyclerMyAnswers);
        this.swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        this.layoutProgress = view.findViewById(R.id.layoutProgress);
        this.lblNoAnswers = view.findViewById(R.id.lblNoAnswers);

        this.recyclerMyAnswers.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
    }

    private void bindEvents() {
        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullToRefresh();
            }
        });
    }

    private void loadData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadAnswers();
            }
        }, 500);
    }

    private void pullToRefresh() {
        this.swipeRefreshLayout.setRefreshing(true);
        this.loadAnswers();
    }

    private void loadAnswers() {
        if (this.answersList == null) {
            this.answersList = new ArrayList<>();
        }
        Apimethods apiMethods = API_Call_Retrofit.getretrofit(getActivity()).create(Apimethods.class);
        Call<KnowledgeAnswers> call = apiMethods.getMyAnswers(this.userId, 0);

        API_Call_Retrofit.methodCalled(call.request().url().toString());
        call.enqueue(new Callback<KnowledgeAnswers>() {
            @Override
            public void onResponse(Call<KnowledgeAnswers> call, Response<KnowledgeAnswers> response) {
                swipeRefreshLayout.setRefreshing(false);

                if (response.code() != 200) {
                    return;
                }

                layoutProgress.setVisibility(View.GONE);
                List<KnowledgeAnswer> answers = response.body().getAnswers();
                if (answers != null && !answers.isEmpty()) {
                    answersList.addAll(answers);
                }

                if (adapter == null) {
                    adapter = new MyAnswersAdapter(getActivity(), answersList);
                    adapter.setOnPostDeleteListener(new OnPostDeleteListener() {
                        @Override
                        public void onDelete(String postId) {
                            deleteAnswer(postId);
                        }
                    });
                    recyclerMyAnswers.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }

                if (!answersList.isEmpty()) {
                    recyclerMyAnswers.setVisibility(View.VISIBLE);
                    lblNoAnswers.setVisibility(View.GONE);
                } else {
                    lblNoAnswers.setVisibility(View.VISIBLE);
                    recyclerMyAnswers.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<KnowledgeAnswers> call, Throwable t) {
                layoutProgress.setVisibility(View.GONE);
            }
        });
    }

    private void deleteAnswer(final String answerId) {
        String msg = "Are you sure want to delete?";
        Utils.showConfirmationDialog(getActivity(), msg, null, new OnConfirmDialogListener() {
            @Override
            public void onConfirmation() {
                Utils.deleteAnswer(getActivity(), answerId, new OnCallbackListener() {
                    @Override
                    public void OnComplete() {
                        updateDeletedItem(answerId);
                    }
                });
            }

            @Override
            public void onCancelled() {

            }
        });
    }

    private void updateDeletedItem(String answerId) {
        if (getActivity() == null || answerId == null ||
                this.answersList == null || this.adapter == null) {
            return;
        }

        for (KnowledgeAnswer answer : this.answersList) {
            if (answer.getAnswerId().equals(answerId)) {
                this.answersList.remove(answer);
                break;
            }
        }

        this.adapter.notifyDataSetChanged();
    }
}
