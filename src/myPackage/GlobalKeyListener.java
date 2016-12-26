package myPackage;

import java.awt.AWTException;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class GlobalKeyListener implements NativeKeyListener {

    public void nativeKeyReleased(NativeKeyEvent e) {
    	if (e.getKeyCode() == NativeKeyEvent.VC_F1) {
    		Utils.showInfo();
    	}
    	
    	if (e.getKeyCode() == NativeKeyEvent.VC_F2) {
    		Utils.PAUSED = !Utils.PAUSED;
    		Utils.showInfo();
    	}
    	
    	if (Utils.PAUSED && e.getKeyCode() == NativeKeyEvent.VC_F3) {
    		try {
    			Utils.PAUSED = false;
    			Utils.MODE = "M";
				Utils.showInfo();
				Utils.startNewGame();
			} catch (AWTException | InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    	}
    	
    	if (Utils.PAUSED && e.getKeyCode() == NativeKeyEvent.VC_F4) {
    		try {
    			Utils.PAUSED = false;
    			Utils.MODE = "MF";
				Utils.showInfo();
				Utils.startNewGame();
			} catch (AWTException | InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    	}
    	
    	if (e.getKeyCode() == NativeKeyEvent.VC_F5) {
    		try {
    			GlobalScreen.unregisterNativeHook();
    			GlobalScreen.removeNativeKeyListener(this);
    			System.exit(0);
			} catch (NativeHookException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    	}
    }

	public void nativeKeyTyped(NativeKeyEvent e) {}
	public void nativeKeyPressed(NativeKeyEvent e) {}
}
