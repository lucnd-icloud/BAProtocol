package com.binhanh.protocol.serializer;

import com.binhanh.protocol.serializer.model.ExtendedByteBuffer;
import com.binhanh.protocol.serializer.model.TimeBySix;

import java.lang.annotation.Annotation;

public class TimeBySixSerializer extends TypeSerializer {

	@Override
	public <T> void serialize(T value, ExtendedByteBuffer buffer, Annotation... annotations) {
		// Nếu null thì truyền mảng rỗng
		if (value != null && value instanceof TimeBySix) {
			buffer.putBytes(((TimeBySix) value).getBytesTime());
		} else {
			buffer.putBytes(new byte[6]);
		}
	}

	@Override
	public Object deserialize(Class<?> cls, ExtendedByteBuffer buffer, Annotation... annotations){
		return new TimeBySix(buffer.getBytes(6));
	}

}
