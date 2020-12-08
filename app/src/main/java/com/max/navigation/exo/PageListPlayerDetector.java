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
                if (dx == 0 && dy == 0) {
                    //时序问题。当执行了AdapterDataObserver#onItemRangeInserted  可能还没有被布局到RecyclerView上。
                    //所以此时 recyclerView.getChildCount()还是等于0的。
                    //等childView 被布局到RecyclerView上之后，会执行onScrolled（）方法
                    //并且此时 dx,dy都等于0
                    autoPlay();
                } else {
                    //如果有正在播放的,且滑动时被划出了屏幕 则 停止他
                    if (playingTarget != null && playingTarget.isPlaying() && !inTargetInBounds(playingTarget)) {
                        playingTarget.inActive();
                    }
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
        if (playingTarget != null && playingTarget.isPlaying() && inTargetInBounds(playingTarget)) {
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


    public void onResume() {
        if (playingTarget != null) {
            playingTarget.onActive();
        }


    }

    public void onPause() {
        if (playingTarget != null) {
            playingTarget.inActive();
        }
    }
}