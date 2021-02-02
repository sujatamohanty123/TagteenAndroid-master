package in.tagteen.tagteen.profile;

import android.app.Activity;
import android.graphics.Matrix;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import in.tagteen.tagteen.Model.MySelfyPostList;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.configurations.RequestConstants;
import in.tagteen.tagteen.networkEngine.AsyncResponse;
import in.tagteen.tagteen.networkEngine.AsyncWorker;
import in.tagteen.tagteen.selfyManager.models.MySelfylistModel;
import in.tagteen.tagteen.selfyManager.models.SelfyReactModel;
import in.tagteen.tagteen.util.CountDownTimer;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lovekushvishwakarma on 29/10/17.
 */

public class SelfyMaterView extends Activity implements AsyncResponse {

    CountDownTimer countDownTimer, countDownTimer2;
    boolean flag = false;
    ProgressBar progressTime;
    int premainingTime = 10 * 1000;

    ImageView mySelfyImage, imageSelfyAciton;
    private ScaleGestureDetector scaleGestureDetector;
    private Matrix matrix = new Matrix();

    ArrayList<MySelfyPostList.SelfyData> mySelfyList;
    int index = 0;
    String _id;
    ArrayList<MySelfylistModel> myselylist = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selfy_masterview);
        _id = getIntent().getStringExtra("_id");
        mySelfyList = new ArrayList<>();
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
        imageSelfyAciton = (ImageView) findViewById(R.id.imageSelfyAciton);
        mySelfyImage = (ImageView) findViewById(R.id.mySelfyImage);
        progressTime = (ProgressBar) findViewById(R.id.progressTime);
        progressTime.setMax(10 * 1000);

        // updateProgress();

        /*mySelfyImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_DOWN:
                        countDownTimer.pause();
                        countDownTimer2.pause();
                        break;
                    case MotionEvent.ACTION_UP:
                        countDownTimer.start();
                        countDownTimer2.start();

                        break;
                }
                return true;
            }
        });
*/
        // getAllSelfylist(_id);
        getAllreactions(_id);


        imageSelfyAciton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /// new ReactionOnMySelfy(SelfyMaterView.this);
            }
        });

    }

    private void updateProgress() {
        countDownTimer2 = new CountDownTimer(10 * 1000, 100) {

            public void onTick(long millisUntilFinished) {
                int current = progressTime.getProgress();
                progressTime.setProgress((current + 100));
                if (current >= 10 * 1000) {
                    index++;
                    countDownTimer.start();
                    updateProgress();
                    if (index == mySelfyList.size() - 1)
                        finish();
                }
            }

            public void onFinish() {
            }

        }.start();
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void ReceivedResponseFromServer(String output, String REQUEST_NUMBER) {
        System.out.print("" + output);
        Log.e(output, "");

        try {
            JSONObject jsonObject = new JSONObject(output);

            JSONArray data = jsonObject.getJSONArray("data");
            boolean success = jsonObject.getBoolean("success");
            if (success) {

                for (int i = 0; i < data.length(); i++) {
                    JSONObject innerdata = data.getJSONObject(i);
                    String date_created = innerdata.getString("date_created");
                    String last_date_updated = innerdata.getString("last_date_updated");
                    String post_creator_id = innerdata.getString("post_creator_id");
                    String post_type_id = innerdata.getString("post_type_id");
                    String title = innerdata.getString("title");
                    String _id = innerdata.getString("_id");
                    boolean is_selfie = innerdata.getBoolean("is_selfie");

                    ArrayList<SelfyReactModel> reactList = new ArrayList<>();
                    JSONArray action_type = innerdata.getJSONArray("action_type");
                    for (int j = 0; j < action_type.length(); j++) {
                        JSONObject innerActionType = action_type.getJSONObject(j);
                        String timestamp = innerActionType.getString("timestamp");
                        String user_id = innerActionType.getString("user_id");
                        String action_id = innerActionType.getString("_id");
                        reactList.add(new SelfyReactModel(action_id, timestamp, user_id));
                    }
                    JSONArray images = innerdata.getJSONArray("image");
                    JSONObject image = images.getJSONObject(0);
                    String url = image.getString("url");
                    String width = image.getString("width");
                    String image_id = image.getString("_id");

                    myselylist.add(new MySelfylistModel(date_created, last_date_updated, post_creator_id, post_type_id, title, _id, reactList, is_selfie, url));

                    Glide.with(SelfyMaterView.this).load(url).into(mySelfyImage);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class ScaleListener extends ScaleGestureDetector.
            SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5.0f));
            matrix.setScale(scaleFactor, scaleFactor);
            mySelfyImage.setImageMatrix(matrix);
            return true;
        }
    }

    private void getAllSelfylist(String _id) {
        SharedPreferenceSingleton.getInstance().init(SelfyMaterView.this);
        String userid = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
        String token = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TOKEN);
        Apimethods methods = API_Call_Retrofit.getretrofit(SelfyMaterView.this).create(Apimethods.class);
        Call<MySelfyPostList> call = methods.getMySelfyPost(_id, token);
        Log.d("url", "url=" + call.request().url().toString());
        call.enqueue(new Callback<MySelfyPostList>() {
            @Override
            public void onResponse(Call<MySelfyPostList> call, Response<MySelfyPostList> response) {
                int statuscode = response.code();
                if (statuscode == 200) {
                    MySelfyPostList getresponsemodel = response.body();
                    ArrayList<MySelfyPostList.SelfyData> getdatalist = (ArrayList<MySelfyPostList.SelfyData>) getresponsemodel.getSelfyData();

                    for (int i = 0; i < getdatalist.size(); i++) {
                        mySelfyList.add(getdatalist.get(i));
                    }
                }
            }

            @Override
            public void onFailure(Call<MySelfyPostList> call, Throwable t) {
                //Toast.makeText(getActivity(),"fail",Toast.LENGTH_SHORT).show();
            }
        });


    }

    void getAllreactions(String userid) {
        AsyncWorker mWorker = new AsyncWorker(SelfyMaterView.this);
        mWorker.delegate = this;
        mWorker.delegate = this;
        JSONObject BroadcastObject = new JSONObject();
        String url = "http://13.126.136.177/write/api/v1.0/user/get_my_selfie_post/59ef8c8a6aae307c07fad328/1";
        mWorker.execute(url, BroadcastObject.toString(), RequestConstants.GET_REQUEST, RequestConstants.HEADER_YES, RequestConstants.REQUEST_GET_SELFY_REACTION);
    }

}
