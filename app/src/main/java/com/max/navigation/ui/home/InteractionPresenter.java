package com.max.navigation.ui.home;

import android.content.Context;
import android.view.View;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.alibaba.fastjson.JSONObject;
import com.max.common.AppGlobals;
import com.max.navigation.model.Comment;
import com.max.navigation.model.Feed;
import com.max.navigation.model.User;
import com.max.navigation.ui.login.UserManager;
import com.max.network.ApiResponse;
import com.max.network.ApiService;
import com.max.network.JsonCallback;


import java.util.Date;

/**
 * @author: maker
 * @date: 2020/12/5 17:25
 * @description:
 */
public class InteractionPresenter {
    public static final String URL_TOGGLE_FEED_LIKE = "/ugc/toggleFeedLike";
    public static final String URL_TOGGLE_FEED_DISS = "/ugc/dissFeed";
    private static final String URL_SHARE = "/ugc/increaseShareCount";
    private static final String URL_TOGGLE_COMMENT_LIKE = "/ugc/toggleCommentLike";

    public static void toggleFeedLike(LifecycleOwner lifecycleOwner, Feed feed) {
        if (UserManager.get().isLogin()) {
            LiveData<User> userLiveData = UserManager.get().login(AppGlobals.getApplication());
            userLiveData.observe(lifecycleOwner, new Observer<User>() {
                @Override
                public void onChanged(User user) {
                    if (user != null) {
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
                .addParam("itemId", feed.itemId)
                .execute(new JsonCallback<JSONObject>() {
                    @Override
                    public void onSuccess(ApiResponse<JSONObject> response) {
                        if (response != null && response.body != null) {

                            boolean hasLiked = response.body.getBoolean("hasLiked");
                            feed.getUgc().setHasLiked(hasLiked);


                        }
                    }

                    @Override
                    public void onError(ApiResponse<JSONObject> response) {
                        super.onError(response);
                    }
                });

    }


    public static void toggleFeedDiss(LifecycleOwner owner, Feed feed) {
        if (UserManager.get().isLogin()) {
            LiveData<User> userLiveData = UserManager.get().login(AppGlobals.getApplication());
            userLiveData.observe(owner, new Observer<User>() {
                @Override
                public void onChanged(User user) {
                    if (user != null) {
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
                .addParam("itemId", feed.itemId)
                .execute(new JsonCallback<JSONObject>() {
                    @Override
                    public void onSuccess(ApiResponse<JSONObject> response) {
                        if (response != null && response.body != null) {
                            boolean hasLiked = response.body.getBoolean("hasLiked");
                            feed.getUgc().setHasdiss(hasLiked);

                        }
                    }

                    @Override
                    public void onError(ApiResponse<JSONObject> response) {
                        super.onError(response);
                    }
                });

    }


    public static void openShare(Context context, Feed feed) {
        String url = "https://h5.pipix.com/item/%s?app_id=1319&app=super&timestamp=%s&user_id=%s";
//        String content = String.format(url, feed.itemId, new Date().getTime(), UserManager.get().getUserId());
        ShareAlertDialog shareAlertDialog = new ShareAlertDialog(context);
        shareAlertDialog.setShareContent(url);
        shareAlertDialog.setShareItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiService.get(URL_SHARE)
                        .addParam("itemId", feed.itemId)
                        .execute(new JsonCallback<JSONObject>() {
                            @Override
                            public void onSuccess(ApiResponse<JSONObject> response) {
                                if (response != null && response.body != null) {
                                    int count = response.body.getIntValue("count");
                                    feed.getUgc().setShareCount(count);

                                }
                            }
                        });

            }
        });

        shareAlertDialog.show();
    }


    public static void toggleCommentLike(LifecycleOwner owner, Comment comment) {
        if (UserManager.get().isLogin()) {
            LiveData<User> userLiveData = UserManager.get().login(AppGlobals.getApplication());
            userLiveData.observe(owner, new Observer<User>() {
                @Override
                public void onChanged(User user) {
                    if (user != null) {
                        toggleCommentLikeInternal(comment);
                    }
                    userLiveData.removeObserver(this);
                }
            });
            return;
        }
        toggleCommentLikeInternal(comment);

    }

    private static void toggleCommentLikeInternal(Comment comment) {
        ApiService.get(URL_TOGGLE_COMMENT_LIKE)
                .addParam("commentId", comment.itemId)
                .addParam("userId", UserManager.get().getUserId())
                .execute(new JsonCallback<JSONObject>() {
                    @Override
                    public void onSuccess(ApiResponse<JSONObject> response) {
                        if (response != null && response.body != null) {
                            boolean hasLiked = response.body.getBooleanValue("hasLiked");
                            comment.getUgc().setHasLiked(hasLiked);
                        }

                    }
                });
    }
} 