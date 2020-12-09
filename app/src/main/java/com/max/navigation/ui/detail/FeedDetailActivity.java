package com.max.navigation.ui.detail;

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

    private static final String KEY_FEED = "key_feed";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Feed feed = getIntent().getParcelableExtra(KEY_FEED);
        if(feed == null){
            finish();
            return;
        }

        ViewHandler viewHandler = null;
        if(feed.itemType == Feed.TYPE_IMAGE){
            viewHandler = new ImageViewHandler(this);
        } else {
            viewHandler = new VideoHandler(this);
        }

        viewHandler.bindInitData(feed);
    }
}