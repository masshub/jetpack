package com.max.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.max.common.ViewHelper;

/**
 * @author: maker
 * @date: 2021/2/8 16:33
 * @description:
 */
public class CornerLinearLayout extends LinearLayout {
    public CornerLinearLayout(Context context) {
        this(context, null);
    }

    public CornerLinearLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CornerLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CornerLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        ViewHelper.setViewOutline(this, attrs, defStyleAttr, defStyleRes);
    }


}