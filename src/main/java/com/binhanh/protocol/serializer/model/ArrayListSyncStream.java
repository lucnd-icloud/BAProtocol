package com.binhanh.protocol.serializer.model;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by lucnd on 27/12/2017.
 * xử lý liên quan đến CopyOnWriteArrayList
 */

public class ArrayListSyncStream<E> extends ArrayList<E> {

    private final String TAG;
    private final List<E> lock;

    public ArrayListSyncStream() {
        lock = Collections.synchronizedList(this);
        TAG = String.valueOf(hashCode());
    }

    public ArrayListSyncStream(@NonNull Collection<? extends E> c) {
        super(c);
        lock = Collections.synchronizedList(this);
        TAG = String.valueOf(hashCode());
    }

    /**
     * for toàn bộ mảng
     *
     * @param action
     */
    public void forItem(@NonNull Consumer<E> action) {
        synchronized(lock) {
            for (E e: this) {
                action.accept(e);
            }
        }
    }

    public void iterator(@NonNull Consumer<Iterator<E>> action) {
        synchronized(lock) {
            Iterator<E> iterator = iterator();
            while (iterator.hasNext()) {
//                Log.d(TAG,"iterator -------------------------");
                action.accept(iterator);
            }
        }
    }

    @Override
    public boolean add(E e) {
        synchronized(lock) {
//            Log.d(TAG,"add -------------------------");
            return super.add(e);
        }
    }

    @Override
    public E get(int index) {
        synchronized(lock) {
            return super.get(index);
        }
    }

    @Override
    public E remove(int index) {
        synchronized(lock) {
//            Log.d(TAG,"remove -------------------------");
            return super.remove(index);
        }
    }

    @Override
    public boolean remove(Object o) {
        synchronized(lock) {
//            Log.d(TAG,"remove -------------------------");
            return super.remove(o);
        }
    }

    public void sorted(@NonNull Comparator<E> comparator){
        synchronized(lock) {
            if(size() > 1){
//                Log.d(TAG,"sort 1 -------------------------");
                Collections.sort(this, comparator);
//                Log.d(TAG,"sort 2 -------------------------");
            }
        }
    }
}
