/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyscreenshothelper;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

/**
 *
 * @author jakob
 */
public class ScreenshotHelper {

	public static BufferedImage takeScreenshot() throws Exception {
		HWND hwnd = User32.INSTANCE.GetForegroundWindow();
		RECT rect = new RECT();
		User32.INSTANCE.GetWindowRect(hwnd, rect);
		BufferedImage image = new Robot().createScreenCapture(rect.toRectangle());
		return image;		
	}
}
