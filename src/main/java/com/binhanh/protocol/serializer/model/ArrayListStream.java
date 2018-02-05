package com.binhanh.protocol.serializer.model;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

/**
 * Created by lucnd on 14/12/2017.
 * xử lý liên quan đến ArrayMap
 */

public class ArrayListStream<T> extends ArrayList<T> {

    public ArrayListStream(int initialCapacity) {
        super(initialCapacity);
    }

    public ArrayListStream() {
    }

    public ArrayListStream(@NonNull Collection<? extends T> c) {
        super(c);
    }

    /**
     * for toàn bộ mảng
     *
     * @param action
     */
    public void forItem(@NonNull Consumer<T> action) {
        for (T t : this) {
            action.accept(t);
        }
    }

    public ArrayListStream<T> iterator(@NonNull Consumer<T> action) {
        Iterator<T> iterator = iterator();
        while (iterator.hasNext()) {
            action.accept(iterator.next());
        }
        return this;
    }

    public void sorted(@NonNull Comparator<T> comparator){
        if(size() > 1){
            Collections.sort(this, comparator);
        }
    }

    public void forIterator(@NonNull Consumer<Iterator<T>> action) {
        Iterator<T> iterator = iterator();
        while (iterator.hasNext()) {
//                Log.d(TAG,"iterator -------------------------");
            action.accept(iterator);
        }
    }
}
