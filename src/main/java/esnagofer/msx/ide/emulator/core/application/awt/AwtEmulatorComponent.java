package esnagofer.msx.ide.emulator.core.application.awt;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyListener;

import javax.swing.JPanel;

import esnagofer.msx.ide.emulator.core.domain.model.hardware.keyboard.Keyboard;
import esnagofer.msx.ide.emulator.core.domain.model.hardware.screen.OnPaint;
import esnagofer.msx.ide.emulator.core.domain.model.hardware.screen.Screen;

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
	
	/** The on paint. */
	private OnPaint onPaint;

	/** The keyboard. */
	private Keyboard keyboard;
	
	/**
	 * Instantiates a new awt emulator component.
	 *
	 * @param dimension the dimension
	 * @param keyboard the keyboard
	 * @param onPaint the on paint
	 */
	private AwtEmulatorComponent(Dimension dimension, Keyboard keyboard, OnPaint onPaint) {
		super();
		awtScreen = AwtScreen.class.cast(AwtScreen.newInstance(this));
		this.screen = awtScreen;
		this.onPaint = onPaint;
		this.keyboard = keyboard;
		setFocusable(true);
		setPreferredSize(dimension);
		addKeyListener(KeyListener.class.cast(this.keyboard));
		requestFocusInWindow();
	}

	/**
	 * Paint component.
	 *
	 * @param graphics the graphics
	 */
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
		AwtScreen.class.cast(screen).setGraphics(graphics);
		onPaint.proceed();
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
	 * @param dimension the dimension
	 * @param keyboard the keyboard
	 * @param onPaint the on paint
	 * @return the awt emulator component
	 */
	public static AwtEmulatorComponent newInstance(Dimension dimension, Keyboard keyboard, OnPaint onPaint) {
		return new AwtEmulatorComponent(dimension, keyboard, onPaint);
	}

}
