package com.max.navigation.ui.detail;

import android.view.LayoutInflater;
import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;

import com.max.navigation.R;
import com.max.navigation.databinding.FeedDetailVideoHeaderBinding;
import com.max.navigation.databinding.FeedDetailVideoTypeBinding;
import com.max.navigation.model.Feed;
import com.max.navigation.view.FullScreenPlayerView;

/**
 * @author: maker
 * @date: 2020/12/9 16:36
 * @description:
 */
public class VideoViewHandler extends ViewHandler {
    private final CoordinatorLayout coordinator;
    private FullScreenPlayerView playerView;
    private FeedDetailVideoTypeBinding mVideoBinding;
    private String category;
    private boolean backPressed;

    public VideoViewHandler(FragmentActivity activity) {
        super(activity);

        mVideoBinding = DataBindingUtil.setContentView(activity, R.layout.feed_detail_video_type);

        feedDetailInteractionBinding = mVideoBinding.bottomInteraction;
        mRecycleView = mVideoBinding.recyclerView;
        playerView = mVideoBinding.playerView;
        coordinator = mVideoBinding.coordinator;

        View authorInfoView = mVideoBinding.authorInfo.getRoot();
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) authorInfoView.getLayoutParams();
        params.setBehavior(new ViewAnchorBehavior(R.id.player_view));


        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) playerView.getLayoutParams();
        ViewZoomBehavior behavior = (ViewZoomBehavior) layoutParams.getBehavior();
        behavior.setViewZoomCallback(new ViewZoomBehavior.ViewZoomCallback() {
            @Override
            public void onDragZoom(int height) {
                int bottom = playerView.getBottom();
                boolean moveUp = height < bottom;
                boolean fullscreen = moveUp ? height >= coordinator.getBottom() - feedDetailInteractionBinding.getRoot().getHeight()
                        : height >= coordinator.getBottom();
                setViewAppearance(fullscreen);
            }
        });

    }

    @Override
    public void bindInitData(Feed feed) {
        super.bindInitData(feed);
        mVideoBinding.setFeed(feed);

        category = mFragmentActivity.getIntent().getStringExtra(FeedDetailActivity.KEY_CATEGORY);
        playerView.bindData(category, mFeed.width, mFeed.height, mFeed.cover, mFeed.url);

        //这里需要延迟一帧 等待布局完成，再来拿playerView的bottom值 和 coordinator的bottom值
        //做个比较。来校验是否进入详情页时时视频在全屏播放
        playerView.post(() -> {
            boolean fullscreen = playerView.getBottom() >= coordinator.getBottom();
            setViewAppearance(fullscreen);
        });

        //给headerview、 绑定数据并添加到列表之上
        FeedDetailVideoHeaderBinding headerBinding = FeedDetailVideoHeaderBinding.inflate(
                LayoutInflater.from(mFragmentActivity),
                mRecycleView,
                false);

        headerBinding.setFeed(mFeed);
        feedCommentAdapter.addHeaders(headerBinding.getRoot());

    }

    private void setViewAppearance(boolean fullscreen) {
        mVideoBinding.setFullScreen(fullscreen);
        feedDetailInteractionBinding.setFullScreen(fullscreen);
        mVideoBinding.fullscreenAuthorInfo.getRoot().setVisibility(fullscreen ? View.VISIBLE : View.GONE);

        //底部互动区域的高度
        int inputHeight = feedDetailInteractionBinding.getRoot().getMeasuredHeight();
        //播放控制器的高度
        int ctrlViewHeight = playerView.getPlayController().getMeasuredHeight();
        //播放控制器的bottom值
        int bottom = playerView.getPlayController().getBottom();
        //全屏播放时，播放控制器需要处在底部互动区域的上面
        playerView.getPlayController().setY(fullscreen ? bottom - inputHeight - ctrlViewHeight
                : bottom - ctrlViewHeight);
        feedDetailInteractionBinding.tvFeedDetailInput.setBackgroundResource(fullscreen ? R.drawable.bg_comment_edit2 : R.drawable.bg_comment_edit);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backPressed = true;
        //按了返回键后需要 恢复 播放控制器的位置。否则回到列表页时 可能会不正确的显示
        playerView.getPlayController().setTranslationY(0);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!backPressed) {
            playerView.inActive();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        backPressed = false;
        playerView.onActive();
    }
}