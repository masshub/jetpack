package com.max.navigation.ui.home;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.max.common.ViewHelper;

/**
 * @author: maker
 * @date: 2020/12/7 11:08
 * @description:
 */
public class RoundFrameLayout extends FrameLayout {
    public RoundFrameLayout(@NonNull Context context) {
        this(context,null);
    }

    public RoundFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RoundFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);
    }

    public RoundFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        ViewHelper.setViewOutline(this,attrs,defStyleAttr,defStyleRes);
    }


    public void setViewOutline(int radius,int radiusSide){
        ViewHelper.setViewOutlineRadius(this,radius,radiusSide);
    }
}