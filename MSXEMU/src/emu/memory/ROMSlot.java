package emu.memory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ROMSlot extends AbstractSlot {

	private int size = 65536;
	
	public final byte[] mem;
	
	public ROMSlot() {
		mem = new byte[size];
	}
	
	public ROMSlot(int s) {
		this.size = s;
		mem = new byte[size];
	}
	
	public byte rdByte(short addr) {
		return (addr & 0xffff) < size? mem[addr & 0xffff]: (byte)0xff;
	}
	
	public final void wrtByte(short addr, byte value) {
		mem[addr & 0xffff] = value;
	}

	@Override
	public boolean isWritable(short addr) {
		return false;
	}
	
}
