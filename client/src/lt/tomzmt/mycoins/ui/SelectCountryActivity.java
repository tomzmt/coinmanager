package lt.tomzmt.mycoins.ui;

import lt.tomzmt.mycoins.ui.fragments.CountryListFragment;
import lt.tomzmt.mycoins.ui.fragments.CountryListFragment.OnCountrySelectListener;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

public class SelectCountryActivity extends Activity implements OnCountrySelectListener {

	/**
	 * 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		CountryListFragment fragment = new CountryListFragment();
		fragment.setOnCountrySelectListener(this);
		
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.add(android.R.id.content, fragment, CountryListFragment.TAG);
		ft.commit();
	}

	/**
	 * 
	 */
	@Override
	public void onCountrySelect(long id, String name) {
		Intent intent = new Intent(this, EditCoinActivity.class);
		intent.putExtra(EditCoinActivity.COUNTRY_NAME, name);
		startActivity(intent);
	}
}
