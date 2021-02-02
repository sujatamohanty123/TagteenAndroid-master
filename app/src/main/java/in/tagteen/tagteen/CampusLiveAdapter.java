package in.tagteen.tagteen;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import in.tagteen.tagteen.Fragments.ImagePreviewFragment;
import in.tagteen.tagteen.Interfaces.OnCallbackListener;
import in.tagteen.tagteen.Interfaces.OnLoadMoreListener;
import in.tagteen.tagteen.Model.SectionDataModel;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;

public class CampusLiveAdapter extends RecyclerView.Adapter {
  private static final String TAG = "CampusLiveAdapter";
  private Activity context;
  private List<SectionDataModel> models;

  private final int VIEW_TYPE_ITEM = 0;
  private final int VIEW_TYPE_LOADING = 1;
  private OnLoadMoreListener onLoadMoreListener;
  private boolean isLoading;
  private int visibleThreshold = 5;
  private int lastVisibleItem, totalItemCount;
  private File downloadedFile;
  private String loggedInUserId;
  private int selectedIndex = -1;

  private ProgressDialog dialog;
  private PostActionListener postActionListener;
  private OnCallbackListener onCallbackListener;

  public CampusLiveAdapter(Activity context, List<SectionDataModel> models,
      RecyclerView recyclerView) {
    this.context = context;
    this.models = models;
    this.loggedInUserId =
        SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);

    final LinearLayoutManager linearLayoutManager =
        (LinearLayoutManager) recyclerView.getLayoutManager();
    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        totalItemCount = linearLayoutManager.getItemCount();
        lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
        if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
          if (onLoadMoreListener != null) {
            new Handler().postDelayed(new Runnable() {
              @Override
              public void run() {
                onLoadMoreListener.onLoadMore();
              }
            }, 200);
          }
          isLoading = true;
        }
      }
    });
  }

  public void setPostActionListener(PostActionListener postActionListener) {
    this.postActionListener = postActionListener;
  }

  public void setOnCallbackListener(OnCallbackListener onCallbackListener) {
    this.onCallbackListener = onCallbackListener;
  }

  public void refreshSelectedModel(SectionDataModel model) {
    if (model != null &&
        this.selectedIndex != -1 &&
        this.selectedIndex < this.models.size()) {
      SectionDataModel selectedModel = this.models.get(this.selectedIndex);
      selectedModel.setCommentcount(model.getCommentcount());
      selectedModel.setReactsCount(model.getReactsCount());
      notifyItemChanged(this.selectedIndex, selectedModel);
    }
  }

  public void setLoaded() {
    isLoading = false;
  }

  public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
    this.onLoadMoreListener = onLoadMoreListener;
  }

  @Override
  public int getItemViewType(int position) {
    return this.models.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (viewType == VIEW_TYPE_ITEM) {
      View view = LayoutInflater.from(this.context).inflate(R.layout.campus_live_row, null);
      return new ViewHolder(view);
    } else if (viewType == VIEW_TYPE_LOADING) {
      View view = LayoutInflater.from(this.context).inflate(R.layout.load_more, parent, false);
      return new LoadingViewHolder(view);
    }
    return null;
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int i) {
    if (holder instanceof ViewHolder) {
      final SectionDataModel model = this.models.get(i);
      final ViewHolder viewHolder = (ViewHolder) holder;

      int index = i % Constants.CAMPUS_LIVE_COLOR_LIST.size();
      String colorCode = Constants.CAMPUS_LIVE_COLOR_LIST.get(index);
      viewHolder.layoutContent.setBackgroundColor(Color.parseColor(colorCode));
      if (model.getText_description() != null && model.getText_description().trim().length() > 0) {
        viewHolder.lblContent.setVisibility(View.VISIBLE);
        viewHolder.lblContent.setText(Html.fromHtml(model.getText_description()));
      } else {
        viewHolder.lblContent.setVisibility(View.GONE);
      }
      try {
        viewHolder.lblPostedAgo.setText(
            Utils.getRelativeTime(model.getPost_created_date_time()));
      } catch (Exception e) {
        Log.e(TAG, e.getMessage());
      }
      viewHolder.lblPostedBy.setText(model.getPost_creator_name());

      String profilePicUrl = model.getOwner_post_creator_profilepic();
      if (profilePicUrl == null || profilePicUrl.trim().length() == 0) {
        profilePicUrl = model.getPost_creator_profilepic();
      }
      if (profilePicUrl != null) {
        Utils.loadProfilePic(context, viewHolder.imgUserProfile, profilePicUrl);
      }

      // bind profile
      viewHolder.lblPostedBy.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Utils.gotoProfile(context, model.getPost_userid());
        }
      });
      viewHolder.imgUserProfile.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Utils.gotoProfile(context, model.getPost_userid());
        }
      });

      // replies
      String replySuffix = " replies";
      int commentsCount = model.getCommentcount();
            /*if (commentsCount == 0 || commentsCount == 1) {
                replySuffix = " reply";
            }*/
      viewHolder.lblRepliesCount.setText(commentsCount + replySuffix);
      final int position = holder.getAdapterPosition();
      viewHolder.lblRepliesCount.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          moveToComments(position, model, 0);
        }
      });
      viewHolder.lblReply.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          moveToComments(position, model, 1);
        }
      });
      viewHolder.moreOption.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          showPopupMenu(viewHolder.moreOption, position, model.getPost_userid());
        }
      });

      viewHolder.lblAttachedFilename.setVisibility(View.GONE);
      if (model.getPost_image_createdby_creator_url() != null &&
          model.getPost_image_createdby_creator_url().isEmpty() == false) {
        String filePath = model.getPost_image_createdby_creator_url().get(0);
        int beginIndex = filePath.lastIndexOf("/");
        final String fileName = filePath.substring(beginIndex + 1);
        String extension = null;
        int extStartIndex = fileName.lastIndexOf(".");
        if (extStartIndex != -1 && extStartIndex < fileName.length() + 1) {
          extension = fileName.substring(extStartIndex + 1);
        }
        if (extension != null) {
          if (Constants.IMAGE_EXTENSIONS.contains(extension)) {
            viewHolder.layoutPostAttachment.setVisibility(View.VISIBLE);
            viewHolder.imgPost.setVisibility(View.VISIBLE);
            viewHolder.imgFileAttachment.setVisibility(View.GONE);
            viewHolder.imgPlayVideoBackground.setVisibility(View.GONE);
            viewHolder.imgPlayVideo.setVisibility(View.GONE);
            Glide.with(context)
                .load(model.getPost_image_createdby_creator_url().get(0))
                .thumbnail(0.15f)
                .listener(new RequestListener<Drawable>() {
                  @Override
                  public boolean onLoadFailed(@Nullable GlideException e, Object model,
                      Target<Drawable> target, boolean isFirstResource) {
                    viewHolder.loadingSpinner.setVisibility(View.GONE);
                    return false;
                  }

                  @Override
                  public boolean onResourceReady(Drawable resource, Object model,
                      Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    viewHolder.loadingSpinner.setVisibility(View.GONE);
                    return false;
                  }
                })
                .fitCenter()
                .into(viewHolder.imgPost);
            viewHolder.imgPost.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                previewImage(model, viewHolder.imgPost);
              }
            });
          } else {
            viewHolder.layoutPostAttachment.setVisibility(View.VISIBLE);
            viewHolder.imgPost.setVisibility(View.GONE);
            viewHolder.imgFileAttachment.setVisibility(View.VISIBLE);
            viewHolder.imgPlayVideoBackground.setVisibility(View.GONE);
            viewHolder.imgPlayVideo.setVisibility(View.GONE);
            viewHolder.loadingSpinner.setVisibility(View.GONE);
            viewHolder.lblAttachedFilename.setVisibility(View.VISIBLE);

            viewHolder.lblAttachedFilename.setText(fileName);
            if (extension.equalsIgnoreCase(Constants.PDF)) {
              viewHolder.imgFileAttachment.setImageResource(R.drawable.pdf_icon);
            } else if (extension.equalsIgnoreCase(Constants.DOC)) {
              viewHolder.imgFileAttachment.setImageResource(R.drawable.doc_icon);
            } else if (extension.equalsIgnoreCase(Constants.XLS) ||
                extension.equalsIgnoreCase(Constants.XLSX)) {
              viewHolder.imgFileAttachment.setImageResource(R.drawable.xls_icon);
            } else if (Constants.AUDIO_EXTENSIONS.contains(extension)) {
              viewHolder.imgFileAttachment.setImageResource(R.drawable.music_icon);
            } else {
              viewHolder.imgFileAttachment.setImageResource(R.drawable.ic_file_icon_campus_live);
              viewHolder.imgFileAttachment.setColorFilter(
                  context.getResources().getColor(R.color.white));
            }

            // download and view file
            viewHolder.imgFileAttachment.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                downloadFile(model.getPost_image_createdby_creator_url().get(0), fileName);
              }
            });
          }
        }
      } else if (model.getPost_video_thumb_createdby_creator() != null &&
          model.getPost_video_thumb_createdby_creator().isEmpty() == false) {
        viewHolder.layoutPostAttachment.setVisibility(View.VISIBLE);
        viewHolder.imgPost.setVisibility(View.VISIBLE);
        viewHolder.imgFileAttachment.setVisibility(View.GONE);
        viewHolder.imgPlayVideoBackground.setVisibility(View.VISIBLE);
        viewHolder.imgPlayVideo.setVisibility(View.VISIBLE);
        Glide.with(context)
            .load(model.getPost_video_thumb_createdby_creator().get(0))
            .listener(new RequestListener<Drawable>() {
              @Override
              public boolean onLoadFailed(@Nullable GlideException e, Object model,
                  Target<Drawable> target, boolean isFirstResource) {
                viewHolder.loadingSpinner.setVisibility(View.GONE);
                return false;
              }

              @Override
              public boolean onResourceReady(Drawable resource, Object model,
                  Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                viewHolder.loadingSpinner.setVisibility(View.GONE);
                return false;
              }
            })
            .fitCenter()
            .into(viewHolder.imgPost);
        viewHolder.imgPost.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Intent it = new Intent(context, MomentFeedVideoPlay.class);
            it.putExtra("postid", model.getPostid());
            it.putExtra("post_creater_id", model.getPost_userid());
            context.startActivity(it);
          }
        });
      } else {
        viewHolder.layoutPostAttachment.setVisibility(View.GONE);
      }
    }
  }

  @Override
  public int getItemCount() {
    return models != null ? models.size() : 0;
  }

  private void previewImage(SectionDataModel model, ImageView imageView) {
    if (model == null || model.getPost_image_createdby_creator_url() == null || imageView == null) {
      return;
    }
    if (this.onCallbackListener != null) {
      this.onCallbackListener.OnComplete();
    }

    Bundle bundle = new Bundle();
    bundle.putSerializable("postlistdata", model);
//    imageView.setDrawingCacheEnabled(true);
//    Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
    Bitmap bitmap = Utils.getBitmapFromImageView(imageView);
    //Bitmap bitmap = imageView.getDrawingCache();
    bundle.putParcelable(Constants.IMAGE_BITMAP, bitmap);

    Fragment fragment = new ImagePreviewFragment();
    fragment.setArguments(bundle);
    FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.setCustomAnimations(R.anim.frag_fade_in, R.anim.frag_fade_out,
        R.anim.frag_fade_in, R.anim.frag_fade_out);
    fragmentTransaction.replace(R.id.main_content, fragment);
    fragmentTransaction.addToBackStack(null);
    fragmentTransaction.commit();
  }

  private void moveToComments(int position, SectionDataModel data, int showKeypad) {
    this.selectedIndex = position;
    Intent intent = new Intent(this.context, CommentLikeActivity_new.class);
    Bundle bundle = new Bundle();
    bundle.putSerializable(Constants.SELECTED_MODEL, data);
    bundle.putInt(Constants.SELECT_COMMENT_LIKE, Constants.FLAG_COMMENT);
    bundle.putInt(Constants.SHOW_KEYPAD, showKeypad);
    bundle.putString(Constants.POST_ID, data.getPostid());
    bundle.putBoolean(Constants.BLOCK_REACTS, true);
    bundle.putString(Constants.COMMENTS_TYPE, Constants.POST_TYPE_CAMPUSLIVE);
    //bundle.putString("react_type", "");
    bundle.putInt(Constants.COMMENTS_COUNT, data.getCommentcount());
    intent.putExtras(bundle);
    context.startActivityForResult(intent, Constants.COMMENT_REQUEST_CODE);
  }

  private void downloadFile(String fileUrl, String fileName) {
    if (Utils.isNetworkAvailable(this.context) == false) {
      Utils.showShortToast(this.context, "Please check your network connection");
      return;
    }
    if (fileUrl == null || fileUrl.trim().length() == 0) {
      Utils.showShortToast(this.context, "Incorrect url");
      return;
    }

    String downloadURL = Utils.constructDownloadURL(fileUrl);
    String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
    File folder = new File(extStorageDirectory, Constants.APP_FOLDER_NAME);
    if (folder.exists() == false) {
      folder.mkdir();
    }

    this.downloadedFile = new File(folder, fileName);
    try {
      this.downloadedFile.createNewFile();
    } catch (IOException e) {
      e.printStackTrace();
    }
    this.downloadFileInBackground(downloadURL);
  }

  private void downloadFileInBackground(final String fileUrl) {
    new AsyncTask<Void, Void, Void>() {
      @Override
      protected void onPreExecute() {
        dialog = new ProgressDialog(context);
        dialog.setMessage("Downloading file");
        dialog.show();
      }

      @Override
      protected Void doInBackground(Void... voids) {
        try {
          FileOutputStream fout = new FileOutputStream(downloadedFile);
          URL url = new URL(fileUrl);
          HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
          con.connect();

          InputStream is = con.getInputStream();
          byte[] buffer = new byte[1024];

          int len = 0;
          while ((len = is.read(buffer)) > 0) {
            fout.write(buffer, 0, len);
          }
          fout.close();
          is.close();
        } catch (Exception e) {
          e.printStackTrace();
          downloadedFile = null;
        }
        return null;
      }

      @Override
      protected void onPostExecute(Void aVoid) {
        if (dialog.isShowing()) {
          dialog.dismiss();
        }
        viewDownloadedFile();
      }
    }.execute();
  }

  private void viewDownloadedFile() {
    if (this.downloadedFile == null) {
      Utils.showShortToast(context, "Download failed");
      return;
    }
    Uri data = FileProvider.getUriForFile(
        this.context, BuildConfig.APPLICATION_ID + ".provider", this.downloadedFile);
    Intent intent = new Intent(Intent.ACTION_VIEW);
    MimeTypeMap map = MimeTypeMap.getSingleton();
    String ext = MimeTypeMap.getFileExtensionFromUrl(data.toString());
    String mimeType = map.getMimeTypeFromExtension(ext);
    if (mimeType == null) {
      mimeType = "*/*";
    }
    intent.setDataAndType(data, mimeType);
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    try {
      this.context.startActivity(intent);
    } catch (Exception e) {
      Utils.showShortToast(this.context, "No application to view this file");
    }
  }

  private void showPopupMenu(View view, final int position, String postUserId) {
    PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
    MenuInflater inflater = popupMenu.getMenuInflater();
    Menu menu = popupMenu.getMenu();

    if (this.loggedInUserId.equalsIgnoreCase(postUserId)) {
      inflater.inflate(R.menu.popup_menu_delete, menu);
    } else {
      inflater.inflate(R.menu.popup_menu_report, menu);
    }
    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
      @Override
      public boolean onMenuItemClick(MenuItem menuItem) {
        if (postActionListener != null) {
          postActionListener.onDeleteOrReportPost(position);
        }
        return false;
      }
    });
    popupMenu.show();
  }

  private class ViewHolder extends RecyclerView.ViewHolder {
    private RelativeLayout layoutContent;
    private TextView lblContent;
    private ImageButton imgUserProfile;
    private TextView lblPostedAgo;
    private TextView lblPostedBy;
    private TextView lblRepliesCount;
    private TextView lblReply;

    private ImageView moreOption;
    private RelativeLayout layoutPostAttachment;
    private ImageView imgPost;
    private ImageView imgFileAttachment;
    private TextView lblAttachedFilename;
    private ImageView imgPlayVideo;
    private ImageView imgPlayVideoBackground;
    private ProgressBar loadingSpinner;

    public ViewHolder(View view) {
      super(view);

      this.layoutContent = (RelativeLayout) view.findViewById(R.id.layoutContent);
      this.lblContent = (TextView) view.findViewById(R.id.lblContent);
      this.imgUserProfile = (ImageButton) view.findViewById(R.id.imgUserProfile);
      this.lblPostedAgo = (TextView) view.findViewById(R.id.lblPostedAgo);
      this.lblPostedBy = (TextView) view.findViewById(R.id.lblPostedBy);
      this.lblRepliesCount = (TextView) view.findViewById(R.id.lblRepliesCount);
      this.lblReply = (TextView) view.findViewById(R.id.lblReply);

      this.moreOption = (ImageView) view.findViewById(R.id.moreOption);
      this.layoutPostAttachment = (RelativeLayout) view.findViewById(R.id.layoutPostAttachment);
      this.imgPost = (ImageView) view.findViewById(R.id.imgPost);
      this.imgFileAttachment = (ImageView) view.findViewById(R.id.imgFileAttachment);
      this.lblAttachedFilename = (TextView) view.findViewById(R.id.lblAttachedFilename);
      this.imgPlayVideo = (ImageView) view.findViewById(R.id.imgPlayVideo);
      this.imgPlayVideoBackground = (ImageView) view.findViewById(R.id.imgPlayVideoBackground);
      this.loadingSpinner = (ProgressBar) view.findViewById(R.id.loadingSpinner);
    }
  }

  private class LoadingViewHolder extends RecyclerView.ViewHolder {
    public ProgressBar progressBar;

    public LoadingViewHolder(View view) {
      super(view);
      progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
    }
  }

  public interface PostActionListener {
    void onDeleteOrReportPost(int position);
  }
}
