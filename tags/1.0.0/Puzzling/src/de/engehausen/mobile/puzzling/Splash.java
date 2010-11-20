package de.engehausen.mobile.puzzling;

import java.io.IOException;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.midlet.MIDletStateChangeException;

/**
 * A splash screen shown for a couple of seconds when
 * the application is starting up.
 */
public class Splash extends Canvas implements Constants {
	
	private final Image splashImage;
	private boolean go;

	/**
	 * Creates the splash canvas.
	 * @throws IOException in case of error.
	 */
	public Splash() throws IOException {
		super();
		splashImage = Image.createImage("/splash.png");
	}

	/**
	 * Shows the splash screen and then waits to be notified
	 * to terminate. A minimum time of about five seconds will
	 * be waited for displaying the splash screen.
	 * @param main the main control object.
	 */
	public void display(final Main main) {
		setFullScreenMode(true);
		main.setCurrent(this, true);
		final Thread thread = new Thread(new Runnable() {			
			public void run() {
				final long begin = System.currentTimeMillis();
				try {
					while (!go) {
						synchronized (splashImage) {
							splashImage.wait(5000L);
						}
					}
					while (System.currentTimeMillis()-begin<4000L) {
						Thread.sleep(500L);						
					}
				} catch (InterruptedException e) {
					// ignore
				}
				try {
					main.startApp();
				} catch (MIDletStateChangeException e) {
					// ignore
				}
			}
		});
		thread.start();
	}

	/**
	 * Indicate processing has been done and the splash
	 * screen may go away.
	 */
	public void terminate() {
		go = true;
		synchronized (splashImage) {
			splashImage.notify();			
		}
	}

	// non-javadoc: see superclass
	protected void paint(final Graphics g) {
		g.setColor(WHITE);
		final int width = getWidth();
		final int height = getHeight();
		g.fillRect(0, 0, width, height);
		g.drawImage(splashImage, (width-splashImage.getWidth())/2, (height-splashImage.getHeight())/2, POSITIONING);
	}
	
}
