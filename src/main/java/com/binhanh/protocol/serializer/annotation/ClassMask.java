package com.binhanh.protocol.serializer.annotation;

import android.renderscript.Element.DataType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE})
public @interface ClassMask {
	/**Độ dài của loại dữ liệu để đánh dấu loại dữ liệu cần chuyển đổi*/
	public DataType length() default DataType.SIGNED_16;
}
