package com.binhanh.protocol.serializer.model;

import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;

import java.util.Iterator;

/**
 * Created by lucnd on 14/12/2017.
 * xử lý liên quan đến ArrayMap
 */

public class ArrayMapStream<K, V> extends ArrayMap<K, V> {

    public ArrayMapStream() {
        super();
    }

    /**
     * Create a new ArrayMap with the mappings from the given ArrayMap.
     */
    public ArrayMapStream(ArrayMap<K, V> map) {
        super(map);
    }

    public ArrayMapStream(int capacity) {
        super(capacity);
    }

    /**
     * for toàn bộ mảng
     *
     * @param func
     */
    public ArrayMapStream<K, V> forEach(@NonNull BiConsumer<K, V> func) {
        for (int i = 0; i < size(); i++) {
            func.accept(keyAt(i), valueAt(i));
        }
        return this;
    }

    public ArrayMapStream<K, V> forEach(@NonNull Consumer<V> func) {
        for (int i = 0; i < size(); i++) {
            func.accept(valueAt(i));
        }
        return this;
    }

    public ArrayMapStream<K, V> iterator(@NonNull BiConsumer<K, V> func) {
        Iterator<K> iterator = keySet().iterator();
        K k;
        while (iterator.hasNext()) {
            k = iterator.next();
            func.accept(k, get(k));
        }

        return this;
    }

    public ArrayMapStream<K, V> iterator(@NonNull Consumer<V> func) {
        Iterator<K> iterator = keySet().iterator();
        while (iterator.hasNext()) {
            func.accept(get(iterator.next()));
        }

        return this;
    }

    /**
     * filter theo mảng
     *
     * @param func
     * @return
     */
    public ArrayMapStream<K, V> filter(@NonNull Func1<V, Boolean> func) {
        final ArrayMapStream<K, V> stream = new ArrayMapStream<>();
        K k;
        V v;
        for (int i = 0; i < size(); i++) {
            v = valueAt(i);
            if (func.apply(v)) {
                stream.put(keyAt(i), v);
            }
        }
        return stream;
    }

    public void doNext() {
    }

    public interface Func1<T, R> {
        R apply(T t);
    }

    public interface Consumer<T> {
        void accept(T t);
    }

    public interface BiConsumer<T, U> {
        void accept(T t, U u);
    }
}
