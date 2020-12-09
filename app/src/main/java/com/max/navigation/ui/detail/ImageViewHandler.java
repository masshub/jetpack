package com.max.navigation.ui.detail;

import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.max.navigation.R;
import com.max.navigation.databinding.ActivityFeedDetailImageBinding;
import com.max.navigation.databinding.FeedDetailImageHeaderBinding;
import com.max.navigation.model.Feed;
import com.max.navigation.view.MaxImageView;

/**
 * @author: maker
 * @date: 2020/12/9 16:35
 * @description:
 */
public class ImageViewHandler extends ViewHandler {

    private ActivityFeedDetailImageBinding feedDetailImageBinding;
    private FeedDetailImageHeaderBinding headerBinding;

    public ImageViewHandler(FragmentActivity activity) {
        super(activity);

        feedDetailImageBinding = DataBindingUtil.setContentView(activity, R.layout.activity_feed_detail_image);
        feedDetailImageBinding.setFeed(mFeed);
        mRecycleView = feedDetailImageBinding.recyclerView;
        feedDetailInteractionBinding = feedDetailImageBinding.feedDetailInteraction;
    }

    @Override
    public void bindInitData(Feed feed) {
        super.bindInitData(feed);
        headerBinding = FeedDetailImageHeaderBinding.inflate(LayoutInflater.from(mFragmentActivity), mRecycleView, false);
        headerBinding.setFeed(feed);
        MaxImageView maxImageView = headerBinding.mivFeedDetailImageHeader;
        maxImageView.bindData(feed.width, feed.height, feed.width > feed.height ? 0 : 16, mFeed.cover);

        feedCommentAdapter.addHeaders(headerBinding.getRoot());
        mRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                boolean visible = headerBinding.getRoot().getTop() <= -feedDetailImageBinding.flFeedDetailTitle.getMeasuredHeight();
                feedDetailImageBinding.feedDetailAuthorInfo.getRoot().setVisibility(visible ? View.VISIBLE : View.GONE);
                feedDetailImageBinding.flFeedDetailTitle.setVisibility(visible ? View.VISIBLE : View.GONE);
            }
        });


    }
}