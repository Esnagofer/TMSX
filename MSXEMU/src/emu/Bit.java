package emu;

public class Bit {

	public static final byte and(byte a, byte b) {
		return (byte) (a & b);
	}
	
	public static final byte or(byte a, byte b) {
		return (byte) (a | b);
	}

	public static final byte rshift(byte a, int n) {
		return (byte) ((a & 0xff) >> n);
	}
	
	public static final byte lshift(byte a, int n) {
		return (byte) ((a & 0xff) << n);
	}

	public static final short and(short a, short b) {
		return (short) (a & b);
	}
	
	public static final short or(short a, short b) {
		return (short) (a | b);
	}

	public static final short rshift(short a, int n) {
		return (short) ((a & 0xffff) >> n);
	}
	
	public static final short lshift(short a, int n) {
		return (short) ((a & 0xffff) << n);
	}

	public static byte invert(byte value) {
		return (byte)~value;
	}

	
	public static void main(String[] args) {
		byte b = (byte)0xff;
		System.out.println(b);
		b--;
		System.out.println(b);
		System.out.println(b & 0xff);
		System.out.println(Tools.toHexString(b));
		
	}
}
