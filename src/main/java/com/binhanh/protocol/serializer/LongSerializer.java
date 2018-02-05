package com.binhanh.protocol.serializer;

import com.binhanh.protocol.serializer.model.ExtendedByteBuffer;

import java.lang.annotation.Annotation;

public class LongSerializer extends TypeSerializer{

	@Override
	public <T> void serialize(T value, ExtendedByteBuffer buffer, Annotation...annotations) {
		buffer.putLong((Long) value);
	}

	@Override
	public Object deserialize(Class<?> cls, ExtendedByteBuffer buffer, Annotation... annotations){
		return buffer.getLong();
	}

}
