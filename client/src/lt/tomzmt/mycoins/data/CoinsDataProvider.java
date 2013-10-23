package lt.tomzmt.mycoins.data;

import lt.tomzmt.mycoins.data.entities.Coin;
import lt.tomzmt.mycoins.data.entities.Country;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class CoinsDataProvider extends ContentProvider {

	private static final String AUTHORITY = "lt.tomzmt.mycoins.provider";
	
	public static final Uri BASE_URI = new Uri.Builder().scheme("content").authority(AUTHORITY).build();
	
	private static final int COIN = 1;
	private static final int COIN_BY_ID = 2;
	private static final int COUNTRY = 3;
	private static final int COUNTRY_BY_ID = 4;
	private static final int CURRENCY = 5;
	private static final int CURRENCY_BY_ID = 6;
	
	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	
	static {
        sURIMatcher.addURI(AUTHORITY, Coin.TABLE_NAME, COIN);
        sURIMatcher.addURI(AUTHORITY, Coin.TABLE_NAME + "/#", COIN_BY_ID);
        sURIMatcher.addURI(AUTHORITY, Country.TABLE_NAME, COUNTRY);
        sURIMatcher.addURI(AUTHORITY, Country.TABLE_NAME + "/#", COUNTRY_BY_ID);
    }
	
	private SimpleDbOpener mOpenHelper;
	
	/**
	 * 
	 */
	@Override
	public boolean onCreate() {
		mOpenHelper = new SimpleDbOpener(getContext());
		return true;
	}

	/**
	 * 
	 * @param uri
	 * @return
	 */
	private String resolveTable(Uri uri) {
		switch (sURIMatcher.match(uri)) {
		case COIN:
		case COIN_BY_ID:
			return Coin.TABLE_NAME;
		case COUNTRY:
		case COUNTRY_BY_ID:
			return Country.TABLE_NAME;
		}
		return null;		
	}
	
	/**
	 * 
	 */
	@Override
	public String getType(Uri uri) {
		switch (sURIMatcher.match(uri)) {
			case COIN:
			case COUNTRY:
				return "vnd.android.cursor.dir/" + resolveTable(uri);
			case COIN_BY_ID:
			case COUNTRY_BY_ID:
				return "vnd.android.cursor.item/" + resolveTable(uri);
			default:
				return null;
		}
	}

	/**
	 * 
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		
		selection = updateSelectionForUri(uri, selection);
		
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor c = db.query(resolveTable(uri), projection, selection, selectionArgs, null, null, sortOrder);		
		c.setNotificationUri(getContext().getContentResolver(), uri);
		
		return c;
	}
	
	/**
	 * 
	 */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		db.insert(resolveTable(uri), null, values);
		getContext().getContentResolver().notifyChange(uri, null);
		
		return uri;
	}

	/**
	 * 
	 */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		
		selection = updateSelectionForUri(uri, selection);
		
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int result = db.delete(resolveTable(uri), selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		
		return result;
	}
	
	/**
	 * 
	 */
	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		
		selection = updateSelectionForUri(uri, selection);
		
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int result = db.update(resolveTable(uri), values, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		
		return result;
	}
	
	/**
	 * 
	 * @param uri
	 * @param selection
	 * @return
	 */
	private String updateSelectionForUri(Uri uri, String selection) {
		switch (sURIMatcher.match(uri)) {
			case COIN:
			case COUNTRY:
				break;
			case COIN_BY_ID:
			case COUNTRY_BY_ID:
				if (selection != null && selection.length() > 0) {
					selection = "_id=" + uri.getLastPathSegment() + " AND " + selection;
				} else {
					selection = "_id=" + uri.getLastPathSegment();
				}
				break;
			default:		
		}
		return selection;
	}
}
