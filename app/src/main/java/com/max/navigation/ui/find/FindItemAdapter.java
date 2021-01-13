package com.max.navigation.ui.find;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.max.navigation.abs.AbsPagedListAdapter;
import com.max.navigation.databinding.ItemFindBinding;
import com.max.navigation.model.TagList;
import com.max.navigation.ui.home.InteractionPresenter;

import java.util.zip.Inflater;

/**
 * @author: maker
 * @date: 2021/1/13 16:06
 * @description:
 */
public class FindItemAdapter extends AbsPagedListAdapter<TagList, FindItemAdapter.ViewHolder> {
    private Context mContext;
    private final LayoutInflater mInflater;


    protected FindItemAdapter(@NonNull Context context) {
        super(new DiffUtil.ItemCallback<TagList>() {
            @Override
            public boolean areItemsTheSame(@NonNull TagList oldItem, @NonNull TagList newItem) {
                return oldItem.tagId == newItem.tagId;
            }

            @Override
            public boolean areContentsTheSame(@NonNull TagList oldItem, @NonNull TagList newItem) {
                return oldItem.equals(newItem);
            }
        });

        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);


    }

    @Override
    protected int getItemViewType2(int position) {
        return 0;
    }

    @Override
    protected ViewHolder onCreateViewHolder2(@NonNull ViewGroup parent, int viewType) {
        ItemFindBinding itemFindBinding = ItemFindBinding.inflate(mInflater, parent, false);
        return new ViewHolder(itemFindBinding.getRoot(), itemFindBinding);
    }

    @Override
    protected void onBindViewHolder2(@NonNull ViewHolder holder, int position) {
        holder.bindData(getItem(position));
        holder.mItemFindBinding.mbTagFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InteractionPresenter.toggleTagLike((LifecycleOwner) mContext,getItem(position));

            }
        });

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ItemFindBinding mItemFindBinding;

        public ViewHolder(@NonNull View itemView, ItemFindBinding itemFindBinding) {
            super(itemView);
            mItemFindBinding = itemFindBinding;
        }

        public void bindData(TagList item) {
            mItemFindBinding.setTagList(item);

        }
    }
}