package in.tagteen.tagteen;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ogaclejapan.arclayout.ArcLayout;

import java.util.ArrayList;
import java.util.List;

import in.tagteen.tagteen.Adapters.MyCustomPagerAdapter2;
import in.tagteen.tagteen.Model.InsertCoolModel;
import in.tagteen.tagteen.Model.LikeJsonInputModel;
import in.tagteen.tagteen.Model.SectionDataModel;
import in.tagteen.tagteen.apimodule_retrofit.CommonApicallModule;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.utils.AnimatorUtils;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;

public class TeenfeedPreviewFragment extends Fragment {
    private ViewPager viewPager;
    private MyCustomPagerAdapter2 myCustomPagerAdapter;

    private TextView lblReactsCount, lblCommentsCount, lblReactOnPost, lblCommentOnPost;
    private SectionDataModel dataModel;
    private RelativeLayout layoutAction;
    private Bitmap bitmap;

    private MediaPlayer soundLikeFeed = null;
    private MediaPlayer soundCommentFeed = null;

    private boolean isTaggedUser = SharedPreferenceSingleton.getInstance().getBoolPreference(RegistrationConstants.IS_TAGGED_USER);
    private final String loggedInUserId = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);

    private boolean isUserLikedPost;
    private int reactsCount;

    private static final int FLAG_COMMENT = 0;
    private static final int FLAG_REACTS = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.teenfeedpreview, container, false);

        this.initWidgets(view);
        this.bindEvents();

        return view;
    }

    private void initWidgets(View view) {
        this.soundLikeFeed = MediaPlayer.create(getActivity(), R.raw.like_teenfeed);
        this.soundCommentFeed = MediaPlayer.create(getActivity(), R.raw.comment);

        this.viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        this.layoutAction = (RelativeLayout) view.findViewById(R.id.layoutAction);
        this.lblReactsCount = (TextView) view.findViewById(R.id.lblReactsCount);
        this.lblCommentsCount = (TextView) view.findViewById(R.id.lblCommentsCount);
        this.lblReactOnPost = (TextView) view.findViewById(R.id.lblReactOnPost);
        this.lblCommentOnPost = (TextView) view.findViewById(R.id.lblCommentOnPost);

        ArrayList<String> pathlist = new ArrayList<>();
        if (getArguments().getSerializable("postlistdata") != null) {
            this.dataModel = (SectionDataModel) getArguments().getSerializable("postlistdata");
        } else {
            // get post and load
        }
        if (getArguments().getParcelable(Constants.IMAGE_BITMAP) != null) {
            this.bitmap = getArguments().getParcelable(Constants.IMAGE_BITMAP);
        }

        if (this.dataModel != null) {
            this.reactsCount = this.dataModel.getReactsCount();
            this.lblReactsCount.setText(this.reactsCount + " found helpful");
            String commentsCount = this.dataModel.getCommentcount() + " comment";
            if (this.dataModel.getCommentcount() > 1) {
                commentsCount += "s";
            }
            this.lblCommentsCount.setText(commentsCount);

            this.isUserLikedPost = this.dataModel.isUserLike();
            if (this.isUserLikedPost) {
                this.lblReactOnPost.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_light_bulb_activated, 0, 0, 0);
            } else {
                this.lblReactOnPost.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_light_bulb, 0, 0, 0);
            }

            if (this.dataModel.getPost_image_createdby_creator_url() != null) {
                for (int i = 0; i < this.dataModel.getPost_image_createdby_creator_url().size(); i++) {
                    pathlist.add(this.dataModel.getPost_image_createdby_creator_url().get(i));
                }
            }
        }
        this.myCustomPagerAdapter = new MyCustomPagerAdapter2(getActivity(), this.bitmap, pathlist, 2);
        this.viewPager.setAdapter(this.myCustomPagerAdapter);
    }

    private void bindEvents() {
        this.lblReactsCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToComments(FLAG_REACTS, 0);
            }
        });
        this.lblCommentsCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToComments(FLAG_COMMENT, 0);
            }
        });
        this.lblReactOnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isUserLikedPost) {
                    reactsCount--;
                    lblReactOnPost.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_light_bulb, 0, 0, 0);
                } else {
                    reactsCount++;
                    lblReactOnPost.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_light_bulb_activated, 0, 0, 0);
                }
                lblReactsCount.setText(reactsCount + " found helpful");
                likePost();
            }
        });
        this.lblCommentOnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToComments(FLAG_COMMENT, 1);
            }
        });
        /*this.myCustomPagerAdapter.setOnItemClickListener(new MyCustomPagerAdapter2.OnItemClickListener() {
            @Override
            public void onItemClick(View view, String imagePath, int position) {
                
            }
        });*/
    }

    private void likePost() {
        if (Utils.isGuestLogin()) {
            Utils.moveToRegistration(getActivity());
            return;
        }
        if(!this.isTaggedUser){
            Utils.showUnverifiedUserDialog(getActivity());
            return;
        }
        if (this.soundLikeFeed != null) this.soundLikeFeed.start();
        if (this.isUserLikedPost) {
            this.isUserLikedPost = false;
            InsertCoolModel json = new InsertCoolModel();
            json.setFlag(5);
            json.setPost_id(this.dataModel.getPostid());
            json.setFriend_user_id(this.loggedInUserId);
            String token = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TOKEN);
            CommonApicallModule.deleteCoolSwagDebNerd(json, token, getActivity());
        } else{
            this.isUserLikedPost = true;
            LikeJsonInputModel likeJsonInputModel = new LikeJsonInputModel();
            String token = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TOKEN);
            likeJsonInputModel.setUser_id(this.loggedInUserId);
            likeJsonInputModel.setPost_id(this.dataModel.getPostid());
            CommonApicallModule.callApiForLike(likeJsonInputModel, token, getActivity());
        }
    }

    private static void slideDown(final View view) {
        view.animate()
                .translationY(view.getHeight())
                .alpha(0.f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // superfluous restoration
                        view.setVisibility(View.GONE);
                        view.setAlpha(1.f);
                        view.setTranslationY(0.f);
                    }
                });
    }

    private static void slideUp(final View view) {
        view.setVisibility(View.VISIBLE);
        view.setAlpha(0.f);

        if (view.getHeight() > 0) {
            slideUpNow(view);
        } else {
            // wait till height is measured
            view.post(new Runnable() {
                @Override
                public void run() {
                    slideUpNow(view);
                }
            });
        }
    }

    private static void slideUpNow(final View view) {
        view.setTranslationY(view.getHeight());
        view.animate()
                .translationY(0)
                .alpha(1.f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.VISIBLE);
                        view.setAlpha(1.f);
                    }
                });
    }

    private void moveToComments(int flag, int showKeypad) {
        if (Utils.isGuestLogin()) {
            Utils.moveToRegistration(getActivity());
            return;
        }
        Intent intent = new Intent(getActivity(), CommentLikeActivity_new.class);
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.SELECT_COMMENT_LIKE, flag);
        bundle.putInt("keypadshow", showKeypad);
        bundle.putString("postid", this.dataModel.getPostid());
        bundle.putString("type", "teenfeed");
        bundle.putString("react_type", "");
        bundle.putInt(Constants.COMMENTS_COUNT, this.dataModel.getCommentcount());
        bundle.putInt(Constants.REACTS_COUNT, this.reactsCount);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void showMenu(ImageView imagview, ArcLayout arcLayout, final FrameLayout menuLayout) {
        menuLayout.setVisibility(View.VISIBLE);

        List<Animator> animList = new ArrayList<>();

        for (int i = 0, len = arcLayout.getChildCount(); i < len; i++) {
            animList.add(createShowItemAnimator(imagview, menuLayout, arcLayout.getChildAt(i)));
        }

        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(400);
        animSet.setInterpolator(new OvershootInterpolator());
        animSet.playTogether(animList);
        animSet.start();
    }

    private void hideMenu(ImageView imagview, ArcLayout arcLayout, final FrameLayout menuLayout) {
        List<Animator> animList = new ArrayList<>();

        for (int i = arcLayout.getChildCount() - 1; i >= 0; i--) {
            animList.add(createHideItemAnimator(imagview, menuLayout, arcLayout.getChildAt(i)));
        }

        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(400);
        animSet.setInterpolator(new AnticipateInterpolator());
        animSet.playTogether(animList);
        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                menuLayout.setVisibility(View.INVISIBLE);
            }
        });
        animSet.start();
    }

    private Animator createShowItemAnimator(final ImageView fab, FrameLayout frameLayout, View item) {
        float dx = fab.getX() - item.getX();
        float dy = fab.getY() - item.getY();

        dy = dy + 320;
        dx = dx + 280;
        item.setRotation(0f);
        item.setTranslationX(dx);
        item.setTranslationY(dy);

        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.rotation(0f, 720f),
                AnimatorUtils.translationX(dx, 0f),
                AnimatorUtils.translationY(dy, 0f)
        );

        return anim;
    }

    private Animator createHideItemAnimator(final ImageView fab, FrameLayout frameLayout, final View item) {
        float dx = fab.getX() - item.getX();
        float dy = fab.getY() - item.getY();

        dy = dy + 320;
        dx = dx + 280;
        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.rotation(720f, 0f),
                AnimatorUtils.translationX(0f, dx),
                AnimatorUtils.translationY(0f, dy)
        );

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                item.setTranslationX(0f);
                item.setTranslationY(0f);
            }
        });

        return anim;
    }
}
