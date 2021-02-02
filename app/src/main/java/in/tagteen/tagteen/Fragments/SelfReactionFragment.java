package in.tagteen.tagteen.Fragments;



import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.yalantis.ucrop.UCrop;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import in.tagteen.tagteen.Adapters.FacereactionAdapter;
import in.tagteen.tagteen.LocalCasha.DataBaseHelper;
import in.tagteen.tagteen.LocalCasha.DatabaseContracts;
import in.tagteen.tagteen.Model.FaceReactionModel;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.TagteenInterface.AWSFaceractionResponse;
import in.tagteen.tagteen.TagteenInterface.ChatImageInterface;
import in.tagteen.tagteen.TagteenInterface.FacereactionInterface;
import in.tagteen.tagteen.configurations.AWSUtility;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import static android.app.Activity.RESULT_OK;

public class SelfReactionFragment extends Fragment {
    GridLayoutManager lLayout;
    FacereactionAdapter adapter;
    Uri imageUri;
    SQLiteDatabase db;
    DataBaseHelper dbHelper;
    String selectedReaction="1234";
    List<FaceReactionModel> reaction = new ArrayList<>();
    ChatImageInterface chatImageInterface;
    private String reactionList[]={"Happy","Sad","Crazy","Angry","Surprised","Excited","In Love","Confused",
            "Sleepy","Scared","In Pain","Exhausted","Frustrated","Bored","Cool","Disgust","Shame","Serious"};
    String rr="https://s3.ap-south-1.amazonaws.com/ttprofileurl/Self_Reaction/Happy.png";
    RecyclerView reactionListView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_self_reaction, container, false);
        chatImageInterface =(ChatImageInterface) getActivity();
      /*  dbHelper= new DataBaseHelper(getActivity());
        db = dbHelper.getWritableDatabase();*/
         reactionListView = (RecyclerView) rootView.findViewById(R.id.faceReaction_recyclerView);
        setMyDatabase();
        lLayout = new GridLayoutManager(getActivity(), 3);
        reactionListView.setHasFixedSize(true);
        reactionListView.setLayoutManager(lLayout);
        adapter = new FacereactionAdapter(getActivity(),reaction, new FacereactionInterface() {
            @Override
            public void sendReaction(String image) {
                chatImageInterface.selfReaction(image);
            }

            @Override
            public void SetReaction(String reaction, int position) {
                takePhoto(reaction,position);
            }
        });
        reactionListView.setAdapter(adapter);
        return rootView;

    }

    public void takePhoto(String reaction,int position) {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        File photo = new File(Environment.getExternalStorageDirectory(), reaction+".jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(photo));
        intent.putExtra("position",position);
        imageUri = Uri.fromFile(photo);
        SelfReactionFragment.this.startActivityForResult(intent, 100);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 100) {
                Uri selectedImage = imageUri;
                Uri destinationUri = Uri.fromFile(new File(getActivity().getCacheDir(), "IMG_" + System.currentTimeMillis()+".jpg"));
                startActivityForResult(UCrop.of(selectedImage, destinationUri)
                        .withAspectRatio(1,1)
                        .withMaxResultSize(300, 300)
                        .getIntent(getContext()),UCrop.REQUEST_CROP);
            }else if (requestCode == UCrop.REQUEST_CROP) {

                handleCropResult(data);
            }else{
                Toast.makeText(getActivity(),"toast_cannot_retrieve_selected_image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handleCropResult(@NonNull Intent result) {
        final Uri resultUri = UCrop.getOutput(result);
        if (resultUri != null) {
            File file = new File(resultUri.getPath());
            Mediaupload(file, selectedReaction);
        } else {
            Toast.makeText(getActivity(),"toast_cannot_retrieve_selected_image", Toast.LENGTH_SHORT).show();
        }
    }


    public void Mediaupload(File file, final String reaction) {

        SharedPreferenceSingleton.getInstance().init(getActivity());
        String UserId  = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
        AWSUtility.uploadFaceReactionImage(file, getActivity(),UserId,reaction, new AWSFaceractionResponse() {
            @Override
            public void sentReaction(String url, String Reaction) {
            boolean isdone= updateReactionCache(reaction, url);
                if(isdone){
                    updateUIFromCache();
                }
            }
            @Override
            public void onUploadReaction(String url, String Reaction, int percent) {
                Toast.makeText(getActivity(),"onUploadReaction",Toast.LENGTH_LONG);
            }
            @Override
            public void onerror(String url, String Reaction) {
                Toast.makeText(getActivity(),"onUploadReaction",Toast.LENGTH_LONG);
            }
        });
    }

    private boolean insertReactionMessage(String id, String name, String url, String localPath){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContracts.FaceReactionDataBase.ID , id);
        contentValues.put(DatabaseContracts.FaceReactionDataBase.REACTION_NAME, name);
        contentValues.put(DatabaseContracts.FaceReactionDataBase.REACTON_AWS_URL, url);
        contentValues.put(DatabaseContracts.FaceReactionDataBase.REACTION_lOCAL_PATH, localPath);
        db.insert(DatabaseContracts.FaceReactionDataBase.REACTION_TABLE, null, contentValues);
        return true;
    }

    private boolean updateReactionCache(String Name, String Url) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContracts.FaceReactionDataBase.REACTON_AWS_URL, Url);
        db.update(DatabaseContracts.FaceReactionDataBase.REACTION_TABLE, contentValues,DatabaseContracts.FaceReactionDataBase.REACTION_NAME +"= ? ", new String[]{Name});

        return true;
    }

    public void updateUIFromCache() {
        reaction.clear();
        Cursor res = db.rawQuery("SELECT * FROM "+DatabaseContracts.FaceReactionDataBase.REACTION_TABLE , null);
        res.moveToFirst();
        while (!res.isAfterLast()) {
            FaceReactionModel facereaction = new FaceReactionModel();
            facereaction.setId(res.getString(res.getColumnIndex(DatabaseContracts.FaceReactionDataBase.ID)));
            facereaction.setReactionName(res.getString(res.getColumnIndex(DatabaseContracts.FaceReactionDataBase.REACTION_NAME)));
            facereaction.setReactionLocalPath(res.getString(res.getColumnIndex(DatabaseContracts.FaceReactionDataBase.REACTION_lOCAL_PATH)));
            facereaction.setReactionURL(res.getString(res.getColumnIndex(DatabaseContracts.FaceReactionDataBase.REACTON_AWS_URL)));
            reaction.add(facereaction);
            res.moveToNext();
        }
        res.close();
        reactionListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void setMyDatabase(){
       /* Cursor res = db.rawQuery("select * from "+DatabaseContracts.FaceReactionDataBase.REACTION_TABLE, null);
        if(res.getCount() > 0){
            for (int index = 0; index < reactionList.length; index++) {
                    insertBroadcastMessage(""+index+1 ,reactionList[index],rr ,null);
            }
            updateUIFromCache();
        }*/
        for (int index = 0; index < reactionList.length; index++) {

            reaction.add( new FaceReactionModel("" +index+1,reactionList[index],rr,null));
            reactionListView.setAdapter(adapter);
        }
    }


}
