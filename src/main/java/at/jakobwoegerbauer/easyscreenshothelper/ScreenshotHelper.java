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
	private static final int RADIUS = 10;
	
	public static BufferedImage takeScreenshot(Point mousePosition) throws Exception {
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
		if(image != null){
			Graphics2D g = image.createGraphics();
			
			g.setStroke(new BasicStroke(1));

			g.setColor(new Color(255, 0, 0, 100));
			g.drawOval(mousePosition.x - rectangle.x - 3*RADIUS,
					mousePosition.y - rectangle.y - 3*RADIUS, 6*RADIUS, 6*RADIUS);
			
			g.setColor(new Color(255, 0, 0, 100));			
			g.fillOval(mousePosition.x - rectangle.x - RADIUS,
					mousePosition.y - rectangle.y - RADIUS, 2*RADIUS, 2*RADIUS);			
			
			g.setColor(Color.BLACK);
			g.fillOval(mousePosition.x - rectangle.x - RADIUS/4,
					mousePosition.y - rectangle.y - RADIUS/4, RADIUS/2,RADIUS/2);
		}
		return image;
	}
}
