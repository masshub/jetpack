package com.max.navigation.ui.my;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.max.libnavannotation.FragmentDestination;
import com.max.navigation.R;
import com.max.navigation.databinding.FragmentMyBinding;
import com.max.navigation.model.User;
import com.max.navigation.ui.login.UserManager;
import com.max.navigation.utils.StatusBar;

@FragmentDestination(pageUrl = "main/tabs/my", asStarter = false)
public class MyFragment extends Fragment {
    private FragmentMyBinding mBinding;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentMyBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        User user = UserManager.get().getUser();
        mBinding.setUser(user);
        UserManager.get().refresh().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                mBinding.setUser(user);
            }
        });

        mBinding.aivActionLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setMessage(R.string.confirm_logout)
                        .setNegativeButton(getString(R.string.cancel),null)
                        .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                UserManager.get().logout();
                                getActivity().onBackPressed();

                            }
                        }).create().show();

            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        StatusBar.lightStatusBar(getActivity(),false);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        StatusBar.lightStatusBar(getActivity(),hidden);



    }
}