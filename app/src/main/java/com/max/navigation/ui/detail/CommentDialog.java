package com.max.navigation.ui.detail;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.fragment.app.DialogFragment;

import com.max.common.AppGlobals;
import com.max.navigation.R;
import com.max.navigation.databinding.DialogCommentBinding;
import com.max.navigation.model.Comment;
import com.max.navigation.ui.login.UserManager;
import com.max.network.ApiResponse;
import com.max.network.ApiService;
import com.max.network.JsonCallback;

/**
 * @author: maker
 * @date: 2020/12/17 10:42
 * @description:
 */
public class CommentDialog extends DialogFragment implements View.OnClickListener {
    private DialogCommentBinding binding;
    private long itemId;
    private addCommentListener mCommentListener;
    private static final String KEY_ITEM_ID = "key_item_id";


    public static CommentDialog newInstance(long itemId) {

        Bundle args = new Bundle();
        args.putLong(KEY_ITEM_ID,itemId);
        CommentDialog fragment = new CommentDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogCommentBinding.inflate(inflater, container, false);
        binding.ivCommentDialogDelete.setOnClickListener(this);
        binding.ivCommentVideoRecord.setOnClickListener(this);
        binding.mbCommentDialogSend.setOnClickListener(this);

        Window window = getDialog().getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        this.itemId = getArguments().getLong(KEY_ITEM_ID);
        return binding.getRoot();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_comment_dialog_delete:
                publishComment();
                break;
            case R.id.iv_comment_video_record:
                break;
            case R.id.mb_comment_dialog_send:
                break;
            default:
                break;
        }

    }

    private void publishComment() {
        String comment = binding.etCommentDialog.getText().toString().trim();
        if (TextUtils.isEmpty(comment)) {
            return;
        }

        ApiService.post("/comment/addComment")
                .addParam("userId", UserManager.get().getUserId())
                .addParam("itemId", itemId)
                .addParam("commentText", comment)
                .addParam("image_url", null)
                .addParam("video_url", null)
                .addParam("width", 0)
                .addParam("height", 0)
                .execute(new JsonCallback<Comment>() {
                    @Override
                    public void onSuccess(ApiResponse<Comment> response) {
                        onCommonSuccess(response.body);
                    }


                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onError(ApiResponse<Comment> response) {
                        ArchTaskExecutor.getMainThreadExecutor().execute(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AppGlobals.getApplication(), "评论失败：" + response.message, Toast.LENGTH_SHORT).show();
                            }
                        });


                    }
                });


    }

    @SuppressLint("RestrictedApi")
    private void onCommonSuccess(Comment comment) {
        ArchTaskExecutor.getMainThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(AppGlobals.getApplication(), "评论添加成功", Toast.LENGTH_SHORT).show();
            }
        });

        if(mCommentListener != null){
            mCommentListener.onAddComment(comment);
        }

    }


    public interface addCommentListener{
        void onAddComment(Comment comment);
    }

    public void setAddCommentListener(addCommentListener addCommentListener){
        mCommentListener = addCommentListener;
    }
}