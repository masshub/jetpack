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
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
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
    public String videoUrl;

    public PageListPlayer() {
        Application application = AppGlobals.getApplication();
        exoPlayer = new SimpleExoPlayer.Builder(application,
                new DefaultRenderersFactory(application))
                .setBandwidthMeter(new DefaultBandwidthMeter.Builder(application).build())
                .setTrackSelector(new DefaultTrackSelector(application))
                .setLoadControl(new DefaultLoadControl())
                .build();

        playerView = (PlayerView) LayoutInflater.from(application).inflate(R.layout.exo_player_view, null, false);
        controlView = (PlayerControlView) LayoutInflater.from(application).inflate(R.layout.exo_player_control_view, null, false);

        playerView.setPlayer(exoPlayer);
        controlView.setPlayer(exoPlayer);

    }

    public void release() {
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(false);
            exoPlayer.stop(true);
            exoPlayer.release();
            exoPlayer = null;
        }

        if (playerView != null) {
            playerView.setPlayer(null);
            playerView = null;
        }

        if (controlView != null) {
            controlView.setPlayer(null);
            controlView.addVisibilityListener(null);
            controlView = null;
        }

    }


    public void switchPlayerViewS(PlayerView playerView){
        if(playerView != null && playerView != this.playerView){
            this.playerView.setPlayer(null);
            playerView.setPlayer(this.exoPlayer);
        }else{
            this.playerView.setPlayer(this.exoPlayer);
        }
    }

    public void switchPlayerView(PlayerView playerView) {
        if (playerView != null && playerView != this.playerView) {
            this.playerView.setPlayer(null);
            playerView.setPlayer(this.exoPlayer);
        } else {
            this.playerView.setPlayer(this.exoPlayer);
        }
    }
}