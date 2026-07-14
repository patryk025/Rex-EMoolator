package pl.genschu.bloomooemulator.platform;

import android.app.Activity;
import android.graphics.Bitmap;

import androidx.print.PrintHelper;

import com.badlogic.gdx.graphics.Pixmap;

/**
 * {@link PrinterService} backed by the Android print framework — the system
 * dialog offers printer selection and save-as-PDF out of the box.
 */
public class AndroidPrinterService implements PrinterService {
	private final Activity activity;

	public AndroidPrinterService(Activity activity) {
		this.activity = activity;
	}

	@Override
	public boolean isSupported() {
		return PrintHelper.systemSupportsPrint();
	}

	@Override
	public void printImage(Pixmap image, String jobName) {
		// Copy the pixels before returning: the caller disposes the pixmap.
		Bitmap bitmap = toBitmap(image);
		activity.runOnUiThread(() -> {
			PrintHelper helper = new PrintHelper(activity);
			helper.setScaleMode(PrintHelper.SCALE_MODE_FIT);
			helper.printBitmap(jobName, bitmap);
		});
	}

	private static Bitmap toBitmap(Pixmap pixmap) {
		int width = pixmap.getWidth();
		int height = pixmap.getHeight();
		int[] pixels = new int[width * height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int rgba = pixmap.getPixel(x, y);
				// RGBA8888 -> ARGB8888
				pixels[y * width + x] = (rgba << 24) | (rgba >>> 8);
			}
		}
		return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
	}
}
