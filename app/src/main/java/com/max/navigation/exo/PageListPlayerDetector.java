package com.max.navigation.exo;

import android.graphics.Point;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: maker
 * @date: 2020/12/7 17:13
 * @description:
 */
public class PageListPlayerDetector {

    private List<IPlayerTarget> playerTargets = new ArrayList<>();
    private RecyclerView recyclerView;
    private IPlayerTarget playingTarget;


    public void addTarget(IPlayerTarget playerTarget) {
        playerTargets.add(playerTarget);
    }

    public void removeTarget(IPlayerTarget playerTarget) {
        playerTargets.remove(playerTarget);
    }

    public PageListPlayerDetector(LifecycleOwner owner, RecyclerView recyclerView) {
        this.recyclerView = recyclerView;

        owner.getLifecycle().addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    recyclerView.getAdapter().unregisterAdapterDataObserver(adapterDataObserver);
                    owner.getLifecycle().removeObserver(this);
                }
            }
        });

        recyclerView.getAdapter().registerAdapterDataObserver(adapterDataObserver);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    autoPlay();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(playingTarget != null && playingTarget.isPlaying() && !inTargetInBounds(playingTarget)){
                    playingTarget.inActive();
                }
            }
        });
    }

    private RecyclerView.AdapterDataObserver adapterDataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            autoPlay();
        }


    };

    private void autoPlay() {
        if (playerTargets.size() <= 0 || recyclerView.getChildCount() <= 0) {
            return;
        }

        // 若上一个视频仍在屏幕中播放，则无需计算视频播放位置
        if(playingTarget != null && playingTarget.isPlaying() && inTargetInBounds(playingTarget)){
            return;
        }

        IPlayerTarget activeTarget = null;
        for (IPlayerTarget playerTarget : playerTargets) {
            if (inTargetInBounds(playerTarget)) {
                activeTarget = playerTarget;
                break;
            }
        }

        if (activeTarget != null) {
            if (playingTarget != null && playingTarget.isPlaying()) {
                playingTarget.inActive();
            }
            playingTarget = activeTarget;
            activeTarget.onActive();
        }


    }

    private Boolean inTargetInBounds(IPlayerTarget playerTarget) {
        ViewGroup view = playerTarget.getOwner();
        ensureRecycleViewLocation();


        if (!view.isShown() || !view.isAttachedToWindow()) {
            return false;
        }

        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int center = location[1] + view.getHeight() / 2;
        return center >= rvLocation.x && center <= rvLocation.y;
    }

    private Point rvLocation = null;

    private Point ensureRecycleViewLocation() {
        if (rvLocation == null) {
            int[] location = new int[2];
            recyclerView.getLocationOnScreen(location);

            int top = location[1];
            int bottom = top + recyclerView.getHeight();
            rvLocation = new Point(top, bottom);
        }
        return rvLocation;
    }


}