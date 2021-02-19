package com.max.navigation.ui.my;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.paging.ItemKeyedDataSource;
import androidx.paging.PagedList;

import com.max.navigation.R;
import com.max.navigation.data.MutableItemKeyDataSource;
import com.max.navigation.model.Feed;
import com.max.navigation.ui.home.FeedAdapter;
import com.max.navigation.ui.home.InteractionPresenter;
import com.max.navigation.utils.TimeUtils;

/**
 * @author: maker
 * @date: 2021/2/19 10:11
 * @description:
 */
public class ProfileListAdapter extends FeedAdapter {
    protected ProfileListAdapter(Context context, String category) {
        super(context, category);
    }

    @Override
    public int getItemViewType(int position) {
        if(TextUtils.equals(mCategory,ProfileActivity.TAB_TYPE_COMMENT)){
            return R.layout.feed_type_comment;
        }
        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        View dissView = holder.itemView.findViewById(R.id.mb_feed_diss);
        View deleteView = holder.itemView.findViewById(R.id.iv_comment_delete);
        TextView createTime = holder.itemView.findViewById(R.id.tv_feed_author_time);

        Feed item = getItem(position);
        createTime.setVisibility(View.VISIBLE);
        createTime.setText(TimeUtils.calculate(item.createTime));

        boolean isCommentTab = TextUtils.equals(mCategory, ProfileActivity.TAB_TYPE_COMMENT);
        if(isCommentTab){
            dissView.setVisibility(View.GONE);
        }

        deleteView.setOnClickListener(v -> {
            if(isCommentTab){
                InteractionPresenter.deleteFeedComment(mContext,item.itemId,item.topComment.commentId)
                        .observe((LifecycleOwner) mContext, success -> {
                            refreshList(item);

                        });

            } else {
                InteractionPresenter.deleteFeed(mContext,item.itemId)
                        .observe((LifecycleOwner) mContext, success -> {
                            refreshList(item);
                        });
            }


        });
    }

    private void refreshList(Feed item) {
        PagedList<Feed> currentList = getCurrentList();
        MutableItemKeyDataSource<Long,Feed> dataSource = new MutableItemKeyDataSource<Long, Feed>((ItemKeyedDataSource) currentList.getDataSource()) {
            @NonNull
            @Override
            public Long getKey(@NonNull Feed item) {
                return Long.valueOf(item.id);
            }
        };

        for (Feed feed : currentList) {
            if(feed != item){
                dataSource.data.add(feed);
            }
        }

        PagedList<Feed> feeds = dataSource.buildNewPagedList(currentList.getConfig());
        submitList(feeds);

    }
}