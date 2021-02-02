package io.agora.openlive.utils;

public class ImageUrlUtils {

  public static String getUpdatedImageUrl(String url, String size) {
    if (isImagePathContainOld(url)) {
      String fileName = url.substring(url.lastIndexOf("/") + 1);
      url = "https://tagteen-images-resized.s3.ap-south-1.amazonaws.com/"
          + size
          + "/outputs/"
          + fileName;
    }
    return url;
  }

  public static boolean isImagePathContainOld(String url) {
    boolean flag = false;
    if (url.contains("tagteen-images.s3.ap-south-1.amazonaws.com") || url.contains(
        "https://tagteen-images.s3.amazonaws.com")) {
      flag = true;
    }

    return flag;
  }
}
