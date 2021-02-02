package in.tagteen.tagteen.Fragments;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.alexvasilkov.gestures.views.GestureImageView;

import in.tagteen.tagteen.R;
import in.tagteen.tagteen.util.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class SimpleImagePreviewFragment extends Fragment {
    private Bitmap bitmap;

    public SimpleImagePreviewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.item, container, false);
        GestureImageView imageView = (GestureImageView) itemView.findViewById(R.id.imageView);
        ProgressBar loadingSpinner = (ProgressBar) itemView.findViewById(R.id.loading_spinner);

        byte[] byteArray = getArguments().getByteArray(Constants.IMAGE_BITMAP);
        if (byteArray != null) {
            this.bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        }
        if (this.bitmap != null) {
            loadingSpinner.setVisibility(View.GONE);
            imageView.setImageBitmap(this.bitmap);
        }

        return itemView;
    }
}
