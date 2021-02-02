package io.agora.openlive;

import android.content.Context;
import android.content.SharedPreferences;
import io.agora.openlive.rtc.AgoraEventHandler;
import io.agora.openlive.rtc.EngineConfig;
import io.agora.openlive.rtc.EventHandler;
import io.agora.openlive.stats.StatsManager;
import io.agora.openlive.utils.FileUtil;
import io.agora.openlive.utils.PrefManager;
import io.agora.rtc.RtcEngine;

public class AgoraApplication {
  private RtcEngine mRtcEngine;
  private EngineConfig mGlobalConfig = new EngineConfig();
  private AgoraEventHandler mHandler = new AgoraEventHandler();
  private StatsManager mStatsManager = new StatsManager();

  private static AgoraApplication instance;
  private Context context;

  public static AgoraApplication getInstance(Context context) {
    if (instance == null) {
      instance = new AgoraApplication(context);
    }
    return instance;
  }

  private AgoraApplication(Context context) {
    this.context = context;
    this.initConfig();
  }

  private void initConfig() {
    try {
      mRtcEngine =
          RtcEngine.create(this.context, this.context.getString(R.string.private_app_id), mHandler);
      mRtcEngine.setChannelProfile(io.agora.rtc.Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
      mRtcEngine.enableVideo();
      mRtcEngine.setLogFile(FileUtil.initializeLogFile(this.context));
    } catch (Exception e) {
      e.printStackTrace();
    }

    mGlobalConfig.setChannelName("1883zk1cnpk3rl5gzf");
    SharedPreferences pref = PrefManager.getPreferences(this.context);
    mGlobalConfig.setVideoDimenIndex(pref.getInt(
        Constants.PREF_RESOLUTION_IDX, Constants.DEFAULT_PROFILE_IDX));

    boolean showStats = pref.getBoolean(Constants.PREF_ENABLE_STATS, false);
    mGlobalConfig.setIfShowVideoStats(showStats);
    mStatsManager.enableStats(showStats);
  }

  public EngineConfig engineConfig() {
    return mGlobalConfig;
  }

  public RtcEngine rtcEngine() {
    return mRtcEngine;
  }

  public StatsManager statsManager() {
    return mStatsManager;
  }

  public void registerEventHandler(EventHandler handler) {
    mHandler.addHandler(handler);
  }

  public void removeEventHandler(EventHandler handler) {
    mHandler.removeHandler(handler);
  }

  public static void terminate() {
    RtcEngine.destroy();
  }
}
