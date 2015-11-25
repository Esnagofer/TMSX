package emu;

import java.util.ArrayList;

import emu.memory.AbstractSlot;

public class Z802 {

	boolean eff1 = false, eff2 = false;

	boolean halt = false;

	/* Program counter */
	short PC = 0, oldPC = 0;

	/* Registers */
	byte A, B, C, D, E, F, H, L, S, P, IXH, IXL, IYH, IYL; // Registers

	/* Register copies */
	byte AP, BP, CP, DP, EP, FP, HP, LP, FB, AB;

	/* Interrupt control and Memory refresh registers */
	byte IC, RR;

	/* Temporary variables */
	short ts = 0, ts2 = 0;
	byte bs = 0, dis = 0;

	/* Debug message */
	private final int stateBufSize = 1000;
	private String[] stateBuf = new String[stateBufSize];
	private int stateBufPtr = 0;
	private boolean stateBufEnable = true;
	
	/* Cycle counter */
	int s;

	/* Memory instance */
	private final AbstractSlot mem;

	private final Z80OutDevice[] out = new Z80OutDevice[256];
	private final Z80InDevice[] in = new Z80InDevice[256];

	short one = (short) 1;

	private int interruptDelayCount = 0;

	private String lastMsg;

	private static final short INTERRUPT_HOOK = 0x0038;
	
	public Z802(AbstractSlot mem) {
		this.mem = mem;
		reset();
	}
	
	public void reset() {
		/* Initialize flags and registers */
		eff1 = false;
		eff2 = false;
		PC = (short)0x0000;
		setAF((short)0xFFFF);
		setBC((short)0xFFFF);
		setDE((short)0xFFFF);
		setHL((short)0xFFFF);
		setIX((short)0xFFFF);
		setIY((short)0xFFFF);
		AP = (byte)0xFF;
		BP = (byte)0xFF;
		CP = (byte)0xFF;
		DP = (byte)0xFF;
		FP = (byte)0xFF;
		HP = (byte)0xFF;
		LP = (byte)0xFF;
		FB = (byte)0xFF;
		AB = (byte)0xFF;
		s = 0;
	}

	public void execute() {
		if (interruptDelayCount > 0) interruptDelayCount--;
		executeRegular();
		stateBuf[stateBufPtr] = getState();
		stateBufPtr = (stateBufPtr + 1) % stateBufSize;
	}

	public void dbg(String msg) {
		lastMsg = msg;
	}

	public String getLastMsg() {
		return lastMsg;
	}
	
	public void executeRegular() {
		oldPC = PC;
		//System.out.println("PC = " + Tools.toHexString(PC));
		byte b = fetchByte();
		if ((oldPC & 0xFFFF) == 0xFFFF) {
			if (PC == 0) {
				throw new RuntimeException("PC wrap around");
			}
		}
		switch (b) {
		case 0x00:	// NOP 4
			s += 4; 
			dbg("NOP");
			break;
		case 0x01:	// LD BC,&HHLL 10
			s += 10; 
			ts = fetchWordLH();
			setBC(ts);
			dbg("LD BC, & HHLL : " + Tools.toHexString(ts));
			break;
		case 0x02:	// LD (BC),A 7
			s += 7; 
			mem.wrtByte(getBC(), A);
			dbg("LD (BC), A : BC = " + Tools.toHexString(getBC()) + ", A = " + Tools.toHexString(A));
			break;
		case 0x03:	// INC BC 6
			s += 6; 
			setBC(inc(getBC()));
			dbg("INC BC");
			break;
		case 0x04:	// INC B 4
			s += 4; 
			B = inc(B);
			dbg("INC B");
			break;
		case 0x05:	// SUB B 4
			s += 4; 
			B = sub(B, (byte) 1, false);
			dbg("SUB B");
			break;
		case 0x06:	// LD B,&NN 7
			s += 7; 
			B = fetchByte();
			dbg("LD B, &NN (&NN = " + B + ")");
			break;
		case 0x07:	// RLCA 4
			s += 4;
			doRLCA();
			dbg("RLCA");
			break; 
		case 0x08:	// EX AF,AF' 4
			s += 4;
			byte AX = A, FX = F;
			A = AP;
			F = FP;
			AP = AX;
			FP = FX;
			dbg("EX AF, AF'");
			break; 
		case 0x09:	// ADD HL,BC 11
			s += 11;
			setHL(add(getHL(), getBC(), false));
			dbg("ADD HL, BC");
			break; 
		case 0x0A:	// LD A,(BC) 7
			s += 7;
			A = mem.rdByte(getBC());
			dbg("LD A, (BC) : " + Tools.toHexString(getBC()));
			break; 
		case 0x0B:	// DEC BC 6
			s += 6;
			setBC(dec(getBC()));
			dbg("DEC BC");
			break; 
		case 0x0C:	// INC C 4
			s += 4;
			C = inc(C);
			dbg("INC C");
			break; 
		case 0x0D:	// DEC C 4
			s += 4;
			C = dec(C);
			dbg("DEC C");
			break; 
		case 0x0E:	// LD C,&NN 7
			s += 7;
			C = fetchByte();
			dbg("LD C,&NN");
			break; 
		case 0x0F:	// RRCA 4
			s += 4;
			dbg("RRCA");
			doRRCA();
			break; 
		case 0x10:	// DJNZ &NN 13,8
			s += 8;
			bs = fetchByte();
			B--;
			if (B != 0) {
				PC = (short) (PC + bs);
				s += 5;
			}
			dbg("DJNZ &NN : &NN = " + bs + " NZ = " + (B != 0) + " PC = " + PC);
			break; 
		case 0x11:	// LD DE,&HHLL 10
			s += 10;
			setDE(fetchWordLH());
			dbg("LD DE, &HHLL : " + Tools.toHexString(getDE()));
			break; 
		case 0x12:	// LD (DE),A 7
			s += 7;
			mem.wrtByte(getDE(), A);
			dbg("LD (DE), A");
			break; 
		case 0x13:	// INC DE 6
			s += 6;
			setDE(inc(getDE()));
			dbg("INC DE");
			break; 
		case 0x14:	// INC D 4
			s += 4;
			D = inc(D);
			dbg ("INC D");
			break; 
		case 0x15:	// DEC D 4
			s += 4;
			D = dec(D);
			dbg("DEC D");
			break; 
		case 0x16:	// LD D,&NN 7
			s += 7;
			D = fetchByte();
			dbg("LD D, &NN");
			break; 
		case 0x17:	// RLA 4
			s += 4;
			doRLA();
			dbg("RLA");
			break; 
		case 0x18:	// JR &NN 12
			s += 12;
			bs = fetchByte();
			PC = (short) (PC + bs);
			dbg("JR &NN (" + Tools.toHexString(bs) + ")");
			break; 
		case 0x19:	// ADD HL,DE 11
			s += 11;
			setHL(add(getHL(), getDE(), false));
			dbg("ADD HL, DE");
			break; 
		case 0x1A:	// LD A,(DE) 7
			s += 7;
			A = mem.rdByte(getDE());
			dbg("LD A, (DE)");
			break; 
		case 0x1B:	// DEC DE 6
			s += 6;
			setDE(dec(getDE()));
			dbg("DEC DE");
			break; 
		case 0x1C:	// INC E 4
			s += 4;
			E = inc(E);
			dbg("INC E");
			break; 
		case 0x1D:	// DEC E 4
			s += 4;
			E = dec(E);
			dbg("DEC E");
			break; 
		case 0x1E:	// LD E,&NN 7
			s += 7;
			E = fetchByte();
			dbg("LD E, &NN");
			break; 
		case 0x1F:	// RRA 4
			s += 4;
			doRRA();
			dbg("RRA");
			break; 
		case 0x20:	// JR NZ,&NN 12,7
			s += 7;
			bs = fetchByte();
			if (!getZFlag()) {
				PC = (short) (PC + bs);
				s += 5;
			}
			dbg("JR NZ,&NN (" + bs + ")");
			break;
		case 0x21:	// LD HL,&HHLL 10
			s += 10;
			setHL(fetchWordLH());
			dbg("LD HL, &HHLL");
			break; 
		case 0x22:	// LD (&HHLL),HL 16
			s += 16;
			ts = fetchWordLH();
			mem.writeShortLH(ts, getHL());
			dbg("LD (&HHLL), HL (" + Tools.toHexString(ts) + ")");
			break; 
		case 0x23:	// INC HL 6
			s += 6;
			setHL(inc(getHL()));
			dbg("INC HL");
			break; 
		case 0x24:	// INC H 4
			s += 4;
			H = add(H, (byte) 1, false);
			dbg("INC H");
			break; 
		case 0x25:	// DEC H 4
			s += 4;
			H = dec(H);
			dbg("DEC H");
			break; 
		case 0x26:	// LD H,&NN 7
			s += 7;
			H = fetchByte();
			dbg("LD H, &NN");
			break; 
		case 0x27:	// DAA 4
			s += 4;
			doDAA();
			dbg("DAA");
			break; 	
		case 0x28:	// JR Z,&NN 12,7
			s += 7;
			bs = fetchByte(); 
			if (getZFlag()) {
				PC = (short) (PC + bs);
				s += 5;
			}
			dbg("JR Z, &NN (" + Tools.toHexString(bs) + ")");
			break;
		case 0x29:	// ADD HL,HL 11
			s += 11;
			setHL(add(getHL(), getHL(), false));
			dbg("ADD HL, HL");
			break;
		case 0x2A:	// LD HL,(&HHLL) 16
			s += 16;
			ts = fetchWordLH();
			setHL(mem.readWordLH(ts));
			dbg("LD HL, (&HHLL) (" + Tools.toHexString(ts) + ")");
			break; 
		case 0x2B:	// DEC HL 6
			s += 6;
			setHL(dec(getHL()));
			dbg("DEC HL");
			break; 
		case 0x2C:	// INC L 4
			s += 4;
			L = inc(L);
			dbg("INC L");
			break; 
		case 0x2D:	// DEC L 4
			s += 4;
			L = dec(L);
			dbg("DEC L");
			break; 
		case 0x2E:	// LD L,&NN 7
			s += 7;
			L = fetchByte();
			dbg("LD, L, &NN : " + Tools.toHexString(L));
			break; 
		case 0x2F:	// CPL 4
			s += 4;
			A = (byte) ~A;
			setHFlag(true);
			setNFlag(true);
			dbg("CPL");
			break; 
		case 0x30:	// JR NC,&NN 12,7
			s += 7;
			bs = fetchByte(); 
			if (!getCFlag()) {
				PC = (short) (PC + bs);
				s += 5;
			}
			dbg("JR NC, &NN (" + Tools.toHexString(bs) + ")");
			break;
		case 0x31:	// LD SP,&HHLL 10
			s += 10;
			setSP(fetchWordLH());
			dbg("LD SP, &HHLL : " + Tools.toHexString(getSP()));
			break; 
		case 0x32:	// LD (&HHLL), A 16
			s += 16;
			ts = fetchWordLH();
			mem.wrtByte(ts, A);
			dbg("LD (&HHLL), A (" + Tools.toHexString(ts) + ")");
			break; 
		case 0x33:	// INC SP 6
			s += 6;
			setSP(inc(getSP()));
			dbg("INC SP");
			break; 
		case 0x34:	// INC (HL) 11
			s += 11;
			mem.wrtByte(getHL(), inc(mem.rdByte(getHL())));
			dbg("INC (HL)");
			break; 
		case 0x35:	// DEC (HL) 11
			s += 11;
			mem.wrtByte(getHL(), dec(mem.rdByte(getHL())));
			dbg("DEC (HL)");
			break; 
		case 0x36:	// LD (HL),&NN 10
			s += 10;
			bs = fetchByte();
			mem.wrtByte(getHL(), bs);
			dbg("LD (HL), &NN (" + Tools.toHexString(bs) + ")");
			break; 
		case 0x37:	// SCF 4
			s += 4;
			setCFlag(true);
			setHFlag(false);
			setNFlag(false);
			dbg("SCF");
			break; 
		case 0x38:	// JR C,&NN 12,7
			s += 7;
			bs = fetchByte(); 
			if (getCFlag()) {
				PC = (short) (PC + bs);
				s += 5;
			}
			dbg("JR C, &NN (" + Tools.toHexString(bs) + ")");
			break;
		case 0x39:	// ADD HL,SP 11
			s += 11;
			setHL(add(getHL(), getSP(), false));
			dbg("ADD HL, SP");
			break; 
		case 0x3A:	// LD A,(&HHLL) 13
			s += 13;
			A = mem.rdByte(fetchWordLH());
			dbg("LD A,(&HHLL)");
			break; 
		case 0x3B:	// DEC SP 6
			s += 6;
			setSP(dec(getSP()));
			dbg("DEC SP");
			break; 
		case 0x3C:	// INC A 4
			s += 4;
			A = inc(A);
			dbg("INC A");
			break; 
		case 0x3D:	// DEC A 4
			s += 4;
			A = dec(A);
			dbg("DEC A");
			break; 
		case 0x3E:	// LD A,&NN 7
			s += 7;
			A = fetchByte();
			dbg("LD A, &NN");
			break; 	
		case 0x3F:	// CCF 4
			s += 4;
			setCFlag(!getCFlag());
			setHFlag(!getHFlag());
			setNFlag(false);
			dbg("CCF");
			break; 
		case 0x40:	// LD B,B 4
			s += 4;
			dbg("LD B, B");
			break; 
		case 0x41:	// LD B,C 4
			s += 4;
			B = C;
			dbg("LD B, C");
			break; 
		case 0x42:	// LD B,D 4
			s += 4;
			B = D;
			dbg("LD B, D");
			break; 
		case 0x43:	// LD B,E 4
			s += 4;
			B = E;
			dbg("LD B, E : " + Tools.toHexString(B));
			break; 
		case 0x44:	// LD B,H 4
			s += 4;
			B = H;
			dbg("LD B, H : " + Tools.toHexString(B));
			break; 
		case 0x45:	// LD B,L 4
			s += 4;
			B = L;
			dbg("LD B, L : " + Tools.toHexString(B));
			break; 
		case 0x46:	// LD B,(HL) 7
			s += 7;
			B = mem.rdByte(getHL());
			dbg("LD B, (HL) : " + Tools.toHexString(B) + " HL = " + Tools.toHexString(getHL()));
			break; 
		case 0x47:	// LD B,A 4
			s += 4;
			B = A;
			dbg("LD B, A : " + Tools.toHexString(B));
			break; 

		case 0x48:	// LD C,B 4
			s += 4;
			C = B;
			dbg("LD C, B : " + Tools.toHexString(C));
			break; 
		case 0x49:	// LD C,C 4
			s += 4;
			dbg("LD C, C : " + Tools.toHexString(C));
			break; 
		case 0x4A:	 // LD C,D 4
			s += 4;
			C = D;
			dbg("LD C, D : " + Tools.toHexString(C));
			break;
		case 0x4B:	// LD C,E 4
			s += 4;
			C = E;
			dbg("LD C, E : " + Tools.toHexString(C));
			break; 
		case 0x4C:	// LD C,H 4
			s += 4;
			C = H;
			dbg("LD C, H : " + Tools.toHexString(C));
			break; 
		case 0x4D:	// LD C,L 4
			s += 4;
			C = L;
			dbg("LD C, L : " + Tools.toHexString(C));
			break; 
		case 0x4E:	// LD C,(HL) 7
			s += 7;
			C = mem.rdByte(getHL());
			dbg("LD C, (HL) : " + Tools.toHexString(C) + " HL = " + Tools.toHexString(getHL()));
			break; 
		case 0x4F:	// LD C,A 4
			s += 4;
			C = A;
			dbg("LD C, A : " + Tools.toHexString(C));
			break; 

		case 0x50:	// LD D,B 4
			s += 4;
			D = B;
			dbg("LD D, B : " + Tools.toHexString(D));
			break; 
		case 0x51:	// LD D,C 4
			s += 4;
			D = C;
			dbg("LD D, C : " + Tools.toHexString(D));
			break; 
		case 0x52:	// LD D,D 4
			s += 4;
			dbg("LD D, D : " + Tools.toHexString(D));
			break; 
		case 0x53:	// LD D,E 4
			s += 4;
			D = E;
			dbg("LD D, E : " + Tools.toHexString(D));
			break; 
		case 0x54:	// LD D,H 4
			s += 4;
			D = H;
			dbg("LD D, H : " + Tools.toHexString(D));
			break; 
		case 0x55:	// LD D,L 4
			s += 4;
			D = L;
			dbg("LD D, L : " + Tools.toHexString(D));
			break; 
		case 0x56:	// LD D,(HL) 7
			s += 7;
			D = mem.rdByte(getHL());
			dbg("LD D, (HL) : " + Tools.toHexString(D) + " HL = " + Tools.toHexString(getHL()));
			break; 
		case 0x57:	// LD D,A 4
			s += 4;
			D = A;
			dbg("LD D, A : " + Tools.toHexString(D));
			break; 

		case 0x58:	// LD E,B 4
			s += 4;
			E = B;
			dbg("LD E, B : " + Tools.toHexString(E));
			break; 
		case 0x59:	// LD E,C 4
			s += 4;
			E = C;
			dbg("LD E, C : " + Tools.toHexString(E));
			break; 
		case 0x5A:	// LD E,D 4
			s += 4;
			E = D;
			dbg("LD E, D : " + Tools.toHexString(E));
			break; 
		case 0x5B:	// LD E,E 4
			s += 4;
			dbg("LD E, E : " + Tools.toHexString(E));
		break; 
		case 0x5C:	// LD E,H 4
			s += 4;
			E = H;
			dbg("LD E, H : " + Tools.toHexString(E));
			break;
		case 0x5D:	// LD E,L 4
			s += 4;
			E = L;
			dbg("LD E, L : " + Tools.toHexString(E));
			break; 
		case 0x5E:	// LD E,(HL) 7
			s += 7;
			E = mem.rdByte(getHL());
			dbg("LD E, (HL) : " + Tools.toHexString(E) + " HL = " + Tools.toHexString(getHL()));
			break; 
		case 0x5F:	// LD E,A 4
			s += 4;
			E = A;
			dbg("LD E, A : " + Tools.toHexString(E));
			break; 

		case 0x60:	// LD H,B 4
			s += 4;
			H = B;
			dbg("LD H, B : " + Tools.toHexString(H));
			break; 
		case 0x61:	// LD H,C 4
			s += 4;
			H = C;
			dbg("LD H, C : " + Tools.toHexString(H));
			break; 	
		case 0x62:	// LD H,D 4
			s += 4;
			H = D;
			dbg("LD H, D : " + Tools.toHexString(H));
			break; 
		case 0x63:	// LD H,E 4
			s += 4;
			H = E;
			dbg("LD H, E : " + Tools.toHexString(H));
			break; 
		case 0x64:	// LD H,H 4
			s += 4;
			dbg("LD H, H : " + Tools.toHexString(H));
			break; 
		case 0x65:	// LD H,L 4
			s += 4;
			H = L;
			dbg("LD H, L : " + Tools.toHexString(H));
			break; 
		case 0x66:	// LD H,(HL) 7
			s += 7;
			H = mem.rdByte(getHL());
			dbg("LD H, (HL) : " + Tools.toHexString(H) + " HL = " + Tools.toHexString(getHL()));
			break; 
		case 0x67:	// LD H,A 4
			s += 4;
			H = A;
			dbg("LD H, A : " + Tools.toHexString(H));
			break; 

		case 0x68:	// LD L,B 4
			s += 4;
			L = B;
			dbg("LD L, B : " + Tools.toHexString(L));
			break; 
		case 0x69:	// LD L,C 4
			s += 4;
			L = C;
			dbg("LD L, C : " + Tools.toHexString(L));
			break; 
		case 0x6A:	// LD L,D 4
			s += 4;
			L = D;
			dbg("LD L, D : " + Tools.toHexString(L));
			break; 
		case 0x6B:	// LD L,E 4
			s += 4;
			L = E;
			dbg("LD L, E : " + Tools.toHexString(L));
			break; 
		case 0x6C:	// LD L,H 4
			s += 4;
			L = H;
			dbg("LD L, H : " + Tools.toHexString(L));
			break; 
		case 0x6D:	// LD L,L 4
			s += 4;
			dbg("LD L, L : " + Tools.toHexString(L));
			break; 
		case 0x6E:	// LD L,(HL) 7
			s += 7;
			L = mem.rdByte(getHL());
			dbg("LD L, (HL) : " + Tools.toHexString(L) + ", HL = " + Tools.toHexString(getHL()));
			break; 
		case 0x6F:	// LD L,A 4
			s += 4;
			L = A;
			dbg("LD L, A : " + Tools.toHexString(L));
			break; 

		case 0x70:	// LD (HL),B 7
			s += 7;
			s += 4;
			mem.wrtByte(getHL(), B);
			dbg("LD (HL), B");
			break; 
		case 0x71:	// LD (HL),C 7
			s += 7;
			mem.wrtByte(getHL(), C);
			dbg("LD (HL), C");
			break; 
		case 0x72:	// LD (HL),D 7
			s += 7;
			mem.wrtByte(getHL(), D);
			dbg("LD (HL), D");
			break; 
		case 0x73:	// LD (HL),E 7
			s += 7;
			mem.wrtByte(getHL(), E);
			dbg("LD (HL), E");
			break; 
		case 0x74:	// LD (HL),H 7
			s += 7;
			mem.wrtByte(getHL(), H);
			dbg("LD (HL), H");
			break; 
		case 0x75:	// LD (HL),L 7
			s += 7;
			mem.wrtByte(getHL(), L);
			dbg("LD (HL), L");
			break; 
		case 0x76:	// HALT 4
			s += 4;
			halt = true;
			dbg("HALT");
			break; 
		case 0x77:	// LD (HL),A 7
			s += 7;
			mem.wrtByte(getHL(), A);
			dbg("LD (HL), A");
			break; 
		case 0x78:	// LD A,B 4
			s += 4;
			A = B;
			dbg("LD A, B");
			break; 
		case 0x79:	// LD A,C 4
			s += 4;
			A = C;
			dbg("LD A, C");
			break; 	
		case 0x7A:	// LD A,D 4
			s += 4;
			A = D;
			dbg("LD A, D");
			break; 
		case 0x7B:	// LD A,E 4
			s += 4;
			A = E;
			dbg("LD A, E");
			break; 
		case 0x7C:	// LD A,H 4
			s += 4;
			A = H;
			dbg("LD A, H");
			break; 
		case 0x7D:	// LD A,L 4
			s += 4;
			A = L;
			dbg("LD A, L");
			break; 
		case 0x7E:	// LD A,(HL) 7
			s += 7;
			A = mem.rdByte(getHL());
			dbg("LD A, (HL)");
			break; 
		case 0x7F:	// LD A,A 4
			s += 4;
			dbg("LD A, A");
			break; 
		case (byte) 0x80:	// ADD A,B 4
			s += 4;
			A = add(A, B, false);
			dbg("ADD A, B");
			break; 
		case (byte) 0x81:	// ADD A,C 4
			s += 4;
			A = add(A, C, false);
			dbg("ADD A, C");
			break; 
		case (byte) 0x82:	// ADD A,D 4
			s += 4;
			A = add(A, D, false);
			dbg("ADD A, D");
			break; 
		case (byte) 0x83:	// ADD A,E 4
			s += 4;
			A = add(A, E, false);
			dbg("ADD A, E");
			break; 
		case (byte) 0x84:	// ADD A,H 4
			s += 4;
			A = add(A, H, false);
			dbg("ADD A, H");
			break; 
		case (byte) 0x85:	// ADD A,L 4
			s += 4;
			A = add(A, L, false);
			dbg("ADD A, L");
			break; 
		case (byte) 0x86:	// ADD A,(HL) 7
			s += 7;
			A = add(A, mem.rdByte(getHL()), false);
			dbg("ADD A, (HL)");
			break; 
		case (byte) 0x87:	// ADD A,A 4
			s += 4;
			A = add(A, A, false);
			dbg("ADD A, A");
			break; 

		case (byte) 0x88:	// ADC A,B 4
			s += 4;
			A = add(A, B, true);
			dbg("ADC A, B");
			break; 
		case (byte) 0x89:	// ADC A,C 4
			s += 4;
			A = add(A, C, true);
			dbg("ADC A, C");
			break; 
		case (byte) 0x8A:	// ADC A,D 4
			s += 4;
			A = add(A, D, true);
			dbg("ADC A, D");
			break; 
		case (byte) 0x8B:	// ADC A,E 4
			s += 4;
			A = add(A, E, true);
			dbg("ADC A, E");
			break; 
		case (byte) 0x8C:	// ADC A,H 4
			s += 4;
			A = add(A, H, true);
			dbg("ADC A, H");
			break; 
		case (byte) 0x8D:	// ADC A,L 4
			s += 4;
			A = add(A, L, true);
			dbg("ADC A, L");
			break; 
		case (byte) 0x8E:	// ADC A,(HL) 7
			s += 7;
			A = add(A, mem.rdByte(getHL()), true);
			dbg("ADC A, (HL)");
			break; 
		case (byte) 0x8F:	// ADC A,A 4
			s += 4;
			A = add(A, A, true);
			dbg("ADC A, A");
			break; 

		case (byte) 0x90:	// SUB A,B 4
			s += 4;
			A = sub(A, B, false);
			dbg("SUB A, B");
			break; 
		case (byte) 0x91:	// SUB A,C 4
			s += 4;
			A = sub(A, C, false);
			dbg("SUB A, C");
			break; 
		case (byte) 0x92:	// SUB A,D 4
			s += 4;
			A = sub(A, D, false);
			dbg("SUB A, D");
			break; 
		case (byte) 0x93:	// SUB A,E 4
			s += 4;
			A = sub(A, E, false);
			dbg("SUB A, E");
			break; 
		case (byte) 0x94:	// SUB A,H 4
			s += 4;
			A = sub(A, H, false);
			dbg("SUB A, H");
			break; 
		case (byte) 0x95:	// SUB A,L 4
			s += 4;
			A = sub(A, L, false);
			dbg("SUB A, L");
			break; 
		case (byte) 0x96:	// SUB A,(HL) 7
			s += 7;
			A = sub(A, mem.rdByte(getHL()), false);
			dbg("SUB A, (HL)");
			break; 
		case (byte) 0x97:	// SUB A,A 4
			s += 4;
			A = sub(A, A, false);
			dbg("SUB A, A");
			break; 

		case (byte) 0x98:	// SBC A,B 4
			s += 4;
			A = sub(A, B, true);
			dbg("SBC A, B");
			break; 
		case (byte) 0x99:	// SBC A,C 4
			s += 4;
			A = sub(A, C, true);
			dbg("SBC A, C");
			break; 
		case (byte) 0x9A:	// SBC A,D 4
			s += 4;
			A = sub(A, D, true);
			dbg("SBC A, D");
			break; 
		case (byte) 0x9B:	// SBC A,E 4
			s += 4;
			A = sub(A, E, true);
			dbg("SBC A, E");
			break; 
		case (byte) 0x9C:	// SBC A,H 4
			s += 4;
			A = sub(A, H, true);
			dbg("SBC A, H");
			break; 
		case (byte) 0x9D:	// SBC A,L 4
			s += 4;
			A = sub(A, L, true);
			dbg("SBC A, L");
			break; 
		case (byte) 0x9E:	// SBC A,(HL) 7
			s += 7;
			A = sub(A, mem.rdByte(getHL()), true);
			dbg("SBC A, (HL)");
			break; 
		case (byte) 0x9F:	// SBC A,A 4
			s += 4;
			A = sub(A, A, true);
			dbg("SBC A, A");
			break; 

		case (byte) 0xA0:	// AND B 4
			s += 4;
			A = and(A, B);
			dbg("AND A, B");
			break; 
		case (byte) 0xA1:	// AND C 4
			s += 4;
			A = and(A, C);
			dbg("AND A, C");
			break; 
		case (byte) 0xA2:	// AND D 4
			s += 4;
			A = and(A, D);
			dbg("AND A, D");
			break; 
		case (byte) 0xA3:	// AND E 4
			s += 4;
			A = and(A, E);
			dbg("AND A, E");
			break; 
		case (byte) 0xA4:	// AND H 4
			s += 4;
			A = and(A, H);
			dbg("AND A, H");
			break; 
		case (byte) 0xA5:	// AND L 4
			s += 4;
			A = and(A, L);
			dbg("AND A, L");
			break; 
		case (byte) 0xA6:	// AND (HL) 7
			s += 7;
			A = and(A, mem.rdByte(getHL()));
			dbg("AND A, (HL)");
			break; 
		case (byte) 0xA7:	// AND A 4
			s += 4;
			A = and(A, A);
			dbg("AND A, A");
			break; 

		case (byte) 0xA8:	// XOR B 4
			s += 4;
			xor(B);
			dbg("XOR B");
			break; 
		case (byte) 0xA9:	// XOR C 4
			s += 4;
			xor(C);
			dbg("XOR C");
			break; 
		case (byte) 0xAA:	// XOR D 4
			s += 4;
			xor(D);
			dbg("XOR D");
			break; 
		case (byte) 0xAB:	// XOR E 4
			s += 4;
			xor(E);
			dbg("XOR E");
			break; 
		case (byte) 0xAC:	// XOR H 4
			s += 4;
			xor(H);
			dbg("XOR H");
			break; 
		case (byte) 0xAD:	// XOR L 4
			s += 4;
			xor(L);
			dbg("XOR L");
			break; 
		case (byte) 0xAE:	// XOR (HL) 7
			s += 7;
			xor(mem.rdByte(getHL()));
			dbg("XOR (HL)");
			break; 
		case (byte) 0xAF:	// XOR A 4
			s += 4;
			xor(A);
			dbg("XOR A");
			break; 

		case (byte) 0xB0:	// OR B 4
			s += 4;
			A = or(A, B);
			dbg("OR B");
			break; 
		case (byte) 0xB1:	// OR C 4
			s += 4;
			A = or(A, C);
			dbg("OR C");
			break; 
		case (byte) 0xB2:	// OR D 4
			s += 4;
			A = or(A, D);	
			dbg("OR D");
			break; 
		case (byte) 0xB3:	// OR E 4
			s += 4;
			A = or(A, E);
			dbg("OR E");
			break; 
		case (byte) 0xB4:	// OR H 4
			s += 4;
			A = or(A, H);
			dbg("OR H");
			break; 
		case (byte) 0xB5:	// OR L 4
			s += 4;
			A = or(A, L);
			dbg("OR L");
			break; 
		case (byte) 0xB6:	// OR (HL) 7
			s += 7;
			A = or(A, mem.rdByte(getHL()));
			dbg("OR (HL)");
			break; 
		case (byte) 0xB7:	// OR A 4
			s += 4;
			A = or(A, A);
			dbg("OR A");
			break; 

		case (byte) 0xB8:	// CP B 4
			s += 4;
			cp(A, B);
			dbg("CP B");
			break; 
		case (byte) 0xB9:	// CP C 4
			s += 4;
			cp(A, C);
			dbg("CP C");
			break; 
		case (byte) 0xBA:	// CP D 4
			s += 4;
			cp(A, D);
			dbg("CP D");
			break; 
		case (byte) 0xBB:	// CP E 4
			s += 4;
			cp(A, E);
			dbg("CP E");
			break; 
		case (byte) 0xBC:	// CP H 4
			s += 4;
			cp(A, H);
			dbg("CP H");
			break; 
		case (byte) 0xBD:	// CP L 4
			s += 4;
			cp(A, L);
			dbg("CP L");
			break; 
		case (byte) 0xBE:	// CP (HL) 7
			s += 7;
			cp(A, mem.rdByte(getHL()));
			dbg("CP (HL)");
			break; 
		case (byte) 0xBF:	// CP A 4
			s += 4;
			cp(A, A);
			dbg("CP A");
			break; 

		case (byte) 0xC0:	// RET NZ 11,15
			s += 11;
			if (!getZFlag()) {
				PC = popSP();
				s += 4;
			}
			dbg("RET NZ");
			break; 
		case (byte) 0xC1:	// POP BC 10
			s += 10;
			setBC(popSP());
			dbg("POP BC");
			break; 
		case (byte) 0xC2:	// JP NZ,&HHLL 10 (TODO check timing)
			s += 10;
			ts = fetchWordLH(); 
			if (!getZFlag()) {
				PC = ts;
			}
			dbg("JP NZ, &HHLL (" + Tools.toHexString(ts) + ")");
			break;
		case (byte) 0xC3:	// JP,&HHLL 10 (TODO check timing)
			s += 10;
			PC = fetchWordLH();
			dbg("JP &HHLL (" + Tools.toHexString(PC) + ")");
			break; 
		case (byte) 0xC4:	// CALL NZ,&HHLL 17,10
			s += 10;
			ts = fetchWordLH(); 
			if (!getZFlag()) {
				pushSP((short) (PC));
				PC = ts;
				s += 7;
			}
			dbg("CALL NZ, &HHLL (" + Tools.toHexString(ts) + ")");
			break;
		case (byte) 0xC5:	// PUSH BC 10
			s += 10;
			pushSP(getBC());
			dbg("PUSH BC : " + Tools.toHexString(getBC()) + " (SP after = " + Tools.toHexString(getSP()) + ")");
			break; 
		case (byte) 0xC6:	// ADD A,&NN 7
			s += 7;
			bs = fetchByte();
			A = add(A, bs, false);
			dbg("ADD A, &NN (" + Tools.toHexString(bs) + ")");
			break; 
		case (byte) 0xC7:	// RST &00 11
			s += 11;
			pushSP(PC);
			PC = (short) 0x00;
			dbg("RST &00");
			break; 
		case (byte) 0xC8:	// RET Z 11,15
			s += 11;
			if (getZFlag()) {
				PC = popSP();
				s += 4;
			}
			dbg("REZ Z");
			break; 
		case (byte) 0xC9:	// RET 10
			s += 10;
			PC = popSP();
			dbg("RET");
			break; 
		case (byte) 0xCA:	// JP Z,&HHLL 10
			s += 10;
			ts = fetchWordLH(); 
			if (getZFlag())
				PC = ts;
			break;
		case (byte) 0xCB:	// Execute CB prefix
			executeCB(false);
			break;
		case (byte) 0xCC:	// CALL Z,&LLHH 17,10
			s += 10;
			ts = fetchWordLH(); 
			if (getZFlag()) {
				pushSP(PC);
				PC = ts;
				s += 7;
			}
			dbg("CALL Z, &LLHH (" + Tools.toHexString(PC) + ")");
			break;
		case (byte) 0xCD:	// CALL &HHLL 17
			s += 17;
			ts = fetchWordLH(); 
			pushSP(PC);
			PC = ts;
			dbg("CALL &HHLL (" + Tools.toHexString(ts) + ")");
			break;
		case (byte) 0xCE:	// ADC A,&NN 7
			s += 7;
			bs = fetchByte();
			A = add(A, bs, true);
			dbg("ADC A, &NN (" + Tools.toHexString(bs) + ")");
			break; 
		case (byte) 0xCF:	// RST &08 11
			s += 11;
			pushSP(PC);
			PC = (short) 0x08;
			dbg("RST &08");
			break; 
		case (byte) 0xD0:	// RET NC 11,15
			s += 11;
			if (!getCFlag()) {
				PC = popSP();
				s += 4;
			}
			dbg("RET NC");
			break; 
		case (byte) 0xD1:	// POP DE 10
			s += 10;
			setDE(popSP());
			dbg("POP DE");
			break; 
		case (byte) 0xD2:	// JP NC,&HHLL 10
			s += 10;
			ts = fetchWordLH(); 
			if (!getCFlag())
				PC = ts;
			dbg("JP NC,&HHLL (" + Tools.toHexString(ts) +")");
			break;
		case (byte) 0xD3:	// OUT (&NN),A 11
			s += 10;
			byte port = fetchByte();
			dbg("OUT (&NN), A (&NN= " + Tools.toHexString(port) + ")");
			out(port, A);
			break; 
		case (byte) 0xD4:	// CALL NC,&HHLL 17,10
			s += 10;
			ts = fetchWordLH(); 
			if (!getCFlag()) {
				pushSP(PC);
				PC = ts;
				s += 7;
			}
			dbg("CAL NC, &HHLL (" + Tools.toHexString(PC) + ")");
			break;
		case (byte) 0xD5:	// PUSH DE 10
			s += 10;
			pushSP(getDE());
			dbg("PUSH DE : " + Tools.toHexString(getDE()) + " (SP after = " + Tools.toHexString(getSP()) + ")");
			break; 	
		case (byte) 0xD6:	// SUB A,&NN 7
			s += 7;
			A = sub(A, fetchByte(), false);
			dbg("SUB A, &NN");
			break; 
		case (byte) 0xD7:	// RST &10 11
			s += 11;
			pushSP(PC);
			PC = (short) 0x10;
			dbg("RST &10");
			break; 
		case (byte) 0xD8:	// RET C 11,15
			s += 11;
			if (getCFlag()) {
				PC = popSP();
				s += 4;
			}
			dbg("RET C");
			break; 	
		case (byte) 0xD9:	// EXX 4
			s += 4;
			bs = B;
			B = BP;
			BP = bs; 
			bs = C;
			C = CP;
			CP = bs;
			bs = D;
			D = DP;
			DP = bs;
			bs = E;
			E = EP;
			EP = bs;
			bs = H;
			H = HP;
			HP = bs;
			bs = L;
			L = LP;
			LP = bs;
			dbg("EXX");
			break;
		case (byte) 0xDA:	// JP C,&HHLL 10
			s += 10;
			ts = fetchWordLH(); 
			if (getCFlag())
				PC = ts;
			dbg("JP C, &HHLL");
			break;
		case (byte) 0xDB:	// IN A,(&NN) 11
			s += 11;
			A = in(fetchByte()); 
			dbg("IN A, (&NN)");
			break;
		case (byte) 0xDC:	// CALL C,&HHLL 17,10
			s += 10;
			ts = fetchWordLH(); 
			if (getCFlag()) {
				pushSP(PC);
				PC = ts;
				s += 7;
			}
			dbg("CALL C, &HHLL (" + Tools.toHexString(PC) + ")");
			break;
		case (byte) 0xDD:	// Execute DD prefix
			executeDD();
			break; 
		case (byte) 0xDE:	// SBC A,&NN 7
			s += 7;
			bs = fetchByte();
			A = sub(A, bs, true);
			dbg("SBC A, &NN (" + Tools.toHexString(bs) + ")");
			break; 
		case (byte) 0xDF:	// RST &18 11
			s += 11;
			pushSP(PC);
			PC = (short) 0x18;
			break; 
		case (byte) 0xE0:	// RET PO 11,15
			s += 11;
			if (!getPVFlag()) {
				PC = popSP();
				s += 4;
			}
			dbg("RET PO");
			break; 
		case (byte) 0xE1:	 // POP HL 10
			s += 10;
			setHL(popSP());
			dbg("POP HL");
			break;
		case (byte) 0xE2:	// JP PO,&HHLL 10
			s += 10;
			ts = fetchWordLH(); 
			if (!getPVFlag())
				PC = ts;
			dbg("JP PO, &HHLL (" + Tools.toHexString(ts) + ")");
			break;
		case (byte) 0xE3:	// EX (SP),HL 19
			s += 19;
			bs = L; 
			L = mem.rdByte(getSP());
			mem.wrtByte(getSP(), bs);
			bs = H;
			H = mem.rdByte((short) (getSP() + 1));
			mem.wrtByte((short) (getSP() + 1), bs);
			dbg("EX (SP),HL");
			break;
		case (byte) 0xE4:	// CALL PO,&HHLL 17,10
			s += 10;
			ts = fetchWordLH(); 
			if (!getPVFlag()) {
				pushSP(PC);
				PC = ts;
				s += 7;
			}
			dbg("CALL PO, &HHLL (" + Tools.toHexString(ts) + ")");
			break;
		case (byte) 0xE5:	// PUSH HL 10
			s += 10;
			pushSP(getHL());
			dbg("PUSH HL");
			break; 
		case (byte) 0xE6:	// AND &NN 7
			s += 7;
			bs = fetchByte();
			A = and(A, bs);
			dbg("AND &NN (" + Tools.toHexString(bs) + ")");
			break; 
		case (byte) 0xE7:	// RST &20 11
			s += 11;
			pushSP(PC);
			PC = (short) 0x20;
			break; 
		case (byte) 0xE8:	// RET PE 11,15
			s += 11;
			if (getPVFlag()) {
				PC = popSP();
				s += 4;
			}
			break; 
		case (byte) 0xE9: 	// JP (HL) 4 
			s += 4;
			PC = getHL();
			break; 
		case (byte) 0xEA:	// JP PE,&HHLL 10
			s += 10;
			ts = fetchWordLH(); 
			if (getPVFlag())
				PC = ts;
			break;
		case (byte) 0xEB:	// EX DE, HL 4
			s += 4;
			ts = getDE();
			setDE(getHL());
			setHL(ts);
			break; 
		case (byte) 0xEC:	// CALL PE,&HHLL 17,10
			s += 10;
			ts = fetchWordLH(); 
			if (getPVFlag()) {
				pushSP(PC);
				PC = ts;
				s += 17;
			}
			dbg("CALL PE, &HHLL (" + Tools.toHexString(PC) + ")");
			break;
		case (byte) 0xED:	// Execute ED prefix
			executeED();
			break; 
		case (byte) 0xEE:	// XOR &NN 7
			s += 7;
			xor(fetchByte());
			break; 
		case (byte) 0xEF:	// RST &28 11
			s += 11;
			pushSP(PC);
			PC = (short) 0x28;
			break; 
		case (byte) 0xF0:	// RET P 11,15
			s += 11;
			if (!getSFlag()) {
				PC = popSP();
				s += 4;
			}
			break; 
		case (byte) 0xF1:	// POP AF 10
			s += 10;
			setAF(popSP());
			dbg("POP AF");
			break; 
		case (byte) 0xF2:	// JP P,&HHLL 10 
			s += 10;
			ts = fetchWordLH(); 
			if (!getSFlag()) PC = ts; // TODO P is positive?
			break;
		case (byte) 0xF3:	// DI 4
			s += 4;
			doDI();
			dbg("DI");
			break; 
		case (byte) 0xF4:	// CALL P,&HHLL 17,10
			s += 10;
			ts = fetchWordLH(); 
			if (!getSFlag()) { // TODO P is positive?
				pushSP(PC);
				PC = ts;
				s += 7;
			}
			dbg("CALL P, &HHLL (" + Tools.toHexString(PC) + ")");
			break;
		case (byte) 0xF5:	// PUSH AF 10
			s += 10;
			pushSP(getAF());
			dbg("PUSH AF");
			break; 
		case (byte) 0xF6:	// OR &NN 7
			s += 7;
			A = or(A, fetchByte());
			break; 
		case (byte) 0xF7:	// RST &30 11
			s += 11;
			pushSP(PC);
			PC = (short) 0x30;
			break; 
		case (byte) 0xF8:	// RET M 11,15
			s += 11;
			if (getSFlag()) {
				PC = popSP();
				s += 4;
			}
			break; 
		case (byte) 0xF9:	// LD SP,HL 10
			s += 10;
			setSP(getHL());
			dbg("LD SP, HL (" + Tools.toHexString(getSP()) + ")");
			break; 
		case (byte) 0xFA:	// JP M,&HHLL 10
			s += 10;
			ts = fetchWordLH(); 
			if (getSFlag())
				PC = ts;
			break;
		case (byte) 0xFB:	// EI 4
			s += 4;
			doEI();
			break; 
		case (byte) 0xFC:	// CALL M,&HHLL 17,10
			s += 10;
			ts = fetchWordLH(); 
			if (getSFlag()) {
				pushSP(PC);
				PC = ts;
				s += 7;
			}
			dbg("CALL M, &HHLL (" + Tools.toHexString(PC) + ")");
			break;
		case (byte) 0xFD:	// Execute FD prefix
			dbg("NEXT INSTRUCTION: IX <> IY");
			bs = IXH;
			IXH = IYH;
			IYH = bs; 
			bs = IXL;
			IXL = IYL;
			IYL = bs;
			executeDD();
			bs = IXH;
			IXH = IYH;
			IYH = bs;
			bs = IXL;
			IXL = IYL;
			IYL = bs;
			break;
		case (byte) 0xFE:	// CP &NN 7
			s += 7;
			bs = fetchByte();
			cp(A, bs);
			dbg("CP &NN ("+ Tools.toHexString(bs) + ")");
			break; 
		case (byte) 0xFF:	// RST &38 11
			s += 11;
			pushSP(PC);
			PC = (short) 0x38;
			break; 
		}
	}

	public byte inc(byte v) {
		boolean c = getCFlag(); // dec8 is like sub 1 except that C is unaffected
		byte res = add(v, (byte)1, false);
		setCFlag(c);
		return res;
	}

	public short inc(short v) {
		byte flags = F; // inc16 is like sub 1 except that C is unaffected
		short res = add(v, (short)1, false);
		F = flags;
		return res;
	}

	public byte dec(byte v) {
		boolean c = getCFlag(); // dec8 is like sub 1 except that C is unaffected
		byte res = sub(v, (byte)1, false);
		setCFlag(c);
		return res;
	}

	public short dec(short v) {
		byte flags = F; // dec16 is like sub 1 except that C is unaffected
		short res = sub(v, (short)1, false);
		F = flags;
		return res;
	}

	public void executeDD() {

		short addr = 0;
		byte b = fetchByte();
		switch (b) {
		case (byte) 0x09: // ADD IX,BC TODO: carry?
			s += 15;
			setIX(add(getIX(), getBC(), false));
			dbg("ADD IX, BC");
			break; 
		case (byte) 0x19: // ADD IX,DE TODO: carry?
			s += 15;
			setIX(add(getIX(), getDE(), false));
			dbg("ADD IX, DE");
			break; 
		case (byte) 0x21: // LD IX,&HHLL
			s += 14;
			ts = fetchWordLH();
			setIX(mem.readWordLH(ts));
			dbg("LD IX, &LLHH (" + ts + ")");
			break; 
		case (byte) 0x22: // LD (&HHLL),IX
			s += 20;
			ts = fetchWordLH();
			mem.writeShortLH(ts, getIX());
			dbg("LD (&HHLL),IX (" + Tools.toHexString(ts) + ")");
			break; 
		case (byte) 0x23: // INC IX
			s += 10;
			setIX(inc(getIX()));
			dbg("INC IX");
			break; 
		case (byte) 0x24: // INC IXH
			s += 8;
			IXH = inc(IXH);
			dbg("INC IXH");
			break; 
		case (byte) 0x25: // DEC IXH
			s += 8;
			IXH = dec(IXH);
			dbg("DEC IXH");
			break; 
		case (byte) 0x26: // LD IXH,&NN
			s += 11;
			IXH = fetchByte(); 
			dbg("LD IXH, &NN");
			break;
		case (byte) 0x29: // ADD IX,IX
			s += 15;
			setIX(add(getIX(), getIX(), false));
			dbg("ADD IX, IX");
			break; 
		case (byte) 0x2A: // LD IX,(&HHLL)
			s += 20;
			setIX(mem.readWordLH(fetchWordLH()));
			dbg("LD IX, (&HHLL)");
			break; 
		case (byte) 0x2B: // DEC IX
			s += 10;
			setIX(dec(getIX()));
			dbg("DEC IX");
			break; 
		case (byte) 0x2C: // INC IXL
			s += 8;
			IXL = inc(IXL);
			dbg("INC IXL");
			break;
		case (byte) 0x2D: // DEC IXL
			s += 8;
			IXL = dec(IXL);
			dbg("DEC IXL");
			break;
		case (byte) 0x2E: // LD IXL,&NN
			s += 11;
			IXL = fetchByte();
			dbg("LD IXL, &NN");
			break; 
		case (byte) 0x34:// INC (IX+NN) TODO: carry?
			s += 23;
			dis = fetchByte();
			ts = (short)((getIX() & 0xffff) + dis);
			mem.wrtByte(ts, inc(mem.rdByte(ts)));
			dbg("INC (IX+" + dis +")");
			break;
		case (byte) 0x35: // DEC (IX+NN) TODO: carry?
			s += 23;
			dis = fetchByte();
			ts = (short)((getIX() & 0xffff) + dis);
			mem.wrtByte(ts, dec(mem.rdByte(ts)));
			dbg("DEC (IX+"+dis+")");
			break;
		case (byte) 0x36: // LD (IX+NN), nn 
			s += 19;
			ts = (short)((getIX() & 0xffff) + dis);
			mem.wrtByte(ts, fetchByte());
			dbg("LD (IX+NN), nn");
			break;
		case (byte) 0x39: // ADD IX,SP
			s += 15;
			setIX(add(getIX(), getSP(), false));
			dbg("ADD IX, SP");
			break; 
		case (byte) 0x44: // LD B,IXH
			s += 8;
			B = IXH;
			dbg("LD B, IXH");
			break; 
		case (byte) 0x45: // LD B,IXL
			s += 8;
			B = IXL;
			dbg("LD B, IXL");
			break;
		case (byte) 0x46: // LD B,(IX+NN)
			s += 19;
			dis = fetchByte();
			ts = (short)((getIX() & 0xffff) + dis);
			B = mem.rdByte(ts);
			dbg("LD B, (IX+"+dis+")");
			break; 
		case (byte) 0x4C: // LD C,IXH
			s += 8;
			C = IXH;
			dbg("LD C, IXH");
			break; 
		case (byte) 0x4D: // LD C,IXL
			s += 8;
			C = IXL;
			dbg("LD C, IXL");
			break; 
		case (byte) 0x4E: // LD C,(IX+NN)
			s += 19;
			dis = fetchByte();
			ts = (short)((getIX() & 0xffff) + dis);
			C = mem.rdByte(ts);
			dbg("LD C, (IX+"+dis+")");
			break; 
		case (byte) 0x54: // LD D,IXH
			s += 8;
			D = IXH;
			dbg("LD D, IXH");
			break; 
		case (byte) 0x55: // LD D,IXL
			s += 8;
			D = IXL;
			dbg("LD D, IXL");
			break; 
		case (byte) 0x56: // LD D,(IX+NN)
			s += 19;
			dis = fetchByte();
			ts = (short)((getIX() & 0xffff) + dis);
			D = mem.rdByte(ts);
			dbg("LD D,(IX+"+dis+")");
			break; 
		case (byte) 0x5C: // LD E,IXH
			s += 8;
			E = IXH;
			dbg("LD E, IXH");
			break; 
		case (byte) 0x5D: // LD E,IXL
			s += 8;
			E = IXL;
			dbg("LD E, IXL");
			break; 
		case (byte) 0x5E: // LD E,(IX+NN)
			s += 19;
			dis = fetchByte();
			ts = (short)(getIX() + dis);
			E = mem.rdByte(ts);
			dbg("LD E, (IX+"+dis+")");
			break; 
		case (byte) 0x60: // ID IXH,B
			s += 8;
			IXH = B;
			dbg("LD IXH, B");
			break; 
		case (byte) 0x61: // LD IXH,C
			s += 8;
			IXH = C;
			dbg("LD IXH, C");
			break; 
		case (byte) 0x62: // LD IXH,D
			s += 8;
			IXH = D;
			dbg("LD IXH, D");
			break; 
		case (byte) 0x63: // LD IXH,E
			s += 8;
			IXH = E;
			dbg("LD IXH, E");
			break; 
		case (byte) 0x64: // LD IXH,IXH
			s += 8;
			dbg("LD IXH, IXH");
			break; 
		case (byte) 0x65: // LD IXH,IXL
			s += 8;
			IXH = IXL;
			dbg("LD IXH, IXL");
			break; 
		case (byte) 0x66: // LD H,(IX+NN)
			s += 19;
			dis = fetchByte();
			ts = (short)(getIX() + dis);
			H = mem.rdByte(ts);
			dbg("LD H,(IX+"+dis+")");
			break; 
		case (byte) 0x67: // LD IXH,A
			s += 8;
			IXH = A;
			dbg("LD IXH, A");
			break; 
		case (byte) 0x68: // LD IXL,B
			s += 8;
			IXL = B;
			dbg("LD IXL, B");
			break; 
		case (byte) 0x69: // LD IXL,C
			s += 8;
			IXL = C;
			dbg("LD IXL, C");
			break; 
		case (byte) 0x6A: // LD IXL,D
			s += 8;
			IXL = D;
			dbg("LD IXL, D");
			break; 
		case (byte) 0x6B: // LD IXL,E
			s += 8;
			IXL = E;
			dbg("LD IXL, E");
			break; 
		case (byte) 0x6C: // LD IXL,IXH
			s += 8;
			IXL = IXH;
			dbg("LD IXL, IXH");
			break; 
		case (byte) 0x6D: // LD IXL,IXL
			s += 8;
			dbg("LD IXL, IXL");
			break; 
		case (byte) 0x6E: // LD L,(IX+NN)
			s += 19;
			dis = fetchByte();
			addr = (short)((getIX() & 0xffff) + dis);
			L = mem.rdByte(addr);
			dbg("LD L, (IX+"+dis+")");
			break;  
		case (byte) 0x6F: // LD IXL,A
			s += 8;
			IXL = A;
			dbg("LD IXL, A");
			break;  
		case (byte) 0x70: // LD (IX+NN),B
			s += 19;
			dis = fetchByte();
			addr = (short)((getIX() & 0xffff) + dis);
			mem.wrtByte(addr, B);
			dbg("LD (IX+"+dis+"),B");
			break; 
		case (byte) 0x71: // LD (IX+NN),C
			s += 19;
			dis = fetchByte();
			addr = (short)((getIX() & 0xffff) + dis);
			mem.wrtByte(addr, C);
			dbg("LD (IX+"+dis+"),C");
			break; 
		case (byte) 0x72: // LD (IX+NN),D
			s += 19;
			dis = fetchByte();
			addr = (short)((getIX() & 0xffff) + dis);
			mem.wrtByte(addr, D);
			dbg("LD (IX+"+dis+"),D");
			break; 
		case (byte) 0x73: // LD (IX+NN),E
			s += 19;
			dis = fetchByte();
			addr = (short)((getIX() & 0xffff) + dis);
			mem.wrtByte(addr, E);
			dbg("LD (IX+"+dis+"),E");
			break; 
		case (byte) 0x74: // LD (IX+NN),H
			s += 19;
			dis = fetchByte();
			addr = (short)((getIX() & 0xffff) + dis);
			mem.wrtByte(addr, H);
			dbg("LD (IX+"+dis+"),H");
			break; 
		case (byte) 0x75: // LD (IX+NN),L
			s += 19;
			dis = fetchByte();
			addr = (short)((getIX() & 0xffff) + dis);
			mem.wrtByte(addr, L);
			dbg("LD (IX+"+dis+"),L");
			break; 
		case (byte) 0x77: // LD (IX+NN),A
			s += 19;
			dis = fetchByte();
			addr = (short)((getIX() & 0xffff) + dis);
			mem.wrtByte(addr, A);
			dbg("LD (IX+"+dis+"),A");
			break; 
		case (byte) 0x7C: // LD A,IXH
			s += 8;
			A = IXH;
			dbg("LD A, IXH");
			break; 
		case (byte) 0x7D: // LD A,IXL
			s += 8;
			A = IXL;
			dbg("LD A, IXL");
			break; 
		case (byte) 0x7E: // LD A,(IX+NN)
			s += 19;
			dis = fetchByte();
			addr = (short)((getIX() & 0xffff) + dis);
			A = mem.rdByte(addr);
			dbg("LD A, (IX+"+dis+")");
			break; 
		case (byte) 0x84: // ADD A,IXH
			s += 8;
			A = add(A, IXH, false);
			break; 
		case (byte) 0x85: // ADD A,IXL
			s += 8;
			A = add(A, IXL, false);
			break; 
		case (byte) 0x86: // ADD A, (IX+NN)
			s += 19;
			dis = fetchByte();
			addr = (short)((getIX() & 0xffff) + dis);
			A = add(A, mem.rdByte(addr), false);
			dbg("ADD A, (IX+"+dis+")");
			break; 
		case (byte) 0x8C: // ADC A,IXH
			s += 8;
			A = add(A, IXH, true);
			break; 
		case (byte) 0x8D: // ADC A,IXL
			s += 8;
			A = add(A, IXL, true);
			break; 
		case (byte) 0x8E: // ADC A, (IX+NN)
			s += 19;
			dis = fetchByte();
			addr = (short)((getIX() & 0xffff) + dis);
			A = add(A, mem.rdByte(addr), true);
			dbg("ADC A, (IX+"+dis+")");
			break; 
		case (byte) 0x94: // SUB A,IXH
			s += 8;
			A = sub(A, IXH, false);
			break; 
		case (byte) 0x95: // SUB A,IXL
			s += 8;
			A = sub(A, IXL, false);
			break; 
		case (byte) 0x96:
			s += 19;
			dis = fetchByte();
			addr = (short)((getIX() & 0xffff) + dis);
			A = sub(A, mem.rdByte(addr), false);
			dbg("SUB A, (IX+"+dis+")");
			break; // SUB A, (IX+NN)
		case (byte) 0x9C:
			s += 8;
			A = sub(A, IXH, true);
			break; // SBC A,IXH
		case (byte) 0x9D:
			s += 8;
			A = sub(A, IXL, true);
			break; // SBC A,IXL
		case (byte) 0x9E:
			s += 19;
			dis = fetchByte();
			addr = (short)((getIX() & 0xffff) + dis);
			A = sub(A, mem.rdByte(addr), true);
			dbg("SBC A, (IX+"+dis+")");
			break; // SBC A, (IX+NN)
		case (byte) 0xA4:
			s += 8;
			A = and(A, IXH);
			break; // AND IXH
		case (byte) 0xA5:
			s += 8;
			A = and(A, IXL);
			break; // AND IXL
		case (byte) 0xA6:
			s += 19;
			dis = fetchByte();
			addr = (short)((getIX() & 0xffff) + dis);
			A = and(A, mem.rdByte(addr));
			dbg("AND (IX+"+dis+")");
			break; // AND (IX+NN)
		case (byte) 0xAC:
			s += 8;
			xor(IXH);
			break; // XOR IXH
		case (byte) 0xAD:
			s += 8;
			xor(IXL);
			break; // XOR IXL
		case (byte) 0xAE:
			s += 19;
			dis = fetchByte();
			addr = (short)((getIX() & 0xffff) + dis);
			xor(mem.rdByte(addr));
			dbg("XOR (IX+"+dis+")");
			break; // XOR (IX+NN)
		case (byte) 0xB4:
			s += 8;
			A = or(A, IXH);
			break; // OR IXH
		case (byte) 0xB5:
			s += 8;
			A = or(A, IXL);
			break; // OR IXL
		case (byte) 0xB6:
			s += 19;
			dis = fetchByte();
			addr = (short)((getIX() & 0xffff) + dis);
			A = or(A, mem.rdByte(addr));
			dbg("OR (IX+"+dis+")");
			break; // OR (IX+NN)
		case (byte) 0xBC:
			s += 8;
			cp(A, IXH);
			dbg("CP IXH");
			break; // CP IXH
		case (byte) 0xBD:
			s += 8;
			cp(A, IXL);
			dbg("CP IXL");
			break; // CP IXL
		case (byte) 0xBE:
			s += 19;
			dis = fetchByte();
			addr = (short)((getIX() & 0xffff) + dis);
			cp(A, mem.rdByte(addr));
			dbg("CP (IX+"+dis+")");
			break; // CP (IX+NN)
		case (byte) 0xCB:
			executeCB(true);
			break;
		case (byte) 0xE1:
			s += 14;
			setIX(popSP());
			dbg("POP IX");
			break; // POP IX
		case (byte) 0xE3:
			s += 23;
			bs = IXL; // EX (SP),IX 19
			IXL = mem.rdByte(getSP());
			mem.wrtByte(getSP(), bs);
			bs = IXH;
			IXH = mem.rdByte((short) ((getSP() & 0xffff) + 1));
			mem.wrtByte((short) ((getSP() & 0xffff) + 1), bs);
			dbg("EX (SP), IX");
			break;
		case (byte) 0xE5: // PUSH IX
			s += 15;
			pushSP(getIX());
			dbg("PUSH IX");
			break; 
		case (byte) 0xE9: // JP (IX)
			s += 8;
			PC = getIX();
			dbg("JP (IX)");
			break;  
		case (byte) 0xF9: // LD SP, IX (correct?)
			s += 10;
			setSP(getIX());
			dbg("LD SP, IX (" + Tools.toHexString(PC) + ")");
			break;
		}
	}

	public void executeCB(boolean afterDD) {

		// Read displacement
		byte displacement = 0;
		if (afterDD) {
			displacement = fetchByte();
		}
		
		byte opcode = fetchByte();
		int x = Bit.and(Bit.rshift(opcode, 6), (byte)0x03);
		int y = Bit.and(Bit.rshift(opcode, 3), (byte)0x07);
		int z = Bit.and(opcode, (byte)0x07);

		String dbg = "";
		
		// Read value
		byte val = 0;
		if (afterDD) {
			val = mem.rdByte((short) (getIX() + displacement));
		} else {
			switch (z) {
			case 0: val = B; dbg = "B"; break;
			case 1: val = C; dbg = "C"; break;
			case 2: val = D; dbg = "D"; break;
			case 3: val = E; dbg = "E"; break;
			case 4: val = H; dbg = "H"; break;
			case 5: val = L; dbg = "L"; break;
			case 6: val = mem.rdByte((short) (getHL())); dbg = "(HL)"; break;
			case 7: val = A; dbg = "A"; break;
			}
		}
		
		dbg = " " + dbg +"=" + Tools.toHexString(val) + "(" + Tools.toBinString(val) + ")";

		// Perform operation
		switch (x) {
		case 0:
			// rot[y] r[z]
			s += 23;
			switch (y) {
			case 0: val = doRLC(val); dbg = "RLC " + dbg; break; // RLC
			case 1: val = doRRC(val); dbg = "RRC " + dbg; break; // RRC
			case 2: val = doRL(val); dbg = "RL " + dbg; break;
			case 3: val = doRR(val); dbg = "RR " + dbg; break;
			case 4: val = doSLA(val); dbg = "SLA " + dbg; break;
			case 5: val = doSRA(val); dbg = "SRA " + dbg; break;
			case 6: val = doSLL(val); dbg = "SLL " + dbg; break;
			case 7: val = doSRL(val); dbg = "SRL " + dbg; break;
			}
			break;
		case 1: s += 20; readBit(y, val); dbg = "BIT " + y + dbg; break; // BIT y, r[z]
		case 2: s += 23; val = setBit(y, val, false); dbg = "RES " + y + dbg; break; // RES y, r[z]
		case 3: s += 23; val = setBit(y, val, true); dbg = "SET " + y + dbg; break; // SET y, r[z]
		}

		// Write back result
		// For afterDD case: Both in (IX+d) and, if instruction is not BIT, also in initial
		// register, unless initial register was (HL))
		if (afterDD) {
			mem.wrtByte((short) (getIX() + displacement), val);
		}
		if (x != 1) {
			switch (z) {
			case 0: B = val; break;
			case 1: C = val; break;
			case 2: D = val; break;
			case 3: E = val; break;
			case 4: H = val; break;
			case 5: L = val; break;
			case 6: if (!afterDD) mem.wrtByte((short) (getHL()), val); break;
			case 7: A = val; break;
			}
		}
		dbg = dbg + " after = " + Tools.toHexString(val) + "(" + Tools.toBinString(val) + ")";
		dbg(dbg);
	}

	public void executeED() {

		// TODO: check all neg, reti, retn, im operations from clrhome.org/table/

		byte b = fetchByte();
		switch (b) {
		case (byte) 0x40:
			s += 12;
			B = in(C);
			setInFlags(B);
			break; // IN B,(C)
		case (byte) 0x41:
			s += 12;
			out(C, B);
			break; // OUT (C),B
		case (byte) 0x42:
			s += 15;
			setHL(sub(getHL(), getBC(), true));
			break; // SBC HL,BC
		case (byte) 0x43: // LD (&HHLL),BC
			s += 20;
			ts = fetchWordLH();
			mem.writeShortLH(ts, getBC());
			dbg("LD (&LLHH), BC (" + Tools.toHexString(ts) + ")");
			break;
		case (byte) 0x44:
			s += 8;
			A = sub((byte) 0, A, false);
			break; // NEG
		case (byte) 0x45:
			s += 14;
			doRETN();
			break; // RETN 
		case (byte) 0x46:
			s += 8;
			doIM(0);
			break; // IM 0
		case (byte) 0x47:
			s += 9;
			IC = A;
			break; // LD I,A
		case (byte) 0x48:
			s += 12;
			C = in(C);
			setInFlags(C);
			break; // IN C,(C)
		case (byte) 0x49:
			s += 12;
			out(C, C);
			break; // OUT C,(C)
		case (byte) 0x4A:
			s += 15;
			setHL(add(getHL(), getBC(), true));
			break; // ADC HL,BC
		case (byte) 0x4B:
			s += 20;
			setBC(mem.readWordLH(fetchWordLH()));
			dbg("LD BC, (&HHLL)");
			break; // LD BC,(&HHLL)
		case (byte) 0x4D:
			s += 8;
			doRETI();
			break; // RETI
		case (byte) 0x4F:
			s += 9;
			RR = A;
			break; // LD R,A
		case (byte) 0x50:
			s += 12;
			D = in(C);
			setInFlags(D);
			break; // IN D,(C)
		case (byte) 0x51:
			s += 12;
			out(C, D);
			break; // OUT (C),D
		case (byte) 0x52:
			s += 15;
			setHL(sub(getHL(), getDE(), true));
			break; // SBC HL,DE
		case (byte) 0x53:	// LD (&HHLL),DE
			s += 20;
			ts = fetchWordLH();
			mem.writeShortLH(ts, getDE());
			dbg("LD (&LLHH), DE (written to " + Tools.toHexString(ts));
			break; 
		case (byte) 0x56:
			s += 8;
			doIM(1);
			break; // IM 1
		case (byte) 0x57:
			s += 9; 
			A = IC;
			break; // LD A,I
		case (byte) 0x58:
			s += 12;
			E = in(C);
			setInFlags(E);
			break; // IN E,(C)
		case (byte) 0x59:
			s += 12;
			out(C, E);
			break; // OUT (C),E
		case (byte) 0x5A:
			s += 15;
			setHL(add(getHL(), getDE(), true));
			break; // ADC HL,DE
		case (byte) 0x5B:
			s += 20;
			setDE(mem.readWordLH(fetchWordLH()));
			break; // LD DE,(&HHLL)
		case (byte) 0x5D: // RETN 
			s += 14;
			doRETN();
			break;
		case (byte) 0x5E:
			s += 8;
			doIM(2);
			break; // IM 2 
		case (byte) 0x5F:
			s += 9;
			A = RR;
			break; // LD A,R
		case (byte) 0x60:
			s += 12;
			H = in(C);
			setInFlags(H);
			break; // IN H,(C)
		case (byte) 0x61:
			s += 12;
			out(C, H);
			break; // OUT (C),H
		case (byte) 0x62:
			s += 15;
			setHL(sub(getHL(), getHL(), true));
			break; // SBC HL,HL
		case (byte) 0x63:	// LD (&HHLL),HL
			s += 20;
			ts = fetchWordLH();
			mem.writeShortLH(ts, getHL());
			dbg("LD (&HHLL), HL (written to " + Tools.toHexString(ts) +")");
			break; 
		case (byte) 0x67:
			s += 18;
			doRRD();
			dbg("RRD");
			break;
		case (byte) 0x68:
			s += 12;
			L = in(C);
			dbg("IN L, (C)");
			setInFlags(L);
			break; // IN L,(C)
		case (byte) 0x69:
			s += 12;
			out(C, L);
			dbg("OUT (C), L");
			break; // OUT (C),L
		case (byte) 0x6A:
			s += 15;
			setHL(add(getHL(), getHL(), true));
			dbg("ADC HL, HL");
			break; // ADC HL,HL
		case (byte) 0x6B:
			s += 20;
			setHL(mem.readWordLH(fetchWordLH()));
			dbg("LD HL, (&HHLL)");
			break; // LD HL,(&HHLL)
		case (byte) 0x6F:
			s += 18;
			doRLD();
			dbg("RLD");
			break;
		case (byte) 0x70:
			s += 12;
			F = in(C);
			//setInFlags(F); ???
			break; // IN F,(C)
		case (byte) 0x71:
			s += 12;
			out(C, F);
			break; // OUT (C),F
		case (byte) 0x72:
			s += 15;
			setHL(sub(getHL(), getSP(), true));
			dbg("SBC HL, SP");
			break; // SBC HL,SP
		case (byte) 0x73:
			s += 20;
			ts = fetchWordLH();
			mem.writeShortLH(ts, getSP());
			dbg("LD (&HHLL), SP (written to " + Tools.toHexString(ts) + ")");
			break; // LD (&HHLL),SP
		case (byte) 0x78:
			s += 12;
			A = in(C);
			setInFlags(A);
			dbg("IN A, (C)");
			break; // IN A,(C)
		case (byte) 0x79:
			s += 12;
			out(C, A);
			dbg("OUT (C), A)");
			break; // OUT (C),A
		case (byte) 0x7A:
			/*
			s += 15;
			setHL(add(getHL(), getSP(), true));
			dbg("ADC HL, SP");
			break; // ADC HL,SP
			*/
			throw new RuntimeException("Unsupported instruction");
		case (byte) 0x7B:
			s += 20;
			setSP(mem.readWordLH(fetchWordLH()));
			dbg("LD SP, (&HHLL)");
			break; // LD SP,(&HHLL)
		case (byte) 0xA0:
			/*
			s += 16;
			mem.wrtByte(getDE(), mem.rdByte(getHL()));
			setHL((short) (getHL() + 1));
			setDE((short) (getDE() + 1));
			setBC((short) (getBC() - 1));
			setHFlag(false); setPVFlag(getBC() != 0); setNFlag(false);
			dbg("LDI");
			break;
			*/
			throw new RuntimeException("Unsupported instruction");
		case (byte) 0xA1: // CPI
			/*
			s += 16;
			boolean c = getCFlag();
			cp(A, mem.rdByte(getHL())); 
			setHL((short) (getHL() + 1));
			setBC((short) (getBC() - 1));
			setPVFlag(getBC() != 0);
			setCFlag(c);
			dbg("CPI");
			break;
			*/
			throw new RuntimeException("Unsupported instruction");
		case (byte) 0xA2:
		/*
			s += 16;
			mem.wrtByte(getHL(), in(C)); // INI
			setHL((short) (getHL() + 1));
			setBC((short) (getBC() - 1));
			dbg("INI");
			break;
			*/
			throw new RuntimeException("Unsupported instruction");
		case (byte) 0xA3:
			/*
			s += 16;
			out(C, mem.rdByte(getHL())); // OTI
			setHL((short) (getHL() + 1));
			setBC((short) (getBC() - 1));
			dbg("OTI");
			break;
			*/
			throw new RuntimeException("Unsupported instruction");
		case (byte) 0xA8: // LDD
			/*
			s += 16;
			mem.wrtByte(getDE(), mem.rdByte(getHL())); 
			setHL((short) (getHL() - 1));
			setDE((short) (getDE() - 1));
			setBC((short) (getBC() - 1));
			setHFlag(false); setPVFlag(getBC() != 0); setNFlag(false);
			dbg("LDD");
			break;
			*/
			throw new RuntimeException("Unsupported instruction");
		case (byte) 0xA9:
			/*
			s += 16;
			boolean tc = getCFlag();
			cp(A, mem.rdByte(getHL())); // CPD
			setHL((short) (getHL() - 1));
			setBC((short) (getBC() - 1));
			setNFlag(true);
			setCFlag(tc);
			setPVFlag((getBC() & 0xffff) - 1 != 0);
			dbg("CPD");
			break;
			*/
			throw new RuntimeException("Unsupported instruction");
		case (byte) 0xAA:
			/*
			s += 16;
			mem.wrtByte(getHL(), in(C)); // IND
			setHL((short) (getHL() - 1));
			setBC((short) (getBC() - 1));
			dbg("IND");
			break;
			*/
			throw new RuntimeException("Unsupported instruction");
		case (byte) 0xAB:
			/*
			s += 16;
			out(C, mem.rdByte(getHL())); // OTD
			setHL((short) (getHL() - 1));
			setBC((short) (getBC() - 1));
			dbg("OTD");
			break;
			*/
			throw new RuntimeException("Unsupported instruction");
		case (byte) 0xB0: // LDIR
			s += 16;
			mem.wrtByte(getDE(), mem.rdByte(getHL())); 
			String xx = "Wrote " + Tools.toHexString(mem.rdByte(getHL())) + " to address " + Tools.toHexString(getDE());
			setHL((short) (getHL() + 1));
			setDE((short) (getDE() + 1));
			setBC((short) (getBC() - 1));
			if (getBC() != 0) {
				PC--; // B0
				PC--; // ED
				s += 5;
			}
			setHFlag(false); setPVFlag(false); setNFlag(false);
			dbg("LDIR " + xx);
			break;
		case (byte) 0xB1: // CPIR
			/*
			s += 16;
			c = getCFlag();
			cp(A, mem.rdByte(getHL())); 
			setHL((short) (getHL() + 1));
			setBC((short) (getBC() - 1));
			if (getBC() != 0 && !getZFlag()) {
				PC--;
				PC--;
				s += 5;
			}
			setPVFlag(getBC() != 0);
			setCFlag(c);
			dbg("CPIR");
			break;
			*/
			throw new RuntimeException("Unsupported instruction");
		case (byte) 0xB2:
			/*
			s += 16;
			mem.wrtByte(getHL(), in(C)); // INIR
			setHL((short) (getHL() + 1));
			setBC((short) (getBC() - 1));
			if (getBC() != 0) {
				PC--;
				PC--;
				s += 5;
			}
			dbg("INIR");
			break;
			*/
			throw new RuntimeException("Unsupported instruction");
		case (byte) 0xB3:
			/*
			s += 16;
			out(C, mem.rdByte(getHL())); // OTIR
			setHL((short) (getHL() + 1));
			setBC((short) (getBC() - 1));
			if (getBC() != 0) {
				PC--;
				PC--;
				s += 5;
			}
			dbg("OTIR");
			break;
			*/
			throw new RuntimeException("Unsupported instruction");

		case (byte) 0xB8: // LDDR
			/*
			s += 16;
			mem.wrtByte(getDE(), mem.rdByte(getHL())); 
			setHL((short) (getHL() - 1));
			setDE((short) (getDE() - 1));
			setBC((short) (getBC() - 1));
			if (getBC() != 0) {
				PC--; // B8
				PC--; // ED
				s += 5;
			}
			setHFlag(false); setPVFlag(false); setNFlag(false);
			dbg("LDDR");
			break;
			*/
			throw new RuntimeException("Unsupported instruction");
		case (byte) 0xB9: // CPDR		TODO: suspicious
			boolean tc = getCFlag();
			cp(A, mem.rdByte(getHL())); // CPD
			setHL((short) ((getHL() & 0xffff) - 1));
			setBC((short) ((getBC() & 0xffff) - 1));
			setNFlag(true);
			setCFlag(tc);
			setPVFlag((getBC() & 0xffff) != 0); // bc-1 != 0?
			if (getPVFlag() && !getZFlag()) {
				PC = (short)((PC & 0xffff) - 2);
				s += 5;
			} else {
				s += 0;
			}

		//	s += 16;
		//	c = getCFlag();
		//	cp(A, mem.rdByte(getHL())); 
		//	setHL((short) ((getHL() & 0xffff) - 1));
		//	setBC((short) ((getBC() & 0xffff) - 1));
		//	if ((getBC() & 0xffff) != 0 && getZFlag()) {
		//		PC--;
		//		PC--;
		//		s += 5;
		//	}
		//	setCFlag(c);
		//	setPVFlag(getBC() != 0);

			dbg("CPDR");
			break;
		case (byte) 0xBA:
			/*
			s += 16;
			mem.wrtByte(getHL(), in(C)); // INDR
			setHL((short) (getHL() - 1));
			setBC((short) (getBC() - 1));
			if (getBC() != 0) {
				PC--;
				PC--;
				s += 5;
			}
			dbg("INDR");
			break;
			*/
			throw new RuntimeException("Unsupported instruction");
		case (byte) 0xBB:
			/* s += 16;
			out(C, mem.rdByte(getHL())); // OTDR
			setHL((short) (getHL() - 1));
			setBC((short) (getBC() - 1));
			if (getBC() != 0) {
				PC--;
				PC--;
				s += 5;
			}
			dbg("OTDR");
			break;
			*/
			throw new RuntimeException("Unsupported instruction");
		case (byte) 0x4C: // neg (TODO: correct?)
		case (byte) 0x5C: 
		case (byte) 0x6C: 
		case (byte) 0x7C: 
		case (byte) 0x54: 
		case (byte) 0x64: 
			throw new RuntimeException("Unsupported instruction");
		case (byte) 0x74: 
			s += 8;
			A = sub((byte)0, A, false);
			break;
		}
	}

	private void doRLD() {
		byte AL = Bit.and(A, (byte)0x0f);
		byte HLC = mem.rdByte(getHL());
		A = Bit.or(Bit.and(A, (byte)0xF0), Bit.rshift(HLC, 4));
		HLC = Bit.lshift(HLC, 4);
		HLC = Bit.or(HLC, AL);
		mem.wrtByte(getHL(), HLC);
		setSFlag(A < 0);
		setZFlag(A == 0);
		setHFlag(false);
		setPVFlag(Tools.getParity(A));
		setNFlag(false);
	}

	private void doRRD() {
		byte AL = Bit.and(A, (byte)0x0f);
		byte HLC = mem.rdByte(getHL());
		A = Bit.or(Bit.and(A, (byte)0xF0), Bit.and(HLC, (byte)0x0F));
		HLC = Bit.rshift(HLC, 4);
		HLC = Bit.or(HLC, Bit.lshift(AL, 4));
		mem.wrtByte(getHL(), HLC);
		setSFlag(A < 0);
		setZFlag(A == 0);
		setHFlag(false);
		setPVFlag(Tools.getParity(A));
		setNFlag(false);
	}
	
	// Set flag values after IN r (C) operation
	private void setInFlags(byte val) {
		setSFlag(val < 0);
		setZFlag(val == 0);
		setHFlag(false);
		setPVFlag(Tools.getParity(val));
		setNFlag(false);
	}

	//private final short readIXNN() {
	//	return (short) (getIX() + fetchByte());
//	}

	private final short fetchWordLH() {
		short value = (short) mem.readWordLH(PC);
		PC++;
		PC++;
		return value;
	}

	private final byte fetchByte() {
		return mem.rdByte(PC++);
	}

	/**
	 * Get register pair values
	 */
	public final short getBC() {
		return (short) ((C & 0xff) | (B << 8));
	}

	public final short getDE() {
		return (short) ((E & 0xff) | (D << 8));
	}

	public final short getSP() {
		return (short) ((P & 0xff) | (S << 8));
	}

	public final short getAF() {
		return (short) ((F & 0xff) | (A << 8));
	}

	public final short getHL() {
		return (short) ((L & 0xff) | (H << 8));
	}

	public final short getIX() {
		return (short) ((IXL & 0xff) | (IXH << 8));
	}

	public final short getIY() {
		return (short) ((IYL & 0xff) | (IYH << 8));
	}


	private int rShift(short val, int i) {
		return (val & 0xffff) >> i;
	}
	
	private int rShift(byte val, int i) {
		return (val & 0xff) >> i;
	}

	/**
	 * Set register pair values
	 */
	public final void setBC(short val) {
		B = (byte) ((rShift(val, 8)) & 0xff);
		C = (byte) (val & 0xff);
	}

	public final void setDE(short val) {
		D = (byte) ((rShift(val, 8)) & 0xff);
		E = (byte) (val & 0xff);
	}

	public final void setSP(short val) {
		S = (byte) ((rShift(val, 8)) & 0xff);
		P = (byte) (val & 0xff);
	}

	public final void setAF(short val) {
		A = (byte) ((rShift(val, 8)) & 0xff);
		F = (byte) (val & 0xff);
	}

	public final void setHL(short val) {
		H = (byte) ((rShift(val, 8)) & 0xff);
		L = (byte) (val & 0xff);
	}

	public final void setIX(short val) {
		IXH = (byte) ((rShift(val, 8)) & 0xff);
		IXL = (byte) (val & 0xff);
	}

	public final void setIY(short val) {
		IYH = (byte) ((rShift(val, 8)) & 0xff);
		IYL = (byte) (val & 0xff);
	}

	/**
	 * Set flag values
	 */
	public final void setSFlag(boolean v) {
		F = v ? (byte) (F | 0b10000000) : (byte) (F & 0b01111111);
	}

	public final void setZFlag(boolean v) {
		F = v ? (byte) (F | 0b01000000) : (byte) (F & 0b10111111);
	}

	public final void setX1Flag(boolean v) {
		F = v ? (byte) (F | 0b00100000) : (byte) (F & 0b11011111);
	}

	public final void setHFlag(boolean v) {
		F = v ? (byte) (F | 0b00010000) : (byte) (F & 0b11101111);
	}

	public final void setX2Flag(boolean v) {
		F = v ? (byte) (F | 0b00001000) : (byte) (F & 0b11110111);
	}

	public final void setPVFlag(boolean v) {
		F = v ? (byte) (F | 0b00000100) : (byte) (F & 0b11111011);
	}

	public final void setNFlag(boolean v) {
		F = v ? (byte) (F | 0b00000010) : (byte) (F & 0b11111101);
	}

	public final void setCFlag(boolean v) {
		F = v ? (byte) (F | 0b00000001) : (byte) (F & 0b11111110);
	}

	/**
	 * Get flag values
	 */
	public final boolean getSFlag() {
		return (F & 0b10000000) != 0;
	}

	public final boolean getZFlag() {
		return (F & 0b01000000) != 0;
	}

	public final boolean getX1Flag() {
		return (F & 0b00100000) != 0;
	}

	public final boolean getHFlag() {
		return (F & 0b00010000) != 0;
	}

	public final boolean getX2Flag() {
		return (F & 0b00001000) != 0;
	}

	public final boolean getPVFlag() {
		return (F & 0b00000100) != 0;
	}

	public final boolean getNFlag() {
		return (F & 0b00000010) != 0;
	}

	public final boolean getCFlag() {
		return (F & 0b00000001) != 0;
	}

	/**
	 * Do 8 bit addition (sets S, Z, H, PV, N and C flags as prescribed).
	 * 
	 * @param a
	 *            First operand
	 * @param b
	 *            Second operand
	 * @param useCarry
	 *            Use carry flag.
	 * @return
	 */
	public final byte add(byte a, byte b, boolean useCarry) {
		int carry = (useCarry && getCFlag()?1: 0);
		setHFlag((a & 0x0F) + (b & 0x0F) + carry > 0x0F);
		int res = a + b + carry;
		setPVFlag(res < -128 || res > 127);
		res = ((a & 0xFF) + (b & 0xFF)) + carry; // as unsigned value
		setSFlag((res & 0x0080) != 0);
		setCFlag((res & 0xFF00) != 0);
		res = res & 0xFF;
		setZFlag(res == 0);
		setNFlag(false);
		return (byte)res;
	}

	/**
	 * Do 16 bit addition (sets H, N, C flags as prescribed).
	 * 
	 * @param a
	 *            First operand
	 * @param b
	 *            Second operand
	 * @param useCarry
	 *            Use carry flag.
	 * @return
	 */
	public final short add(short a, short b, boolean useCarry) {
		boolean sf = getSFlag(), zf = getZFlag(), pvf = getPVFlag();
		byte res_lsb = add(Tools.getLSB(a), Tools.getLSB(b), useCarry);
		byte res_msb = add(Tools.getMSB(a), Tools.getMSB(b), true);
		if (!useCarry) {
			setSFlag(sf);
			setZFlag(zf);
			setPVFlag(pvf);
		} else {
			setZFlag(res_lsb == 0 && res_msb == 0);
		}
		return Tools.combine(res_lsb, res_msb);
	}

	/**
	 * Do 16 bit substraction (sets H, N, C flags as prescribed).
	 * 
	 * @param a
	 *            First operand
	 * @param b
	 *            Second operand
	 * @param useCarry
	 *            Use carry flag.
	 * @return
	 */
	public final short sub(short a, short b, boolean useCarry) {
		byte res_lsb = sub(Tools.getLSB(a), Tools.getLSB(b), useCarry);
		byte res_msb = sub(Tools.getMSB(a), Tools.getMSB(b), true);
		setZFlag(res_lsb == 0 && res_msb == 0);
		return Tools.combine(res_lsb, res_msb);
	}

	public final void doDAA() {

		// This provides a simple pattern for the instruction:
		// if the lower 4 bits form a number greater than 9 or H is set, add $06
		// to the accumulator
		// if the upper 4 bits form a number greater than 9 or C is set, add $60
		// to the accumulator

		int l = A & 0x0F;
		if (l > 9 || getHFlag()) {
			if (getNFlag()) {
				A = (byte) (A - 0x06);
			} else {
				A = (byte) (A + 0x06);
			}
		}
		int h = (A & 0xF0) >> 4;
		if (h > 9 || getCFlag()) {
			if (getNFlag()) {
				A = (byte) (A - 0x60);
			} else {
				A = (byte) (A + 0x60);
			}
			setCFlag(true);
		} else {
			setCFlag(false);
		}
		
		setPVFlag(Tools.getParity(A));

		// A = 00, carry = true
		
		//dbg("* DAA : " + Tools.toHexString(bs) + " > " + Tools.toHexString(A));

	}

	/**
	 * Do subtraction (sets S, Z, H, PV, N and C flags as prescribed).
	 * 
	 * @param a
	 *            First operand
	 * @param b
	 *            Second operand
	 * @param useCarry
	 *            Use carry flag.
	 * @return
	 */
	public final byte sub(byte a, byte b, boolean useCarry) {
		int carry = (useCarry && getCFlag()?1: 0);
		setHFlag((a & 0x0F) < (b & 0x0F) + carry);
		int res = a - b - carry; // as signed value
		setPVFlag(res < -128 || res > 127);
		res = ((a & 0xFF) - (b & 0xFF)) - carry; // as unsigned value
		setSFlag((res & 0x0080) != 0);
		setCFlag((res & 0xFF00) != 0);
		res = res & 0x00FF;
		setZFlag(res == 0);
		setNFlag(true);
		return (byte)res; // return truncated sum
	}

	public final byte and(byte a, byte b) {
		a = (byte) (a & b);
		setSFlag(a < 0);
		setZFlag(a == 0);
		setHFlag(true);
		setPVFlag(Tools.getParity(a));
		setNFlag(false);
		setCFlag(false);
		//dbg("* AND " + Tools.toHexString(bs) + ", " + Tools.toHexString(b) + " > " + a);
		return a;
	}

	public final byte or(byte a, byte b) {
		byte bs = a;
		a = (byte) (a | b);
		setSFlag(a < 0);
		setZFlag(a == 0);
		setHFlag(false);
		setPVFlag(Tools.getParity(a));
		setNFlag(false);
		setCFlag(false);
		//dbg("* OR " + Tools.toHexString(bs) + ", " + Tools.toHexString(b) + " > " + a);
		return a;
	}

	public final void xor(byte b) {
		A = (byte) ((A & 0xff) ^ (b & 0xff));
		setSFlag(A < 0);
		setZFlag(A == 0);
		setHFlag(false);
		setPVFlag(Tools.getParity(A));
		setNFlag(false);
		setCFlag(false);
		//dbg("* XOR " + Tools.toHexString(bs) + ", " + Tools.toHexString(b) + " > " + a);
	}

	// RLCA: rotate A left circular (put rotated bit in carry)
	public void doRLCA() {
		A = (byte) (A << 1 | (rShift(A, 7) & 1));
		setHFlag(false);
		setNFlag(false);
		setCFlag((A % 2) != 0);
	}

	// RLA: (rotate A left through carry)
	public void doRLA() {
		boolean nc = A < 0;
		A = (byte) (A << 1 | (getCFlag()? 1: 0));
		setHFlag(false);
		setNFlag(false);
		setCFlag(nc);
	}
	
	// RRCA: rotate A right circular (put rotated bit in carry)
	public void doRRCA() { // TODO: test
		boolean c = (A % 2) != 0;
		setCFlag(c);
		A = (byte) ((rShift(A, 1) & 0b01111111) | (c? 0b10000000: 0));
		setHFlag(false);
		setNFlag(false);
	}
	
	// RRA: rotate A right through carry
	public void doRRA() {
		boolean nc = A % 2 != 0;
		A = (byte) (rShift(A, 1) | (getCFlag()? 0b10000000: 0));
		setHFlag(false);
		setNFlag(false);
		setCFlag(nc);
	}
	
	// rotate left circular (put rotated bit in carry)
	public byte doRLC(byte val) {
		val = (byte) (val << 1 | (rShift(val, 7) & 1));
		setSFlag(val < 0);
		setZFlag(val == 0);
		setHFlag(false);
		setPVFlag(Tools.getParity(val));
		setNFlag(false);
		setCFlag((val % 2) != 0);
		return val;
	}
	
	// rotate right circular (put rotated bit in carry)
	public byte doRRC(byte val) { // TODO: test
		boolean c = (val % 2) != 0;
		val = (byte) (((rShift(val, 1)) & 0b01111111) | (c? 0b10000000: 0));
		setSFlag(val < 0);
		setZFlag(val == 0);
		setHFlag(false);
		setPVFlag(Tools.getParity(val));
		setNFlag(false);
		setCFlag(c);
		return val;
	}

	// RL (rotate left through carry)
	public byte doRL(byte val) {
		boolean nc = val < 0;
		if (getCFlag()) {
			val = (byte) (val << 1 | 0b00000001);
		} else {
			val = (byte) (val << 1 & 0b11111110);
		}
		setSFlag(val < 0);
		setZFlag(val == 0);
		setHFlag(false);
		setPVFlag(Tools.getParity(val));
		setNFlag(false);
		setCFlag(nc);
		return val;
	}

	// rotate right through carry
	public byte doRR(byte val) {
		boolean nc = val % 2 != 0;
		if (getCFlag()) {
			val = (byte) (rShift(val, 1) | 0b10000000);
		} else {
			val = (byte) (rShift(val, 1) & 0b01111111);
		}
		setSFlag(val < 0);
		setZFlag(val == 0);
		setHFlag(false);
		setPVFlag(Tools.getParity(val));
		setNFlag(false);
		setCFlag(nc);
		return val;
	}

	
	// shift left, put bit 7 in carry, set bit 0 to 0
	public byte doSLA(byte val) {
		setCFlag(val < 0);
		val = (byte) (val << 1);
		setSFlag(val < 0);
		setZFlag(val == 0);
		setHFlag(false);
		setPVFlag(Tools.getParity(val));
		setNFlag(false);
		return val;
	}

	/**
	 * SLL: left shift, bit 0 becomes 1, bit 7 is copied to carry. Side effects
	 * are: s += result is negative Z = result is zero H = 0 PV = parity of
	 * result N = 0 C = carry as described above
	 * 
	 * @param val
	 *            input
	 * @return Result of SLL operation
	 */
	public byte doSLL(byte val) { // TODO: test
		setCFlag(val < 0);
		val = (byte) ((val << 1) | 0b00000001);
		setSFlag(val < 0);
		setZFlag(val == 0);
		setHFlag(false);
		setPVFlag(Tools.getParity(val));
		setNFlag(false);
		return val;
	}

	// shift right, keep bit 7, copy bit 0 to carry
	public byte doSRA(byte val) { // TODO: test
		setCFlag(val % 2 != 0);
		val = (byte)rShift(val, 1);
		setSFlag(val < 0);
		setZFlag(val == 0);
		setHFlag(false);
		setPVFlag(Tools.getParity(val));
		setNFlag(false);
		return val;
	}

	/**
	 * SRL: right shift, bit 7 becomes 0, bit 0 is copied to carry. Side effects
	 * are: s += result is negative Z = result is zero H = 0 PV = parity of
	 * result N = 0 C = carry as described above
	 * 
	 * @param val
	 *            input
	 * @return Result of SRL operation
	 */
	public byte doSRL(byte val) { // TODO: test
		setCFlag((val & 0b0000001) > 0);
		val = (byte) (rShift(val, 1) & 0b01111111);
		setSFlag(val < 0);
		setZFlag(val == 0);
		setHFlag(false);
		setPVFlag(Tools.getParity(val));
		setNFlag(false);
		return val;
	}

	/**
	 * Set bit n of val to value of b. Set Z flag to old value of bit n.
	 * 
	 * @param n
	 *            Bit to set.
	 * @param val
	 *            Input value.
	 * @param b
	 *            Value to set.
	 * @return Input with bit n set to b.
	 */
	public byte setBit(int n, int val, boolean b) {
		return b ? (byte) (val | (1 << n)) : (byte) (val & ~(1 << n));
	}

	/**
	 * Read bit n of value v. Put it in the Z flag.
	 * 
	 * @param n
	 *            Bit to test.
	 * @param val
	 *            Value to test.
	 */
	public void readBit(int n, int val) {
		int f = (1 << n);
		int i = val & f;
		setZFlag(i == 0);
		
//		setZFlag((val & (1 << n)) > 0);
	}

	public final void cp(byte a, byte b) {
		sub(a, b, false);
	}

	/**
	 * Push word and decrement SP.
	 * 
	 * @param word
	 *            Value to push onto stack.
	 */
	public final void pushSP(short word) {
		setSP((short) (getSP() - 1));
		dbg("* Pushing " + Tools.toHexString(word) + " to SP at addr " + Tools.toHexString(getSP()));
		mem.wrtByte(getSP(), Tools.getMSB(word));
		setSP((short) (getSP() - 1));
		mem.wrtByte(getSP(), Tools.getLSB(word));
	}

	/**
	 * Pop word and increment SP.
	 * 
	 * @returns Value to popped from stack.
	 */
	public final short popSP() {
		byte lsb = mem.rdByte(getSP());
		setSP((short) (getSP() + 1));
		byte msb = mem.rdByte(getSP());
		setSP((short) (getSP() + 1));
		dbg("* Popped " + Tools.toHexString(Tools.combine(lsb, msb)) + " from SP at addr " + Tools.toHexString((short)(getSP() - 2)));
		return Tools.combine(lsb, msb);
	}

	/**
	 * Perform flag testing operation encoded by Y parameter.
	 * 
	 * @param y
	 *            The Y parameter.
	 * @return True if condition encoded by Y parameter holds.
	 */
	public boolean testCC(int y) {
		switch (y) {
		case 0:
			return !getZFlag();
		case 1:
			return getZFlag();
		case 2:
			return !getCFlag();
		case 3:
			return getCFlag();
		case 4:
			return !getPVFlag(); // TODO ??
		case 5:
			return getPVFlag(); // TODO ??
		case 6:
			return !getSFlag(); // TODO ??
		case 7:
			return getSFlag(); // TODO ??
		default:
			throw new RuntimeException("Illegal CC index");
		}
	}

	private void out(byte port, byte value) {
		Z80OutDevice dev = out[port & 0xff];
		if (dev != null) {
			dev.out(port, value);
		}
	}

	private byte in(byte port) {
		Z80InDevice dev = in[port & 0xff];
		if (dev != null) {
			return dev.in(port);
		}
		return (byte)0xff;
	}

	public void registerOutDevice(Z80OutDevice d, int port) {
		out[port] = d;
	}

	public void registerInDevice(Z80InDevice d, int port) {
		in[port] = d;
	}
	
	public String getState() {
		StringBuilder sb = new StringBuilder();
		sb.append(" PC   A  SZHPNC I  IFF BC   DE   HL   A'F' B'C' D'E' H'L' IX   IY   SP   \n");
		sb.append(" " + Tools.toHexString(PC) + " ");
		sb.append(Tools.toHexString(A) + " ");
		sb.append(getSFlag()?"1":"0");
		sb.append(getZFlag()?"1":"0");
		sb.append(getHFlag()?"1":"0");
		sb.append(getPVFlag()?"1":"0");
		sb.append(getNFlag()?"1":"0");
		sb.append(getCFlag()?"1 ":"0 ");
		sb.append("-- ");
		sb.append("--  ");
		sb.append(Tools.toHexString(getBC()) + " ");
		sb.append(Tools.toHexString(getDE()) + " ");
		sb.append(Tools.toHexString(getHL()) + " ");
		sb.append("---- ");
		sb.append("---- ");
		sb.append("---- ");
		sb.append("---- ");
		sb.append(Tools.toHexString(getIX()) + " ");
		sb.append(Tools.toHexString(getIY()) + " ");
		sb.append(Tools.toHexString(getSP()) + " \n"); 
		return sb.toString();
	}
	
	public void printState() {
		System.out.print(getState() + "\n\n");
	}
	
	public boolean isHalted() {
		return halt;
	}
	
	public final void doEI() {
		
		/* Set flags */
		eff1 = true; eff2 = true;

		/* Interrupts will be enabled after next instruction */
		interruptDelayCount = 2;
	}

	public final void doDI() {
		eff1 = false; eff2 = false;
	}
	
	public final void doIM(int mode) {
		if (mode != 1) {
			throw new RuntimeException("Unsupported interrupt mode " + mode);
		}
	}
	
	public final void doRETI() {
		eff1 = eff2;
		PC = popSP();
	}

	public final void doRETN() {
		eff1 = eff2;
		PC = popSP();
	}

	public void interrupt() {

		/* Ignore if interrupt disabled */
		if (!eff1) return;
		
		/* Ignore if interrupt delay has not yet passed */
		if (interruptDelayCount > 0) return;
		
		/* Reset IFF1, IFF2 */
		eff1 = false;
		eff2 = false;
		
		/* Push PC */
		pushSP(PC);
			
		/* Call 0x38 */
		PC = INTERRUPT_HOOK;
	}
	
	public final int getCycleCount() {
		return s;
	}
	
	public final void setCycleCount(int s) {
		this.s = s;
	}

	public short getPC() {
		return PC;
	}
	
	public ArrayList<String> getStateBuf() {
		ArrayList<String> res = new ArrayList<String>();
		for (int i = 0; i < stateBufSize; i++) res.add(stateBuf[(stateBufPtr + i) % stateBufSize]);
		return res;
	}
}
