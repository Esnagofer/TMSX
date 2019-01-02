package tmsx.application.emulator;

import tmsx.application.emulator.awt.AwtMsxEmulatorGui;

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
		AwtMsxEmulatorGui.newInstance().start();
	}
		
}
