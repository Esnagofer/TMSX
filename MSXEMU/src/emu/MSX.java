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

import emu.memory.AbstractSlot;
import emu.memory.EmptySlot;
import emu.memory.RAMSlot;
import emu.memory.ROMSlot;

public class MSX { // implements IBaseDevice {

	private HashSet<Short> breakpoints = new HashSet<Short>();
	
	//private Z80Core core;
	private Z802 cpu;
	private TMS9918A vdp;
	private AbstractSlot primarySlot;
	private AbstractSlot[] slots;
	private KeyboardDecoder keyboard;

	private boolean debugMode = false;
	private boolean startDebug = false;
	
	public boolean running = true;
	
	public static int vSyncInterval = 3200000;
	
	/* PPI A register (primary slot select) */
	private byte ppiA_SlotSelect;

	/* PPI C register (keyboard and cassette control) */
	private byte ppiC_Keyboard;
	private JFrame frame;
	
	private final Z80OutDevice[] out = new Z80OutDevice[256];
	private final Z80InDevice[] in = new Z80InDevice[256];

	private int intCount = 0;

	public static void main(String[] args) {
		MSX m = new MSX();
		m.initHardware();
		m.startMSX();
	}
	
	public void initHardware() {
		debug("Init memory");
		initMemory();
		debug("Init CPU");
		initCPU();
		debug("Init Frame");
		initFrame();
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
				if ((addr & 0xffff) <= 0x3fff) return slots[getSlotForPage(0)].rdByte(addr);
				else if ((addr & 0xffff) <= 0x7fff) return slots[getSlotForPage(1)].rdByte(addr);
				else if ((addr & 0xffff) <= 0xb3ff) return slots[getSlotForPage(2)].rdByte(addr);
				else return slots[getSlotForPage(3)].rdByte(addr);
			}

			@Override
			public void wrtByte(short addr, byte value) {
				if ((addr & 0xffff) == 0xfc4a) {
					debug("Writing himem (fc4a));" + Tools.toHexString(value));
					debug("Slots = (" + getSlotForPage(0) + "/" + getSlotForPage(1) + "/" + getSlotForPage(2) + "/" +getSlotForPage(3) + ")");
				}
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
		
		/* Slot 0 is BASIC ROM (at page 0/1) */
		slots[0] = new ROMSlot(0x8000);
		try {
			slots[0].load("/MSX.rom", (short)0x0000);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}

		/* We fill slot 1 and 3 with empty ROM */
		slots[1] = new EmptySlot();
		slots[3] = new EmptySlot();

		/* Slot 2 is RAM */
		slots[2] = new RAMSlot();
		
	}

	public void initCPU() {
		//core = new Z80Core(primarySlot, this);
		cpu = new Z802(primarySlot);
	}
	
	public void initVDP() {
		vdp = new TMS9918A();
		
		// 98 = VRAM data read/write port
		// 99 = Status register read port (read only)

		// VRAM data read/write port
		this.registerInDevice(new Z80InDevice() {
			public byte in(byte port) {
				debug("Read from VRAM");
				return vdp.readVRAMData();
			}
		}, 0x98);
		this.registerOutDevice(new Z80OutDevice() {
			public void out(byte port, byte value) {
				debug("Write to VRAM");
				vdp.writeVRAMData(value);
			}
		}, 0x98);
		
		// VDP register write port
		this.registerOutDevice(new Z80OutDevice() {
			public void out(byte port, byte value) {
				debug("VDP register write");
				vdp.writeRegister(value);
			}
		}, 0x99);
		
		// VDP status register read port
		this.registerInDevice(new Z80InDevice() {
			public byte in(byte port) {
				debug("VDP register read");
				return vdp.readStatus();
			}
		}, 0x99);
		
		vdp.initBuffer();
		
	}

	public void initKeyboard() {
		
		keyboard = new KeyboardDecoder();
		frame.addKeyListener(keyboard);
	}
	
	public void initPPI() {

		/* PPI register A (slot select) (port A8) */
		this.registerInDevice(new Z80InDevice() {
			public byte in(byte port) {
				debug("Reading slot select");
				return ppiA_SlotSelect;
			}
		}, 0xA8);
		this.registerOutDevice(new Z80OutDevice() {
			public void out(byte port, byte value) {
				ppiA_SlotSelect = value;
				debug("Writing slot select. Value = " + Tools.toHexString(value) + " (" + getSlotForPage(0) + "/" + getSlotForPage(1) + "/" + getSlotForPage(2) + "/" +getSlotForPage(3) + ")");
			}
		}, 0xA8);

		/* PPI register B (keyboard matrix row input register) (port A9) */
		this.registerInDevice(new Z80InDevice() {
			public byte in(byte port) {
				byte value = keyboard.getRowValue(ppiC_Keyboard & 0x0F);
				if (value != 0) System.out.println("Reading keyboard matrix " + value);
				return value;
			}
		}, 0xA9);

		/* PPI register C (keyboard and cassette interface) (port AA) */
		this.registerInDevice(new Z80InDevice() {
			public byte in(byte port) {
				//if (ppiC_Keyboard != 0) System.out.println("Reading keyboard matrix row register" + ppiC_Keyboard);
				return ppiC_Keyboard;
			}
		}, 0xAA);
		this.registerOutDevice(new Z80OutDevice() {
			public void out(byte port, byte value) {
				debug("Writing keyboard matrix row register");
				ppiC_Keyboard = value;
				//keyboard.setCapslock((ppiC_Keyboard & 0x40) != 0);
			}
		}, 0xAA);

		/* PPI command register (used for setting bit 4-7 of ppi_C) (port AB) */
		this.registerOutDevice(new Z80OutDevice() {
			public void out(byte port, byte value) {
				int bit_no = (value & (0x0E >> 1)) + 4;
				if (value % 2 != 0) {
					ppiC_Keyboard = (byte)(ppiC_Keyboard | (1 << bit_no));
				} else {
					ppiC_Keyboard = (byte)(ppiC_Keyboard & ~(1 << bit_no));
				}
				//keyboard.setCapslock((ppiC_Keyboard & 0x40) != 0);
			}
			
		}, 0xAB);

	}
	
	private void registerInDevice(Z80InDevice inDevice, int port) {
		cpu.registerInDevice(inDevice, port);
		//in[port] = inDevice;
	}

	private void registerOutDevice(Z80OutDevice outDevice, int port) {
		cpu.registerOutDevice(outDevice, port);
		//out[port] = outDevice;
	}

	private void initFrame() {
		frame = new JFrame() {
			public void paint(Graphics g) {
				if (vdp != null) vdp.paint(g);
			}
		};
		frame.setSize(400, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.repaint();
		frame.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == '*') {
					startDebug = true;
				}
				
			}

			@Override
			public void keyPressed(KeyEvent e) {}

			@Override
			public void keyReleased(KeyEvent e) {}
			
		});
	}
	
	public void startMSX() {

		int cnt = 0;
		short minAddr = (short)0xffff, maxAddr = (short)0x0000;
		
		while (running) {

			if (startDebug) {
				startDebug = false;
				debugMode();
			}
			
			String desc = BIOSDebug.getDesc((short)cpu.getPC());
			
			//if (cpu.getPC() == 0) {
			//	debugMode();
			//}
			if(breakpoints.contains((short)cpu.getPC())) {
				System.out.println("Breakpoint found");
				debugMode();
			}
			
			if (desc.equals("INIT")) {
				System.out.println("Init called");
				System.out.println("Slot config: (" + getSlotForPage(0) + "/" + getSlotForPage(1) + "/" + getSlotForPage(2) + "/" +getSlotForPage(3) + ")");
				debugMode();
			}

			if (cpu.getPC() == 0) {
				System.out.println("Executing 0x0000");
			}

			if (desc.equals("MAINLOOP")) {
				System.out.println("Main Loop Started");
			}

			if (desc.equals("powerup")) {
				System.out.println("Powerup Started");
			}
			
			cpu.execute();
			System.out.println("pc" + cpu.getPC());
			//if ((cpu.getPC() & 0xffff) < (minAddr & 0xffff)) minAddr = cpu.getPC();
			//if ((cpu.getPC() & 0xffff) > (maxAddr & 0xffff)) maxAddr = cpu.getPC();
			//cnt++;
			//if (cnt > 100000) {
			//	System.out.println("Executed " + cnt + " steps between " + Tools.toHexString(minAddr) + " and " + Tools.toHexString(maxAddr));
			//	minAddr = (short)0xffff; maxAddr = (short)0x0000;
			//	cnt = 1;
			//}
				
			if (cpu.s >= vSyncInterval) {
				frame.repaint();
				doVSyncInterrupt();
				cpu.s = (cpu.s - vSyncInterval);
			}
		}
		
	}
	
	public void debugMode() {
		
		debugMode = true;
		
		try {
			System.out.println(cpu.getLastMsg());
			cpu.printState();
		
			BufferedReader bi = new BufferedReader(new InputStreamReader(System.in));
			boolean preStep = false;
			String input = "";
			while (true) {
				if (preStep) {
					preStep = false;
					System.out.println("Step ?");
					input = bi.readLine();
					if (input.equals("")) input = "step";
				} else {
					System.out.println("? ");
					input = bi.readLine();
				}
				if (input.equals("step")) {
					preStep = true;
					System.out.println("ADDR = " + Tools.toHexString4(cpu.PC) + ", value = " + Tools.toHexString(primarySlot.rdByte((short)cpu.PC)) + ", T = " + cpu.s);
					cpu.execute();
					System.out.println(cpu.getLastMsg());
					cpu.printState();
					continue;
				}
				
				if (input.startsWith("peek")) {
					preStep = false;
					String addrString = input.substring(5);
					int addrInt = Integer.decode(addrString);
					System.out.println("Value at addr " + Tools.toHexString((short)addrInt) + " = " + Tools.toHexString(primarySlot.rdByte((short)addrInt)));
					continue;
				}
				if (input.startsWith("brpt")) {
					preStep = false;
					String addrString = input.substring(5);
					int addrInt = Integer.decode(addrString);
					System.out.println("Added break point at " + Tools.toHexString((short)addrInt));
					breakpoints.add((short)addrInt);
					continue;
				}
				if (input.startsWith("rbrp")) {
					preStep = false;
					String addrString = input.substring(5);
					int addrInt = Integer.decode(addrString);
					System.out.println("Removed break point " + Tools.toHexString((short)addrInt));
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
					System.out.println("Slot select: (" + getSlotForPage(0) + "/" + getSlotForPage(1) + "/" + getSlotForPage(2) + "/" +getSlotForPage(3) + ")");
					continue;
				}
				if (input.equals("slot info")) {
					preStep = false;
					System.out.println("Slot select: (" + getSlotForPage(0) + "/" + getSlotForPage(1) + "/" + getSlotForPage(2) + "/" +getSlotForPage(3) + ")");
					continue;
				}
				if (input.equals("interrupt")) {
					doVSyncInterrupt();
					cpu.execute();
					cpu.printState();
					continue;
				}
				if (input.equals("printstatebuf")) {
					List<String> stateBuf = cpu.getStateBuf();
					for (String s: stateBuf) System.out.println(s);
					continue;
				}
				if (input.equals("charbuf")) {
					System.out.print("Char buf <");
					for (int i = 0; i < 40; i++) {
						char c = (char)primarySlot.rdByte((short)(0xFBF0 + i));
						if (c <= 126 && c >= 32) {
							System.out.print((char)c);
						} else {
							System.out.print(".");
						}
					}
					System.out.print(">");
					System.out.println();
					continue;
				}
				if (input.equals("vramdump")) {
					for (int i = 0; i < 0x3fff; i++) {
						byte b = vdp.mem.rdByte((short)(i&0xffff));
						char c = 0;
						if ((b & 0xff) <= 126 && (b & 0xff) >= 32) {
							c = (char)b;
						} else {
							c = '.';
						}
						String hb = Tools.toHexString(b);
						System.out.print(""+c);
						if (i % 4 == 0) System.out.print(" ");
						if (i % 16 == 0) System.out.println(" <- " + Tools.toHexString4(i-16));
					}
					continue;
				}

				if (input.equals("continue")) return;
				System.out.println("Unknown command");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			debugMode = false;
		}
	}
	
	private void doVSyncInterrupt() {

		//if (debugMode) return;
		
		/* Set status bit (will be reset when read) */
		vdp.setStatusINT(true);

		/* Return if interrupt disabled ? */
		if (!vdp.getGINT()) return;

		cpu.interrupt();
		
		intCount++;
		if (intCount == 10) {
			System.out.println("Interrupt count " + intCount);
			intCount = 0;
		}
	}
	
	private void debug(String msg) {
		if (cpu != null) { 
			//System.out.println("ADDR = " + Tools.toHexString4(cpu.getPC()) + ": " + msg);
		} else {
			//System.out.println(msg);
		}
	}

	//@Override
	public int IORead(int address) {
		if (in[address] != null) return in[address].in((byte)address); 
		return 0;
	}

	//@Override
	public void IOWrite(int address, int data) {
		if (out[address] != null) out[address].out((byte)address, (byte)data); 
	}
	
}
