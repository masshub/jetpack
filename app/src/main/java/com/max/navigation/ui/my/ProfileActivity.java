package com.max.navigation.ui.my;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.max.navigation.R;
import com.max.navigation.databinding.ActivityProfileBinding;
import com.max.navigation.model.User;
import com.max.navigation.ui.login.UserManager;

/**
 * @author: maker
 * @date: 2021/2/8 15:18
 * @description:
 */
public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding mBinding;
    public static final String TAB_TYPE_ALL = "tab_all";
    public static final String TAB_TYPE_FEED = "tab_feed";
    public static final String TAB_TYPE_COMMENT = "tab_comment";
    public static final String KEY_TAB_TYPE = "key_tab_type";

    public static void startProfileActivity(Context context, String type) {
        Intent intent = new Intent();
        intent.putExtra(KEY_TAB_TYPE, type);
        context.startActivity(intent);

    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile);

        User user = UserManager.get().getUser();
        mBinding.setUser(user);
        mBinding.actionBack.setOnClickListener(v -> {
            finish();
        });

        String[] tabs = getResources().getStringArray(R.array.profile_tabs);
        ViewPager2 viewPager2 = mBinding.vpProfilePage;
        TabLayout tabLayout = mBinding.tlProfileTab;
        new TabLayoutMediator(tabLayout, viewPager2, false, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(tabs[position]);
            }
        }).attach();

        int initTabPosition = getInitTabPosition();
        if(initTabPosition != 0){
            viewPager2.post(()->{viewPager2.setCurrentItem(initTabPosition);});
        }

        viewPager2.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return ProfileListFragment.newInstance(getTabTypeByPosition(position));
            }

            private String getTabTypeByPosition(int position) {
                switch (position){
                    case 0:
                        return TAB_TYPE_ALL;
                    case 1:
                        return TAB_TYPE_FEED;
                    case 2:
                        return TAB_TYPE_COMMENT;
                }
                return TAB_TYPE_ALL;
            }

            @Override
            public int getItemCount() {
                return tabs.length;
            }
        });

        mBinding.appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                boolean expand = Math.abs(verticalOffset) < appBarLayout.getTotalScrollRange();
                mBinding.setExpand(expand);
            }
        });

    }

    private int getInitTabPosition(){
        String type = getIntent().getStringExtra(KEY_TAB_TYPE);
        switch (type){
            case TAB_TYPE_FEED:
                return 1;
            case TAB_TYPE_COMMENT:
                return 2;
            default:
                return 0;
        }
    }
}