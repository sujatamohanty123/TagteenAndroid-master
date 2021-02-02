package in.tagteen.tagteen.Fragments.youthtube;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import in.tagteen.tagteen.R;

/**
 * Created by lovekushvishwakarma on 11/12/17.
 */

public class FullScreenVideoFragment extends Fragment {

    VideoView mvideoView;
    VideoDetailListActivity videoDetailListActivity;
    TextView textTimeupdate, textTotaltime;
    SeekBar seekabrview;
    ImageView makeVideofullscren;
    VideoView videoView;
    Runnable onEverySecond;

    public FullScreenVideoFragment(VideoView videoView, VideoDetailListActivity videoDetailListActivity) {
        this.mvideoView = videoView;
        this.videoDetailListActivity = videoDetailListActivity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.full_screen_fragment, null);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        videoView = (VideoView) root.findViewById(R.id.videoview);
        textTimeupdate = (TextView) root.findViewById(R.id.textTimeupdate);
        textTotaltime = (TextView) root.findViewById(R.id.textTotaltime);
        seekabrview = (SeekBar) root.findViewById(R.id.seekabrview);
        makeVideofullscren = (ImageView) root.findViewById(R.id.makeVideofullscren);
        makeVideofullscren.setImageResource(R.drawable.make_smallscreen);
        videoView = mvideoView;
        //videoView.start();


        makeVideofullscren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });



        return root;


    }


    @Override
    public void onResume() {

        super.onResume();

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {

                    // handle back button
                    getActivity().getSupportFragmentManager().popBackStack();
                    return true;

                }

                return false;
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        videoDetailListActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
    }
}
