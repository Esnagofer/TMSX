/*
 * 
 */
package esnagofer.msx.ide.emulator.core.application.awt;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import esnagofer.msx.ide.emulator.core.domain.model.components.cartridgeloaders.CartridgeLoader;
import esnagofer.msx.ide.emulator.core.domain.model.components.cartridgeloaders.CartridgeLoaderRegistry;
import esnagofer.msx.ide.emulator.core.domain.model.components.cartridgeloaders.FlatMapper;
import esnagofer.msx.ide.emulator.core.domain.model.components.keyboard.Keyboard;
import esnagofer.msx.ide.emulator.core.domain.model.components.memory.RamMemory;
import esnagofer.msx.ide.emulator.core.domain.model.components.memory.RomMemory;
import esnagofer.msx.ide.emulator.core.domain.model.components.screen.Screen;
import esnagofer.msx.ide.emulator.core.domain.model.components.tms9918a.TMS9918A;
import esnagofer.msx.ide.emulator.core.domain.model.emulator.Emulator;

/**
 * The Class AwtMsxEmulatorGui.
 */
public class AwtEmulatorGui extends JFrame {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5724943280943859808L;
	
	/** The awt emulator component. */
	private AwtEmulatorComponent awtEmulatorComponent;
	
	/** The msx. */
	private Emulator msx;
	
	/** The keyboard. */
	private Keyboard keyboard;

	/** The screen. */
	private Screen screen;
	
	/**
	 * Instantiates a new msx gui.
	 */
	public AwtEmulatorGui() {
		super();
	}

	/**
	 * Inits the bios.
	 */
	private void initBios() {
		RomMemory bios = new RomMemory(0xC000, "system");
		Preferences prefs = Preferences.userRoot().node("MSXEMU");
		String romFile = prefs.get("msx_system_rom", "-");
		try {
			/* Load ROM */
			bios.load(romFile, (short)0x0000, 0x8000);
			msx.setSlot(Emulator.SLOT_0, bios);
		} catch (IOException e) {
			activateROMLoadState();
			System.out.println("Could not load system rom \"" + romFile + "\": " + e.getMessage());
		}
	}
	
	/**
	 * Disable all buttons except Load System ROM.
	 */
	private void activateROMLoadState() {
	}

	/**
	 * Enable all buttons. Use after ROM is loaded
	 */
	private void romLoaded() {
	}
	
	/**
	 * Builds the frame.
	 */
	private void initGui() {
		// Set up the main panel
		add(awtEmulatorComponent, BorderLayout.PAGE_START);
		// Button panel
		FlowLayout buttonLayout = new FlowLayout();
		JPanel buttonPanel = new JPanel(buttonLayout);
		add(buttonPanel, BorderLayout.PAGE_END);
		setTitle("TMSX - MSX Emulator");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(512,435);
		setResizable(true);
		setVisible(true);
		pack();
	}
	
	/**
	 * Load rom from this file.
	 *
	 * @param file the file
	 */
	private void loadRomFromThisFile(File file) {
		CartridgeLoader loader;
		if (file.length() <= 0x8000) {
			loader = new FlatMapper("cart1");
		} else {
		    String input = (String) JOptionPane.showInputDialog(AwtEmulatorGui.this, "Choose cartridge loader",
		        "Cartridge loader", 
		        JOptionPane.QUESTION_MESSAGE, 
		        null, 
		        CartridgeLoaderRegistry.getCartridgeLoaders(), 
		        CartridgeLoaderRegistry.getCartridgeLoaders()[0]
    		); // Initial choice
		    loader = CartridgeLoaderRegistry.getInstance(input, "cart1");
		}
		
		/* Load */
		try {
			loader.load(file.getAbsolutePath(), (int)file.length());

			/* Set slot and reset */
			msx.setSlot(Emulator.SLOT_1, loader.getSlot());
			msx.reset();

		} catch (IOException ex) {
			JOptionPane.showMessageDialog(AwtEmulatorGui.this,
				    ex.getMessage(),
				    "Error loading ROM",
				    JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Paint.
	 */
	private void paint() {
		msx.paint();
	}
	
	/**
	 * Inits the components.
	 */
	private void initComponents() {
		keyboard = AwtKeyboard.newInstance();
		awtEmulatorComponent = AwtEmulatorComponent.newInstance(new Dimension(512,384), keyboard, this::paint);
		screen = awtEmulatorComponent.screen();
		TMS9918A vdp = TMS9918A.newInstance(new RamMemory(0xFFFF, "vram"), screen);	
		msx = Emulator.builder()
			.withVdp(vdp)
			.withKeyboard(keyboard)
		.build();
	}
	
	/**
	 * Start.
	 *
	 * @param romFileName the rom file name
	 * @param startInDebugMode the start in debug mode
	 */
	private void start(String romFileName, boolean startInDebugMode) {
		File file = new File(romFileName);
		loadRomFromThisFile(file);
		msx.start(startInDebugMode);
	}
	
	/**
	 * Inits the.
	 */
	private void init() {
		initComponents();
		initGui();
	    initBios();
	}

	/**
	 * Boot.
	 *
	 * @param romFileName the rom file name
	 */
	public void boot(String romFileName) {
		init();
		start(romFileName, false);		
	}

	/**
	 * Debug.
	 *
	 * @param romFileName the rom file name
	 */
	public void debug(String romFileName) {
		init();
		start(romFileName, true);		
	}

	/**
	 * New instance.
	 *
	 * @return the msx gui
	 */
	public static AwtEmulatorGui newInstance() {
		return new AwtEmulatorGui();
	}

}
