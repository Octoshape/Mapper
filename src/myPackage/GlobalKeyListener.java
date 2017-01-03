package myPackage;

import java.awt.AWTException;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class GlobalKeyListener implements NativeKeyListener {

    public void nativeKeyReleased(NativeKeyEvent e) {
		try {
			switch (e.getKeyCode()) {
				case NativeKeyEvent.VC_F1:
					Utils.showInfo();
					break;
				case NativeKeyEvent.VC_F2:
					Utils.PAUSED = !Utils.PAUSED;
					Utils.showInfo();
					break;
				case NativeKeyEvent.VC_F3:
				case NativeKeyEvent.VC_F4:
				case NativeKeyEvent.VC_F6:
					Utils.MODE = modeForKeyCode(e.getKeyCode());
					Utils.showInfo();
					Utils.startNewGame();
					Utils.hasInitialized = false;
					Utils.PAUSED = false;
					break;
				case NativeKeyEvent.VC_F5:
					GlobalScreen.unregisterNativeHook();
					GlobalScreen.removeNativeKeyListener(this);
					System.exit(0);
				case NativeKeyEvent.VC_F7:
					for (int i = 0; i < 3; i++) {
						Utils.openChests(i);
					}
			}
		} catch (AWTException | InterruptedException | NativeHookException ex) {
			ex.printStackTrace();
		}
    }

    private String modeForKeyCode(int keyCode) {
    	switch(keyCode) {
			case NativeKeyEvent.VC_F3:
				return "M";
			case NativeKeyEvent.VC_F4:
				return "MF";
			case NativeKeyEvent.VC_F6:
				return "P";
			default:
				return "Unknown";
		}
	}

	public void nativeKeyTyped(NativeKeyEvent e) {}
	public void nativeKeyPressed(NativeKeyEvent e) {}
}
