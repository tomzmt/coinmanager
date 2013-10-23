package lt.tomzmt.mycoins.ui.utils;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class ThumbnailCashe extends LruCache<String, Bitmap>{

	public ThumbnailCashe(int size) {
		super(size);
	}
	
	@Override
	protected int sizeOf(String key, Bitmap value) {
		return value.getRowBytes() * value.getHeight();
	}
}
