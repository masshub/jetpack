package com.max.navigation.ui.detail;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.max.navigation.R;
import com.max.navigation.abs.AbsPagedListAdapter;
import com.max.navigation.databinding.FeedTopCommentBinding;
import com.max.navigation.databinding.ItemCommentBinding;
import com.max.navigation.model.Comment;
import com.max.navigation.ui.login.UserManager;
import com.max.navigation.utils.PixUtils;

/**
 * @author: maker
 * @date: 2020/12/9 16:45
 * @description:
 */
public class FeedCommentAdapter extends AbsPagedListAdapter<Comment, FeedCommentAdapter.ViewHolder> {


    protected FeedCommentAdapter() {
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
    }


    @Override
    protected int getItemViewType2(int position) {
        return 0;
    }

    @Override
    protected ViewHolder onCreateViewHolder2(@NonNull ViewGroup parent, int viewType) {
        ItemCommentBinding binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding.getRoot(),binding);
    }

    @Override
    protected void onBindViewHolder2(@NonNull ViewHolder holder, int position) {
        Comment item = getItem(position);
        holder.bindData(item);

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
            if(!TextUtils.isEmpty(item.imageUrl)){
                mBinding.mivCommentCover.setVisibility(View.VISIBLE);
                mBinding.mivCommentCover.bindData(item.width,item.height,0, PixUtils.dp2px(200),PixUtils.dp2px(200),item.imageUrl);
                if(TextUtils.isEmpty(item.videoUrl)){
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