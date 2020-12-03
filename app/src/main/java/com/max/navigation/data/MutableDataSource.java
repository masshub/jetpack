package com.max.navigation.data;

import androidx.annotation.NonNull;
import androidx.paging.PageKeyedDataSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author: maker
 * @date: 2020/12/3 19:57
 * @description:
 */
public class MutableDataSource<Key,Value> extends PageKeyedDataSource<Key,Value> {
    public List<Value> data = new ArrayList<>();
    @Override
    public void loadInitial(@NonNull LoadInitialParams<Key> params, @NonNull LoadInitialCallback<Key, Value> callback) {
        callback.onResult(data,null,null);

    }

    @Override
    public void loadBefore(@NonNull LoadParams<Key> params, @NonNull LoadCallback<Key, Value> callback) {
        callback.onResult(Collections.emptyList(),null);

    }

    @Override
    public void loadAfter(@NonNull LoadParams<Key> params, @NonNull LoadCallback<Key, Value> callback) {
        callback.onResult(Collections.emptyList(),null);

    }
}