/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tutorialhelper;

import java.util.Observable;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

/**
 *
 * @author jakob
 */
public class GlobalKeyListener extends Observable implements NativeKeyListener {
	private final int keyCode;
	
	public GlobalKeyListener(Configuration config){
		this.keyCode = config.getKeyCode();
	}
	
	@Override
	public void nativeKeyPressed(NativeKeyEvent nke) {
		
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent nke) {
		
		if(nke.getKeyCode()== keyCode){
			setChanged();
			notifyObservers();
		}
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent nke) {
		
	}
	
}
