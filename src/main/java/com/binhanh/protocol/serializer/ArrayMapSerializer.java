package com.binhanh.protocol.serializer;


import android.support.v4.util.ArrayMap;

import com.binhanh.protocol.serializer.model.ExtendedByteBuffer;
import com.binhanh.protocol.serializer.model.KeyObjectInterface;
import com.binhanh.utils.Log;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

public class ArrayMapSerializer extends TypeSerializer{

	@Override
	public <T> void serialize(T value, ExtendedByteBuffer buffer, Annotation...annotations) {
		int size = 0;
		ArrayMap<?, ?> maps = null;

		// Nếu không có dữ liệu
		if (value == null) {
			size = 0;
		} else {
			// trường hợp là 1 List đối tượng
			maps = (ArrayMap<?, ?>) value;

			// gán độ lớn của mảng
			size = maps.size();
		}

		// đấy số lượng của danh sách
		putValueByDataType(size, buffer, annotations);

		// cập nhật vào danh sách đối tượng
		if (maps != null && size > 0) {
			// gán từng đối tượng con
			ExtendedByteBuffer bufferList = ExtendedByteBuffer.allocate();
			Object item;
			TypeSerializer serializer = null;
			for (int i = 0; i < size; i++) {
				item = maps.valueAt(i);
				if(serializer == null && item != null){
					serializer = get(item.getClass());
				}
                if(serializer!= null){
                    serializer.serialize(item, bufferList);
                }else{
                    throw new IllegalArgumentException("Loại TypeSerializer không có trong map: " + item.getClass());
                }

			}
			// chèn vào đối tượng
			buffer.putBytes(bufferList.getByteBuffer());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object deserialize(Class<?> cls, ExtendedByteBuffer buffer, Annotation... annotations) {
		// Lấy độ dài của mảng
		int size = getSize(buffer, annotations);
		
		@SuppressWarnings("rawtypes")
        ArrayMap map = new ArrayMap<>();
		// gán từng đối tượng con
		TypeSerializer serializer = get(cls);
		Object o;
		for (int i = 0; i < size; i++) {
		    o = serializer.deserialize(cls, buffer);
		    if(o instanceof KeyObjectInterface){
                map.put(((KeyObjectInterface)o).getKey(), o);
            }else{
		        throw new IllegalArgumentException("Đối tượng không kế thừa KeyStringInterface: " + o.getClass().getName());
            }
		}
		return map;
	}
	
	@Override
	public Object deserialize(Field field, ExtendedByteBuffer buffer, Object value) {
		try {
			
			if(buffer == null || buffer.isAfterLast()){
				Log.e("SparseArraySerializer not deserialize with field: " + field.getName() + "; Class: " + value.getClass().getName());
				return null;
			}
			
			Method method = getMethodMark(field, value);
			//nếu có hàm gọi để xử lý cho trường này thì sẽ thực hiện hàm này
			if(method != null){
				return method.invoke(value, buffer);
			}else {
				//Nếu là danh sách thì lấy loại dữ liệu generic
                Class<?> itemType = getSecondsParameterizeType(field);
				return deserialize(itemType, buffer, field.getAnnotations());
			}
		} catch (Exception e) {
            Log.e("", e);
		}
		return null;
	}

}
