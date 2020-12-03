package com.max.navigation.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.max.common.AppGlobals;
import com.max.navigation.R;
import com.max.navigation.model.BottomBar;
import com.max.navigation.model.Destination;
import com.max.navigation.utils.AppConfig;

import java.util.List;

/**
 * Created by Maker on 2020/11/25.
 * Description:
 */
public class AppBottomBar extends BottomNavigationView {

    private static int[] sIcons = new int[]{R.drawable.icon_tab_home, R.drawable.icon_tab_sofa,
            R.drawable.icon_tab_publish, R.drawable.icon_tab_find, R.drawable.icon_tab_mine};

    public AppBottomBar(@NonNull Context context) {
        this(context, null);
    }

    public AppBottomBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("RestrictedApi")
    public AppBottomBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        BottomBar bottomBar = AppConfig.getBottomBar();
        List<BottomBar.Tabs> tabs = bottomBar.tabs;

        int[][] states = new int[2][];
        states[0] = new int[]{android.R.attr.state_selected};
        states[1] = new int[]{};

        int[] colors = new int[]{Color.parseColor(bottomBar.activeColor), Color.parseColor(bottomBar.inActiveColor)};
        ColorStateList colorStateList = new ColorStateList(states, colors);

        setItemIconTintList(colorStateList);
        setItemTextColor(colorStateList);
        setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        setSelectedItemId(bottomBar.selectTab);

        for (int i = 0; i < tabs.size(); i++) {
            BottomBar.Tabs tab = tabs.get(i);
            if (!tab.enable) {
                return;
            }

            int id = getId(tab.pageUrl);
            if (id < 0) return;
            MenuItem menuItem = getMenu().add(0, id, tab.index, tab.title);

            menuItem.setIcon(sIcons[tab.index]);

        }

        for (int i = 0; i < tabs.size(); i++) {
            BottomBar.Tabs tab  = tabs.get(i);
            int iconSize = dp2Px(tab.size);

            BottomNavigationMenuView bottomNavigationMenuView = (BottomNavigationMenuView) getChildAt(0);
            BottomNavigationItemView bottomNavigationItemView = (BottomNavigationItemView) bottomNavigationMenuView.getChildAt(tab.index);
            bottomNavigationItemView.setIconSize(iconSize);

            if(TextUtils.isEmpty(tab.title)){
                bottomNavigationItemView.setIconTintList(ColorStateList.valueOf(Color.parseColor(tab.tintColor)));
                bottomNavigationItemView.setShifting(false);
            }




        }


    }

    private int dp2Px(int size) {
        float value = AppGlobals.getApplication().getResources().getDisplayMetrics().density * size + 0.5f;
        return (int) value;
    }


    private int getId(String pageUrl) {
        Destination destination = AppConfig.getDestConfig().get(pageUrl);
        if (destination == null) {
            return -1;
        }
        return destination.getId();

    }
}
