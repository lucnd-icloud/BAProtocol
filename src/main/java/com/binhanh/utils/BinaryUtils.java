package com.binhanh.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;

public class BinaryUtils {
	
	/**
	 * sắp xếp mảng byte vào mảng byte lớn
	 * @param src
	 * @param dst
	 * @param offset
	 */
	public static void putBytes(byte[] src, byte[] dst, int offset){
		System.arraycopy(dst, 0, src, offset, dst.length);
		offset += dst.length;
	}
	
	/**
	 * Lấy mảng byte con trong mảng byte với chiều dài chỉ rõ
	 * @param src
	 * @param offset
	 * @param length
	 * @return
	 */
	public static byte[] getBytes(byte[] src, int offset, int length){
		if(length > src.length - offset){
			throw new IndexOutOfBoundsException();
		}
		
		byte[] dst = new byte[length];
		System.arraycopy(src, offset, dst, 0, length);
		return dst;
	}
	
	/**
	 * Tính giá trị 2 byte
	 * 
	 * @param src
	 * @param offset
	 * @return
	 */
	public static short getShort(byte[] src, int offset) {
		return (short) (((src[offset + 1] << 8) & 0xff00) | (src[offset] & 0xff));
	}

	/**
	 * Lưu một số kiểu short vào 2 byte trong mảng byte
	 * 
	 * @param src
	 * @param value
	 * @param offset
	 */
	public static void putShort(byte[] src, short value, int offset) {
		src[offset] = (byte) (value & 0xff);
		src[offset + 1] = (byte) ((value & 0xff00) >> 8);
	}

	/**
	 * Lấy giá trị số Long từ 8 byte trong mảng
	 * 
	 * @param bytes
	 * @param offset
	 * @return
	 */
	public static long getLong(byte[] bytes, int offset) {
		long value = 0;
		long l4byte = 0;
		long m4byte = 0;
		for (int i = 7; i > 3; i--) {
			m4byte += (long) ((bytes[offset + i] & 0xff) << (8 * (i - 4)));
			// Log.d(GnavContstants.TAG, " i = " + i + " -- bit move =" + (8 *
			// (i - offset - 4)) + " -- byte[i] = " + bytes[i] + " -- delta = "
			// + ((bytes[i] & 0xff) << (8 * (i - offset - 4))) + " -- m4byte = "
			// + m4byte) ;
		}

		for (int i = 3; i > -1; i--) {
			l4byte += (long) ((bytes[offset + i] & 0xff) << (8 * i));
			// Log.d(GnavContstants.TAG, " i = " + i + " -- bit move =" + (8 *
			// (i - offset)) + " -- byte[i] = " + bytes[i] + " -- delta = " +
			// ((bytes[i] & 0xff) << (8 * (i - offset))) + " -- l4byte = " +
			// l4byte) ;
		}
		// Log.d(GnavContstants.TAG, " m4byte * 0xffffffff = " + (long)m4byte *
		// (long)0xffffffff);
		value = (long) (m4byte << 32) + (long) l4byte;
		return value;
	}

	/**
	 * Lưu giá trị số long vào mảng byte
	 * 
	 * @param src
	 * @param value
	 * @param offset
	 */
	public static void putLong(byte[] src, long value, int offset) {
		for (int i = 7; i >= 0; i--) {
			src[offset + i] = (byte) ((value >> (8 * i) & 0x00ff));
		}
	}

	/**
	 * Lấy giá trị số Int từ 4 byte trong mảng
	 * 
	 * @param bytes
	 * @param offset
	 * @return
	 */
	public static int getInt(byte[] bytes, int offset) {
		int value = 0;
		for (int i = 3; i > -1; i--) {
			value += (bytes[offset + i] & 0xff) << (8 * i);
		}
		return value;
	}

	/**
	 * Lưu giá trị số Integer vào mảng byte
	 * 
	 * @param src
	 * @param value
	 * @param offset
	 */
	public static void putInt(byte[] src, int value, int offset) {
		for (int i = 3; i >= 0; i--) {
			src[offset + i] = (byte) ((value >> (8 * i) & 0x00ff));
		}
	}

	/**
	 * đẩy 1 chuỗi vào 1 mảng có sẵn
	 * 
	 * @param src
	 * @param value
	 * @param offset
	 */
	public static void putString(byte[] src, String value, int offset) {
		System.arraycopy(value.getBytes(), 0, src, offset, value.getBytes().length);
	}

	/**
	 * Lấy chuỗi trong mảng
	 * 
	 * @param src
	 *            : Mảng byte chứa chuỗi
	 * @param offset
	 *            : vị trí đầu tiên của cần lấy
	 * @param length
	 *            : độ dài của cuỗi
	 * @return
	 */
	public static String getString(byte[] src, int offset, int length) {
		byte[] value = new byte[length];
		System.arraycopy(src, offset, value, 0, length);
		return new String(value);
	}
	
	public static String logBinary(byte[] b) {
		String s = "";
		s += "{" + (b[0] & 0xff);
		for (int i = 1; i < b.length - 1; i++) {
			s += ", " + (b[i] & 0xff);
		}
		s += ", " + (b[b.length - 1] & 0xff) + " };";

		return s;
	}

   
    
	/**
	 * đẩy 1 giá trị float vào mảng byte
	 * @param src
	 * @param offset
	 * @param value
	 */
	public static void putFloat(byte[] src, int offset, float value) {
		byte byteArray[] = new byte[4];

		// wrap the byte array to the byte buffer
		ByteBuffer byteBuf = ByteBuffer.wrap(byteArray);
		byteBuf.order(ByteOrder.LITTLE_ENDIAN);

		// create a view of the byte buffer as a float buffer
		FloatBuffer floatBuf = byteBuf.asFloatBuffer();

		// now put the float array to the float buffer,
		// it is actually stored to the byte array
		floatBuf.put(value);

		System.arraycopy(byteArray, 0, src, offset, 4);
	}

	/**
	 * Lấy giá trị float trong mảng byte
	 * @param src
	 * @param offset
	 * @return
	 */
	public static float getFloat(byte[] src, int offset) {
		ByteBuffer bb = ByteBuffer.wrap(src, offset, 4);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.getFloat();
	}
	
	/**
	 * đẩy 1 giá trị double vào mảng byte
	 * @param src
	 * @param offset
	 * @param value
	 */

	public static void putDouble(byte[] src, int offset, double value) {
		byte byteArray[] = new byte[8];

		// wrap the byte array to the byte buffer
		ByteBuffer byteBuf = ByteBuffer.wrap(byteArray);
		byteBuf.order(ByteOrder.LITTLE_ENDIAN);

		// create a view of the byte buffer as a float buffer
		DoubleBuffer doubleBuf = byteBuf.asDoubleBuffer();

		// now put the float array to the float buffer,
		// it is actually stored to the byte array
		doubleBuf.put(value);

		System.arraycopy(byteArray, 0, src, offset, 8);
	}

	/**
	 * Lấy giá trị double trong mảng byte
	 * @param src
	 * @param offset
	 * @return
	 */
	public static double getDouble(byte[] src, int offset) {
		ByteBuffer bb = ByteBuffer.wrap(src, offset, 8);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.getDouble();
	}

	/**
	 * Hàm này dùng để validate toàn bộ mảng byte khi server nhận dữ liệu từ
	 * client
	 */
	public static byte getCheckNumber(byte[] buffer) {
		int result = 0;
		if (buffer != null) {
			for (int i = 0; i < buffer.length - 1; i++) {
				result += buffer[i];
			}
		}
		result &= 0xff;
		return (byte) (result &= 0xff);
	}
	
}
