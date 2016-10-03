/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.jakobwoegerbauer.easyscreenshothelper;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jakob
 */
public class ScreenshotHelper {

	public static BufferedImage takeScreenshot() throws Exception {
		HWND hwnd = User32.INSTANCE.GetForegroundWindow();
		RECT rect = new RECT();
		User32.INSTANCE.GetWindowRect(hwnd, rect);
		BufferedImage image = null;
		try {
			image = new Robot().createScreenCapture(rect.toRectangle());
		} catch (Exception e) {
			Logger.getLogger("ScreenshotHelper").log(Level.WARNING, "rectangle height is zero");
		}
		return image;
	}
}
