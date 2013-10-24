package lt.tomzmt.mycoins.ui.utils;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;

/**
 * 
 * @author tomzmt
 *
 */
public class TabNavigationHandler implements ActionBar.TabListener {
	
	private final FragmentProvider mProvider;
	
	public TabNavigationHandler(FragmentProvider provider) {
		mProvider = provider;
    }

	@Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
    	String tag = tab.getTag().toString();
    	Fragment fragment = mProvider.getFragment(tag);
        if (fragment == null) {
        	fragment = mProvider.createFragment(tag);
            ft.add(android.R.id.content, fragment, tag);
        } else {
            ft.attach(fragment);
        }
    }

	@Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    	String tag = tab.getTag().toString();
    	Fragment fragment = mProvider.getFragment(tag);
        if (fragment != null) {
            ft.detach(fragment);
        }
    }

	@Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
    }
	
    /**
     * 
     * @author tomzmt
     *
     */
	public interface FragmentProvider {
		public Fragment getFragment(String tag);
		public Fragment createFragment(String tag);
	}
}
