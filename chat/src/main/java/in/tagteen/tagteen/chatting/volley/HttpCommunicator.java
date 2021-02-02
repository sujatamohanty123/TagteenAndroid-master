package in.tagteen.tagteen.chatting.volley;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.tagteen.tagteen.chatting.socket.NetworkFactory;

/**
 * Created by Tony on 12/24/2017.
 */

public class HttpCommunicator {

    private final String TAG = HttpCommunicator.class.getSimpleName();

    private VolleyResponse.ResponseListener mResponseListener;
    private VolleyRequest volleyRequest;

    public HttpCommunicator() {
        this(null);
    }

    public HttpCommunicator(
            @Nullable VolleyResponse.ResponseListener responseListener) {
        mResponseListener = responseListener;
        volleyRequest = new VolleyRequest();
    }

    public <T> void addRequest(@NonNull RequestConfiguration<T> config,
                               @NonNull String token) {
        Request request = getJsonObjectRequest(config.getUrl(),
                config.getTag(),
                config.getPayload(),
                config.getDataUnMarshaller(),
                token);
        volleyRequest.addToRequestQueue(request, config.getTag());
    }

    public <T> void addGetRequest(@NonNull RequestConfiguration<T> config,
                                  @NonNull String token) {
        Log.i(TAG, "Requesting " + config.getUrl());
        final JsonObjectRequest jReq = new JsonObjectRequest(Request.Method.GET,
                config.getUrl(),
                null,
                (JSONObject response) -> {
                    Log.d(TAG, "Response : " + response.toString());
                    try {
                        String success = response.getString("success");
                        if (success.equalsIgnoreCase("true")) {
                            RequestResponse<T> data = new RequestResponse(config.getTag(),
                                    NetworkFactory.deserializeJson(response.getJSONObject("data"),
                                            config.getDataUnMarshaller()), null);
                            if (getResponseListener() != null)
                                getResponseListener().onDataResponse(data);
                        } else {
                            RequestResponse<T> data = new RequestResponse(config.getTag(),
                                    NetworkFactory.deserializeJson(response.getJSONObject("data"),
                                            config.getDataUnMarshaller()), null);
                            if (getResponseListener() != null)
                                getResponseListener().onDataResponse(data);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        RequestResponse<T> responseError = new RequestResponse(config.getTag(),
                                null, new VolleyError(e.getMessage()));
                        if (getResponseListener() != null)
                            getResponseListener().onDataResponse(responseError);
                    }
                },
                (VolleyError error) -> {
                    Log.d(TAG, "Response error" + error);
                    RequestResponse<T> response = new RequestResponse(config.getTag(),
                            null, error);
                    if (getResponseListener() != null)
                        getResponseListener().onDataResponse(response);
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Content-Type", "application/json");
                header.put("token", token);
                return header;
            }
        };
        volleyRequest.addToRequestQueue(jReq, config.getTag());
    }

    private <T> JsonObjectRequest getJsonObjectRequest(@NonNull String url,
                                                       final int tag,
                                                       @Nullable Object requestBody,
                                                       @Nullable final Class<T> dataUnMarshaller,
                                                       @NonNull String token) {
        Log.d(TAG, "Executing " + url);
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                url,
                NetworkFactory.serializeJson(requestBody),
                (JSONObject response) -> {
                    Log.d(TAG, "Response : " + response.toString());
                    try {
                        String success = response.getString("success");
                        if (success.equalsIgnoreCase("true")) {
                            RequestResponse<T> data = null;
                            if (dataUnMarshaller != null)
                                data = new RequestResponse(tag,
                                        NetworkFactory.deserializeJson(response.getJSONObject("data"), dataUnMarshaller), null);
                            if (getResponseListener() != null)
                                getResponseListener().onDataResponse(data);
                        } else {
                            RequestResponse<T> data = null;
                            if (dataUnMarshaller != null)
                                data = new RequestResponse(tag,
                                        NetworkFactory.deserializeJson(response.getJSONObject("data"),
                                                dataUnMarshaller), null);
                            if (getResponseListener() != null)
                                getResponseListener().onDataResponse(data);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        RequestResponse<T> responseError = new RequestResponse(tag,
                                null, new VolleyError(e.getMessage()));
                        if (getResponseListener() != null)
                            getResponseListener().onDataResponse(responseError);
                    }
                },
                (VolleyError error) -> {
                    RequestResponse<T> response = new RequestResponse(tag,
                            null, error);
                    if (getResponseListener() != null)
                        getResponseListener().onDataResponse(response);
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Content-Type", "application/json");
                header.put("token", token);
                Log.d(TAG, "getHeaders: "+header.toString());
                return header;
            }
        };
        return jsonObjectRequest;
    }

    public void setResponseListener(VolleyResponse.ResponseListener mResponseListener) {
        this.mResponseListener = mResponseListener;
    }

    private VolleyResponse.ResponseListener getResponseListener() throws ResponseNotFoundException {
//        if (mResponseListener == null)
//            throw new ResponseNotFoundException();
        return mResponseListener;
    }

    public void loadImage(String url, final ImageView imageView) {
        volleyRequest.getImageLoader().get(url, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                imageView.setImageBitmap(response.getBitmap());
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

}
