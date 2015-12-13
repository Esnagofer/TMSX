package emu.memory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import emu.Tools;

public class KonamiRom extends AbstractSlot {

	volatile private byte[][] bank = new byte[4][];
	volatile private byte[][] rom = new byte[32][];
	
	public void loadRom(String fileName, int xsize) throws IOException {
		File file=new File(fileName);
		FileInputStream in = new FileInputStream(file);
		int byteCount = 0x2000, block = -1;
		while (true) {
			int v = in.read();
			if (v == -1) {
				in.close();
				break;
			};
			if (byteCount == 0x2000) {
				block++;
				rom[block] = new byte[0x2000];
				byteCount = 0;
			}
			rom[block][byteCount] = (byte)v;
			byteCount++;
		}
		bank[0] = rom[0];
		bank[1] = rom[1];
		bank[2] = rom[2];
		bank[3] = rom[3];
	}
	
	@Override
	public byte rdByte(short addr) {
		int iAddr = addr & 0xffff;
		if (iAddr >= 0x4000 && iAddr < 0x6000) {
			return bank[0] == null? (byte)0xff: bank[0][iAddr - 0x4000];
		}
		if (iAddr >= 0x6000 && iAddr < 0x8000) {
			return bank[1] == null? (byte)0xff: bank[1][iAddr - 0x6000];
		}
		if (iAddr >= 0x8000 && iAddr < 0xA000) {
			return bank[2] == null? (byte)0xff: bank[2][iAddr - 0x8000];
		}
		if (iAddr >= 0xA000 && iAddr < 0xC000) {
			return bank[3] == null? (byte)0xff: bank[3][iAddr - 0xA000];
		}
		return (byte)0xff;
	}

	@Override
	public void wrtByte(short addr, byte value) {
		System.out.println("Writing " + Tools.toHexString(addr) + " value " + Tools.toHexString(value));
		int iAddr = addr & 0xffff;
		//if (iAddr >= 0x4000 && iAddr < 0x6000) {
		//	setRom(0, value & 0xff);
		//}
		if (iAddr >= 0x6000 && iAddr < 0x8000) {
			setRom(1, (value & 0xff) % 16);
		}
		if (iAddr >= 0x8000 && iAddr < 0xA000) {
			setRom(2, (value & 0xff) % 16);
		}
		if (iAddr >= 0xA000 && iAddr < 0xC000) {
			setRom(3, (value & 0xff) % 16);
		}
		return;
	}

	/** Selects a block of the ROM image for reading in a certain region.
	  * @param region number of 8kB region in Z80 address space
	  *   (region i starts at Z80 address i * 0x2000)
	  * @param block number of 8kB block in the ROM image
	  *   (block i starts at ROM image offset i * 0x2000)
	  */
	private void setRom(int region, int value) {
		System.out.println("Setting bank " + region + " to " + value);
		bank[region] = rom[value];
	}

	@Override
	public boolean isWritable(short addr) {
		return true;
	}


	
}
