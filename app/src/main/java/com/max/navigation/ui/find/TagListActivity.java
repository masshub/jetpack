package com.max.navigation.ui.find;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.max.common.view.EmptyView;
import com.max.navigation.R;
import com.max.navigation.abs.AbsPagedListAdapter;
import com.max.navigation.databinding.ActivityTagListBinding;
import com.max.navigation.databinding.TagListHeaderBinding;
import com.max.navigation.exo.PageListPlayerDetector;
import com.max.navigation.exo.PageListPlayerManager;
import com.max.navigation.model.Feed;
import com.max.navigation.model.TagList;
import com.max.navigation.ui.home.FeedAdapter;
import com.max.navigation.utils.PixUtils;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.RefreshState;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

/**
 * @author: maker
 * @date: 2021/1/15 8:56
 * @description:
 */
public class TagListActivity extends AppCompatActivity implements View.OnClickListener, OnRefreshListener, OnLoadMoreListener {
    public static final String KEY_TAG_LIST = "tag_list";
    public static final String KEY_FEED_TYPE = "feed_type";
    private ActivityTagListBinding mBinding;
    private RecyclerView recyclerView;
    private EmptyView emptyView;
    private SmartRefreshLayout refreshLayout;
    private PageListPlayerDetector pageListPlayerDetector;
    private boolean shouldPause = true;
    private TagList tagList;
    private AbsPagedListAdapter adapter;
    private int totalScrollY;
    private TagListViewModel tagListViewModel;


    public static void startTagListActivity(Context context, TagList tagList) {
        Intent intent = new Intent(context, TagListActivity.class);
        intent.putExtra(KEY_TAG_LIST, tagList);
        context.startActivity(intent);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_tag_list);
        recyclerView = mBinding.refreshLayout.recyclerView;
        emptyView = mBinding.refreshLayout.emptyView;
        refreshLayout = mBinding.refreshLayout.refreshLayout;
        mBinding.ivTagBack.setOnClickListener(this);


        tagList = getIntent().getParcelableExtra(KEY_TAG_LIST);
        mBinding.setTagList(tagList);
        mBinding.setOwenr(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        pageListPlayerDetector = new PageListPlayerDetector(this, recyclerView);
        adapter = (AbsPagedListAdapter) getAdapter();
        recyclerView.setAdapter(adapter);


        addHeaderView();

        tagListViewModel = ViewModelProviders.of(this).get(TagListViewModel.class);
        tagListViewModel.setFeedType(tagList.title);
        tagListViewModel.getPageData().observe(this, new Observer<PagedList<Feed>>() {
            @Override
            public void onChanged(PagedList<Feed> feeds) {
                submitList(feeds);

            }
        });


        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadMoreListener(this);

    }

    private void submitList(PagedList<Feed> feeds) {
        if(feeds.size() > 0){
            adapter.submitList(feeds);
        }

        finishAndRefresh(feeds.size() > 0);
    }

    private void finishAndRefresh(boolean hasData) {
        PagedList currentList = adapter.getCurrentList();
        hasData = currentList == null && currentList.size() > 0 && hasData;
        if(hasData){
            emptyView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.VISIBLE);
        }
        RefreshState state = refreshLayout.getState();
        if(state.isOpening && state.isHeader){
            refreshLayout.finishRefresh();
        } else if(state.isOpening && state.isFooter){
            refreshLayout.finishLoadMore();
        }

    }

    private void addHeaderView() {
        TagListHeaderBinding headerBinding = TagListHeaderBinding.inflate(LayoutInflater.from(this), recyclerView, false);
        headerBinding.setTagList(tagList);
        headerBinding.setOwner(this);
        adapter.addHeaders(headerBinding.getRoot());

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalScrollY += dy;
                if (totalScrollY >= PixUtils.dp2px(48)) {
                    mBinding.mivTagLogo.setVisibility(View.VISIBLE);
                    mBinding.tvTagListTitle.setVisibility(View.VISIBLE);
                    mBinding.mbTagListFollow.setVisibility(View.VISIBLE);
                    mBinding.ivTagBack.setImageResource(R.drawable.icon_back_black);
                } else {
                    mBinding.mivTagLogo.setVisibility(View.GONE);
                    mBinding.tvTagListTitle.setVisibility(View.GONE);
                    mBinding.mbTagListFollow.setVisibility(View.GONE);
                    mBinding.ivTagBack.setImageResource(R.drawable.icon_back_white);


                }
            }
        });
    }


    public PagedListAdapter getAdapter() {
        return new FeedAdapter(this, KEY_FEED_TYPE) {
            @Override
            public void onViewAttachedToWindow(@NonNull FeedAdapter.ViewHolder holder) {
                super.onViewAttachedToWindow(holder);
                if (holder.isVideoItem()) {
                    pageListPlayerDetector.addTarget(holder.getListPlayerView());
                }
            }

            @Override
            public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
                super.onViewDetachedFromWindow(holder);
                pageListPlayerDetector.removeTarget(holder.getListPlayerView());
            }

            @Override
            public void onStartFeedDetailActivity(Feed feed) {
                boolean isVideo = feed.itemType == Feed.TYPE_VIDEO;
                shouldPause = !isVideo;
            }

            @Override
            public void onCurrentListChanged(@Nullable PagedList<Feed> previousList, @Nullable PagedList<Feed> currentList) {
                // 每调用一次adapter。submitlist触发一次
                if (previousList != null && currentList != null) {
                    if (!currentList.containsAll(previousList)) {
                        recyclerView.scrollToPosition(0);
                    }

                }
            }
        };
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (shouldPause) {
            pageListPlayerDetector.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (pageListPlayerDetector != null) pageListPlayerDetector.onResume();
    }

    @Override
    protected void onDestroy() {
        PageListPlayerManager.release(KEY_FEED_TYPE);
        super.onDestroy();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_tag_back:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        tagListViewModel.getDataSource().invalidate();


    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {

    }
}