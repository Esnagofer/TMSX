package emu;

import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import emu.memory.AbstractSlot;
import emu.memory.EmptySlot;
import emu.memory.RAMSlot;
import emu.memory.ROMSlot;

public class MSX { // implements IBaseDevice {

	private HashSet<Short> breakpoints = new HashSet<Short>();
	
	public static final int INTERRUPT_RATE = 50;
	public static final double SPEED = 3.5;
	public static final int INITIAL_DELAY = 20;
	
	private Z802 cpu;
	private TMS9918A vdp;
	private AbstractSlot primarySlot;
	private AbstractSlot[] slots;
	private KeyboardDecoder keyboard;

	public boolean debugMode = false, debugEnabled = true;
	
	public boolean running = true;
	
	public static int vSyncInterval = 70000, interruptRate = 50;
	
	/* PPI A register (primary slot select) */
	private byte ppiA_SlotSelect = 0;

	/* PPI C register (keyboard and cassette control) */
	private byte ppiC_Keyboard;
	
	private JPanel screenPanel;
		
	public void initHardware() {
		debug("Init memory");
		initMemory();
		debug("Init CPU");
		initCPU();
		debug("Init keyboard");
		initKeyboard();
		debug("Init PPI");
		initPPI();
		debug("Init VDP");
		initVDP();
	}
	
	public final int getSlotForPage(int page) {
		switch (page) {
		case 0:	return ppiA_SlotSelect & 0x03;
		case 1:	return (ppiA_SlotSelect & 0x0C) >> 2;
		case 2:	return (ppiA_SlotSelect & 0x30) >> 4;
		case 3:	return (ppiA_SlotSelect & 0xC0) >> 6;
		default: throw new IllegalArgumentException("Illegal page number " + page);
		}
	}	

	public final void setSlotForPage(int page, int slot) {
		switch (page) {
		case 0:	ppiA_SlotSelect = (byte)((ppiA_SlotSelect & ~0x03) | slot); break;
		case 1:	ppiA_SlotSelect = (byte)((ppiA_SlotSelect & ~0x0C) | slot << 2); break;
		case 2:	ppiA_SlotSelect = (byte)((ppiA_SlotSelect & ~0x30) | slot << 4); break;
		case 3:	ppiA_SlotSelect = (byte)((ppiA_SlotSelect & ~0xC0) | slot << 6); break;
		}
	}	
	public void initMemory() {
		
		slots = new AbstractSlot[4];
		
		/* Create primary slot object */
		primarySlot = new AbstractSlot() {

			@Override
			public byte rdByte(short addr) {
				byte val = 0;
				if ((addr & 0xffff) <= 0x3fff) val = slots[getSlotForPage(0)].rdByte(addr);
				else if ((addr & 0xffff) <= 0x7fff) val = slots[getSlotForPage(1)].rdByte(addr);
				else if ((addr & 0xffff) <= 0xb3ff) val = slots[getSlotForPage(2)].rdByte(addr);
				else val = slots[getSlotForPage(3)].rdByte(addr);
				return val;
			}

			@Override
			public void wrtByte(short addr, byte value) {
				if (isWritable(addr)) {
					if ((addr & 0xffff) <= 0x3fff) slots[getSlotForPage(0)].wrtByte(addr, value);
					else if ((addr & 0xffff) <= 0x7fff) slots[getSlotForPage(1)].wrtByte(addr, value);
					else if ((addr & 0xffff) <= 0xb3ff) slots[getSlotForPage(2)].wrtByte(addr, value);
					else slots[getSlotForPage(3)].wrtByte(addr, value);
				}
			}

			@Override
			public boolean isWritable(short addr) {
				if ((addr & 0xffff) <= 0x3fff) return slots[getSlotForPage(0)].isWritable(addr);
				else if ((addr & 0xffff) <= 0x7fff) return slots[getSlotForPage(1)].isWritable(addr);
				else if ((addr & 0xffff) <= 0xb3ff) return slots[getSlotForPage(2)].isWritable(addr);
				else return slots[getSlotForPage(3)].isWritable(addr);
			}
			
		};
	}

	/**
	 * Initialize CPU
	 */
	public void initCPU() {
		cpu = new Z802(primarySlot);
	}
	
	/**
	 * Initialize VDP (including I/O channels)
	 */
	public void initVDP() {
		vdp = new TMS9918A();
		
		// 98 = VRAM data read/write port
		// 99 = Status register read port (read only)

		// VRAM data read/write port
		cpu.registerInDevice(new Z80InDevice() {
			public byte in(byte port) {
				return vdp.readVRAMData();
			}
		}, 0x98);
		cpu.registerOutDevice(new Z80OutDevice() {
			public void out(byte port, byte value) {
				vdp.writeVRAMData(value);
			}
		}, 0x98);
		
		// VDP register write port
		cpu.registerOutDevice(new Z80OutDevice() {
			public void out(byte port, byte value) {
				vdp.writeRegister(value);
			}
		}, 0x99);
		
		// VDP status register read port
		cpu.registerInDevice(new Z80InDevice() {
			public byte in(byte port) {
				return vdp.readStatus();
			}
		}, 0x99);
		
	}

	/**
	 * Initialize keyboard
	 */
	public void initKeyboard() {
		keyboard = new KeyboardDecoder();
	}
	
	/**
	 * Initialize PPI (keyboard and slot select I/O)
	 */
	public void initPPI() {

		/* PPI register A (slot select) (port A8) */
		cpu.registerInDevice(new Z80InDevice() {
			public byte in(byte port) {
				return ppiA_SlotSelect;
			}
		}, 0xA8);
		cpu.registerOutDevice(new Z80OutDevice() {
			public void out(byte port, byte value) {
				ppiA_SlotSelect = value;
			}
		}, 0xA8);

		/* PPI register B (keyboard matrix row input register) (port A9) */
		cpu.registerInDevice(new Z80InDevice() {
			public byte in(byte port) {
				byte value = keyboard.getRowValue(ppiC_Keyboard & 0x0F);
				value = Tools.invert(value);
				return value;
			}
		}, 0xA9);

		/* PPI register C (keyboard and cassette interface) (port AA) */
		cpu.registerInDevice(new Z80InDevice() {
			public byte in(byte port) {
				return ppiC_Keyboard;
			}
		}, 0xAA);
		cpu.registerOutDevice(new Z80OutDevice() {
			public void out(byte port, byte value) {
				ppiC_Keyboard = value;
				//keyboard.setCapslock((ppiC_Keyboard & 0x40) != 0);
			}
		}, 0xAA);

		/* PPI command register (used for setting bit 4-7 of ppi_C) (port AB) */
		cpu.registerOutDevice(new Z80OutDevice() {
			public void out(byte port, byte value) {
				if ((value & 0xff) >> 7 == 0) {
					int bit_no = (value & 0x0E) >> 1;
					if (value % 2 == 0) {
						ppiC_Keyboard = (byte)(ppiC_Keyboard & ~(1 << bit_no));
					} else {
						ppiC_Keyboard = (byte)(ppiC_Keyboard | (1 << bit_no));
					}
				}
				//keyboard.setCapslock((ppiC_Keyboard & 0x40) != 0);
			}
			
		}, 0xAB);

	}
	
	/**
	 * Start MSX. Basically a while look that executes CPU instructions,
	 * triggers VSync interrupts, and stops when halted. 
	 */
	public void startMSX() {

		/* Values used for delay and automatic correction of delay */
		long previousInterruptCycle = System.currentTimeMillis();
		int intCount = 0, delay = INITIAL_DELAY;
		
		/* Main loop */
		while (true) {

			/* Not running? Sleep and continue */
			if (!running) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
			}
			
			/* Check whether we should go into debug mode */
			if (debugMode) debugMode();
			if (breakpoints.contains((short)cpu.getPC())) { debug("Breakpoint found"); debugMode(); }
			
			/* Trigger interrupt if GINT and INT are both set */
			if (vdp.getGINT() && vdp.getStatusINT()) {
				cpu.interrupt();
			}
			
			/* Execute one instruction */
			cpu.execute();
			
			/* Trigger interrupt if 1/INTERRUPT_RATE seconds has passed according to CPU cycle count */
			if (cpu.s >= ((SPEED * 1000000.0)/INTERRUPT_RATE)) {
				cpu.s = (cpu.s - vSyncInterval);
				long now = System.currentTimeMillis();
								
				/* Execute delay */
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				/* Trigger interrupt */
				vdp.setStatusINT(true);

				updateScreen();

				/* Keep track of interrupt rate and correct delay if necessary */
				int checkInterval = 1;
				intCount++;
				if (now - previousInterruptCycle >= (1000 / checkInterval)) {
					if (Math.abs(intCount - (interruptRate / checkInterval)) > 1) {
						if (intCount < INTERRUPT_RATE) {
							if (delay > 0) { 
								delay -= 1;
								//System.out.println("Running too slow: " + (intCount * checkInterval) + " interrupts/sec" + ". Decreasing delay ("+delay+" ms).");
							}
						}
						if (intCount > INTERRUPT_RATE) {
							delay += 1;
							//System.out.println("Running too slow: " + (intCount * checkInterval) + " interrupts/sec" + ". Increasing delay ("+delay+" ms).");
						}
					}
					previousInterruptCycle = System.currentTimeMillis();
					intCount = 0;
				}
			}
		}
	}
	
	/**
	 * A very simple debug interface
	 */
	public void debugMode() {
		
		debugMode = true;
		short preSP = 0;
		
		try {
			preSP = cpu.getSP();
			debug(cpu.getLastMsg());
			cpu.printState();
		
			BufferedReader bi = new BufferedReader(new InputStreamReader(System.in));
			boolean preStep = false;
			String input = "";
			while (true) {
				if (preStep) {
					preStep = false;
					debug("Step ?");
					input = bi.readLine();
					if (input.equals("")) input = "step";
				} else {
					debug("? ");
					input = bi.readLine();
				}
				if (input.equals("step")) {
					preStep = true;
					debug("ADDR = " + Tools.toHexString4(cpu.PC) + ", value = " + Tools.toHexString(primarySlot.rdByte((short)cpu.PC)) + ", T = " + cpu.s);
					cpu.execute();
					debug(cpu.getLastMsg());
					cpu.printState();
					continue;
				}
				if (input.equals("stepout")) {
					preStep = true;
					while (preSP != cpu.getSP()) {
						cpu.execute();
						updateScreen();
					}
					cpu.printState();
					continue;
				}
				if (input.startsWith("peek")) {
					preStep = false;
					String addrString = input.substring(5);
					int addrInt = Integer.decode(addrString);
					debug("Value at addr " + Tools.toHexString((short)addrInt) + " = " + Tools.toHexString(primarySlot.rdByte((short)addrInt)));
					continue;
				}
				if (input.startsWith("brpt")) {
					preStep = false;
					String addrString = input.substring(5);
					int addrInt = Integer.decode(addrString);
					debug("Added break point at " + Tools.toHexString((short)addrInt));
					breakpoints.add((short)addrInt);
					continue;
				}
				if (input.startsWith("rbrp")) {
					preStep = false;
					String addrString = input.substring(5);
					int addrInt = Integer.decode(addrString);
					debug("Removed break point " + Tools.toHexString((short)addrInt));
					breakpoints.remove((short)addrInt);
					continue;
				}
				if (input.startsWith("set page")) {
					preStep = false;
					String pageString = input.substring(9, 10);
					int page = Integer.decode(pageString);
					String slotString = input.substring(11,12);
					int slot = Integer.decode(slotString);
					setSlotForPage(page, slot);
					debug("Slot select: (" + getSlotForPage(0) + "/" + getSlotForPage(1) + "/" + getSlotForPage(2) + "/" +getSlotForPage(3) + ")");
					continue;
				}
				if (input.equals("slot info")) {
					preStep = false;
					debug("Slot select: (" + getSlotForPage(0) + "/" + getSlotForPage(1) + "/" + getSlotForPage(2) + "/" +getSlotForPage(3) + ")");
					continue;
				}
				if (input.equals("interrupt")) {
					this.triggerVSyncInterrupt();
					cpu.execute();
					cpu.printState();
					continue;
				}
				if (input.equals("vramdump")) {
					String s = "";
					for (int i = 0; i < 0x3fff; i++) {
						byte b = vdp.mem.rdByte((short)(i&0xffff));
						char c = 0;
						if ((b & 0xff) <= 126 && (b & 0xff) >= 32) {
							c = (char)b;
						} else {
							c = '.';
						}
						s += Tools.toHexString(b) + " ";
						if (i % 4 == 0) s += " ";
						if (i % 16 == 0) debug(s + " <- " + Tools.toHexString4(i-16));
					}
					continue;
				}

				if (input.equals("continue")) return;
				debug("Unknown command");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			debugMode = false;
		}
	}
	
	/**
	 * Register a screen panel, which will be repainted every time
	 * a VSync interrupt is triggered.
	 * 
	 * @param screenPanel The panel to repaint upon VSync interrupt
	 */
	public void setScreenPanel(JPanel screenPanel) {
		this.screenPanel = screenPanel;
	}
	
	/**
	 * Repaint the screen panel.
	 */
	private void updateScreen() {
		if (screenPanel != null) screenPanel.repaint();
	}

	/**
	 * Trigger a VSync interrupt. Will set the relevant status bits in the VDP
	 * and calls cpu.interrupt() if interrupts are enabled.
	 */
	private void triggerVSyncInterrupt() {
		vdp.setStatusINT(true);
		if (vdp.getStatusINT() && vdp.getGINT()) {
			cpu.interrupt();
		}
	}
	
	/**
	 * Print a debug message (only if debug is enabled or if in debug mode)
	 * 
	 * @param msg Message to print on console.
	 */
	private void debug(String msg) {
		if (debugEnabled || debugMode) {
			System.out.println(msg);
		}
	}

	/**
	 * Reset the MSX (simply resets PC to 0x0000)
	 */
	public void reset() {
		cpu.PC = 0;
	}

	/**
	 * @return The VDP instance of this MSX.
	 */
	public TMS9918A getVDP() {
		return vdp;
	}
	
	/**
	 * @return The keyboard decoder instance of this MSX.
	 */
	public KeyboardDecoder getKeyBoard() {
		return keyboard;
	}
	
	/**
	 * Change content of a slot.
	 * 
	 * @param slot
	 * @param s
	 */
	public void setSlot(int slot, AbstractSlot s) {
		slots[slot] = s;
	}

	public boolean isPaused() {
		return !running;
	}
	
	public void pause(boolean f) {
		running = !f;
	}

	public AbstractSlot[] getSlots() {
		return slots;
	}
	
}
