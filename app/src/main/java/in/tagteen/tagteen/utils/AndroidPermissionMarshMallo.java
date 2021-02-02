package in.tagteen.tagteen.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.widget.Toast;

import in.tagteen.tagteen.util.Constants;

/**
 * Created by bsetec on 4/19/2016.
 */
public class AndroidPermissionMarshMallo {
    RejectedPermisionDialog denaiedPopup;
    public static final int RECORD_PERMISSION_REQUEST_CODE = 1;
    public static final int EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 2;
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 3;
    public static final int WRITE_SETTINGS_CODE = 4;
    public static final int READ_EXTERNAL_STORAGE_CODE = 5;
    public static final int WRITE_EXTERNAL_STORAGE_CODE = 6;
    public static final int CLEAR_CACHE_PERMITION = 7;
    Activity activity;
    Intent intent;

    public AndroidPermissionMarshMallo(Activity activity) {
        this.activity = activity;
        intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", Constants.APP_FOLDER_NAME, null);
        intent.setData(uri);
        denaiedPopup = new RejectedPermisionDialog(activity);
    }

    public boolean checkPermissionForRecord() {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPermissionForExternalStorage() {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPermissionForCamera() {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }


    public void requestPermissionForRecord() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.RECORD_AUDIO)) {
            denaiedPopup.displayMsg("Microphone");
            //Toast.makeText(activity, "Microphone permission needed for recording. Please allow in TagteenApplication AppSettings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_PERMISSION_REQUEST_CODE);
        }
    }

    public void requestPermissionForExternalStorage() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            denaiedPopup.displayMsg("External Storage");
            // Toast.makeText(activity, "External Storage permission needed. Please allow in TagteenApplication AppSettings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
        }
    }

    public void requestPermissionForCamera() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {
            denaiedPopup.displayMsg("Camera");
            // Logger.d("NEEDED");
            // Toast.makeText(activity, "Camera permission needed. Please allow in TagteenApplication AppSettings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        }
    }


    public boolean checkPermissionForWriteSetting() {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_SETTINGS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public void requestPermissionForWriteSetting() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_SETTINGS)) {
            denaiedPopup.displayMsg("WRITE_SETTINGS");
            // Toast.makeText(activity, "WRITE_SETTINGS permission needed. Please allow in TagteenApplication AppSettings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_SETTINGS}, WRITE_SETTINGS_CODE);
        }

    }

    public void requestPermissionForReadExternal() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            denaiedPopup.displayMsg("WRITE_SETTINGS");
            // Toast.makeText(activity, "WRITE_SETTINGS permission needed. Please allow in TagteenApplication AppSettings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_CODE);
        }

    }

    public boolean checkPermissionForReadExternal() {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPermissionForWriteExternal() {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public void requestPermissionForWriteExternal() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(activity, "WRITE_SETTINGS permission needed. Please allow in TagteenApplication AppSettings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_CODE);
        }
    }

    public boolean checkAllPermison() {
        boolean flag = false;
        if (checkPermissionForRecord() == false || checkPermissionForReadExternal() == false || checkPermissionForWriteExternal() ||
                checkPermissionForCamera()) {
            flag = false;
        } else {
            flag = true;
        }
        return flag;
    }

    public boolean requestAllPermison() {
        boolean flag = false;
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_SETTINGS, Manifest.permission.CLEAR_APP_CACHE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermissionForRecord() == false) {
                ActivityCompat.requestPermissions(activity, perms, 1);
            }
            /*if(AppSettings.System.canWrite(activity)==false){
                Intent intent = new Intent();
                intent.setAction(AppSettings.ACTION_MANAGE_WRITE_SETTINGS);
                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                intent.setData(uri);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                activity.startActivity(intent);
            }*/
        } else {
            flag = true;
        }
        return flag;
    }


}