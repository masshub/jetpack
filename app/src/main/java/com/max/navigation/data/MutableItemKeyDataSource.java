package com.max.navigation.data;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.paging.ItemKeyedDataSource;
import androidx.paging.PagedList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author: maker
 * @date: 2020/12/17 14:22
 * @description:
 */
public abstract class MutableItemKeyDataSource<Key,Value> extends ItemKeyedDataSource<Key,Value> {
    public List<Value> data = new ArrayList<>();
    private ItemKeyedDataSource mDataSource;

    @SuppressLint("RestrictedApi")
    public PagedList<Value> buildNewPagedList(PagedList.Config config) {
        PagedList<Value> pagedList = new PagedList.Builder<Key, Value>(this, config)
                .setFetchExecutor(ArchTaskExecutor.getIOThreadExecutor())
                .setNotifyExecutor(ArchTaskExecutor.getMainThreadExecutor())
                .build();

        return pagedList;

    }

    public MutableItemKeyDataSource(ItemKeyedDataSource dataSource) {
        this.mDataSource = dataSource;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Key> params, @NonNull LoadInitialCallback<Value> callback) {

    }

    @Override
    public void loadAfter(@NonNull LoadParams<Key> params, @NonNull LoadCallback<Value> callback) {
        mDataSource.loadAfter(params,callback);

    }

    @Override
    public void loadBefore(@NonNull LoadParams<Key> params, @NonNull LoadCallback<Value> callback) {
        callback.onResult(Collections.emptyList());

    }

    @NonNull
    @Override
    public abstract Key getKey(@NonNull Value item);
}