package lt.tomzmt.mycoins.ui;

import lt.tomzmt.mycoins.R;
import lt.tomzmt.mycoins.data.entities.Country;
import lt.tomzmt.mycoins.ui.utils.AlertUtils;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class NewCountryActivity extends Activity {

	/**
	 * 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_country);
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.continents, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner spinner = (Spinner)findViewById(R.id.continent);
		spinner.setAdapter(adapter);
	}
	
	/**
	 * 
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.new_country_menu, menu);
		return true;
	}
	
	/**
	 * 
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_save:
			
			String name = ((TextView)findViewById(R.id.name)).getText().toString();
			if (name.length() == 0) {
				AlertUtils.showAlert(this, getString(R.string.error_empty_country_name));
				return true;
			}
			
			String mainCurrency = ((TextView)findViewById(R.id.main_currency)).getText().toString();
			if (mainCurrency.length() == 0) {
				AlertUtils.showAlert(this, getString(R.string.error_empty_main_currency));
				return true;
			}			
			
			String secondaryCurrency = ((TextView)findViewById(R.id.secondary_currency)).getText().toString();
			if (secondaryCurrency.length() == 0) {
				AlertUtils.showAlert(this, getString(R.string.error_empty_main_currency));
				return true;
			}	
			
			ContentValues values = new ContentValues();			
			values.put(Country.NAME, name);
			values.put(Country.CURRENCY_MAIN, mainCurrency);
			values.put(Country.CURRENCY_SECONDARY, secondaryCurrency);

			String continent = (String)((Spinner)findViewById(R.id.continent)).getSelectedItem();
			values.put(Country.CONTINENT, continent);
			
			getContentResolver().insert(Country.DIR_URI, values);
			
			Intent intent = new Intent(this, EditCoinActivity.class);
			intent.putExtra(EditCoinActivity.COUNTRY_NAME, name);
			startActivity(intent);
			finish();
			
			return true;

		default:
			return super.onMenuItemSelected(featureId, item);
		}
	}
}
