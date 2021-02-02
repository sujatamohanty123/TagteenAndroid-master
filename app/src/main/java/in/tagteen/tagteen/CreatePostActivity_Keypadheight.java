package in.tagteen.tagteen;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import in.tagteen.tagteen.Adapters.SelectedGalaryImageAdapter;
import in.tagteen.tagteen.Filter.activity.ActivityGallery;
import in.tagteen.tagteen.Fragments.VideoFragment;
import in.tagteen.tagteen.Fragments.beans.FileDataSender;
import in.tagteen.tagteen.Fragments.youthtube.VideoTrimmerActivity;
import in.tagteen.tagteen.Fragments.youthtube.bean.VideoDataSender;
import in.tagteen.tagteen.GallaryPicker.GalleryFragment;
import in.tagteen.tagteen.Model.CreatePostJsonInputModel;
import in.tagteen.tagteen.Model.CreatePostJsonResponseModel;
import in.tagteen.tagteen.Model.GetAllUserFriendlist;
import in.tagteen.tagteen.Model.GetFanList;
import in.tagteen.tagteen.TagteenInterface.CameraInterface;
import in.tagteen.tagteen.TagteenInterface.ChatScreenCallback;
import in.tagteen.tagteen.TagteenInterface.GalleryClickedPosition;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.configurations.AWSUtility;
import in.tagteen.tagteen.configurations.ApplicationConstants;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.emoji.*;
import in.tagteen.tagteen.emoji.EmojiconEditText;
import in.tagteen.tagteen.selfyManager.CameraActivity;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.utils.AndroidPermissionMarshMallo;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static in.tagteen.tagteen.utils.AndroidPermissionMarshMallo.CAMERA_PERMISSION_REQUEST_CODE;

public class CreatePostActivity_Keypadheight extends AppCompatActivity implements GalleryClickedPosition, EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener, ChatScreenCallback, CameraInterface {
    private RelativeLayout mRootView, mRlTextBox;
    private CoordinatorLayout.LayoutParams mRootViewLayoutParams;
    private RelativeLayout.LayoutParams mRlTextBoxLayoutParams, mCustomViewLayoutParams;

    private EmojiconEditText mTextBox;
    private FrameLayout mCustomView;

    private int mKeyBoardHeight = -1;
    private int mTop;
    private boolean isKeyBoardShowing = false;

    private Fragment mCurrentGlalleryFragment;

    private RecyclerView imageList;
    private ImageView send,send_select;
    private LinearLayoutManager mLayoutManager;
    private SelectedGalaryImageAdapter mAdapter;
    private ImageView emojiButton, galleryButton, cameraButton, videoButton;
    private ArrayList<String> selectedPath = new ArrayList<>();
    private ArrayList<String> cachedImages = new ArrayList<>();
    public static int sharetoselected=0;
    private TextView public_txt, private_txt, frnd_txt, bff_txt, fans_txt;
    private ImageView img1,img2,img3,img4,img5;
    private TextView text2,text3,text4,text5,text6;
    private RelativeLayout sharebtn;
    private String filePath = "", actual_path = "";
    private VideoView videoView;
    private ImageView video_play,video_pause;
    private int vdHeight = 0, vdWidth = 0, vduration = 0;
    private CreatePostJsonInputModel json = new CreatePostJsonInputModel();
    private VideoDataSender dataSedataSender=new VideoDataSender();

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    private static final int BEAUTIFY_IMAGE_REQUEST_CODE = 4;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final String IMAGE_DIRECTORY_NAME = "Hello Camera";
    private Uri fileUri;
    private int imageHeight = 0, imageWidth = 0;
    private TextView item_count;
    private GetAllUserFriendlist.Data datalist=new GetAllUserFriendlist.Data();
    private ArrayList<GetAllUserFriendlist.FriendsUserList> friendlist = new ArrayList<>();
    private ArrayList<GetAllUserFriendlist.FriendsUserList> bfflist = new ArrayList<>();
    private ArrayList<GetFanList.UserData> fanlist=new ArrayList<>();
    private Dialog d;
    private TextView msg, name, continueorder, dismiss, ok;
    private LinearLayout buttonLayout;
    private String userid,token;

    private AndroidPermissionMarshMallo permissionMarshMallo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post__keypadheight);

        SharedPreferenceSingleton.getInstance().init(this);
        userid = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
        token = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TOKEN);

        permissionMarshMallo = new AndroidPermissionMarshMallo(this);

        d = new Dialog(this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setCanceledOnTouchOutside(true);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        d.setContentView(R.layout.confirmationpopup);
        msg = (TextView) d.findViewById(R.id.message);
        name = (TextView) d.findViewById(R.id.yourname);
        continueorder = (TextView) d.findViewById(R.id.confirm);
        dismiss = (TextView) d.findViewById(R.id.dismiss);
        ok = (TextView) d.findViewById(R.id.confirm_ok_btn);
        buttonLayout = (LinearLayout) d.findViewById(R.id.button_layout);

        mRootView = (RelativeLayout) findViewById(R.id.rootView);
        mRlTextBox = (RelativeLayout) findViewById(R.id.rlTextBox);
        mTextBox = (EmojiconEditText) findViewById(R.id.post_comment);
        galleryButton = (ImageView) findViewById(R.id.gallery_fram);
        emojiButton = (ImageView) findViewById(R.id.emoji_frame);
        videoButton = (ImageView) findViewById(R.id.video_fram);
        cameraButton = (ImageView) findViewById(R.id.camera_fram);
        mCustomView = (FrameLayout) findViewById(R.id.customView);

        mRootViewLayoutParams = (CoordinatorLayout.LayoutParams) mRootView.getLayoutParams();
        mRlTextBoxLayoutParams = (RelativeLayout.LayoutParams) mRlTextBox.getLayoutParams();
        mCustomViewLayoutParams = (RelativeLayout.LayoutParams) mCustomView.getLayoutParams();

        send = (ImageView) findViewById(R.id.post_button);
        send_select=(ImageView) findViewById(R.id.post_button_select);
        item_count=(TextView) findViewById(R.id.item_count);
        imageList = (RecyclerView) findViewById(R.id.selected_imge);
        sharebtn = (RelativeLayout) findViewById(R.id.share);
        public_txt = (TextView) findViewById(R.id.public_txt);
        private_txt = (TextView) findViewById(R.id.private_txt);
        fans_txt = (TextView) findViewById(R.id.fans_txt);
        frnd_txt = (TextView) findViewById(R.id.frnd_txt);
        bff_txt = (TextView) findViewById(R.id.bff_txt);
        public_txt.setVisibility(View.VISIBLE);
        videoView = (VideoView) findViewById(R.id.videoView);
        video_play = (ImageView) findViewById(R.id.video_play);
        video_pause = (ImageView) findViewById(R.id.video_pause);
        SharedPreferenceSingleton.getInstance().init(CreatePostActivity_Keypadheight.this);
        mTextBox.setHint("What's up" + " " + SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.FIRST_NAME) + "?");

        init();

        imageList.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        imageList.setLayoutManager(mLayoutManager);

        mTextBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emojiButton.setImageResource(R.drawable.ic_emoji_grey_new);
                galleryButton.setImageResource(R.drawable.ic_gallary_new);
                videoButton.setImageResource(R.drawable.ic_video_grey);
                cameraButton.setImageResource(R.drawable.ic_camera_new);
                showGallery(false,0);
            }
        });

        mTextBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setPostButtonEnabled();
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                setPostButtonEnabled();
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (videoView.getVisibility()==View.GONE) {
                    emojiButton.setImageResource(R.drawable.ic_emoji_grey_new);
                    galleryButton.setImageResource(R.drawable.ic_gallary_new_select);
                    videoButton.setImageResource(R.drawable.ic_video_grey);
                    cameraButton.setImageResource(R.drawable.ic_camera_new);
                    showGallery(true, 2);
                } else {
                    Utils.showShortToast(getApplicationContext(),"You can't upload image and video simultaneously.");
                }
            }
        });

        emojiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emojiButton.setImageResource(R.drawable.ic_emoji_grey_new_select);
                galleryButton.setImageResource(R.drawable.ic_gallary_new);
                videoButton.setImageResource(R.drawable.ic_video_grey);
                cameraButton.setImageResource(R.drawable.ic_camera_new);
                showGallery(true,1);
            }
        });
        videoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedPath.isEmpty()) {
                    emojiButton.setImageResource(R.drawable.ic_emoji_grey_new);
                    galleryButton.setImageResource(R.drawable.ic_gallary_new);
                    videoButton.setImageResource(R.drawable.ic_video_grey_select);
                    cameraButton.setImageResource(R.drawable.ic_camera_new);
                    showGallery(true, 3);
                } else {
                    Toast.makeText(getApplicationContext(),"You can't upload image and video simultaneously.",Toast.LENGTH_LONG).show();
                }
            }
        });
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SelfiFragment(new SelfiFragment());
                /*Intent intent = new Intent(CreatePostActivity.this, CameraActivity.class);
                intent.putExtra("cameraposition","addpost");
                startActivityForResult(intent, 4);*/
                emojiButton.setImageResource(R.drawable.ic_emoji_grey_new);
                galleryButton.setImageResource(R.drawable.ic_gallary_new);
                videoButton.setImageResource(R.drawable.ic_video_grey);
                cameraButton.setImageResource(R.drawable.ic_camera_new_select);
                if (!isDeviceSupportCamera()) {
                    Utils.showShortToast(getApplicationContext(), "Sorry! Your device doesn't support camera");
                    // will close the app if the device does't have camera
                    finish();
                }
                if (permissionMarshMallo.checkPermissionForCamera()) {
                    openCamera();
                } else {
                    permissionMarshMallo.requestPermissionForCamera();
                }
            }
        });

        filePath = getIntent().getStringExtra(Constants.TRIMMED_VIDEO_PATH);
        actual_path = getIntent().getStringExtra("actual_path");
        if (filePath != null && actual_path != null) {
            imageList.setVisibility(View.GONE);
            videoView.setVisibility(View.VISIBLE);
            video_play.setVisibility(View.VISIBLE);
            item_count.setVisibility(View.GONE);
            item_count.setText("1");
            send_select.setVisibility(View.VISIBLE);
            send.setVisibility(View.GONE);

            try {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(filePath);
                if (retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH) != null) {
                    vdWidth = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
                }
                if (retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT) != null) {
                    vdHeight = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
                }
                if (retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION) != null) {
                    vduration = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                }
                retriever.release();
            } catch (Exception e) {
                // do nothing
            }

            dataSedataSender.setVideoHeight(vdHeight);
            dataSedataSender.setVideoWidth(vdWidth);
            dataSedataSender.setVideoPath(filePath);
            dataSedataSender.setVideo_duration(vduration);
            /*MediaController myMediaController = new MediaController(CreatePostActivity_Keypadheight.this);
            videoView.setMediaController(myMediaController);*/
        }
        video_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                video_play.setVisibility(View.GONE);
                video_pause.setVisibility(View.VISIBLE);
                videoView.setVideoURI(Uri.parse(filePath));
                videoView.start();
            }
        });
        video_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                video_pause.setVisibility(View.GONE);
                video_play.setVisibility(View.VISIBLE);
                videoView.pause();
            }
        });

       send_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_select.setVisibility(View.GONE);
                send.setVisibility(View.VISIBLE);
                FileDataSender.POST_TO = FileDataSender.POST_TO_MOMENTS;
                if (imageList.getVisibility() == View.VISIBLE) {
                    if (selectedPath != null && !selectedPath.isEmpty()) {
                        FileDataSender.HAS_FILE_TO_UPLOAD = true;
                        FileDataSender.VIEW_TO = sharetoselected;
                        FileDataSender.DESCRIPTION = mTextBox.getText().toString();
                        FileDataSender.FILE_PATH = null;
                        FileDataSender.FILE_PATH_LIST = selectedPath;
                        FileDataSender.CATEGORY_ID = 1;
                        setResult(RESULT_OK, null);
                        finish();
                    } else if (mTextBox.getText().toString().length() > 0) {
                        callapiPost(selectedPath, mTextBox.getText().toString());
                    }
                } else if (videoView.getVisibility() == View.VISIBLE) {
                    dataSedataSender.setVideoDesc(mTextBox.getText().toString());
                    dataSedataSender.setVideoView_to(String.valueOf(sharetoselected));
                    dataSedataSender.setIsCall(true);
                    setResult(RESULT_OK, null);
                    finish();
                } else if (mTextBox.getText().toString().length() > 0) {
                    callapiPost(selectedPath, mTextBox.getText().toString());
                } else {
                    CreatePostActivity_Keypadheight.this.finish();
                }
            }
        });
        sharebtn.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick (View v){
                final Dialog onLongPressdialog = new Dialog(CreatePostActivity_Keypadheight.this);
                onLongPressdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                onLongPressdialog.setCanceledOnTouchOutside(true);
                onLongPressdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                onLongPressdialog.setContentView(R.layout.privacy_dialog);
                RelativeLayout public_relative = (RelativeLayout) onLongPressdialog.findViewById(R.id.relative1);
                RelativeLayout private_relative = (RelativeLayout) onLongPressdialog.findViewById(R.id.relative2);
                RelativeLayout frnds_relative = (RelativeLayout) onLongPressdialog.findViewById(R.id.relative3);
                RelativeLayout bff_relative = (RelativeLayout) onLongPressdialog.findViewById(R.id.relative4);
                RelativeLayout fan_relative = (RelativeLayout) onLongPressdialog.findViewById(R.id.relative5);
                 img1 = (ImageView) onLongPressdialog.findViewById(R.id.img1);
                 img2 = (ImageView) onLongPressdialog.findViewById(R.id.img2);
                 img3 = (ImageView) onLongPressdialog.findViewById(R.id.img3);
                 img4 = (ImageView) onLongPressdialog.findViewById(R.id.img4);
                 img5 = (ImageView) onLongPressdialog.findViewById(R.id.img5);
                 text2 = (TextView) onLongPressdialog.findViewById(R.id.text2);
                 text3 = (TextView) onLongPressdialog.findViewById(R.id.text3);
                 text4 = (TextView) onLongPressdialog.findViewById(R.id.text4);
                 text5 = (TextView) onLongPressdialog.findViewById(R.id.text5);
                 text6 = (TextView) onLongPressdialog.findViewById(R.id.text6);
                final ImageView imageProfilePic = (ImageView) onLongPressdialog.findViewById(R.id.imageProfilePic);
                TextView done = (TextView) onLongPressdialog.findViewById(R.id.done);

                imageProfilePic.setVisibility(View.GONE);

                callApiFriendsList();
                getFanList();

                if(public_txt.getVisibility()==View.VISIBLE){
                    public_select();
                }
                if(private_txt.getVisibility()==View.VISIBLE){
                    private_select();
                }
                if(frnd_txt.getVisibility()==View.VISIBLE){
                    frnd_select();
                }
                if(bff_txt.getVisibility()==View.VISIBLE){
                    bff_select();
                }
                if(fans_txt.getVisibility()==View.VISIBLE){
                    fan_select();
                }


                public_relative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        public_select();
                    }
                });
                private_relative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        private_select();
                    }
                });
                frnds_relative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        frnd_select();
                    }
                });
                bff_relative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bff_select();
                    }
                });
                fan_relative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fan_select();
                    }
                });
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (img1.getVisibility() == View.VISIBLE) {
                            public_txt.setVisibility(View.VISIBLE);
                            private_txt.setVisibility(View.GONE);
                            frnd_txt.setVisibility(View.GONE);
                            bff_txt.setVisibility(View.GONE);
                            fans_txt.setVisibility(View.GONE);
                            sharetoselected = 1;
                        }
                        if (img2.getVisibility() == View.VISIBLE) {
                            public_txt.setVisibility(View.GONE);
                            private_txt.setVisibility(View.VISIBLE);
                            frnd_txt.setVisibility(View.GONE);
                            bff_txt.setVisibility(View.GONE);
                            fans_txt.setVisibility(View.GONE);
                            sharetoselected = 5;
                        }
                        if (img3.getVisibility() == View.VISIBLE) {
                            if(friendlist.size()>0) {
                                public_txt.setVisibility(View.GONE);
                                private_txt.setVisibility(View.GONE);
                                frnd_txt.setVisibility(View.VISIBLE);
                                bff_txt.setVisibility(View.GONE);
                                fans_txt.setVisibility(View.GONE);
                                sharetoselected = 2;
                            }else {
                                dialog_show_share("Friend");
                            }
                        }
                        if (img4.getVisibility() == View.VISIBLE) {
                            if(bfflist.size()>0) {
                                public_txt.setVisibility(View.GONE);
                                private_txt.setVisibility(View.GONE);
                                frnd_txt.setVisibility(View.GONE);
                                bff_txt.setVisibility(View.VISIBLE);
                                fans_txt.setVisibility(View.GONE);
                                sharetoselected = 4;
                            }else{
                                dialog_show_share("BFF");
                            }
                        }
                        if (img5.getVisibility() == View.VISIBLE) {
                            if(fanlist.size()>0) {
                                public_txt.setVisibility(View.GONE);
                                private_txt.setVisibility(View.GONE);
                                frnd_txt.setVisibility(View.GONE);
                                bff_txt.setVisibility(View.GONE);
                                fans_txt.setVisibility(View.VISIBLE);
                                sharetoselected = 3;
                            }else {
                                dialog_show_share("Supporter");
                            }
                        }
                        onLongPressdialog.dismiss();
                    }
                });

                onLongPressdialog.show();
            }
        });

    }

    private void dialog_show_share(String s) {
        buttonLayout.setVisibility(View.GONE);
        ok.setVisibility(View.VISIBLE);
        name.setVisibility(View.GONE);
        msg.setText("you don't have a "+s);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        d.show();
    }


    private void callApiFriendsList() {
        Apimethods methods = API_Call_Retrofit.getretrofit(this).create(Apimethods.class);
        Call<GetAllUserFriendlist> call =methods.getalluserfriendslist(userid,token);
        Log.d("url","url="+call.request().url().toString());
        call.enqueue(new Callback<GetAllUserFriendlist>() {
            @Override
            public void onResponse(Call<GetAllUserFriendlist> call, Response<GetAllUserFriendlist> response) {
                if(response.code()==200){
                    GetAllUserFriendlist getlistdata=response.body();
                    datalist=getlistdata.getData();

                    bfflist= (ArrayList<GetAllUserFriendlist.FriendsUserList>) datalist.getBff();
                    friendlist= (ArrayList<GetAllUserFriendlist.FriendsUserList>) datalist.getFriendsUserList();
                }else if(response.code()==401){

                }
            }

            @Override
            public void onFailure(Call<GetAllUserFriendlist> call, Throwable t) {

            }
        });
    }
    private void getFanList() {
        Apimethods methods = API_Call_Retrofit.getretrofit(this).create(Apimethods.class);
        Call<GetFanList> call = methods.getUserFanList(userid, 1, 10, token);
        Log.d("url", "url=" + call.request().url().toString());
        call.enqueue(new Callback<GetFanList>() {
            @Override
            public void onResponse(Call<GetFanList> call, Response<GetFanList> response) {
                int statuscode = response.code();
                if (statuscode == 200) {

                    GetFanList getresponsemodel = response.body();
                    fanlist = (ArrayList<GetFanList.UserData>) getresponsemodel.getData();
                }
                if (statuscode == 401) {

                }
            }

            @Override
            public void onFailure(Call<GetFanList> call, Throwable t) {

            }
        });
    }

    private void fan_select() {
        img1.setVisibility(View.GONE);
        img2.setVisibility(View.GONE);
        img3.setVisibility(View.GONE);
        img4.setVisibility(View.GONE);
        img5.setVisibility(View.VISIBLE);

        text2.setTextColor(getResources().getColor(R.color.grey));
        text3.setTextColor(getResources().getColor(R.color.grey));
        text4.setTextColor(getResources().getColor(R.color.grey));
        text5.setTextColor(getResources().getColor(R.color.grey));
        text6.setTextColor(getResources().getColor(R.color.black));
    }

    private void bff_select() {
        img1.setVisibility(View.GONE);
        img2.setVisibility(View.GONE);
        img3.setVisibility(View.GONE);
        img4.setVisibility(View.VISIBLE);
        img5.setVisibility(View.GONE);

        text2.setTextColor(getResources().getColor(R.color.grey));
        text3.setTextColor(getResources().getColor(R.color.grey));
        text4.setTextColor(getResources().getColor(R.color.grey));
        text5.setTextColor(getResources().getColor(R.color.black));
        text6.setTextColor(getResources().getColor(R.color.grey));
    }

    private void frnd_select() {
        img1.setVisibility(View.GONE);
        img2.setVisibility(View.GONE);
        img3.setVisibility(View.VISIBLE);
        img4.setVisibility(View.GONE);
        img5.setVisibility(View.GONE);

        text2.setTextColor(getResources().getColor(R.color.grey));
        text3.setTextColor(getResources().getColor(R.color.grey));
        text4.setTextColor(getResources().getColor(R.color.black));
        text5.setTextColor(getResources().getColor(R.color.grey));
        text6.setTextColor(getResources().getColor(R.color.grey));
    }

    private void private_select() {
        img1.setVisibility(View.GONE);
        img2.setVisibility(View.VISIBLE);
        img3.setVisibility(View.GONE);
        img4.setVisibility(View.GONE);
        img5.setVisibility(View.GONE);

        text2.setTextColor(getResources().getColor(R.color.grey));
        text3.setTextColor(getResources().getColor(R.color.black));
        text4.setTextColor(getResources().getColor(R.color.grey));
        text5.setTextColor(getResources().getColor(R.color.grey));
        text6.setTextColor(getResources().getColor(R.color.grey));
    }

    private void public_select() {
        img1.setVisibility(View.VISIBLE);
        img2.setVisibility(View.GONE);
        img3.setVisibility(View.GONE);
        img4.setVisibility(View.GONE);
        img5.setVisibility(View.GONE);

        text2.setTextColor(getResources().getColor(R.color.black));
        text3.setTextColor(getResources().getColor(R.color.grey));
        text4.setTextColor(getResources().getColor(R.color.grey));
        text5.setTextColor(getResources().getColor(R.color.grey));
        text6.setTextColor(getResources().getColor(R.color.grey));
    }

    private void init(){
        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                mRootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = mRootView.getRootView().getHeight();

                // r.bottom is the position above soft keypad or device button.
                // if keypad is shown, the r.bottom is smaller than that before.
                int keypadHeight = screenHeight - r.bottom;

                Log.d("test", "keypadHeight = " + keypadHeight);
                mTop = r.top;

                if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                    // keyboard is opened
                    isKeyBoardShowing = true;
                    mKeyBoardHeight = keypadHeight;
                } else {
                    // keyboard is closed
                    if(mKeyBoardHeight == -1) {
                        mKeyBoardHeight = (int) (screenHeight * 0.3f);
                    }
                    isKeyBoardShowing = false;
                }
            }
        });
    }

    private void showGallery(boolean isShow, int i){
        if(isShow){
            mRootViewLayoutParams.height = mRootView.getRootView().getHeight();
            mRlTextBoxLayoutParams.bottomMargin = mKeyBoardHeight + mTop;
            mCustomViewLayoutParams.height = mKeyBoardHeight + mTop;
            mCustomView.setVisibility(View.VISIBLE);
            hideKeyBoard();

            if (i == 1){
                mCurrentGlalleryFragment = new EmojiconGridFragment();
            } else if(i == 2){
                mCurrentGlalleryFragment = new GalleryFragment();
                Bundle args = new Bundle();
                args.putStringArrayList(Constants.SELECTED_IMAGE_PATHS, selectedPath);
                mCurrentGlalleryFragment.setArguments(args);
            } else if(i == 3){
                mCurrentGlalleryFragment = new VideoFragment();
            }
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.customView, mCurrentGlalleryFragment);
            ft.commit();
        } else {
            mRootViewLayoutParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;
            mRlTextBoxLayoutParams.bottomMargin = 0;
            mCustomView.setVisibility(View.GONE);

            if (mCurrentGlalleryFragment != null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.remove(mCurrentGlalleryFragment);
                ft.commit();
            }
        }
        mRootView.requestLayout();
    }

    private void moveToBeautifyImage(String imagePath) {
        Intent it = new Intent(this, ActivityGallery.class );
        SharedPreferenceSingleton.getInstance().init(this);
        it.putExtra("camera","camera");
        SharedPreferenceSingleton.getInstance().writeStringPreference(ApplicationConstants.CAPTURED_IMAGE_PATH, imagePath);
        startActivityForResult(it, BEAUTIFY_IMAGE_REQUEST_CODE);
    }

    private void moveToTrimVideo(String videoPath) {
        Intent intent = new Intent(this, VideoTrimmerActivity.class);
        intent.putExtra(Constants.EXTRA_VIDEO_PATH, videoPath);
        intent.putExtra(Constants.VIDEO_PICK,"1");
        startActivity(intent);
        finish();
    }

    private void hideKeyBoard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void callapiPost(List<String> selectpath, final String textstring) {
        json = new CreatePostJsonInputModel();
        String userid = SharedPreferenceSingleton.getInstance().getStringPreferenceNull(RegistrationConstants.USER_ID);
        String token = SharedPreferenceSingleton.getInstance().getStringPreferenceNull(RegistrationConstants.TOKEN);
        json.setPostCreatorId(userid);
        String textval = textstring == null ? "" : textstring;
        json.setContent(textval);
        json.setType(1);
        json.setCategorie_id(1);
        if (sharetoselected==0) {
            sharetoselected = 1;
            json.setView_to(sharetoselected);
        } else {
            json.setView_to(sharetoselected);
        }
        //FIXME: for now share it all public
        json.setView_to(1);

        List<CreatePostJsonInputModel.ImageDataAllToSend> imagedataall = new ArrayList<>();
        if (selectpath != null) {
            for (int i = 0; i < selectpath.size(); i++) {
                CreatePostJsonInputModel.ImageDataAllToSend imagedata = new CreatePostJsonInputModel.ImageDataAllToSend();
                Log.d("CreatePost", "File path:" + selectpath.get(i));
                File file_for_selectedPath = new File(selectpath.get(i));
                Uri selected_image_uri = Uri.fromFile(file_for_selectedPath);
                //getIMGSize(selected_image_uri);
                getIMGSize(selectpath.get(i));
                String path_from_aws = AWSUtility.uploadProfileImage(
                        file_for_selectedPath, CreatePostActivity_Keypadheight.this, RegistrationConstants.POST_IMAGE, userid);
                imagedata.setUrl(path_from_aws);
                imagedata.setHeight(String.valueOf(imageHeight));
                imagedata.setWidth(String.valueOf(imageWidth));
                imagedataall.add(imagedata);
            }
            json.setImage(imagedataall);
        }

        Apimethods methods = API_Call_Retrofit.getretrofit(CreatePostActivity_Keypadheight.this).create(Apimethods.class);
        Call<CreatePostJsonResponseModel> call = methods.setpost(json, token);
        final String url = call.request().url().toString();
        call.enqueue(new Callback<CreatePostJsonResponseModel>() {
            @Override
            public void onResponse(Call<CreatePostJsonResponseModel> call, Response<CreatePostJsonResponseModel> response) {
                if (response.code() == 200) {
                    //Utils.showShortToast(CreatePostActivity_Keypadheight.this, "Successfully created");
                    setResult(RESULT_OK, new Intent());
                    finish();
                } else if (response.code() == 401) {
                    setResult(RESULT_CANCELED, new Intent());
                    finish();
                }
            }

            @Override
            public void onFailure(Call<CreatePostJsonResponseModel> call, Throwable t) {
                Log.d("Create post", "Failed url:" + url);
                setResult(RESULT_CANCELED, new Intent());
                finish();
            }
        });
    }

    private void getIMGSize(String uri) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(uri, options);
        imageHeight = options.outHeight;
        imageWidth = options.outWidth;
    }

    @Override
    public void clicked(ArrayList<String> imagePath, int requestNumber) {
        imageList.setVisibility(View.VISIBLE);
        videoView.setVisibility(View.GONE);
        if (requestNumber == 0) {
            if (imagePath.size() > 0) {
                selectedPath = imagePath;
                Set_SelectedGalaryImageAdapter(imagePath);
            } else {
                selectedPath = new ArrayList<>();
                Set_SelectedGalaryImageAdapter(imagePath);
            }
        } else {
            Intent it = new Intent(CreatePostActivity_Keypadheight.this, BrowsePicture2.class);
            startActivityForResult(it, 5);
        }

    }
    private void Set_SelectedGalaryImageAdapter(ArrayList<String> imagePath) {
        mAdapter = new SelectedGalaryImageAdapter(this, imagePath);
        imageList.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        madapter_item_click();
        this.setPostButtonEnabled();
    }

    private void setPostButtonEnabled() {
        String desc = this.mTextBox.getText().toString();
        if ((selectedPath == null || selectedPath.isEmpty()) &&
                (desc == null || desc.trim().length() == 0)){
            send_select.setVisibility(View.GONE);
            send.setVisibility(View.VISIBLE);
        } else {
            send_select.setVisibility(View.VISIBLE);
            send.setVisibility(View.GONE);
        }
    }

    private void madapter_item_click() {
        mAdapter.setOnItemClickListener(new SelectedGalaryImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, ArrayList<String> imagePath, int clickedPosition) {
                Intent intent = new Intent(CreatePostActivity_Keypadheight.this, ActivityGallery_Viewpager.class);
                intent.putStringArrayListExtra(ApplicationConstants.FILTERED_IMAGE_PATH, imagePath);
                intent.putExtra("position_id", clickedPosition);
                startActivityForResult(intent, 4);
            }
        });
    }

    public void onFragmentViewClick(View v) {
        if (v.getId() == R.id.closs_selfi_view) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            //getSupportActionBar().show();
        }
    }

    private void openCamera() {
        this.hideKeyBoard();
        Intent intent = new Intent(CreatePostActivity_Keypadheight.this, CameraActivity.class);
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Utils.hideKeyboard(this, getCurrentFocus());
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // successfully captured the image
                // display it in image view
                String imagePath = data.getStringExtra(Constants.PATH_IMAGE_CAPTURED);
                if (imagePath != null) {
                    Log.d("CreatePost", "Selected image" + imagePath);
                    this.cachedImages.add(imagePath);
                    this.moveToBeautifyImage(imagePath);
                } else if (data.getStringExtra(Constants.PATH_VIDEO_RECORDED) != null) {
                    this.moveToTrimVideo(data.getStringExtra(Constants.PATH_VIDEO_RECORDED));
                }
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Log.d("CreatePost", "User cancelled image capture");
            } else {
                // failed to capture image
                Utils.showShortToast(this, "Sorry! Failed to capture image");
            }
        }  if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // video successfully recorded
                // preview the recorded video
                //previewVideo();
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled recording
                Utils.showShortToast(getApplicationContext(), "User cancelled video recording");
            } else {
                // failed to record video
                Utils.showShortToast(getApplicationContext(), "Sorry! Failed to record video");
            }
        }
        if (resultCode == BEAUTIFY_IMAGE_REQUEST_CODE) {
            //getSupportActionBar().show();
            ArrayList<String> imagePathList = new ArrayList<>();
            imagePathList = data.getStringArrayListExtra("ImagePath");
            if (imagePathList != null && imagePathList.isEmpty() == false) {
                for (String imagePath : imagePathList) {
                    cachedImages.add(imagePath);
                }
            }
            selectedPath.clear();
            selectedPath.addAll(imagePathList);
            Set_SelectedGalaryImageAdapter(imagePathList);
        }
        if (resultCode == 5) {
            ArrayList<String> imagePath = data.getStringArrayListExtra("selectedImage");
            if (imagePath.size() > 0) {
                selectedPath.addAll(imagePath);
                Set_SelectedGalaryImageAdapter(selectedPath);
            } else {
                selectedPath = new ArrayList<>();
                Set_SelectedGalaryImageAdapter(selectedPath);
            }
        }
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(mTextBox, emojicon);
    }
    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(mTextBox);
    }
    @Override
    public void isPasswordMatched(boolean isPasswordMatched) {
    }
    @Override
    public void gallerySelectedImage(boolean send) {
    }
    @Override
    public void cameraClickedImage(String imagePath) {
    }
    @Override
    public void audioMusicFile(String imagePath) {
    }
    @Override
    public void cameraClose(boolean isClicked) {
    }
    @Override
    public void clickedPick(String Image, boolean isClosed) {
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {
        // External sdcard location
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }
    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (selectedPath.size()>0){
                dialog_show();
            } else if (mTextBox.getText().toString().length() > 0){
                dialog_show();
            } else if (videoView.getVisibility() == View.VISIBLE){
                dialog_show();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void dialog_show() {
        ok.setVisibility(View.GONE);
        name.setVisibility(View.GONE);
        msg.setText("Are you sure you want to discard this moment ?");
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });
        continueorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
                deleteCachedImages();
                finish();
            }
        });
        d.show();
    }

    private void deleteCachedImages() {
        if (this.cachedImages.size() > 0){
            for (String imagePath : this.cachedImages) {
                File file = new File(imagePath);
                if (file != null && file.exists()) {
                    file.delete();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (this.mCustomView.getVisibility() == View.VISIBLE) {
            this.showGallery(false, 0);
            return;
        }
        super.onBackPressed();
    }
}
