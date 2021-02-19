package com.max.navigation.ui.my;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagedListAdapter;

import com.max.navigation.abs.AbsListFragment;
import com.max.navigation.exo.PageListPlayerDetector;
import com.max.navigation.model.Feed;
import com.max.navigation.ui.publish.PreviewActivity;
import com.scwang.smart.refresh.layout.api.RefreshLayout;

/**
 * @author: maker
 * @date: 2021/2/8 17:23
 * @description:
 */
public class ProfileListFragment extends AbsListFragment<Feed,ProfileViewModel> {

    private String tabType;
    private PageListPlayerDetector playerDetector;
    private boolean shouldPause = true;

    public static ProfileListFragment newInstance(String tabType) {

        Bundle args = new Bundle();
        args.putString(ProfileActivity.KEY_TAB_TYPE, tabType);
        ProfileListFragment fragment = new ProfileListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        playerDetector = new PageListPlayerDetector(this, recyclerView);
        mViewModel.setProfileType(tabType);
        refreshLayout.setEnableRefresh(false);

    }

    @Override
    public PagedListAdapter getAdapter() {
        tabType = getArguments().getString(ProfileActivity.KEY_TAB_TYPE);
        return new ProfileListAdapter(getContext(),tabType){

            @Override
            public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
               if(holder.isVideoItem()){
                   playerDetector.removeTarget(holder.getListPlayerView());
               }
            }

            @Override
            public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
                if(holder.isVideoItem()){
                    playerDetector.addTarget(holder.getListPlayerView());
                }
            }

            @Override
            public void onStartFeedDetailActivity(Feed feed) {
                shouldPause = false;
            }
        };
    }

    @Override
    public void onPause() {
        super.onPause();
        if(shouldPause){
            playerDetector.onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        playerDetector.onResume();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {

    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {

    }
}