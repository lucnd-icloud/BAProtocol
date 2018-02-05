package com.binhanh.protocol.serializer;

import com.binhanh.protocol.serializer.model.ExtendedByteBuffer;

import java.lang.annotation.Annotation;
import java.util.Date;

/**
 * 
 * @author Nguyễn Đức Lực
 * Thời gian được sử dụng để serialize là second
 *
 */
public class DateSerializer extends TypeSerializer{

	@Override
	public <T> void serialize(T value, ExtendedByteBuffer buffer, Annotation...annotations) {
		// thời gian gửi tới server là giây
		if (value == null) {
			buffer.putLong(0);
		} else {
			Date date = (Date) value;
			buffer.putLong(date.getTime() / 1000);
		}
	}

	@Override
	public Object deserialize(Class<?> cls, ExtendedByteBuffer buffer, Annotation... annotations){
		return new Date(buffer.getLong() * 1000);
	}

}
