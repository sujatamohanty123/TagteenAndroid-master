package in.tagteen.tagteen;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.SnapHelper;

import java.util.ArrayList;
import java.util.List;

import im.ene.toro.widget.Container;
import in.tagteen.tagteen.Adapters.knowledge.KnowledgeAnswerContainerAdapter;
import in.tagteen.tagteen.Model.knowledge.KnowledgeAnswer;
import in.tagteen.tagteen.Model.knowledge.KnowledgeAnswers;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.widgets.StartSnapHelper;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KnowledgeAnswerPreviewActivity extends AppCompatActivity {
    private Container playerContainer;
    private TextView lblQuestionTitle;
    private TextView lblAnswersCount;
    private ImageView imgProfileImage;
    private TextView lblUsername;
    private TextView lblTagNo;
    private TextView lblViewsCount;

    private KnowledgeAnswer selectedPostData;
    private List<KnowledgeAnswer> answersList;
    private KnowledgeAnswerContainerAdapter adapter;

    private String userId;
    private String questionId;
    private String questionTitle;
    private String profilePicUrl;
    private String postedBy;
    private String postedByTagNo;
    private int viewCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_knowledge_answer_preview);

        this.initWidgets();
        this.bindEvents();

        this.loadData();
    }

    private void initWidgets() {
        this.userId = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
        // get arguments
        Bundle bundle = getIntent().getExtras();
        if (bundle.getSerializable(Constants.SHOWROOM_POST_DATA) != null) {
            if (bundle.getSerializable(Constants.SHOWROOM_POST_DATA) instanceof KnowledgeAnswer) {
                this.selectedPostData = (KnowledgeAnswer) bundle.getSerializable(Constants.SHOWROOM_POST_DATA);
            }
        }
        this.questionId = bundle.getString(Constants.QUESTION_ID);
        this.questionTitle = bundle.getString(Constants.QUESTION_TITLE);
        this.profilePicUrl = bundle.getString(Constants.PROFILE_PIC_URL);
        this.postedBy = bundle.getString(Constants.POSTED_BY);
        this.postedByTagNo = bundle.getString(Constants.POSTED_BY_TAG_NO);
        this.viewCount = bundle.getInt(Constants.VIEW_COUNT);

        this.playerContainer = findViewById(R.id.playerContainer);
        this.playerContainer.setLayoutManager(new LinearLayoutManager(this));
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(this.playerContainer);

        this.lblQuestionTitle = findViewById(R.id.lblQuestionTitle);
        this.lblAnswersCount = findViewById(R.id.lblAnswersCount);
        this.imgProfileImage = findViewById(R.id.profileImage);
        this.lblUsername = findViewById(R.id.userName);
        this.lblTagNo = findViewById(R.id.userTag);
        this.lblViewsCount = findViewById(R.id.lblViewsCount);

        this.answersList = new ArrayList<>();
        if (this.selectedPostData != null) {
            this.answersList.add(this.selectedPostData);
        }
        this.initAdapter();
        this.bindDetails();
    }

    private void loadData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadAnswers();
            }
        }, 500);
    }

    private void bindEvents() {
        /*this.toggleFullScreenView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFullScreen();
            }
        });*/
    }

    private void bindDetails() {
        if (this.selectedPostData == null) {
            return;
        }

        String question = this.selectedPostData.getQuestionTitle();
        if (this.questionTitle != null) {
            question = this.questionTitle;
        }
        if (question != null) {
            question += "?";
        }
        this.lblQuestionTitle.setText(Utils.getHtmlEscapedString(question));
        this.lblAnswersCount.setVisibility(View.GONE);

        if (this.profilePicUrl != null && this.profilePicUrl.trim().length() > 0) {
            Utils.loadProfilePic(this, this.imgProfileImage, this.profilePicUrl);
        }
        if (this.postedBy != null) {
            this.lblUsername.setText(this.postedBy);
        }
        if (this.postedByTagNo != null) {
            this.lblTagNo.setText(this.postedByTagNo);
        }
        this.lblViewsCount.setText("" + this.viewCount);
    }

    private void initAdapter() {
        this.adapter = new KnowledgeAnswerContainerAdapter(this, this.answersList);
        this.playerContainer.setAdapter(this.adapter);
    }

    private void loadAnswers() {
        if (this.questionId == null) {
            return;
        }
        Apimethods apiMethods = API_Call_Retrofit.getretrofit(this).create(Apimethods.class);
        Call<KnowledgeAnswers> call = apiMethods.getAnswersForQuestion(this.userId, this.questionId, 0);

        call.enqueue(new Callback<KnowledgeAnswers>() {
            @Override
            public void onResponse(Call<KnowledgeAnswers> call, Response<KnowledgeAnswers> response) {
                if (response.code() != 200) {
                    return;
                }

                List<KnowledgeAnswer> answers = response.body().getAnswers();
                if (answers != null && !answers.isEmpty()) {
                    for (KnowledgeAnswer answer : answers) {
                        if (!answer.getAnswerId().equals(selectedPostData.getAnswerId())) {
                            answersList.add(answer);
                        }
                    }
                    adapter.notifyItemRangeInserted(1, answers.size());
                } else {
                    //
                }

                if (!answersList.isEmpty()) {
                    lblAnswersCount.setText("Answers: " + answersList.size());
                    lblAnswersCount.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<KnowledgeAnswers> call, Throwable t) {
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
