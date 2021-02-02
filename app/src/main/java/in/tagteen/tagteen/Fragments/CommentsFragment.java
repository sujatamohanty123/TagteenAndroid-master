package in.tagteen.tagteen.Fragments;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import in.tagteen.tagteen.Adapters.CommentAdapter;
import in.tagteen.tagteen.Model.Add_Remove_Bff;
import in.tagteen.tagteen.Model.GetAllCommentList;
import in.tagteen.tagteen.Model.InsertComment_JsonInputModel;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.base.BaseFragment;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.emoji.Emojicon;
import in.tagteen.tagteen.emoji.EmojiconEditText;
import in.tagteen.tagteen.emoji.EmojiconGridFragment;
import in.tagteen.tagteen.emoji.EmojiconsFragment;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.utils.Commons;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CommentsFragment extends BaseFragment {
    private static final String TAG = "CommentsFragment";
    private List<GetAllCommentList.Commentmodel> commentlist = new ArrayList<>();
    private RecyclerView recyclerView;
    private CommentAdapter mAdapter;
    private EmojiconEditText comment_enter;
    private ImageView send_comment;
    private String postid = "";
    private String commentType;
    private int page = 1;
    private int pagelimit = 100;
    private LinearLayoutManager mLayoutManager;

    private LinearLayout layoutProgress;
    private CommentFragmentListener commentFragmentListener;

    private ImageView optionEmoji;
    private RelativeLayout mRootView;
    private Fragment fragmentKeypad;
    private FrameLayout keypadLayout;
    private ViewPager.LayoutParams mRootViewLayoutParams;
    private RelativeLayout.LayoutParams mKeypadViewLayoutParams;
    private RelativeLayout.LayoutParams commentLayoutParams;
    private LinearLayout commentLayout;
    private int mKeyBoardHeight = -1;
    private int mTop;
    private boolean isShowingEmojiKeypad = false;
    Apimethods methods;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_one, container, false);
        initComponent();
        this.initWidgets(view);
        this.bindEvents();
        this.callApiAllComment();
        return view;
    }

    private void initComponent() {
        methods = API_Call_Retrofit.getretrofit(getContext()).create(Apimethods.class);
    }

    private void initWidgets(View view) {
        this.comment_enter = (EmojiconEditText) view.findViewById(R.id.comment_enter);
        InputMethodManager input = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        input.showSoftInput(comment_enter, InputMethodManager.SHOW_FORCED);
        comment_enter.requestFocus();
        commentlist.clear();
        Bundle bundle = getArguments();
        if (bundle != null) {
            postid = bundle.getString("postid");
            this.commentType = bundle.getString(Constants.COMMENTS_TYPE);
        }

        if (this.commentType != null && this.commentType.equals(Constants.POST_TYPE_CAMPUSLIVE)) {
            this.comment_enter.setHint("Reply");
        }

        recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        send_comment = (ImageView) view.findViewById(R.id.sendcomment);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addOnScrollListener(recyclerViewOnScrollListener);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), mLayoutManager.getOrientation());
        recyclerView.addItemDecoration(mDividerItemDecoration);

        this.layoutProgress = (LinearLayout) view.findViewById(R.id.layoutProgress);

        // emoji keypad
        this.optionEmoji = (ImageView) view.findViewById(R.id.optionEmoji);
        this.mRootView = (RelativeLayout) view.findViewById(R.id.relative);
        this.keypadLayout = (FrameLayout) view.findViewById(R.id.viewKeypad);
        this.commentLayout = (LinearLayout) view.findViewById(R.id.insert_comment);
        this.mRootViewLayoutParams = (ViewPager.LayoutParams) this.mRootView.getLayoutParams();
        this.mKeypadViewLayoutParams = (RelativeLayout.LayoutParams) this.keypadLayout.getLayoutParams();
        this.commentLayoutParams = (RelativeLayout.LayoutParams) this.commentLayout.getLayoutParams();

        this.initKeypad();
    }

    private void bindEvents() {
        this.send_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Objects.requireNonNull(comment_enter.getText()).toString().length() > 0) {
                    send_comment.setEnabled(false);
                    send_comment.setImageResource(R.drawable.ic_send_new);
                    String comment = comment_enter.getText().toString();
                    comment_enter.setText("");
                    callapi_insert_comment(comment, postid);
                    Commons.hideSoftKeyboard(getActivity().getWindow(), getActivity());
                } else {
                    Utils.showShortToast(getActivity(), "Enter comment");
                }
            }
        });
        this.comment_enter.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (comment_enter.getText().toString().length() > 0) {
                    send_comment.setImageResource(R.drawable.ic_send_selected);
                } else {
                    send_comment.setImageResource(R.drawable.ic_send_new);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                if (comment_enter.getText().toString().length() > 0) {
                    send_comment.setImageResource(R.drawable.ic_send_selected);
                } else {
                    send_comment.setImageResource(R.drawable.ic_send_new);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        this.comment_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetKeypadView();
            }
        });
        this.optionEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isShowingEmojiKeypad) {
                    resetKeypadView();
                } else {
                    showEmojiKeypadWithDelay();
                }
            }
        });
    }

    private void initKeypad() {
        this.mRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                mRootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = mRootView.getRootView().getHeight();

                // r.bottom is the position above soft keypad or device button.
                // if keypad is shown, the r.bottom is smaller than that before.
                int keypadHeight = screenHeight - r.bottom;
                mTop = r.top;

                if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                    mKeyBoardHeight = keypadHeight;
                } else {
                    // keyboard is closed
                    if (mKeyBoardHeight == -1) {
                        mKeyBoardHeight = (int) (screenHeight * 0.3f);
                    }
                }
            }
        });
    }

    private void showEmojiKeypadWithDelay() {
        Utils.hideKeyboard(getActivity(), getActivity().getCurrentFocus());
        this.optionEmoji.setEnabled(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showEmojiKeypad();
            }
        }, 300);
    }

    private void showEmojiKeypad() {
        this.optionEmoji.setEnabled(true);
        this.isShowingEmojiKeypad = true;
        this.optionEmoji.setImageResource(R.drawable.ic_emoji_grey_new_select);
        this.mRootViewLayoutParams.height = this.mRootView.getRootView().getHeight();
        this.mKeypadViewLayoutParams.height = this.mKeyBoardHeight /*+ this.mTop*/;
        this.commentLayoutParams.bottomMargin = this.mKeyBoardHeight /*+ this.mTop + 5*/;
        this.keypadLayout.setVisibility(View.VISIBLE);
        this.fragmentKeypad = new EmojiconGridFragment();
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.viewKeypad, this.fragmentKeypad);
        ft.commit();
        this.mRootView.requestLayout();
    }

    private void resetKeypadView() {
        this.isShowingEmojiKeypad = false;
        this.optionEmoji.setImageResource(R.drawable.ic_emoji_grey_new);
        this.commentLayoutParams.bottomMargin = 5;
        this.mRootViewLayoutParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;
        this.keypadLayout.setVisibility(View.GONE);
        if (this.fragmentKeypad != null) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.remove(this.fragmentKeypad);
            ft.commit();
        }
        this.mRootView.requestLayout();
        Utils.showKeypad(this.comment_enter);
    }

    public void setCommentFragmentListener(CommentFragmentListener commentFragmentListener) {
        this.commentFragmentListener = commentFragmentListener;
    }

    private void callApiAllComment() {
        String token = SharedPreferenceSingleton.getInstance().getStringPreference(getContext(), RegistrationConstants.TOKEN);
        Call<GetAllCommentList> call = methods.getComment(postid, page, pagelimit, token);

        Log.e("api", call.request().toString());

        call.enqueue(new Callback<GetAllCommentList>() {
            @Override
            public void onResponse(@NotNull Call<GetAllCommentList> call, @NotNull Response<GetAllCommentList> response) {

                if (response.code() == 200) {
                    layoutProgress.setVisibility(View.GONE);
                    GetAllCommentList getcmntlist = response.body();
                    if (getcmntlist != null) {
                        commentlist = getcmntlist.getCommentlist();
                        if (commentFragmentListener != null && commentlist != null) {
                            commentFragmentListener.setCount(commentlist.size());
                        }
//                        if (commentlist != null && !commentlist.isEmpty()) {
//                            //Collections.reverse(commentlist);
//                        }
                        mAdapter = new CommentAdapter(getContext(), commentlist, "comment");
                        mAdapter.setAdapaterListener(new CommentAdapter.AdapaterListener() {
                            @Override
                            public void onCommentDeleted() {
                            }

                            @Override
                            public void setCommentsCount(int count) {
                                if (commentFragmentListener != null) {
                                    commentFragmentListener.setCount(count);
                                }
                            }
                        });
                        recyclerView.setAdapter(mAdapter);
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<GetAllCommentList> call, @NotNull Throwable t) {
                Log.d(TAG, "Failed : " + call.request().url().toString());
                showErrorDialog(t.getLocalizedMessage());
                t.printStackTrace();
            }
        });
    }

    // region Listeners
    private RecyclerView.OnScrollListener recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NotNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int visibleItemCount = mLayoutManager.getChildCount();
            int totalItemCount = mLayoutManager.getItemCount();
            int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();
            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0
                    && totalItemCount >= pagelimit) {
                page++;
                callApiAllComment();
            }
        }
    };

    private void callapi_insert_comment(final String comment, String postid) {
        InsertComment_JsonInputModel jsonObject = new InsertComment_JsonInputModel();
        String token = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TOKEN);
        final String fname = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.FIRST_NAME);
        final String lname = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.LAST_NAME);
        final String taggedno = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TAGGED_NUMBER);
        final String profile_url = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.PROFILE_URL);
        jsonObject.setUser_id(SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID));
        jsonObject.setPost_id(postid);
        jsonObject.setContent(comment);
        Call<Add_Remove_Bff> call = methods.insertComment(jsonObject, token);

        call.enqueue(new Callback<Add_Remove_Bff>() {
            @Override
            public void onResponse(@NotNull Call<Add_Remove_Bff> call, @NotNull Response<Add_Remove_Bff> response) {
                if (response.code() == 200) {
                    Add_Remove_Bff add_remove_bff = response.body();
                    comment_enter.setText("");
                    callApiAllComment();
                }
                enableSendButton();
            }

            @Override
            public void onFailure(@NotNull Call<Add_Remove_Bff> call, @NotNull Throwable t) {
                enableSendButton();
                Log.d(TAG, "Failed : " + call.request().url().toString());
                showErrorDialog(t.getLocalizedMessage());
                t.printStackTrace();
            }
        });
    }

    private void enableSendButton() {
        this.send_comment.setEnabled(true);
        if (this.comment_enter.getText().toString().length() > 0) {
            this.send_comment.setImageResource(R.drawable.ic_send_selected);
        } else {
            this.send_comment.setImageResource(R.drawable.ic_send_new);
        }
    }

    public interface CommentFragmentListener {
        void setCount(int count);
    }

    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(this.comment_enter, emojicon);
    }

    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(this.comment_enter);
    }

    public boolean isEmojiKeypadOpen() {
        if (this.isShowingEmojiKeypad) {
            this.resetKeypadView();
            return true;
        }
        return false;
    }
}
