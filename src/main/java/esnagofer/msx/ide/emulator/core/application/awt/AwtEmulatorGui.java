/*
 * 
 */
package esnagofer.msx.ide.emulator.core.application.awt;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import esnagofer.msx.ide.emulator.core.domain.model.cartridgeloaders.CartridgeLoader;
import esnagofer.msx.ide.emulator.core.domain.model.cartridgeloaders.CartridgeLoaderRegistry;
import esnagofer.msx.ide.emulator.core.domain.model.cartridgeloaders.FlatMapper;
import esnagofer.msx.ide.emulator.core.domain.model.emulator.Emulator;
import esnagofer.msx.ide.emulator.core.domain.model.hardware.keyboard.Keyboard;
import esnagofer.msx.ide.emulator.core.domain.model.hardware.memory.RomMemory;
import esnagofer.msx.ide.emulator.core.domain.model.hardware.screen.Screen;

/**
 * The Class AwtMsxEmulatorGui.
 */
public class AwtEmulatorGui extends JFrame {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5724943280943859808L;
	
	/** The cartridge button. */
	private JButton cartridgeButton;
	
	/** The sys rom button. */
	private JButton sysRomButton;
	
	/** The pause button. */
	private JButton pauseButton;
	
	/** The reset button. */
	private JButton resetButton;
	
	/** The break button. */
	private JButton breakButton;
	
	/** The awt emulator component. */
	private AwtEmulatorComponent awtEmulatorComponent;
	
	/** The msx. */
	private Emulator msx;
	
	/** The keyboard. */
	private Keyboard keyboard;
	
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
		cartridgeButton.setEnabled(false);
		sysRomButton.setEnabled(true);
		pauseButton.setEnabled(false);
		resetButton.setEnabled(false);
		breakButton.setEnabled(false);
	}

	/**
	 * Enable all buttons. Use after ROM is loaded
	 */
	private void romLoaded() {
		cartridgeButton.setEnabled(true);
		sysRomButton.setEnabled(true);
		pauseButton.setEnabled(true);
		resetButton.setEnabled(true);
		breakButton.setEnabled(true);
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
		cartridgeButton = new JButton("Set Cartridge");
		sysRomButton = new JButton("Set System ROM");
		resetButton = new JButton("Reset");
		pauseButton = new JButton("Debug");
		breakButton = new JButton("Stop");
		resetButton.setPreferredSize(new Dimension(70, 25));
		pauseButton.setPreferredSize(new Dimension(70, 25));
		breakButton.setPreferredSize(new Dimension(70, 25));
		buttonPanel.add(cartridgeButton);
		buttonPanel.add(sysRomButton);
		buttonPanel.add(pauseButton);
		buttonPanel.add(resetButton);
		buttonPanel.add(breakButton);
		cartridgeButton.setFocusable(false);
		sysRomButton.setFocusable(false);
		pauseButton.setFocusable(false);
		resetButton.setFocusable(false);
		breakButton.setFocusable(false);

		// Reset button listener
		resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				msx.reset();
			}
		});
		
		// Pause button listener
		pauseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				msx.debugger().start();
			}
		});

		// Stop button listener (as mouse listener, so we can distinguish pressed/released events)
		breakButton.addMouseListener(new MouseAdapter() {
	        @Override
	        public void mousePressed(MouseEvent e) {
	            keyboard.stopKeyPressed();
	        }
	        @Override
	        public void mouseReleased(MouseEvent e) {
	            keyboard.stopKeyDepressed();
	        }
		});	
		
		// Slot load listeners
		cartridgeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				/* Select file */
				JFileChooser fc = new JFileChooser();
				int returnVal = fc.showOpenDialog(AwtEmulatorGui.this);
		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            loadRomFromThisFile(fc.getSelectedFile());
		        }
			}			
		});
		
		// System rom button listener
		sysRomButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
			    Preferences prefs = Preferences.userRoot().node("MSXEMU");
				JFileChooser fc = new JFileChooser();
				fc.setSelectedFile(new File(prefs.get("msx_system_rom", "~/")));
				int returnVal = fc.showOpenDialog(AwtEmulatorGui.this);
		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile();
		            RomMemory bios = new RomMemory(0xC000, "system");
					boolean romLoadOK = false;
				    try {
				    	bios.load(file.getAbsolutePath(), (short)0x0000, 0x8000);
					    msx.setSlot(Emulator.SLOT_0, bios);
						romLoadOK = true;
					} catch (IOException ex) {
						System.out.println("Could not load system rom \"" + file.getAbsolutePath() + "\": " + ex.getMessage());
					}
				    if (romLoadOK) {
					    prefs.put("msx_system_rom", file.getAbsolutePath());
					    romLoaded();
						msx.reset();
				    }
		        }
			}
			
		});
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
		/* Select cartridge loader */
		CartridgeLoader loader;
		if (file.length() <= 0x8000) {
			/* We default to flat mapper for roms < 32K */
			loader = new FlatMapper("cart1");
		} else {
		    /* Manually select loader */
		    String input = (String) JOptionPane.showInputDialog(AwtEmulatorGui.this, "Choose cartridge loader",
		        "Cartridge loader", JOptionPane.QUESTION_MESSAGE, null, CartridgeLoaderRegistry.getCartridgeLoaders(), CartridgeLoaderRegistry.getCartridgeLoaders()[0]); // Initial choice
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
		Screen screen = awtEmulatorComponent.screen();
		msx = Emulator.builder()
			.withScreen(screen)
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
	 */
	public void boot(String romFileName) {
		init();
		start(romFileName, false);		
	}

	/**
	 * Debug.
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
