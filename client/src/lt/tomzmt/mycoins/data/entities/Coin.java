package lt.tomzmt.mycoins.data.entities;

import lt.tomzmt.mycoins.data.CoinsDataProvider;
import android.database.Cursor;
import android.net.Uri;

public class Coin {

	public static final String TABLE_NAME = "coins";
	
	public static final Uri DIR_URI = Uri.withAppendedPath(CoinsDataProvider.BASE_URI, Coin.TABLE_NAME);
	public static final Uri ITEM_URI = Uri.withAppendedPath(CoinsDataProvider.BASE_URI, Coin.TABLE_NAME + "/#");
	
	public static final String ID = "_id";
	public static final String CURRENCY = "currency";
	public static final String DENOMINATION = "denomination";
	public static final String YEARS = "years";
	public static final String COUNTRY = "country";
	public static final String AVERSE = "averse";
	public static final String REVERSE = "reverse";
	
	public static final String CREATE_TABLE_STATEMENT = "CREATE TABLE " +
			TABLE_NAME + " ( " +
		    ID + " INTEGER PRIMARY KEY, " +
		    CURRENCY + " TEXT, " +
		    DENOMINATION + " TEXT, " +
		    YEARS + " INTEGER, " +
		    COUNTRY + " TEXT, " +
		    AVERSE + " TEXT, " +
		    REVERSE + " TEXT )";
	
	public static long getId(Cursor cursor) {
        int index = cursor.getColumnIndex(ID);
        return cursor.getLong(index);
	}

	public static String getCurrency(Cursor cursor) {
        int index = cursor.getColumnIndex(CURRENCY);
        return cursor.getString(index);	
	}

	public static String getDenomination(Cursor cursor) {
        int index = cursor.getColumnIndex(DENOMINATION);
        return cursor.getString(index);	
	}

	public static String getYears(Cursor cursor) {
        int index = cursor.getColumnIndex(YEARS);
        return cursor.getString(index);
	}

	public static String getCountry(Cursor cursor) {
        int index = cursor.getColumnIndex(COUNTRY);
        return cursor.getString(index);
	}

	public static String getAverse(Cursor cursor) {
        int index = cursor.getColumnIndex(AVERSE);
        return cursor.getString(index);
	}

	public static String getReverse(Cursor cursor) {
        int index = cursor.getColumnIndex(REVERSE);
        return cursor.getString(index);
	}
}
