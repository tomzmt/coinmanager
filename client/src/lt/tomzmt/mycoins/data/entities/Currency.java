package lt.tomzmt.mycoins.data.entities;

import android.database.Cursor;

public class Currency {
	public static final String TABLE_NAME = "currency";
	
	public static final String ID = "_id";
	public static final String NAME = "name";
	public static final String CONTINENT = "continent";
	
	public static final String CREATE_TABLE_STATEMENT = "CREATE TABLE " +
			TABLE_NAME + " ( " +
		    ID + " INTEGER PRIMARY KEY, " +
		    NAME + " TEXT, " +
		    CONTINENT + " TEXT)";
	
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
}
