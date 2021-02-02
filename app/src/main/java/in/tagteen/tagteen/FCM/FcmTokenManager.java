package in.tagteen.tagteen.FCM;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import in.tagteen.tagteen.Model.FCMTokenUpdateModel;
import in.tagteen.tagteen.Model.SimpleActionModel;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.configurations.RegistrationConstants;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by tony00 on 8/4/2019.
 */
//tony00
public class FcmTokenManager {

    private static final String TAG = FcmTokenManager.class.getSimpleName();

    private Context context;
    private OnFcmTokenUpdatedListener onFcmTokenUpdatedListener;

    public FcmTokenManager(@NonNull Context context,
        @Nullable OnFcmTokenUpdatedListener listener){
        this.context = context;
        this.onFcmTokenUpdatedListener = listener;
    }

    public static FcmTokenManager createFrom(@NonNull Context context,
                                             @Nullable OnFcmTokenUpdatedListener listener){
        return new FcmTokenManager(context, listener);
    }

    public static FcmTokenManager createFrom(@NonNull Context context){
        return new FcmTokenManager(context, null);
    }

    public void saveFcmToken(@Nullable String token) {
        if(TextUtils.isEmpty(token)) return;

        Apimethods methods = API_Call_Retrofit.getretrofit(context).create(Apimethods.class);

        String loggedInUserId = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.USER_ID);
        String sessionToken = SharedPreferenceSingleton.getInstance().getStringPreference(RegistrationConstants.TOKEN);

        if (TextUtils.isEmpty(loggedInUserId)) return;

        Log.d(TAG, "Updating GCM Registration Refreshed Token @FCMService:" + loggedInUserId);

        FCMTokenUpdateModel model = new FCMTokenUpdateModel();
        model.setUserId(loggedInUserId);
        model.setFcmToken(token);
        Call<SimpleActionModel> call = methods.updateFCMToken(sessionToken, model);
        call.enqueue(new Callback<SimpleActionModel>() {
            @Override
            public void onResponse(Call<SimpleActionModel> call, Response<SimpleActionModel> response) {
                SimpleActionModel model = response.body();

                if (model == null) {
                    Log.d(TAG, "GCM Registration Refreshed Token Update Failure:");
                } else {
                    Log.d(TAG, "GCM Registration Refreshed Token Updated @LOGIN:" + model.getSuccess().toString());
                }

                if(onFcmTokenUpdatedListener != null)
                    onFcmTokenUpdatedListener.onFcmTokenUpdated();
            }

            @Override
            public void onFailure(Call<SimpleActionModel> call, Throwable t) {
                Log.d(TAG, "GCM Registration Refreshed Token Update Faiure:" + t.getMessage());
            }
        });
    }

    public void saveFcmTokenInPreference(@Nullable String token) {
        if(TextUtils.isEmpty(token)) return;

        SharedPreferences pref = context.getSharedPreferences(Config.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        editor.apply();
    }

    public interface OnFcmTokenUpdatedListener{
        void onFcmTokenUpdated();
    }
}
