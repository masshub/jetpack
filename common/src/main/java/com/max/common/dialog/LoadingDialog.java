package com.max.common.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.max.common.R;

/**
 * @author: maker
 * @date: 2021/1/7 15:34
 * @description:
 */
public class LoadingDialog  extends AlertDialog {

    private TextView loadingText;

    public LoadingDialog(@NonNull Context context) {
        super(context);
    }

    public LoadingDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public void setLoadingText(String text){
        if (this.loadingText != null){
            loadingText.setText(text);
        }
    }


    @Override
    public void show() {
        super.show();
        setContentView(R.layout.dailog_loading);
        loadingText = findViewById(R.id.tv_loading);

        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.height = WindowManager.LayoutParams.WRAP_CONTENT;
        attributes.width = WindowManager.LayoutParams.WRAP_CONTENT;
        attributes.gravity = Gravity.CENTER;
        attributes.dimAmount = 0.35f;
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setAttributes(attributes);
    }
}