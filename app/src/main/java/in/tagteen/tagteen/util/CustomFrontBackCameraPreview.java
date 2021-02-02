package in.tagteen.tagteen.util;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

public class CustomFrontBackCameraPreview extends SurfaceView implements
		SurfaceHolder.Callback {
	private SurfaceHolder mHolder;
	boolean previewing = false;
	private Camera mCamera;
	SurfaceView surfaceView;
	Context context;
	int mCameraId;
	int f;

	
	@SuppressWarnings("deprecation")
	public CustomFrontBackCameraPreview(Context context, Camera camera,
										int cameraId) {
		super(context);
		Log.d("Const", "const");
		mCamera = camera;
		this.context = context;
		this.mCameraId = cameraId;
		mHolder = getHolder();
		mHolder.addCallback(this);
		// deprecated setting, but required on Android versions prior to 3.0
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

	}

	public void surfaceCreated(SurfaceHolder holder) {
		try {
			// create the surface and start camera preview
			if (mCamera == null) {

				mCamera.setDisplayOrientation(90);
				mCamera.setPreviewDisplay(holder);
				mCamera.startPreview();
				previewing = false;
			}
		} catch (Exception e) {
			Log.d(VIEW_LOG_TAG,
					"Error setting camera preview: " + e.getMessage());
		}
	}

	public void refreshCamera(Camera camera) {
		if (mHolder.getSurface() == null) {
			// preview surface does not exist
			return;
		}
		// stop preview before making changes
		try {
			mCamera.stopPreview();
		} catch (Exception e) {
			// ignore: tried to stop a non-existent preview
		}
		// set preview size and make any resize, rotate or
		// reformatting changes here
		// start preview with new settings
		setCamera(camera);
		try {
			mCamera.setPreviewDisplay(mHolder);
			mCamera.setDisplayOrientation(90);
			mCamera.startPreview();
		} catch (Exception e) {
			Log.d(VIEW_LOG_TAG,
					"Error starting camera preview: " + e.getMessage());
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		// If your preview can change or rotate, take care of those events here.
		// Make sure to stop the preview before resizing or reformatting it.

		try {
			if (previewing) {
				mCamera.stopPreview();
				previewing = false;
			}
			Camera.Parameters p = mCamera.getParameters();
			/*List<Camera.Size> sizes = p.getSupportedPreviewSizes();
			Camera.Size cs = sizes.get(0);
			p.setPreviewSize(cs.width, cs.height);
			Log.d("tel", "width: \n" + cs.width + "Height: " + cs.height);*/

			p.setJpegQuality(100);
			List<Camera.Size> sizes = p.getSupportedPictureSizes();
			Camera.Size size = sizes.get(0);
			for(int i=0;i<sizes.size();i++)
			{
				if(sizes.get(i).width > size.width)
					size = sizes.get(i);
			}
			p.setPictureSize(size.width, size.height);

			mCamera.setParameters(p);
			if (mCamera != null) {
				try {
					mCamera.setDisplayOrientation(90);
					mCamera.setPreviewDisplay(holder);
					mCamera.startPreview();
					previewing = true;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			refreshCamera(mCamera);
		}catch (Exception e){
			e.printStackTrace();
		}

	}

	public void setCamera(Camera camera) {
		// method to set a camera instance
		mCamera = camera;
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		mCamera.release();

	}

	public void setCameraDisplayOrientationAndSize() {
		CameraInfo info = new CameraInfo();
		Camera.getCameraInfo(mCameraId, info);
		int rotation = ((Activity) context).getWindowManager()
				.getDefaultDisplay().getRotation();
		int degrees = rotation * 90;
		Log.i("check2","inside setCameraDisplayOrientationAndSize()");
		int result;
		if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360;
		} else {
			result = (info.orientation - degrees + 360) % 360;
		}
		mCamera.setDisplayOrientation(result);

		Camera.Size previewSize = mCamera.getParameters().getPreviewSize();
		if (result == 90 || result == 270) {
			mHolder.setFixedSize(previewSize.height, previewSize.width);
		} else {
			mHolder.setFixedSize(previewSize.width, previewSize.height);

		}
	}

//	public static void setCameraDisplayOrientation(Activity activity,
//			int cameraId, android.hardware.Camera camera) {
//		android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
//		android.hardware.Camera.getCameraInfo(cameraId, info);
//		int rotation = activity.getWindowManager().getDefaultDisplay()
//				.getRotation();
//		Log.i("check1","inside setCameraDisplayOrientation");
//		int degrees = 0;
//		switch (rotation) {
//		case Surface.ROTATION_0:
//			degrees = 0;
//			break;
//		case Surface.ROTATION_90:
//			degrees = 90;
//			break;
//		case Surface.ROTATION_180:
//			degrees = 180;
//			break;
//		case Surface.ROTATION_270:
//			degrees = 270;
//			break;
//		}
//
//		int result;
//		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
//			result = (info.orientation + degrees) % 360;
//			result = (360 - result) % 360; // compensate the mirror
//		} else { // back-facing
//			result = (info.orientation - degrees + 360) % 360;
//		}
//		camera.setDisplayOrientation(result);
//	}

}