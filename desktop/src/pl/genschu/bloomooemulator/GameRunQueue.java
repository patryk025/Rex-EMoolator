package pl.genschu.bloomooemulator;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Runs game launches on the process main thread. The LWJGL3/GLFW loop must not
 * run on the Swing EDT: a blocked EDT defers every queued AWT dialog — e.g. the
 * print-to-file prompt of PDF printers — until the game window closes.
 */
public final class GameRunQueue {
	private static final BlockingQueue<Runnable> QUEUE = new LinkedBlockingQueue<>();

	private GameRunQueue() {
	}

	/** Enqueues a game launch to run on the main thread. */
	public static void submit(Runnable launch) {
		QUEUE.add(launch);
	}

	/** Blocks the calling (main) thread forever, executing submitted launches. */
	public static void runLoop() {
		while (true) {
			try {
				QUEUE.take().run();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return;
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}
	}
}
