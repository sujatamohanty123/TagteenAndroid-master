package in.tagteen.tagteen.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;
import in.tagteen.tagteen.GalleryActivity;
import in.tagteen.tagteen.GalleryActivity_new;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.backgroundUpload.UrlUtils;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.util.Utils;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mathivanan on 11-04-2017.
 */

public class Profile_photos_Adapter
    extends RecyclerView.Adapter<Profile_photos_Adapter.ViewHolder> {
  private Context mContext;
  private List<String> imageList;

  public Profile_photos_Adapter(Context context, List<String> img) {
    this.mContext = context;
    this.imageList = img;
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    public ImageView imageView;

    public ViewHolder(View v) {
      super(v);
      this.imageView = (ImageView) v.findViewById(R.id.img);
    }
  }

  @Override
  public Profile_photos_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view1 =
        LayoutInflater.from(mContext).inflate(R.layout.fragment_profile_photos_row, parent, false);
    ViewHolder viewHolder1 = new ViewHolder(view1);
    return viewHolder1;
  }

  @Override
  public void onBindViewHolder(final ViewHolder holder, final int position) {
    Utils.loadImageUsingGlideCenterCrop(
        this.mContext, holder.imageView,
        UrlUtils.getUpdatedImageUrl(this.imageList.get(position), "large"));

    holder.imageView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(mContext, GalleryActivity_new.class);
        Bitmap bitmap = holder.imageView.getDrawingCache();
        intent.putExtra(Constants.IMAGE_BITMAP, bitmap);
        intent.putExtra(Constants.SELETED_IMAGE_INDEX, position);
        intent.putStringArrayListExtra(GalleryActivity.EXTRA_NAME, (ArrayList<String>) imageList);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
      }
    });
  }

  @Override
  public int getItemCount() {
    return imageList.size();
  }
}