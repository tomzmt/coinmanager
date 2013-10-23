package lt.tomzmt.mycoins;

import lt.tomzmt.mycoins.data.SimpleDbOpener;
import android.app.Application;

public class MyCoinsApplication extends Application {

	private SimpleDbOpener mOpener = null;
	
	/**
	 * 
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		
		mOpener = new SimpleDbOpener(this);
	}
	
	/**
	 * 
	 */
	public SimpleDbOpener getDatabaseOpener() {
		return mOpener;
	}
}
