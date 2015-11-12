package emu.memory;

public class EmptySlot extends AbstractSlot {

	public EmptySlot() {
	}
	
	public EmptySlot(int s) {
	}
	
	public byte rdByte(short addr) {
		// Return 0xff (pull up)
		return (byte)0xff;
	}
	
	public final void wrtByte(short addr, byte value) {
	}

	@Override
	public boolean isWritable(short addr) {
		return false;
	}
	
}