package com.max.navigation.ui.find;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.ItemKeyedDataSource;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;

import com.max.navigation.R;
import com.max.navigation.abs.AbsListFragment;
import com.max.navigation.data.MutableItemKeyDataSource;
import com.max.navigation.model.TagList;
import com.scwang.smart.refresh.layout.api.RefreshLayout;

import java.util.List;

/**
 * @author: maker
 * @date: 2021/1/13 15:03
 * @description:
 */
public class FindItemFragment extends AbsListFragment<TagList,FindItemViewModel> {
    public static final String TAG_TYPE= "tag_type";
    private String tagType;

    public static FindItemFragment newInstance(String tagType) {

        Bundle args = new Bundle();
        args.putString(TAG_TYPE,tagType);
        FindItemFragment fragment = new FindItemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public PagedListAdapter getAdapter() {
        tagType = getArguments().getString(TAG_TYPE);
        mViewModel.setTagType(tagType);
        FindItemAdapter adapter = new FindItemAdapter(getContext());
        return adapter;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(TextUtils.equals(tagType,"onlyFollow")){
            emptyView.setEmptyText(getString(R.string.empty_find_tip));
            emptyView.setEmptyAction(getString(R.string.view_recommend), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewModel.getSwitchFindTab().postValue(new Object());


                }
            });


        }
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        PagedList<TagList> currentList = getAdapter().getCurrentList();
        long tagId = currentList== null ? 0 : currentList.get(currentList.size() - 1).tagId;
        mViewModel.loadData(tagId,new ItemKeyedDataSource.LoadCallback(){
            @Override
            public void onResult(@NonNull List data) {
                MutableItemKeyDataSource<Long,TagList> mutableItemKeyDataSource = new MutableItemKeyDataSource<Long, TagList>((ItemKeyedDataSource) mViewModel.getDataSource()) {
                    @NonNull
                    @Override
                    public Long getKey(@NonNull TagList item) {
                        return Long.valueOf(item.tagId);
                    }
                };

                mutableItemKeyDataSource.data.addAll(currentList);
                mutableItemKeyDataSource.data.addAll(data);
                PagedList<TagList> tagLists = mutableItemKeyDataSource.buildNewPagedList(currentList.getConfig());
                if(tagLists.size() > 0){
                    submitList(tagLists);
                }

            }
        });

    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        mViewModel.getDataSource().invalidate();

    }
}