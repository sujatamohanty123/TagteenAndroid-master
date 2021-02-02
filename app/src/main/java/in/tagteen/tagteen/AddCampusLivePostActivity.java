package in.tagteen.tagteen;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import in.tagteen.tagteen.Fragments.beans.FileDataSender;
import in.tagteen.tagteen.Fragments.youthtube.VideoGallery;
import in.tagteen.tagteen.Fragments.youthtube.bean.VideoDataSender;
import in.tagteen.tagteen.GallaryPicker.GalleryPickerActivity;
import in.tagteen.tagteen.Model.CreatePostJsonInputModel;
import in.tagteen.tagteen.Model.CreatePostJsonResponseModel;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.emoji.Emojicon;
import in.tagteen.tagteen.emoji.EmojiconEditText;
import in.tagteen.tagteen.emoji.EmojiconGridFragment;
import in.tagteen.tagteen.emoji.EmojiconsFragment;
import in.tagteen.tagteen.selfyManager.CameraActivity;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.FloatingActionButton;
import in.tagteen.tagteen.util.PathUtil;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCampusLivePostActivity extends AppCompatActivity implements EmojiconGridFragment.OnEmojiconClickedListener, ChatTreadActivity.OnEmojiconBackspaceClickedListener {
    private static final String TAG = "AddCampusLivePost";
    private ImageView imageBack;
    private ImageView imageUpload;
    private EmojiconEditText txtContent;
    private TextView lblContentLength;
    private LinearLayout layoutContent;

    private RelativeLayout mRootView;
    private Fragment fragmentKeypad;
    private FrameLayout keypadLayout;
    private FrameLayout.LayoutParams mRootViewLayoutParams;
    private RelativeLayout.LayoutParams mKeypadViewLayoutParams;
    private RelativeLayout.LayoutParams attachmentOptionsLayoutParams;
    private LinearLayout layoutAttachmentOptions;

    private RelativeLayout layoutAttachment;
    private TextView fileView;
    private ImageView imageView;
    private VideoView videoView;
    private ImageView imgPlayVideo;
    private ImageView imgPauseVideo;

    private String videoPath;
    private int vdHeight = 0, vdWidth = 0, vduration = 0;
    private int shareToSelected = 1;
    private VideoDataSender videoDataSender = new VideoDataSender();

    private int mKeyBoardHeight = -1;
    private int mTop;

    private FloatingActionButton btnColor1;
    private FloatingActionButton btnColor2;
    private FloatingActionButton btnColor3;
    private FloatingActionButton btnColor4;
    private FloatingActionButton btnColor5;
    private FloatingActionButton btnColor6;
    private FloatingActionButton btnColor7;
    private FloatingActionButton btnColor8;

    private ImageView optionEmoji;
    private ImageView optionImage;
    private ImageView optionCamera;
    private ImageView optionVideo;
    private ImageView optionFile;

    private String filePath;
    private int imageHeight = 0, imageWidth = 0;

    private static final int CAMERA_PICK_REQUEST = 1;
    private static final int GALLERY_PICK_REQUEST = 2;
    private static final int VIDEO_PICK_REQUEST = 3;
    private static final int FILE_PICK_REQUEST = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_campus_live_post);

        this.initWidgets();
        this.bindEvents();
    }

    private void initWidgets() {
        this.mRootView = (RelativeLayout) findViewById(R.id.rootView);
        this.imageBack = (ImageView) findViewById(R.id.imageBack);
        this.imageUpload = (ImageView) findViewById(R.id.imageUplaod);
        this.imageUpload.setEnabled(false);
        this.txtContent = (EmojiconEditText) findViewById(R.id.txtContent);
        this.lblContentLength = (TextView) findViewById(R.id.lblContentLength);
        this.layoutContent = (LinearLayout) findViewById(R.id.layoutContent);
        this.keypadLayout = (FrameLayout) findViewById(R.id.viewKeypad);
        this.layoutAttachmentOptions = (LinearLayout) findViewById(R.id.layoutAttachmentOptions);

        this.mRootViewLayoutParams = (FrameLayout.LayoutParams) this.mRootView.getLayoutParams();
        this.mKeypadViewLayoutParams = (RelativeLayout.LayoutParams) this.keypadLayout.getLayoutParams();
        this.attachmentOptionsLayoutParams = (RelativeLayout.LayoutParams) this.layoutAttachmentOptions.getLayoutParams();

        this.layoutAttachment = (RelativeLayout) findViewById(R.id.layoutAttachment);
        this.fileView = (TextView) findViewById(R.id.fileView);
        this.imageView = (ImageView) findViewById(R.id.imageView);
        this.videoView = (VideoView) findViewById(R.id.videoView);
        this.imgPlayVideo = (ImageView) findViewById(R.id.imgPlayVideo);
        this.imgPauseVideo = (ImageView) findViewById(R.id.imgPauseVideo);

        this.optionEmoji = (ImageView) findViewById(R.id.optionEmoji);
        this.optionImage = (ImageView) findViewById(R.id.optionImage);
        this.optionCamera = (ImageView) findViewById(R.id.optionCamera);
        this.optionVideo = (ImageView) findViewById(R.id.optionVideo);
        this.optionFile = (ImageView) findViewById(R.id.optionFile);

        this.btnColor1 = (FloatingActionButton) findViewById(R.id.btnColor1);
        this.btnColor2 = (FloatingActionButton) findViewById(R.id.btnColor2);
        this.btnColor3 = (FloatingActionButton) findViewById(R.id.btnColor3);
        this.btnColor4 = (FloatingActionButton) findViewById(R.id.btnColor4);
        this.btnColor5 = (FloatingActionButton) findViewById(R.id.btnColor5);
        this.btnColor6 = (FloatingActionButton) findViewById(R.id.btnColor6);
        this.btnColor7 = (FloatingActionButton) findViewById(R.id.btnColor7);
        this.btnColor8 = (FloatingActionButton) findViewById(R.id.btnColor8);

        this.initKeypad();
    }

    private void initKeypad(){
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

    private void bindEvents() {
        this.imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        this.imageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final MediaPlayer postCampusliveSound =
                        MediaPlayer.create(AddCampusLivePostActivity.this, R.raw.campus_live_post);
                if (postCampusliveSound != null){
                    postCampusliveSound.start();
                }
                enablePostButton(false);
                createPost();
            }
        });
        this.txtContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetKeypadView();
            }
        });
        this.txtContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                lblContentLength.setText(charSequence.length() + "/180");
                setPostButtonState();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        this.optionEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEmojiKeypad();
            }
        });
        this.optionImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToGallery();
            }
        });
        this.optionCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToCamera();
            }
        });
        this.optionVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToVideos();
            }
        });
        this.optionFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickFile();
            }
        });
        this.imgPlayVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoPath != null) {
                    imgPlayVideo.setVisibility(View.GONE);
                    imgPauseVideo.setVisibility(View.VISIBLE);
                    videoView.setVideoURI(Uri.parse(videoPath));
                    videoView.start();
                }
            }
        });
        this.imgPauseVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgPauseVideo.setVisibility(View.GONE);
                imgPlayVideo.setVisibility(View.VISIBLE);
                videoView.pause();
            }
        });


        this.btnColor1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentBackgroundColor(0);
            }
        });
        this.btnColor2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentBackgroundColor(1);
            }
        });
        this.btnColor3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentBackgroundColor(2);
            }
        });
        this.btnColor4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentBackgroundColor(3);
            }
        });
        this.btnColor5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentBackgroundColor(4);
            }
        });
        this.btnColor6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentBackgroundColor(5);
            }
        });
        this.btnColor7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentBackgroundColor(6);
            }
        });
        this.btnColor8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentBackgroundColor(7);
            }
        });
    }

    private void setPostButtonState() {
        boolean enable = false;
        if (this.filePath != null ||
                this.videoPath != null ||
                this.txtContent.getText().toString().trim().length() > 0) {
            enable = true;
        }
        this.enablePostButton(enable);
    }

    private void enablePostButton(boolean enable) {
        this.imageUpload.setEnabled(enable);
        if (enable) {
            this.imageUpload.setImageResource(R.drawable.ic_send_selected);
        } else {
            this.imageUpload.setImageResource(R.drawable.ic_send_new);
        }
    }

    private void resetKeypadView() {
        this.attachmentOptionsLayoutParams.bottomMargin = 5;
        this.mRootViewLayoutParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;
        this.keypadLayout.setVisibility(View.GONE);
        if (this.fragmentKeypad != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.remove(this.fragmentKeypad);
            ft.commit();
        }
        this.mRootView.requestLayout();
    }

    private void showEmojiKeypad() {
        this.mRootViewLayoutParams.height = this.mRootView.getRootView().getHeight();
        this.mKeypadViewLayoutParams.height = this.mKeyBoardHeight + this.mTop;
        this.attachmentOptionsLayoutParams.bottomMargin = this.mKeyBoardHeight + this.mTop + 5;
        this.keypadLayout.setVisibility(View.VISIBLE);
        Utils.hideKeyboard(this, this.getCurrentFocus());
        this.fragmentKeypad = new EmojiconGridFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.viewKeypad, this.fragmentKeypad);
        ft.commit();
        this.mRootView.requestLayout();
    }

    private void goToCamera() {
        Utils.hideKeyboard(this, this.getCurrentFocus());
        Intent cameraIntent = new Intent(this, CameraActivity.class);
        cameraIntent.putExtra(Constants.DISABLE_VIDEO, true);
        startActivityForResult(cameraIntent, CAMERA_PICK_REQUEST);
    }

    private void goToGallery() {
        Utils.hideKeyboard(this, this.getCurrentFocus());
        Intent photoPickerIntent = new Intent(this, GalleryPickerActivity.class);
        startActivityForResult(photoPickerIntent, GALLERY_PICK_REQUEST);
    }

    private void goToVideos() {
        Utils.hideKeyboard(this, this.getCurrentFocus());
        Intent videoIntent = new Intent(this, VideoGallery.class);
        videoIntent.putExtra(Constants.FROM_SCREEN, Constants.ADD_CAMPUS_LIVE_POST_SCREEN);
        startActivityForResult(videoIntent, VIDEO_PICK_REQUEST);
    }

    private void pickFile() {
        Utils.hideKeyboard(this, this.getCurrentFocus());
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, FILE_PICK_REQUEST);
    }

    private void resetAttachment() {
        this.filePath = null;
        this.videoPath = null;
        this.fileView.setVisibility(View.GONE);
        this.imageView.setVisibility(View.GONE);
        this.videoView.setVisibility(View.GONE);
        this.imgPlayVideo.setVisibility(View.GONE);
        this.imgPauseVideo.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CAMERA_PICK_REQUEST) {
            if (data != null) {
                String imagePath = data.getStringExtra(Constants.PATH_IMAGE_CAPTURED);
                if (imagePath != null) {
                    this.resetAttachment();
                    this.filePath = imagePath;
                    this.imageView.setImageURI(Uri.parse(this.filePath));
                    this.imageView.setVisibility(View.VISIBLE);
                }
            } else {
                Log.e(TAG, "Failed to attach image");
            }
        } else if (requestCode == GALLERY_PICK_REQUEST) {
            if (data != null) {
                String imagePath = data.getStringExtra(Constants.SELECTED_IMAGE_PATHS);
                if (imagePath != null) {
                    this.resetAttachment();
                    this.filePath = imagePath;
                    this.imageView.setImageURI(Uri.parse(this.filePath));
                    this.imageView.setVisibility(View.VISIBLE);
                }
            }
        } else if (requestCode == VIDEO_PICK_REQUEST) {
            if (data != null) {
                String videoPath = data.getStringExtra(Constants.TRIMMED_VIDEO_PATH);
                if (videoPath != null) {
                    this.resetAttachment();
                    this.videoPath = videoPath;
                    this.loadVideoIntoView();
                }
            } else {
                Log.e(TAG, "No video to attach");
            }
        } else if (requestCode == FILE_PICK_REQUEST) {
            Uri result = data != null ? data.getData() : null;
            if (result != null) {
                String fileUrl = PathUtil.getPath(this, result);
                this.resetAttachment();
                this.setFileAttached(fileUrl);
            }
        }
        this.setPostButtonState();
    }

    /*private String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(column_index);
        cursor.close();
        return s;
    }*/

    private void setFileAttached(String filePath) {
        if (filePath == null) {
            return;
        }
        int beginIndex = filePath.lastIndexOf("/");
        String fileName = filePath.substring(beginIndex + 1);
        String extension = null;
        int extStartIndex = fileName.lastIndexOf(".");
        if (extStartIndex != -1 && extStartIndex < fileName.length() + 1) {
            extension = fileName.substring(extStartIndex + 1);
        }
        if (extension != null) {
            if (Constants.IMAGE_EXTENSIONS.contains(extension)) {
                this.filePath = filePath;
                this.imageView.setImageURI(Uri.parse(this.filePath));
                this.imageView.setVisibility(View.VISIBLE);
            } else if (Constants.VIDEO_EXTENSIONS.contains(extension)) {
                this.videoPath = filePath;
                this.loadVideoIntoView();
            } else {
                this.filePath = filePath;
                this.fileView.setVisibility(View.VISIBLE);
                this.fileView.setText(fileName);
            }
        } else {
            this.filePath = filePath;
            this.fileView.setVisibility(View.VISIBLE);
            this.fileView.setText(fileName);
        }
    }

    private void loadVideoIntoView() {
        if (this.videoPath == null) {
            return;
        }
        this.videoView.setVisibility(View.VISIBLE);
        this.imgPlayVideo.setVisibility(View.VISIBLE);

        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(this.videoPath);
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

        this.videoDataSender.setVideoHeight(vdHeight);
        this.videoDataSender.setVideoWidth(vdWidth);
        this.videoDataSender.setVideoPath(videoPath);
        this.videoDataSender.setVideo_duration(vduration);
    }

    private void setContentBackgroundColor(int index) {
        this.txtContent.setTextColor(Color.WHITE);
        this.txtContent.setHintTextColor(Color.WHITE);
        this.layoutContent.setBackgroundColor(
                Color.parseColor(Constants.CAMPUS_LIVE_COLOR_LIST.get(index)));
    }

    private void createPost() {
        FileDataSender.POST_TO = FileDataSender.POST_TO_CAMPUSLIVE;
        if (this.videoPath != null) {
            this.videoDataSender.setVideoDesc(this.txtContent.getText().toString());
            this.videoDataSender.setVideoView_to(String.valueOf(shareToSelected));
            this.videoDataSender.setIsCall(true);
            finish();
        } else if (this.filePath != null) {
            FileDataSender.HAS_FILE_TO_UPLOAD = true;
            FileDataSender.VIEW_TO = shareToSelected;
            FileDataSender.DESCRIPTION = this.txtContent.getText().toString();
            FileDataSender.FILE_PATH = this.filePath;
            FileDataSender.FILE_PATH_LIST = null;
            FileDataSender.CATEGORY_ID = 1;
            finish();
        } else {
            this.createTextPost();
        }
    }

    private void createTextPost() {
        CreatePostJsonInputModel inputModel = new CreatePostJsonInputModel();
        String userId = SharedPreferenceSingleton.getInstance().getStringPreferenceNull(RegistrationConstants.USER_ID);
        String token = SharedPreferenceSingleton.getInstance().getStringPreferenceNull(RegistrationConstants.TOKEN);
        inputModel.setPostCreatorId(userId);
        String content = this.txtContent.getText().toString();
        inputModel.setContent(content);
        inputModel.setType(4);
        inputModel.setCategorie_id(1);
        inputModel.setView_to(1);

        Apimethods methods = API_Call_Retrofit.getretrofit(this).create(Apimethods.class);
        Call<CreatePostJsonResponseModel> call = methods.setpost(inputModel, token);
        final String url = call.request().url().toString();
        call.enqueue(new Callback<CreatePostJsonResponseModel>() {
            @Override
            public void onResponse(Call<CreatePostJsonResponseModel> call, Response<CreatePostJsonResponseModel> response) {
                enablePostButton(true);
                if (response.code() == 200) {
                    Utils.showShortToast(AddCampusLivePostActivity.this, "Successfully updated");
                    setResult(RESULT_OK);
                    finish();
                } else if (response.code() == 401) {

                }
            }

            @Override
            public void onFailure(Call<CreatePostJsonResponseModel> call, Throwable t) {
                Log.d("Create post", "Failed url:" + url);
            }
        });
    }

    private void getIMGSize(String uri) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(uri, options);
        this.imageHeight = options.outHeight;
        this.imageWidth = options.outWidth;
    }

    // emoji click events
    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(this.txtContent, emojicon);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(this.txtContent);
    }

    @Override
    public void onBackPressed() {
        if (this.keypadLayout.getVisibility() == View.VISIBLE) {
            this.resetKeypadView();
            return;
        }
        super.onBackPressed();
    }
}
