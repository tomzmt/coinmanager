package lt.tomzmt.mycoins.ui.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageUtils {
	
	/**
	 * 
	 * @param fileName
	 * @return
	 */
	public static Bitmap loadBitmapForList(String fileName) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 4;
		return BitmapFactory.decodeFile(fileName, options);
	}
	
	/**
	 * 
	 * @param fileName
	 * @return
	 */
	public static Bitmap loadBitmapForDetails(String fileName) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 2;
		return BitmapFactory.decodeFile(fileName, options);		
	}
}
