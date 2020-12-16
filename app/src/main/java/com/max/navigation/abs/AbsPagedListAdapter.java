package com.max.navigation.abs;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author: maker
 * @date: 2020/12/9 15:35
 * @description:
 */

public abstract class AbsPagedListAdapter<T, VH extends RecyclerView.ViewHolder> extends PagedListAdapter<T, VH> {
    private SparseArray<View> headers = new SparseArray<>();
    private SparseArray<View> footers = new SparseArray<>();

    private int BASE_ITEM_HEADER_TYPE = 10000;
    private int BASE_ITEM_FOOTER_TYPE = 20000;

    /**
     * 添加头部
     * @param header
     */
    public void addHeaders(View header) {
        if (headers.indexOfValue(header) < 0) {
            headers.put(BASE_ITEM_HEADER_TYPE++, header);
            notifyDataSetChanged();
        }
    }

    /**
     * 移除头部
     * @param view
     */
    public void removeHeaderView(View view){
        int index =headers.indexOfValue(view);
        if(index < 0) return;
        headers.removeAt(index);
        notifyDataSetChanged();
    }

    /**
     * 添加尾部
     * @param footer
     */
    public void addFooter(View footer) {
        if (footers.indexOfValue(footer) < 0) {
            footers.put(BASE_ITEM_FOOTER_TYPE++, footer);
            notifyDataSetChanged();
        }
    }

    /**
     * 移除尾部
     * @param view
     */
    public void removeFooterView(View view){
        int index = footers.indexOfValue(view);
        if(index < 0) return;
        footers.removeAt(index);
        notifyDataSetChanged();
    }


    protected AbsPagedListAdapter(@NonNull DiffUtil.ItemCallback<T> diffCallback) {
        super(diffCallback);
    }

    @Override
    public int getItemCount() {
        int count = super.getItemCount();
        count = count + headers.size() + footers.size();
        return count;
    }

    public int getOriginalItemCount() {
        return getItemCount() - headers.size() - footers.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderPosition(position)) {
            return headers.keyAt(position);
        }

        if (isFooterPosition(position)) {
            position = position - getOriginalItemCount() - headers.size();
            return footers.keyAt(position);
        }

        position = position - headers.size();


        return getItemViewType2(position);
    }

    protected abstract int getItemViewType2(int position);

    private boolean isFooterPosition(int position) {
        return position >= getOriginalItemCount() + headers.size();
    }


    private boolean isHeaderPosition(int position) {
        return position < headers.size();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (headers.indexOfKey(viewType) >= 0) {
            View view = headers.get(viewType);
            return (VH) new RecyclerView.ViewHolder(view) {
            };
        }

        if (footers.indexOfKey(viewType) >= 0) {
            View view = footers.get(viewType);
            return (VH) new RecyclerView.ViewHolder(view) {
            };
        }

        return onCreateViewHolder2(parent, viewType);
    }

    protected abstract VH onCreateViewHolder2(@NonNull ViewGroup parent, int viewType);


    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        if (isHeaderPosition(position) || isFooterPosition(position)) {
            return;
        }
        position = position - headers.size();

        onBindViewHolder2(holder, position);

    }

    protected abstract void onBindViewHolder2(@NonNull VH holder, int position);


    @Override
    public void registerAdapterDataObserver(@NonNull RecyclerView.AdapterDataObserver observer) {
        super.registerAdapterDataObserver(new AdapterDataObserverProxy(observer));
    }

    private class AdapterDataObserverProxy extends RecyclerView.AdapterDataObserver {
        private RecyclerView.AdapterDataObserver mObserver;

        public AdapterDataObserverProxy(RecyclerView.AdapterDataObserver observer) {
            mObserver = observer;

        }

        public void onChanged() {
            mObserver.onChanged();
        }

        public void onItemRangeChanged(int positionStart, int itemCount) {
            mObserver.onItemRangeChanged(positionStart + headers.size(), itemCount);
        }

        public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            mObserver.onItemRangeChanged(positionStart + headers.size(), itemCount, payload);
        }

        public void onItemRangeInserted(int positionStart, int itemCount) {
            mObserver.onItemRangeInserted(positionStart + headers.size(), itemCount);
        }

        public void onItemRangeRemoved(int positionStart, int itemCount) {
            mObserver.onItemRangeRemoved(positionStart + headers.size(), itemCount);
        }

        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            mObserver.onItemRangeMoved(fromPosition + headers.size(), toPosition + headers.size(), itemCount);
        }


    }
}