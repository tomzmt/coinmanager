package lt.tomzmt.mycoins.ui;

import lt.tomzmt.mycoins.R;
import lt.tomzmt.mycoins.ui.fragments.CoinListFragment;
import lt.tomzmt.mycoins.ui.fragments.CountryListFragment;
import lt.tomzmt.mycoins.ui.fragments.CountryListFragment.OnCountrySelectListener;
import lt.tomzmt.mycoins.ui.utils.TabNavigationHandler;
import lt.tomzmt.mycoins.ui.utils.TabNavigationHandler.FragmentProvider;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

public class HomeActivity extends Activity implements FragmentProvider,
													  OnCountrySelectListener {

	public static final int COIN_LIST_LOADER = 1001;
	
	public static final int COUNTRIES_LOADER = 1002;

	private static final String SELECTED_TAB = "MainActivity.selectedTab";
	
	/**
	 * From Activity
	 */
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		TabNavigationHandler navigationHandler = new TabNavigationHandler(this);
		
		Tab tab1 = actionBar.newTab()
				.setTag(CoinListFragment.TAG)
				.setText(getString(R.string.navigation_item_1))
				.setTabListener(navigationHandler);
		actionBar.addTab(tab1);
		
		Tab tab2 = actionBar.newTab()
				.setTag(CountryListFragment.TAG)
				.setText(getString(R.string.navigation_item_2))
				.setTabListener(navigationHandler);
		actionBar.addTab(tab2);
		
		if (arg0 != null) {
			int position = arg0.getInt(SELECTED_TAB, 0);
			actionBar.setSelectedNavigationItem(position);
		}
	}
	
	/**
	 * From Activity
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(SELECTED_TAB, getActionBar().getSelectedNavigationIndex());
	}
	
	/**
	 * From FragmentProvider
	 */
	@Override
	public Fragment getFragment(String tag) {
		return getFragmentManager().findFragmentByTag(tag);
	}
	
	/**
	 * From FragmentProvider
	 */
	@Override
	public Fragment createFragment(String tag) {
		if (tag.equals(CoinListFragment.TAG)) {
			CoinListFragment coinListFragment = new CoinListFragment();
			return coinListFragment;
		} else if (tag.equals(CountryListFragment.TAG)) {
			CountryListFragment countryListFragment = new CountryListFragment();
			countryListFragment.setOnCountrySelectListener(this);
			return countryListFragment;
		} else {
			throw new RuntimeException("Requested TAG undefined");
		}
	}
	
	/**
	 * From OnCountrySelectListener
	 */
	@Override
	public void onCountrySelect(long id, String name) {
		
	}
}
