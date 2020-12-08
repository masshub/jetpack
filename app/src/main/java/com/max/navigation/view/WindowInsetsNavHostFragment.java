package com.max.navigation.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.fragment.NavHostFragment;

/**
 * @author: maker
 * @date: 2020/12/5 10:20
 * @description:
 */
public class WindowInsetsNavHostFragment extends NavHostFragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        WindowInsetsFrameLayout layout = new WindowInsetsFrameLayout(inflater.getContext());
        layout.setId(getId());

        return layout;
    }
}