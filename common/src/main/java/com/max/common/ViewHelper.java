package com.max.common;

import android.content.res.TypedArray;
import android.graphics.Outline;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.annotation.Nullable;

/**
 * @author: maker
 * @date: 2020/12/7 11:17
 * @description:
 */
public class ViewHelper {

    public static final int RADIUS_ALL = 0;
    public static final int RADIUS_TOP = 1;
    public static final int RADIUS_RIGHT = 2;
    public static final int RADIUS_BOTTOM = 3;
    public static final int RADIUS_LEFT = 4;

    public static void setViewOutline(View view, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray array = view.getContext().obtainStyledAttributes(attrs, R.styleable.viewOutLineStrategy, defStyleAttr, defStyleRes);
        int radius = array.getIndex(R.styleable.viewOutLineStrategy_radius);
        int radiusSide = array.getIndex(R.styleable.viewOutLineStrategy_radius_side);
        array.recycle();

        setViewOutlineRadius(view, radius, radiusSide);

    }

    public static void setViewOutlineRadius(View view, int radius, int radiusSide) {
        if (radius <= 0) {
            return;
        }

        view.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                int width = view.getWidth();
                int height = view.getHeight();
                if (width <= 0 || height <= 0) {
                    return;
                }

                int top = 0, left = 0, bottom = height, right = width;
                if (radiusSide != RADIUS_ALL) {
                    if (radiusSide == RADIUS_LEFT) {
                        right += radius;
                    } else if (radiusSide == RADIUS_TOP) {
                        bottom += radius;
                    } else if (radiusSide == RADIUS_RIGHT) {
                        left -= radius;
                    } else if (radiusSide == RADIUS_BOTTOM) {
                        top -= radius;
                    }

                    outline.setRoundRect(left, top, right, bottom, radius);
                    return;
                } else {
                    if (radius > 0) {
                        outline.setRoundRect(0, 0, width, height, radius);
                    } else {
                        outline.setRect(0, 0, width, height);

                    }

                }


            }
        });

        view.setClipToOutline(radius > 0);
        view.invalidate();


    }


}