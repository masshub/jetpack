package com.max.navigation.ui.home;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;

import com.max.libnavannotation.FragmentDestination;
import com.max.navigation.abs.AbsListFragment;
import com.max.navigation.model.Feed;
import com.scwang.smart.refresh.layout.api.RefreshLayout;

@FragmentDestination(pageUrl = "main/tabs/home",asStarter = true)
public class HomeFragment extends AbsListFragment<Feed,HomeViewModel> {



    @Override
    protected void afterCreateView() {

    }

    @Override
    public PagedListAdapter getAdapter() {
        String category = getArguments() == null ? "all" : getArguments().getString("feedType");
        return new FeedAdapter(getContext(),category);
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {

    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {

    }
}