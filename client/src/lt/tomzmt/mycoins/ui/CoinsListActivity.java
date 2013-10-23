package lt.tomzmt.mycoins.ui;

import lt.tomzmt.mycoins.R;
import lt.tomzmt.mycoins.data.entities.Coin;
import lt.tomzmt.mycoins.ui.utils.CoinCursorAdapter;
import android.app.SearchManager;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class CoinsListActivity extends FragmentActivity implements OnItemClickListener,
														   		   LoaderCallbacks<Cursor>{

	private static final int COIN_LIST_LOADER = 1001;
	
	private static final String PARAM_SEARCH_QUERY = "CoinsListActivity.search_query";
	
	private static final String TAG = CoinsListActivity.class.getSimpleName();
	
	private CoinCursorAdapter mAdapter = null;
	
	/**
	 * 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_coins_list);
		
		((ListView)findViewById(R.id.list)).setOnItemClickListener(this);
		
		mAdapter = new CoinCursorAdapter(getApplicationContext(), null);
		
		ListView view = (ListView)findViewById(R.id.list);
		view.setAdapter(mAdapter);
		
		Intent intent = getIntent();
		handleIntent(intent);
	}

	/**
	 * 
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		handleIntent(intent);
	}
	
	/**
	 * 
	 * @param intent
	 */
	private void handleIntent(Intent intent) {
		String action = intent.getAction();
		if (action != null && action.equals(Intent.ACTION_SEARCH)) {
			Log.i(TAG, "so search");
			String query = intent.getStringExtra(SearchManager.QUERY);
			Bundle args = new Bundle();
			args.putString(PARAM_SEARCH_QUERY, query);
			getSupportLoaderManager().restartLoader(COIN_LIST_LOADER, args, this);
		} else if (action != null && action.equals(Intent.ACTION_VIEW)) {
			Log.i(TAG, "show details");
			Uri data = intent.getData();
			Intent newIntent = new Intent(this, CoinDetailsActivity.class);
			newIntent.putExtra(CoinDetailsActivity.CONTENT_URI, data);
			startActivity(newIntent);
		} else {
			Log.i(TAG, "show all");
			getSupportLoaderManager().initLoader(COIN_LIST_LOADER, null, this);
		}
	}
	
	/**
	 * 
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.coin_list, menu);
		return true;
	}

	/**
	 * 
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_add:
			Intent intent = new Intent(this, SelectCountryActivity.class);
			startActivity(intent);
			return true;
		case R.id.action_search:
			onSearchRequested();
			return true;
		case R.id.action_reset:
			getSupportLoaderManager().restartLoader(COIN_LIST_LOADER, null, this);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	/**
	 * 
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
		Log.i(TAG, "ID: " + id);
		Intent intent = new Intent(this, CoinDetailsActivity.class);
		Uri uri = ContentUris.withAppendedId(Coin.DIR_URI, id);
		intent.putExtra(CoinDetailsActivity.CONTENT_URI, uri);
		startActivity(intent);
	}
	
	/**
	 * 
	 */
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		if (id == COIN_LIST_LOADER) {
			if (bundle != null) {
				String keyword = bundle.getString(PARAM_SEARCH_QUERY);
				String key = "%" + keyword + "%"; 
				return new CursorLoader(this, Coin.DIR_URI, null, Coin.CURRENCY + " LIKE ? OR " + Coin.COUNTRY + " LIKE ?", new String[]{key, key}, null);
			} else {
				return new CursorLoader(this, Coin.DIR_URI, null, null, null, null);
			}
		}
		return null;	
	}
	
	/**
	 * 
	 */
	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		mAdapter.swapCursor(null);
	}
	
	/**
	 * 
	 */
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mAdapter.swapCursor(data);
	}
}
