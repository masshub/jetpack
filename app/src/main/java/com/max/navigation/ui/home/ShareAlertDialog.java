package com.max.navigation.ui.home;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.max.common.ViewHelper;
import com.max.navigation.R;
import com.max.navigation.utils.PixUtils;
import com.max.navigation.view.MaxImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: maker
 * @date: 2020/12/7 10:17
 * @description:
 */
public class ShareAlertDialog extends AlertDialog {
    private List<ResolveInfo> shareItems = new ArrayList<>();
    private ShareAdapter shareAdapter;
    private String shareContent;
    private View.OnClickListener onClickListener;

    protected ShareAlertDialog(@NonNull Context context) {
        super(context);
    }

    protected ShareAlertDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected ShareAlertDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RoundFrameLayout layout = new RoundFrameLayout(getContext());
        layout.setBackgroundColor(Color.WHITE);
        layout.setViewOutline(PixUtils.dp2px(20), ViewHelper.RADIUS_TOP);

        RecyclerView recyclerView = new RecyclerView(getContext());
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        shareAdapter = new ShareAdapter();
        recyclerView.setAdapter(shareAdapter);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.leftMargin = layoutParams.topMargin = layoutParams.rightMargin = layoutParams.bottomMargin = PixUtils.dp2px(20);
        layoutParams.gravity = Gravity.CENTER;
        layout.addView(recyclerView, layoutParams);

        setContentView(layout);
        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        queryShareItems();
    }

    public void setShareContent(String shareContent) {
        this.shareContent = shareContent;
    }

    public void setShareItemClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    private void queryShareItems() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");

        List<ResolveInfo> resolveInfos = getContext().getPackageManager().queryIntentActivities(intent, 0);

        for (ResolveInfo resolveInfo : resolveInfos) {
            String packageName = resolveInfo.activityInfo.packageName;
            if (TextUtils.equals(packageName, "com.tencent.mm") || TextUtils.equals(packageName, "com.tencent.mobileqq")) {
                shareItems.add(resolveInfo);
            }
        }

        shareAdapter.notifyDataSetChanged();


    }

    private class ShareAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final PackageManager packageManager;

        public ShareAdapter() {
            packageManager = getContext().getPackageManager();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.item_share, parent, false);
            return new RecyclerView.ViewHolder(view) {
            };
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ResolveInfo resolveInfo = shareItems.get(position);
            MaxImageView shareIcon = holder.itemView.findViewById(R.id.miv_share_icon);
            Drawable drawable = resolveInfo.loadIcon(packageManager);
            shareIcon.setImageDrawable(drawable);

            TextView shareTitle = holder.itemView.findViewById(R.id.tv_share_title);
            shareTitle.setText(resolveInfo.loadLabel(packageManager));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    String name = resolveInfo.activityInfo.name;
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.setComponent(new ComponentName(packageName, name));
                    intent.putExtra(Intent.EXTRA_TEXT, shareContent);
                    getContext().startActivity(intent);

                    if (onClickListener != null) {
                        onClickListener.onClick(v);
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return shareItems.size();
        }
    }
}