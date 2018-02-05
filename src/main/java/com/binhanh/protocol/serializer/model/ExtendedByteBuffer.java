package com.binhanh.protocol.serializer.model;

import com.google.android.gms.maps.model.LatLng;

import java.nio.charset.Charset;

public class ExtendedByteBuffer {

	private static final int CAPACITY_SIZE = 1024;
	private static final String CHARSET = "UTF-8";
	
	private byte[] mBuffer;
	private int mOffset;

	private ExtendedByteBuffer(byte[] buffer, int offset) {
		this.mBuffer = buffer;
		this.mOffset = offset;
	}

	/**
	 * Cấp phát 1 bộ đệm các byte với kích thước được chỉ rõ
	 * 
	 * @param capacity
	 * @return
	 */
	public static ExtendedByteBuffer allocate(int capacity) {
		if (capacity < 0) {
			capacity = 0;
		}
		return new ExtendedByteBuffer(new byte[capacity], 0);
	}

	/**
	 * câp phát vùng nhớ cố định
	 * 
	 * @return
	 */
	public static ExtendedByteBuffer allocate() {
		return allocate(CAPACITY_SIZE);
	}

	/**
	 * Chèn mảng byte tới buffer
	 * 
	 * @param array
	 * @return
	 */
	public static ExtendedByteBuffer wrap(byte[] array) {
		try {
			return new ExtendedByteBuffer(array, 0);
		} catch (IllegalArgumentException x) {
			throw new IndexOutOfBoundsException();
		}
	}

	/**
	 * sắp xếp mảng byte vào mảng byte lớn
	 * 
	 * @param dst
	 */
	public void putByte(byte dst) {
		appendCapacity(1);
		mBuffer[mOffset] = dst;
		moveTo(1);
	}
	
	public void putBoolean(boolean dst) {
		int b = dst?1:0;
		putByte((byte)b);
	}

	/**
	 * Lấy mảng byte con trong mảng byte với chiều dài chỉ rõ
	 * 
	 * @return
	 */
	public byte getByte() {
		appendCapacity(1);
		byte value = mBuffer[mOffset];
		moveTo(1);
		return value;
	}
	
	public boolean getBoolean() {
		return getByte() > 0;
	}

	/**
	 * sắp xếp mảng byte vào mảng byte lớn
	 * 
	 * @param dst
	 */
	public void putBytes(byte[] dst) {
		// nếu không có dữ liệu để chèn thì hủy
		if (dst == null || dst.length == 0)
			return;
		int len = dst.length;
		appendCapacity(len);
		System.arraycopy(dst, 0, mBuffer, mOffset, len);
		moveTo(len);
	}
	
	public void putBytes(byte[] dst, int len) {
		// nếu không có dữ liệu để chèn thì hủy
		if (dst == null 
				|| dst.length == 0 
				|| dst.length < len){
			return;
		}
			
		appendCapacity(len);
		System.arraycopy(dst, 0, mBuffer, mOffset, len);
		moveTo(len);
	}

	/**
	 * Lấy mảng byte con trong mảng byte với chiều dài chỉ rõ
	 * 
	 * @param len
	 * @return
	 */
	public byte[] getBytes(int len) {
		if (len <= 0)
			return null;
		appendCapacity(len);
		byte[] dst = new byte[len];
		System.arraycopy(mBuffer, mOffset, dst, 0, len);
		moveTo(len);
		return dst;
	}
	
	public <T> void putShortBytes(T value) {
		if (value != null && value instanceof byte[]) {
			byte[] bs = (byte[])value;
			putShort((short) bs.length);
			putBytes(bs);
		}else{
			putShort((short) 0);
		}
	}
	
	public <T> byte[] getShortBytes() {
		int length = getShort();
		return getBytes(length);
	}
	
	/**
	 * Lấy mảng byte từ cuối mảng
	 * @return
	 */
	public byte[] getLastBytes() {
		int len = mBuffer.length - (mOffset);
		return getBytes(len);
	}

	/**
	 * Tính giá trị 2 byte
	 * 
	 * @return
	 */
	public short getShort() {
		appendCapacity(2);
		short value = (short) (((mBuffer[mOffset + 1] << 8) & 0xff00) | (mBuffer[mOffset] & 0xff));
		moveTo(2);
		return value;
	}
	/**
	 * Lưu một số kiểu short vào 2 byte trong mảng byte
	 * 
	 * @param value
	 */
	public void putShort(short value) {
		appendCapacity(2);
		mBuffer[mOffset] = (byte) (value & 0xff);
		mBuffer[mOffset + 1] = (byte) ((value & 0xff00) >> 8);
		moveTo(2);
	}
	/**
	 * Lấy giá trị số Long từ 8 byte trong mảng
	 * 
	 * @return
	 */
	public long getLong() {
		appendCapacity(8);
		
		
		//lấy giá trị của 4 byte cao
		long m4byte = 0;
		for (int i = 7; i > 3; i--) {
			m4byte += (long) ((mBuffer[mOffset + i] & 0xff) << (8 * (i - 4)));
		}

		//lấy giá trị của 4 byte thấp
		long l4byte = 0;
		for (int i = 3; i > -1; i--) {
			l4byte += (long) ((mBuffer[mOffset + i] & 0xff) << (8 * i));
		}
		moveTo(8);
		
		return (long) (m4byte << 32) + (long) l4byte;
	}

	/**
	 * Lưu giá trị số long vào mảng byte
	 * 
	 * @param value
	 */
	public void putLong(long value) {
		appendCapacity(8);
		for (int i = 7; i >= 0; i--) {
			mBuffer[mOffset + i] = (byte) ((value >> (8 * i) & 0x00ff));
		}
		moveTo(8);
	}

	/**
	 * Lấy giá trị số Int từ 4 byte trong mảng
	 * 
	 * @return
	 */
	public int getInt() {
		appendCapacity(4);
		int value = 0;
		for (int i = 3; i > -1; i--) {
			value += (mBuffer[mOffset + i] & 0xff) << (8 * i);
		}
		moveTo(4);
		return value;
	}

	/**
	 * Lưu giá trị số Integer vào mảng byte
	 * 
	 * @param value
	 */
	public void putInt(int value) {
		appendCapacity(4);
		for (int i = 3; i >= 0; i--) {
			mBuffer[mOffset + i] = (byte) ((value >> (8 * i) & 0x00ff));
		}
		moveTo(4);
	}

	/**
	 * đẩy 1 chuỗi vào 1 mảng có sẵn
	 * 
	 * @param value
	 */
	public void putString(String value) {
		if (value == null || value.isEmpty())
			return;
		putBytes(value.getBytes(getCharset()));
	}

	/**
	 * đấy một chuỗi string vào mảng byte.
	 * chuỗi này được chèn thành 2 phần:
	 * phần 1: 2 byte đầu lưu trữ chiều dài của chuỗi
	 * phần 2: là mảng byte của cuỗi
	 * @param value
	 */
	public <T> void putShortString(T value) {
		if (value != null && value instanceof String) {
			String str = (String)value;
			byte[] bs = str.getBytes(getCharset());
			putShort((short) bs.length);
			putBytes(bs);
			
		}else{
			putShort((short) 0);
		}
	}

	/**
	 * Lấy chuỗi trong mảng
	 * 
	 * @param length
	 *            : độ dài của cuỗi
	 * @return
	 */
	public String getString(int length) {
		if (length == 0)
			return "";
		byte[] value = getBytes(length);
		
		if(value == null) return "";
		
		return new String(value, getCharset());
	}

	public String getShortString() {
		int length = getShort();
		return getString(length);
	}

	@Override
	public String toString() {
		String s = "";
		s += "{" + (mBuffer[0] & 0xff);
		for (int i = 1; i < mBuffer.length - 1; i++) {
			s += ", " + (mBuffer[i] & 0xff);
		}
		s += ", " + (mBuffer[mBuffer.length - 1] & 0xff) + " };";

		return s;
	}

	/**
	 * đẩy 1 giá trị float vào mảng byte
	 * 
	 * @param value
	 */

	public void putFloat(float value) {
		putInt(Float.floatToIntBits(value));
	}

	/**
	 * Lấy giá trị float trong mảng byte
	 * 
	 * @return
	 */
	public float getFloat() {
		return Float.intBitsToFloat(getInt());
	}

	/**
	 * đẩy 1 giá trị double vào mảng byte
	 * 
	 * @param value
	 */

	public void putDouble(double value) {
		putLong(Double.doubleToLongBits(value));
	}

	/**
	 * Lấy giá trị double trong mảng byte
	 * 
	 * @return
	 */
	public double getDouble() {
		return Double.longBitsToDouble(getLong());
	}
	/**
	 * chuyển đổi các byte thành không dấu
	 */
	public void convertUnsignedBytes() {
		for (int i = 0; i < mBuffer.length; i++) {
			mBuffer[i] = (byte) (mBuffer[i] & 0xff);
		}
	}

	/**
	 * ném ngoại lệ khi lấy dữ liệu từ buffer không hợp lệ
	 * 
	 * @param len
	 */
	private void appendCapacity(int len) {
		if (mBuffer == null) {
			throw new NullPointerException();
		}
		if (mOffset < 0 || len <= 0) {

			throw new IndexOutOfBoundsException();
		}

		// tăng bộ nhớ lên
		if (mOffset + len > mBuffer.length) {
			
			if(len <  CAPACITY_SIZE){
				len = CAPACITY_SIZE;
			}
			
			byte[] bs = new byte[mOffset + len];
			System.arraycopy(mBuffer, 0, bs, 0, mOffset);
			mBuffer = bs;
		}
	}

	/**
	 * di chuyển con trỏ lệch tơi ví trí bằng step
	 * 
	 * @param step
	 */
	public void moveTo(int step) {
		mOffset += step;
	}

	public void setOffset(int offset) {
		mOffset = offset;
	}

	public int getOffset() {
		return mOffset;
	}

	/**
	 * lấy mảng byte thực đang lưu trữ trong buffer
	 * Nếu buffer hiện tại lưu trữ nhiều hơn byte thực được lưu trữ
	 * thì bị cắt bỏ phần còn lại
	 * @return
	 */
	public byte[] getByteBuffer() {
		if (mBuffer.length == mOffset) {
			return mBuffer;
		} else {
			byte[] bs = new byte[mOffset];
			System.arraycopy(mBuffer, 0, bs, 0, mOffset);
			return bs;
		}
	}
	
	/**
	 * lấy toàn bộ buffer, không quan tâm số lượng byte thực có trong buffer
	 * @return
	 */
	public byte[] getBuffer() {
		return mBuffer;
	}
	
	/**
	 * thiết lập lại mảng byte
	 * @param buffer
	 */
	public void setByteBuffer(byte[] buffer) {
		this.mBuffer = buffer;
		this.mOffset = 0;
	}
	
	/**
	 * Kiểm tra xem đọc đến cuối mảng chưa
	 * @return
	 */
	public boolean isAfterLast(){
		return mBuffer == null || mBuffer.length <= mOffset;
	}

	public int getLength() {
		return mBuffer.length;
	}

	public LatLng getLatLng() {
		return new LatLng(getFloat(), getFloat());
	}

	/**
	 * đẩy thông tin vị trí
	 * @param latlng
	 */
	public <T> void putLatLng(T latlng) {
		LatLng lng;
		if(latlng != null 
				&& latlng instanceof LatLng){
			lng = (LatLng)latlng;
		}else{
			lng = new LatLng(0, 0);
		}
		putFloat((float) lng.latitude);
		putFloat((float) lng.longitude);
	}
	
	/**
	 * Lấy giá trị thời gian theo 6 byte
	 * 
	 * @return
	 */
	public TimeBySix getTimeBySix() {
		return new TimeBySix(getBytes(6));
	}

	/**
	 * lưu giá trị mảng byte
	 * @param <T>
	 * 
	 * @param timeBySix
	 */
	public <T> void putTimeBySix(T timeBySix) {
		//Nếu null thì truyền mảng rỗng
		if(timeBySix != null 
				&& timeBySix instanceof TimeBySix){
			putBytes(((TimeBySix)timeBySix).getBytesTime());
		}else{
			putBytes(new byte[6]);
		}
	}
	
	public void setCheckSum() {
		int result = getCheckSum();
		putByte((byte) result);
	}

	public int getCheckSum() {
		int result = 0;
		if (mBuffer != null) {
			for (int i = 0; i < mBuffer.length - 1; i++) {
				result += mBuffer[i];
			}
		}
		result &= 0xff;
		return result;
	}
	
	/**
	 * kiểm tra tính toàn vẹn của mảng byte
	 * 
	 * @return
	 */
	public boolean isChecksum() {
		int result = 0;

		// lấy giá trị checksum
		int checksum = mBuffer[mBuffer.length - 1] & 0xff;

		// lấy tổng số byte
		for (int i = 0; i < mBuffer.length - 1; i++) {
			result += mBuffer[i];
		}
		result &= 0xff;

		return checksum == result;
	}
	
	public static Charset getCharset() {
		return Charset.forName(CHARSET);
	}
}
