package lt.tomzmt.mycoins.ui.utils;

import lt.tomzmt.mycoins.R;
import android.app.AlertDialog;
import android.content.Context;

public class AlertUtils {

	public static void showAlert(Context context, String massage) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.app_name);
		builder.setMessage(massage);
		builder.setNegativeButton(R.string.ok, null);
		builder.create().show();
	}
}
