package com.binhanh.protocol.serializer.annotation;

import android.renderscript.Element.DataType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.FIELD})
public @interface PropertyIndex {
	
	/**Chỉ số, vị trí của trường xử lý*/
	public int index() default 0;
	
	/**Độ dài của dữ liệu*/
	public DataType length() default DataType.SIGNED_16;
	
	/**hàm được gọi trả về giá trị biến được sửa dụng*/
	public MethodMask method() default @MethodMask(value = "");
	
	public boolean isLastIndex() default false;
}
