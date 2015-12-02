package emu;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import emu.memory.RAMSlot;

public class TMS9918A {

	public static final int 
		IMG_SCALE = 2,
		VDP_WIDTH = 256, 
		VDP_HEIGHT = 192,
		MODE0_OFFSET = 8;
		
	/* VRAM size */
	private int ramSize = 0xFFFF;
	
	/* Buffered image to hold display contents */
	private BufferedImage img;
	
	/* VRAM */
	public RAMSlot mem;
		
	/* Registers */
	private byte[] registers = new byte[8];
	private byte statusRegister = 0;

	/* I/O variables */
	private short readWriteAddr;
	private byte readAhead;
	private boolean secondByteFlag = false;
	private byte ioByte0, ioByte1;
	
	private int[] spriteLineCount = new int[VDP_HEIGHT+16];
	private boolean[][] spritePriorityMatrix = new boolean[VDP_WIDTH+16][VDP_HEIGHT+16];
	private boolean[][] spriteCollisionMatrix = new boolean[VDP_WIDTH+16][VDP_HEIGHT+16];
	
	private volatile boolean writeDisabled = false; // Disable changing registers/vram
	
	public static final Color[] colors = { 
		(new Color(0,0,0,0)),		// 0
		(new Color(0,0,0)),			// 1
		(new Color(33,200,66)),		// 2
		(new Color(94,220,120)),	// 3
		(new Color(84,85,237)),		// 4
		(new Color(125,118,252)),	// 5
		(new Color(212,82,77)),		// 6
		(new Color(66,235,245)),	// 7
		(new Color(252,85,84)),		// 8
		(new Color(255,121,120)),	// 9
		(new Color(212,193,84)),	// A
		(new Color(230,206,128)),	// B
		(new Color(33,176,59)),		// C
		(new Color(201,91,186)),	// D
		(new Color(204,204,204)),	// E
		(new Color(255,255,255)),	// F
		};
	
	public TMS9918A() {
		mem = new RAMSlot(ramSize);
		img = new BufferedImage(VDP_WIDTH*IMG_SCALE, VDP_HEIGHT*IMG_SCALE, BufferedImage.TYPE_INT_ARGB);
	}
		
	public boolean getStatusBit(int bit) {
		return Tools.getBit(statusRegister, bit);
	}
	
	public void setStatusBit(int bit, boolean v) {
		if (writeDisabled) return;
		statusRegister = Tools.setBit(statusRegister, bit, v);
	}
	
	public boolean getStatusINT()		{ return getStatusBit(7); }
	public boolean getStatus5S()		{ return getStatusBit(6); }
	public boolean getStatusC()			{ return getStatusBit(5); }
	public boolean getStatusFS4()		{ return getStatusBit(4); }
	public boolean getStatusFS3()		{ return getStatusBit(3); }
	public boolean getStatusFS2()		{ return getStatusBit(2); }
	public boolean getStatusFS1()		{ return getStatusBit(1); }
	public boolean getStatusFS0()		{ return getStatusBit(0); }
	
	public void setStatusINT(boolean v)			{ setStatusBit(7,v); }
	public void setStatus5S(boolean v)			{ setStatusBit(6,v); }
	public void setStatusC(boolean v)			{ setStatusBit(5,v); }
	public void setStatusFS4(boolean v)			{ setStatusBit(4,v); }
	public void setStatusFS3(boolean v)			{ setStatusBit(3,v); }
	public void setStatusFS2(boolean v)			{ setStatusBit(2,v); }
	public void setStatusFS1(boolean v)			{ setStatusBit(1,v); }
	public void setStatusFS0(boolean v)			{ setStatusBit(0,v); }
	
	public boolean getBit(int reg, int bit) {
		return Tools.getBit(registers[reg],bit);
	}

	public void setBit(int reg, int bit, boolean value) {
		if (writeDisabled) return;
		registers[reg] = Tools.setBit(registers[reg], bit, value);
	}
	
	public boolean getEXTVID()	{ return getBit(0, 0); }
	public boolean getM2()		{ return getBit(0, 1); }
	
	public boolean getMAG()		{ return getBit(1, 0); }
	public boolean getSI()		{ return getBit(1, 1); }
	public boolean getM3()		{ return getBit(1, 3); }
	public boolean getM1()		{ return getBit(1, 4); }
	public boolean getGINT()	{ return getBit(1, 5); }
	public void setGINT(boolean b)	{ setBit(1, 5, b); }
	public boolean getBL()		{ return getBit(1, 6); }
	public boolean get416K()	{ return getBit(1, 7); }
	
	public boolean getPN10()	{ return getBit(2, 0); }
	public boolean getPN11()	{ return getBit(2, 1); }
	public boolean getPN12()	{ return getBit(2, 2); }
	public boolean getPN13()	{ return getBit(2, 3); }
	
	public boolean getCT6()		{ return getBit(3, 0); }
	public boolean getCT7()		{ return getBit(3, 1); }
	public boolean getCT8()		{ return getBit(3, 2); }
	public boolean getCT9()		{ return getBit(3, 3); }
	public boolean getCT10()	{ return getBit(3, 4); }
	public boolean getCT11()	{ return getBit(3, 5); }
	public boolean getCT12()	{ return getBit(3, 6); }
	public boolean getCT13()	{ return getBit(3, 7); }
	
	public boolean getPG11()	{ return getBit(4, 0); }
	public boolean getPG12()	{ return getBit(4, 1); }
	public boolean getPG13()	{ return getBit(4, 2); }

	public boolean getSA7()		{ return getBit(5, 0); }
	public boolean getSA8()		{ return getBit(5, 1); }
	public boolean getSA9()		{ return getBit(5, 2); }
	public boolean getSA10()	{ return getBit(5, 3); }
	public boolean getSA11()	{ return getBit(5, 4); }
	public boolean getSA12()	{ return getBit(5, 5); }
	public boolean getSA13()	{ return getBit(5, 6); }

	public boolean getSG11()	{ return getBit(6, 0); }
	public boolean getSG12()	{ return getBit(6, 1); }
	public boolean getSG13()	{ return getBit(6, 2); }

	public boolean getBD0()		{ return getBit(7, 0); }
	public boolean getBD1()		{ return getBit(7, 1); }
	public boolean getBD2()		{ return getBit(7, 2); }
	public boolean getBD3()		{ return getBit(7, 3); }
	public boolean getTC0()		{ return getBit(7, 4); }
	public boolean getTC1()		{ return getBit(7, 5); }
	public boolean getTC2()		{ return getBit(7, 6); }
	public boolean getTC3()		{ return getBit(7, 7); }

	public short getNameTableAddr() {
		int v = 0;	// TODO: optimize
		if (getPN10()) v+= 1<<10;
		if (getPN11()) v+= 1<<11;
		if (getPN12()) v+= 1<<12;
		if (getPN13()) v+= 1<<13;
		return (short)v;
	}
	
	public short getColorTableAddr() {
		int v = 0; // TODO: optimize
		if (getCT6()) v += 1<<6;
		if (getCT7()) v += 1<<7;
		if (getCT8()) v += 1<<8;
		if (getCT9()) v += 1<<9;
		if (getCT10()) v += 1<<10;
		if (getCT11()) v += 1<<11;
		if (getCT12()) v += 1<<12;
		if (getCT13()) v += 1<<13;
		return (short)v;	
	}
	
	public short getPatternTableAddr() {
		int v = 0; // TODO: optimize
		if (getPG11()) v+= 1<<11;
		if (getPG12()) v+= 1<<12;
		if (getPG13()) v+= 1<<13;
		return (short)v;
	}
	
	public short getSpriteAttrTable() {
		int v = 0; // TODO: optimize
		if (getSA7()) v += 1<<7;
		if (getSA8()) v += 1<<8;
		if (getSA9()) v += 1<<9;
		if (getSA10()) v += 1<<10;
		if (getSA11()) v += 1<<11;
		if (getSA12()) v += 1<<12;
		if (getSA13()) v += 1<<13;
		return (short)v;	
	}
	
	public short getSpriteGenTable() {
		int v = 0; // TODO: optimize
		if (getSG11()) v += 1<<11;
		if (getSG12()) v += 1<<12;
		if (getSG13()) v += 1<<13;
		return (short)v;	
	}

	public int getOnBitColor() {
		return (registers[7] & 0xFF) >> 4; 
	}
	
	public int getOffBitColor() {
		return (registers[7] & 0x0F); 
	}
	
	public int getFifthSpriteNr() {
		return (registers[7] & 0x0F); 
	}

	public byte getSpriteX(int sprite) {
		int attrTable = getSpriteAttrTable();
		return mem.rdByte((short)((attrTable & 0xffff) + (sprite * 4) + 1));
	}

	public byte getSpriteY(int sprite) {
		int attrTable = getSpriteAttrTable();
		return mem.rdByte((short)((attrTable & 0xffff) + (sprite * 4) + 0));
	}

	public byte getSpritePattern(int sprite) {
		int attrTable = getSpriteAttrTable();
		return mem.rdByte((short)((attrTable & 0xffff) + (sprite * 4) + 2));
	}

	public byte getSpriteColour(int sprite) {
		int attrTable = getSpriteAttrTable();
		return mem.rdByte((short)((attrTable & 0xffff) + (sprite * 4) + 3));
	}

	/**
	 * Port 0 read.
	 * 
	 * @return
	 */
	public final byte readVRAMData() {
		byte result = readAhead;
		readAhead = mem.rdByte(readWriteAddr);
		increaseReadWriteAddr();
		secondByteFlag = false;
		return result;
	}
	
	/**
	 * Port 0 write.
	 * 
	 * @param value
	 */
	public final void writeVRAMData(byte value) {
		if (writeDisabled) return;
		readAhead = value;
		mem.wrtByte(readWriteAddr, readAhead);
		increaseReadWriteAddr();
		secondByteFlag = false;
	}

	/**
	 * Write port one. See documentation.
	 * 
	 * @param value
	 */
	public final void writeRegister(byte value) {

		if (writeDisabled) return;
		
		if (!secondByteFlag) {
			ioByte0 = value;
			secondByteFlag = true;
		} else  {
			ioByte1 = value;

			/* Are we doing memory i/o? */
			if (((ioByte1 & 0x80) >> 7) == 0) {
				
				/* If so, set the read/write address to value stored in ioBytes */
				readWriteAddr = (short)((ioByte0 & 0xFF) | ((ioByte1 & 0x003F) << 8));
	
				/* In case of read: fill read ahead buffer and increase read/write address */
				if (((ioByte1 & 0xC0) >> 6) == 0) {
					readAhead = mem.rdByte(readWriteAddr);
					increaseReadWriteAddr();
				}
			}
			
			/* If not, we're doing register i/o */
			else if (((ioByte1 & 0x80) >> 7) == 1) {
				if ((ioByte1 & 0x07) <= 7) {
					registers[ioByte1 & 0x07] = ioByte0;
				}
			}

			secondByteFlag = false;
		}

	}

	public final void increaseReadWriteAddr() {
		readWriteAddr = (short) ((readWriteAddr & 0xffff) + 1);
		if ((readWriteAddr & 0xFFFF) == 0x3FFF) readWriteAddr = 0;
	}
	
	/*
	 * Return value of status bit. The side-effects of doing this is
	 * that the INT and C flags are reset, and that the second byte
	 * flag is reset, which affects the writeRegister behaviour.
	 */
	public final byte readStatus() {
		secondByteFlag = false;
		byte value = statusRegister;
		setStatusINT(false);
		setStatusC(false);
		return value;
	}
	
	/**
	 * Draw the backdrop
	 */
	public void drawBackDrop() {
		Color off = colors[getOffBitColor()];
		for (int x = 0; x < 256; x++) {
			for (int y = 0; y < 192; y++) {
				setPixel(x, y, colors[1]);
				//setPixel(x, y, off);
			}
		}
	}
	
	/**
	 * Draw pattern according to mode 0 (screen 1 / graphic 1)
	 */
	public void drawMode0() {
		short nameTablePtr = getNameTableAddr();
		short patternTableBase = getPatternTableAddr();
		short colorTableBase = getColorTableAddr();
		// For all x/y positions
		for (int y = 0; y < 24; y++) {
			for (int x = 0; x < 32; x++) {
				// Read index of pattern from name table address
				byte patternIdx = mem.rdByte(nameTablePtr);
				int patternAddr = (patternTableBase & 0xffff) + ((patternIdx & 0xff) * 8);
				// For all lines of the character
				for (int charLine = 0; charLine < 8; charLine++) {
					byte line = mem.rdByte((short)((patternAddr & 0xFFFF) + charLine));
					// For all pixels of the line
					for (int linePos = 0; linePos < 8; linePos++) {
						// Calculate location of pixel
						int px = 7 + ((x * 8) - linePos);	
						int py = ((y * 8) + charLine);
						// Get foreground/background
						int colorTableAddr = ((colorTableBase & 0xffff) + ((patternIdx & 0xff)/8));
						byte color = mem.rdByte((short)colorTableAddr);
						Color fg = colors[(color & 0xf0) >> 4];
						Color bg = colors[(color & 0x0f)];
						setPixel(px, py, Tools.getBit(line, linePos)? fg: bg);
					}					
				}
				// Update name table pointer
				nameTablePtr = (short)(nameTablePtr + 1);
			}
		}
	}
	
	private final void setPixel(int px, int py, Color color) {
		if (color.getAlpha() == 0) return;
		px <<= IMG_SCALE-1; py <<= IMG_SCALE-1;
		if (px+1 >= VDP_WIDTH*IMG_SCALE || py+1 >= VDP_HEIGHT*IMG_SCALE || px < 0 || py < 0) return;
		for (int xs = 0; xs < IMG_SCALE; xs++) {
			for (int ys = 0; ys < IMG_SCALE; ys++) {
				img.setRGB(px + xs, py + ys, color.getRGB());
			}
		}
	}

	/**
	 * Draw pattern according to mode 1 (screen 0 / text 1)
	 */
	public void drawMode1() {
		short nameTablePtr = getNameTableAddr();
		short patternTableBase = getPatternTableAddr();
		Color offBit = colors[getOffBitColor()];
		Color onBit = colors[getOnBitColor()];
		// For all x/y positions
		for (int y = 0; y < 24; y++) {
			for (int x = 0; x < 40; x++) {
				// Read index of pattern from name table address
				byte patternIdx = mem.rdByte(nameTablePtr);
				int patternAddr = (patternTableBase & 0xffff) + ((patternIdx & 0xff) * 8);
				// For all lines of the character
				for (int charLine = 0; charLine < 8; charLine++) {
					byte line = mem.rdByte((short)((patternAddr & 0xFFFF) + charLine));
					// For all pixels of the line
					for (int linePos = 0; linePos < 6; linePos++) {
						// Calculate location of pixel
						int px = 5 + ((x * 6) - linePos);	
						int py = ((y * 8) + charLine);
						// Set pixel
						setPixel(px + MODE0_OFFSET, py, Tools.getBit(line, linePos+2)? onBit: offBit);
					}					
				}
				nameTablePtr = (short)(nameTablePtr + 1);
			}
		}
	}
	
	/**
	 * Draw pattern according to mode 1 (screen 2 / graphic 2)
	 */
	public void drawMode2() {
		short nameTableBase = getNameTableAddr();
		int nameTableIdx = 0;
		short patternTableBase = getPG13()?(short)0x2000:0;
		boolean bit0 = this.getBit(4,0);
		boolean bit1 = this.getBit(4,1);
		int patternMask = ((bit0?0:(1 << 7)) | (bit1?0:(1 << 8)));
		short colorTableBase = getCT13()?(short)0x2000:0;
		// For all x/y positions
		for (int y = 0; y < 24; y++) {
			for (int x = 0; x < 32; x++) {
				// Read index of pattern from name table address
				byte patternIdx = mem.rdByte((short)((nameTableBase & 0xffff) + nameTableIdx));
				int patternAddr = (patternTableBase & 0xffff) + ((patternIdx & 0xff) * 8);
				//patternAddr += (2048 * (nameTableIdx / 256));
				if (bit0 && (nameTableIdx / 256) == 1) patternAddr += 2048;
				if (bit1 && (nameTableIdx / 256) == 2) patternAddr += 4096;
				// For all lines of the character
				for (int charLine = 0; charLine < 8; charLine++) {
					byte line = mem.rdByte((short)((patternAddr & 0xffff) + charLine));
					int colorTableAddr = (colorTableBase & 0xffff) + ((patternIdx & 0xff) * 8);
					//colorTableAddr += (2048 * (nameTableIdx / 256));
					if (bit0 && (nameTableIdx / 256) == 1) colorTableAddr += 2048;
					if (bit1 && (nameTableIdx / 256) == 2) colorTableAddr += 4096;
					byte lineColor = mem.rdByte((short)((colorTableAddr & 0xFFFF) + charLine));
					Color fg = colors[(lineColor & 0xf0) >> 4] ;
					Color bg = colors[(lineColor & 0x0f)] ;
					// For all pixels of the line
					int py = ((y * 8) + charLine);
					for (int linePos = 0; linePos < 8; linePos++) {
						// Calculate location of pixel
						int px = 7 + ((x * 8) - linePos);	
						setPixel(px, py, Tools.getBit(line, linePos)? fg: bg);
					}					
				}
				nameTableIdx += 1;
			}
		}
	}
	
	public void drawSprites() {
		short patternTableAddr = getSpriteGenTable();
		boolean siFlag = getSI();
		
		// Clear sprite per line count array
		for (int i = 0; i < VDP_HEIGHT; i++) spriteLineCount[i] = 0;
		
		// Clear priority array
		for (int x = 0; x < VDP_WIDTH; x++) for (int y = 0; y < VDP_HEIGHT; y++) spritePriorityMatrix[x][y] = false;
		
		// Clear collision array
		for (int x = 0; x < VDP_WIDTH; x++) for (int y = 0; y < VDP_HEIGHT; y++) spriteCollisionMatrix[x][y] = false;
		
		// For each sprite ...
		for (int i = 0; i < 32; i++) {
			
			// Break if 0xD0 encountered
			if ((getSpriteY(i) & 0xff) == 0xD0) break;
			
			// Get sprite info
			int sx = getSpriteX(i) & 0xff;
			int sy = (getSpriteY(i) & 0xff) + 1;
			int patternIdx = getSpritePattern(i) & 0xff;
			byte colour = getSpriteColour(i);
						
			// If EC bit set: place sprite 32 pixels to the left
			if ((colour & 0x80) != 0) sx -= 32;
			
			// Keep track of number of sprites per line
			for (int yPos = sy; yPos < sy + (siFlag? 16: 8) && yPos < VDP_HEIGHT; yPos++) spriteLineCount[yPos]++;

			// If sprite is transparent then skip
			if ((colour & 0x0f) == 0) continue;
			
			// Draw sprite (qx/qy range over quadrants for 16x16 mode. In 8x8 mode, qx = qy = 0)
			for (int qx = 0; qx < (siFlag? 2: 1); qx++) {
				for (int qy = 0; qy < (siFlag? 2: 1); qy++) {
					int quadrantNumber = (2 * qx) + qy;
					for (int y = 0; y < 8; y++) {
						for (int x = 0; x < 8; x++) {
							int xPos = sx + x + (qx * 8), yPos = sy + y + (qy * 8);
							if (xPos >= VDP_WIDTH || yPos >= VDP_HEIGHT || xPos < 0 || yPos < 0) continue; // Out of bounds, skip
							
							// Mark coincidence (TODO: is this correct timing-wise?)
							if (spriteCollisionMatrix[xPos][yPos]) setStatusC(true); 
							spriteCollisionMatrix[xPos][yPos] = true;
							
							// Do we need to fill this pixel?
							boolean fill = (mem.rdByte((short)(patternTableAddr + (8*(patternIdx + quadrantNumber) + y))) & (1 << (7-x))) != 0;
							if (!fill) continue;

							// Are there already 4 sprites drawn on the current line? I so, mark 5th sprite flag and status register, and skip
							if (spriteLineCount[yPos] > 4) {	
								setStatus5S(true);
								statusRegister = (byte)((statusRegister & 0xE0) | (i & 0x1F)); 
								continue;
							}
							
							// We iterate from high priority to low priority. Skip if a higher priority sprite was already drawn here.
							if (spritePriorityMatrix[xPos][yPos]) continue; 
							spritePriorityMatrix[xPos][yPos] = true;

							// Draw the pixel
							setPixel(xPos, yPos, colors[colour & 0x0f]);
						}
					}
				}
			}
		}
	}

	public void drawMode3() {
		System.err.println("drawmode3 not implemented");
		drawMode0();
	}

	public int debugCount = 0;
	
	public void drawPattern() {
	}
	
	public void paint(Graphics g) {
		if (img == null) return;
		
		// Draw backdrop
		drawBackDrop();

		// Draw pattern
		if (!getM1() && !getM2() && !getM3()) {
			drawMode0();
		} else if (getM1()) {
			drawMode1();
		} else if (getM2()) {
			drawMode2();
		} else if (getM3()) {
			drawMode3();
		}

		// Draw sprites
		if (!getM1() && getBL()) {
			drawSprites();
		}
		
		g.drawImage(img, 0, 0, null);

	}
}
