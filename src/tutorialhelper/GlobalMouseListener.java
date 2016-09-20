/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tutorialhelper;

import java.util.Observable;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseListener;

/**
 *
 * @author jakob
 */
public class GlobalMouseListener extends Observable implements NativeMouseListener {

	@Override
	public void nativeMouseClicked(NativeMouseEvent nme) {		
		setChanged();
		notifyObservers(nme);
	}

	@Override
	public void nativeMousePressed(NativeMouseEvent nme) {
		
	}

	@Override
	public void nativeMouseReleased(NativeMouseEvent nme) {
		
	}
}
