package com.max.navigation.ui.find;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.max.libnavannotation.FragmentDestination;
import com.max.navigation.model.SofaTab;
import com.max.navigation.ui.sofa.SofaFragment;
import com.max.navigation.utils.AppConfig;

@FragmentDestination(pageUrl = "main/tabs/find",asStarter = false)
public class FindFragment extends SofaFragment {

    @Override
    public Fragment getTabFragment(int position) {
        FindItemFragment findItemFragment = FindItemFragment.newInstance(getTabConfig().tabs.get(position).tag);
        return findItemFragment;
    }

    @Override
    public void onAttachFragment(@NonNull Fragment childFragment) {
        super.onAttachFragment(childFragment);
        String tagType = childFragment.getArguments().getString(FindItemFragment.TAG_TYPE);
        if(TextUtils.equals(tagType,"onlyFollow")){
            ViewModelProviders.of(childFragment).get(FindItemViewModel.class)
                    .getSwitchFindTab()
                    .observe(this, new Observer() {
                        @Override
                        public void onChanged(Object o) {
                            viewPager2.setCurrentItem(1);

                        }
                    });
        }
    }

    @Override
    public SofaTab getTabConfig() {
        return AppConfig.getFindTab();
    }
}