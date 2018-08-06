package cn.joys.wifi.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.Display;
import android.view.WindowManager;

public class ImageUtil {

	public static Bitmap getResizedBitmap(BitmapDrawable drawable,
			Context context) {
		Bitmap bitmap = drawable.getBitmap();
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();

		int width = display.getWidth();
		int heigth = display.getHeight();
		if (width < 320 || heigth < 480) {
			return bitmap.createScaledBitmap(bitmap, 32, 32, false);
		} else {
			return bitmap.createScaledBitmap(bitmap, 48, 48, false);
		}

	}
}
