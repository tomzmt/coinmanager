package lt.tomzmt.mycoins.data;

import lt.tomzmt.mycoins.data.entities.Coin;
import lt.tomzmt.mycoins.data.entities.Country;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SimpleDbOpener extends SQLiteOpenHelper {

	public final static String DATABASE_NAME = "Database.db";
	private final static int BADABASE_VERSION = 1;

	/**
	 * 
	 * @param cnt
	 */
	public SimpleDbOpener(Context cnt) {
		super(cnt, DATABASE_NAME, null, BADABASE_VERSION);
	}

	/**
	 * 
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(Coin.CREATE_TABLE_STATEMENT);
		db.execSQL(Country.CREATE_TABLE_STATEMENT);
		
	}
	
	/**
	 * 
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
}