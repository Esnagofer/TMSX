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

	private int ramSize = 0x3FFF;
	
	private BufferedImage img;
	
	private RAMSlot mem;
		
	private byte[] registers = new byte[8];
//	private byte statusRegister = 0;
	
	private short readWriteAddr;
	private byte readAhead;
	
	private boolean secondByteFlag = false;
	private byte ioByte0, ioByte1;

	private int count;
	
	public static final int
		VDP_WIDTH = 256,
		VDP_HEIGHT = 192;

	public static final int[] colors = { 
		(new Color(0,0,0,0)).getRGB(),	// 0
		(new Color(0,0,0)).getRGB(),	// 1
		(new Color(33,200,66)).getRGB(),	// 2
		(new Color(94,220,120)).getRGB(),	// 3
		(new Color(84,85,237)).getRGB(),	// 4
		(new Color(125,118,252)).getRGB(),	// 5
		(new Color(212,82,77)).getRGB(),	// 6
		(new Color(66,235,245)).getRGB(),	// 7
		(new Color(252,85,84)).getRGB(),	// 8
		(new Color(255,121,120)).getRGB(),	// 9
		(new Color(212,193,84)).getRGB(),	// A
		(new Color(230,206,128)).getRGB(),	// B
		(new Color(33,176,59)).getRGB(),	// C
		(new Color(201,91,186)).getRGB(),	// D
		(new Color(204,204,204)).getRGB(),	// E
		(new Color(255,255,255)).getRGB(),	// F
		};
	
	public static final int
		MEM_SIZE = 0,		
		BLK_SCREEN = 1,		
		IE = 2,				
		M1 = 3,				
		M2 = 4,				
		M3 = 5,				
		EXT_VID = 6,			
		SPR_SIZE = 7,		
		SPR_MAG = 8,		
		FRAME_FLAG = 9,
		FIFTH_SPR = 10,
		C = 11;
	
	public static final int reg[] = new int[] {
		1, 1, 1, 1, 1, 0, 0, 1, 1, 8, 8, 8
	};

	
	public static final int pos[] = new int[] {
		7, 6, 5, 4, 3, 1, 0, 1, 0, 7, 6, 5
	};
	public TMS9918A() {
		mem = new RAMSlot(ramSize);
	}
	
	public Mode getMode() {
		if (Tools.getBit(registers[0], 6)) return Mode.Graphics2;
		if (Tools.getBit(registers[1], 4)) return Mode.MultiColor;
		if (Tools.getBit(registers[1], 3)) return Mode.Text;
		return Mode.Graphics1;
	}
	
	public boolean getBit(int bit) {
		return Tools.getBit(registers[reg[bit]], pos[bit]);
	}

	public void setBit(int bit, boolean value) {
		registers[reg[bit]] = Tools.setBit(registers[reg[bit]], pos[bit], value);
	}

	public short getNameTableAddr() {
		return (short)(registers[2] * 0x400);
	}
	
	public short getColorTableAddr() {
		return (short)(registers[3] * 0x40);
	}
	
	public short getPatternTableAddr() {
		return (short)(registers[4] * 0x800);
	}
	
	public short getSpriteAttrTable() {
		return (short)(registers[5] * 0x80);
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

	// Port 0 read
	public final byte readVRAMData() {
		byte result = readAhead;
		readAhead = mem.rdByte(readWriteAddr);
		increaseReadWriteAddr();
		secondByteFlag = false;
		return result;
	}

	// Port 0 write
	public final void writeVRAMData(byte value) {
		readAhead = value;
		mem.wrtByte(readWriteAddr, readAhead);
		increaseReadWriteAddr();
		secondByteFlag = false;
	}

	// Port 1 write
	public final void writeRegister(byte value) {

		if (!secondByteFlag) {
			ioByte0 = value;
			secondByteFlag = true;
		} else  {
			ioByte1 = value;

			/* Are we doing memory i/o? */
			if ((ioByte1 & 0xF0) == 0) {
				
				/* If so, set the read/write address to value stored in ioBytes */
				readWriteAddr = (short)((ioByte0 & 0x00FF) | ((ioByte1 & 0x003F) << 8));
	
				/* In case of read: fill read ahead buffer and increase read/write address */
				if ((ioByte1 & 0x40) == 0) {
					readAhead = mem.rdByte(readWriteAddr);
					increaseReadWriteAddr();
				}
			}
			
			/* If not, we're doing register i/o */
			else {
				if ((ioByte1 & 0x07) <= 7) {
					registers[ioByte1 & 0x07] = ioByte0;
				}
			}

			secondByteFlag = false;
		}

	}

	public final void increaseReadWriteAddr() {
		readWriteAddr++;
		if ((readWriteAddr & 0xFFFF) == 0x3FFF) readWriteAddr = 0;
	}
	
	// Port 1 read
	public final byte readStatus() {
		secondByteFlag = false;
		byte value = registers[8];
		setBit(FRAME_FLAG, false);
		setBit(C, false);
		return value;
	}

	public void initBuffer() {
		img = new BufferedImage(VDP_WIDTH, VDP_HEIGHT, BufferedImage.TYPE_INT_ARGB);
		
	}
	
	public void drawBackPlane() {
		
	}
	
	public void drawBackDrop() {
		
	}
	
	public void drawPattern() {
		//switch (getMode()) {
		//case Graphics1:
		//	break;
		//case Graphics2:
		//	break;
		//case Text:
			short nameTableAddr = this.getNameTableAddr();
			short patternTableAddr = this.getPatternTableAddr();
			int offBit = colors[getOffBitColor()];
			int onBit = colors[getOnBitColor()];
			for (int y = 0; y < 24; y++) {
				for (int x = 0; x < 40; x++) {
					int characterAddr = patternTableAddr + (mem.rdByte((short)((nameTableAddr & 0xFFFF) + (x + (40 * y)))) * 8);
					for (int charLine = 0; charLine < 8; charLine++) {
						byte line = mem.rdByte((short)((characterAddr & 0xFFFF) + charLine));
						for (int linePos = 0; linePos < 6; linePos++) {
							int px = 8 + (x * 6) + linePos;
							int py = (y * 8) + charLine;
							//System.out.println("px = " + px + " py = " + py);
							img.setRGB(px, py, Tools.getBit(line, linePos)? onBit: offBit);
						}
					}
				}
			}
			//break;
		//case MultiColor:
		//	break;
		//}
	}
	
	public void drawSprites() {
		
	}

	public void updateScreen() {
		// TODO Auto-generated method stub
		
	}

	public void paint(Graphics g) {
		count++;
		drawPattern();
		
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, VDP_WIDTH + 50, VDP_HEIGHT + 50);

		g.setColor(new Color((int)(Math.random() * 40000)));
		g.fillRect(0, 0, VDP_WIDTH + 20, VDP_HEIGHT + 20);
		g.drawImage(img, 10, 10, null);
		
		g.setColor(Color.BLACK);
		char[] text = ("Count: " + count).toCharArray();
		g.drawChars(text, 0, text.length, 50, 50);

	}
}
