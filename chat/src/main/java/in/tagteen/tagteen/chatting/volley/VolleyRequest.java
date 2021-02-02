package in.tagteen.tagteen.chatting.volley;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import in.tagteen.tagteen.chatting.socket.ChatProcessLifecycleManager;

/**
 * Created by Tony on 12/24/2017.
 */

public class VolleyRequest {

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    public VolleyRequest() {
        initImageLoader();
    }

    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null)
            mRequestQueue = Volley.newRequestQueue(ChatProcessLifecycleManager.getApplicationContext());
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request, int tag) {
        request.setTag(tag);
        getRequestQueue().add(request);
    }

    private void initImageLoader() {
        final LruCache<String, Bitmap> cache = new LruCache<>(100);
        mImageLoader = new ImageLoader(getRequestQueue(), new ImageLoader.ImageCache() {
            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    public void cancelRequest(Object tag) {
        if (tag != null)
            getRequestQueue().cancelAll(tag);
    }
}
