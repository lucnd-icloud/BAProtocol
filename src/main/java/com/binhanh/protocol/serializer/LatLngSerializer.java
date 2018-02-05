package com.binhanh.protocol.serializer;

import com.binhanh.protocol.serializer.model.ExtendedByteBuffer;
import com.google.android.gms.maps.model.LatLng;

import java.lang.annotation.Annotation;

public class LatLngSerializer extends TypeSerializer{

	@Override
	public <T> void serialize(T value, ExtendedByteBuffer buffer, Annotation...annotations) {
		// Là 1 đối tượng Latlng
		LatLng lng;
		if(value != null 
				&& value instanceof LatLng){
			lng = (LatLng)value;
		}else{
			lng = new LatLng(0, 0);
		}
		buffer.putFloat((float) lng.latitude);
		buffer.putFloat((float) lng.longitude);
	}

	@Override
	public Object deserialize(Class<?> cls, ExtendedByteBuffer buffer, Annotation... annotations) {
		return new LatLng(buffer.getFloat(), buffer.getFloat());
	}

}
