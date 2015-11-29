package emu.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
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

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import emu.MSX;
import emu.memory.ROMSlot;

public class EmuFrame extends JFrame {

	private MSX msx;
	
	public static void main(String[] args) {
		MSX msx = new MSX();
		msx.initHardware();
		
		EmuFrame frame = new EmuFrame(msx);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(512,435);
		frame.setResizable(false);
		frame.pack();
	    SwingUtilities.invokeLater(new Runnable() {
	        public void run() {
	          frame.setVisible(true);
	        }
	      });
		
	    msx.startMSX();
	}
	
	public EmuFrame(MSX msx) {
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
		GridLayout buttonLayout = new GridLayout(1,3);
		JPanel buttonPanel = new JPanel(buttonLayout);
		add(buttonPanel, BorderLayout.PAGE_END);
		JButton slot1 = new JButton("Slot 1");
		JButton slot2 = new JButton("Slot 2");
		JButton reset = new JButton("Reset");
		JButton bbreak = new JButton("Stop");
		buttonPanel.add(slot1);
		buttonPanel.add(slot2);
		buttonPanel.add(reset);
		buttonPanel.add(bbreak);
		slot1.setFocusable(false);
		slot2.setFocusable(false);
		reset.setFocusable(false);
		bbreak.setFocusable(false);
		
		// Reset button listener
		reset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				msx.reset();
			}
		});
		
		// Stop button listener (as mouse listener, so we can distinguish pressed/released events)
		bbreak.addMouseListener(new MouseAdapter() {
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
		slot1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				int returnVal = fc.showOpenDialog(EmuFrame.this);
		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile();
		            ROMSlot s = new ROMSlot(0xffff);
		    		try {
		    			s.load(file.getAbsolutePath(), (short)0x4000);
		    		} catch (IOException ex) {
		    			ex.printStackTrace();
		    			System.exit(0);
		    		}
		    		msx.setSlot(1, s);
		        }
			}
			
		});
		
		msxPanel.requestFocusInWindow();
	}

}
