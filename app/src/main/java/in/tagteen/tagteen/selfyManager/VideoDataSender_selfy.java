package in.tagteen.tagteen.selfyManager;

import in.tagteen.tagteen.Fragments.youthtube.bean.VideoDataSender;

/**
 * Created by Sujata on 18-06-2018.
 */

public class VideoDataSender_selfy {
    private static boolean isCall = false;
        private static String videoPath = "";
        private static String videoDesc = "";


        public static boolean isCall() {
            return isCall;
        }

        public static void setIsCall(boolean isCall) {
            VideoDataSender_selfy.isCall = isCall;
        }

        public static String getVideoPath() {
            return videoPath;
        }

        public static void setVideoPath(String videoPath) {
            VideoDataSender_selfy.videoPath = videoPath;
        }
        public static String getVideoDesc() {
            return videoDesc;
        }

        public static void setVideoDesc(String videoDesc) {
            VideoDataSender_selfy.videoDesc = videoDesc;
        }
}
