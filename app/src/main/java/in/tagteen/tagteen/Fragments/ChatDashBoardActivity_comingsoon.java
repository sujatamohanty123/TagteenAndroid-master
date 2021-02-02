package in.tagteen.tagteen.Fragments;

import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import in.tagteen.tagteen.R;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.util.Constants;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;

public class ChatDashBoardActivity_comingsoon extends Fragment {
    private ImageView imgIcon1;
    private ImageView imgIcon2;
    private TextView lblTitle2;
    private ImageView imgIcon3;
    private TextView lblTitle3;
    private ImageView imgIcon4;
    private TextView lblTitle4;
    private ImageView imgIcon5;
    private TextView lblTitle5;

    private String loggedInUserFirstName;
    private boolean isInfoExpanded;

    private MediaPlayer mediaPlayer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat_dash_board_activity_comingsoon, container, false);

        this.loggedInUserFirstName = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.FIRST_NAME);
        this.isInfoExpanded = SharedPreferenceSingleton.getInstance().getBoolPreference(Constants.CHAT_INFO_EXPANDED);

        this.initWidgets(view);
        this.bindEvents();
        return view;
    }

    private void initWidgets(View view) {
        this.imgIcon1 = view.findViewById(R.id.imgIcon1);
        this.imgIcon2 = view.findViewById(R.id.imgIcon2);
        this.lblTitle2 = view.findViewById(R.id.lblTitle2);
        this.imgIcon3 = view.findViewById(R.id.imgIcon3);
        this.lblTitle3 = view.findViewById(R.id.lblTitle3);
        this.imgIcon4 = view.findViewById(R.id.imgIcon4);
        this.lblTitle4 = view.findViewById(R.id.lblTitle4);
        this.imgIcon5 = view.findViewById(R.id.imgIcon5);
        this.lblTitle5 = view.findViewById(R.id.lblTitle5);

        if (this.isInfoExpanded) {
            this.imgIcon2.setVisibility(View.VISIBLE);
            this.lblTitle2.setVisibility(View.VISIBLE);
            this.imgIcon3.setVisibility(View.VISIBLE);
            this.lblTitle3.setVisibility(View.VISIBLE);
            this.imgIcon4.setVisibility(View.VISIBLE);
            this.lblTitle4.setVisibility(View.VISIBLE);
            this.imgIcon5.setVisibility(View.VISIBLE);
            this.lblTitle5.setVisibility(View.VISIBLE);
        }
    }

    private void stopPlaying() {
        if (this.mediaPlayer != null) {
            this.mediaPlayer.stop();
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        }
    }

    private void startPlaying() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    private void bindEvents() {
        this.imgIcon1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopPlaying();
                mediaPlayer = MediaPlayer.create(getActivity(), R.raw.poo);
                startPlaying();

                lblTitle2.setVisibility(View.VISIBLE);
                imgIcon2.setVisibility(View.VISIBLE);
            }
        });
        this.imgIcon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopPlaying();
                mediaPlayer = MediaPlayer.create(getActivity(), R.raw.lol);
                startPlaying();

                lblTitle3.setVisibility(View.VISIBLE);
                imgIcon3.setVisibility(View.VISIBLE);
            }
        });
        this.imgIcon3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopPlaying();
                mediaPlayer = MediaPlayer.create(getActivity(), R.raw.oh_yeah);
                startPlaying();

                String str = "It's young, fun &amp; made for you <b>" + loggedInUserFirstName + "</b>";
                lblTitle4.setText(Html.fromHtml(str));
                lblTitle4.setVisibility(View.VISIBLE);
                imgIcon4.setVisibility(View.VISIBLE);
            }
        });
        this.imgIcon4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferenceSingleton.getInstance().writeBoolPreference(Constants.CHAT_INFO_EXPANDED, true);
                stopPlaying();
                mediaPlayer = MediaPlayer.create(getActivity(), R.raw.happiness_with_love);
                startPlaying();

                lblTitle5.setVisibility(View.VISIBLE);
                imgIcon5.setVisibility(View.VISIBLE);
            }
        });
        this.imgIcon5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopPlaying();
                mediaPlayer = MediaPlayer.create(getActivity(), R.raw.applause);
                startPlaying();
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //Write down your refresh code here, it will call every time user come to this fragment.
            //If you are using listview with custom adapter, just call notifyDataSetChanged().

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
