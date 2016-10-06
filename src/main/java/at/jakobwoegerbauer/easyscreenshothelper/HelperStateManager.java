/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.jakobwoegerbauer.easyscreenshothelper;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.jnativehook.mouse.NativeMouseEvent;

/**
 *
 * @author jakob
 */
public class HelperStateManager implements Observer {

	private MainApp main;
	private String saveDirectory;
	private BufferedImage lastScreenshot;
	private Point lastPosition;
	private int count = 0;
	private boolean saveAll;
	private boolean setSaveAll;
	private Rectangle lastWindowRectangle;

	public HelperStateManager(String saveDirectory, MainApp main) {
		this.saveDirectory = saveDirectory;
		this.main = main;
	}

	@Override
	public void update(Observable o, Object arg) {
		try {
			if (o.getClass().equals(GlobalMouseListener.class)) {
				if (((NativeMouseEvent) arg).getClickCount() == 1) {
					ScreenshotHelper.ScreenshotWrapper screenshotWrapper = ScreenshotHelper.takeScreenshot();
					lastScreenshot = screenshotWrapper.getImage();
					lastWindowRectangle = screenshotWrapper.getWindowRectangle();
					lastPosition = ((NativeMouseEvent) arg).getPoint();
					if (lastScreenshot != null) {
					markMousePosition(lastScreenshot, lastPosition, lastWindowRectangle, false);
					}
				} else if (lastScreenshot != null) {
					if (saveAll) {
						count--; // override screenshot with double click mark
					}
					markMousePosition(lastScreenshot, lastPosition, lastWindowRectangle, true);
				}
				if (saveAll && lastScreenshot != null) {
					saveImage(lastScreenshot, new File(saveDirectory + File.separator + (++count) + ".png"));
					lastScreenshot = null;
					main.onScreenshotSaved();
				}
			} else if (lastScreenshot != null) {
				saveImage(lastScreenshot, new File(saveDirectory + File.separator + (++count) + ".png"));
				lastScreenshot = null;
				main.onScreenshotSaved();
			}
			saveAll = setSaveAll;
		} catch (Exception e) {
			Logger.getLogger(HelperStateManager.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	private void markMousePosition(BufferedImage image, Point mousePosition, Rectangle windowRectangle, boolean doubleclick) {
		int radius = 10;

		Graphics2D g = image.createGraphics();

		g.setStroke(new BasicStroke(1));

		if (doubleclick) {
			g.setColor(new Color(0, 255, 0, 100));
			g.drawOval(mousePosition.x - windowRectangle.x - 4 * radius,
					mousePosition.y - windowRectangle.y - 4 * radius, 8 * radius, 8 * radius);
		} else {
			g.setColor(new Color(255, 0, 0, 100));
		}
		g.drawOval(mousePosition.x - windowRectangle.x - 3 * radius,
				mousePosition.y - windowRectangle.y - 3 * radius, 6 * radius, 6 * radius);

		g.setColor(new Color(255, 0, 0, 100));
		g.fillOval(mousePosition.x - windowRectangle.x - radius,
				mousePosition.y - windowRectangle.y - radius, 2 * radius, 2 * radius);

		g.setColor(Color.BLACK);
		g.fillOval(mousePosition.x - windowRectangle.x - radius / 4,
				mousePosition.y - windowRectangle.y - radius / 4, radius / 2, radius / 2);
		
		g.dispose();
		
		java.util.logging.Logger.getLogger("XXXXXXXXXXXXXXXXXXXXX").log(Level.INFO, "doubleclick: " + doubleclick);
	}

	private void saveImage(BufferedImage image, File file) throws IOException {
		ImageIO.write(image, "png", file);
	}

	void setSaveAll(boolean save) {
		setSaveAll = save;
		if (!save) {
			saveAll = save;
		}
	}
}
