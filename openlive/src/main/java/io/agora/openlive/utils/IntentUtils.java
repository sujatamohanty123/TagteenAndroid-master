package io.agora.openlive.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import java.io.File;
import java.io.FileOutputStream;

public class IntentUtils {

  public static final String SHARE_LINK_TEMP_FILE_NAME = "sharelinke.jpg";

  public static void shareWebShowAppDownloadLink(Context context, View view,
      String broadcasterName) {
    Bitmap bitmap = getBitmapFromView(view);
    if (bitmap != null) {
      try {
        String root = Environment.getExternalStorageDirectory().toString();
        File dirPath = new File(root + "/tagteen");
        dirPath.mkdirs();
        File file = new File(dirPath, SHARE_LINK_TEMP_FILE_NAME);
        FileOutputStream outStream = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outStream);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setPackage("com.whatsapp");
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_TEXT,
            "Hey! Download tagteen app\uD83C\uDDEE\uD83C\uDDF3 and watch amazing "
                + broadcasterName
                + " ⭐perform LIVE!\uD83D\uDD25 for free. Come and Join before it ends..\n"
                + "https://play.google.com/store/apps/details?id=in.tagteen.tagteen"
        );
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(file.toString()));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
          context.startActivity(intent);
        } catch (android.content.ActivityNotFoundException ex) {
          ex.printStackTrace();
          context.startActivity(Intent.createChooser(intent, "Share download link"));
        }
      } catch (Exception e) {
        Log.e("ShareLink", e.getMessage());
        e.printStackTrace();
      }
    }
  }

  public static void shareBrodcasterWebShowAppDownloadLink(Context context, Bitmap bitmap,
      String broadcasterName) {
    if (bitmap != null) {
      try {
        String root = Environment.getExternalStorageDirectory().toString();
        File dirPath = new File(root + "/tagteen");
        dirPath.mkdirs();
        File file = new File(dirPath, SHARE_LINK_TEMP_FILE_NAME);
        FileOutputStream outStream = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outStream);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setPackage("com.whatsapp");
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_TEXT,
            "Hey! Download tagteen app\uD83C\uDDEE\uD83C\uDDF3 and watch amazing "
                + broadcasterName
                + " ⭐perform LIVE!\uD83D\uDD25 for free. Come and Join before it ends..\n"
                + "https://play.google.com/store/apps/details?id=in.tagteen.tagteen"
        );
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(file.toString()));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
          context.startActivity(intent);
        } catch (android.content.ActivityNotFoundException ex) {
          ex.printStackTrace();
          context.startActivity(Intent.createChooser(intent, "Share download link"));
        }
      } catch (Exception e) {
        Log.e("ShareLink", e.getMessage());
        e.printStackTrace();
      }
    }
  }

  private static Bitmap getBitmapFromView(View view) {
    Bitmap returnedBitmap =
        Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(returnedBitmap);
    Drawable bgDrawable = view.getBackground();
    if (bgDrawable != null) {
      bgDrawable.draw(canvas);
    } else {
      canvas.drawColor(Color.WHITE);
    }
    view.draw(canvas);
    return returnedBitmap;
  }

  public static void removeShareLinkTempImage() {
    try {
      String root = Environment.getExternalStorageDirectory().toString();
      File dirPath = new File(root + "/tagteen");
      if (dirPath == null) {
        return;
      }
      File file = new File(dirPath, SHARE_LINK_TEMP_FILE_NAME);
      if (file != null && file.exists()) {
        file.delete();
      }
    } catch (Exception e) {
      // do nothing
    }
  }
}
