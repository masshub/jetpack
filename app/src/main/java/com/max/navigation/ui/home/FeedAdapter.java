package com.max.navigation.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.max.navigation.BR;
import com.max.navigation.R;
import com.max.navigation.data.LiveDataBus;
import com.max.navigation.databinding.FeedImageTypeBinding;
import com.max.navigation.databinding.FeedVideoTypeBinding;
import com.max.navigation.exo.PageListPlayer;
import com.max.navigation.model.Feed;
import com.max.navigation.ui.detail.FeedDetailActivity;
import com.max.navigation.view.ListPlayerView;

import static com.max.navigation.model.Feed.TYPE_IMAGE;

/**
 * @author: maker
 * @date: 2020/12/3 14:25
 * @description:
 */
public class FeedAdapter extends PagedListAdapter<Feed, FeedAdapter.ViewHolder> {
    private final LayoutInflater mInflater;
    public Context mContext;
    public String mCategory;
    private FeedObserver mFeedObserver;

    protected FeedAdapter(Context context, String category) {
        super(new DiffUtil.ItemCallback<Feed>() {
            @Override
            public boolean areItemsTheSame(@NonNull Feed oldItem, @NonNull Feed newItem) {
                return oldItem.id == newItem.id;
            }

            @Override
            public boolean areContentsTheSame(@NonNull Feed oldItem, @NonNull Feed newItem) {
                return false;
            }
        });


        mInflater = LayoutInflater.from(context);
        mContext = context;
        mCategory = category;
    }


    @Override
    public int getItemViewType(int position) {
        Feed feed = getItem(position);
        if(feed.itemType == TYPE_IMAGE){
            return R.layout.feed_image_type;
        } else if(feed.itemType == Feed.TYPE_VIDEO){
            return R.layout.feed_video_type;
        }
        return 0;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewDataBinding binding = DataBindingUtil.inflate(mInflater,viewType,parent,false);
//        if (viewType == TYPE_IMAGE) {
//            binding = FeedImageTypeBinding.inflate(mInflater);
//        } else {
//            binding = FeedVideoTypeBinding.inflate(mInflater);
//
//        }
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Feed feed = getItem(position);
        holder.bindData(feed);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeedDetailActivity.startFeedDetailActivity(mContext, getItem(position), mCategory);
                onStartFeedDetailActivity(feed);
                if (mFeedObserver == null) {
                    mFeedObserver = new FeedObserver();
                    LiveDataBus.get().with(InteractionPresenter.DATA_FROM_INTERACTION)
                            .observe((LifecycleOwner) mContext, mFeedObserver);
                }

                mFeedObserver.setFeed(feed);
            }
        });


    }

    public void onStartFeedDetailActivity(Feed feed) {

    }

    private class FeedObserver implements Observer<Feed> {

        private Feed mFeed;

        @Override
        public void onChanged(Feed newFeed) {
            if (mFeed.id != newFeed.id) return;
            mFeed.author = newFeed.author;
            mFeed.ugc = newFeed.ugc;
            mFeed.notifyChange();

        }

        public void setFeed(Feed feed) {
            mFeed = feed;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ViewDataBinding binding;
        private ListPlayerView listPlayerView;
        private ImageView feedImage;

        public ViewHolder(View root, ViewDataBinding binding) {
            super(root);
            this.binding = binding;
        }

        public void bindData(Feed item) {
            binding.setVariable(BR.feed,item);
            if (binding instanceof FeedImageTypeBinding) {
                FeedImageTypeBinding imageTypeBinding = (FeedImageTypeBinding) binding;
                imageTypeBinding.setFeed(item);
                imageTypeBinding.mivFeedImage.bindData(item.width, item.height, 16, item.cover);
                imageTypeBinding.setLifecycleOwner((LifecycleOwner) mContext);
                feedImage = imageTypeBinding.mivFeedImage;
            } else {
                FeedVideoTypeBinding videoTypeBinding = (FeedVideoTypeBinding) binding;
                videoTypeBinding.setFeed(item);
                videoTypeBinding.lpvVideo.bindData(mCategory, item.width, item.height, item.cover, item.url);
                videoTypeBinding.setLifecycleOwner((LifecycleOwner) mContext);
                listPlayerView = videoTypeBinding.lpvVideo;
            }
        }

        public boolean isVideoItem() {
            return binding instanceof FeedVideoTypeBinding;
        }

        public ListPlayerView getListPlayerView() {
            return listPlayerView;
        }
    }
}