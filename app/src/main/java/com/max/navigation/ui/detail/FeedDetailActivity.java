package com.max.navigation.ui.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.max.navigation.model.Feed;

/**
 * @author: maker
 * @date: 2020/12/8 17:34
 * @description:
 */
public class FeedDetailActivity extends AppCompatActivity {

    public static final String KEY_FEED = "key_feed";
    public static final String KEY_CATEGORY = "key_category";
    private ViewHandler viewHandler;

    public static void startFeedDetailActivity(Context mContext, Feed item, String mCategory) {
        Intent intent = new Intent(mContext, FeedDetailActivity.class);
        intent.putExtra(KEY_FEED, item);
        intent.putExtra(KEY_CATEGORY, mCategory);
        mContext.startActivity(intent);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Feed feed = (Feed) getIntent().getSerializableExtra(KEY_FEED);
        if (feed == null) {
            finish();
            return;
        }


        if (feed.itemType == Feed.TYPE_IMAGE) {
            viewHandler = new ImageViewHandler(this);
        } else {
            viewHandler = new VideoViewHandler(this);
        }

        viewHandler.bindInitData(feed);
    }
}