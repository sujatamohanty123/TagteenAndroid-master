package in.tagteen.tagteen.backgroundUpload;

import in.tagteen.tagteen.utils.GeneralApiUtils;

public class UrlUtils {

    public static boolean isVideoPathContainOld(String url) {
        boolean flag = false;
        if (url.contains("tagteen-input-videos.s3.ap-south-1.amazonaws.com") || url.contains(
                "tagteen-input-videos.amazonaws.com")) {
            flag = true;
        }

        return flag;
    }

    public static boolean isImagePathContainOld(String url) {
        ////https://tagteen-images.s3.ap-south-1.amazonaws.com/inputs/1593518283912_5cc30b6e925f59364d03fc36_Image-6046.jpg
        boolean flag = false;
        if (url.contains("tagteen-images.s3.ap-south-1.amazonaws.com") || url.contains(
                "https://tagteen-images.s3.amazonaws.com")) {
            flag = true;
        }

        return flag;
    }

    public static String getUpdatedVideoUrl(String url) {
        if (!GeneralApiUtils.isStringEmpty(url) && isVideoPathContainOld(url)) {
            //https://tagteen-input-videos.s3.ap-south-1.amazonaws.com/Image1593516164252_5cc30b6e925f59364d03fc36__20200630_165234.mp4
            String fileName = url.substring(url.lastIndexOf("/") + 1);
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
            url = "https://tagteen-output-videos.s3.ap-south-1.amazonaws.com/"
                    + fileName
                    + "/Default_360.mp4";
        }
        return url;
    }

    public static String getUpdatedImageUrl(String url, String size) {
        ////https://tagteen-images.s3.ap-south-1.amazonaws.com/inputs/1593518283912_5cc30b6e925f59364d03fc36_Image-6046.jpg
        //https://tagteen-images-resized.s3.ap-south-1.amazonaws.com/small/inputs/1593518283912_5cc30b6e925f59364d03fc36_Image-6046.jpg
        if (!GeneralApiUtils.isStringEmpty(url) && isImagePathContainOld(url)) {
            String fileName = url.substring(url.lastIndexOf("/") + 1);
            url = "https://tagteen-images-resized.s3.ap-south-1.amazonaws.com/"
                    + size
                    + "/inputs/"
                    + fileName;
        }
        return url;
    }

    public static String getTrimmedVideoUrl(String url) {
        String fileName = url.substring(url.lastIndexOf("/") + 1);
        fileName = fileName.substring(0, fileName.lastIndexOf("."));
        url = "https://tagteen-output-videos.s3.ap-south-1.amazonaws.com/"
                + fileName
                + "/Default_clipped.mp4";
        return url;
    }
}
