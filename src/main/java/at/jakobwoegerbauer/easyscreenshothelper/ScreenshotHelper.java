/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.jakobwoegerbauer.easyscreenshothelper;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jakob
 */
public class ScreenshotHelper {
	
	public static ScreenshotWrapper takeScreenshot() throws Exception {
		HWND hwnd = User32.INSTANCE.GetForegroundWindow();
		RECT rect = new RECT();
		User32.INSTANCE.GetWindowRect(hwnd, rect);
		Rectangle rectangle = rect.toRectangle();
		BufferedImage image = null;
		try {
			image = new Robot().createScreenCapture(rectangle);
		} catch (Exception e) {
			Logger.getLogger("ScreenshotHelper").log(Level.WARNING, "rectangle height is zero");
		}
		return new ScreenshotWrapper(image, rectangle);
	}
	
	public static class ScreenshotWrapper {
		private BufferedImage image;
		private Rectangle windowRectangle;

		public ScreenshotWrapper(BufferedImage image, Rectangle windowRectangle) {
			this.image = image;
			this.windowRectangle = windowRectangle;
		}

		public BufferedImage getImage() {
			return image;
		}

		public void setImage(BufferedImage image) {
			this.image = image;
		}

		public Rectangle getWindowRectangle() {
			return windowRectangle;
		}

		public void setWindowRectangle(Rectangle windowRectangle) {
			this.windowRectangle = windowRectangle;
		}
		
	}
}
