package in.tagteen.tagteen.Adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.ogaclejapan.arclayout.ArcLayout;

import java.util.ArrayList;
import java.util.List;

import in.tagteen.tagteen.CommentLikeActivity_new;
import in.tagteen.tagteen.Fragments.ImagePreviewFragment;
import in.tagteen.tagteen.Fragments.MomentsFeed;
import in.tagteen.tagteen.Fragments.share.ShareDialog;
import in.tagteen.tagteen.Interfaces.OnCallbackListener;
import in.tagteen.tagteen.Model.SectionDataModel;
import in.tagteen.tagteen.MomentFeedVideoPlay;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.TeenfeedPreviewFragment;
import in.tagteen.tagteen.backgroundUpload.UrlUtils;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.profile.AboutFragment;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.utils.AnimatorUtils;
import in.tagteen.tagteen.utils.FontStyles;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;

public class MomentslistAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //private final Animation myAnim, Anim2;
    private ArrayList<SectionDataModel> dataList;
    private Activity mContext;
    private Bundle bundle = new Bundle();
    private MomentsFeed momentsFeed;
    private AboutFragment aboutFragment;
    private int flag = 0;
    private final String logedInUserid =
            SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
    private String Accesstoken =
            SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TOKEN);
    private boolean flag_option = false;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private static final int FLAG_COMMENT = 0;
    private static final int FLAG_REACTS = 1;

    private int selectedIndex = -1;

    public MomentslistAdapter(Activity context, ArrayList<SectionDataModel> dataList,
                              MomentsFeed momentsFeed, int flag) {
        this.dataList = dataList;
        this.mContext = context;
        this.momentsFeed = momentsFeed;
        this.flag = flag;
    }

    public MomentslistAdapter(Activity context, ArrayList<SectionDataModel> dataList,
                              AboutFragment aboutFragment, int flag) {
        this.dataList = dataList;
        this.mContext = context;
        this.aboutFragment = aboutFragment;
        this.flag = flag;
    }

    @Override
    public int getItemViewType(int position) {
        return this.dataList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(this.mContext).inflate(R.layout.momentslist_items, null);
            return new ItemRowHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.load_more, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemRowHolder) {
            final ItemRowHolder itemRowHolder = (ItemRowHolder) holder;
            final SectionDataModel data = dataList.get(position);
            final String postUserid = data.getPost_userid();
            itemRowHolder.moreoption.setTag(true);
            itemRowHolder.layoutMain.setVisibility(View.VISIBLE);
            if (data.isShare_to()) {
                if (data.getOwner_post_type_id() == null) {
                    return;
                }
                itemRowHolder.relFeedThumb.setVisibility(View.GONE);
                itemRowHolder.relFeedThumb_share.setVisibility(View.VISIBLE);
                if (data.getOwner_post_type_id().equalsIgnoreCase("3")) {
                    itemRowHolder.itemTitle.setText(data.getPost_creator_name() + " shared a showroom video");
                } else if (data.getOwner_post_type_id().equalsIgnoreCase("2")) {
                    itemRowHolder.itemTitle.setText(
                            data.getPost_creator_name() + " shared a teenfeed article");
                } else {
                    itemRowHolder.itemTitle.setText(data.getPost_creator_name() + " shared a moments post");
                }
                itemRowHolder.owner_name.setText(data.getOwner_post_creator_name());
                itemRowHolder.owner_post_date.setText(
                        Utils.getRelativeTime(Long.parseLong(data.getOriginal_post_date())));
                itemRowHolder.tagged_number_owner_creator.setText(data.getOwner_tagged_number());
                Utils.loadProfilePic(mContext, itemRowHolder.owner_pic, data.getOwner_post_creator_profilepic());
                if (data.getPost_image_createdby_creator_url().isEmpty()
                        || data.getPost_image_createdby_creator_url() == null) {
                    if (data.getPost_video_thumb_createdby_creator().isEmpty()) {
                        itemRowHolder.owner_post.setVisibility(View.GONE);
                        itemRowHolder.imageAnim1_share.setVisibility(View.GONE);
                    } else {
                        itemRowHolder.imageAnim1_share.setVisibility(View.VISIBLE);
                        itemRowHolder.imageAnim1_share.setImageResource(
                                R.drawable.ic_play_circle_filled_black_24dp);
                        itemRowHolder.owner_post.setVisibility(View.VISIBLE);
                        itemRowHolder.owner_post.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        Glide.with(mContext)
                                .load(
                                        UrlUtils.getUpdatedImageUrl(data.getPost_video_thumb_createdby_creator().get(0),
                                                "large"))
                                .fitCenter()
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                                Target<Drawable> target, boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model,
                                                                   Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        if (resource instanceof BitmapDrawable) {
                                            itemRowHolder.imageBitmap = ((BitmapDrawable) resource).getBitmap();
                                        }
                                        return false;
                                    }
                                })
                                .into(itemRowHolder.owner_post);
                    }
                } else {
                    itemRowHolder.owner_post.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    Glide.with(mContext)
                            .load(UrlUtils.getUpdatedImageUrl(data.getPost_image_createdby_creator_url().get(0),
                                    "large"))
                            .fitCenter()
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                            Target<Drawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model,
                                                               Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    if (resource instanceof BitmapDrawable) {
                                        itemRowHolder.imageBitmap = ((BitmapDrawable) resource).getBitmap();
                                    }
                                    return false;
                                }
                            })
                            .into(itemRowHolder.owner_post);
                }

                itemRowHolder.relFeedThumb_share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (data.getOwner_post_type_id().equalsIgnoreCase("3")) {
                            Utils.moveToVideoDetails(mContext, data);
                        } else if (data.getOwner_post_type_id().equalsIgnoreCase("2")) {
                            Fragment fragment = new TeenfeedPreviewFragment();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("postlistdata", data);
                            fragment.setArguments(bundle);
                            FragmentManager fragmentManager =
                                    ((AppCompatActivity) mContext).getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.setCustomAnimations(R.anim.frag_fade_in, R.anim.frag_fade_out,
                                    R.anim.frag_fade_in, R.anim.frag_fade_out);
                            fragmentTransaction.replace(R.id.main_content, fragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        } else {
                            if (data.getPost_video_url() != null) {
                                Intent it = new Intent(mContext, MomentFeedVideoPlay.class);
                                it.putExtra("postid", data.getOwner_post_id());
                                it.putExtra("post_creater_id", data.getPost_userid());
                                mContext.startActivity(it);
                            } else {
                                previewImage(data, itemRowHolder.owner_post);
                            }
                        }
                    }
                });
            } else {
                itemRowHolder.relFeedThumb.setVisibility(View.VISIBLE);
                itemRowHolder.relFeedThumb_share.setVisibility(View.GONE);
                itemRowHolder.itemTitle.setText(data.getPost_creator_name());
                if (data.getPost_image_createdby_creator_url().isEmpty()
                        || data.getPost_image_createdby_creator_url() == null) {
                    if (data.getPost_video_thumb_createdby_creator().isEmpty()
                            || data.getPost_video_thumb_createdby_creator() == null) {
                        itemRowHolder.relFeedThumb.setVisibility(View.GONE);
                    } else {
                        itemRowHolder.postImage.setVisibility(View.VISIBLE);
                        itemRowHolder.loading_spinner.setVisibility(View.GONE);
                        Glide.with(mContext)
                                .load(
                                        UrlUtils.getUpdatedImageUrl(data.getPost_video_thumb_createdby_creator().get(0),
                                                "large"))
                                .placeholder(R.color.grey_400)
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                                Target<Drawable> target, boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model,
                                                                   Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        if (resource instanceof BitmapDrawable) {
                                            itemRowHolder.imageBitmap = ((BitmapDrawable) resource).getBitmap();
                                        }
                                        return false;
                                    }
                                })
                                .into(itemRowHolder.postImage);
                    }
                    itemRowHolder.total_image.setVisibility(View.GONE);
                } else {
                    itemRowHolder.loading_spinner.setVisibility(View.GONE);
                    try {
                        Glide.with(mContext)
                                .load(UrlUtils.getUpdatedImageUrl(data.getPost_image_createdby_creator_url().get(0),
                                        "large"))
                                .placeholder(R.color.grey_400)
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                                Target<Drawable> target, boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model,
                                                                   Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        if (resource instanceof BitmapDrawable) {
                                            itemRowHolder.imageBitmap = ((BitmapDrawable) resource).getBitmap();
                                        }
                                        return false;
                                    }
                                })
                                .into(itemRowHolder.postImage);
                        if (data.getPost_image_createdby_creator_url().size() == 1) {
                            itemRowHolder.total_image.setVisibility(View.GONE);
                        } else {
                            itemRowHolder.total_image.setText(
                                    "1/" + data.getPost_image_createdby_creator_url().size());
                            itemRowHolder.total_image.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (data.getView_to() == 1) {
                itemRowHolder.post_view_type.setImageResource(R.drawable.ic_public_icon);
            } else if (data.getView_to() == 2) {
                itemRowHolder.post_view_type.setImageResource(R.drawable.ic_friends_icon);
            } else if (data.getView_to() == 3) {
                itemRowHolder.post_view_type.setImageResource(R.drawable.ic_support_inactivated);
            } else if (data.getView_to() == 4) {
                itemRowHolder.post_view_type.setImageResource(R.drawable.ic_bff_icon);
            } else if (data.getView_to() == 5) {
                itemRowHolder.post_view_type.setImageResource(R.drawable.ic_private_icon);
            }

            itemRowHolder.taggednumber.setText(data.getPosted_creator_tagged_number());
            Utils.loadProfilePic(mContext, itemRowHolder.im, data.getPost_creator_profilepic());
            if (data.getText_description().equalsIgnoreCase("") || data.getText_description()
                    .equalsIgnoreCase(" ") || data.getText_description() == null) {
                itemRowHolder.txt_descriptn.setVisibility(View.GONE);
                itemRowHolder.owner_post_title.setVisibility(View.GONE);
            } else {
                if (data.isShare_to()) {
                    itemRowHolder.owner_post_title.setVisibility(View.VISIBLE);
                    itemRowHolder.owner_post_title.setText(data.getText_description());
                } else {
                    itemRowHolder.txt_descriptn.setVisibility(View.VISIBLE);
                    itemRowHolder.txt_descriptn.setText(data.getText_description());
                }
            }

            itemRowHolder.im.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.gotoProfile(mContext, data.getPost_userid());
                }
            });
            itemRowHolder.owner_pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.gotoProfile(mContext, data.getPost_userid());
                }
            });
            itemRowHolder.itemTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.gotoProfile(mContext, data.getPost_userid());
                }
            });
            itemRowHolder.taggednumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.gotoProfile(mContext, data.getPost_userid());
                }
            });

            if (data.getPost_image_createdby_creator_url() != null
                    && data.getPost_image_createdby_creator_url().size() > 1) {
                itemRowHolder.total_image.setText("1/" + data.getPost_image_createdby_creator_url().size());
                itemRowHolder.total_image.setVisibility(View.VISIBLE);
            } else {
                itemRowHolder.total_image.setVisibility(View.GONE);
            }

            itemRowHolder.timeset.setText(Utils.getRelativeTime(data.getPost_created_date_time()));

            if (data.getCommentcount() >= 1) {
                itemRowHolder.textCommentcount.setVisibility(View.VISIBLE);
                itemRowHolder.textCommentcount.setText("View " + data.getCommentcount() + " Comment");
            } else {
                itemRowHolder.textCommentcount.setVisibility(View.GONE);
            }

            int reactsCount = data.getLikecount();
            reactsCount += data.getCoolcount();
            reactsCount += data.getNerdcount();
            reactsCount += data.getSwegcount();
            reactsCount += data.getDabcount();
            data.setReactsCount(reactsCount);
            this.setReactCount(itemRowHolder, data);

            if (data.getAction_flag() == 1) {
                itemRowHolder.textReact.setText("Cool");
                itemRowHolder.coolbtn.setImageResource(R.drawable.svg_cool_emoji);
            } else if (data.getAction_flag() == 2) {
                itemRowHolder.textReact.setText("Swag");
                itemRowHolder.coolbtn.setImageResource(R.drawable.svg_swag_emoji);
            } else if (data.getAction_flag() == 4) {
                itemRowHolder.textReact.setText("Dab");
                itemRowHolder.coolbtn.setImageResource(R.drawable.svg_dab_emoji);
            } else if (data.getAction_flag() == 3) {
                itemRowHolder.textReact.setText("Nerd");
                itemRowHolder.coolbtn.setImageResource(R.drawable.ic_nerd);
            } else if (data.getAction_flag() == 5) {
                itemRowHolder.textReact.setText("Heart");
                itemRowHolder.coolbtn.setImageResource(R.drawable.ic_svg_heart);
            } else {
                itemRowHolder.textReact.setText("React");
                itemRowHolder.coolbtn.setImageResource(R.drawable.ic_svg_cool_select);
            }

            itemRowHolder.textShareCount.setText(Utils.getRelativeTime(data.getPost_created_date_time()));
            final int index = holder.getAdapterPosition();
            itemRowHolder.headrCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moveToComments(index, data, FLAG_REACTS, 0);
                }
            });
            itemRowHolder.textCommentcount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moveToComments(index, data, FLAG_COMMENT, 0);
                }
            });

            itemRowHolder.linearComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MediaPlayer comment_sound = MediaPlayer.create(mContext, R.raw.comment);
                    if (comment_sound != null) {
                        comment_sound.start();
                    }
                    moveToComments(index, data, FLAG_COMMENT, 1);
                }
            });

            itemRowHolder.sharebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MediaPlayer share_sound = MediaPlayer.create(mContext, R.raw.share);
                    if (share_sound != null) {
                        share_sound.start();
                    }
                    if (data.getView_to() == 1) {
                        new ShareDialog(mContext, data, new OnCallbackListener() {
                            @Override
                            public void OnComplete() {
                                if (momentsFeed != null) {
                                    momentsFeed.pullToRefresh();
                                }
                            }
                        });
                    } else {
                        if (flag == 1) {
                            momentsFeed.share_dialog_show(index);
                        }
                        if (flag == 2) {
                            aboutFragment.share_dialog_show(index);
                        }
                    }
                }
            });
            if (data.getPost_video_url() != null) {
                itemRowHolder.video_btn_layout.setVisibility(View.VISIBLE);
                itemRowHolder.imageAnim.setVisibility(View.VISIBLE);
                itemRowHolder.imageAnim.setImageResource(R.drawable.ic_latest_showroom_svg);
            } else {
                itemRowHolder.imageAnim.setVisibility(View.GONE);
                itemRowHolder.video_btn_layout.setVisibility(View.GONE);
            }

            itemRowHolder.postImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemRowHolder.total_image.getVisibility() == View.GONE
                            && data.getPost_image_createdby_creator_url().size() > 1) {
                        itemRowHolder.total_image.setVisibility(View.VISIBLE);
                    }
                    if (data.getPost_video_url() != null) {
                        Intent it = new Intent(mContext, MomentFeedVideoPlay.class);
                        it.putExtra("postid", data.getPostid());
                        it.putExtra("post_creater_id", data.getPost_userid());
                        mContext.startActivity(it);
                    } else {
                        previewImage(data, itemRowHolder.postImage);
                    }
                }
            });
            itemRowHolder.layoutMain.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    report_visibility(event, itemRowHolder);
                    return false;
                }
            });
            itemRowHolder.postImage.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    report_visibility(event, itemRowHolder);
                    return false;
                }
            });
            itemRowHolder.relFeedThumb_share.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    report_visibility(event, itemRowHolder);
                    return false;
                }
            });

            itemRowHolder.moreoption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopupMenu(itemRowHolder.moreoption, index, postUserid);
                }
            });

            itemRowHolder.linReact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MediaPlayer react = MediaPlayer.create(mContext, R.raw.react);
                    if (react != null) react.start();
                    itemRowHolder.textReact.setText("React");
                    itemRowHolder.coolbtn.setImageResource(R.drawable.ic_svg_cool_select);
                    SectionDataModel smodel = data;
                    if (smodel.getAction_flag() > 0) {
                        /**
                         * * Call All API rlated to Unlike/Uncool/UnDaab Etc
                         * */
                        if (flag == 1) {
                            momentsFeed.deleteDataset(index, smodel.getAction_flag(), itemRowHolder.textReact,
                                    itemRowHolder.coolbtn);
                        }
                        if (flag == 2) {
                            aboutFragment.deleteDataset(index, smodel.getAction_flag(), itemRowHolder.textReact,
                                    itemRowHolder.coolbtn);
                        }
                        data.addReactsCount(-1);
                        setReactCount(itemRowHolder, data);
                    } else {
                        /**
                         * * Show ANimation to User action Like/Cool/Nerd/Dab
                         * */
                        if (v.isSelected()) {
                            hideMenu(itemRowHolder.coolbtn, itemRowHolder.arc_layout, itemRowHolder.framArcView);
                        } else {
                            showMenu(itemRowHolder.coolbtn, itemRowHolder.arc_layout, itemRowHolder.framArcView);
                        }
                        v.setSelected(!v.isSelected());
                    }
                }
            });
            itemRowHolder.framArcView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideMenu(itemRowHolder.coolbtn, itemRowHolder.arc_layout, itemRowHolder.framArcView);
                    itemRowHolder.linReact.setSelected(false);
                    //itemRowHolder.framArcView.setVisibility(View.INVISIBLE);
                }
            });

            // user reactions
            itemRowHolder.btnarc_cool.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MediaPlayer mp = MediaPlayer.create(mContext, R.raw.react_chosen);
                    if (mp != null) mp.start();
                    hideMenu(itemRowHolder.coolbtn, itemRowHolder.arc_layout, itemRowHolder.framArcView);
                    itemRowHolder.linReact.setSelected(false);
                    if (flag == 1) {
                        momentsFeed.updateDataset(index, 1, itemRowHolder.textReact, itemRowHolder.coolbtn);
                    } else {
                        aboutFragment.updateDataset(index, 1, itemRowHolder.textReact, itemRowHolder.coolbtn);
                    }
                    data.addReactsCount(1);
                    setReactCount(itemRowHolder, data);
                }
            });
            itemRowHolder.btnarc_swag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MediaPlayer mp = MediaPlayer.create(mContext, R.raw.react_chosen);
                    if (mp != null) mp.start();
                    hideMenu(itemRowHolder.coolbtn, itemRowHolder.arc_layout, itemRowHolder.framArcView);
                    itemRowHolder.linReact.setSelected(false);
                    if (flag == 1) {
                        momentsFeed.updateDataset(index, 2, itemRowHolder.textReact, itemRowHolder.coolbtn);
                    } else {
                        aboutFragment.updateDataset(index, 2, itemRowHolder.textReact, itemRowHolder.coolbtn);
                    }
                    data.addReactsCount(1);
                    setReactCount(itemRowHolder, data);
                }
            });
            itemRowHolder.btnarc_nerd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MediaPlayer mp = MediaPlayer.create(mContext, R.raw.react_chosen);
                    if (mp != null) mp.start();
                    hideMenu(itemRowHolder.coolbtn, itemRowHolder.arc_layout, itemRowHolder.framArcView);
                    itemRowHolder.linReact.setSelected(false);
                    if (flag == 1) {
                        momentsFeed.updateDataset(index, 3, itemRowHolder.textReact, itemRowHolder.coolbtn);
                    } else {
                        aboutFragment.updateDataset(index, 3, itemRowHolder.textReact, itemRowHolder.coolbtn);
                    }
                    data.addReactsCount(1);
                    setReactCount(itemRowHolder, data);
                }
            });
            itemRowHolder.btnarc_dab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MediaPlayer mp = MediaPlayer.create(mContext, R.raw.react_chosen);
                    if (mp != null) mp.start();
                    hideMenu(itemRowHolder.coolbtn, itemRowHolder.arc_layout, itemRowHolder.framArcView);
                    itemRowHolder.linReact.setSelected(false);
                    if (flag == 1) {
                        momentsFeed.updateDataset(index, 4, itemRowHolder.textReact, itemRowHolder.coolbtn);
                    } else {
                        aboutFragment.updateDataset(index, 4, itemRowHolder.textReact, itemRowHolder.coolbtn);
                    }
                    data.addReactsCount(1);
                    setReactCount(itemRowHolder, data);
                }
            });
            itemRowHolder.btnarc_heart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MediaPlayer mp = MediaPlayer.create(mContext, R.raw.react_chosen);
                    if (mp != null) mp.start();
                    hideMenu(itemRowHolder.coolbtn, itemRowHolder.arc_layout, itemRowHolder.framArcView);
                    itemRowHolder.linReact.setSelected(false);
                    if (flag == 1) {
                        momentsFeed.updateDataset(index, 5, itemRowHolder.textReact, itemRowHolder.coolbtn);
                    } else {
                        aboutFragment.updateDataset(index, 5, itemRowHolder.textReact, itemRowHolder.coolbtn);
                    }
                    data.addReactsCount(1);
                    setReactCount(itemRowHolder, data);
                }
            });
        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    private void previewImage(SectionDataModel model, ImageView imageView) {
        if (model == null || model.getPost_image_createdby_creator_url() == null || imageView == null) {
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable("postlistdata", model);
        Bitmap bitmap = Utils.getBitmapFromImageView(imageView);
        //Bitmap bitmap = imageView.getDrawingCache();
        bundle.putParcelable(Constants.IMAGE_BITMAP, bitmap);

        Fragment fragment = new ImagePreviewFragment();
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = ((AppCompatActivity) mContext).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.frag_fade_in, R.anim.frag_fade_out,
                R.anim.frag_fade_in, R.anim.frag_fade_out);
        fragmentTransaction.replace(R.id.main_content, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void moveToComments(int position, SectionDataModel data, int flag, int showKeypad) {
        this.selectedIndex = position;
        Intent intent = new Intent(mContext, CommentLikeActivity_new.class);
        bundle.putSerializable(Constants.SELECTED_MODEL, data);
        bundle.putInt("comment_select_flag", flag);
        bundle.putInt("keypadshow", showKeypad);
        bundle.putString("postid", data.getPostid());
        bundle.putString("type", "moment");
        bundle.putString("react_type", "");
        bundle.putInt(Constants.COMMENTS_COUNT, data.getCommentcount());
        bundle.putInt(Constants.REACTS_COUNT, data.getReactsCount());
        intent.putExtras(bundle);
        mContext.startActivityForResult(intent, Constants.COMMENT_REQUEST_CODE);
    }

    public void refreshSelectedModel(SectionDataModel model) {
        if (model != null &&
                this.selectedIndex != -1 &&
                this.selectedIndex < this.dataList.size()) {
            SectionDataModel selectedModel = this.dataList.get(this.selectedIndex);
            selectedModel.setCommentcount(model.getCommentcount());
            selectedModel.setReactsCount(model.getReactsCount());
            notifyItemChanged(this.selectedIndex, selectedModel);
        }
    }

    private void showPopupMenu(View view, final int position, String postUserid) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        Menu menu = popupMenu.getMenu();

        if (logedInUserid.equalsIgnoreCase(postUserid)) {
            inflater.inflate(R.menu.popup_menu_delete, menu);
        } else {
            inflater.inflate(R.menu.popup_menu_report, menu);
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (flag == 1) {
                    momentsFeed.deleteOrReportPost(position);
                } else if (flag == 2) {
                    aboutFragment.deleteOrReportPost(position);
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void report_visibility(MotionEvent event, ItemRowHolder itemRowHolder) {
        if (event.getAction() == MotionEvent.ACTION_DOWN
                || event.getAction() == MotionEvent.ACTION_UP
                ||
                event.getAction() == MotionEvent.ACTION_SCROLL
                || event.getAction() == MotionEvent.ACTION_CANCEL) {
            itemRowHolder.moreoption.setTag(true);
        }
    }

    private void setReactCount(ItemRowHolder itemRowHolder, SectionDataModel data) {
        int reacts = 0;
        if (data != null) {
            reacts = data.getReactsCount();
        }
        if (reacts == 0) {
            itemRowHolder.headrCount.setVisibility(View.GONE);
        } else if (reacts == 1) {
            itemRowHolder.headrCount.setVisibility(View.VISIBLE);
            itemRowHolder.headrCount.setText("1" + " react");
        } else {
            itemRowHolder.headrCount.setVisibility(View.VISIBLE);
            itemRowHolder.headrCount.setText(reacts + " reacts");
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

    private void userActionAnication(Animation anim, final ImageView imageAnim, int rid) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                imageAnim.setVisibility(View.GONE);
            }
        }, 300);
        imageAnim.setImageResource(rid);
        imageAnim.setVisibility(View.VISIBLE);
        imageAnim.startAnimation(anim);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imageAnim.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    private class ItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView itemTitle, taggednumber, txt_descriptn, total_image;
        protected RecyclerView recycler_view_list;
        protected RelativeLayout relativeLayout;
        protected ImageView im, imageAnim, moreoption, imageAnim1_share;
        //    private View viewListContainer;
        private ImageView postImage, coolbtn, likebtn, commentbtn;

        private TextView timeset, headrCount, textCommentcount, textShareCount, textReact, textReport;
        private LinearLayout linearComment, linReact, sharebtn;
        FrameLayout framArcView;
        RelativeLayout layoutMain;
        ArcLayout arc_layout;
        ImageButton btnarc_heart, btnarc_cool, btnarc_dab, btnarc_nerd, btnarc_swag;
        RecyclerView selfyListRecyce;
        RelativeLayout relFeedThumb, relFeedThumb_share;
        LinearLayout video_btn_layout;
        ImageView owner_pic, owner_post, post_view_type;
        TextView owner_name, tagged_number_owner_creator, owner_post_title, owner_post_date;
        ProgressBar loading_spinner;

        private Bitmap imageBitmap;

        public ItemRowHolder(View view) {
            super(view);
            total_image = (TextView) view.findViewById(R.id.total_image);
            itemTitle = (TextView) view.findViewById(R.id.posted_user_name);
            taggednumber = (TextView) view.findViewById(R.id.tagged_number_post_creator);
            txt_descriptn = (TextView) view.findViewById(R.id.text_post_description);
            timeset = (TextView) view.findViewById(R.id.time_days_go);
            im = (ImageView) view.findViewById(R.id.userViewImage);
            likebtn = (ImageView) view.findViewById(R.id.like);
            commentbtn = (ImageView) view.findViewById(R.id.comment);
            sharebtn = (LinearLayout) view.findViewById(R.id.linearshare);
            postImage = (ImageView) view.findViewById(R.id.buzz_posted_image);
            txt_descriptn.setTypeface(FontStyles.font4Profile(mContext));
            layoutMain = (RelativeLayout) view.findViewById(R.id.top);
            textReport = (TextView) itemView.findViewById(R.id.textReport);
            imageAnim = (ImageView) view.findViewById(R.id.imageAnim);
            imageAnim1_share = (ImageView) view.findViewById(R.id.imageAnim1);
            video_btn_layout = (LinearLayout) view.findViewById(R.id.video_btn_layout);
            moreoption = (ImageView) view.findViewById(R.id.moreoption);
            headrCount = (TextView) view.findViewById(R.id.headrCount);
            textCommentcount = (TextView) view.findViewById(R.id.Commentcount);
            textShareCount = (TextView) view.findViewById(R.id.textShareCount);
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
            relFeedThumb = (RelativeLayout) view.findViewById(R.id.relFeedThumb);
            relFeedThumb_share = (RelativeLayout) view.findViewById(R.id.relFeedThumb_share);
            owner_name = (TextView) view.findViewById(R.id.owner_name);
            tagged_number_owner_creator = (TextView) view.findViewById(R.id.tagged_number_owner_creator);
            owner_post_title = (TextView) view.findViewById(R.id.owner_post_title);
            owner_pic = (ImageView) view.findViewById(R.id.owner_pic);
            owner_post = (ImageView) view.findViewById(R.id.owner_post);
            owner_post_date = (TextView) view.findViewById(R.id.owner_post_date);
            post_view_type = (ImageView) view.findViewById(R.id.post_view_type);
            loading_spinner = (ProgressBar) view.findViewById(R.id.loading_spinner);
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        }
    }

    private Animator createHideItemAnimator(final ImageView fab, FrameLayout frameLayout,
                                            final View item) {
        /*float dx = frameLayout.getX() / 2;//fab.getX() - item.getX();
        float dy = 0;//fab.getY()- item.getY();*/

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
}