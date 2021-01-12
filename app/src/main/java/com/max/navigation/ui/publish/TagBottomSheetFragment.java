package com.max.navigation.ui.publish;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bttomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.max.navigation.R;
import com.max.navigation.model.TagList;
import com.max.navigation.ui.login.UserManager;
import com.max.navigation.utils.PixUtils;
import com.max.network.ApiResponse;
import com.max.network.ApiService;
import com.max.network.JsonCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: maker
 * @date: 2021/1/12 16:32
 * @description:
 */
public class TagBottomSheetFragment extends BottomSheetDialogFragment {

    private RecyclerView tags;
    private TagsAdapter tagsAdapter;
    private List<TagList> mTagList = new ArrayList<>();

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dailog_publish_tag,null,false);
        tags = view.findViewById(R.id.rv_publish_tags);
        tags.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        tagsAdapter = new TagsAdapter();
        tags.setAdapter(tagsAdapter);

        ViewGroup parent = (ViewGroup) view.getParent();
        BottomSheetBehavior<ViewGroup> behavior = BottomSheetBehavior.from(parent);
        behavior.setPeekHeight(PixUtils.getScreenHeight() / 3);
        behavior.setHideable(false);

        ViewGroup.LayoutParams layoutParams = parent.getLayoutParams();
        layoutParams.height = PixUtils.getScreenHeight() / 3 * 2;
        parent.setLayoutParams(layoutParams);

        queryTagList();




        dialog.setContentView(view);
        return dialog;
    }

    private void queryTagList() {
        ApiService.get("/tag/queryTagList")
                .addParam("userId", UserManager.get().getUserId())
                .addParam("pageCount",100)
                .addParam("tagId",0)
                .execute(new JsonCallback<List<TagList>>() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onSuccess(ApiResponse<List<TagList>> response) {
                        if(response.body != null){
                            List<TagList> body = response.body;
                            mTagList.clear();
                            mTagList.addAll(body);
                            ArchTaskExecutor.getMainThreadExecutor().execute(new Runnable() {
                                @Override
                                public void run() {
                                    tagsAdapter.notifyDataSetChanged();
                                }
                            });

                        }
                    }

                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onError(ApiResponse<List<TagList>> response) {
                        ArchTaskExecutor.getMainThreadExecutor().execute(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), response.message, Toast.LENGTH_SHORT).show();
                            }
                        });


                    }
                });

    }


    class TagsAdapter extends RecyclerView.Adapter{

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView textView = new TextView(parent.getContext());
            textView.setTypeface(Typeface.DEFAULT_BOLD);
            textView.setTextSize(13);
            textView.setGravity(Gravity.CENTER_VERTICAL);
            textView.setTextColor(ContextCompat.getColor(parent.getContext(),R.color.black));
            textView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,PixUtils.dp2px(46)));
            new RecyclerView.ViewHolder(textView){

            };

            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            TextView itemView = (TextView) holder.itemView;

        }

        @Override
        public int getItemCount() {
            return mTagList.size();
        }
    }
}