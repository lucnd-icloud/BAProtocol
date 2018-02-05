package com.binhanh.protocol.serializer;

import android.renderscript.Element;
import android.text.TextUtils;

import com.binhanh.protocol.serializer.annotation.ClassMask;
import com.binhanh.protocol.serializer.annotation.MethodMask;
import com.binhanh.protocol.serializer.annotation.PropertyIndex;
import com.binhanh.protocol.serializer.model.ExtendedByteBuffer;
import com.binhanh.utils.Log;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class EnumSerializer extends TypeSerializer{
	
	public static final String GET_ENUM = "getEnum";
	
	@Override
	public <T> void serialize(T value, ExtendedByteBuffer buffer, Annotation...annotations) {

        //kiểm tra xem giá trị null hay không
        int anEnum = 0;
        Element.DataType dataType = Element.DataType.SIGNED_8;
        if(value != null){
            anEnum = ((Enum)value).ordinal();
            //lấy độ lớn của trường
            Class<?> aClass = value.getClass();
            ClassMask classMask = aClass.getAnnotation(ClassMask.class);
            if(classMask != null){
                dataType = classMask.length();
            }else{
                Log.d("Chưa khởi tạo @ClassMask cho lớp enum để serialize");
            }
        }else{
            Log.d("EnumSerializer Giá trị null, Trường enum phải được khởi tạo để serialize");
        }
        //lây giá trị của enum
        putValueByDataType(anEnum, buffer, dataType);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object deserialize(Class<?> cls, ExtendedByteBuffer buffer, Annotation... annotations) {

        try {
            if(buffer == null || buffer.isAfterLast()){
                Log.d("Không có dữ liệu để deserialize cho field: " + cls.getName());
                return null;
            }

            ClassMask classMask = cls.getAnnotation(ClassMask.class);
            Element.DataType dataType = Element.DataType.SIGNED_8;
            if(classMask != null){
                dataType = classMask.length();
            }else{
                Log.d("EnumSerializer deserialize Chưa khởi tạo @ClassMask cho lớp enum để serialize");
            }

            //lấy loại của enum lưu trữ như byte, short...
            int dataTypeValue = getDataTypeValue(buffer, dataType);

            Object[] objects = cls.getEnumConstants();
            if(dataTypeValue >= objects.length || dataTypeValue < 0){
                dataTypeValue = 0;
            }
            return objects[dataTypeValue];

        } catch (Exception e) {
            Log.e("", e);
        }
        return null;
	}
	
	@Override
	public Object deserialize(Field field, ExtendedByteBuffer buffer, Object value) {
		try {
			
			if(buffer == null || buffer.isAfterLast()){
				Log.d("Không có dữ liệu để deserialize cho field: " + field.getName() + "; Class: " + value.getClass().getName());
				return null;
			}
			
			ClassMask classMask = field.getType().getAnnotation(ClassMask.class);
            if(classMask == null){
                throw new IllegalArgumentException("Chưa khởi tạo @ClassMask cho lớp enum để deserialize");
            }

			int index = getDataTypeValue(buffer, classMask.length());
			String methodName = field.getAnnotation(PropertyIndex.class).method().value();
			if(!TextUtils.isEmpty(methodName)){
				//Lấy tất cả các hàm của method con
				Method[] methods = field.getType().getDeclaredMethods();
				MethodMask methodMask;
				for (Method method : methods) {
					//Lấy hàm overite
					methodMask = method.getAnnotation(MethodMask.class);
					if(methodMask != null 
							&& methodName.equals(methodMask.value())){
						return method.invoke(value, index);
					}
				}
			}else {
				Object[] objects = field.getType().getEnumConstants();
				if(index >= objects.length || index < 0){
					index = 0;
				}
				return objects[index];
			}
		} catch (Exception e) {
            Log.e("", e);
		}
		return null;
	}

}
