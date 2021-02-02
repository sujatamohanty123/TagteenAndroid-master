package in.tagteen.tagteen.GallaryPicker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.media.effect.Effect;
import android.media.effect.EffectContext;
import android.media.effect.EffectFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import in.tagteen.tagteen.GallaryPicker.Utilities.GLToolbox;
import in.tagteen.tagteen.GallaryPicker.Utilities.RecyclerItemClickListener;
import in.tagteen.tagteen.GallaryPicker.Utilities.TextureRenderer;
import in.tagteen.tagteen.R;
import in.tagteen.tagteen.workers.SharedPreferenceSingleton;

import java.io.ByteArrayOutputStream;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;




public class EffectsFilterFragment extends Fragment implements GLSurfaceView.Renderer ,GL {
	Bitmap capturedBitmap;
	private RecyclerView recList;
	int mCurrentEffect;
	private GLSurfaceView mEffectView;
	private int[] mTextures = new int[2];
	private EffectContext mEffectContext;
	private Effect mEffect;
	private TextureRenderer mTexRenderer = new TextureRenderer();
	private int mImageWidth;
	private int mImageHeight;
	private int widthPixels,heightPixels;
	private boolean mInitialized = false;
	private volatile boolean saveFrame;
	private String url;
	ImageView post;
	Bitmap convertedBitMap;
	public void setCurrentEffect(int effect) {
		mCurrentEffect = effect;
	}
	public LinearLayout rcviewContainer;
	private String itemData[] = {

			"No Effect","Autofix","BlackAndWhite","Brightness","Contrast",
			"CrossProcess","Documentary","Duotone","Fillight","FishEye",
			"Flipert","Fliphor","Grain","Grayscale","Lomoish","Negative",
			"Posterize","Rotate","Saturate","Sepia","Sharpen","Temperature",
			"TintEffect","Vignette"

	};

List<Bitmap> damoImage =new ArrayList<>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.imf_effect_factory, container, false);
		mEffectView = (GLSurfaceView) rootView.findViewById(R.id.effectsview);
		rcviewContainer=(LinearLayout)rootView.findViewById(R.id.con_rec_view);
		mEffectView.setEGLContextClientVersion(2);
		mEffectView.setRenderer(this);
		mEffectView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		mCurrentEffect = 0;
		SharedPreferenceSingleton.getInstance().init(getActivity());
		url=SharedPreferenceSingleton.getInstance().getStringPreference("ImagePath_post");
		post=(ImageView)rootView.findViewById(R.id.post_pic);

		Intent intent =getActivity().getIntent();
		byte[] bytes = intent.getByteArrayExtra("ImageBitMab");
		capturedBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

		recList = (RecyclerView) rootView.findViewById(R.id.rc_filter);
		recList.setHasFixedSize(true);
		recList.setLayoutManager(layoutManager);
		/*rcviewContainer.setVisibility(View.VISIBLE);*/

		post.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

					Intent returnIntent = getActivity().getIntent();
				    String getPath = getImageUri(convertedBitMap);
					returnIntent.putExtra ("filterImage",getPath);
					getActivity().setResult(4,returnIntent);
					getActivity().finish();

			}
		});

        recList.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                setCurrentEffect(position);
                mEffectView.requestRender();

            }
        }));


	/*	for(int i=0; i>itemData.length; i++){
			setCurrentEffect(i);
			mEffectView.requestRender();
			Bitmap bitmap =	getBitmapFromView( mEffectView);
			damoImage.add(bitmap);

		}*/

		FilterAdapterFactory filterAdapter = new FilterAdapterFactory();
		recList.setAdapter(filterAdapter);
		getWindowDimension();
		return rootView;
	}

	private void getWindowDimension() {
		WindowManager w = getActivity().getWindowManager();
		Display d = w.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		d.getMetrics(metrics);
// since SDK_INT = 1;
		widthPixels = metrics.widthPixels;
		heightPixels = metrics.heightPixels;
// includes window decorations (statusbar bar/menu bar)
		if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17)
			try {
				widthPixels = (Integer) Display.class.getMethod("getRawWidth").invoke(d);
				heightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(d);
			} catch (Exception ignored) {
			}
// includes window decorations (statusbar bar/menu bar)
		if (Build.VERSION.SDK_INT >= 17)
			try {
				Point realSize = new Point();
				Display.class.getMethod("getRealSize", Point.class).invoke(d, realSize);
				widthPixels = realSize.x;
				heightPixels = realSize.y;
			} catch (Exception ignored) {
			}
	}

	public static Bitmap getBitmapFromView(View view) {
		//Define a bitmap with the same size as the view
		Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
		//Bind a canvas to it
		Canvas canvas = new Canvas(returnedBitmap);
		//Get the view's background
		Drawable bgDrawable =view.getBackground();
		if (bgDrawable!=null)
			//has background drawable, then draw it on the canvas
			bgDrawable.draw(canvas);
		else
			//does not have background drawable, then draw white background on the canvas
			canvas.drawColor(Color.WHITE);
		// draw the view on the canvas
		view.draw(canvas);
		//return the bitmap
		return returnedBitmap;
	}


	private void loadTextures() {
		/*Bitmap bitmap = null;
		GLES20.glGenTextures(2, mTextures, 0);
		String uri= url;
		File imgFile = new File(uri);

		if(imgFile.exists()){
			bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
		}else{
		   bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.nabilah);
		}*/
		mImageHeight = capturedBitmap.getHeight();
		mImageWidth = capturedBitmap.getWidth();

		mTexRenderer.updateTextureSize(mImageWidth, mImageHeight);

		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[0]);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, capturedBitmap, 0);

		// Set texture parameters
		GLToolbox.initTexParams();
		capturedBitmap.recycle();
	}

	private void initEffect() {
		EffectFactory effectFactory = mEffectContext.getFactory();
		if (mEffect != null) {
			mEffect.release();
		}
		/**
		 * Initialize the correct effect based on the selected menu/action item
		 */
		switch (mCurrentEffect) {

		case 0:
			break;

		case 1:
			mEffect = effectFactory.createEffect(EffectFactory.EFFECT_AUTOFIX);
			mEffect.setParameter("scale", 0.5f);
			break;

		case 2:
			mEffect = effectFactory.createEffect(EffectFactory.EFFECT_BLACKWHITE);
			mEffect.setParameter("black", .1f);
			mEffect.setParameter("white", .7f);
			break;

		case 3:
			mEffect = effectFactory.createEffect(EffectFactory.EFFECT_BRIGHTNESS);
			mEffect.setParameter("brightness", 2.0f);
			break;

		case 4:
			mEffect = effectFactory.createEffect(EffectFactory.EFFECT_CONTRAST);
			mEffect.setParameter("contrast", 1.4f);
			break;

		case 5:
			mEffect = effectFactory.createEffect(EffectFactory.EFFECT_CROSSPROCESS);
			break;

		case 6:
			mEffect = effectFactory.createEffect(EffectFactory.EFFECT_DOCUMENTARY);
			break;

		case 7:
			mEffect = effectFactory.createEffect(EffectFactory.EFFECT_DUOTONE);
			mEffect.setParameter("first_color", Color.YELLOW);
			mEffect.setParameter("second_color", Color.DKGRAY);
			break;

		case 8:
			mEffect = effectFactory.createEffect(EffectFactory.EFFECT_FILLLIGHT);
			mEffect.setParameter("strength", .8f);
			break;

		case 9:
			mEffect = effectFactory.createEffect(EffectFactory.EFFECT_FISHEYE);
			mEffect.setParameter("scale", .5f);
			break;

		case 10:
			mEffect = effectFactory.createEffect(EffectFactory.EFFECT_FLIP);
			mEffect.setParameter("vertical", true);
			break;

		case 11:
			mEffect = effectFactory.createEffect(EffectFactory.EFFECT_FLIP);
			mEffect.setParameter("horizontal", true);
			break;

		case 12:
			mEffect = effectFactory.createEffect(EffectFactory.EFFECT_GRAIN);
			mEffect.setParameter("strength", 1.0f);
			break;

		case 13:
			mEffect = effectFactory.createEffect(EffectFactory.EFFECT_GRAYSCALE);
			break;

		case 14:
			mEffect = effectFactory.createEffect(EffectFactory.EFFECT_LOMOISH);
			break;

		case 15:
			mEffect = effectFactory.createEffect(EffectFactory.EFFECT_NEGATIVE);
			break;

		case 16:
			mEffect = effectFactory.createEffect(EffectFactory.EFFECT_POSTERIZE);
			break;

		case 17:
			mEffect = effectFactory.createEffect(EffectFactory.EFFECT_ROTATE);
			mEffect.setParameter("angle", 180);
			break;

		case 18:
			mEffect = effectFactory.createEffect(EffectFactory.EFFECT_SATURATE);
			mEffect.setParameter("scale", .5f);
			break;

		case 19:
			mEffect = effectFactory.createEffect(EffectFactory.EFFECT_SEPIA);
			break;

		case 20:
			mEffect = effectFactory.createEffect(EffectFactory.EFFECT_SHARPEN);
			break;

		case 21:
			mEffect = effectFactory.createEffect(EffectFactory.EFFECT_TEMPERATURE);
			mEffect.setParameter("scale", .9f);
			break;

		case 22:
			mEffect = effectFactory.createEffect(EffectFactory.EFFECT_TINT);
			mEffect.setParameter("tint", Color.MAGENTA);
			break;

		case 23:
			mEffect = effectFactory.createEffect(EffectFactory.EFFECT_VIGNETTE);
			mEffect.setParameter("scale", .5f);
			break;

		default:
			break;

		}
	}

	private void applyEffect() {
		mEffect.apply(mTextures[0], mImageWidth, mImageHeight, mTextures[1]);
	}

	private void renderResult() {
		if (mCurrentEffect != 0) {
			// if no effect is chosen, just render the original bitmap
			mTexRenderer.renderTexture(mTextures[1]);
		} else {
			saveFrame=true;
			// render the result of applyEffect()
			mTexRenderer.renderTexture(mTextures[0]);
		}
	}



	@Override
	public void onDrawFrame(GL10 gl) {
		if (!mInitialized) {
			// Only need to do this once
			mEffectContext = EffectContext.createWithCurrentGlContext();
			mTexRenderer.init();
			loadTextures();
			mInitialized = true;
		}
		if (mCurrentEffect != 0) {
			// if an effect is chosen initialize it and apply it to the texture
			initEffect();
			applyEffect();
		}
		renderResult();
		convertedBitMap = takeScreenshot(gl);

	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		if (mTexRenderer != null) {
			mTexRenderer.updateViewSize(width, height);
		}
	}


	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
	}

	private class FilterAdapterFactory extends RecyclerView.Adapter<in.tagteen.tagteen.GallaryPicker.EffectsFilterFragment.FilterAdapterFactory.FilterHolder>{



		private Context mContext;

		public FilterAdapterFactory() {
			super();
			this.mContext = mContext;

		}

		@Override
		public in.tagteen.tagteen.GallaryPicker.EffectsFilterFragment.FilterAdapterFactory.FilterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View itemView = LayoutInflater.
					from(parent.getContext()).
					inflate(R.layout.imf_filter_item, parent, false);
			in.tagteen.tagteen.GallaryPicker.EffectsFilterFragment.FilterAdapterFactory.FilterHolder viewHolder = new in.tagteen.tagteen.GallaryPicker.EffectsFilterFragment.FilterAdapterFactory.FilterHolder(itemView);
			return viewHolder;
		}

		@RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
		@Override
		public void onBindViewHolder(in.tagteen.tagteen.GallaryPicker.EffectsFilterFragment.FilterAdapterFactory.FilterHolder holder, int position) {
			String val = itemData[position];
			/*Bitmap bit = damoImage.get(position);*/
			holder.imFilter.setText(val);

		}

		@Override
		public int getItemCount() {
			return itemData.length;
		}

		public class FilterHolder extends RecyclerView.ViewHolder {
			public TextView imFilter;

			@RequiresApi(api = Build.VERSION_CODES.FROYO)
			public FilterHolder(View itemView) {
				super(itemView);
				imFilter = (TextView) itemView.findViewById(R.id.effectsviewimage_item);

			}
		}
	}


	public String getImageUri(Bitmap inImage) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), inImage, "Title", null);
		return path;
	}
	/*public Bitmap takeScreenshot(GL10 mGL) {

		*//*mImageHeight = bitmap.getHeight();
		mImageWidth = bitmap.getWidth();*//*

		final int mWidth =  mEffectView.getWidth() ;
		final int mHeight = mEffectView.getHeight();
		final int startx=mHeight - mImageHeight;
		IntBuffer ib = IntBuffer.allocate(mWidth * mHeight);
		IntBuffer ibt = IntBuffer.allocate(mWidth * mHeight);
		mGL.glReadPixels(0,startx, mWidth, mHeight, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, ib);

		for (int i = 0; i < mHeight; i++) {
			for (int j = 0; j < mWidth; j++) {
				ibt.put((mHeight - i - 1) * mWidth + j, ib.get(i * mWidth + j));
			}
		}

		Bitmap mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
		mBitmap.copyPixelsFromBuffer(ibt);
		return mBitmap;
	}*/

	public Bitmap takeScreenshot(GL10 mGL)
	{

		final int mWidth = (mEffectView.getWidth());
		final int mHeight = (mEffectView.getHeight());

		IntBuffer ib = IntBuffer.allocate(mWidth * mHeight);//((heightPixels-mHeight)/2);  //
		IntBuffer ibt = IntBuffer.allocate(mWidth * mHeight);//((widthPixels-mWidth)/2); //
		mGL.glReadPixels(0, 0, mWidth, mHeight, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, ib);

		// Convert upside down mirror-reversed image to right-side up normal
		// image.

		/*Let the image dimensions be x and y, and get the screen dimensions following this post. Let that be  a and b.
		Your starting position will be ((a-x)/2, (b-y)/2).
			Use this to crop your image.*/


		for (int i = 0; i < mHeight; i++)
		{
			for (int j = 0; j < mWidth; j++)
			{
				/*ibt.put(((heightPixels-mHeight)/2), ((widthPixels-mWidth)/2));*/
				ibt.put((mHeight - i - 1) * mWidth + j, ib.get(i * mWidth + j));
			}
		}

		Bitmap mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
		mBitmap.copyPixelsFromBuffer(ibt);
		return mBitmap;
	}
}
