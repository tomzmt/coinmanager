package lt.tomzmt.mycoins.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import lt.tomzmt.mycoins.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class TakePhotoActivity extends Activity implements PictureCallback,
														   SurfaceHolder.Callback,
														   OnClickListener,
														   AutoFocusCallback {

	public static final String RESULT_IMAGE_PATH = "TakePhotoActivity.image_path";
	
	private static final String TAG = TakePhotoActivity.class.getSimpleName();
	private static final String PHOTOS_DIR = "images";
	
	private SurfaceView mPreview = null;
	private SurfaceHolder mPreviewHolder = null;
	private Camera mCamera;
	
	private boolean cameraConfigured = false;
	
	/**
	 * 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_take_photo);
		
	    mPreview = (SurfaceView)findViewById(R.id.preview);
	    mPreviewHolder = mPreview.getHolder();
	    mPreviewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	    mPreviewHolder.addCallback(this);
	    
		final View top = new View(this);
		top.setBackgroundColor(Color.BLACK);
	    
	    final View controls = getLayoutInflater().inflate(R.layout.camera_control, null, false);
	    controls.findViewById(R.id.take).setOnClickListener(this);
	    controls.findViewById(R.id.focus).setOnClickListener(this);
	    controls.findViewById(R.id.dismiss).setOnClickListener(this);
	    
	    final ViewGroup root = (ViewGroup)findViewById(R.id.root);
	    root.postDelayed(new Runnable() {
			@Override
			public void run() {
				int w = root.getWidth();
				int h = root.getHeight();
				
				int controlsHeight = (h - w) / 2;

				RelativeLayout.LayoutParams paramsTop = new RelativeLayout.LayoutParams(w, controlsHeight);
				paramsTop.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				root.addView(top, paramsTop);
				
				RelativeLayout.LayoutParams paramsBottom = new RelativeLayout.LayoutParams(w, controlsHeight);
				paramsBottom.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				root.addView(controls, paramsBottom);
			}
		}, 20);
	}
	
	
	
	/**
	 * 
	 */
	@Override
	public void onResume() {
		super.onResume();

		if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			try {
				mCamera = Camera.open();
				mCamera.setDisplayOrientation(90);
				//startPreview();
			} catch (Exception e) {
				e.printStackTrace();
			}
	    } else {
	    	// TODO handle camera not exist error
	    }
	}

	/**
	 * 
	 */
	@Override
	public void onPause() {
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
	    super.onPause();
	}
	
	/**
	 * 
	 * @param width
	 * @param height
	 * @param parameters
	 * @return
	 */
	private Camera.Size getBestPreviewSize(int width, int height, Camera.Parameters parameters) {
		Camera.Size result=null;		
		for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
			if (size.width <= width && size.height <= height) {
				if (result == null) {
					result=size;
				} else {
					int resultArea = result.width * result.height;
					int newArea = size.width * size.height;

					if (newArea > resultArea) {
						result = size;
					}
				}
			}
		}
		return result;
	}

	/**
	 * 
	 * @param parameters
	 * @return
	 */
	private Camera.Size getSmallestPictureSize(int width, int height, Camera.Parameters parameters) {
		Camera.Size result = null;
		for (Camera.Size size : parameters.getSupportedPictureSizes()) {
			if (size.width <= width && size.height <= height) {
				if (result == null) {
					result = size;
				} else {
					int resultArea = result.width * result.height;
					int newArea = size.width * size.height;

					if (newArea > resultArea) {
						result = size;
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * 
	 */
	private void startPreview() {
		if (cameraConfigured && mCamera != null) {
			mCamera.startPreview();
		}
	}
	
	/**
	 * 
	 * @param width
	 * @param height
	 */
	private void initPreview(int width, int height) {
		if (mCamera != null && mPreviewHolder.getSurface() != null) {
			try {
				mCamera.setPreviewDisplay(mPreviewHolder);
			} catch (Throwable t) {
				Log.e("PreviewDemo-surfaceCallback", "Exception in setPreviewDisplay()", t);
		        Toast.makeText(this, t.getMessage(), Toast.LENGTH_LONG).show();
			}

			if (!cameraConfigured) {
		        Camera.Parameters parameters = mCamera.getParameters();
		        Camera.Size size = getBestPreviewSize(width, height, parameters);
		        Camera.Size pictureSize = getSmallestPictureSize(width, height, parameters);

		        if (size != null && pictureSize != null) {
		        	parameters.setPreviewSize(size.width, size.height);
		        	parameters.setPictureSize(pictureSize.width, pictureSize.height);
		        	parameters.setPictureFormat(ImageFormat.JPEG);
		        	parameters.setRotation(90);
		        	mCamera.setParameters(parameters);
		        	cameraConfigured = true;
		        }
			}
		}
	}
	
	/**
	 * 
	 */
	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		
		Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
		int w = bmp.getWidth();
		int h = bmp.getHeight();
		
		Log.i(TAG, "w: " + w);
		Log.i(TAG, "h: " + h);
		
		int size = (h > w) ? w : h;

		Bitmap scaledBitmap = Bitmap.createBitmap(size, size, Config.ARGB_8888);

		Canvas canvas = new Canvas(scaledBitmap);
		Rect rectSrc;
		if (h > w) {
			rectSrc = new Rect(0, (h - w) / 2, size, size);
		} else {
			rectSrc = new Rect((w - h) / 2, 0, size, size);
		}
		
		Rect rectDsc = new Rect(0, 0, size, size);
		canvas.drawBitmap(bmp, rectSrc, rectDsc, new Paint(Paint.FILTER_BITMAP_FLAG));
		
        File pictureDir = getDir(PHOTOS_DIR, Context.MODE_PRIVATE);
        File pictureFile = new File(pictureDir, "" + System.currentTimeMillis());
        
        if (pictureFile != null){
        	FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(pictureFile);
                scaledBitmap.compress(CompressFormat.JPEG, 100, fos);
                
        		Intent result = new Intent();
        		result.putExtra(RESULT_IMAGE_PATH, pictureFile.getPath());
        		setResult(RESULT_OK, result);
                
        		finish();
        		
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } finally {
            	try {
            		if (fos != null) {
            			fos.close();
            		}
            	} catch (IOException e) {
            		Log.d(TAG, "Error accessing file: " + e.getMessage());
            	}
            }
        }
	}
	
	/**
	 * 
	 * @param holder
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {}
	
	/**
	 * 
	 * @param holder
	 * @param format
	 * @param width
	 * @param height
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	      initPreview(width, height);
	      startPreview();
	}
	
	/**
	 * 
	 * @param holder
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {}
	
	/**
	 * 
	 */
	@Override
	public void onAutoFocus(boolean success, Camera camera) {
	    findViewById(R.id.take).setEnabled(true);
	    findViewById(R.id.focus).setEnabled(true);
	    findViewById(R.id.dismiss).setEnabled(true);
	}
	
	/**
	 * 
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.take:
			mCamera.takePicture(null, null, this);
			break;

		case R.id.focus:
			
		    findViewById(R.id.take).setEnabled(false);
		    findViewById(R.id.focus).setEnabled(false);
		    findViewById(R.id.dismiss).setEnabled(false);
			
			mCamera.autoFocus(this);
			break;
			
		case R.id.dismiss:
			startPreview();
			break;
		}
	}
}