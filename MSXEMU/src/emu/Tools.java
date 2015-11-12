package emu;

public class Tools {

	public static final String toBinString(byte b) {
		String res = "";
		for (int i = 0; i < 8; i++) {
			res = (((b % 2) != 0)? "1": "0") + res;
			b >>= 1;
		}
		return res;
	}
	
	public static final String toBinString(short s) {
		String res = "";
		for (int i = 0; i < 16; i++) {
			res = (((s % 2) != 0)? "1": "0") + res;
			s >>= 1;
		}
		return res;	
	}

	public static final byte getMSB(short s) {
		return (byte) ((s >> 8) & 0xff); 
	}

	public static final byte getLSB(short s) {
		return (byte) s;
	}
	
	public static final short combine(byte lsb, byte msb) {
		int s1 = (msb & 0xff);
		int s2 = s1 << 8;
		int s3 = s2 | (lsb & 0xff);
		short s = (short)s3;
		return s;
	}
	
	/**
	 * Compute parity (true iff input contains even number of 1s)
	 * 
	 * @return Parity of input
	 */
	public static final boolean getParity(byte input) {
		final int i = input & 0xff;
		int parity = i ^ ( i >> 4 );
		parity ^= parity >> 2;
		parity ^= parity >> 1;
		return (parity & 1) == 0;
	}
	
	
	public static void main(String[] args) {
	
		short val = (short)0;
		
		System.out.println("val: " + val);
		System.out.println("val: " + toBinString(val));

		int i = 0;
		while(true) {
			i = i - 1;
			val = (short) (val - 1);
			System.out.println("val: " + val);
			System.out.println("val: " + toBinString(val));
			if (val == 0) {
				System.out.println("i: " + i);
				break;
			}
		} 
	}

	public static byte setBit(byte in, int bit, boolean v) {
		return v? (byte)(in | (1 << bit)): (byte)(in & ((~1) << bit));
	}

	public static boolean getBit(byte in, int bit) {
		return (in & (1 << bit)) != 0;
	}

	public static String toHexString(short v) {
		String s = Integer.toHexString(v & 0xffff);
		while (s.length() < 4) s = "0" + s;
		return s;
	}
	
	public static String toHexString(byte v) {
		String s = Integer.toHexString(v & 0xff);
		while (s.length() < 2) s = "0" + s;
		return s;
	}

	public static String toHexString2(int v) {
		String s = Integer.toHexString(v & 0xff);
		while (s.length() < 2) s = "0" + s;
		return s;
	}

	public static String toHexString4(int v) {
		String s = Integer.toHexString(v & 0xffff);
		while (s.length() < 4) s = "0" + s;
		return s;
	}

}
