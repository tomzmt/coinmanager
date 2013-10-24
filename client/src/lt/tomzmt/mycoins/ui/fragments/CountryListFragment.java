package lt.tomzmt.mycoins.ui.fragments;

import static lt.tomzmt.mycoins.ui.HomeActivity.COUNTRIES_LOADER;

import lt.tomzmt.mycoins.R;
import lt.tomzmt.mycoins.data.entities.Country;
import lt.tomzmt.mycoins.ui.NewCountryActivity;
import lt.tomzmt.mycoins.ui.utils.CountryCursorAdapter;
import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class CountryListFragment extends Fragment implements TextWatcher,
															 LoaderCallbacks<Cursor>,
															 OnItemClickListener {

	public static final String TAG = CountryListFragment.class.getSimpleName();
		
	private static final String KEY_WORD = "CountryListFragment.key_word";
		
	private static final String[] COUNTRY_PROJECTION = new String[] {
		Country.ID,
		Country.NAME
	};
		
	protected CountryCursorAdapter mAdapter = null;
	
	private OnCountrySelectListener mOnCountrySelectListener = null;
	
	/** 
	 * From Fragment.
	 * Called after onAttach().
	 * Called to do initial creation of the fragment.
	 */ 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	/**
	 * From Fragment.
	 * Called after onCreate()
	 * Creates and returns the view hierarchy associated with the fragment.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.list_searchable, container, false);
	
		((EditText)view.findViewById(R.id.search_field)).addTextChangedListener(this);
	
		mAdapter = new CountryCursorAdapter(getActivity().getApplicationContext());
		
		ListView list = (ListView)view.findViewById(R.id.list);
		list.setAdapter(mAdapter);
		list.setOnItemClickListener(this);
	
		return view;
	}
	
	/**
	 * From Fragment.
	 * Called after onCreateView().
	 * Tells the fragment that its activity has completed its own Activity.onCreate()
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getLoaderManager().initLoader(COUNTRIES_LOADER, null, this);
	}
	    
	/**
	 * From Fragment
	 */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    	inflater.inflate(R.menu.country_list, menu);
    }
    
	/**
	 * From Fragment
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_add:
			addNewCountry();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
    
    /**
     * From TextWatcher
     */
	@Override
	public void afterTextChanged(Editable s) {
		Bundle args = null;
		if (s.length() > 0) {
			args = new Bundle();
			args.putString(KEY_WORD, s.toString());
		}
		getLoaderManager().restartLoader(COUNTRIES_LOADER, args, this);
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
		
	/**
	 * From LoaderCallbacks
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
				
			return new CursorLoader(getActivity(), Country.DIR_URI, COUNTRY_PROJECTION, selection, selectionArgs, Country.NAME + " ASC");
		default:
			throw new IllegalArgumentException("Undefined loader id");
		}
	}
		
	/**
	 * From LoaderCallbacks
	 */
	@Override
	public void onLoaderReset(Loader<Cursor> cursor) {
		mAdapter.swapCursor(null);
	}
		
	/**
	 * From LoaderCallbacks
	 */
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		View view = getView();
		if (view != null) {
			String searchKey = ((TextView)view.findViewById(R.id.search_field)).getText().toString();
			if (cursor.getCount() == 0 && searchKey.length() == 0) {
				addNewCountry();
			} else {
				mAdapter.swapCursor(cursor);
			}
		} else {
			mAdapter.swapCursor(cursor);
		}
	}
		
	/**
	 * OnItemClickListener
	 */
	@Override
	public void onItemClick(AdapterView<?> list, View view, int position, long id) {
		Cursor cursor = (Cursor)mAdapter.getItem(position);
		
		if (mOnCountrySelectListener != null) {
			mOnCountrySelectListener.onCountrySelect(Country.getId(cursor), Country.getName(cursor));
		}
		
//		Intent intent = new Intent(getActivity(), EditCoinActivity.class);
//		intent.putExtra(EditCoinActivity.COUNTRY_NAME, Country.getName(cursor));
//		startActivity(intent);
	}
		
	/**
	 * 
	 */
	public void setOnCountrySelectListener(OnCountrySelectListener listener) {
		mOnCountrySelectListener = listener;
	}
	
	/**
	 * 
	 */
	private void addNewCountry() {
		startActivity(new Intent(getActivity(), NewCountryActivity.class));
	}
	
	/**
	 * 
	 * @author tomzmt
	 *
	 */
	public interface OnCountrySelectListener {
		public void onCountrySelect(long id, String name);
	}
}
