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
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class CoinsListActivity extends FragmentActivity implements OnItemClickListener,
																   OnItemLongClickListener,
														   		   LoaderCallbacks<Cursor>,
														   		   ActionMode.Callback {

	static final String TAG = CoinsListActivity.class.getSimpleName();
	
	private static final int COIN_LIST_LOADER = 1001;
	
	private static final String PARAM_SEARCH_QUERY = "CoinsListActivity.search_query";
	
	private CoinCursorAdapter mAdapter = null;
	
	private ActionMode mActionMode = null;
	
	/**
	 * 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_coins_list);
		
		mAdapter = new CoinCursorAdapter(getApplicationContext(), null);
		
		ListView view = (ListView)findViewById(R.id.list);
		view.setOnItemClickListener(this);
		view.setOnItemLongClickListener(this);
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
			
			String query = intent.getStringExtra(SearchManager.QUERY);
			Bundle args = new Bundle();
			args.putString(PARAM_SEARCH_QUERY, query);
			getSupportLoaderManager().restartLoader(COIN_LIST_LOADER, args, this);
			
		} else if (action != null && action.equals(Intent.ACTION_VIEW)) {
			
			Uri data = intent.getData();
			Intent newIntent = new Intent(this, CoinDetailsActivity.class);
			newIntent.putExtra(CoinDetailsActivity.CONTENT_URI, data);
			startActivity(newIntent);
		} else {
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
		case R.id.action_edit:
	        // Start the CAB using the ActionMode.Callback defined above
	        mActionMode = startActionMode(this);
	        ListView listView = (ListView)findViewById(R.id.list);
	        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	        listView.setItemsCanFocus(false);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	/**
	 * 
	 */
	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		 if (mActionMode == null) {
		
			 Intent intent = new Intent(this, CoinDetailsActivity.class);
			 Uri uri = ContentUris.withAppendedId(Coin.DIR_URI, id);
			 intent.putExtra(CoinDetailsActivity.CONTENT_URI, uri);
			 startActivity(intent);
			 
		 } else {
			 
			 ListView listView = (ListView)adapterView;
			 SparseBooleanArray checkedItems = listView.getCheckedItemPositions();
			 
			 boolean checked = checkedItems.get(position);			 
		     listView.setItemChecked(position, checked);
		 }
	}
	
	/**
	 * 
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
		
        if (mActionMode != null) {
            return false;
        }

        // Start the CAB using the ActionMode.Callback defined above
        mActionMode = startActionMode(this);
        ListView listView = (ListView)adapterView;
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setItemsCanFocus(false);
        listView.setItemChecked(position, true);
		
		return true;
	}
	
	/**
	 * Called when the action mode is created; startActionMode() was called
	 */
    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
        return true;
    }

    /**
     * Called each time the action mode is shown. Always called after onCreateActionMode, but
     * may be called multiple times if the mode is invalidated.
     */ 
    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false; // Return false if nothing is done
    }
    
    /**
     *  Called when the user selects a contextual menu item
     */
    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:

   			 	ListView listView = (ListView)findViewById(R.id.list);
   			 	SparseBooleanArray checkedItems = listView.getCheckedItemPositions();
   			 	
   			 	int count = checkedItems.size();
   			 	String selection = "";
   			 	String[] args = new String[count]; 
   			 	for (int i = 0; i < checkedItems.size(); i++) {
   			 		if (selection.length() != 0) {
   			 			selection += ", ";
   			 		}
   			 		selection += "?";
   			 		args[i] = String.valueOf(mAdapter.getItemId(checkedItems.keyAt(i)));
   			 	}
            	
            	listView.clearChoices();
   			 	
            	getContentResolver().delete(Coin.DIR_URI, Coin.ID + " IN (" + selection + ")", args);
            	
                return true;
            default:
                return false;
        }
    }
    
    /**
     *  Called when the user exits the action mode
     */
    @Override
    public void onDestroyActionMode(ActionMode mode) {
        final ListView listView = (ListView)findViewById(R.id.list);
        SparseBooleanArray checkedItems = listView.getCheckedItemPositions();
        for (int i = 0; i < checkedItems.size(); i++) {
        	listView.setItemChecked(i, false);
        }
        listView.clearChoices();
        listView.post(new Runnable() {
            @Override
            public void run() {
            	listView.setChoiceMode(ListView.CHOICE_MODE_NONE);
            }
        });
        mActionMode = null;
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
