package lt.tomzmt.mycoins.ui;

import lt.tomzmt.mycoins.R;
import lt.tomzmt.mycoins.data.entities.Coin;
import lt.tomzmt.mycoins.ui.utils.ImageRotator;
import lt.tomzmt.mycoins.ui.utils.ImageRotator.RotationCommand;
import lt.tomzmt.mycoins.ui.utils.ImageUtils;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class CoinDetailsActivity extends FragmentActivity implements LoaderCallbacks<Cursor>,
																	 OnClickListener {

	public static final String CONTENT_URI = "CoinDetailsActivity.content_uri";
	
	public static final int COIN_LOADER = 1002; 
	
	private ImageRotator mImageRotator = new ImageRotator();
	private boolean mRotated = false;

	private Bitmap mAverse = null;
	private Bitmap mReverse = null;
	
	/**
	 * 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_coins_details);
		
		Uri contentUri = getIntent().getParcelableExtra(CONTENT_URI);
		if (contentUri != null) {
			Bundle args = new Bundle();
			args.putParcelable(CONTENT_URI, contentUri);
			getSupportLoaderManager().initLoader(COIN_LOADER, args, this);
		} else {
			throw new IllegalArgumentException("Content Uri is not povided");
		}
	}
	
	/**
	 * 
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.coin_details, menu);
		return true;
	}
	
	/**
	 * 
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_edit:
			
			Intent intent = new Intent(getApplicationContext(), EditCoinActivity.class);
			intent.putExtra(EditCoinActivity.CONTENT_URI, getIntent().getParcelableExtra(CONTENT_URI));
			startActivity(intent);
			return true;

		default:
			return false;
		}
	}
	
	/**
	 * 
	 */
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		if (id == COIN_LOADER) {
			Uri uri = bundle.getParcelable(CONTENT_URI);
			if (uri != null) {
				return new CursorLoader(this, uri, null, null, null, null);
			} else {
				throw new IllegalArgumentException("Content Uri is not povided");
			}
		} else {
			throw new IllegalArgumentException("Undefined loaded ID");
		}
	}
	
	/**
	 * 
	 */
	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// ignore
	}
	
	/**
	 * 
	 */
	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
		if (cursor.moveToFirst()) {
			
			String averse = Coin.getAverse(cursor);
			if (averse != null) {
				mAverse = BitmapFactory.decodeFile(averse);
			} else {
				mAverse = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_gallery);
			}
			
			String reverse = Coin.getReverse(cursor);
			if (reverse != null) {
				mReverse = BitmapFactory.decodeFile(reverse);
			} else {
				mReverse = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_gallery);
			}
			
			ImageView img = (ImageView)findViewById(R.id.coin_image);
			img.setOnClickListener(this);
			img.setImageBitmap(mAverse);
			
			((TextView)findViewById(R.id.denomination)).setText(Coin.getDenomination(cursor));
			((TextView)findViewById(R.id.currency)).setText(Coin.getCurrency(cursor));
			((TextView)findViewById(R.id.country)).setText(Coin.getCountry(cursor));
			((TextView)findViewById(R.id.years)).setText(Coin.getYears(cursor));
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.coin_image:
			rotateView((ImageView)v);
			break;

		default:
			break;
		}
		
	}
	
	private void rotateView(final ImageView view) {
		mRotated = !mRotated;
		
		RotationCommand command = new RotationCommand() {
			public void execute() {
				if (mRotated) {
					view.setImageBitmap(mReverse);
				} else {
					view.setImageBitmap(mAverse);
				}
			}
		}; 
		mImageRotator.applyRotation(view, command);
	}
}
