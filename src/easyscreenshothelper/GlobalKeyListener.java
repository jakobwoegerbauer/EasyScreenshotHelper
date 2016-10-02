/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyscreenshothelper;

import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

/**
 *
 * @author jakob
 */
public class GlobalKeyListener extends Observable implements NativeKeyListener {
	private final int keyCode;
	private boolean controlDown;
	
	private static final int CONTROL_KEY = 29;
	
	public GlobalKeyListener(Configuration config){
		this.keyCode = config.getKeyCode();
		this.controlDown = false;
	}
	
	@Override
	public void nativeKeyPressed(NativeKeyEvent nke) {
		if(nke.getKeyCode() == CONTROL_KEY){
			controlDown = true;
			Logger.getLogger("GlobalKeyListener").log(Level.SEVERE, "control key down");
		}
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent nke) {
		if(nke.getKeyCode() == CONTROL_KEY){
			controlDown = false;
			Logger.getLogger("GlobalKeyListener").log(Level.SEVERE, "control key up");
			return;
		}
		if(nke.getKeyCode()== keyCode && controlDown){
			Logger.getLogger("GlobalKeyListener").log(Level.SEVERE, "command detected");
			setChanged();
			notifyObservers();
		}
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent nke) {
		
	}
	
}
