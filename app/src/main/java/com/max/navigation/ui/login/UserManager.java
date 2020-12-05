package com.max.navigation.ui.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.common.escape.ArrayBasedUnicodeEscaper;
import com.max.navigation.model.User;
import com.max.network.cache.CacheManager;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * @author: maker
 * @date: 2020/12/4 17:39
 * @description:
 */
public class UserManager {
    private static final String KEY_USER_CACHE = "user_cache";
    private static UserManager userManager = new UserManager();
    private MutableLiveData<User> userLiveData = new MutableLiveData<>();
    private User mUser;

    public static UserManager get() {
        return userManager;
    }

    @SuppressLint("RestrictedApi")
    private UserManager() {

        ArchTaskExecutor.getIOThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                User cache = (User) CacheManager.getCache(KEY_USER_CACHE);
                if (cache != null && cache.expires_time < System.currentTimeMillis()) {
                    mUser = cache;
                }

            }
        });


    }

    public void save(User user) {
        mUser = user;
        CacheManager.save(KEY_USER_CACHE, user);
        if (userLiveData.hasObservers()) {
            userLiveData.postValue(user);
        }
    }

    public LiveData<User> login(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        return userLiveData;
    }

    public boolean isLogin() {
        return mUser == null ? false : mUser.expires_time < System.currentTimeMillis();
    }


    public User getUser() {
        return isLogin() ? mUser : null;
    }

    public Long getUserId() {
        return isLogin() ? mUser.userId : 0;
    }
}