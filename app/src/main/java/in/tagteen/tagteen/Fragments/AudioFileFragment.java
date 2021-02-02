package in.tagteen.tagteen.Fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;

import in.tagteen.tagteen.Adapters.AudioFileListAdapter;
import in.tagteen.tagteen.Model.SongMediaModel;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.TagteenInterface.AudioClickList;
import in.tagteen.tagteen.TagteenInterface.SendAudioFile;

import java.util.ArrayList;
import java.util.List;


public class AudioFileFragment extends Fragment {

    private List<SongMediaModel> songs = new ArrayList<SongMediaModel>();
    Animation alpha;
    TabLayout tabLayout;
    ImageView imageView;
    RecyclerView recyclerView;
    AudioFileListAdapter audioFileListAdapter;
    SendAudioFile sendAudioFile;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_audio_file, container, false);
        sendAudioFile=(SendAudioFile) getActivity();
        recyclerView=(RecyclerView)rootView.findViewById(R.id.audio_recyview);
        recyclerView.setHasFixedSize(true);
        songFile();
        audioFileListAdapter=new AudioFileListAdapter(getActivity(), songs, new AudioClickList() {
            @Override
            public void clickedMusicList(String path, String name) {
                sendAudioFile.sendAudio(path);

            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(audioFileListAdapter);






        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onStop() {
        super.onStop();

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    public void songFile() {

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION
        };

        Cursor cursor = getActivity().managedQuery(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null);

        while (cursor.moveToNext()) {
            SongMediaModel dashboardData = new SongMediaModel();
            dashboardData.setmSongID(cursor.getString(0));
            dashboardData.setmArtist(cursor.getString(1));
            dashboardData.setmSongTitle(cursor.getString(2));
            dashboardData.setmSongPath(cursor.getString(4));
            dashboardData.setmDuration(cursor.getString(5));
            songs.add(dashboardData);
             }
    }
}
