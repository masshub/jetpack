package com.max.navigation.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.max.navigation.databinding.FeedImageTypeBinding;
import com.max.navigation.databinding.FeedVideoTypeBinding;
import com.max.navigation.model.Feed;

import static com.max.navigation.model.Feed.TYPE_IMAGE;

/**
 * @author: maker
 * @date: 2020/12/3 14:25
 * @description:
 */
public class FeedAdapter extends PagedListAdapter<Feed, FeedAdapter.ViewHolder> {
    private final LayoutInflater mInflater;
    private Context mContext;
    private String mCategory;

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
        return feed.itemType;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewDataBinding binding = null;
        if (viewType == TYPE_IMAGE) {
            binding = FeedImageTypeBinding.inflate(mInflater);
        } else {
            binding = FeedVideoTypeBinding.inflate(mInflater);

        }
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(getItem(position));

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ViewDataBinding binding;

        public ViewHolder(View root, ViewDataBinding binding) {
            super(root);
            this.binding = binding;
        }

        public void bindData(Feed item) {
            if (binding instanceof FeedImageTypeBinding) {
                FeedImageTypeBinding imageTypeBinding = (FeedImageTypeBinding) binding;
                imageTypeBinding.setFeed(item);
                imageTypeBinding.mivFeedImage.bindData(item.width, item.height, 16, item.cover);
            } else {
                FeedVideoTypeBinding videoTypeBinding = (FeedVideoTypeBinding) binding;
                videoTypeBinding.setFeed(item);
                videoTypeBinding.lpvVideo.bindData(mCategory, item.width, item.height, item.cover, item.url);
            }

        }
    }
}