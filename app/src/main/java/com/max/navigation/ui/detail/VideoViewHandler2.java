package com.max.navigation.ui.detail;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.FragmentActivity;

import com.max.navigation.R;
import com.max.navigation.databinding.FeedDetailVideoHeader2Binding;
import com.max.navigation.databinding.FeedDetailVideoTypeBinding;
import com.max.navigation.model.Feed;

/**
 * @author: maker
 * @date: 2021/1/8 16:23
 * @description:
 */
public class VideoViewHandler2 extends ViewHandler {

    private FeedDetailVideoTypeBinding videoTypeBinding;
    private String category;
    private boolean backPressed;

    public VideoViewHandler2(FragmentActivity activity) {
        super(activity);

        videoTypeBinding = DataBindingUtil.setContentView(activity, R.layout.feed_detail_video_type);

        feedDetailInteractionBinding = videoTypeBinding.bottomInteraction;
        mRecycleView = videoTypeBinding.recyclerView;

        View authorInfoView = videoTypeBinding.authorInfo.getRoot();
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) authorInfoView.getLayoutParams();
        layoutParams.setBehavior(new ViewAnchorBehavior2(R.id.player_view));
    }

    @Override
    public void bindInitData(Feed feed) {
        super.bindInitData(feed);
        videoTypeBinding.setFeed(feed);
        category = mFragmentActivity.getIntent().getStringExtra(FeedDetailActivity.KEY_CATEGORY);
        videoTypeBinding.playerView.bindData(category, mFeed.width, mFeed.height, mFeed.cover, mFeed.url);

        videoTypeBinding.playerView.post(new Runnable() {
            @Override
            public void run() {
                boolean fullScreen = videoTypeBinding.playerView.getBottom() >= videoTypeBinding.coordinator.getBottom();
                setViewAppearance(fullScreen);

            }
        });

        FeedDetailVideoHeader2Binding header2Binding = FeedDetailVideoHeader2Binding.inflate(LayoutInflater.from(mFragmentActivity), mRecycleView, false);
        header2Binding.setFeed(feed);
        feedCommentAdapter.addHeaders(header2Binding.getRoot());
    }

    private void setViewAppearance(boolean fullScreen) {
        videoTypeBinding.setFullScreen(fullScreen);
        videoTypeBinding.fullscreenAuthorInfo.getRoot().setVisibility(fullScreen ? View.VISIBLE : View.GONE);
        int measuredHeight = feedDetailInteractionBinding.getRoot().getMeasuredHeight();
        int playControllerHeight = videoTypeBinding.playerView.getPlayController().getMeasuredHeight();
        int bottom = videoTypeBinding.playerView.getPlayController().getBottom();
        videoTypeBinding.playerView.getPlayController().setY(fullScreen ? bottom - playControllerHeight - measuredHeight : bottom - playControllerHeight);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backPressed = true;
        videoTypeBinding.playerView.getPlayController().setTranslationY(0);
    }

    @Override
    public void onResume() {
        super.onResume();
        backPressed  = false;
        videoTypeBinding.playerView.onActive();

    }

    @Override
    public void onPause() {
        super.onPause();
        if(!backPressed){
            videoTypeBinding.playerView.inActive();
        }
    }
}