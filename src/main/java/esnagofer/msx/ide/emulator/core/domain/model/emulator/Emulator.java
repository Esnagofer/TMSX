package esnagofer.msx.ide.emulator.core.domain.model.emulator;

import javax.annotation.Generated;

import esnagofer.msx.ide.emulator.core.domain.model.debugger.Debugger;
import esnagofer.msx.ide.emulator.core.domain.model.debugger.LocalDebugger;
import esnagofer.msx.ide.emulator.core.domain.model.hardware.ay38910.AY38910;
import esnagofer.msx.ide.emulator.core.domain.model.hardware.keyboard.Keyboard;
import esnagofer.msx.ide.emulator.core.domain.model.hardware.memory.AbstractMemory;
import esnagofer.msx.ide.emulator.core.domain.model.hardware.memory.EmptyMemory;
import esnagofer.msx.ide.emulator.core.domain.model.hardware.memory.Memory;
import esnagofer.msx.ide.emulator.core.domain.model.hardware.memory.RamMemory;
import esnagofer.msx.ide.emulator.core.domain.model.hardware.memory.RomMemory;
import esnagofer.msx.ide.emulator.core.domain.model.hardware.screen.Screen;
import esnagofer.msx.ide.emulator.core.domain.model.hardware.tms9918a.TMS9918A;
import esnagofer.msx.ide.emulator.core.domain.model.hardware.z80.Z80;
import esnagofer.msx.ide.emulator.core.domain.model.hardware.z80.Z80InDevice;
import esnagofer.msx.ide.emulator.core.domain.model.hardware.z80.Z80OutDevice;
import esnagofer.msx.ide.lib.Tools;
import esnagofer.msx.ide.lib.Validate;

/**
 * The main class that ties all the elements of an MSX computer together: CPU, VDP,
 * and keyboard decoder. All the relevant I/O ports are set up, as well as the 
 * 'primary slot' memory space with the underlying page switching mechanism.
 * 
 * @author tjitze.rienstra
 * @author esnagofer
 * 
 */
/**
 * @author user
 *
 */
public class Emulator {

	/** The Constant SLOT_0. */
	public static final int SLOT_0 = 0;

	/** The Constant SLOT_1. */
	public static final int SLOT_1 = 1;

	/** The Constant SLOT_2. */
	public static final int SLOT_2 = 2;

	/** The Constant SLOT_3. */
	public static final int SLOT_3 = 3;
	
	/**  Interrupt rate (interrupts per second). */
	public static final int INTERRUPT_RATE = 50;

	/**  CPU emulation speed (in Hz). */
	public static final int SPEED = 3500000;

	/**  Initial delay value (will be adjusted to match desired speed). */
	public static final int INITIAL_DELAY = 20;

	/** The cpu. */
	Z80 cpu;
	
	/** The vdp. */
	TMS9918A vdp;
	
	/** The psg. */
	AY38910 psg;
	
	/** The rom. */
	RomMemory rom;
	
	/** The cart 1. */
	RomMemory cart1;
	
	/** The cart 2. */
	RomMemory cart2;
	
	/** The ram. */
	RamMemory ram;
	
	/** The primary slot. */
	Memory primarySlot;
	
	/** The secondary slots. */
	Memory[] secondarySlots;

	/** The keyboard. */
	Keyboard keyboard;

	/** The screen. */
	Screen screen;
	
	/** The running. */
	public boolean /*debugMode = false, debugEnabled = true,*/ running = true;
	
	/** The ppi A slot select. */
	private byte ppiA_SlotSelect = 0;

	/** The ppi C keyboard. */
	private byte ppiC_Keyboard;

	/** The psg register select. */
	protected byte psg_RegisterSelect;

	/** The debugger. */
	private LocalDebugger debugger;

	/** The previous interrupt cycle. */
	private long previousInterruptCycle;

	/** The int count. */
	private int intCount;

	/** The delay. */
	private int delay;

	/** The bios loaded. */
	private boolean biosLoaded = false;

	/**
	 * Instantiates a new msx emulator.
	 *
	 * @param builder the builder
	 */
	@Generated("SparkTools")
	private Emulator(Builder builder) {
		super();
		transferSatate(builder);
		init();
		initHardware();
		initSlots();
		validateInvariants();
	}

	/**
	 * Inits the.
	 */
	private void init() {
		this.debugger = new LocalDebugger(this);
	}

	/**
	 * Validate invariants.
	 */
	private void validateInvariants() {
		Validate.notNull("MsxEmulator: 'cpu' not set", cpu);
		Validate.notNull("MsxEmulator: 'psg' not set", psg);
		Validate.notNull("MsxEmulator: 'vdp' not set", vdp);
		Validate.notNull("MsxEmulator: 'screen' not set", screen);
		Validate.notNull("MsxEmulator: 'keyboard' not set", keyboard);		
	}

	/**
	 * Transfer satate.
	 *
	 * @param builder the builder
	 */
	private void transferSatate(Builder builder) {
		this.cpu = builder.cpu;
		this.vdp = builder.vdp;
		this.psg = builder.psg;
		this.keyboard = builder.keyboard;
		this.screen = builder.screen;
		this.rom = builder.rom;
		this.ram = builder.ram;
		this.cart1 = builder.cart1;
		this.cart2 = builder.cart2;
	}

	/**
	 * Inits the slots.
	 */
	private void initSlots() {
		if (rom == null) {
			setSlot(SLOT_0, new RomMemory(0xC000, "system"));
		} else {
			setSlot(SLOT_0, rom);
		}
		setSlot(SLOT_1, new EmptyMemory("cart1 (empty)"));
		setSlot(SLOT_2, new EmptyMemory("cart2 (empty)"));
		if (ram == null) {
			setSlot(SLOT_3, new RamMemory("ram"));			
		} else {
			setSlot(SLOT_3, ram);
		}
		if (cart1 != null) {
			setSlot(SLOT_1, cart1);
		} else {
			if (cart2 != null) {
				setSlot(SLOT_2, cart2);
			}
		}
	}

	/**
	 * This method must be called after construction, to set up all parts of the emulation 
	 * (memory, cpu, keyboard, ppi, vdp).
	 */
	private void initHardware() {
		initMemory();
		initCPU();
		initKeyboard();
		initPPI();
		initVDP();
		initPSG();
	}

	/**
	 * Initialize memory system. A 'Primary Slot' address space is created with an underlying
	 * page switching mechanism. The selected secondary slot for each page is determined by the 
	 * ppiA_SlotSect value, which is accessed via I/O ports (see initPPI()) or, indirectly, via 
	 * the convenient getSlotForPage(...) and setSlotForPage(...) methods.
	 *  
	 */
	private void initMemory() {
		
		secondarySlots = new AbstractMemory[4];
		
		/* Create primary slot object */
		primarySlot = new AbstractMemory("primary") {

			@Override
			public byte rdByte(short addr) {
				byte val = 0;
				if ((addr & 0xffff) < 0x4000) val = secondarySlots[getSlotForPage(0)].rdByte(addr);
				else if ((addr & 0xffff) < 0x8000) val = secondarySlots[getSlotForPage(1)].rdByte(addr);
				else if ((addr & 0xffff) < 0xc000) val = secondarySlots[getSlotForPage(2)].rdByte(addr);
				else val = secondarySlots[getSlotForPage(3)].rdByte(addr);
				return val;
			}

			@Override
			public void wrtByte(short addr, byte value) {
				if (isWritable(addr)) {
					if ((addr & 0xffff) < 0x4000) secondarySlots[getSlotForPage(0)].wrtByte(addr, value);
					else if ((addr & 0xffff) < 0x8000) secondarySlots[getSlotForPage(1)].wrtByte(addr, value);
					else if ((addr & 0xffff) < 0xc000) secondarySlots[getSlotForPage(2)].wrtByte(addr, value);
					else secondarySlots[getSlotForPage(3)].wrtByte(addr, value);
				}
			}

			@Override
			public boolean isWritable(short addr) {
				if ((addr & 0xffff) < 0x4000) return secondarySlots[getSlotForPage(0)].isWritable(addr);
				else if ((addr & 0xffff) < 0x8000) return secondarySlots[getSlotForPage(1)].isWritable(addr);
				else if ((addr & 0xffff) < 0xc000) return secondarySlots[getSlotForPage(2)].isWritable(addr);
				else return secondarySlots[getSlotForPage(3)].isWritable(addr);
			}
		};
	}

	/**
	 * Initialize CPU.
	 */
	private void initCPU() {
		if(cpu == null) {
			cpu = Z80.newInstance(primarySlot);
		}
	}
	
	/**
	 * Initialize VDP. This method also sets up teh I/O ports that connect the VDP to the CPU
	 * (i.e., port 0x98 for VRAM data read/write and port 0x99 for status register I/O).
	 */
	private void initVDP() {
		if (vdp == null) {
			vdp = TMS9918A.newInstance(new RamMemory(0xFFFF, "vram"), screen);
			
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
	}

	/**
	 * Initialize keyboard.
	 */
	private void initKeyboard() {
	}
	
	/**
	 * Initialize PPI. At the emulation level, this amounts to setting up a number 
	 * of I/O ports, namely those for keyboard I/O (0xA9, 0xAA and 0xAB) and slot select
	 * I/O (port 0xA8).
	 */
	private void initPPI() {
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
	 * Initialize PSG.
	 */
	private void initPSG() {
		if (psg == null) {
			/* Construct PSG */
			psg = AY38910.newInstance();
			
			/* PSG register write port (port 0xA0) */
			cpu.registerInDevice(new Z80InDevice() {
				public byte in(byte port) {
					return psg_RegisterSelect;
				}
			}, 0xA0);
			cpu.registerOutDevice(new Z80OutDevice() {
				public void out(byte port, byte value) {
					//System.out.println("Set psg register" + value);
					psg_RegisterSelect = value;
				}
			}, 0xA0);

			/* PSG value write port (port 0xA1) */
			cpu.registerOutDevice(new Z80OutDevice() {
				public void out(byte port, byte value) {
					//if (psg_RegisterSelect != 15 && psg_RegisterSelect != 14) {
					//	System.out.println("Writing port " + (psg_RegisterSelect & 0xff) + " value " + (value & 0xff));
					//};
					psg.out(psg_RegisterSelect & 0xff, value & 0xff);
				}
			}, 0xA1);

			/* PSG value write read (port 0xA2) */
			cpu.registerInDevice(new Z80InDevice() {
				public byte in(byte port) {
					return (byte)psg.in(psg_RegisterSelect & 0xff);
				}
			}, 0xA2);			
		}
		psg.init();
		psg.start();
	}

	/**
	 * Sleep delay.
	 *
	 * @param delay the delay
	 */
	private void sleepDelay(int delay) {
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Return the secondary slot that is selected for the given page.
	 *
	 * @param page the page
	 * @return the slot for page
	 */
	final int getSlotForPage(int page) {
		switch (page) {
		case 0:	return ppiA_SlotSelect & 0x03;
		case 1:	return (ppiA_SlotSelect & 0x0C) >> 2;
		case 2:	return (ppiA_SlotSelect & 0x30) >> 4;
		case 3:	return (ppiA_SlotSelect & 0xC0) >> 6;
		default: throw new IllegalArgumentException("Illegal page number " + page);
		}
	}	

	/**
	 * Set the secondary slot that is selected for the given page. Setting 
	 * page 0 (resp. 1, 2, 3) will cause the address space 0000-4000 (resp.
	 * 4000-8000, 8000-B000, B000-FFFF) to be mapped to the given secondary slot.
	 *
	 * @param page the page
	 * @param slot the slot
	 */
	final void setSlotForPage(int page, int slot) {
		switch (page) {
		case 0:	ppiA_SlotSelect = (byte)((ppiA_SlotSelect & ~0x03) | slot); break;
		case 1:	ppiA_SlotSelect = (byte)((ppiA_SlotSelect & ~0x0C) | slot << 2); break;
		case 2:	ppiA_SlotSelect = (byte)((ppiA_SlotSelect & ~0x30) | slot << 4); break;
		case 3:	ppiA_SlotSelect = (byte)((ppiA_SlotSelect & ~0xC0) | slot << 6); break;
		}
	}	
	
	/**
	 * Trigger a VSync interrupt. Will set the relevant status bits in the VDP
	 * and calls cpu.interrupt() if interrupts are enabled.
	 */
	void triggerVSyncInterrupt() {
		vdp.setStatusINT(true);
		if (vdp.getStatusINT() && vdp.getGINT()) {
			cpu.interrupt();
		}
	}

	/**
	 * Cpu.
	 *
	 * @return the z80
	 */
	public Z80 cpu() {
		return cpu;
	}

	/**
	 * Reset the MSX (simply resets PC to 0x0000).
	 */
	public void reset() {
		cpu.PC = 0;
	}

	/**
	 * Paint.
	 */
	public void paint() {
		vdp.paint();
	}
	
	/**
	 * Sets the slot.
	 *
	 * @param slot the slot
	 * @param s the s
	 */
	public void setSlot(int slot, Memory s) {
		secondarySlots[slot] = s;
	}

	/**
	 * Checks if is paused.
	 *
	 * @return true, if is paused
	 */
	public boolean isPaused() {
		return !running;
	}
	
	/**
	 * Pause.
	 *
	 * @param f the f
	 */
	public void pause(boolean f) {
		running = !f;
	}

	/**
	 * Execute.
	 */
	private void execute() {
		if (vdp.getGINT() && vdp.getStatusINT()) {
			cpu.interrupt();
		}
		debugger.beforeExecute();
		cpu.execute();
		debugger.afterExecute();
	}

	/**
	 * Inits the start state.
	 *
	 * @param startInDebugMode the start in debug mode
	 */
	private void initStartState(boolean startInDebugMode) {
		previousInterruptCycle = System.currentTimeMillis();
		intCount = 0;
		delay = INITIAL_DELAY;
		if (startInDebugMode) {
			debugger.startDebugger();
		}
	}
	
	/**
	 * Adjust rate.
	 */
	private void adjustRate() {
		if (cpu.s >= (SPEED/INTERRUPT_RATE)) {
			cpu.s = (cpu.s - (SPEED/INTERRUPT_RATE));
			long now = System.currentTimeMillis();
			biosLoaded = biosLoaded || cpu.getPC() >= 0x4000; 
			if (biosLoaded) {
				sleepDelay(delay);
			}
			/* Trigger interrupt */
			vdp.setStatusINT(true);
			screen.refresh();
			/* Keep track of interrupt rate and correct delay if necessary */
			int checkInterval = 1;
			intCount++;
			if (now - previousInterruptCycle >= (1000 / checkInterval)) {
				if (Math.abs(intCount - (INTERRUPT_RATE / checkInterval)) > 1) {
					if ((intCount < INTERRUPT_RATE) && (delay > 0)) {
						delay -= 1;
					}
					if (intCount > INTERRUPT_RATE) {
						delay += 1;
					}
				}
				previousInterruptCycle = System.currentTimeMillis();
				intCount = 0;
			}
		}		
	}
	
	/**
	 * Start MSX. Basically a while loop that executes CPU instructions,
	 * triggers VSync interrupts, adjusts delay timing and stops when halted.
	 *
	 * @param startInDebugMode the start in debug mode
	 */
	public void start(boolean startInDebugMode) {
		initStartState(startInDebugMode);
		while (true) {
			execute();
			adjustRate();
		}
	}

	/**
	 * Debugger.
	 *
	 * @return the debugger
	 */
	public Debugger debugger() {
		return debugger;
	}

	/**
	 * Creates builder to build {@link Emulator}.
	 * @return created builder
	 */
	@Generated("SparkTools")
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Builder to build {@link Emulator}.
	 */
	@Generated("SparkTools")
	public static final class Builder {
		
		/** The cpu. */
		private Z80 cpu;
		
		/** The vdp. */
		private TMS9918A vdp;
		
		/** The psg. */
		private AY38910 psg;
		
		/** The keyboard. */
		private Keyboard keyboard;
		
		/** The screen. */
		private Screen screen;

		/** The rom. */
		private RomMemory rom;
		
		/** The cart 1. */
		private RomMemory cart1;
		
		/** The cart 2. */
		private RomMemory cart2;
		
		/** The ram. */
		private RamMemory ram;
		
		/**
		 * Instantiates a new builder.
		 */
		private Builder() {
		}

		/**
		 * With cpu.
		 *
		 * @param cpu the cpu
		 * @return the builder
		 */
		public Builder withCpu(Z80 cpu) {
			this.cpu = cpu;
			return this;
		}

		/**
		 * With vdp.
		 *
		 * @param vdp the vdp
		 * @return the builder
		 */
		public Builder withVdp(TMS9918A vdp) {
			this.vdp = vdp;
			return this;
		}

		/**
		 * With psg.
		 *
		 * @param psg the psg
		 * @return the builder
		 */
		public Builder withPsg(AY38910 psg) {
			this.psg = psg;
			return this;
		}

		/**
		 * With keyboard.
		 *
		 * @param keyboard the keyboard
		 * @return the builder
		 */
		public Builder withKeyboard(Keyboard keyboard) {
			this.keyboard = keyboard;
			return this;
		}

		/**
		 * With screen.
		 *
		 * @param screen the screen
		 * @return the builder
		 */
		public Builder withScreen(Screen screen) {
			this.screen = screen;
			return this;
		}

		/**
		 * With rom.
		 *
		 * @param rom the rom
		 * @return the builder
		 */
		public Builder withRom(RomMemory rom) {
			this.rom = rom;
			return this;
		}

		/**
		 * With cart 1.
		 *
		 * @param cart1 the cart 1
		 * @return the builder
		 */
		public Builder withCart1(RomMemory cart1) {
			this.cart1 = cart1;
			return this;
		}

		/**
		 * With cart 2.
		 *
		 * @param cart2 the cart 2
		 * @return the builder
		 */
		public Builder withCart2(RomMemory cart2) {
			this.cart2 = cart2;
			return this;
		}

		/**
		 * With ram.
		 *
		 * @param ram the ram
		 * @return the builder
		 */
		public Builder withRam(RamMemory ram) {
			this.ram = ram;
			return this;
		}

		/**
		 * Builds the.
		 *
		 * @return the msx emulator
		 */
		public Emulator build() {
			return new Emulator(this);
		}
	}

}
