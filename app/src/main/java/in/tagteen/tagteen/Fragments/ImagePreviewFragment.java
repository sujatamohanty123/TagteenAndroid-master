package in.tagteen.tagteen.Fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ogaclejapan.arclayout.ArcLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import in.tagteen.tagteen.Adapters.MyCustomPagerAdapter2;
import in.tagteen.tagteen.CommentLikeActivity_new;
import in.tagteen.tagteen.Model.InsertCoolModel;
import in.tagteen.tagteen.Model.LikeJsonInputModel;
import in.tagteen.tagteen.Model.SectionDataModel;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.apimodule_retrofit.CommonApicallModule;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.utils.AnimatorUtils;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;

public class ImagePreviewFragment extends Fragment {
    private ViewPager viewPager;
    private MyCustomPagerAdapter2 myCustomPagerAdapter;
    private ArrayList<String> pathlist = new ArrayList<>();
    private RelativeLayout mainscreen;
    private View hideshowdesc;
    private boolean isUp;
    private static String output = "";
    private int view_count = 0, like_count = 0, conversation_count = 0;
    private String first_name, profile_url, tagged_number, content, date_created;
    private TextView text_post_description, Commentcount, textShareCount, headrCount, textReact;
    private LinearLayout linearComment, linReact;
    private Bundle bundle = new Bundle();
    private String postid, post_creater_id;
    private ArcLayout arc_layout;
    private ImageView coolbtn;
    private ImageButton btnarc_heart, btnarc_cool, btnarc_dab, btnarc_nerd, btnarc_swag;
    private FrameLayout framArcView;
    private SectionDataModel dataList;
    private final String logedInUserid = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
    private String Accesstoken = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TOKEN);

    private static final int FLAG_COMMENT = 0;
    private static final int FLAG_REACTS = 1;

    private Bitmap bitmap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_buzz_preview, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        hideshowdesc = (View) view.findViewById(R.id.Desc);
        mainscreen = (RelativeLayout) view.findViewById(R.id.relative);
        text_post_description = (TextView) view.findViewById(R.id.text_post_description);
        Commentcount = (TextView) view.findViewById(R.id.Commentcount);
        textShareCount = (TextView) view.findViewById(R.id.textShareCount);
        headrCount = (TextView) view.findViewById(R.id.headrCount);
        linearComment = (LinearLayout) view.findViewById(R.id.linearComment);
        linReact = (LinearLayout) view.findViewById(R.id.linReact);
        framArcView = (FrameLayout) view.findViewById(R.id.menu_layout);
        arc_layout = (ArcLayout) view.findViewById(R.id.arc_layout);
        btnarc_heart = (ImageButton) view.findViewById(R.id.btnarc_heart);
        btnarc_cool = (ImageButton) view.findViewById(R.id.btnarc_cool);
        btnarc_dab = (ImageButton) view.findViewById(R.id.btnarc_dab);
        btnarc_nerd = (ImageButton) view.findViewById(R.id.btnarc_nerd);
        btnarc_swag = (ImageButton) view.findViewById(R.id.btnarc_swag);
        textReact = (TextView) view.findViewById(R.id.textReact);
        coolbtn = (ImageView) view.findViewById(R.id.cool);
        isUp = false;
        if (hideshowdesc.isClickable()) {
            isUp = true;
        }

        dataList = (SectionDataModel) getArguments().getSerializable("postlistdata");
        if (getArguments().getParcelable(Constants.IMAGE_BITMAP) != null) {
            this.bitmap = getArguments().getParcelable(Constants.IMAGE_BITMAP);
        }

        /*ImageView share = (ImageView) view.findViewById(R.id.share);
        try {
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //new ShareDialog(getActivity(), output);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        this.getData();
        return view;
    }

    public static void slideDown(final View view) {
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

    public static void slideUp(final View view) {
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

    void getData() {
        /*if (this.dataList == null) {
            return;
        }*/
        postid = dataList.getPostid();
        post_creater_id = dataList.getPost_userid();
        view_count = dataList.getView_count();
        like_count = dataList.getLikecount();
        // conversation_count=dataList.;
        first_name = dataList.getPost_creator_name();
        profile_url = dataList.getPost_creator_profilepic();
        tagged_number = dataList.getPosted_creator_tagged_number();
        content = dataList.getText_description();
        date_created = String.valueOf(dataList.getPost_created_date_time());
        for (int i = 0; i < dataList.getPost_image_createdby_creator_url().size(); i++) {
            pathlist.add(dataList.getPost_image_createdby_creator_url().get(i));
        }
        mainscreen.setVisibility(View.VISIBLE);
        myCustomPagerAdapter = new MyCustomPagerAdapter2(Objects.requireNonNull(getContext()), this.bitmap, pathlist, 1);
        viewPager.setAdapter(myCustomPagerAdapter);
        myCustomPagerAdapter.setOnItemClickListener(new MyCustomPagerAdapter2.OnItemClickListener() {
            @Override
            public void onItemClick(View view, String imagePath, int position) {
                if (isUp) {
                    slideUp(hideshowdesc);
                } else {
                    slideDown(hideshowdesc);
                }
                isUp = !isUp;
            }
        });
        /*if (content.equalsIgnoreCase("") || content == null) {
            text_post_description.setVisibility(View.GONE);
        } else {
            text_post_description.setText(content);
        }
        headrCount.setText(like_count + " React");
        textShareCount.setText(Utils.getRelativeTime(Long.parseLong(date_created)));
        if (conversation_count == 0) {
            Commentcount.setVisibility(View.GONE);
        } else {
            Commentcount.setText(conversation_count);
        }
        Commentcount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToComments(FLAG_COMMENT);
            }
        });
        linearComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToComments(FLAG_REACTS);
            }
        });*/

        /*if (dataList.getAction_flag() == 1) {
            textReact.setText("Cool");
            coolbtn.setImageResource(R.drawable.svg_cool_emoji);
        } else if (dataList.getAction_flag() == 2) {
            textReact.setText("Swag");
            coolbtn.setImageResource(R.drawable.svg_swag_emoji);
        } else if (dataList.getAction_flag() == 4) {
            textReact.setText("Dab");
            coolbtn.setImageResource(R.drawable.svg_dab_emoji);
        } else if (dataList.getAction_flag() == 3) {
            textReact.setText("Nerd");
            coolbtn.setImageResource(R.drawable.ic_nerd);
        } else if (dataList.getAction_flag() == 5) {
            textReact.setText("Heart");
            coolbtn.setImageResource(R.drawable.ic_svg_heart);
        } else {
            textReact.setText("React");
            coolbtn.setImageResource(R.drawable.ic_svg_cool_select);
        }*/
    }

    private void moveToComments(int flag) {
        Intent intent = new Intent(getActivity(), CommentLikeActivity_new.class);
        bundle.putInt("comment_select_flag", flag);
        bundle.putInt("keypadshow", 0);
        bundle.putString("postid", postid);
        bundle.putInt(Constants.COMMENTS_COUNT, conversation_count);
        bundle.putInt(Constants.REACTS_COUNT, like_count);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void updateDataset(int action_flag) {
        dataList.setAction_flag(action_flag);
        if (action_flag == 5) {
            textReact.setText("Heart");
            linReact.setSelected(!linReact.isSelected());
            hideMenu(coolbtn, arc_layout, framArcView);
            coolbtn.setImageResource(R.drawable.ic_svg_heart);
            LikeJsonInputModel json = new LikeJsonInputModel();
            json.setPost_id(dataList.getPostid());
            json.setUser_id(logedInUserid);
            int currentLikeCount = dataList.getLikecount();
            currentLikeCount = currentLikeCount + 1;
            dataList.setLikecount(currentLikeCount);
            CommonApicallModule.callApiForLike(json, Accesstoken, getActivity());
        } else {
            if (action_flag == 1) {
                textReact.setText("Cool");
                linReact.setSelected(!linReact.isSelected());
                hideMenu(coolbtn, arc_layout, framArcView);
                coolbtn.setImageResource(R.drawable.svg_cool_emoji);
                int currentCount = dataList.getCoolcount();
                currentCount = currentCount + 1;
                dataList.setCoolcount(currentCount);
            } else if (action_flag == 2) {
                textReact.setText("Swag");
                linReact.setSelected(!linReact.isSelected());
                hideMenu(coolbtn, arc_layout, framArcView);
                coolbtn.setImageResource(R.drawable.svg_swag_emoji);
                int currentCount = dataList.getSwegcount();
                currentCount = currentCount + 1;
                dataList.setSwegcount(currentCount);
            } else if (action_flag == 3) {
                textReact.setText("Nerd");
                linReact.setSelected(!linReact.isSelected());
                hideMenu(coolbtn, arc_layout, framArcView);
                coolbtn.setImageResource(R.drawable.ic_nerd);
                int currentCount = dataList.getNerdcount();
                currentCount = currentCount + 1;
                dataList.setNerdcount(currentCount);
            } else if (action_flag == 4) {
                textReact.setText("Dab");
                linReact.setSelected(!linReact.isSelected());
                hideMenu(coolbtn, arc_layout, framArcView);
                coolbtn.setImageResource(R.drawable.svg_dab_emoji);
                int currentCount = dataList.getDabcount();
                currentCount = currentCount + 1;
                dataList.setDabcount(currentCount);
            }
            InsertCoolModel json = new InsertCoolModel();
            json.setPost_id(dataList.getPostid());
            json.setFriend_user_id(logedInUserid);
            json.setFlag(action_flag);
            CommonApicallModule.insertCoolSwagDebNerd(json, Accesstoken, getActivity());
        }
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