/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyscreenshothelper;

import java.awt.Point;
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

	private String saveDirectory;
	private BufferedImage lastScreenshot;
	private Point lastPosition;
	private int count = 0;
	
	public HelperStateManager(String saveDirectory) {
		this.saveDirectory = saveDirectory;
	}

	@Override
	public void update(Observable o, Object arg) {
		try {
			if (o.getClass().equals(GlobalMouseListener.class)) {
				lastScreenshot = ScreenshotHelper.takeScreenshot();
				lastPosition = ((NativeMouseEvent)arg).getPoint();
			} else {
				saveImage(lastScreenshot, new File(saveDirectory + File.separator + (++count) + ".png"));
			}
		} catch (Exception e) {
			Logger.getLogger(HelperStateManager.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	private void saveImage(BufferedImage image, File file) throws IOException {
		ImageIO.write(image, "png", file);
		//TODO mark lastPoint on image
	}
}
