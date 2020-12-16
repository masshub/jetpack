package com.max.navigation.ui.detail;

import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.CallSuper;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.max.common.view.EmptyView;
import com.max.navigation.R;
import com.max.navigation.databinding.CustomDataBinding;
import com.max.navigation.model.Comment;
import com.max.navigation.model.Feed;

/**
 * @author: maker
 * @date: 2020/12/9 16:34
 * @description:
 */
public abstract class ViewHandler {
    protected FragmentActivity mFragmentActivity;
    protected RecyclerView mRecycleView;
    protected CustomDataBinding feedDetailInteractionBinding;
    protected Feed mFeed;
    protected FeedCommentAdapter feedCommentAdapter;
    private final FeedDetailViewModel feedDetailViewModel;

    public ViewHandler(FragmentActivity activity) {
        mFragmentActivity = activity;
        feedDetailViewModel = ViewModelProviders.of(activity).get(FeedDetailViewModel.class);
    }

    @CallSuper
    public void bindInitData(Feed feed) {
        Log.e("bindInitData", "bindInitData: " +  mFragmentActivity.getLocalClassName());
//        Log.e("bindInitData", "bindInitData: " +  feedDetailInteractionBinding.toString());

        feedDetailInteractionBinding.setOwner(mFragmentActivity);
        mFeed = feed;

        mRecycleView.setLayoutManager(new LinearLayoutManager(mFragmentActivity, LinearLayoutManager.VERTICAL, false));
        mRecycleView.setItemAnimator(null);

        feedCommentAdapter = new FeedCommentAdapter();
        mRecycleView.setAdapter(feedCommentAdapter);


        feedDetailViewModel.setItemId(feed.itemId);

        feedDetailViewModel.getPageData().observe(mFragmentActivity, new Observer<PagedList<Comment>>() {
            @Override
            public void onChanged(PagedList<Comment> comments) {
                feedCommentAdapter.submitList(comments);
                handleEmpty(comments.size() > 0);

            }
        });


    }

    private EmptyView emptyView;
    private void handleEmpty(boolean hasData) {
        if(hasData){
            if(emptyView != null){
                feedCommentAdapter.removeHeaderView(emptyView);
            }

        } else {
            if(emptyView == null){
                emptyView = new EmptyView(mFragmentActivity);
                emptyView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                emptyView.setEmptyText(mFragmentActivity.getString(R.string.empty_comment));
                feedCommentAdapter.addHeaders(emptyView);
            }


        }

    }
}