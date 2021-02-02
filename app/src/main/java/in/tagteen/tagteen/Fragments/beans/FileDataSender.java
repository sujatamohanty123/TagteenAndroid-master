package in.tagteen.tagteen.Fragments.beans;

import java.util.ArrayList;

public class FileDataSender {
    public static final int POST_TO_MOMENTS = 1;
    public static final int POST_TO_CAMPUSLIVE = 2;

    public static boolean HAS_FILE_TO_UPLOAD = false;
    public static String FILE_PATH = "";
    public static ArrayList<String> FILE_PATH_LIST = null;
    public static int CATEGORY_ID;
    public static String DESCRIPTION = "";
    public static int VIEW_TO;

    //
    public static int POST_TO;
    //public static int IMG_HEIGHT = 0, IMG_WIDTH = 0;

    public static void clear() {
        FileDataSender.HAS_FILE_TO_UPLOAD = false;
        FileDataSender.FILE_PATH_LIST = null;
        FileDataSender.FILE_PATH = "";
        FileDataSender.CATEGORY_ID = 1;
        FileDataSender.DESCRIPTION = "";
        FileDataSender.VIEW_TO = 1;
        FileDataSender.POST_TO = 0;
        //FileDataSender.IMG_HEIGHT = 0;
        //FileDataSender.IMG_WIDTH = 0;
    }
}
