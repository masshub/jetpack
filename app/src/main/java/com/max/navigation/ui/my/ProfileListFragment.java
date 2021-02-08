package com.max.navigation.ui.my;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;

import com.max.navigation.abs.AbsListFragment;
import com.max.navigation.model.Feed;
import com.max.navigation.ui.publish.PreviewActivity;
import com.scwang.smart.refresh.layout.api.RefreshLayout;

/**
 * @author: maker
 * @date: 2021/2/8 17:23
 * @description:
 */
public class ProfileListFragment extends AbsListFragment<Feed,ProfileViewModel> {
    public static ProfileListFragment newInstance(String tabType) {

        Bundle args = new Bundle();
        args.putString(ProfileActivity.KEY_TAB_TYPE, tabType);
        ProfileListFragment fragment = new ProfileListFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public PagedListAdapter getAdapter() {
        return null;
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {

    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {

    }
}