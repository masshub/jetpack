package com.max.navigation.ui.my;

import androidx.annotation.NonNull;
import androidx.paging.DataSource;
import androidx.paging.ItemKeyedDataSource;

import com.max.navigation.abs.AbsViewModel;
import com.max.navigation.model.Feed;

/**
 * @author: maker
 * @date: 2021/2/8 17:24
 * @description:
 */
public class ProfileViewModel extends AbsViewModel<Feed> {
    @Override
    public DataSource createDataSource() {
        return new DataSource();
    }

    private class DataSource extends ItemKeyedDataSource<Long,Feed>{

        @Override
        public void loadInitial(@NonNull LoadInitialParams<Long> params, @NonNull LoadInitialCallback<Feed> callback) {

        }

        @Override
        public void loadAfter(@NonNull LoadParams<Long> params, @NonNull LoadCallback<Feed> callback) {

        }

        @Override
        public void loadBefore(@NonNull LoadParams<Long> params, @NonNull LoadCallback<Feed> callback) {

        }

        @NonNull
        @Override
        public Long getKey(@NonNull Feed item) {
            return null;
        }
    }
}