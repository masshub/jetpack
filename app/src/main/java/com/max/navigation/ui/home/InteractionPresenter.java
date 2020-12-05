package com.max.navigation.ui.home;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.max.common.AppGlobals;
import com.max.navigation.model.Feed;
import com.max.navigation.model.User;
import com.max.navigation.ui.login.UserManager;
import com.max.navigation.utils.AppConfig;
import com.max.network.ApiResponse;
import com.max.network.ApiService;
import com.max.network.JsonCallback;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author: maker
 * @date: 2020/12/5 17:25
 * @description:
 */
public class InteractionPresenter {
    public static final String URL_TOGGLE_FEED_LIKE = "/ugc/toggleFeedLike";
    public static final String URL_TOGGLE_FEED_DISS = "/ugc/dissFeed";

    public static void toggleFeedLike(LifecycleOwner lifecycleOwner,Feed feed){
        if(UserManager.get().isLogin()) {
            LiveData<User> userLiveData = UserManager.get().login(AppGlobals.getApplication());
            userLiveData.observe(lifecycleOwner, new Observer<User>() {
                @Override
                public void onChanged(User user) {
                    if(user != null){
                        toggleFeedLikeInternal(feed);
                    }
                    userLiveData.removeObserver(this);
                }
            });
            return;
        }
        toggleFeedLikeInternal(feed);


    }

    private static void toggleFeedLikeInternal(Feed feed) {
        ApiService.get(URL_TOGGLE_FEED_LIKE)
                .addParam("userId", UserManager.get().getUserId())
                .addParam("itemId",feed.itemId)
                .execute(new JsonCallback<JSONObject>() {
                    @Override
                    public void onSuccess(ApiResponse<JSONObject> response) {
                        if (response != null && response.body != null){
                            try {
                                boolean hasLiked = response.body.getBoolean("hasLiked");
                                feed.getUgc().setHasLiked(hasLiked);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(ApiResponse<JSONObject> response) {
                        super.onError(response);
                    }
                });

    }


    public static void toggleFeedDiss(LifecycleOwner owner,Feed feed){
        if(UserManager.get().isLogin()) {
            LiveData<User> userLiveData = UserManager.get().login(AppGlobals.getApplication());
            userLiveData.observe(owner, new Observer<User>() {
                @Override
                public void onChanged(User user) {
                    if(user != null){
                        toggleFeedDissInternal(feed);
                    }
                    userLiveData.removeObserver(this);
                }
            });
            return;
        }
        toggleFeedDissInternal(feed);

    }

    private static void toggleFeedDissInternal(Feed feed) {
        ApiService.get(URL_TOGGLE_FEED_DISS)
                .addParam("userId", UserManager.get().getUserId())
                .addParam("itemId",feed.itemId)
                .execute(new JsonCallback<JSONObject>() {
                    @Override
                    public void onSuccess(ApiResponse<JSONObject> response) {
                        if (response != null && response.body != null){
                            try {
                                boolean hasLiked = response.body.getBoolean("hasLiked");
                                feed.getUgc().setHasdiss(hasLiked);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(ApiResponse<JSONObject> response) {
                        super.onError(response);
                    }
                });

    }
} 