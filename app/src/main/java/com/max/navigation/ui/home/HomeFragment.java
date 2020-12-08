package com.max.navigation.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.paging.ItemKeyedDataSource;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;

import com.max.libnavannotation.FragmentDestination;
import com.max.navigation.abs.AbsListFragment;
import com.max.navigation.data.MutableDataSource;
import com.max.navigation.exo.PageListPlayerDetector;
import com.max.navigation.model.Feed;
import com.scwang.smart.refresh.layout.api.RefreshLayout;

import java.util.List;

@FragmentDestination(pageUrl = "main/tabs/home", asStarter = true)
public class HomeFragment extends AbsListFragment<Feed, HomeViewModel> {


    private PageListPlayerDetector pageListPlayerDetector;
    private String TAG = "HomeFragment";
    private static String feedType;


    public static HomeFragment newInstance(String feedType) {
        Bundle args = new Bundle();
        args.putString("feedType", feedType);
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel.getCacheLiveData().observe(getViewLifecycleOwner(), new Observer<PagedList<Feed>>() {
            @Override
            public void onChanged(PagedList<Feed> feeds) {
                adapter.submitList(feeds);
            }
        });

        pageListPlayerDetector = new PageListPlayerDetector(this, recyclerView);

    }


    @Override
    public PagedListAdapter getAdapter() {
        feedType = getArguments() == null ? "all" : getArguments().getString("feedType");
        return new FeedAdapter(getContext(), feedType) {
            @Override
            public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
                super.onViewAttachedToWindow(holder);
                if (holder.isVideoItem()) {
                    pageListPlayerDetector.addTarget(holder.getListPlayerView());
                }
            }

            @Override
            public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
                super.onViewDetachedFromWindow(holder);
                pageListPlayerDetector.removeTarget(holder.getListPlayerView());
            }
        };
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            pageListPlayerDetector.onPause();
        } else {
            pageListPlayerDetector.onResume();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getParentFragment() != null) {
            if (getParentFragment().isVisible() && isVisible()) {
                pageListPlayerDetector.onResume();
            }
        } else {
            pageListPlayerDetector.onResume();
        }

    }

    @Override
    public void onPause() {
        pageListPlayerDetector.onPause();
        super.onPause();

    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        Feed feed = adapter.getCurrentList().get(adapter.getItemCount() - 1);
        mViewModel.loadAfter(feed.id, new ItemKeyedDataSource.LoadCallback<Feed>() {

            @Override
            public void onResult(@NonNull List<Feed> data) {
                PagedList.Config config = adapter.getCurrentList().getConfig();
                if (data != null && data.size() > 0) {
                    MutableDataSource dataSource = new MutableDataSource();
                    dataSource.data.addAll(data);
                    PagedList pagedList = dataSource.buildNewPagedList(config);
                    submitList(pagedList);

                }


            }
        });


    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        mViewModel.getDataSource().invalidate();
    }


}