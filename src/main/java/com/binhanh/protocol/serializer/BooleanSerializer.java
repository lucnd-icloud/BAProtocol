package com.binhanh.protocol.serializer;

import com.binhanh.protocol.serializer.model.ExtendedByteBuffer;

import java.lang.annotation.Annotation;

public class BooleanSerializer extends TypeSerializer{

	@Override
	public <T> void serialize(T object, ExtendedByteBuffer buffer, Annotation...annotations) {
		boolean b = (Boolean) object;
		buffer.putByte((byte) (b ? 1 : 0));
	}

	@Override
	public Object deserialize(Class<?> cls, ExtendedByteBuffer buffer, Annotation... annotations){
		return buffer.getByte() > 0;
	}

}
