package com.binhanh.protocol.serializer;

import com.binhanh.protocol.serializer.model.ExtendedByteBuffer;

import java.lang.annotation.Annotation;

public class ShortSerializer extends TypeSerializer{

	@Override
	public <T> void serialize(T object, ExtendedByteBuffer buffer, Annotation...annotations) {
		buffer.putShort((Short)object);
		
	}

	@Override
	public Object deserialize(Class<?> cls, ExtendedByteBuffer buffer, Annotation... annotations){
		return buffer.getShort();
	}

}
