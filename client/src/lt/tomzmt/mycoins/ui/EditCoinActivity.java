package lt.tomzmt.mycoins.ui;

import lt.tomzmt.mycoins.R;
import lt.tomzmt.mycoins.data.entities.Coin;
import lt.tomzmt.mycoins.ui.utils.ImageUtils;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
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

public class EditCoinActivity extends FragmentActivity implements LoaderCallbacks<Cursor>,
																 OnClickListener {

	private static final int REQUEST_TAKE_AVERSE_PHOTO = 1;
	private static final int REQUEST_TAKE_REVERSE_PHOTO = 2;
	
	public static final String TAG = EditCoinActivity.class.getSimpleName();
	
	public static final String CONTENT_URI = "AddCoinActivity.content_uri";
	public static final String COUNTRY_NAME = "AddCoinActivity.country_name";
	
	private static final int COIN_LOADER = 1003;
	
	/**
	 * 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_coin);
		
		findViewById(R.id.averse).setOnClickListener(this);
		findViewById(R.id.reverse).setOnClickListener(this);
		
		Intent intent = getIntent();
		Uri contentUri = intent.getParcelableExtra(CONTENT_URI);

		if (contentUri != null) {
			Bundle args = new Bundle();
			args.putParcelable(CONTENT_URI, contentUri);
			getSupportLoaderManager().initLoader(COIN_LOADER, args, this);
		} else {
			String country = intent.getStringExtra(COUNTRY_NAME);
			if (country != null) {
				((TextView)findViewById(R.id.country)).setText(country);
			} else {
				throw new IllegalArgumentException("coin uri or Country name is not provided");
			}
		}
	}
	
	/**
	 * 
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.coin_add, menu);
		return true;
	}

	/**
	 * 
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_save:
			
			ContentValues values = new ContentValues();
			
			String str = ((TextView)findViewById(R.id.currency)).getText().toString();
			values.put(Coin.CURRENCY, str);

			str = ((TextView)findViewById(R.id.denomination)).getText().toString();
			values.put(Coin.DENOMINATION, str);

			str = ((TextView)findViewById(R.id.years)).getText().toString();
			values.put(Coin.YEARS, str);

			str = ((TextView)findViewById(R.id.country)).getText().toString();
			values.put(Coin.COUNTRY, str);
			
			str = (String)findViewById(R.id.averse).getTag();
			if (str != null) {
				values.put(Coin.AVERSE, str);
			}
			
			str = (String)findViewById(R.id.reverse).getTag();
			if (str != null) {
				values.put(Coin.REVERSE, str);
			}
			
			Uri contentUri = getIntent().getParcelableExtra(CONTENT_URI);
			if (contentUri != null) {
				getContentResolver().update(contentUri, values, null, null);
			} else {
				getContentResolver().insert(Coin.DIR_URI, values);
			}
			
			Intent intent = new Intent(this, HomeActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			
			return true;

		case R.id.action_clear:

			((TextView)findViewById(R.id.denomination)).setText("");
			((TextView)findViewById(R.id.years)).setText("");
			((TextView)findViewById(R.id.country)).setText("");
			
			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if ((requestCode == REQUEST_TAKE_AVERSE_PHOTO || requestCode == REQUEST_TAKE_REVERSE_PHOTO) && data != null) {
			String str = data.getStringExtra(TakePhotoActivity.RESULT_IMAGE_PATH);
			
			Bitmap bmp = ImageUtils.loadBitmapForDetails(str);
			
			ImageView img = null;
			if (requestCode == REQUEST_TAKE_AVERSE_PHOTO) {
				img = (ImageView)findViewById(R.id.averse);
			} else {
				img = (ImageView)findViewById(R.id.reverse);
			}
			img.setImageBitmap(bmp);
			img.setTag(str);
		}
	}
	
	/**
	 * 
	 */
	@Override
	public void onClick(View v) {
		Intent intent = new Intent(this, TakePhotoActivity.class);
		switch (v.getId()) {
		case R.id.averse:
			startActivityForResult(intent, REQUEST_TAKE_AVERSE_PHOTO);
			break;
		case R.id.reverse:
			startActivityForResult(intent, REQUEST_TAKE_REVERSE_PHOTO);
			break;
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
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		
		if (cursor.moveToFirst()) {
					
			String denomination = Coin.getDenomination(cursor);
			((TextView)findViewById(R.id.denomination)).setText(denomination);
        
			String currency = Coin.getCurrency(cursor);
			((TextView)findViewById(R.id.currency)).setText(currency);
        
			String country = Coin.getCountry(cursor);
			((TextView)findViewById(R.id.country)).setText(country);
        
			String years = Coin.getYears(cursor);
			((TextView)findViewById(R.id.years)).setText(years);
			
			String averse = Coin.getAverse(cursor);
			if (averse != null) {
				ImageView img = (ImageView)findViewById(R.id.averse);
				Bitmap bmp = ImageUtils.loadBitmapForDetails(averse);
				img.setImageBitmap(bmp);
			}
			
			String reverse = Coin.getReverse(cursor);
			if (reverse != null) {
				ImageView img = (ImageView)findViewById(R.id.reverse);
				Bitmap bmp = ImageUtils.loadBitmapForDetails(reverse);
				img.setImageBitmap(bmp);
			}
		}
	}
}