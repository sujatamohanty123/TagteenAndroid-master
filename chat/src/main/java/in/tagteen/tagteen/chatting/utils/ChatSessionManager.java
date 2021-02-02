package in.tagteen.tagteen.chatting.utils;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import in.tagteen.tagteen.chatting.socket.ChatProcessLifecycleManager;

public class ChatSessionManager {


    //pri
//    public static final String SENDER_ID = "5cc94bef34d2b82c290e40b2";
//    public static final String TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJtb2JpbGUiOiI5MDA4NDk2ODQ2In0.Gjnx-owwbx4Fbv2WUKMi_QAZyOczMcPoX4G_fzwP064";


//    //Tony
//    public static final String SENDER_ID = "5c6777e66a7781160cfd0e88";
//    public static final String TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJtb2JpbGUiOiI5NzUxNTAzNzAxIn0.8jBpu2F6mt1HtOEA5naFkzIIdc0aNC-XW5wlSptamH8";

    //Antony
//    private static final String SENDER_ID = "5c6779346a7781160cfd0e93";
//    private static final String TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJtb2JpbGUiOiI4NjY3NDU0NzI1In0.Qe4FHCn8B6hsJ5SN_9HBiaw5TwkutUhIPokZrVXLQOY";

    private static final String PREFERENCE = ChatSessionManager.class.getCanonicalName() + ".Preference";
    private static final String KEY_SENDER_ID = ChatSessionManager.class.getCanonicalName() + ".SenderId";
    private static final String KEY_TOKEN = ChatSessionManager.class.getCanonicalName() + ".Token";

    private static ChatSessionManager instance;
    private static SharedPreferences preferences;

    private ChatSessionManager() {
        preferences = ChatProcessLifecycleManager.getApplicationContext()
                .getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
    }

    @NonNull
    public synchronized static ChatSessionManager getInstance() {
        if (instance == null)
            instance = new ChatSessionManager();
        return instance;
    }

    private static SharedPreferences.Editor getEditor() {
        return preferences.edit();
    }

    //tony00
    public void initiateSession(@NonNull String userId, @NonNull String token){
        putSenderId(userId);
        putToken(token);
    }

    public void putSenderId(
            @NonNull String value) {
        getEditor().putString(KEY_SENDER_ID, value).commit();
    }

    @Nullable
    public String getSenderId() {
        return preferences.getString(KEY_SENDER_ID, null);
    }

    public void putToken(
            @NonNull String value) {
        getEditor().putString(KEY_TOKEN, value).commit();
    }

    @Nullable
    public String getToken() {
        return preferences.getString(KEY_TOKEN, null);
    }

    public void clear(){
        getEditor().clear().commit();
    }
}
