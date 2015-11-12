package emu;

import java.io.IOException;
import java.util.function.Function;

import emu.memory.AbstractSlot;
import emu.memory.RAMSlot;

public class Tester {

	private static boolean stopFlag = false;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//testADD();
		//testSUB();
		//testDJNZ();
		//testJR();
		//testSUB16();
		
		testFloat();
		
		//testCopyCode();
		//testMultiplicationCode();
		//testBitOps();
		//testStack();
	}
	
	public static void testADD() {
		for (int i = -128; i < 128; i++) {
			for (int j = -128; j < 128; j++) {
				RAMSlot m = new RAMSlot();
				Z802 z80 = new Z802(m);
				m.wrtByte((short)0x0000, (byte)0x3E); // LD A, i  	0011 1110 3E
				m.wrtByte((short)0x0001, (byte)i); //          	i
				m.wrtByte((short)0x0002, (byte)0x06); // LD B, j		0000 0110 06
				m.wrtByte((short)0x0003, (byte)j); //				j
				m.wrtByte((short)0x0004, (byte)0x80); // ADD A, B		1000 0000 80
				for (int x = 0; x < 6; x++) {
					z80.execute();
				}
				int res = z80.A;
				if (((byte)res) != (byte)(((byte)i) + ((byte)j))) throw new RuntimeException("Error");
			}
		}
		System.out.println("ADD ok");
	}

	public static void testSUB16() {

		Z802 z = new Z802(new RAMSlot());

		for (int i = -1000; i <= 1000; i += 10) {
			for (int j = -1001; j <= 1001; j += 10) {
				short a = (short)i;
				short b = (short)j;
				z.setCFlag(false);
				short c = z.sub(a, b, true);
				if (c != a - b) throw new RuntimeException("sub error for " + a + " - " + b + " (is " + c + ")");
				z.setCFlag(true);
				c = z.sub(a, b, true);
				if (c != a - (b + 1)) throw new RuntimeException("sub error for " + a + " - " + b + " with carry (is " + c + ")");
			}
		}
		
		System.out.println("SUB16 OK");
		
	}
	
	public static void testSUB() {
		for (int i = -128; i < 128; i++) {
			for (int j = -128; j < 128; j++) {
				RAMSlot m = new RAMSlot();
				Z802 z80 = new Z802(m);
				m.wrtByte((short)0x0000, (byte)0x3E); // LD A, i  	0011 1110 3E
				m.wrtByte((short)0x0001, (byte)i); //          	i
				m.wrtByte((short)0x0002, (byte)0x06); // LD B, j		0000 0110 06
				m.wrtByte((short)0x0003, (byte)j); //				j
				m.wrtByte((short)0x0004, (byte)0x90); // SUB A, B		1001 0000 80
				for (int x = 0; x < 6; x++) {
					z80.execute();
				}
				byte res = z80.A;
				if (((byte)res) != (byte)(((byte)i) - ((byte)j))) throw new RuntimeException("Error");
			}
		}
		System.out.println("SUB ok");
	}

	public static void testDJNZ() {
		RAMSlot m = new RAMSlot();
		Z802 z80 = new Z802(m);
		
		m.wrtByte((short)0x0000, (byte)0x06); // LD B, 2
		m.wrtByte((short)0x0001, (byte)0x02); 
		m.wrtByte((short)0x0002, (byte)0x10); // DJNZ 0x10
		m.wrtByte((short)0x0003, (byte)0x10);
		m.wrtByte((short)0x0004, (byte)0x10); // DJNZ 0x10 
		m.wrtByte((short)0x0005, (byte)0x10);
		m.wrtByte((short)0x0006, (byte)0x10); // DJNZ 0x10 
		m.wrtByte((short)0x0007, (byte)0x10);
	
		m.wrtByte((short)0x0010, (byte)0x3C); // INC A 9
		m.wrtByte((short)0x0011, (byte)0x3C); // INC A 8
		m.wrtByte((short)0x0012, (byte)0x3C); // INC A 7
		m.wrtByte((short)0x0013, (byte)0x3C); // INC A 6
		m.wrtByte((short)0x0014, (byte)0x3C); // INC A 5
		m.wrtByte((short)0x0015, (byte)0x3C); // INC A 4
		m.wrtByte((short)0x0016, (byte)0x3C); // INC A 3
		m.wrtByte((short)0x0017, (byte)0x3C); // INC A 2
		m.wrtByte((short)0x0018, (byte)0x3C); // INC A 1

		for (int i = 0; i < 100; i++) {
			z80.execute();
		}

		// Should jump to 14 thus A should contain 5
		if (z80.A != 5) throw new RuntimeException("DJNZ error");
		System.out.println("DJNZ ok");
	}
	
	public static void testJR() {
		RAMSlot m = new RAMSlot();
		Z802 z80 = new Z802(m);
		
		m.wrtByte((short)0x0000, (byte)0x06); // LD B, 2
		m.wrtByte((short)0x0001, (byte)0x02); 
		m.wrtByte((short)0x0002, (byte)0x18); // JR 0x10
		m.wrtByte((short)0x0003, (byte)0x10);
		m.wrtByte((short)0x0004, (byte)0x18); // JR 0x10 
		m.wrtByte((short)0x0005, (byte)0x10);
	
		m.wrtByte((short)0x0010, (byte)0x3C); // INC A 9
		m.wrtByte((short)0x0011, (byte)0x3C); // INC A 8
		m.wrtByte((short)0x0012, (byte)0x3C); // INC A 7
		m.wrtByte((short)0x0013, (byte)0x3C); // INC A 6
		m.wrtByte((short)0x0014, (byte)0x3C); // INC A 5
		m.wrtByte((short)0x0015, (byte)0x3C); // INC A 4
		m.wrtByte((short)0x0016, (byte)0x3C); // INC A 3
		m.wrtByte((short)0x0017, (byte)0x3C); // INC A 2
		m.wrtByte((short)0x0018, (byte)0x3C); // INC A 1

		for (int i = 0; i < 100; i++) {
			z80.execute();
		}

		// Should jump to 14 thus A should contain 7
		if (z80.A != 7) throw new RuntimeException("JR error");
		System.out.println("JR ok");
	
	}	
	
	public static void testLD() {
		
	}
	
	public static void testFloat() {
		RAMSlot m = new RAMSlot();
		Z802 z80 = new Z802(m);
		try {
			m.load("/MSX.rom", (short)0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		z80.registerOutDevice(new Z80OutDevice() {

			@Override
			public void out(byte port, byte value) {
				System.out.print((char)value);
				//System.out.println((int)value & 0xff);
				if (Tools.toHexString(value).equals("0a")) {
					//z80.printState();
				}
			}
			
		}, 0);
		System.out.println("Start");
		while (!z80.isHalted()) {
			z80.execute();
			//z80.printDebugMessage();
			z80.printState();
		}
	}
	
	public static void testCopyCode() {
		// 1000                        org     1000h       ;Origin at 1000h
		// 1000            memcpy      public
		// 1000 78         loop        ld      a,b         ;Test BC,
		// 1001 B1                     or      c           ;If BC = 0,
		// 1002 C8                     retz                ;Return
		// 1003 1A                     ld      a,(de)      ;Load A from (DE)
		// 1004 77                     ld      (hl),a      ;Store A into (HL)
		// 1005 13                     inc     de          ;Increment DE
		// 1006 23                     inc     hl          ;Increment HL
		// 1007 0B                     dec     bc          ;Decrement BC
		// 1008 C3 00 10               jp      loop        ;Repeat the loop
		// 100B                        end 
		RAMSlot m = new RAMSlot();
		Z802 z80 = new Z802(m);
		
		// Write code
		m.wrtByte((short)0x1000, (byte)0x78);
		m.wrtByte((short)0x1001, (byte)0xB1);
		m.wrtByte((short)0x1002, (byte)0xC8);
		m.wrtByte((short)0x1003, (byte)0x1A);
		m.wrtByte((short)0x1004, (byte)0x77);
		m.wrtByte((short)0x1005, (byte)0x13);
		m.wrtByte((short)0x1006, (byte)0x23);
		m.wrtByte((short)0x1007, (byte)0x0B);
		m.wrtByte((short)0x1008, (byte)0xC3);
		m.wrtByte((short)0x1009, (byte)0x00);
		m.wrtByte((short)0x100A, (byte)0x10);

		m.wrtByte((short)0x0000, (byte)0x3E); // LD A, FF
		m.wrtByte((short)0x0001, (byte)0xFF); 
		m.wrtByte((short)0x0002, (byte)0xD3); // OUT 99, A
		m.wrtByte((short)0x0003, (byte)0xFF);
		
		// Write data
		m.wrtByte((short)0x2000, (byte)0xFF);
		m.wrtByte((short)0x2001, (byte)0xEE);
		m.wrtByte((short)0x2002, (byte)0xDD);
		m.wrtByte((short)0x2003, (byte)0xCC);
		m.wrtByte((short)0x2004, (byte)0xBB);
		m.wrtByte((short)0x2005, (byte)0xAA);
		m.wrtByte((short)0x2006, (byte)0x99);
		m.wrtByte((short)0x2007, (byte)0x88);
		m.wrtByte((short)0x2008, (byte)0x77);
		m.wrtByte((short)0x2009, (byte)0x66);
		m.wrtByte((short)0x200A, (byte)0x55);
		m.wrtByte((short)0x200B, (byte)0x44);
		m.wrtByte((short)0x200C, (byte)0x33);
		m.wrtByte((short)0x200D, (byte)0x22);
		m.wrtByte((short)0x200E, (byte)0x11);
		m.wrtByte((short)0x200F, (byte)0x00);
		
		// Setup
		z80.setBC((short)5); // Bytes to copy
		z80.setDE((short)0x2000); // Source
		z80.setHL((short)0x3000); // Destination
		
		// Stop mechanism
		stopFlag = false;
		z80.registerOutDevice(new Z80OutDevice() {
			@Override
			public void out(byte port, byte value) {
				if (value == (byte)0xFF) {
					stopFlag = true;
				}
			}
		}, 0xFF);
		
		// Run
		z80.PC = (short)0x1000;
		while (!stopFlag) {
			z80.execute();
		}
		
		// Test
		if (m.rdByte((short)0x3000) != (byte)0xFF) throw new RuntimeException();
		if (m.rdByte((short)0x3001) != (byte)0xEE) throw new RuntimeException();
		if (m.rdByte((short)0x3002) != (byte)0xDD) throw new RuntimeException();
		if (m.rdByte((short)0x3003) != (byte)0xCC) throw new RuntimeException();
		if (m.rdByte((short)0x3004) != (byte)0xBB) throw new RuntimeException();
		if (m.rdByte((short)0x3005) != (byte)0) throw new RuntimeException();
		
		System.out.println("Copy code OK");
	}
	
	public static void testMultiplicationCode() {
		

		// DE_Times_A:           ; HL = DE × A
		//     LD     HL, 0      ; Use HL to store the product
		//     LD     B, 8       ; Eight bits to check
		// _loop:
		//     RRCA              ; Check least-significant bit of accumulator
		//     JR     NC, _skip  ; If zero, skip addition							XXX 
		//     ADD    HL, DE
		// _skip:
		//     SLA    E          ; Shift DE one bit left							XXX
		//     RL     D																XXX
		//		    DJNZ   _loop
		//			RET
		
		//		21 00 00 	LD HL, 0
		//		06 08 		LD B, 8
		//		Loop:
		//		0F			RRCA
		//		30 01 		JR     NC, _skip
		//		19			ADD HL, DE 
		//		Skip:
		//		CB 23		SLA E 
		//		CB 12		RL D 
		//		10 F6 		DJNZ _loop
		//		C9			RET
		
		RAMSlot m = new RAMSlot();
		Z802 z80 = new Z802(m);
		z80.setSP((short)0xE000);
		
		m.wrtByte((short)0x1000, (byte)0x21);
		m.wrtByte((short)0x1001, (byte)0x00);
		m.wrtByte((short)0x1002, (byte)0x00);
		m.wrtByte((short)0x1003, (byte)0x06);
		m.wrtByte((short)0x1004, (byte)0x08);
		m.wrtByte((short)0x1005, (byte)0x0F);
		m.wrtByte((short)0x1006, (byte)0x30);
		m.wrtByte((short)0x1007, (byte)0x01);
		m.wrtByte((short)0x1008, (byte)0x19);
		m.wrtByte((short)0x1009, (byte)0xCB);
		m.wrtByte((short)0x100A, (byte)0x23);
		m.wrtByte((short)0x100B, (byte)0xCB);
		m.wrtByte((short)0x100C, (byte)0x12);
		m.wrtByte((short)0x100D, (byte)0x10);
		m.wrtByte((short)0x100E, (byte)0xF6);
		m.wrtByte((short)0x100F, (byte)0xC9);

		m.wrtByte((short)0x0000, (byte)0x3E); // LD A, FF
		m.wrtByte((short)0x0001, (byte)0xFF); 
		m.wrtByte((short)0x0002, (byte)0xD3); // OUT 99, A
		m.wrtByte((short)0x0003, (byte)0xFF);

		// Stopping mechanism
		z80.registerOutDevice(new Z80OutDevice() {
			@Override
			public void out(byte port, byte value) {
				if (value == (byte)0xFF) {
					stopFlag = true;
				}
			}
		}, 0xFF);

		// Test
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				if (i == 2 && j == 0) {
					System.out.println("test");
				}
				// Setup
				z80.setAF((short)0);
				z80.A = (byte)i; 
				z80.setDE((short)j);
				z80.setHL((short)0);
				z80.setSP((short)0xE000);
				stopFlag = false;
				
				// Run
				z80.PC = (short)0x1000;
				while (!stopFlag) {
					z80.printState();
					z80.execute();
				}

				int result = z80.getHL();
				if (result != i * j) {
					throw new RuntimeException("Multiplication code error (i="+i+",j="+j+",result="+result+")");
				}
			}
		}
		
		System.out.println("Multiplication code OK");
	}
	
	public static void testBitOps() {
		
		Z802 z = new Z802(new RAMSlot());
		byte val;
		
		// Set bit
		if (z.setBit(0, (byte)0b10101010, true)  != (byte)0b10101011) throw new RuntimeException("Error");
		if (z.setBit(1, (byte)0b10101010, false) != (byte)0b10101000) throw new RuntimeException("Error");
		if (z.setBit(7, (byte)0b10101010, false) != (byte)0b00101010) throw new RuntimeException("Error");

		// Read bit
		z.readBit(0, (byte)0b00100101); if (z.getZFlag() != true) throw new RuntimeException("Error");
		z.readBit(1, (byte)0b00100101); if (z.getZFlag() != false) throw new RuntimeException("Error");
		z.readBit(7, (byte)0b00100101); if (z.getZFlag() != false) throw new RuntimeException("Error");
		
		// RL
		z.setCFlag(true);
		val = z.doRL((byte)0b00001000);
		if (z.getCFlag() != false || val != (byte)0b00010001) throw new RuntimeException("Error");
		z.setCFlag(false);
		val = z.doRL((byte)0b10001000);
		if (z.getCFlag() != true || val != (byte)0b00010000) throw new RuntimeException("Error");
			
		// RLC
		z.setCFlag(true);
		val = z.doRLC((byte)0b00001000);
		if (z.getCFlag() != false || val != (byte)0b00010000) throw new RuntimeException("Error");
		z.setCFlag(false);
		val = z.doRLC((byte)0b10001000);
		if (z.getCFlag() != true || val != (byte)0b00010001) throw new RuntimeException("Error");

		// RR
		z.setCFlag(true);
		val = z.doRR((byte)0b10001000);
		if (z.getCFlag() != false || val != (byte)0b11000100) throw new RuntimeException("Error");
		z.setCFlag(false);
		val = z.doRR((byte)0b00010001);
		if (z.getCFlag() != true || val != (byte)0b00001000) throw new RuntimeException("Error");

		// RRC
		z.setCFlag(true);
		val = z.doRRC((byte)0b10001000);
		if (z.getCFlag() != false || val != (byte)0b01000100) throw new RuntimeException("Error");
		z.setCFlag(false);
		val = z.doRRC((byte)0b00010001);
		if (z.getCFlag() != true || val != (byte)0b10001000) throw new RuntimeException("Error");
		
		// SLA
		z.setCFlag(false);
		val = z.doSLA((byte)0b10001000);
		if (z.getCFlag() != true || val != (byte)0b00010000) throw new RuntimeException("Error");
		z.setCFlag(true);
		val = z.doSLA((byte)0b00010001);
		if (z.getCFlag() != false || val != (byte)0b00100010) throw new RuntimeException("Error");

		// SLL
		z.setCFlag(false);
		val = z.doSLL((byte)0b10001000);
		if (z.getCFlag() != true || val != (byte)0b00010001) throw new RuntimeException("Error");
		z.setCFlag(true);
		val = z.doSLL((byte)0b00010001);
		if (z.getCFlag() != false || val != (byte)0b00100011) throw new RuntimeException("Error");
		
		// SRA
		z.setCFlag(true);
		val = z.doSRA((byte)0b10001000);
		if (z.getCFlag() != false || val != (byte)0b11000100) throw new RuntimeException("Error");
		z.setCFlag(false);
		val = z.doSRA((byte)0b00010001);
		if (z.getCFlag() != true || val != (byte)0b00001000) throw new RuntimeException("Error");
		
		// SRL
		z.setCFlag(true);
		val = z.doSRL((byte)0b10001000);
		if (z.getCFlag() != false || val != (byte)0b01000100) throw new RuntimeException("Error");
		z.setCFlag(false);
		val = z.doSRL((byte)0b00010001);
		if (z.getCFlag() != true || val != (byte)0b00001000) throw new RuntimeException("Error");

		System.out.println("All bitops ok");
	}
	
	public static void testStack() {
		
		Z802 z = new Z802(new RAMSlot());
		z.pushSP((short)5000);
		z.pushSP((short)5001);
		z.pushSP((short)6000);
		if (z.popSP() != 6000) throw new RuntimeException();
		if (z.popSP() != 5001) throw new RuntimeException();
		if (z.popSP() != 5000) throw new RuntimeException();

		System.out.println("Push and pop ok");

	}
}
