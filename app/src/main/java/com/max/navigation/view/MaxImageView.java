package com.max.navigation.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

/**
 * @author: maker
 * @date: 2020/12/1 15:18
 * @description:
 */
public class MaxImageView extends AppCompatImageView {
    public MaxImageView(@NonNull Context context) {
        super(context);
    }

    public MaxImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MaxImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @BindingAdapter(value = {"image_url","isCircle"},requireAll = false)
    public static void setImageUrl(MaxImageView view,String imageUrl,boolean isCircle){
        RequestBuilder<Drawable> builder = Glide.with(view).load(imageUrl);
        if(isCircle){
            builder.transform(new CircleCrop());
        }

        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if(layoutParams != null && layoutParams.width > 0 && layoutParams.height > 0){
            builder.override(layoutParams.width,layoutParams.height);

        }

        builder.into(view);


    }
}