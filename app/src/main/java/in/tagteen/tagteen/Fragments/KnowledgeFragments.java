package in.tagteen.tagteen.Fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import in.tagteen.tagteen.Adapters.TeenFeedAdapter;
import in.tagteen.tagteen.Adapters.knowledge.KnowledgeCategoriesAdapter;
import in.tagteen.tagteen.Adapters.knowledge.KnowledgeQuestionsAdapter;
import in.tagteen.tagteen.AddKnowledgePostActivity;
import in.tagteen.tagteen.Fragments.beans.FileDataSender;
import in.tagteen.tagteen.Interfaces.OnFileUploadListener;
import in.tagteen.tagteen.Interfaces.OnLoadMoreListener;
import in.tagteen.tagteen.Model.GetPostResponseModel;
import in.tagteen.tagteen.Model.SectionDataModel;
import in.tagteen.tagteen.Model.knowledge.KnowledgeCategories;
import in.tagteen.tagteen.Model.knowledge.KnowledgeQuestions;
import in.tagteen.tagteen.MyQuesAndAnsActivity;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.base.BaseFragment;
import in.tagteen.tagteen.compression.KplCompressor;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.selfyManager.CameraActivity;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.FloatingActionButton;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.utils.DataCache;
import in.tagteen.tagteen.utils.KnowledgeQuesAndAnsUploadHelper;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KnowledgeFragments extends BaseFragment {
  private boolean mScreenLoadedOnce = false;
  private RecyclerView recyclerTeenfeedPosts;
  private RecyclerView recyclerKnowledgeCategories;
  private RecyclerView recyclerKnowledgeQuestions;
  private TextView lblNoQuestions;
  private LinearLayout layoutMyQuesAndAns;
  private RelativeLayout layoutAskQuestion;

  private int pageLimit = 10;
  private String loggedInUserId;
  private String token;

  private int teenfeedPageNumber = 1;
  private boolean teenfeedApiCallBlocker = false;
  private boolean isTeenfeedDataLoading = false;
  private ArrayList<SectionDataModel> teenfeedPosts = new ArrayList<SectionDataModel>();
  private TeenFeedAdapter teenFeedAdapter;

  private int questionsPageNumber = 1;
  private boolean questionsApiCallBlocker;
  private boolean isLoadingQuestions = false;
  private KnowledgeQuestionsAdapter questionsAdapter;
  private List<KnowledgeQuestions.Question> questionsList;
  private NestedScrollView nestedScrollView;

  private String selectedCategoryId;
  private String answeringToQuestionId;
  private String knowledgePostType;
  private String filePath;

  private ProgressBar uploadProgress;
  private ProgressBar progressPreparingVideo;
  private TextView textProgress;
  private RelativeLayout layoutProgress;
  private FloatingActionButton fabAskQuestion;

  private SwipeRefreshLayout swipeRefreshLayout;

  private Apimethods apiMethods;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_knowledge, container, false);

    this.initWidgets(view);
    this.bindEvents();
    return view;
  }

  @Override
  public void setUserVisibleHint(boolean isVisibleToUser) {
    super.setUserVisibleHint(isVisibleToUser);
    if (isVisibleToUser && !mScreenLoadedOnce) {
      loadData();
      mScreenLoadedOnce = true;
    }
    // do the rest of the code here.

  }

  private void initWidgets(View view) {
    SharedPreferenceSingleton sharedPref = SharedPreferenceSingleton.getInstance();
    this.loggedInUserId = sharedPref.getStringPreference(RegistrationConstants.USER_ID);
    this.token = sharedPref.getStringPreference(RegistrationConstants.TOKEN);

    this.selectedCategoryId = "0";

    this.apiMethods = API_Call_Retrofit.getretrofit(getContext()).create(Apimethods.class);

    this.nestedScrollView = view.findViewById(R.id.nestedScrollview);
    this.recyclerTeenfeedPosts = view.findViewById(R.id.recyclerTeenfeedPosts);
    this.recyclerTeenfeedPosts.setHasFixedSize(true);
    this.recyclerTeenfeedPosts.setLayoutManager(
        new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

    this.recyclerKnowledgeCategories = view.findViewById(R.id.recyclerKnowledgeCategories);
    this.recyclerKnowledgeCategories.setHasFixedSize(true);
    this.recyclerKnowledgeCategories.setLayoutManager(
        new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

    this.recyclerKnowledgeQuestions = view.findViewById(R.id.recyclerKnowledgeQuestions);
    this.recyclerKnowledgeQuestions.setHasFixedSize(true);
    this.recyclerKnowledgeQuestions.setLayoutManager(
        new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

    this.lblNoQuestions = view.findViewById(R.id.lblNoQuestions);
    this.layoutAskQuestion = view.findViewById(R.id.layoutAskQuestion);
    this.layoutMyQuesAndAns = view.findViewById(R.id.layoutMyQuesAndAns);

    this.uploadProgress = view.findViewById(R.id.progressBar);
    this.progressPreparingVideo = view.findViewById(R.id.progressPreparingVideo);
    this.textProgress = view.findViewById(R.id.textPer);
    this.layoutProgress = view.findViewById(R.id.layoutProgress);
    this.layoutProgress.setVisibility(View.GONE);
    this.fabAskQuestion = view.findViewById(R.id.fabAskQuestion);

    this.swipeRefreshLayout = view.findViewById(R.id.simpleSwipeRefreshLayout);
    this.swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
  }

  private void bindEvents() {
    this.layoutAskQuestion.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        knowledgePostType = Constants.KNOWLEDGE_QUESTION_TYPE;
        recordVideoToPost();
      }
    });
    this.layoutMyQuesAndAns.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        moveToMyQuestionsAndAnswers();
      }
    });
    this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        pullToRefresh();
      }
    });
    this.fabAskQuestion.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        knowledgePostType = Constants.KNOWLEDGE_QUESTION_TYPE;
        recordVideoToPost();
      }
    });

    // endless scroll for questions
    this.nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
      @Override
      public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX,
          int oldScrollY) {
        if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
          if (questionsApiCallBlocker && isLoadingQuestions == false) {
            questionsList.add(null);
            questionsAdapter.notifyItemInserted(questionsList.size() - 1);
            questionsApiCallBlocker = false;
            loadQuestions();
          }
        }
      }
    });
  }

  private void loadData() {
    //loadTeenFeedPosts();
    this.loadCategories();
    this.loadQuestions();
  }

  private void pullToRefresh() {
    this.swipeRefreshLayout.setRefreshing(true);
    this.questionsPageNumber = 1;
    this.isLoadingQuestions = true;
    this.loadQuestions();
  }

  private void loadTeenFeedPosts() {
    Call<GetPostResponseModel> call =
        this.apiMethods.get_teenfeed_articlePost(
            this.loggedInUserId, this.teenfeedPageNumber, this.pageLimit, this.token);

    API_Call_Retrofit.methodCalled(call.request().url().toString());
    call.enqueue(new Callback<GetPostResponseModel>() {
      @Override
      public void onResponse(@NotNull Call<GetPostResponseModel> call,
          @NotNull Response<GetPostResponseModel> response) {
        int statuscode = response.code();
        teenfeedApiCallBlocker = true;
        if (teenfeedPageNumber > 1 && teenfeedPosts != null && teenfeedPosts.size() > 0) {
          teenfeedPosts.remove(teenfeedPosts.size() - 1);
          teenFeedAdapter.notifyItemRemoved(teenfeedPosts.size());
        }
        if (teenfeedPageNumber == 1) {
          teenfeedPosts.clear();
        }
        //simpleSwipeRefreshLayout.setRefreshing(false);
        if (statuscode == 200) {
          GetPostResponseModel getresponsemodel = response.body();
          ArrayList<GetPostResponseModel.PostDetails> getdatalist =
              (ArrayList<GetPostResponseModel.PostDetails>) getresponsemodel.getData();
          if (getdatalist.isEmpty()) {
            teenfeedApiCallBlocker = false;
          }
          int addedPosition = teenfeedPosts.size() - 1;
          for (int i = 0; i < getdatalist.size(); i++) {
            if (!getdatalist.get(i).getFirst_name().equalsIgnoreCase("")) {
              SectionDataModel section = new SectionDataModel();
              section.setPostid(getdatalist.get(i).getId());
              section.setPost_userid(getdatalist.get(i).getPostCreatorId());
              section.setPost_creator_name(
                  getdatalist.get(i).getFirst_name() + " " + getdatalist.get(i).getLast_name());
              section.setPosted_creator_tagged_number(getdatalist.get(i).getTagged_number());
              section.setPost_creator_profilepic(getdatalist.get(i).getProfile_url());
              section.setPost_created_date_time(getdatalist.get(i).getDateCreated());
              section.setCategory(getdatalist.get(i).getCategorie_name());
              //////for imagelist////////////////////////////////
              List<String> imagepathlist = new ArrayList<String>();
              for (int j = 0; j < getdatalist.get(i).getImage().size(); j++) {
                //String desc=getdatalist.get(i).getImage().get(j).getUrl();
                // section.setPost_image_createdby_creator(getdatalist.get(i).getImage().get(j).getUrl());
                imagepathlist.add(getdatalist.get(i).getImage().get(j).getUrl());
                section.setPost_image_createdby_creator_url(imagepathlist);
              }

              section.setUserLike(getdatalist.get(i).getUserLike());
              section.setCoolcount(getdatalist.get(i).getCoolCount());
              section.setLikecount(getdatalist.get(i).getLikeCount());
              section.setCommentcount(getdatalist.get(i).getConversationCount());

              String desc = getdatalist.get(i).getContent();
              section.setText_description(getdatalist.get(i).getContent());
              section.setViewCount(getdatalist.get(i).getView_count());

              teenfeedPosts.add(section);
            }
          }

          if (teenFeedAdapter == null) {
            teenFeedAdapter =
                new TeenFeedAdapter(getContext(), teenfeedPosts, recyclerTeenfeedPosts);
            recyclerTeenfeedPosts.setAdapter(teenFeedAdapter);
            teenFeedAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
              @Override
              public void onLoadMore() {
                if (teenfeedApiCallBlocker && !isTeenfeedDataLoading) {
                  teenfeedPosts.add(null);
                  teenFeedAdapter.notifyItemInserted(teenfeedPosts.size() - 1);
                  teenfeedApiCallBlocker = false;
                  loadTeenFeedPosts();
                }
              }
            });
          } else {
            if (teenfeedPageNumber == 1) {
              teenFeedAdapter.notifyDataSetChanged();
            } else {
              if (addedPosition < 0) {
                addedPosition = 0;
              }
              if (getdatalist.size() > 0) {
                teenFeedAdapter.notifyItemRangeChanged(addedPosition, getdatalist.size());
              }
            }
            teenFeedAdapter.setLoaded();
          }
          isTeenfeedDataLoading = false;
          teenfeedPageNumber++;
        }

        if (statuscode == 401) {
          Log.d("url", "url=" + call.request().url().toString());
        }
      }

      @Override
      public void onFailure(Call<GetPostResponseModel> call, Throwable t) {

      }
    });
  }

  private void loadCategories() {
    Call<KnowledgeCategories> call = this.apiMethods.getKnowledgeCategories();

    API_Call_Retrofit.methodCalled(call.request().url().toString());
    call.enqueue(new Callback<KnowledgeCategories>() {
      @Override
      public void onResponse(Call<KnowledgeCategories> call,
          Response<KnowledgeCategories> response) {
        if (response.code() != 200) {
          return;
        }

        List<KnowledgeCategories.Category> categories = response.body().getCategories();
        if (categories != null) {
          DataCache.addKnowledgeCategories(categories);
          KnowledgeCategoriesAdapter adapter =
              new KnowledgeCategoriesAdapter(getContext(), categories);
          adapter.setOnItemClickListener(new KnowledgeCategoriesAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(String categoryId) {
              selectedCategoryId = categoryId;
              reloadQuestions();
            }
          });
          recyclerKnowledgeCategories.setAdapter(adapter);
        }
      }

      @Override
      public void onFailure(Call<KnowledgeCategories> call, Throwable t) {

      }
    });
  }

  private void loadQuestions() {
    this.isLoadingQuestions = true;
    Call<KnowledgeQuestions> call =
        this.apiMethods.getKnowledgeQuestions(this.selectedCategoryId, this.questionsPageNumber);

    API_Call_Retrofit.methodCalled(call.request().url().toString());
    call.enqueue(new Callback<KnowledgeQuestions>() {
      @Override
      public void onResponse(@NotNull Call<KnowledgeQuestions> call,
          @NotNull Response<KnowledgeQuestions> response) {
        swipeRefreshLayout.setRefreshing(false);
        questionsApiCallBlocker = true;

        if (response.code() != 200) {
          return;
        }

        if (questionsList == null) {
          questionsList = new ArrayList<>();
        }

        if (questionsPageNumber > 1 && questionsList != null && questionsList.size() > 0) {
          questionsList.remove(questionsList.size() - 1);
          questionsAdapter.notifyItemRemoved(questionsList.size());
        }
        if (questionsPageNumber == 1) {
          questionsList.clear();
        }

        int addedPosition = questionsList.size() - 1;
        List<KnowledgeQuestions.Question> questions = response.body().getQuestions();
        if (questions == null || questions.isEmpty()) {
          questionsApiCallBlocker = false;
        } else {
          questionsList.addAll(questions);
        }

        if (questionsAdapter == null) {
          questionsAdapter = new KnowledgeQuestionsAdapter(getContext(), questionsList);
          questionsAdapter.setAddAnswerListener(new KnowledgeQuestionsAdapter.AddAnswerListener() {
            @Override
            public void addAnswerToQuestion(KnowledgeQuestions.Question question) {
              answeringToQuestionId = question.getQuestionId();
              knowledgePostType = Constants.KNOWLEDGE_ANSWER_TYPE;
              recordVideoToPost();
            }
          });
          recyclerKnowledgeQuestions.setAdapter(questionsAdapter);
        } else {
          if (questionsPageNumber == 1) {
            questionsAdapter.notifyDataSetChanged();
          } else {
            if (addedPosition < 0) {
              addedPosition = 0;
            }
            if (questions.size() > 0) {
              questionsAdapter.notifyItemRangeInserted(addedPosition, questions.size());
            }
          }
        }
        isLoadingQuestions = false;
        questionsPageNumber++;

        if (questionsList.isEmpty()) {
          lblNoQuestions.setVisibility(View.VISIBLE);
          recyclerKnowledgeQuestions.setVisibility(View.GONE);
        } else {
          lblNoQuestions.setVisibility(View.GONE);
          recyclerKnowledgeQuestions.setVisibility(View.VISIBLE);
        }
      }

      @Override
      public void onFailure(Call<KnowledgeQuestions> call, Throwable t) {

      }
    });
  }

  private void reloadQuestions() {
    this.questionsPageNumber = 1;
    this.isLoadingQuestions = true;
    this.loadQuestions();
  }

  @Override
  public void onResume() {
    super.onResume();

    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        uploadKnowledgePost();
      }
    }, 200);
  }

  private void uploadKnowledgePost() {
    if (FileDataSender.POST_TO == FileDataSender.POST_TO_MOMENTS ||
        FileDataSender.POST_TO == FileDataSender.POST_TO_CAMPUSLIVE) {
      return;
    }
    if (FileDataSender.HAS_FILE_TO_UPLOAD) {
      FileDataSender.HAS_FILE_TO_UPLOAD = false;
      this.layoutProgress.setVisibility(View.VISIBLE);
      this.uploadProgress.setProgress(0);
      this.progressPreparingVideo.setVisibility(View.VISIBLE);

      long fileSizeInMB = Utils.getFileSizeInMB(FileDataSender.FILE_PATH);
      if (fileSizeInMB > Constants.MAX_SIZE_RAW_VIDEO_UPLOAD) {
        Utils.showToast(getContext(), "Preparing to upload...");
        new VideoCompressAsyncTask().execute();
      } else {
        this.progressPreparingVideo.setVisibility(View.GONE);
        this.uploadFile();
      }
    }
  }

  class VideoCompressAsyncTask extends AsyncTask<String, String, String> {

    @Override
    protected String doInBackground(String... strings) {
      String filePath;
      try {
        String srcPath = FileDataSender.FILE_PATH;
        filePath = KplCompressor.with(getContext())
            .compressVideo(srcPath, Utils.getVideoCompressionPath());
      } catch (URISyntaxException e) {
        return null;
      }
      return filePath;
    }

    @Override
    protected void onPostExecute(String resultPath) {
      if (getActivity() == null) {
        return;
      }

      progressPreparingVideo.setVisibility(View.GONE);
      textProgress.setTextColor(
          ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.colorAccent));
      if (resultPath != null) {
        FileDataSender.FILE_PATH = resultPath;
      }

      uploadFile();
    }
  }

  private void uploadFile() {
    new KnowledgeQuesAndAnsUploadHelper(
        getActivity(),
        this.layoutProgress,
        this.uploadProgress,
        this.textProgress,
        this.knowledgePostType,
        this.answeringToQuestionId,
        new OnFileUploadListener() {
          @Override
          public void OnFileUploaded() {
            FileDataSender.clear();
            reloadQuestions();
          }

          @Override
          public void onFileUploadFailed() {
            FileDataSender.clear();
          }
        });
  }

  private void recordVideoToPost() {
    Utils.hideKeyboard(getActivity(), getActivity().getCurrentFocus());
    if (Utils.isGuestLogin()) {
      Utils.moveToRegistration(getActivity());
    } else {
      Intent cameraIntent = new Intent(getActivity(), CameraActivity.class);
      cameraIntent.putExtra(Constants.DISABLE_PHOTO, true);
      cameraIntent.putExtra(Constants.DISABLE_VIDEO, false);
      cameraIntent.putExtra(Constants.DEFAULT_FRONT_CAMERA, true);
      int duration;
      if (Constants.KNOWLEDGE_ANSWER_TYPE.equals(this.knowledgePostType)) {
        duration = 60;
      } else {
        duration = 20;
      }
      cameraIntent.putExtra(Constants.VIDEO_RECORD_DURATION, duration);
      getActivity().startActivityForResult(
              cameraIntent, Constants.KNOWLEDGE_VIDEO_CAPTURE_REQUEST_CODE);
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == Constants.KNOWLEDGE_VIDEO_CAPTURE_REQUEST_CODE) {
      if (resultCode == getActivity().RESULT_OK) {
        if (data != null) {
          String imagePath = data.getStringExtra(Constants.PATH_VIDEO_RECORDED);
          if (imagePath != null) {
            this.filePath = imagePath;
            moveToAddKnowledgePost();
          }
        }
      }
    }
  }

  private void moveToAddKnowledgePost() {
    if (getActivity() == null) {
      return;
    }
    Intent intent = new Intent(getActivity(), AddKnowledgePostActivity.class);
    intent.putExtra(Constants.PATH_VIDEO_RECORDED, this.filePath);
    intent.putExtra(Constants.KNOWLEDGE_POST_TYPE, this.knowledgePostType);
    Objects.requireNonNull(getContext()).startActivity(intent);
  }

  private void moveToMyQuestionsAndAnswers() {
    if (Utils.isGuestLogin()) {
      Utils.moveToRegistration(getActivity());
    } else {
      Intent intent = new Intent(getActivity(), MyQuesAndAnsActivity.class);
      Objects.requireNonNull(getContext()).startActivity(intent);
    }
  }
}
