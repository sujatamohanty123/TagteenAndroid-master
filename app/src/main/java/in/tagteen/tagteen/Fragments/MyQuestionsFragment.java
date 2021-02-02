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

import in.tagteen.tagteen.Adapters.knowledge.MyQuestionsAdapter;
import in.tagteen.tagteen.Interfaces.OnCallbackListener;
import in.tagteen.tagteen.Interfaces.OnConfirmDialogListener;
import in.tagteen.tagteen.Interfaces.OnPostDeleteListener;
import in.tagteen.tagteen.Model.knowledge.KnowledgeQuestions;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyQuestionsFragment extends Fragment {
    private static final String TAG = "MyQuestionsFragment";

    private RecyclerView recyclerMyQuestions;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout layoutProgress;
    private TextView lblNoQuestions;

    private String userId;
    private List<KnowledgeQuestions.Question> questionsList;
    private MyQuestionsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_questions, container, false);

        this.initWidgets(view);
        this.bindEvents();

        this.loadData();

        return view;
    }

    private void initWidgets(View view) {
        this.userId = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);

        this.recyclerMyQuestions = view.findViewById(R.id.recyclerMyQuestions);
        this.swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        this.layoutProgress = view.findViewById(R.id.layoutProgress);
        this.lblNoQuestions = view.findViewById(R.id.lblNoQuestions);

        this.recyclerMyQuestions.setLayoutManager(
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
                loadQuestions();
            }
        }, 500);
    }

    private void pullToRefresh() {
        this.swipeRefreshLayout.setRefreshing(true);
        this.loadQuestions();
    }

    private void loadQuestions() {
        if (this.questionsList == null) {
            this.questionsList = new ArrayList<>();
        }
        Apimethods apiMethods = API_Call_Retrofit.getretrofit(getActivity()).create(Apimethods.class);
        Call<KnowledgeQuestions> call = apiMethods.getMyQuestions(this.userId, 0);

        API_Call_Retrofit.methodCalled(call.request().url().toString());
        call.enqueue(new Callback<KnowledgeQuestions>() {
            @Override
            public void onResponse(Call<KnowledgeQuestions> call, Response<KnowledgeQuestions> response) {
                swipeRefreshLayout.setRefreshing(false);

                if (response.code() != 200) {
                    return;
                }

                layoutProgress.setVisibility(View.GONE);
                List<KnowledgeQuestions.Question> questions = response.body().getQuestions();
                if (questions != null && !questions.isEmpty()) {
                    questionsList.addAll(questions);
                }

                if (adapter == null) {
                    adapter = new MyQuestionsAdapter(getActivity(), questionsList);
                    adapter.setOnPostDeleteListener(new OnPostDeleteListener() {
                        @Override
                        public void onDelete(String postId) {
                            deleteQuestion(postId);
                        }
                    });
                    recyclerMyQuestions.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }

                if (!questionsList.isEmpty()) {
                    recyclerMyQuestions.setVisibility(View.VISIBLE);
                    lblNoQuestions.setVisibility(View.GONE);
                } else {
                    lblNoQuestions.setVisibility(View.VISIBLE);
                    recyclerMyQuestions.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<KnowledgeQuestions> call, Throwable t) {
                layoutProgress.setVisibility(View.GONE);
            }
        });
    }

    private void deleteQuestion(final String questionId) {
        String msg = "Are you sure want to delete?";
        Utils.showConfirmationDialog(getActivity(), msg, null, new OnConfirmDialogListener() {
            @Override
            public void onConfirmation() {
                Utils.deleteQuestion(getActivity(), questionId, new OnCallbackListener() {
                    @Override
                    public void OnComplete() {
                        updateDeletedItem(questionId);
                    }
                });
            }

            @Override
            public void onCancelled() {

            }
        });
    }

    private void updateDeletedItem(String questionId) {
        if (getActivity() == null || questionId == null ||
                this.questionsList == null || this.adapter == null) {
            return;
        }

        for (KnowledgeQuestions.Question question : this.questionsList) {
            if (question.getQuestionId().equals(questionId)) {
                this.questionsList.remove(question);
                break;
            }
        }

        this.adapter.notifyDataSetChanged();
    }
}
