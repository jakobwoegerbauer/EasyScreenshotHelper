/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.jakobwoegerbauer.easyscreenshothelper;

import java.awt.Point;
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
	
	public HelperStateManager(String saveDirectory, MainApp main) {
		this.saveDirectory = saveDirectory;
		this.main = main;
	}

	@Override
	public void update(Observable o, Object arg) {
		try {
			if (o.getClass().equals(GlobalMouseListener.class)) {
				lastScreenshot = ScreenshotHelper.takeScreenshot();
				lastPosition = ((NativeMouseEvent) arg).getPoint();
				if(saveAll && lastScreenshot != null){
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

	private void saveImage(BufferedImage image, File file) throws IOException {
		ImageIO.write(image, "png", file);
		//TODO mark lastPoint on image
	}

	void setSaveAll(boolean save) {
		setSaveAll = save;
		if(!save){
			saveAll = save;
		}
	}
}
