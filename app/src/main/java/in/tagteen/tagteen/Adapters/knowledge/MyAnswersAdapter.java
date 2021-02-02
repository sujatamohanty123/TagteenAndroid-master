package in.tagteen.tagteen.Adapters.knowledge;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.tagteen.tagteen.Interfaces.OnPostDeleteListener;
import in.tagteen.tagteen.KnowledgeAnswerPreviewActivity;
import in.tagteen.tagteen.Model.knowledge.KnowledgeAnswer;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;

public class MyAnswersAdapter extends RecyclerView.Adapter<MyAnswersAdapter.ItemRowHolder> {
    private Context context;
    private List<KnowledgeAnswer> answers;
    private String userProfilePic;
    private String userName;
    private String userTag;

    private OnPostDeleteListener onPostDeleteListener;

    public MyAnswersAdapter(Context context, List<KnowledgeAnswer> answers) {
        this.context = context;
        this.answers = answers;

        SharedPreferenceSingleton pref = SharedPreferenceSingleton.getInstance();
        this.userProfilePic = pref.getStringPreference(RegistrationConstants.PROFILE_URL);
        this.userName = pref.getStringPreference(RegistrationConstants.USER_NAME);
        this.userTag = pref.getStringPreference(RegistrationConstants.TAGGED_NUMBER);
    }

    public void setOnPostDeleteListener(OnPostDeleteListener onPostDeleteListener) {
        this.onPostDeleteListener = onPostDeleteListener;
    }

    @NonNull
    @Override
    public ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_answer_row, parent, false);
        return new ItemRowHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemRowHolder holder, int position) {
        final KnowledgeAnswer answer = this.answers.get(position);
        if (answer == null) {
            return;
        }

        String questionTitle = answer.getQuestionTitle();
        if (questionTitle != null) {
            questionTitle += "?";
        }
        holder.lblQuestionTitle.setText(Utils.getHtmlEscapedString(questionTitle));

        holder.lblViews.setText("Views : " + answer.getViewCount());

        if (answer.getVideoThumbnail() != null &&
                answer.getVideoThumbnail().trim().length() > 0 &&
                !answer.getVideoThumbnail().equals(Constants.DUMMY_THUMBNAIL_URL)) {
            Utils.loadImageUsingGlide(this.context, holder.imgAnswerThumbnail, answer.getVideoThumbnail());
        } else {
            Utils.loadImageUsingGlide(this.context, holder.imgAnswerThumbnail, answer.getVideoUrl());
        }

        // actions
        holder.lblQuestionTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //previewAnswer(answer);
            }
        });
        holder.imgAnswerThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previewAnswer(answer);
            }
        });
        holder.layoutDeleteAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPostDeleteListener != null) {
                    onPostDeleteListener.onDelete(answer.getAnswerId());
                }
            }
        });
    }

    private void previewAnswer(KnowledgeAnswer answer) {
        Intent intent = new Intent(this.context, KnowledgeAnswerPreviewActivity.class);
        intent.putExtra(Constants.SHOWROOM_POST_DATA, answer);
        intent.putExtra(Constants.PROFILE_PIC_URL, this.userProfilePic);
        intent.putExtra(Constants.POSTED_BY, this.userName);
        intent.putExtra(Constants.POSTED_BY_TAG_NO, this.userTag);
        this.context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return this.answers.size();
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder {
        private TextView lblQuestionTitle;
        private ImageView imgAnswerThumbnail;
        private TextView lblViews;
        private LinearLayout layoutDeleteAnswer;

        public ItemRowHolder(View view) {
            super(view);

            this.lblQuestionTitle = view.findViewById(R.id.lblQuestionTitle);
            this.imgAnswerThumbnail = view.findViewById(R.id.imgAnswerThumbnail);
            this.lblViews = view.findViewById(R.id.lblViews);
            this.layoutDeleteAnswer = view.findViewById(R.id.layoutDeleteAnswer);
        }
    }
}
