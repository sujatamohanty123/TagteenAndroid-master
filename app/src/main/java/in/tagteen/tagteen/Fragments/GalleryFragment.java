package in.tagteen.tagteen.Fragments;

import android.app.Activity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.tagteen.tagteen.R;



public class GalleryFragment extends Fragment {
    private Activity mCurrentActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View  rootView = inflater.inflate(R.layout.tab_fragment, container, false);
        return rootView;

    }

}