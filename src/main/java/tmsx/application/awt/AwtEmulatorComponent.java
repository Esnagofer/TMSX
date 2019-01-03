package tmsx.application.awt;

import java.awt.Graphics;

import javax.swing.JPanel;

import tmsx.domain.model.emulator.MsxEmulator;
import tmsx.domain.model.hardware.screen.Screen;
import tmsx.infrastructure.awt.domain.model.hardware.screen.AwtScreen;

/**
 * The Class AwtEmulatorComponent.
 */
public class AwtEmulatorComponent extends JPanel {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -294786008151227240L;

	/** The awt screen. */
	private AwtScreen awtScreen;
	
	/** The screen. */
	private Screen screen;
	
	/** The msx. */
	private MsxEmulator msx;
	
	/**
	 * Instantiates a new awt emulator component.
	 */
	private AwtEmulatorComponent() {
		super();
		awtScreen = AwtScreen.class.cast(AwtScreen.newInstance(this));
		this.screen = awtScreen;
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
		AwtScreen.class.cast(screen).setGraphics(graphics);
		msx.paint();		        	
    }

	/**
	 * Sets the msx emulator.
	 *
	 * @param msx the new msx emulator
	 */
	public void setMsxEmulator(MsxEmulator msx) {
		this.msx = msx;
	}
	
	/**
	 * Screen.
	 *
	 * @return the screen
	 */
	public Screen screen() {
		return screen;
	}

	/**
	 * New instance.
	 *
	 * @return the awt emulator component
	 */
	public static AwtEmulatorComponent newInstance() {
		return new AwtEmulatorComponent();
	}

}
