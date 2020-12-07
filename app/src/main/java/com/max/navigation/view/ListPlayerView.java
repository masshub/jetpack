package com.max.navigation.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.max.navigation.R;
import com.max.navigation.exo.IPlayerTarget;
import com.max.navigation.exo.PageListPlayer;
import com.max.navigation.exo.PageListPlayerManager;
import com.max.navigation.utils.PixUtils;

/**
 * @author: maker
 * @date: 2020/12/2 16:14
 * @description:
 */
public class ListPlayerView extends FrameLayout implements IPlayerTarget {
    private MaxImageView videoBackground, videoCover;
    private ImageView videoPlay;
    private ProgressBar videoProgressBar;
    private String mCategory;
    private String mVideoUrl;
    private boolean isPlaying;

    public ListPlayerView(@NonNull Context context) {
        this(context, null);
    }

    public ListPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ListPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.player_view, this, true);
        videoBackground = findViewById(R.id.miv_video_background);
        videoCover = findViewById(R.id.miv_video_cover);
        videoProgressBar = findViewById(R.id.pb_video_progress);
        videoPlay = findViewById(R.id.iv_video_play);
    }

    public void bindData(String category, int widthPx, int heightPx, String coverUrl, String videoUrl) {
        mCategory = category;
        mVideoUrl = videoUrl;

//        视频封面
        videoCover.setImageUrl(videoCover, coverUrl, false);
        if (widthPx < heightPx) {
            videoBackground.setBackgroundImageUrl(coverUrl, 10);
            videoBackground.setVisibility(VISIBLE);
        } else {
            videoBackground.setVisibility(INVISIBLE);
        }

        setSize(widthPx, heightPx);
    }

    protected void setSize(int widthPx, int heightPx) {
        int maxWidth = PixUtils.getScreenWidth();
        int maxHeight = maxWidth;

        int layoutWidth = maxWidth;
        int layoutHeight = 0;

        int coverWidth;
        int coverHeight;

        if (widthPx >= heightPx) {
            coverWidth = maxWidth;
            layoutHeight = coverHeight = (int) (heightPx / (widthPx * 1.0f / maxWidth));
        } else {
            layoutHeight = coverHeight = maxHeight;
            coverWidth = (int) (widthPx / (heightPx * 1.0f / maxHeight));
        }

        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width = layoutWidth;
        layoutParams.height = layoutHeight;
        setLayoutParams(layoutParams);

        ViewGroup.LayoutParams backgroundLayoutParams = videoBackground.getLayoutParams();
        backgroundLayoutParams.width = layoutWidth;
        backgroundLayoutParams.height = layoutHeight;
        videoBackground.setLayoutParams(backgroundLayoutParams);

        FrameLayout.LayoutParams coverLayoutParams = (LayoutParams) videoCover.getLayoutParams();
        coverLayoutParams.width = coverWidth;
        coverLayoutParams.height = coverHeight;
        coverLayoutParams.gravity = Gravity.CENTER;
        videoCover.setLayoutParams(coverLayoutParams);

        FrameLayout.LayoutParams playLayoutParams = (LayoutParams) videoPlay.getLayoutParams();
        playLayoutParams.gravity = Gravity.CENTER;
        videoPlay.setLayoutParams(playLayoutParams);


    }


    @Override
    public ViewGroup getOwner() {
        return this;
    }

    @Override
    public void onActive() {
        PageListPlayer pageListPlayer = PageListPlayerManager.get(mCategory);
        PlayerView playerView = pageListPlayer.playerView;
        PlayerControlView controlView = pageListPlayer.controlView;
        SimpleExoPlayer exoPlayer = pageListPlayer.exoPlayer;
        ViewParent parent = playerView.getParent();
        if(parent != this){
            if(parent != null){
                ((ViewGroup)parent).removeView(playerView);
            }
            ViewGroup.LayoutParams coverLayoutParams = videoCover.getLayoutParams();
            this.addView(playerView,1,coverLayoutParams);

        }

    }

    @Override
    public void inActive() {

    }

    @Override
    public boolean isPlaying() {
        return isPlaying;
    }
}