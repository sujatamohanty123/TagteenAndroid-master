package io.agora.openlive.activities;

import android.os.Bundle;
import android.view.SurfaceView;
import io.agora.openlive.rtc.EventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;

public abstract class RtcBaseActivity extends BaseActivity implements EventHandler {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    registerRtcEventHandler(this);
    configVideo();
  }

  private void configVideo() {
    rtcEngine().setVideoEncoderConfiguration(new VideoEncoderConfiguration(
        io.agora.openlive.Constants.VIDEO_DIMENSIONS[config().getVideoDimenIndex()],
        VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
        VideoEncoderConfiguration.STANDARD_BITRATE,
        VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT
    ));
  }

  public void joinChannel() {
    rtcEngine().joinChannel(config().getToken(), config().getChannelName(), "", 0);
  }

  protected SurfaceView prepareRtcVideo(int uid, boolean local) {
    SurfaceView surface = RtcEngine.CreateRendererView(getApplicationContext());
    if (local) {
      rtcEngine().setupLocalVideo(new VideoCanvas(surface, VideoCanvas.RENDER_MODE_HIDDEN, 0));
    } else {
      rtcEngine().setupRemoteVideo(new VideoCanvas(surface, VideoCanvas.RENDER_MODE_HIDDEN, uid));
    }
    return surface;
  }

  protected void removeRtcVideo(int uid, boolean local) {
    if (local) {
      rtcEngine().setupLocalVideo(null);
    } else {
      rtcEngine().setupRemoteVideo(new VideoCanvas(null, VideoCanvas.RENDER_MODE_HIDDEN, uid));
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    removeRtcEventHandler(this);
    rtcEngine().leaveChannel();
  }
}
