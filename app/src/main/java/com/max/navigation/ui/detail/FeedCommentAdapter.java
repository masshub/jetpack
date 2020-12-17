package com.max.navigation.ui.detail;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.paging.ItemKeyedDataSource;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.max.navigation.R;
import com.max.navigation.abs.AbsPagedListAdapter;
import com.max.navigation.data.MutableItemKeyDataSource;
import com.max.navigation.databinding.FeedTopCommentBinding;
import com.max.navigation.databinding.ItemCommentBinding;
import com.max.navigation.model.Comment;
import com.max.navigation.ui.home.InteractionPresenter;
import com.max.navigation.ui.login.UserManager;
import com.max.navigation.utils.PixUtils;

/**
 * @author: maker
 * @date: 2020/12/9 16:45
 * @description:
 */
public class FeedCommentAdapter extends AbsPagedListAdapter<Comment, FeedCommentAdapter.ViewHolder> {


    private Context mContext;

    protected FeedCommentAdapter(Context context) {
        super(new DiffUtil.ItemCallback<Comment>() {
            @Override
            public boolean areItemsTheSame(@NonNull Comment oldItem, @NonNull Comment newItem) {
                return oldItem.id == newItem.id;
            }

            @Override
            public boolean areContentsTheSame(@NonNull Comment oldItem, @NonNull Comment newItem) {
                return oldItem.equals(newItem);
            }
        });
        mContext = context;
    }


    @Override
    protected int getItemViewType2(int position) {
        return 0;
    }

    @Override
    protected ViewHolder onCreateViewHolder2(@NonNull ViewGroup parent, int viewType) {
        ItemCommentBinding binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    protected void onBindViewHolder2(@NonNull ViewHolder holder, int position) {
        Comment item = getItem(position);
        holder.bindData(item);

        holder.mBinding.ivCommentDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InteractionPresenter.deleteFeedComment(mContext, item.itemId, item.commentId)
                        .observe((LifecycleOwner) mContext, new Observer<Boolean>() {
                            @Override
                            public void onChanged(Boolean success) {
                                if (success) {
                                    MutableItemKeyDataSource<Integer, Comment> dataSource = new MutableItemKeyDataSource<Integer, Comment>((ItemKeyedDataSource) getCurrentList().getDataSource()) {
                                        @NonNull
                                        @Override
                                        public Integer getKey(@NonNull Comment item) {
                                            return item.id;
                                        }
                                    };

                                    PagedList<Comment> currentList = getCurrentList();
                                    for (Comment comment : currentList) {
                                        if (comment != getItem(position)) {
                                            dataSource.data.add(comment);
                                        }
                                    }
                                    PagedList<Comment> commentPagedList = dataSource.buildNewPagedList(getCurrentList().getConfig());
                                    submitList(commentPagedList);
                                }
                            }
                        });

            }
        });

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private com.max.navigation.databinding.ItemCommentBinding mBinding;

        public ViewHolder(@NonNull View itemView, ItemCommentBinding binding) {
            super(itemView);
            mBinding = binding;
        }

        public void bindData(Comment item) {
            mBinding.setComment(item);
            mBinding.mbCommentAuthorLabel.setVisibility(UserManager.get().getUserId() == item.author.userId ? View.VISIBLE : View.GONE);
            mBinding.ivCommentDelete.setVisibility(UserManager.get().getUserId() == item.author.userId ? View.VISIBLE : View.GONE);
            if (!TextUtils.isEmpty(item.imageUrl)) {
                mBinding.mivCommentCover.setVisibility(View.VISIBLE);
                mBinding.mivCommentCover.bindData(item.width, item.height, 0, PixUtils.dp2px(200), PixUtils.dp2px(200), item.imageUrl);
                if (TextUtils.isEmpty(item.videoUrl)) {
                    mBinding.ivCommentPlay.setVisibility(View.GONE);
                } else {
                    mBinding.ivCommentPlay.setVisibility(View.VISIBLE);
                }

            } else {
                mBinding.mivCommentCover.setVisibility(View.GONE);
                mBinding.ivCommentPlay.setVisibility(View.GONE);
            }
        }
    }
}