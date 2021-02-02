package in.tagteen.tagteen.chatting.socket;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;

public class NetworkFactory {

    private static Gson gson = new GsonBuilder().create();

    private NetworkFactory() throws IllegalAccessException {
        throw new IllegalAccessException("Cannot instantiate the class NetworkFactory");
    }

    @Nullable
    public static String serializeJsonString(@NonNull Object data) {
//        getExecutor().submit((Callable<String>) () -> {
                String input = gson.toJson(data);
                System.out.println("Request Input Data : " + input);
                return input;
//        });
//        return null;
    }

    @Nullable
    public static JSONObject serializeJson(@NonNull Object data) {
        try {
            return new JSONObject(serializeJsonString(data));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static <T> T deserializeJson(@NonNull JSONObject obj,
                                        @NonNull Class<T> unMarshaller) {
//        getExecutor().submit((Callable<T>) () -> {
            try {
                return (T) gson.fromJson(obj.toString(), unMarshaller);
            } catch (Exception e) {
                e.printStackTrace();
            }
//            return null;
//        });
        return null;
    }


    @Nullable
    public static <T> T deserializeJson(@NonNull JSONArray obj,
                                        @NonNull Class<T> unMarshaller) {
//        getExecutor().submit((Callable<T>) () -> {
        try {
            return (T) gson.fromJson(obj.toString(), unMarshaller);
        } catch (Exception e) {
            e.printStackTrace();
        }
//            return null;
//        });
        return null;
    }

    @Nullable
    public static <T> T deserializeJson(@NonNull JSONArray obj,
                                        @NonNull Type unMarshaller) {
        try {
            return (T) gson.fromJson(obj.toString(), unMarshaller);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
