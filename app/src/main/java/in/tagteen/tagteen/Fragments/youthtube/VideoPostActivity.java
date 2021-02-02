package in.tagteen.tagteen.Fragments.youthtube;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.VideoView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import in.tagteen.tagteen.AntonyChanges;
import in.tagteen.tagteen.Fragments.VideoDataSender1;
import in.tagteen.tagteen.Fragments.youthtube.adapter.CategoryBean;
import in.tagteen.tagteen.Fragments.youthtube.adapter.CategorySpinnerAdapter;
import in.tagteen.tagteen.Fragments.youthtube.adapter.LanguageSpinnerAdapter;
import in.tagteen.tagteen.GallaryPicker.GalleryPickerActivity;
import in.tagteen.tagteen.Interfaces.OnConfirmDialogListener;
import in.tagteen.tagteen.MainDashboardActivity;
import in.tagteen.tagteen.Model.Language;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.VideoEditor.StartActivity;
import in.tagteen.tagteen.configurations.RequestConstants;
import in.tagteen.tagteen.configurations.ServerConnector;
import in.tagteen.tagteen.networkEngine.AsyncResponse;
import in.tagteen.tagteen.networkEngine.AsyncWorker;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by lovekushvishwakarma on 01/10/17.
 */

public class VideoPostActivity extends AppCompatActivity
    implements View.OnClickListener, AsyncResponse {

  private static final int CATEGORY_SELECT = -1;
  private ProgressBar progressBar;
  long totalSize = 0;
  private ImageView imageback, imageUpload;
  private VideoView videoView;
  private Spinner videoCategory;
  private Spinner videoLanguage;
  private EditText edtDesc;
  private EditText etTitle;
  private float lastAngle = 0;
  boolean is_animated = false;
  private ArrayList<CategoryBean> categoryList;
  String filePath = "", actual_path = "";
  public int vdHeight = 0, vdWidth = 0;
  private VideoDataSender1 dataSedataSender;
  private CategorySpinnerAdapter spinnerAdapter;
  private LanguageSpinnerAdapter mLanguageSpinnerAdapter;
  private ImageView imgThumbnailPlaceholder;
  private ImageView imgThumbnailSelect;
  private FrameLayout framCategory;

  private ArrayList<String> languagesList;
  private Map<String, Integer> languagesMap;

  private static final int GALLERY_PICK_REQUEST = 2;
  int flag=0;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.video_post_activity);
    dataSedataSender = new VideoDataSender1();
    this.dataSedataSender.setThumbnailPath(null);
    progressBar = (ProgressBar) findViewById(R.id.progressBar);
    progressBar.setMax(100);
    imageback = (ImageView) findViewById(R.id.imageback);
    imageUpload = (ImageView) findViewById(R.id.imagerUplaod);
    videoView = (VideoView) findViewById(R.id.videoView);
    videoCategory = (Spinner) findViewById(R.id.spinnerCategory);
    this.videoLanguage = findViewById(R.id.spinLanguage);
    edtDesc = (EditText) findViewById(R.id.edtDesc);
    etTitle = (EditText) findViewById(R.id.et_title);
    framCategory = (FrameLayout) findViewById(R.id.framCategory);
    this.imgThumbnailPlaceholder = findViewById(R.id.imgThumbnailPlaceholder);
    this.imgThumbnailSelect = findViewById(R.id.imgThumbnailSelect);

    categoryList = new ArrayList<>();
    this.initLanguages();

    imageUpload.setOnClickListener(this);
    imageback.setOnClickListener(this);

    Intent extraIntent = getIntent();
    if (extraIntent != null) {
      filePath = extraIntent.getStringExtra(Constants.TRIMMED_VIDEO_PATH);
      flag= extraIntent.getIntExtra("flag",0);

      int duration = 0;
      try {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(filePath);
        if (retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH) != null) {
          vdWidth = Integer.valueOf(
              retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        }
        if (retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT) != null) {
          vdHeight = Integer.valueOf(
              retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        }
        if (retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION) != null) {
          duration = Integer.valueOf(
              retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        }
        retriever.release();
      } catch (Exception e) {
        // do nothing
      }
      dataSedataSender.setVideoHeight(vdHeight);
      dataSedataSender.setVideoWidth(vdWidth);
      dataSedataSender.setVideoPath(filePath);
      dataSedataSender.setVideo_duration(duration);
      Log.e("VideoUpload", "Height:" + vdHeight + " Width:" + vdWidth);
    }

    if (extraIntent != null) {
      // this is only for when we want to goback, than need to send path for Trimview
      actual_path = extraIntent.getStringExtra("actual_path");
    }

    MediaController myMediaController = new MediaController(VideoPostActivity.this);
    videoView.setMediaController(myMediaController);

    videoView.setVideoURI(Uri.parse(filePath));
    videoView.start();

    edtDesc.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override
      public void afterTextChanged(Editable editable) {
                /*if (editable.length() > 10) {
                    lastAngle = -90f;
                    is_animated = true;
                    fulfillRequirment(lastAngle);
                } else if (lastAngle == -90f) {
                    lastAngle = 0f;
                    fulfillRequirment(lastAngle);
                    is_animated = false;
                }*/
      }
    });
    this.imgThumbnailSelect.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        goToGallery();
      }
    });
    this.imgThumbnailPlaceholder.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        goToGallery();
      }
    });

    videoCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
               /* if (i % 2 == 0) {
                    framCategory.setBackgroundColor(getResources().getColor(R.color.white));
                } else {
                    framCategory.setBackgroundColor(getResources().getColor(R.color.white));
                }*/
      }

      @Override
      public void onNothingSelected(AdapterView<?> adapterView) {

      }
    });
    getAllCategories();
  }

  private void initLanguages() {
    this.languagesMap = new HashMap<>();
    List<Language> languages = Utils.getVideoPostLanguages();
    this.languagesList = new ArrayList<String>();
    for (Language lang : languages) {
      this.languagesMap.put(lang.getLanguage(), lang.getId());
      this.languagesList.add(lang.getLanguage());
    }

    //Collections.sort(this.languagesList);
        /*this.languagesList.add("Assamese");
        this.languagesList.add("Bengali");
        this.languagesList.add("English");
        this.languagesList.add("Gujarati");
        this.languagesList.add("Hindi");
        this.languagesList.add("Kannada");
        this.languagesList.add("Kashmiri");
        this.languagesList.add("Konkani");
        this.languagesList.add("Malayalam");
        this.languagesList.add("Manipuri");
        this.languagesList.add("Marathi");
        this.languagesList.add("Oriya");
        this.languagesList.add("Punjabi");
        this.languagesList.add("Sanskrit");
        this.languagesList.add("Tamil");
        this.languagesList.add("Telugu");*/
    mLanguageSpinnerAdapter =
        new LanguageSpinnerAdapter(VideoPostActivity.this, R.layout.only_textvew, languagesList);
    videoLanguage.setAdapter(mLanguageSpinnerAdapter);
    mLanguageSpinnerAdapter.notifyDataSetChanged();
  }

  private void goToGallery() {
    Utils.hideKeyboard(this, this.getCurrentFocus());
    Intent photoPickerIntent = new Intent(this, GalleryPickerActivity.class);
    startActivityForResult(photoPickerIntent, GALLERY_PICK_REQUEST);
  }

  private void getAllCategories() {
    AsyncWorker mWorker = new AsyncWorker(this);
    mWorker.delegate = this;
    mWorker.delegate = VideoPostActivity.this;
    JSONObject BroadcastObject = new JSONObject();
    mWorker.execute(
        ServerConnector.REQUEST_GET_ALL_CATEGORIES,
        BroadcastObject.toString(),
        RequestConstants.GET_REQUEST,
        RequestConstants.HEADER_YES,
        RequestConstants.REQUEST_GET_CATEGORIES);
  }

  private void addYouthTubePost() {
    AsyncWorker mWorker = new AsyncWorker(this);
    mWorker.delegate = this;
    mWorker.delegate = VideoPostActivity.this;
    JSONObject BroadcastObject = new JSONObject();
    JSONObject imagejson = new JSONObject();

    //imagejson.put("url",)

    //mWorker.execute(ServerConnector.REQUEST_ADD_POST, BroadcastObject.toString(), RequestConstants.POST_REQUEST, RequestConstants.HEADER_YES, RequestConstants.REQUEST_GET_CATEGORIES);
  }

  @Override
  @AntonyChanges
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.imageback:
        goback();
        break;
      case R.id.imagerUplaod:
        String desc = edtDesc.getText().toString();
        int pos = videoCategory.getSelectedItemPosition();
        int videoLanguagePosition = videoLanguage.getSelectedItemPosition();
        int languageId = CATEGORY_SELECT;
        int category_id = CATEGORY_SELECT;
        /*if (desc.trim().length() == 0) {
          this.edtDesc.requestFocus();
          Utils.showAlertDialog(this, "Please provide description", "Alert");
          return;
        }*/

        //UnComment below lines to validate title is empty
        //} else if (etTitle.getText().toString().isEmpty()) {
        //  this.etTitle.requestFocus();
        //  Utils.showAlertDialog(this, "Please provide title", "Alert");
        //  return;
        //}

        if (this.categoryList != null && !this.categoryList.isEmpty() &&
            pos >= 0 && pos < this.categoryList.size() && this.categoryList.get(pos) != null) {
          category_id = this.categoryList.get(pos).getId();
        }

        if (this.languagesList != null
            && !this.languagesList.isEmpty()
            && this.languagesMap != null && this.languagesMap.size() > 0 &&
            videoLanguagePosition >= 0
            && videoLanguagePosition < this.languagesList.size()
            && this.languagesList.get(videoLanguagePosition) != null) {
          languageId = this.languagesMap.get(
              this.languagesList.get(videoLanguagePosition));
        }

        if (category_id == CATEGORY_SELECT) {
          Utils.showAlertDialog(VideoPostActivity.this,
              "Please choose a category", "Alert");
          return;
        } else if (languageId == CATEGORY_SELECT) {
          Utils.showAlertDialog(VideoPostActivity.this,
              "Please choose a language", "Alert");
          return;
        } else if (validation(category_id + "", desc)) {
          //showInfoToPost(category_id, languageId, desc, etTitle.getText().toString().trim());
          this.postVideo(category_id, languageId, desc);
        }
        break;
    }
  }

  private void postVideo(final int category_id, final int languageId, final String desc) {
    dataSedataSender.setVideoCategoryId(category_id + "");
    dataSedataSender.setLanguageId(languageId + "");
    //UnComment the below line to pass title value
    //dataSedataSender.setVideoTitle(title);
    dataSedataSender.setIsCall(true);
    dataSedataSender.setVideoDesc(desc);
    imageUpload.setEnabled(false);
    finish();
  }

  private void showInfoToPost(final int category_id, final int languageId, final String desc,
      String title) {
    String msg =
        "Please DO NOT upload tiktok or musically videos, only original and genuine talent videos of your own are allowed and rewarded in Showroom.";
    Utils.showConfirmationDialog(this, msg, "Upload", new OnConfirmDialogListener() {
      @Override
      public void onConfirmation() {
        postVideo(category_id, languageId, desc);
      }

      @Override
      public void onCancelled() {
        // do nothing
      }
    });
  }

  private boolean validation(String category_id, String desc) {
    if (category_id == null || category_id.equalsIgnoreCase("")) {
      return false;
    }
    return true;
  }

  private void goback() {
    if (flag==0) {
      Intent goback = new Intent(this, VideoTrimmerActivity.class);
      goback.putExtra(VideoGallery.EXTRA_VIDEO_PATH, actual_path);
      goback.putExtra("Video_pick", "0");
      startActivity(goback);
      finish();
    }else {
      dialog_show();
    }
  }

  private void dialog_show() {
    AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
    builder1.setMessage("Do You Want Discard To Upload?");
    builder1.setCancelable(true);

    builder1.setPositiveButton(
            "Yes",
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                Intent intent = new Intent(VideoPostActivity.this, MainDashboardActivity.class);
                startActivity(intent);
              }
            });

    builder1.setNegativeButton(
            "No",
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
              }
            });

    AlertDialog alert11 = builder1.create();
    alert11.show();
  }

  private void fulfillRequirment(float angle) {
    final OvershootInterpolator interpolator = new OvershootInterpolator();
    ViewCompat.animate(imageUpload)
        .rotation(angle)
        .withLayer()
        .setDuration(600)
        .setInterpolator(interpolator)
        .start();
  }

  @Override
  public void onRefresh() {

  }

  @AntonyChanges
  @Override
  public void ReceivedResponseFromServer(String output, String REQUEST_NUMBER) {
    try {
      JSONObject data = new JSONObject(output);
      if (data.getBoolean("success")) {
        categoryList.add(new CategoryBean(-1, "Select"));
        JSONArray dataArr = data.getJSONArray("data");
        for (int i = 0; i < dataArr.length(); i++) {
          JSONObject object = dataArr.getJSONObject(i);
          String categoryName = object.getString(Constants.CATEGORY_NAME_PARAM);
          int categoryId = object.getInt(Constants.CATEGORY_ID_PARAM);
          if (!categoryName.equalsIgnoreCase("All")) {
            categoryList.add(new CategoryBean(categoryId, categoryName));
          }
        }
        spinnerAdapter =
            new CategorySpinnerAdapter(VideoPostActivity.this, R.layout.only_textvew, categoryList);
        videoCategory.setAdapter(spinnerAdapter);
        spinnerAdapter.notifyDataSetChanged();
      } else {

      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == GALLERY_PICK_REQUEST) {
      if (data != null) {
        String imagePath = data.getStringExtra(Constants.SELECTED_IMAGE_PATHS);
        if (imagePath != null) {
          this.dataSedataSender.setThumbnailPath(imagePath);
          this.imgThumbnailSelect.setVisibility(View.GONE);
          this.imgThumbnailPlaceholder.setImageURI(Uri.parse(imagePath));
          this.imgThumbnailPlaceholder.setVisibility(View.VISIBLE);
        } else {
          Utils.showShortToast(this, "Couldn't select the image");
        }
      }
    }
  }

  @Override
  public void onBackPressed() {
    goback();
  }
}
