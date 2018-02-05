package com.binhanh.protocol.serializer.model;

/**
 * Created by lucnd on 25/12/2017.
 */

public interface Consumer<T> {
    void accept(T t);
}
