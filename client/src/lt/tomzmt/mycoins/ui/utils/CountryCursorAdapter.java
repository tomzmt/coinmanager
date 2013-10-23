package lt.tomzmt.mycoins.ui.utils;

import lt.tomzmt.mycoins.R;
import lt.tomzmt.mycoins.data.entities.Country;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CountryCursorAdapter extends CursorAdapter {

	/**
	 * 
	 * @param context
	 */
	public CountryCursorAdapter(Context context) {
		super(context, null, 0);
	}
	
	/**
	 * 
	 */
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup root) {
		LayoutInflater inflater = LayoutInflater.from(context);
		return inflater.inflate(R.layout.country_list_item, root, false);
	}
	
	/**
	 * 
	 */
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		String name = Country.getName(cursor);
		((TextView)view.findViewById(R.id.title)).setText(name);
	}
}
