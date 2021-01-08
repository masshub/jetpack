package com.max.navigation.view;

import android.content.Context;
import android.media.session.PlaybackState;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.max.navigation.R;
import com.max.navigation.exo.PageListPlayer;
import com.max.navigation.exo.PageListPlayerManager;
import com.max.navigation.utils.PixUtils;

/**
 * @author: maker
 * @date: 2021/1/8 10:37
 * @description:
 */
public class FullScreenPlayerView2 extends ListPlayerView {

    private PlayerView playerView;

    public FullScreenPlayerView2(@NonNull Context context) {
        this(context, null);
    }

    public FullScreenPlayerView2(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FullScreenPlayerView2(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        playerView = (PlayerView) LayoutInflater.from(context).inflate(R.layout.exo_player_view, null, false);
    }

    @Override
    protected void setSize(int widthPx, int heightPx) {
        if (widthPx >= heightPx) {
            super.setSize(widthPx, heightPx);
        }


        int width = PixUtils.getScreenWidth();
        int height = PixUtils.getScreenHeight();

        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        setLayoutParams(layoutParams);

        FrameLayout.LayoutParams coverLayoutParams = (LayoutParams) videoCover.getLayoutParams();
        coverLayoutParams.width = (int) (widthPx / (heightPx * 1.0f / height));
        coverLayoutParams.height = height;
        coverLayoutParams.gravity = Gravity.CENTER;
        videoCover.setLayoutParams(coverLayoutParams);


    }


    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        if(mHeightPx > mWidthPx){
            int layoutHeight = params.height;
            int layoutWidth = params.width;

            ViewGroup.LayoutParams layoutParams = videoCover.getLayoutParams();
            layoutParams.width = (int) (layoutWidth * (mHeightPx * 1.0f / layoutHeight ));
            layoutParams.height = layoutHeight;
            videoCover.setLayoutParams(layoutParams);
            if(playerView != null){
                ViewGroup.LayoutParams playerViewLayoutParams = playerView.getLayoutParams();
                if(playerViewLayoutParams != null){
                    float scaleX = layoutParams.width * 1.0f / playerViewLayoutParams.width;
                    float scaleY =  layoutParams.height * 1.0f / playerViewLayoutParams.height;
                    playerView.setScaleX(scaleX);
                    playerView.setScaleY(scaleY);
                }
            }
        }
        super.setLayoutParams(params);
    }




    @Override
    public void onActive() {
        PageListPlayer pageListPlayer = PageListPlayerManager.get(mCategory);
        PlayerView playerView1 = playerView;
        PlayerControlView controlView = pageListPlayer.controlView;
        SimpleExoPlayer exoPlayer = pageListPlayer.exoPlayer;
        if (playerView1 == null) {
            return;
        }
        pageListPlayer.switchPlayerView(playerView1);

        ViewParent parent = playerView1.getParent();
        if (parent != this) {
            if (parent != null) {
                ((ViewGroup) parent).removeView(playerView1);
            }
            ViewGroup.LayoutParams coverLayoutParams = videoCover.getLayoutParams();
            this.addView(playerView1, 1, coverLayoutParams);
        }

        ViewParent controlParent = controlView.getParent();
        if (controlParent != this) {
            if (controlParent != null) {
                ((ViewGroup) controlParent).removeView(controlView);
                LayoutParams controlLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                controlLayoutParams.gravity = Gravity.BOTTOM;
                this.addView(controlView, controlLayoutParams);

            }
        }

        if (TextUtils.equals(pageListPlayer.videoUrl, mVideoUrl)) {
            onPlayerStateChanged(true, Player.STATE_READY);
        } else {
            MediaSource mediaSource = PageListPlayerManager.createMediaSource(mVideoUrl);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);
            pageListPlayer.videoUrl = mVideoUrl;
        }

        controlView.show();
        controlView.addVisibilityListener(this);
        exoPlayer.addListener(this);
        exoPlayer.setPlayWhenReady(true);


    }


    @Override
    public void inActive() {
        super.inActive();
        PageListPlayer pageListPlayer = PageListPlayerManager.get(mCategory);
        pageListPlayer.switchPlayerView(null);
    }
}