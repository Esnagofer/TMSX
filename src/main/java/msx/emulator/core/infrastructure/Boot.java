package msx.emulator.core.infrastructure;

import msx.emulator.core.application.awt.AwtEmulatorGui;

/**
 * The Class Boot.
 */
public class Boot {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		AwtEmulatorGui.newInstance().boot();
	}
		
}
