package lt.tomzmt.mycoins.data.entities;

import lt.tomzmt.mycoins.data.CoinsDataProvider;
import android.database.Cursor;
import android.net.Uri;

public class Country {
	public static final String TABLE_NAME = "countries";
	
	public static final Uri DIR_URI = Uri.withAppendedPath(CoinsDataProvider.BASE_URI, Country.TABLE_NAME);
	public static final Uri ITEM_URI = Uri.withAppendedPath(CoinsDataProvider.BASE_URI, Country.TABLE_NAME + "/#");
	
	public static final String ID = "_id";
	public static final String NAME = "name";
	public static final String CONTINENT = "continent";
	public static final String CURRENCY_MAIN = "currency_main";
	public static final String CURRENCY_SECONDARY = "currency_secondary";
	
	public static final String CREATE_TABLE_STATEMENT = "CREATE TABLE " +
			TABLE_NAME + " ( " +
		    ID + " INTEGER PRIMARY KEY, " +
		    NAME + " TEXT, " +
		    CONTINENT + " TEXT, " +
		    CURRENCY_MAIN + " TEXT, " +
		    CURRENCY_SECONDARY + " Text)";
	
	public static long getId(Cursor cursor) {
        int index = cursor.getColumnIndex(ID);
        return cursor.getLong(index);
	}

	public static String getName(Cursor cursor) {
        int index = cursor.getColumnIndex(NAME);
        return cursor.getString(index);
	}

	public static String getContinent(Cursor cursor) {
        int index = cursor.getColumnIndex(CONTINENT);
        return cursor.getString(index);
	}
	
	public static String getMainCurrency(Cursor cursor) {
        int index = cursor.getColumnIndex(CURRENCY_MAIN);
        return cursor.getString(index);
    }
	
	public static String getSecondaryCurrency(Cursor cursor) {
        int index = cursor.getColumnIndex(CURRENCY_SECONDARY);
        return cursor.getString(index);
    }
}
