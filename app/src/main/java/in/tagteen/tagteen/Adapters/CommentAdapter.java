package in.tagteen.tagteen.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.tagteen.tagteen.AntonyChanges;
import in.tagteen.tagteen.Model.CommentLikeDislikeInputJson;
import in.tagteen.tagteen.Model.DeleteCommentModel;
import in.tagteen.tagteen.Model.GetAllCommentList;
import in.tagteen.tagteen.Model.GetAllLReact;
import in.tagteen.tagteen.Model.GetAllLike_Cool_forpost;
import in.tagteen.tagteen.Model.GetAllLike_Cool_forpost1;
import in.tagteen.tagteen.Model.SearchModel;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.backgroundUpload.UrlUtils;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.utils.DateTimeCalculation;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Mathivanan on 23-05-2017.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {
    private List<GetAllCommentList.Commentmodel> commentlist = new ArrayList<>();
    private Context context;
    private CommentLikeDislikeInputJson likeJsonInputModel = new CommentLikeDislikeInputJson();
    private DeleteCommentModel deleteCommentModel = new DeleteCommentModel();
    private String flag = "";
    private List<GetAllLike_Cool_forpost1.Cool> reactlist = new ArrayList<>();
    private List<GetAllLike_Cool_forpost.Data.Cool> likelist = new ArrayList<>();
    private List<GetAllLReact.data.d_like> allreactlist = new ArrayList<>();
    private String s = "";
    private String type;
    private GetAllCommentList.Commentmodel comment;
    private String loggedInUserId = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);

    private AdapaterListener adapaterListener;

    public CommentAdapter(Context context, List<GetAllLike_Cool_forpost1.Cool> reactlist, String flag, String s) {
        this.context = context;
        this.reactlist = reactlist;
        this.flag = flag;
        this.s = s;
    }

    public CommentAdapter(Context context, List<GetAllLike_Cool_forpost.Data.Cool> likelist, String flag, int i, String type) {
        this.context = context;
        this.likelist = likelist;
        this.flag = flag;
        this.type = type;
    }

    public CommentAdapter(Context context, List<GetAllLReact.data.d_like> allreactlist, String flag, int i) {
        this.context = context;
        this.allreactlist = allreactlist;
        this.flag = flag;
    }

    public interface AdapaterListener {
        void onCommentDeleted();

        void setCommentsCount(int count);
    }

    public void setAdapaterListener(AdapaterListener adapaterListener) {
        this.adapaterListener = adapaterListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, tag, posttime, content;
        public ImageView img, like_dislike, deleteComment;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            tag = (TextView) view.findViewById(R.id.tag);
            content = (TextView) view.findViewById(R.id.text_desc);
            img = (ImageView) view.findViewById(R.id.img);
            like_dislike = (ImageView) view.findViewById(R.id.like_heart);
            posttime = (TextView) view.findViewById(R.id.time);
            this.deleteComment = (ImageView) view.findViewById(R.id.deleteComment);
        }
    }

    public CommentAdapter(Context context, List<GetAllCommentList.Commentmodel> commentlist, String flag) {
        this.context = context;
        this.commentlist = commentlist;
        this.flag = flag;
    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_layout_row, parent, false);
        itemView.setFocusable(true);
        itemView.setClickable(true);
        return new MyViewHolder(itemView);
    }

    @Override
    @AntonyChanges
    public void onBindViewHolder(@NotNull MyViewHolder holder, int position) {
        String time = "";
        final String userId;
        if (flag.equalsIgnoreCase("comment")) {
            comment = commentlist.get(position);
            userId = comment.getUser_id();
            holder.name.setText(comment.getFirst_name() + " " + comment.getLast_name());
            holder.tag.setText(comment.getTagged_number());
            Utils.loadProfilePic(context, holder.img, comment.getProfile_url());
            time = comment.getDate_created();

            holder.like_dislike.setVisibility(View.GONE);
            if (comment.getUser_id().equalsIgnoreCase(loggedInUserId)) {
                holder.deleteComment.setVisibility(View.VISIBLE);
            } else {
                holder.deleteComment.setVisibility(View.GONE);
            }
            final int index = holder.getAdapterPosition();
            holder.deleteComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    display_delet_comment_dialog(comment.getId(), index);
                }
            });

            holder.content.setText(comment.getContent());
            holder.posttime.setText(Utils.getRelativeTime(Long.parseLong(comment.getDate_created())));
        } else if (flag.equalsIgnoreCase("heart")) {
            final GetAllLike_Cool_forpost.Data.Cool like = likelist.get(position);
            if (like != null && like.getUserId() != null) {
                userId = like.getUserId().getId();
                holder.name.setText(like.getUserId().getFirstName() + " " + like.getUserId().getLastName());
                holder.tag.setText(like.getUserId().getTaggedNumber());
                Utils.loadProfilePic(context, holder.img, like.getUserId().getProfileUrl());
            } else {
                userId = null;
            }
            holder.content.setVisibility(View.GONE);

            if (type.equalsIgnoreCase(Constants.POST_TYPE_SHOWROOM)) {
                holder.like_dislike.setImageResource(R.drawable.ic_bottom_youthube);
                holder.like_dislike.setColorFilter(ContextCompat.getColor(context, R.color.red_600), android.graphics.PorterDuff.Mode.SRC_IN);
            } else if (type.equalsIgnoreCase(Constants.POST_TYPE_TEENFEED)) {
                holder.like_dislike.setImageResource(R.drawable.ic_light_bulb_activated);
            } else {
                holder.like_dislike.setImageResource(R.drawable.ic_svg_heart);
            }
            holder.posttime.setText(Utils.getRelativeTime(like.getTimestamp()));
            time = String.valueOf(like.getTimestamp());
        } else if (flag.equalsIgnoreCase("all_react")) {
            final GetAllLReact.data.d_like react = allreactlist.get(position);
            userId = null;
            if (react != null)
                holder.name.setText(react.getFirstName() + " " + react.getLastName());
            holder.tag.setText(react.getTaggedNumber());
            Utils.loadProfilePic(context, holder.img, react.getProfileUrl());
            holder.content.setVisibility(View.GONE);
            time = String.valueOf(react.getTimestamp());

            holder.posttime.setVisibility(View.GONE);
            if (react.getFlag().equalsIgnoreCase("like")) {
                holder.like_dislike.setImageResource(R.drawable.ic_svg_heart);
            }
            if (react.getFlag().equalsIgnoreCase("cool")) {
                holder.like_dislike.setImageResource(R.drawable.svg_cool_emoji);
            }
            if (react.getFlag().equalsIgnoreCase("swag")) {
                holder.like_dislike.setImageResource(R.drawable.svg_swag_emoji);
            }
            if (react.getFlag().equalsIgnoreCase("nerd")) {
                holder.like_dislike.setImageResource(R.drawable.ic_nerd);
            }
            if (react.getFlag().equalsIgnoreCase("dab")) {
                holder.like_dislike.setImageResource(R.drawable.svg_dab_emoji);
            }
        } else {
            final GetAllLike_Cool_forpost1.Cool cool = reactlist.get(position);
            if (cool != null && cool.getUserId() != null) {
                userId = cool.getUserId().getId();
                holder.name.setText(cool.getUserId().getFirstName() + " " + cool.getUserId().getLastName());
            } else {
                userId = null;
            }
            holder.tag.setText(cool.getUserId().getTaggedNumber());
            Utils.loadProfilePic(context, holder.img, cool.getUserId().getProfileUrl());
            holder.content.setVisibility(View.GONE);
            holder.like_dislike.setImageResource(R.drawable.ic_svg_heart);
            holder.posttime.setText(Utils.getRelativeTime(Long.parseLong(cool.getTimestamp())));
            time = cool.getTimestamp();

            if (flag.equalsIgnoreCase("cool")) {
                holder.like_dislike.setImageResource(R.drawable.svg_cool_emoji);
            }
            if (flag.equalsIgnoreCase("swag")) {
                holder.like_dislike.setImageResource(R.drawable.svg_swag_emoji);
            }
            if (flag.equalsIgnoreCase("nerd")) {
                holder.like_dislike.setImageResource(R.drawable.ic_nerd);
            }
            if (flag.equalsIgnoreCase("dab")) {
                holder.like_dislike.setImageResource(R.drawable.svg_dab_emoji);
            }
        }

        if (userId != null) {
            holder.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.gotoProfile(context, userId);
                }
            });
            holder.name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.gotoProfile(context, userId);
                }
            });
            holder.tag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.gotoProfile(context, userId);
                }
            });
        }

        try {
            String createdtime = DateTimeCalculation.convertGivenISODateStringToLocalTimeWithGivenFormat(time, "yyyy/MM/dd HH:mm:ss");
            if (createdtime == null || createdtime.trim().length() == 0) {
                return;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = null;
            try {
                date = sdf.parse(createdtime);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (date == null) {
                return;
            }

            long millis = date.getTime();
            long diff = System.currentTimeMillis() - millis;
            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);

            String times = "";
            if (diffDays > 0) {
                times = " " + diffDays + "d";
            } else if (diffHours > 0) {
                times = " " + diffHours + "h";
            } else if (diffMinutes > 0) {
                times = " " + diffMinutes + "m";
            } else if (diffSeconds > 5) {
                times = " " + diffSeconds + "s";
            } else if (diffSeconds <= 5) {
                times = "1 s";
            }
            holder.posttime.setText("" + times);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void display_delet_comment_dialog(final String comment_id, final int position) {
        final Dialog d = new Dialog(context);
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
        msg.setText("Are you sure you want to delete this Comment ?");
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        continueorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteComment(comment_id, position, d);
            }
        });
        d.show();
    }

    private void deleteComment(String comment_id, final int position, final Dialog d) {
        Apimethods methods = API_Call_Retrofit.getretrofit(context).create(Apimethods.class);
        deleteCommentModel.setComment_id(comment_id);
        Call<SearchModel> call = methods.delete_comment(comment_id, deleteCommentModel);
        call.enqueue(new Callback<SearchModel>() {
            @Override
            public void onResponse(Call<SearchModel> call, Response<SearchModel> response) {
                if (response.code() == 200) {
                    SearchModel model = response.body();
                    if (model.getSuccess()) {
                        Utils.showShortToast(context, "Successfully Deleted");
                        if (commentlist.size() > position) {
                            commentlist.remove(position);
                        }
                        notifyDataSetChanged();
                        if (adapaterListener != null) {
                            adapaterListener.onCommentDeleted();
                            adapaterListener.setCommentsCount(commentlist.size());
                        }
                        d.dismiss();
                    }
                }
            }

            @Override
            public void onFailure(Call<SearchModel> call, Throwable t) {
                Utils.showShortToast(context, Constants.SERVER_ERROR);
            }
        });
    }

    @Override
    public int getItemCount() {
        int size = 0;
        if (flag.equalsIgnoreCase("comment")) {
            size = commentlist.size();
        } else if (flag.equalsIgnoreCase("heart")) {
            size = likelist.size();
        } else if (flag.equalsIgnoreCase("all_react")) {
            size = allreactlist.size();
        } else {
            size = reactlist.size();
        }
        return size;
    }
}

