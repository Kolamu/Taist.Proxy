package com.taist.helper;

public final class ByteHelper {
	public static byte[] fromInt16(int number) {
		return fromInt(number, 2);
	}
	
	public static byte[] fromInt24(int number) {
		return fromInt(number, 3);
	}
	
	public static byte[] fromInt32(int number) {
		return fromInt(number, 4);
	}
	
	public static byte[] fromInt(int number, int length) {
		byte[] bs = new byte[length];
		for(int i=0;i<length;i++) {
			bs[i] = (byte)((number >> (length - i - 1) * 8) & 0xff);
		}
		return bs;
	}
	
	public static int toInt(byte[] bs) {
		int length = bs.length;
		if(length > 4) {
			length = 4;
		}
		int result = 0;
		for(int i=0;i<length;i++) {
			result |= ((bs[i] & 0xff) << ((length - i - 1) * 8));
		}
		return result;
	}
	
	public static int toInt(byte[] bs, int offset, int length) {
		return toInt(subarray(bs, offset, length));
	}
	
	public static byte[] subarray(byte[] bs, int offset, int length) {
		if(bs.length < offset) {
			System.out.println("长度过短 " + bs.length + " < " + offset);
			return null;
		}
		if(bs.length < offset + length) {
			System.out.println(bs.length + " " + offset + " + " + length);
			length = bs.length - offset;
			try {
				throw new Exception();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		byte[] b = new byte[length];
		System.arraycopy(bs, offset, b, 0, length);
		return b;
	}
	
	public static byte[] concat(byte[] b0, byte[] b1) {
		byte[] r = new byte[b0.length + b1.length];
		System.arraycopy(b0, 0, r, 0, b0.length);
		System.arraycopy(b1, 0, r, b0.length, b1.length);
		return r;
	}

	private static final String hexCode = "0123456789ABCDEF";
	public static String toString(byte[] b) {
		StringBuffer buf = new StringBuffer();
        for (int n = 0; n < b.length; n++) {
        	if(n % 16 == 0) {
        		buf.append("\r\n");
        	}
        	else {
                buf.append(" ");
        	}
            int v = b[n] & 0XFF;
            buf.append(hexCode.charAt((v>>4) & 0xF));
            buf.append(hexCode.charAt(v & 0xF));
            
        }  
        return buf.toString();
	}
}
