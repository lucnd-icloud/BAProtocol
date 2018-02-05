package com.binhanh.protocol.serializer;

import android.support.annotation.NonNull;

import com.binhanh.protocol.serializer.model.ExtendedByteBuffer;
import com.binhanh.utils.Log;
import com.google.android.gms.maps.model.LatLng;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

public class ObjectSerializer extends TypeSerializer {

	@Override
	public <T> void serialize(T object, ExtendedByteBuffer buffer, Annotation...annotations) {
		
		if(object == null){
			throw new IllegalArgumentException("Đối tượng truyền vào để serialize có giá trị null");
		}
		
		//Khởi tạo mảng để lưu byte
		if (buffer == null) {
			buffer = ExtendedByteBuffer.allocate();
		}
		try {
			Class<?> aClass = object.getClass();
            List<Field> fields = sort(aClass);
			Class<?> fileType;
			for (Field field : fields) {
				// thiết lập để truy cập trường
				field.setAccessible(true);
				
				// lấy giá trị của trường
				Object item = field.get(object);
				
				fileType = field.getType();
				//khởi tạo đối tượng con nếu giá trị là null
				//chú ý các giá trị không tạo được đối tượng như abstract, interface...
				if(item == null){
					if(fileType.isInterface() 
							|| Modifier.isAbstract(fileType.getModifiers()) 
									|| fileType.isEnum()){
					    //không xử lý
					}else if(fileType.equals(LatLng.class)){
//					    LogFile.e("fileType.equals(LatLng.class)");
                        item = new LatLng(0,0);
                    }else{
						item = fileType.newInstance();
					}
				}
                //chuyển đổi giá trị của item vào mảng byte
                if(fileType.isEnum()){
                    get(Enum.class).serialize(item, buffer, field.getAnnotations());
                }else{
                    get(fileType).serialize(item, buffer, field.getAnnotations());
                }
			}
		} catch (Exception e) {
            Log.e("", e);
		}
	}

    /**
     * nén nó là loại primitive
     * @param object
     * @param buffer
     * @param <T>
     */
    public <T> void serializePrimitive(T object, ExtendedByteBuffer buffer){
        get(object.getClass()).serialize(object, buffer);
    }

    /**
     * nén object
     * @param value
     * @param <T>
     * @return
     */
	public <T> byte[] serialize(T value) {
        if(value == null){
            throw new IllegalArgumentException("Đối tượng truyền vào để serialize có giá trị null");
        }
		ExtendedByteBuffer buffer = ExtendedByteBuffer.allocate();
        Class<?> aClass = value.getClass();
        if(aClass.isPrimitive()
                || aClass == String.class){
            serializePrimitive(value, buffer);
        }else{
            serialize(value, buffer);
        }
		return buffer.getByteBuffer();
	}

	@Override
	public Object deserialize(Class<?> cls, ExtendedByteBuffer buffer, Annotation... annotations) {
		if(buffer == null || buffer.isAfterLast()){
			throw new IllegalArgumentException("Không có dữ liệu để deserialize: " + cls);
		}
		try {
			Object value =  cls.newInstance();
			return deserialize(value,buffer,annotations);
		} catch (Exception e) {
            Log.e("", e);
		}
		return null;
	}

    /**
     * chuyển đổi mảng byte thành đối tượng
     * @param cls
     * @param bytes
     * @param annotations
     * @return
     */
    public Object deserialize(@NonNull Class<?> cls, @NonNull byte[] bytes, Annotation... annotations) {
        ExtendedByteBuffer buffer = ExtendedByteBuffer.wrap(bytes);
        return deserialize(cls, buffer);
    }
	
	/**
	 * chuyển đối mảng byte thành đối tượng
	 * @param value
	 * @param buffer
	 * @param annotations
	 * @return
	 */
	public Object deserialize(Object value, ExtendedByteBuffer buffer, Annotation... annotations) {
		try {
			
			if(buffer == null || buffer.isAfterLast()){
                Log.e("Không có dữ liệu để deserialize cho Class: " + value.getClass().getName());
				return null;
			}
			
			// sắp xếp field
            List<Field> fields = sort(value.getClass());
			Object item;
			for (Field field : fields) {

				field.setAccessible(true);
				//Nếu là hằng enum thì xử lý riêng
				if(field.getType().isEnum()){
					item = get(Enum.class).deserialize(field, buffer, value);
				}else{
					item = get(field.getType()).deserialize(field, buffer, value);
				}
				
				//thiết lập giá trị của trường vào value
				if(item != null){
					field.set(value, item);
				}
			}
			
			return value;

		} catch (Exception e) {
            Log.e("", e);
		}
		return null;
	}
}
