package in.tagteen.tagteen.Adapters.knowledge;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.tagteen.tagteen.Interfaces.OnLoadMoreListener;
import in.tagteen.tagteen.KnowledgeQuestionPreviewActivity;
import in.tagteen.tagteen.Model.knowledge.KnowledgeQuestions;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;

public class KnowledgeQuestionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<KnowledgeQuestions.Question> questions;
    private AddAnswerListener addAnswerListener;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public KnowledgeQuestionsAdapter(
            Context context, List<KnowledgeQuestions.Question> questions) {
        this.context = context;
        this.questions = questions;
    }

    public void setAddAnswerListener(AddAnswerListener addAnswerListener) {
        this.addAnswerListener = addAnswerListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.knowledge_question_row, parent, false);
            return new ItemRowHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.load_more, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return this.questions.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
            return;
        }

        ItemRowHolder itemRowHolder = (ItemRowHolder) holder;
        KnowledgeQuestions.Question question = this.questions.get(position);
        if (question == null) {
            return;
        }

        String questionTitle = question.getTitle();
        if (questionTitle != null && questionTitle.endsWith("?") == false) {
            questionTitle += "?";
        }
        itemRowHolder.lblTitle.setText(Utils.getHtmlEscapedString(questionTitle));

        if (question.getAnswers() == null || question.getAnswers().isEmpty()) {
            itemRowHolder.lblNoAnswers.setVisibility(View.VISIBLE);
            itemRowHolder.recyclerAnswers.setVisibility(View.GONE);
        } else {
            itemRowHolder.lblNoAnswers.setVisibility(View.GONE);
            itemRowHolder.recyclerAnswers.setVisibility(View.VISIBLE);

            KnowledgeAnswersAdapter adapter =
                    new KnowledgeAnswersAdapter(
                            this.context, question.getAnswers(), question);
            itemRowHolder.recyclerAnswers.setAdapter(adapter);
        }

        // actions
        itemRowHolder.lblTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previewQuestion(question);
            }
        });
        itemRowHolder.imgVideoIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previewQuestion(question);
            }
        });
        itemRowHolder.lblAddAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addAnswerListener != null) {
                    addAnswerListener.addAnswerToQuestion(question);
                }
            }
        });
    }

    private void previewQuestion(KnowledgeQuestions.Question question) {
        Intent intent = new Intent(this.context, KnowledgeQuestionPreviewActivity.class);
        intent.putExtra(Constants.SHOWROOM_POST_DATA, question);
        this.context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return this.questions.size();
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder {
        private TextView lblTitle;
        private ImageView imgVideoIcon;
        private TextView lblNoAnswers;
        private RecyclerView recyclerAnswers;
        private TextView lblAddAnswer;

        public ItemRowHolder(View view) {
            super(view);

            this.lblTitle = view.findViewById(R.id.lblQuestionTitle);
            this.imgVideoIcon = view.findViewById(R.id.imgVideoIcon);
            this.lblNoAnswers = view.findViewById(R.id.lblNoAnswers);
            this.recyclerAnswers = view.findViewById(R.id.recyclerAnswers);
            this.lblAddAnswer = view.findViewById(R.id.lblAddAnswer);

            this.recyclerAnswers.setHasFixedSize(true);
            this.recyclerAnswers.setLayoutManager(
                    new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View view) {
            super(view);
            this.progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        }
    }

    public interface AddAnswerListener {
        void addAnswerToQuestion(KnowledgeQuestions.Question question);
    }
}
