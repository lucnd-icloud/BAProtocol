package com.binhanh.utils;

import com.binhanh.protocol.serializer.ObjectSerializer;
import com.binhanh.protocol.serializer.TypeSerializer;
import com.binhanh.protocol.serializer.annotation.PropertyIndex;
import com.binhanh.protocol.serializer.model.ExtendedByteBuffer;

import java.lang.reflect.Field;
import java.util.List;

public class ByteUtils {

	/**
	 * chuyển đổi 1 đối tượng thành mảng byte
	 * 
	 * @param value
	 * @return
	 */
	public synchronized static <T> byte[] serialize(T value) {
	    if(value == null) return null;
		return new ObjectSerializer().serialize(value);
	}


	

	/**
	 * chuyển đổi dữ liệu trong mảng bytes thành các giá trị trong đối tượng
	 * value
	 * @param <T>
	 * 
	 * @param value
	 *            : đối tượng nhận dữ liệu chuyển đổi
	 * @param bytes
	 *            : mảng byte cần chuyển đổi
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public synchronized static <T> T deserializeObject(Object value, byte[] bytes) {
		ExtendedByteBuffer buffer = ExtendedByteBuffer.wrap(bytes);
		return (T)new ObjectSerializer().deserialize(value, buffer);
	}


	/**
	 * cập nhật giá trị của trường vào field
	 * 
	 * @param field
	 * @param value
	 * @param buffer
	 * @throws Exception
	 */
	public synchronized static <T> void putValueToField(Field field, T value, ExtendedByteBuffer buffer,
			PropertyIndex pIndex) throws Exception {

		// Nếu là cuối mảng byte rồi thì bỏ qua
		if (buffer.isAfterLast()){
            Log.d("putValueToField: hết buffer: " + field.getName());
            return;
        }
		Class<?> fieldType = field.getType();
		TypeSerializer serializer = TypeSerializer.get(fieldType);
		field.set(value, serializer.deserialize(field, buffer, value));
	}

	/**
	 * phân tích các đối tượng có khởi tạo rỗng
	 * @param <T>
	 * 
	 * @param cls
	 * @param bytes
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public synchronized static <T> T deserializeClass(Class<T> cls, byte[] bytes) {

        if(cls == null ){
            Log.e("deserializeClass lớp nhận dữ liệu cls = null");
            return null;
        }

        if(bytes == null || bytes.length == 0){
            Log.e("deserializeClass bytes = null");
            return null;
        }

        Object o = new ObjectSerializer().deserialize(cls, bytes);
        if(o != null){
            return (T)o;
        }
        return null;
	}


	/**
	 * chuyển đổi từ 1 mảng byte thành giá trị đối tượng
	 * @param <T>
	 * 
	 * @param cls
	 * @param buffer
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T deserialize(Class<T> cls, ExtendedByteBuffer buffer) {
		return (T)new ObjectSerializer().deserialize(cls, buffer);
	}

	public static List<Field> sort(Class<?> cls){
		return TypeSerializer.sort(cls);
	}
}
