package in.tagteen.tagteen.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.CacheUtil;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;

import java.io.IOException;
import java.util.ArrayList;

import in.tagteen.tagteen.R;
import in.tagteen.tagteen.util.Constants;

public class VideoPreLoadingService extends JobIntentService {
    private Context context;
    private SimpleCache simpleCache;
    private ArrayList<String> videosList;

    private static final int JOD_ID = 22;

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, VideoPreLoadingService.class, JOD_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.e("Test", "Service called");
        context = getApplicationContext();

        if (intent != null && intent.getExtras() != null) {
            this.videosList = intent.getStringArrayListExtra(Constants.VIDEOS_LIST);

            if (this.videosList != null && !this.videosList.isEmpty()) {
                this.preCacheVideos();
            }
        }
    }

    private void preCacheVideos() {
        if (this.videosList == null || this.videosList.isEmpty()) {
            return;
        }
        simpleCache = TagteenApplication.getSimpleCache();
        String videoUrl = this.videosList.remove(0);
        Uri videoUri = Uri.parse(videoUrl);
        DataSpec dataSpec = new DataSpec(videoUri);
        DataSource dataSource = new DefaultDataSourceFactory(
                this.context,
                Util.getUserAgent(this.context, getString(R.string.app_name))).createDataSource();
        this.cacheVideo(dataSpec, dataSource);
        this.preCacheVideos();
    }

    private void cacheVideo(DataSpec dataSpec, DataSource dataSource) {
        try {
            CacheUtil.CachingCounters counters = new CacheUtil.CachingCounters();
            CacheUtil.cache(dataSpec, this.simpleCache, dataSource, counters, null);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
