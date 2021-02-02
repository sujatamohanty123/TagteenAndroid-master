package in.tagteen.tagteen.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Created by BSETEC on 15-12-2016.
 */

public class CommanFun {
    public static String showroomExitedFive = "You can mentioned only 5 Showtag!";

    public static void mintshowToast(Context mContext, String msg) {
        try {
            Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void getAlertDialogWithMessage(Context act, String msg) {
        new AlertDialog.Builder(act)
                .setTitle("ERROR!")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.dismiss();
                            }
                        }).show();
    }

    public static void getAlertDialogWithbothMessage(Context act,
                                                     String title, String msg) {
        new AlertDialog.Builder(act)
                .setTitle(title)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        }).show();
    }

    public static void copyStream(InputStream input, OutputStream output)
            throws IOException {

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }






    //Replace text with custom delimters
    public static String replaceTexts(String data) {
        String res = "";
        for (int i = 0; i < data.trim().length(); i++) {
            if (data.charAt(i) == ' ')
                res += "";
            else
                res += data.charAt(i);
        }
        return res;
    }



    public static int getOrientatio(String path) {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);
        return orientation;
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }



    public static boolean getLastWord(String data) {
        boolean flag = false;
        if (data.length() > 0) {
            String splitdata[] = data.split(" ");
            if (splitdata.length > 0) {
                String ss = splitdata[splitdata.length - 1];
                if (splitdata[splitdata.length - 1].charAt(0) == '@' ||
                        splitdata[splitdata.length - 1].charAt(0) == '#' ||
                        splitdata[splitdata.length - 1].charAt(0) == '*' || ss.contains("[")) {
                    flag = true;
                } else {
                    flag = false;
                }
            }
        }
        return flag;
    }









    public static int getTempCount(String str) {
        int count = 0;
        boolean flag = false;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) != '@' && str.charAt(i) != '#' && str.charAt(i) != '*' && str.charAt(i) != '[' && flag == false) {
                count++;
            } else {
                if (str.charAt(i) == ' ' || str.charAt(i) == ']') {
                    flag = false;
                } else {
                    flag = true;
                }
            }
        }
        return count;
    }


    public static String getTypedCountWelcomeMint(String data, final int total) {
        int count = 0;
        StringBuffer sb = new StringBuffer();
        if (data.length() > 0) {
            try {
                if (data.contains("[")) {
                    String array[] = data.split("\\[");
                    for (int i = 0; i < array.length; i++) {
                        String text = array[i];
                        if (text.length() > 1)
                            if (text.contains("]")) {
                                //abc], abcd]love
                                String array2[] = text.split("\\]");
                                if (array2.length > 1 && array2[1].length() > 0) {
                                    sb.append(array2[1]);
                                }
                            } else {
                                //normal text
                                if (array[i].length() > 0) {
                                    sb.append(array[i]);
                                }

                            }
                    }
                } else {
                    sb.append(data);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return sb.toString();
    }






    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);

            Log.e("Package Name=", context.getApplicationContext().getPackageName());

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("Key Hash=", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        }
        catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return key;
    }
}
