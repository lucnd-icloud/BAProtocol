package com.binhanh.protocol.serializer;

import com.binhanh.protocol.serializer.model.ExtendedByteBuffer;

import java.lang.annotation.Annotation;

public class ByteArraySerializer extends TypeSerializer{
	@Override
	public <T> void serialize(T value, ExtendedByteBuffer buffer, Annotation...annotations) {
		if (getPropertyIndex(annotations).isLastIndex()) {
			if (value != null) {
				buffer.putBytes((byte[]) value);
			}
		} else {
			if (value != null && value instanceof byte[]) {
				byte[] bs = (byte[])value;
				//đẩy size
				putValueByDataType(bs.length, buffer, annotations);
				
				//đẩy byte
				buffer.putBytes(bs);
				
			}else{
				putValueByDataType(0, buffer, annotations);
			}
		}
	}

	@Override
	public Object deserialize(Class<?> cls, ExtendedByteBuffer buffer, Annotation... annotations) {
		if (getPropertyIndex(annotations).isLastIndex()) {
			return buffer.getLastBytes();
		} else {
			
			int length = getSize(buffer, annotations);
			
			//lấy mảng byte
			return buffer.getBytes(length);
		}
	}

}
