package emu.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import emu.MSX;
import emu.cartridgeloaders.BlockMapper;
import emu.cartridgeloaders.CartridgeLoader;
import emu.cartridgeloaders.CartridgeLoaderRegistry;
import emu.cartridgeloaders.Konami5Mapper;
import emu.memory.EmptySlot;
import emu.memory.RAMSlot;
import emu.memory.ROMSlot;

public class TMSX extends JFrame {

	private MSX msx;
	private JButton cartridgeButton;
	private JButton sysRomButton;
	private JButton pauseButton;
	private JButton resetButton;
	private JButton breakButton;
	
	public static void main(String[] args) {

		/* Initialize MSX instance */
		MSX msx = new MSX();
		msx.initHardware();
		
		/* Build frame */
		TMSX frame = new TMSX(msx);
		frame.setTitle("TMSX - MSX Emulator");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(512,435);
		frame.setResizable(false);
		frame.pack();
	    SwingUtilities.invokeLater(new Runnable() {
	        public void run() {
	          frame.setVisible(true);
	        }
	      });
		
	    /* Load ROM */
	    Preferences prefs = Preferences.userRoot().node("MSXEMU");
	    String romFile = prefs.get("msx_system_rom", "-");
	    boolean romLoadOK = false;
	    
	    ROMSlot bios = new ROMSlot(0xC000, "system");
		try {
			bios.load(romFile, (short)0x0000, 0x8000);
			msx.getSlots()[0] = bios;
			romLoadOK = true;
		} catch (IOException e) {
			System.out.println("Could not load system rom \"" + romFile + "\": " + e.getMessage());
		}

		/* We fill slot 1 and 2 with empty ROM */
		msx.getSlots()[1] = new EmptySlot("cart1 (empty)");
		msx.getSlots()[2] = new EmptySlot("cart2 (empty)");

		/* Slot 3 is RAM */
		msx.getSlots()[3] = new RAMSlot("ram");

		/* If ROM was not loaded, activate ROM load state */
		if (!romLoadOK) {
			frame.activateROMLoadState();
		}

		/* Start MSX */
		msx.startMSX();

	}
		
	/**
	 * Disable all buttons except Load System ROM
	 */
	public void activateROMLoadState() {
		cartridgeButton.setEnabled(false);
		sysRomButton.setEnabled(true);
		pauseButton.setEnabled(false);
		resetButton.setEnabled(false);
		breakButton.setEnabled(false);
	}

	/**
	 * Enable all buttons. Use after ROM is loaded
	 */
	public void romLoaded() {
		cartridgeButton.setEnabled(true);
		sysRomButton.setEnabled(true);
		pauseButton.setEnabled(true);
		resetButton.setEnabled(true);
		breakButton.setEnabled(true);
	}
	
	public TMSX(MSX msx) {
		this.msx = msx;
		buildFrame();
	}
	
	private void buildFrame() {
		
		// Set up the main panel
		JPanel msxPanel = new JPanel() {
		    @Override
		    protected void paintComponent(Graphics g) {
		        super.paintComponent(g);
		        msx.getVDP().paint(g);
		    }
		};
		msxPanel.setFocusable(true);
		msxPanel.setPreferredSize(new Dimension(512,384));
		add(msxPanel, BorderLayout.PAGE_START);
		msx.setScreenPanel(msxPanel);
		msxPanel.addKeyListener(msx.getKeyBoard());
		
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
				msx.debugMode = true;
			}
		});

		// Stop button listener (as mouse listener, so we can distinguish pressed/released events)
		breakButton.addMouseListener(new MouseAdapter() {
	        @Override
	        public void mousePressed(MouseEvent e) {
	            msx.getKeyBoard().stopKeyPressed();
	        }
	        @Override
	        public void mouseReleased(MouseEvent e) {
	            msx.getKeyBoard().stopKeyDepressed();
	        }
		});	
		
		// Slot load listeners
		cartridgeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				/* Select file */
				JFileChooser fc = new JFileChooser();
				int returnVal = fc.showOpenDialog(TMSX.this);
		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile();

		            /* Select loader */
		            String input = (String) JOptionPane.showInputDialog(null, "Choose cartridge loader",
		                "Cartridge loader", JOptionPane.QUESTION_MESSAGE, null, CartridgeLoaderRegistry.getCartridgeLoaders(), CartridgeLoaderRegistry.getCartridgeLoaders()[0]); // Initial choice
		            CartridgeLoader loader = CartridgeLoaderRegistry.getInstance(input, "cart1");
		    		try {
		    			loader.load(file.getAbsolutePath(), (int)file.length());
		    		} catch (IOException ex) {
		    			ex.printStackTrace();
		    		}
		    		
		    		/* Set slot and reset */
		    		msx.setSlot(1, loader.getSlot());
					msx.reset();
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
				int returnVal = fc.showOpenDialog(TMSX.this);
		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile();
		            ROMSlot bios = new ROMSlot(0xC000, "system");
					boolean romLoadOK = false;
				    try {
				    	bios.load(file.getAbsolutePath(), (short)0x0000, 0x8000);
					    msx.getSlots()[0] = bios;
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
		
		msxPanel.requestFocusInWindow();
	}

}
