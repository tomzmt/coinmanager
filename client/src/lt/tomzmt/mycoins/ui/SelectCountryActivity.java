package lt.tomzmt.mycoins.ui;

import lt.tomzmt.mycoins.R;
import lt.tomzmt.mycoins.data.entities.Country;
import lt.tomzmt.mycoins.ui.utils.AlertUtils;
import lt.tomzmt.mycoins.ui.utils.CountryCursorAdapter;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class SelectCountryActivity extends FragmentActivity implements TextWatcher,
																	   LoaderCallbacks<Cursor>,
																	   OnItemClickListener {

	private static final int COUNTRIES_LOADER = 1001;
	
	private static final String KEY_WORD = "SelectCountryActivity.key_word";
	
	private static final String[] COUNTRY_PROJECTION = new String[] {
		Country.ID,
		Country.NAME
	};
	
	private CountryCursorAdapter mAdapter = null;
	
	/**
	 * 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_country);
		
		((EditText)findViewById(R.id.search_field)).addTextChangedListener(this);
		
		mAdapter = new CountryCursorAdapter(getApplicationContext());
		
		ListView list = (ListView)findViewById(R.id.list);
		list.setAdapter(mAdapter);
		list.setOnItemClickListener(this);
		
		getSupportLoaderManager().initLoader(COUNTRIES_LOADER, null, this);
	}
	
	/**
	 * 
	 */
	@Override
	public void afterTextChanged(Editable s) {
		Bundle args = null;
		if (s.length() > 0) {
			args = new Bundle();
			args.putString(KEY_WORD, s.toString());
		}
		getSupportLoaderManager().restartLoader(COUNTRIES_LOADER, args, this);
	}
	
	/**
	 * 
	 */
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
	
	/**
	 * 
	 */
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {};
	
	/**
	 * 
	 */
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		switch (id) {
		case COUNTRIES_LOADER:
			
			String selection = null;
			String[] selectionArgs = null; 
			
			if (args != null) {
				String key = args.getString(KEY_WORD);
				selection = Country.NAME + " LIKE ?";
				selectionArgs = new String[] {"%" + key + "%"};
			}
			
			return new CursorLoader(this, Country.DIR_URI, COUNTRY_PROJECTION, selection, selectionArgs, Country.NAME + " ASC");
		default:
			throw new IllegalArgumentException("Undefined loader id");
		}
	}
	
	/**
	 * 
	 */
	@Override
	public void onLoaderReset(Loader<Cursor> cursor) {
		mAdapter.swapCursor(null);
	}
	
	/**
	 * 
	 */
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		String searchKey = ((TextView)findViewById(R.id.search_field)).getText().toString();
		if (cursor.getCount() == 0 && searchKey.length() == 0) {
			addNewCountry();
		} else {
			mAdapter.swapCursor(cursor);
		}
	}
	
	/**
	 * 
	 */
	@Override
	public void onItemClick(AdapterView<?> list, View view, int position, long id) {
		Cursor cursor = (Cursor)mAdapter.getItem(position);
		
		Intent intent = new Intent(this, EditCoinActivity.class);
		intent.putExtra(EditCoinActivity.COUNTRY_NAME, Country.getName(cursor));
		startActivity(intent);
	}
	
	/**
	 * 
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.select_country_menu, menu);
		return true;
	}
	
	/**
	 * 
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_add:
			addNewCountry();
			return true;

		default:
			return super.onMenuItemSelected(featureId, item);
		}
	}
	
	/**
	 * 
	 */
	private void addNewCountry() {
		startActivity(new Intent(this, NewCountryActivity.class));
	}
}
