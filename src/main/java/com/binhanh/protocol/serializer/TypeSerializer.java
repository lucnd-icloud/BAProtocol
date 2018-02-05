package com.binhanh.protocol.serializer;

import android.renderscript.Element.DataType;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.SparseArray;

import com.binhanh.protocol.serializer.annotation.ClassMask;
import com.binhanh.protocol.serializer.annotation.MethodMask;
import com.binhanh.protocol.serializer.annotation.PropertyIndex;
import com.binhanh.protocol.serializer.model.ArrayListStream;
import com.binhanh.protocol.serializer.model.ArrayMapStream;
import com.binhanh.protocol.serializer.model.ExtendedByteBuffer;
import com.binhanh.protocol.serializer.model.TimeBySix;
import com.binhanh.utils.Log;
import com.google.android.gms.maps.model.LatLng;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public abstract class TypeSerializer {

    public static final ConcurrentHashMap<Class<?>, TypeSerializer> MAP = new ConcurrentHashMap<Class<?>, TypeSerializer>() {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        {
            put(boolean.class, new BooleanSerializer());
            put(Boolean.class, new BooleanSerializer());
            put(byte.class, new ByteSerializer());
            put(byte[].class, new ByteArraySerializer());
            put(Byte.class, new ByteSerializer());
            put(short.class, new ShortSerializer());
            put(Short.class, new ShortSerializer());
            put(int.class, new IntegerSerializer());
            put(Integer.class, new IntegerSerializer());
            put(long.class, new LongSerializer());
            put(Long.class, new LongSerializer());
            put(float.class, new FloatSerializer());
            put(Float.class, new FloatSerializer());
            put(double.class, new DoubleSerializer());
            put(Double.class, new DoubleSerializer());
            put(String.class, new StringSerializer());
            put(LatLng.class, new LatLngSerializer());
            put(TimeBySix.class, new TimeBySixSerializer());
            put(Date.class, new DateSerializer());
            put(List.class, new ListSerializer());
            put(SparseArray.class, new SparseArraySerializer());
            put(ArrayMap.class, new ArrayMapSerializer());
            put(ArrayMapStream.class, new ArrayMapStreamSerializer());
            put(ArrayListStream.class, new ArrayListStreamSerializer());
            put(Enum.class, new EnumSerializer());
        }
    };

    private static final ConcurrentHashMap<Class<?>, List<Field>> FIELD_LIST = new ConcurrentHashMap<>();

    /**
     * hàm dùng để chuyển đổi đối tượng thành mảng byte
     *
     * @param value:      giá trị cần chuyển đổi thành mảng byte
     * @param buffer:     dùng để lưu mảng byte sau khi chuyển đổi
     * @param annotations
     */
    public abstract <T> void serialize(T value, ExtendedByteBuffer buffer, Annotation... annotations);

    /**
     * Hàm abstract để thực hiện chuyển đổi giá trị trong mảng byte thành đối tượng
     *
     * @param cls
     * @param buffer
     * @param annotations
     * @return
     */
    public abstract Object deserialize(Class<?> cls, ExtendedByteBuffer buffer, Annotation... annotations);

    /**
     * Hàm này thực hiện lấy giá trị trong mảng byte để set vào field
     *
     * @param field:  trường để set giá trị vào
     * @param buffer: mảng byte cần lấy dữ liệu
     * @return
     */
    public Object deserialize(Field field, ExtendedByteBuffer buffer, Object value) {
        try {

            if (buffer == null || buffer.isAfterLast()) {
                Log.d("Không có dữ liệu để deserialize cho field: " + field.getName() + "; Class: " + value.getClass().getName());
                return null;
            }

            Method method = getMethodMark(field, value);
            //nếu có hàm gọi để xử lý cho trường này thì sẽ thực hiện hàm này
            if (method != null) {
                return method.invoke(value, buffer);
            } else {
                return deserialize(field.getType(), buffer, field.getAnnotations());
            }
        } catch (Exception e) {
            Log.e("", e);
        }
        return null;
    }

    /**
     * sắp xếp các trường trong class theo index anotiation;
     *
     * @param cls
     * @return
     */
    public static List<Field> sort(final Class<?> cls) {
        List<Field> lst = FIELD_LIST.get(cls);
        if(lst != null) return lst;

        List<Field> lstTemp = getDeclaredFields(cls);

        //lấy list có order
        lst = new ArrayList<>();
        PropertyIndex index;
        for (Field field : lstTemp) {
            index = field.getAnnotation(PropertyIndex.class);
            if (index != null) {
                lst.add(field);
            }
        }

        // sắp xếp các trường
        Collections.sort(lst, new Comparator<Field>() {
            @Override
            public int compare(Field o1, Field o2) {
                PropertyIndex or1 = o1.getAnnotation(PropertyIndex.class);
                PropertyIndex or2 = o2.getAnnotation(PropertyIndex.class);
                if (or1 != null && or2 != null) {
                    int ret = or1.index() - or2.index();
                    if (ret == 0) {
                        throw new IllegalArgumentException("Lỗi khi index của 2 trường bằng nhau " + cls + ": " +  o1.getName() + " - " + o2.getName());
                    }

                    return ret;
                } else if (or1 != null) {
                    return -1;
                } else if (or2 != null) {
                    return 1;
                }
                return -1;
            }
        });

        //thêm vào mảng nếu có
        FIELD_LIST.put(cls, lst);

        return lst;
    }

    public static PropertyIndex getPropertyIndex(Annotation... annotations) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof PropertyIndex) {
                return (PropertyIndex) annotation;
            }
        }
        return null;
    }

    public static ClassMask getClassMark(Annotation... annotations) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof ClassMask) {
                return (ClassMask) annotation;
            }
        }
        return null;
    }

    public static PropertyIndex getPropertyIndex(Field field) {
        return field.getAnnotation(PropertyIndex.class);
    }

    /**
     * lấy tên hàm được thiết lập trong property của trường
     *
     * @param field
     * @return
     */
    public static Method getMethodMark(Field field, Object value) {
        Class<?> fieldType = field.getType();
        String methodName = getPropertyIndex(field).method().value();
        //nếu có hàm gọi để xử lý cho trường này thì sẽ thực hiện hàm này
        if (!TextUtils.isEmpty(methodName)) {
            Method method = getMethod(value.getClass(), methodName);
            if (method != null) {
                method.setAccessible(true);
                return method;
            } else {
                Log.e("getMethodMark fieldType = " + fieldType.getName());
            }
        }
        return null;
    }

    /**
     * Lấy đối tượng dùng để serializer
     *
     * @param cls
     * @return
     */
    public static TypeSerializer get(Class<?> cls) {
        TypeSerializer serializer = MAP.get(cls);
        if (serializer == null) {
            return new ObjectSerializer();
        }
        return serializer;
    }

    public static DataType getDataType(Annotation... annotations) {
        return getPropertyIndex(annotations).length();
    }

    /**
     * Lấy độ dài của dữ liệu dạng list, string, byte[]
     *
     * @param annotations
     * @return
     */
    public static int getSize(ExtendedByteBuffer buffer, Annotation... annotations) {
        DataType dataType = getDataType(annotations);
        return getDataTypeValue(buffer, dataType);
    }

    public static int getDataTypeValue(ExtendedByteBuffer buffer, DataType dataType) {
        // Lấy độ dài của mảng
        int size = 0;
        switch (dataType) {
            case SIGNED_8:
                size = buffer.getByte();
                break;
            case SIGNED_32:
                size = buffer.getInt();
                break;
            default:
                size = buffer.getShort();
                break;
        }
        return size;
    }

    /**
     * đấy size vào buffer
     *
     * @param size
     * @param buffer
     * @param annotations
     */
    public static void putValueByDataType(int size, ExtendedByteBuffer buffer, Annotation... annotations) {
        DataType dataType = getDataType(annotations);
        putValueByDataType(size, buffer, dataType);
    }

    public static void putValueByDataType(int size, ExtendedByteBuffer buffer, DataType dataType) {
        // đấy số lượng của danh sách
        switch (dataType) {
            case SIGNED_8:
                buffer.putByte((byte) size);
                break;
            case SIGNED_32:
                buffer.putInt((int) size);
                break;
            default:
                buffer.putShort((short) size);
                break;
        }
    }

    /**
     * lấy tất cả các trường của lớp truyền vào, bao gồm cả trường của lớp cha
     *
     * @param cls
     * @return
     */
    public static List<Field> getDeclaredFields(Class<?> cls) {

        //lại null thì không phải
        if (cls == null) {
            return null;
        }

        //lấy danh sách trường của lớp hiện tại
        ArrayList<Field> list = new ArrayList<>(Arrays.asList(cls.getDeclaredFields()));

        //Lấy các field ở lớp cha
        for (Class<?> c = cls.getSuperclass(); c != null; c = c.getSuperclass()) {

            //nếu lớp cha là interface thì kết thúc
            if (c.isInterface()) break;

            //Nếu lớp cha là object thì kết thúc
            if (c.isAssignableFrom(Object.class)) break;

            //kiểm tra lớp cha
            list.addAll(Arrays.asList(c.getDeclaredFields()));
        }
        return list;
    }

    public static Method getMethod(Class<?> cls, String methodName ){
        Method[] methods = cls.getDeclaredMethods();
        // lấy hàm gọi
        MethodMask mask;
        for (Method m : methods) {
            mask = m.getAnnotation(MethodMask.class);
            // khi tìm thấy thì thực hiện gọi hàm
            if (mask != null && mask.value().equals(methodName)) {
                return m;
            }
        }
        return null;
    }

    /**
     * lấy loại tham số đầu tiên của đối số generic
     * @param field
     * @return
     */
    public static Class<?> getFirstParameterizeType(Field field){
        //Nếu là danh sách thì lấy loại dữ liệu generic
        ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
        Class<?> itemType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
        if (itemType == null) {
            throw new IllegalArgumentException("Không lấy được đối tượng generic");
        }
        return itemType;
    }

    /**
     * lấy loại tham số thứ 2 của đối số generic
     * @param field
     * @return
     */
    public static Class<?> getSecondsParameterizeType(Field field){
        //Nếu là danh sách thì lấy loại dữ liệu generic
        ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
        Class<?> itemType = (Class<?>) parameterizedType.getActualTypeArguments()[1];
        if (itemType == null) {
            throw new IllegalArgumentException("Không lấy được đối tượng generic");
        }
        return itemType;
    }
}
