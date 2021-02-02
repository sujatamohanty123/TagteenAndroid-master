package in.tagteen.tagteen.helpers;

import android.content.Context;

import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSink;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;

import in.tagteen.tagteen.utils.TagteenApplication;

public class CacheDataSourceFactory implements DataSource.Factory {
    private final Context context;
    private final DefaultDataSourceFactory defaultDataSourceFactory;
    private static final long MAX_FILE_SIZE = 100 * 1024 * 1024;

    public CacheDataSourceFactory(Context context) {
        super();
        this.context = context;
        String userAgent = "TagTeen";
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        this.defaultDataSourceFactory = new DefaultDataSourceFactory(
                this.context,
                bandwidthMeter,
                new DefaultHttpDataSourceFactory(userAgent, bandwidthMeter));
    }

    @Override
    public DataSource createDataSource() {
        SimpleCache simpleCache = TagteenApplication.getSimpleCache();
        return new CacheDataSource(
                simpleCache,
                defaultDataSourceFactory.createDataSource(),
                new FileDataSource(),
                new CacheDataSink(simpleCache, MAX_FILE_SIZE),
                CacheDataSource.FLAG_BLOCK_ON_CACHE | CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR, null);
    }
}
