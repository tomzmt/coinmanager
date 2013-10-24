package lt.tomzmt.mycoins.ui.utils;

import lt.tomzmt.mycoins.R;
import lt.tomzmt.mycoins.data.entities.Coin;
import android.app.ActivityManager;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class CoinCursorAdapter extends CursorAdapter {

	private final ThumbnailCashe mCashe; 
	
	/**
	 * 
	 * @param context
	 * @param cursor
	 */
	public CoinCursorAdapter(Context context, Cursor cursor) {
		super(context, cursor, 0);
		
		ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		int memmoryClassBytes = am.getMemoryClass() * 1028 * 1028;
		mCashe = new ThumbnailCashe(memmoryClassBytes / 8);
	}
	
	/**
	 * 
	 */
	@Override
	public View newView(Context context, Cursor arg1, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View row = inflater.inflate(R.layout.coin_list_item, parent, false);
		return row;
	}
	
	/**
	 * 
	 */
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		int position = cursor.getPosition();
		if (position % 2 == 0) {
			view.setBackgroundResource(R.drawable.list_item_even_selector);
		} else {
			view.setBackgroundResource(R.drawable.list_item_odd_selector);
		}
		
        TextView title = (TextView)view.findViewById(R.id.title);
        TextView subtitle = (TextView)view.findViewById(R.id.subtitle);

        ImageView icon = (ImageView)view.findViewById(R.id.icon);
        String averse = Coin.getAverse(cursor);
        if (averse != null) {
        	
        	Bitmap bm = mCashe.get(averse);
        	if (bm == null) {
        		bm = ImageUtils.loadBitmapForList(averse);
        		mCashe.put(averse, bm);
        	}
        	icon.setImageBitmap(bm);
        } else {
        	icon.setImageResource(android.R.drawable.ic_menu_gallery);
        }
        
        String denomination = Coin.getDenomination(cursor);
        String currency = Coin.getCurrency(cursor);
        String country = Coin.getCountry(cursor);
        String years = Coin.getYears(cursor);
        
        title.setText(denomination + " " + currency);
        subtitle.setText(country + ", " + years);
	}
}