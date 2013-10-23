package lt.tomzmt.mycoins.data;

import lt.tomzmt.mycoins.data.entities.Coin;
import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class SearchSuggestContentProvider extends ContentProvider {

	private static final String AUTHORITY = "lt.tomzmt.mycoins.data.SearchSuggestContentProvider";
	
	private static final int SEARCH_BY_CURRENCY = 0;
	
	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	
	static {
		sURIMatcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_BY_CURRENCY);
        sURIMatcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH_BY_CURRENCY);
        sURIMatcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/#/*", SEARCH_BY_CURRENCY);
    }
	
	private SimpleDbOpener mOpenHelper;
	
	/**
	 * 
	 */
	@Override
	public String getType(Uri uri) {
		switch (sURIMatcher.match(uri)) {
		case SEARCH_BY_CURRENCY:
			return "vnd.android.cursor.dir/" + Coin.DENOMINATION;
		default:
			return null;
		}
	}
	
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
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		
		switch (sURIMatcher.match(uri)) {
		case SEARCH_BY_CURRENCY:
			
			String keywork = uri.getLastPathSegment();
			
			projection = new String[] {
				Coin.ID,
				Coin.DENOMINATION,
				Coin.CURRENCY,
				Coin.COUNTRY,
				Coin.YEARS
			};
			
			SQLiteDatabase db = mOpenHelper.getReadableDatabase();
			final String limit = uri.getQueryParameter(SearchManager.SUGGEST_PARAMETER_LIMIT);
			String key = "%" + keywork + "%";
			Cursor c = db.query(Coin.TABLE_NAME, projection, Coin.CURRENCY + " LIKE ? OR " + Coin.COUNTRY + " LIKE ?", new String[]{key, key}, null, null, null, limit);
			
			String[] columnNames = new String[] {
					"_id",
					SearchManager.SUGGEST_COLUMN_TEXT_1,
					SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID
			};
			
			MatrixCursor cursor = new MatrixCursor(columnNames, c.getCount());
			c.moveToFirst();
			while (!c.isAfterLast()) {
				String text = Coin.getDenomination(c) + " " + 
							  Coin.getCurrency(c) + ", " +
							  Coin.getCountry(c) + " " +
							  Coin.getYears(c);
			    
				long id = Coin.getId(c);
				cursor.addRow(new Object[]{id, text, id});
				
			    c.moveToNext();
			}
			
			return cursor;

		default:
			throw new IllegalArgumentException("Unknown URL " + uri);
		}
	}
	
	/**
	 * 
	 */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * 
	 */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * 
	 */
	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		throw new UnsupportedOperationException();
	}
}
