package in.tagteen.tagteen.Fragments;

/**
 * Created by lovekushvishwakarma on 08/10/17.
 */

public class VideoDataSender1 {

  private static boolean isCall = false;
  private static String videoPath = "";
  private static String videoCategoryId = "";
  private static String languageId = "";
  private static String videoDesc = "";
  private static int videoHeight = 0, videoWidth = 0, video_duration = 0;
  private static String thumbnailPath;
  private static String videoTitle = "";

  public static String getVideoTitle() {
    return videoTitle;
  }

  public static void setVideoTitle(String videoTitle) {
    VideoDataSender1.videoTitle = videoTitle;
  }

  public static int getVideo_duration() {
    return video_duration;
  }

  public static void setVideo_duration(int video_duration) {
    VideoDataSender1.video_duration = video_duration;
  }

  public static boolean isCall() {
    return isCall;
  }

  public static void setIsCall(boolean isCall) {
    VideoDataSender1.isCall = isCall;
  }

  public static String getVideoPath() {
    return videoPath;
  }

  public static void setVideoPath(String videoPath) {
    VideoDataSender1.videoPath = videoPath;
  }

  public static String getVideoCategoryId() {
    return videoCategoryId;
  }

  public static void setVideoCategoryId(String videoCategoryId) {
    VideoDataSender1.videoCategoryId = videoCategoryId;
  }

  public static String getLanguageId() {
    return languageId;
  }

  public static void setLanguageId(String languageId) {
    VideoDataSender1.languageId = languageId;
  }

  public static String getVideoDesc() {
    return videoDesc;
  }

  public static void setVideoDesc(String videoDesc) {
    VideoDataSender1.videoDesc = videoDesc;
  }

  public static int getVideoHeight() {
    return videoHeight;
  }

  public static void setVideoHeight(int videoHeight) {
    VideoDataSender1.videoHeight = videoHeight;
  }

  public static int getVideoWidth() {
    return videoWidth;
  }

  public static void setVideoWidth(int videoWidth) {
    VideoDataSender1.videoWidth = videoWidth;
  }

  public static String getThumbnailPath() {
    return thumbnailPath;
  }

  public static void setThumbnailPath(String thumbnailPath) {
    VideoDataSender1.thumbnailPath = thumbnailPath;
  }
}
