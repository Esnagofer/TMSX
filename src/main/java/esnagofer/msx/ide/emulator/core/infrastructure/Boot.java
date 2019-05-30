package esnagofer.msx.ide.emulator.core.infrastructure;

import esnagofer.msx.ide.emulator.core.application.awt.AwtEmulatorGui;

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
		AwtEmulatorGui.newInstance().debug(args[0]);
	}
		
}
