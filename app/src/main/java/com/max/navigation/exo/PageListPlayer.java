package com.max.navigation.exo;

import android.app.Application;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.max.common.AppGlobals;
import com.max.navigation.R;

/**
 * @author: maker
 * @date: 2020/12/7 16:27
 * @description:
 */
public class PageListPlayer {
    public SimpleExoPlayer exoPlayer;
    public PlayerView playerView;
    public PlayerControlView controlView;

    public PageListPlayer() {
        Application application = AppGlobals.getApplication();
        exoPlayer = ExoPlayerFactory.newSimpleInstance(application, new DefaultRenderersFactory(application), new DefaultTrackSelector(), new DefaultLoadControl());


        playerView = (PlayerView) LayoutInflater.from(application).inflate(R.layout.exo_player_view, null, false);
        controlView = (PlayerControlView) LayoutInflater.from(application).inflate(R.layout.exo_player_control_view, null, false);

    }

    public void release() {
        if(exoPlayer != null){
            exoPlayer.setPlayWhenReady(false);
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer = null;
        }

        if(playerView != null){
            playerView.setPlayer(null);
            playerView = null;
        }

        if(controlView != null){
            controlView.setPlayer(null);
            controlView.addVisibilityListener(null);
            controlView = null;
        }

    }
}