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
				case NativeKeyEvent.VC_F7:
				case NativeKeyEvent.VC_F8:
				case NativeKeyEvent.VC_F9:
					Utils.MODE = modeForKeyCode(e.getKeyCode());
					Utils.showInfo();
					Utils.loadValueMap();
					Utils.startNewGame();
					Utils.PAUSED = false;
					break;
				case NativeKeyEvent.VC_F5:
					GlobalScreen.unregisterNativeHook();
					GlobalScreen.removeNativeKeyListener(this);
					Utils.saveValueMap();
					System.exit(0);
				case NativeKeyEvent.VC_F10:
					int amount = 2;
					while (amount-- > 0)
						Utils.ascendTroop();
					break;
				case NativeKeyEvent.VC_F11:
					for (int i = 0; i < 3; i++) {
						Utils.openChests(i);
					}
					break;
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
			case NativeKeyEvent.VC_F7:
				return "G";
			case NativeKeyEvent.VC_F8:
				return "Q";
			case NativeKeyEvent.VC_F9:
				return "T";
			default:
				return "Unknown";
		}
	}

	public void nativeKeyTyped(NativeKeyEvent e) {}
	public void nativeKeyPressed(NativeKeyEvent e) {}
}
