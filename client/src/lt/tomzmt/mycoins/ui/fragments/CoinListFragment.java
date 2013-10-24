package lt.tomzmt.mycoins.ui.fragments;

import static lt.tomzmt.mycoins.ui.HomeActivity.COIN_LIST_LOADER;

import lt.tomzmt.mycoins.R;
import lt.tomzmt.mycoins.data.entities.Coin;
import lt.tomzmt.mycoins.ui.CoinDetailsActivity;
import lt.tomzmt.mycoins.ui.SelectCountryActivity;
import lt.tomzmt.mycoins.ui.utils.CoinCursorAdapter;
import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class CoinListFragment extends Fragment implements OnItemClickListener,
														  OnItemLongClickListener,
														  LoaderCallbacks<Cursor>,
														  ActionMode.Callback,
														  TextWatcher {

	public static final String TAG = CoinListFragment.class.getSimpleName();

	private static final String PARAM_SEARCH_QUERY = "CoinListFragment.key_word";

	private CoinCursorAdapter mAdapter = null;

	private ActionMode mActionMode = null;
	
	/**
	 * 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		
		mAdapter = new CoinCursorAdapter(getActivity().getApplicationContext(), null);
	}

	/**
	 * 
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.list_searchable, container, false);
		
		((EditText)view.findViewById(R.id.search_field)).addTextChangedListener(this);
		
		ListView list = (ListView)view.findViewById(R.id.list);
		list.setOnItemClickListener(this);
		list.setOnItemLongClickListener(this);
		list.setAdapter(mAdapter);
		
		return view;

	}

	/**
	 * 
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		getLoaderManager().initLoader(COIN_LIST_LOADER, null, this);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.coin_list, menu);
	}

	/**
	 * 
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_add:
			Intent intent = new Intent(getActivity(), SelectCountryActivity.class);
			startActivity(intent);
			return true;
		case R.id.action_edit:
			mActionMode = getActivity().startActionMode(this);
			ListView listView = (ListView)getView().findViewById(R.id.list);
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

			Intent intent = new Intent(getActivity(), CoinDetailsActivity.class);
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
		mActionMode = getActivity().startActionMode(this);
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
	 * Called when the user selects a contextual menu item
	 */
	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_delete:

			ListView listView = (ListView)getView().findViewById(R.id.list);
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

			getActivity().getContentResolver().delete(Coin.DIR_URI, Coin.ID + " IN (" + selection + ")", args);

			return true;
		default:
			return false;
		}
	}

	/**
	 * Called when the user exits the action mode
	 */
	@Override
	public void onDestroyActionMode(ActionMode mode) {
		final ListView listView = (ListView)getView().findViewById(R.id.list);
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
				return new CursorLoader(getActivity(), Coin.DIR_URI, null, Coin.CURRENCY + " LIKE ? OR " + Coin.COUNTRY + " LIKE ?", new String[]{key, key}, null);
			} else {
				return new CursorLoader(getActivity(), Coin.DIR_URI, null, null, null, null);
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
	
    /**
     * From TextWatcher
     */
	@Override
	public void afterTextChanged(Editable s) {
		Bundle args = null;
		if (s.length() > 0) {
			args = new Bundle();
			args.putString(PARAM_SEARCH_QUERY, s.toString());
		}
		getLoaderManager().restartLoader(COIN_LIST_LOADER, args, this);
	}
		
    /**
     * From TextWatcher
     */
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
		
    /**
     * From TextWatcher
     */
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {};
}
