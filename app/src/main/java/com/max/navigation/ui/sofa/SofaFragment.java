package com.max.navigation.ui.sofa;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.max.libnavannotation.FragmentDestination;
import com.max.navigation.R;
import com.max.navigation.databinding.FragmentSofaBinding;
import com.max.navigation.model.SofaTab;
import com.max.navigation.ui.home.HomeFragment;
import com.max.navigation.utils.AppConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@FragmentDestination(pageUrl = "main/tabs/sofa", asStarter = false)
public class SofaFragment extends Fragment {
    private FragmentSofaBinding binding;
    private TabLayout tabLayout;
    public ViewPager2 viewPager2;
    private SofaTab tabConfig;
    private List<SofaTab.Tabs> tabs;
    private HashMap<Integer, Fragment> fragments = new HashMap<>();
    private TabLayoutMediator mediator;
    private String TAG = "SofaFragment";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSofaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tabLayout = binding.tlSofaTab;
        viewPager2 = binding.vp2SofaPage;

        tabConfig = getTabConfig();
        tabs = new ArrayList<>();
        for (SofaTab.Tabs tab : tabConfig.tabs) {
            if (tab.enable) {
                tabs.add(tab);
            }
        }

        // 禁止预加载机制
        viewPager2.setOffscreenPageLimit(ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT);
        viewPager2.setAdapter(new FragmentStateAdapter(getChildFragmentManager(), getLifecycle()) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                Fragment fragment = fragments.get(position);
                if (fragment == null) {
                    fragment = getTabFragment(position);
                }
                return fragment;
            }

            @Override
            public int getItemCount() {
                return tabs.size();
            }
        });


        mediator = new TabLayoutMediator(tabLayout, viewPager2, false, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setCustomView(makeTabView(position));

            }
        });

        viewPager2.registerOnPageChangeCallback(onPageChangeCallback);
        mediator.attach();

        viewPager2.post(() -> viewPager2.setCurrentItem(tabConfig.select));

    }


    ViewPager2.OnPageChangeCallback onPageChangeCallback = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageSelected(int position) {
            int tabCount = tabLayout.getTabCount();
            for (int i = 0; i < tabCount; i++) {
                TabLayout.Tab tab = tabLayout.getTabAt(i);
                TextView textView = (TextView) tab.getCustomView();
                if (tab.getPosition() == position) {
                    textView.setTextSize(tabConfig.activeSize);
                    textView.setTypeface(Typeface.DEFAULT_BOLD);
                } else {
                    textView.setTextSize(tabConfig.normalSize);
                    textView.setTypeface(Typeface.DEFAULT);

                }
            }
        }
    };

    private View makeTabView(int position) {
        TextView textView = new TextView(getContext());
        int[][] states = new int[2][];
        states[0] = new int[]{android.R.attr.state_selected};
        states[1] = new int[]{};

        int[] colors = new int[]{Color.parseColor(tabConfig.activeColor), Color.parseColor(tabConfig.normalColor)};
        ColorStateList colorStateList = new ColorStateList(states, colors);
        textView.setTextColor(colorStateList);
        textView.setText(tabs.get(position).title);
        textView.setTextSize(tabConfig.normalSize);
        return textView;
    }

    public Fragment getTabFragment(int position) {
        Log.e(TAG, "getTabFragment: feed type " +  tabs.get(position).tag);
        return HomeFragment.newInstance(tabs.get(position).tag);
    }

    public SofaTab getTabConfig() {
        return AppConfig.getSofaTab();
    }

    @Override
    public void onDestroy() {
        mediator.detach();
        viewPager2.unregisterOnPageChangeCallback(onPageChangeCallback);
        super.onDestroy();
    }
}