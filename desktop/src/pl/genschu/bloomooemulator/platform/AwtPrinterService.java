package pl.genschu.bloomooemulator.platform;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.DialogTypeSelection;
import javax.print.attribute.standard.OrientationRequested;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

/**
 * {@link PrinterService} backed by java.awt.print — one code path for Windows,
 * Linux and macOS. The modal system dialog runs on its own thread so the GL
 * loop keeps rendering; the game freezes the coloring itself via DISABLE/ENABLE
 * around PRINT, mirroring the original CPrinter flow. Pages are forced to
 * landscape, matching the original printouts.
 */
public class AwtPrinterService implements PrinterService {
	@Override
	public boolean isSupported() {
		return !GraphicsEnvironment.isHeadless();
	}

	@Override
	public void printImage(Pixmap image, String jobName) {
		// Copy the pixels before returning: the caller disposes the pixmap.
		BufferedImage page = toBufferedImage(image);
		Thread thread = new Thread(() -> print(page, jobName), "kolorowanka-print");
		thread.setDaemon(true);
		thread.start();
	}

	private static void print(BufferedImage image, String jobName) {
		try {
			PrinterJob job = PrinterJob.getPrinterJob();
			job.setJobName(jobName);
			job.setPrintable((graphics, pageFormat, pageIndex) -> {
				if (pageIndex > 0) {
					return Printable.NO_SUCH_PAGE;
				}
				double scale = Math.min(
						pageFormat.getImageableWidth() / image.getWidth(),
						pageFormat.getImageableHeight() / image.getHeight());
				int w = (int) (image.getWidth() * scale);
				int h = (int) (image.getHeight() * scale);
				int x = (int) (pageFormat.getImageableX() + (pageFormat.getImageableWidth() - w) / 2);
				int y = (int) (pageFormat.getImageableY() + (pageFormat.getImageableHeight() - h) / 2);
				Graphics2D g2 = (Graphics2D) graphics;
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
				g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.drawImage(image, x, y, w, h, null);
				return Printable.PAGE_EXISTS;
			});
			PrintRequestAttributeSet attrs = new HashPrintRequestAttributeSet();
			attrs.add(OrientationRequested.LANDSCAPE);
			// Keep the native dialog on Windows; other platforms ignore this
			// and fall back to the cross-platform one.
			attrs.add(DialogTypeSelection.NATIVE);
			if (job.printDialog(attrs)) {
				// Landscape-only, even if changed in the dialog
				attrs.add(OrientationRequested.LANDSCAPE);
				job.print(attrs);
			}
		} catch (PrinterException | RuntimeException e) {
			if (Gdx.app != null) {
				Gdx.app.error("AwtPrinterService", "Print failed", e);
			}
		}
	}

	private static BufferedImage toBufferedImage(Pixmap pixmap) {
		int width = pixmap.getWidth();
		int height = pixmap.getHeight();
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		int[] row = new int[width];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				// RGBA8888 -> 0x00RRGGBB (print page is opaque, alpha dropped)
				row[x] = pixmap.getPixel(x, y) >>> 8;
			}
			image.setRGB(0, y, width, 1, row, 0, width);
		}
		return image;
	}
}
