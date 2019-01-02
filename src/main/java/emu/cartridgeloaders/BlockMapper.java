package emu.cartridgeloaders;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import emu.Tools;
import emu.memory.AbstractSlot;

/**
 * This class extends abstract slot and implements the logic behind the
 * Konami4/Konami5 mappers (and possibly others). They allow the definition
 * of a number of pages of arbitrary size (typically 8K), number (typically 4)
 * and at arbitrary start address (typically 0x4000). The ROM image is loaded
 * into blocks. Writing to a given address within a page (indicated by 
 * setPageAddrOffset) causes a given page to map to a given block.
 * 
 * @author tjitze.rienstra
 *
 */
public class BlockMapper extends AbstractSlot {

	public short pageStart = 0x4000;
	public short pageSize = 0x2000;
	public int pageNum = 4;
	public int blockNum = 32;
	public int firstMappablePage = 1;
	public short setPageAddrOffset = 0;

	/* Blocks of the ROM image */
	public byte[][] blocks = new byte[blockNum][];
	
	/* Mappings */
	public byte[][] pages = new byte[pageNum][];
	
	public BlockMapper(String name, short pageStart, short pageSize, int pageNum, int blockNum, int firstMappablePage, short setPageAddrOffset) {
		super(name);
		this.pageStart = pageStart;
		this.pageSize = pageSize;
		this.pageNum = pageNum;
		this.blockNum = blockNum;
		this.firstMappablePage = firstMappablePage;
		this.setPageAddrOffset = setPageAddrOffset;
	}

	public BlockMapper(String name) {
		super(name);
	}

	@Override
	public byte rdByte(short addr) {
		int page = getPage(addr);
		if (page >= 0 && page < 4) {
			short pageStart = getPageStart(page);
			if (pages[page] == null) return (byte)0xff;
			return pages[page][(addr & 0xffff) - (pageStart & 0xffff)];
		}
		return (byte)0xff;
	}

	private short getPageStart(int page) {
		return (short)((pageStart & 0xffff) + (page * (pageSize & 0xffff)));
	}

	private int getPage(short addr) {
		int page = ((addr & 0xffff) - (pageStart & 0xffff)) / pageSize;
		if (page >= 0 && page < 4) return page;
		return -1;
	}

	@Override
	public void wrtByte(short addr, byte value) {
		/* Get page corresponding to address */
		int page = getPage(addr);
		/* Valid page ? */
		if (page >= firstMappablePage && page < 4) {
			/* If writing to setpage address ... */
			short pageAddr = (short)((pageStart & 0xffff) + ((pageSize & 0xffff) * page));
			if (addr == (short)((pageAddr & 0xffff) + (setPageAddrOffset & 0xffff))) {
				/* Set page to given block */
				pages[page] = blocks[(value & 0xff) % blockNum];
			}
		}
	}

	@Override
	public boolean isWritable(short addr) {
		return true;
	}
	
	public void load(String fileName, int romSize) throws IOException {
	
		/* Load ROM */
		File file=new File(fileName);
		byte[] contents = new byte[romSize];
		FileInputStream in = new FileInputStream(file);
		if (in.read(contents) == -1) throw new RuntimeException("Wrong ROM file size");
		if (in.read() != -1) throw new RuntimeException("Wrong ROM file size");
		in.close();

		/* Copy to blocks */
		int block = -1, addr = pageSize;
		for (int i = 0; i < romSize; i++) {
			byte value = contents[i];
			if (addr == pageSize) {
				block++;
				addr = 0;
				blocks[block] = new byte[pageSize];
			}
			blocks[block][addr] = value;
			addr++;
		}
		
		/* Set initial mapping */
		pages[0] = blocks[0];
		pages[1] = blocks[1];
		pages[2] = blocks[2];
		pages[3] = blocks[3];
		
		System.out.println("BlockMapper " + getName() + " wrote " + block + " blocks. File: " + fileName);

	}

}
