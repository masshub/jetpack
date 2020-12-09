package com.max.navigation.ui.detail;

import androidx.annotation.CallSuper;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.max.navigation.databinding.FeedDetailInteractionBinding;
import com.max.navigation.model.Feed;

/**
 * @author: maker
 * @date: 2020/12/9 16:34
 * @description:
 */
public abstract class ViewHandler {
    protected FragmentActivity mFragmentActivity;
    protected RecyclerView mRecycleView;
    protected FeedDetailInteractionBinding feedDetailInteractionBinding;
    protected Feed mFeed;
    protected FeedCommentAdapter feedCommentAdapter;

    public ViewHandler(FragmentActivity activity) {
        this.mFragmentActivity = activity;
    }

    @CallSuper
    public void bindInitData(Feed feed) {
        mFeed = feed;

        mRecycleView.setLayoutManager(new LinearLayoutManager(mFragmentActivity, LinearLayoutManager.VERTICAL, false));
        mRecycleView.setItemAnimator(null);

        feedCommentAdapter = new FeedCommentAdapter();
        mRecycleView.setAdapter(feedCommentAdapter);


    }
}