package esnagofer.msx.ide.emulator.core.domain.model.components.cartridgeloaders;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import esnagofer.msx.ide.emulator.core.domain.model.components.memory.AbstractMemory;

/**
 * This class extends abstract slot and implements the logic behind the
 * Konami4/Konami5 mappers (and possibly others). They allow the definition
 * of a number of pages of arbitrary size (typically 8K), number (typically 4)
 * and at arbitrary start address (typically 0x4000). The ROM image is loaded
 * into blocks. Writing to a given address within a page (indicated by 
 * setPageAddrOffset) causes a given page to map to a given block.
 * 
 * @author tjitze.rienstra
 * @author esnagofer
 *
 */
public class BlockMapper extends AbstractMemory {

	/** The page start. */
	public short pageStart = 0x4000;
	
	/** The page size. */
	public short pageSize = 0x2000;
	
	/** The page num. */
	public int pageNum = 4;
	
	/** The block num. */
	public int blockNum = 32;
	
	/** The first mappable page. */
	public int firstMappablePage = 1;
	
	/** The set page addr offset. */
	public short setPageAddrOffset = 0;

	/** The blocks. */
	/* Blocks of the ROM image */
	public byte[][] blocks = new byte[blockNum][];
	
	/** The pages. */
	/* Mappings */
	public byte[][] pages = new byte[pageNum][];
	
	/**
	 * Instantiates a new block mapper.
	 *
	 * @param name the name
	 * @param pageStart the page start
	 * @param pageSize the page size
	 * @param pageNum the page num
	 * @param blockNum the block num
	 * @param firstMappablePage the first mappable page
	 * @param setPageAddrOffset the set page addr offset
	 */
	public BlockMapper(String name, short pageStart, short pageSize, int pageNum, int blockNum, int firstMappablePage, short setPageAddrOffset) {
		super(name);
		this.pageStart = pageStart;
		this.pageSize = pageSize;
		this.pageNum = pageNum;
		this.blockNum = blockNum;
		this.firstMappablePage = firstMappablePage;
		this.setPageAddrOffset = setPageAddrOffset;
	}

	/**
	 * Instantiates a new block mapper.
	 *
	 * @param name the name
	 */
	public BlockMapper(String name) {
		super(name);
	}

	/* (non-Javadoc)
	 * @see emu.memory.AbstractSlot#rdByte(short)
	 */
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

	/**
	 * Gets the page start.
	 *
	 * @param page the page
	 * @return the page start
	 */
	private short getPageStart(int page) {
		return (short)((pageStart & 0xffff) + (page * (pageSize & 0xffff)));
	}

	/**
	 * Gets the page.
	 *
	 * @param addr the addr
	 * @return the page
	 */
	private int getPage(short addr) {
		int page = ((addr & 0xffff) - (pageStart & 0xffff)) / pageSize;
		if (page >= 0 && page < 4) return page;
		return -1;
	}

	/* (non-Javadoc)
	 * @see emu.memory.AbstractSlot#wrtByte(short, byte)
	 */
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

	/* (non-Javadoc)
	 * @see emu.memory.AbstractSlot#isWritable(short)
	 */
	@Override
	public boolean isWritable(short addr) {
		return true;
	}
	
	/**
	 * Load.
	 *
	 * @param fileName the file name
	 * @param romSize the rom size
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void load(String fileName, int romSize) throws IOException {
	
		/* Load ROM */
		File file=new File(fileName);
		byte[] contents = new byte[romSize];

		try (FileInputStream in = new FileInputStream(file)) {
			if (in.read(contents) == -1) throw new RuntimeException("Wrong ROM file size");
			if (in.read() != -1) throw new RuntimeException("Wrong ROM file size");
			in.close();
		}

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
