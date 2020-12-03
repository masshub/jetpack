package com.max.common.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import com.google.android.material.button.MaterialButton;
import com.max.common.R;

/**
 * @author: maker
 * @date: 2020/12/2 17:47
 * @description:
 */
public class EmptyView extends LinearLayout {
    private ImageView emptyIcon;
    private TextView emptyText;
    private MaterialButton emptyAction;

    public EmptyView(Context context) {
        this(context, null);
    }

    public EmptyView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmptyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);

        LayoutInflater.from(context).inflate(R.layout.empty_view, this, true);
        emptyIcon = findViewById(R.id.iv_empty);
        emptyText = findViewById(R.id.tv_empty);
        emptyAction = findViewById(R.id.mb_empty_action);
    }

    public void setEmptyIcon(@DrawableRes int drawableRes) {
        emptyIcon.setImageResource(drawableRes);
    }

    public void setEmptyText(String text) {
        if (TextUtils.isEmpty(text)) {
            emptyText.setVisibility(GONE);
        } else {
            emptyText.setText(text);
            emptyText.setVisibility(VISIBLE);
        }
    }


    public void setEmptyAction(String text, OnClickListener onClickListener) {
        if (TextUtils.isEmpty(text)) {
            emptyAction.setVisibility(GONE);
        } else {
            emptyAction.setText(text);
            emptyAction.setVisibility(VISIBLE);
            emptyAction.setOnClickListener(onClickListener);
        }
    }


}