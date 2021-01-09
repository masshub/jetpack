package com.max.navigation.ui.detail;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.OverScroller;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;

import com.max.navigation.R;
import com.max.navigation.utils.PixUtils;
import com.max.navigation.view.FullScreenPlayerView;

/**
 * @author: maker
 * @date: 2021/1/8 17:06
 * @description:
 */
public class ViewZoomBehavior2 extends CoordinatorLayout.Behavior<FullScreenPlayerView> {

    private int scrollingId;
    private int mineHeight;

    private ViewDragHelper viewDragHelper;
    private View scrollingView;
    private FullScreenPlayerView child;
    private int childMeasuredHeight;
    private boolean canFullScreen;

    private ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            if (canFullScreen && child.getBottom() > mineHeight) {
                return true;
            }
            return false;
        }

        @Override
        public int getViewVerticalDragRange(@NonNull View child) {
            return viewDragHelper.getTouchSlop();
        }

        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            if (child == null || dy == 0) return 0;

            if ((dy < 0 && child.getBottom() < mineHeight) ||
                    (dy > 0 && child.getBottom() > childMeasuredHeight) ||
                    (dy > 0 && (scrollingView != null && scrollingView.canScrollVertically(-1)))) {
                return 0;
            }

            int maxConsumed = 0;
            if (dy > 0) {
                if (child.getBottom() + dy > childMeasuredHeight) {
                    maxConsumed = childMeasuredHeight - child.getBottom();
                } else {
                    maxConsumed = dy;
                }
            } else {
                if (child.getBottom() + dy < mineHeight) {
                    maxConsumed = mineHeight - child.getBottom();
                } else {
                    maxConsumed = dy;

                }
            }

            ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
            layoutParams.height = layoutParams.height + maxConsumed;
            child.setLayoutParams(layoutParams);

            if (mViewZoomCallback != null) {
                mViewZoomCallback.onDragZoom(layoutParams.height);
            }

            return maxConsumed;

        }

        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if (child.getBottom() > mineHeight && child.getBottom() > childMeasuredHeight && yvel != 0) {
                FlingRunnable flingRunnable = new FlingRunnable(child);
                flingRunnable.fling((int) xvel, (int) yvel);

            }


        }
    };


    @Override
    public boolean onTouchEvent(@NonNull CoordinatorLayout parent, @NonNull FullScreenPlayerView child, @NonNull MotionEvent ev) {
        if (!canFullScreen || viewDragHelper == null) {
            return super.onTouchEvent(parent, child, ev);
        }

        viewDragHelper.processTouchEvent(ev);
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull CoordinatorLayout parent, @NonNull FullScreenPlayerView child, @NonNull MotionEvent ev) {
        if (!canFullScreen || viewDragHelper == null) {
            return super.onInterceptTouchEvent(parent, child, ev);
        }
        return viewDragHelper.shouldInterceptTouchEvent(ev);
    }

    private ViewZoomCallback mViewZoomCallback;

    public void setViewZoomCallback(ViewZoomCallback viewZoomCallback) {
        this.mViewZoomCallback = viewZoomCallback;
    }

    public interface ViewZoomCallback {
        void onDragZoom(int height);
    }

    private OverScroller overScroller;


    private class FlingRunnable implements Runnable {

        private View mFlingView;

        public FlingRunnable(View flingView) {
            mFlingView = flingView;
        }

        public void fling(int xvel, int yvel) {
            overScroller.fling(0, mFlingView.getBottom(), xvel, yvel, 0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
            run();

        }

        @Override
        public void run() {
            ViewGroup.LayoutParams layoutParams = mFlingView.getLayoutParams();
            int height = layoutParams.height;
            if (overScroller.computeScrollOffset() && height >= mineHeight && height <= childMeasuredHeight) {
                int newHeight = Math.min(overScroller.getCurrY(), childMeasuredHeight);
                if (newHeight != height) {
                    layoutParams.height = newHeight;
                    mFlingView.setLayoutParams(layoutParams);
                    if (mViewZoomCallback != null) {
                        mViewZoomCallback.onDragZoom(newHeight);
                    }
                }
                ViewCompat.postOnAnimation(mFlingView, this);

            } else {
                mFlingView.removeCallbacks(this);
            }


        }
    }


    public ViewZoomBehavior2() {
    }

    public ViewZoomBehavior2(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.view_zoom_behavior, 0, 0);
        scrollingId = array.getResourceId(R.styleable.view_zoom_behavior_scrolling_id, 0);
        mineHeight = array.getDimensionPixelOffset(R.styleable.view_zoom_behavior_min_height, PixUtils.dp2px(200));
        array.recycle();

        overScroller = new OverScroller(context);
    }

    @Override
    public boolean onLayoutChild(@NonNull CoordinatorLayout parent, @NonNull FullScreenPlayerView child, int layoutDirection) {
        if (viewDragHelper == null) {
            viewDragHelper = ViewDragHelper.create(parent, 1.0f, mCallback);
            this.scrollingView = parent.findViewById(scrollingId);
            this.child = child;
            childMeasuredHeight = child.getMeasuredHeight();
            canFullScreen = childMeasuredHeight > parent.getMeasuredWidth();
        }
        return super.onLayoutChild(parent, child, layoutDirection);
    }


}