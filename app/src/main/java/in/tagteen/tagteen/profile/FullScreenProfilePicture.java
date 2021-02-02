package in.tagteen.tagteen.profile;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.alexvasilkov.gestures.views.GestureImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.backgroundUpload.UrlUtils;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class FullScreenProfilePicture extends Fragment {
  private GestureImageView imageUserPIc;

  public FullScreenProfilePicture() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_full_screen_profile_picture, container, false);
    imageUserPIc = (GestureImageView) view.findViewById(R.id.imageUserPIc);
    final ProgressBar loading_spinner = (ProgressBar) view.findViewById(R.id.loading_spinner);
    Bundle bundle = this.getArguments();
    if (bundle != null) {
      String url = bundle.getString("ImageURL");
      Glide.with(Objects.requireNonNull(getContext()))
          .load(UrlUtils.getUpdatedImageUrl(url, "large")) // add your image url
          .listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                Target<Drawable> target, boolean isFirstResource) {
              loading_spinner.setVisibility(View.GONE);
              return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
                DataSource dataSource, boolean isFirstResource) {
              loading_spinner.setVisibility(View.GONE);
              return false;
            }
          })
          .fitCenter()
          .into(imageUserPIc);
    }
    return view;
  }
}
