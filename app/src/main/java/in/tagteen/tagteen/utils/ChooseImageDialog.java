package in.tagteen.tagteen.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;

import in.tagteen.tagteen.R;
import in.tagteen.tagteen.selfyManager.CameraActivity;
import in.tagteen.tagteen.util.Constants;

/**
 * Created by lovekushvishwakarma on 16/10/17.
 */

public class ChooseImageDialog implements View.OnClickListener {

    public static final int CAMERA_PIC_REQUEST = 1;
    public static final int GALLERY_PIC_REQUEST = 2;
    public static final int CROP_PIC_REQUEST = 3;
    TextView textGallery, textCancel, textcamera;
    Dialog dialog;
    Context context;
    AndroidPermissionMarshMallo permissionMarshMallo;

    public ChooseImageDialog(Context context) {
        this.context = context;
        dialog = new Dialog(context, R.style.DialogSlideAnim);
        permissionMarshMallo = new AndroidPermissionMarshMallo((Activity) context);
        dialog.setContentView(R.layout.choose_camera_gallery_dialog);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.gravity = Gravity.BOTTOM;
        lp.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        dialog.getWindow().setAttributes(lp);

        textGallery = (TextView) dialog.findViewById(R.id.textGallery);
        textCancel = (TextView) dialog.findViewById(R.id.textCancel);
        textcamera = (TextView) dialog.findViewById(R.id.textcamera);

        textCancel.setOnClickListener(this);
        textcamera.setOnClickListener(this);
        textGallery.setOnClickListener(this);
        dialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textGallery:
                if (permissionMarshMallo.checkPermissionForReadExternal()) {
                    gotoGallery();
                } else {
                    permissionMarshMallo.requestPermissionForReadExternal();
                }
                break;
            case R.id.textCancel:
                dialog.dismiss();
                break;
            case R.id.textcamera:
                if (permissionMarshMallo.checkPermissionForCamera()) {
                    gotoCamera();
                } else {
                    permissionMarshMallo.requestPermissionForCamera();
                }
                break;
        }
    }

    public void gotoCamera() {
        Intent cameraIntent = new Intent(context, CameraActivity.class);
        cameraIntent.putExtra(Constants.DISABLE_VIDEO, true);
        ((Activity) context).startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
        dialog.dismiss();
    }

    public void gotoGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        ((Activity) context).startActivityForResult(Intent.createChooser(photoPickerIntent, "Select Picture"), GALLERY_PIC_REQUEST);
        dialog.dismiss();
    }
}
