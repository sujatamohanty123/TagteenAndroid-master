package in.tagteen.tagteen.utils;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;

import com.bokecc.camerafilter.LocalVideoFilter;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;

import im.ene.toro.exoplayer.Config;
import im.ene.toro.exoplayer.ExoCreator;
import im.ene.toro.exoplayer.MediaSourceBuilder;
import im.ene.toro.exoplayer.ToroExo;
import in.tagteen.tagteen.ConnectivityReceiver;
import in.tagteen.tagteen.NotificationUtils;
import in.tagteen.tagteen.chatting.socket.ChatProcessLifecycleManager;
import in.tagteen.tagteen.configurations.ServerConnector;
import in.tagteen.tagteen.workers.Selfie;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;
import io.agora.openlive.AgoraApplication;
import io.socket.client.IO;
import io.socket.client.Socket;
import java.io.File;
import java.net.URISyntaxException;

//import in.tagteen.tagteen.util.Constants;

//import com.github.tcking.giraffecompressor.GiraffeCompressor;

public class TagteenApplication extends Application {

  private static TagteenApplication instance;
  public static volatile Handler applicationHandler = null;
  private boolean mIsSocketsEnabled = true;
  public Socket mSocket;
  public static Context context;

  private static SimpleCache simpleCache;
  private static LeastRecentlyUsedCacheEvictor leastRecentlyUsedCacheEvictor;
  private static long exoPlayerCacheSize = 90 * 1024 * 1024;
  private static ExoCreator exoCreator;
  private static Config config;

  // agora
    /*private RtcEngine mRtcEngine;
    private EngineConfig mGlobalConfig = new EngineConfig();
    private AgoraEventHandler mHandler = new AgoraEventHandler();
    private StatsManager mStatsManager = new StatsManager();*/

  @Override
  public void onCreate() {
    super.onCreate();
    instance = this;
    context = getApplicationContext();

    LocalVideoFilter.init(this);
    //tony00
    createNotificationChannel();
    ChatProcessLifecycleManager.createInstance(getApplicationContext()).initialize();

    applicationHandler = new Handler(getInstance().getMainLooper());
    Selfie.initWithDefaults();
    SharedPreferenceSingleton.getInstance().init(this);

    if (leastRecentlyUsedCacheEvictor == null) {
      leastRecentlyUsedCacheEvictor = new LeastRecentlyUsedCacheEvictor(exoPlayerCacheSize);
    }

    if (simpleCache == null) {
      simpleCache = new SimpleCache(getApplicationContext().getCacheDir(), leastRecentlyUsedCacheEvictor);
    }

    config = new Config.Builder().setMediaSourceBuilder(MediaSourceBuilder.LOOPING).setCache(simpleCache).build();
    exoCreator = ToroExo.with(getApplicationContext()).getCreator(config);
    //GiraffeCompressor.init(this);

    // TestVideoBroadcasting - 6dce755001784d5ab89895b26bbd591b
    // VideoCasting LS - 277d22699f914bd5beedbea251e813e5
        /*try {
            mRtcEngine = RtcEngine.create(getApplicationContext(), getString(R.string.agora_app_id), mHandler);
            mRtcEngine.setChannelProfile(io.agora.rtc.Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
            mRtcEngine.enableVideo();
            mRtcEngine.setLogFile(FileUtil.initializeLogFile(this));
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.initConfig();*/
  }
    /*private void initConfig() {
        mGlobalConfig.setChannelName("abc");
        mGlobalConfig.setVideoDimenIndex(
                SharedPreferenceSingleton.getInstance().getMyPreference().getInt(
                        Constants.PREF_RESOLUTION_IDX, Constants.DEFAULT_PROFILE_IDX));

        boolean showStats = SharedPreferenceSingleton.getInstance().getBoolPreference(Constants.PREF_ENABLE_STATS);
        mGlobalConfig.setIfShowVideoStats(showStats);
        mStatsManager.enableStats(showStats);
    }

    public EngineConfig engineConfig() { return mGlobalConfig; }

    public RtcEngine rtcEngine() { return mRtcEngine; }

    public StatsManager statsManager() { return mStatsManager; }

    public void registerEventHandler(EventHandler handler) { mHandler.addHandler(handler); }

    public void removeEventHandler(EventHandler handler) { mHandler.removeHandler(handler); }*/

    /*public static TagteenApplication getApp() {
        return instance;
    }*/

  public static Context getContext() {
    return context;
  }

  //@Override
  //    //protected void attachBaseContext(Context base) {
  //    //    super.attachBaseContext(base);
  //    //    MultiDex.install(this);
  //    //    //SugarContext.init(base);
  //    //}

  public static TagteenApplication getInstance() {
    return instance;
  }

  public static SimpleCache getSimpleCache() {
    return simpleCache;
  }

  public static ExoCreator getExoCreator() {
    return exoCreator;
  }

  public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
    ConnectivityReceiver.connectivityReceiverListener = listener;
  }

  {
    if (mIsSocketsEnabled) {
      try {
        mSocket = IO.socket(ServerConnector.BASE_URL);
      } catch (URISyntaxException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public Socket getSocket() {
    return mSocket;
  }

  public boolean isSocketsEnabled() {
    return mIsSocketsEnabled;
  }

  private void deleteCache(Context context) {
    try {
      File dir = context.getCacheDir();
      deleteDir(dir);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private boolean deleteDir(File dir) {
    if (dir != null && dir.isDirectory()) {
      String[] children = dir.list();
      for (int i = 0; i < children.length; i++) {
        boolean success = deleteDir(new File(dir, children[i]));
        if (!success) {
          return false;
        }
      }
      return dir.delete();
    } else if (dir != null && dir.isFile()) {
      return dir.delete();
    } else {
      return false;
    }
  }

  //tony00
  private void createNotificationChannel() {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      CharSequence name = NotificationUtils.CHANNEL_NAME;
      String description = NotificationUtils.CHANNEL_DESC;
      int importance = NotificationManager.IMPORTANCE_HIGH;
      NotificationChannel channel =
          new NotificationChannel(NotificationUtils.CHANNEL_ID, name, importance);
      channel.setDescription(description);
      // Register the channel with the system; you can't change the importance
      // or other notification behaviors after this
      NotificationManager notificationManager =
          getContext().getSystemService(NotificationManager.class);
      notificationManager.createNotificationChannel(channel);
    }
  }

  @Override
  public void onTrimMemory(int level) {
    super.onTrimMemory(level);
    if (level >= TRIM_MEMORY_BACKGROUND) ToroExo.with(this).cleanUp();
  }

  @Override
  public void onTerminate() {
    super.onTerminate();
    //RtcEngine.destroy();
    AgoraApplication.terminate();
  }

}