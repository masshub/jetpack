package com.max.navigation.ui.detail;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;

import com.max.common.AppGlobals;
import com.max.common.dialog.LoadingDialog;
import com.max.common.utils.FileUploadManager;
import com.max.common.utils.FileUtils;
import com.max.navigation.R;
import com.max.navigation.databinding.DialogCommentBinding;
import com.max.navigation.model.Comment;
import com.max.navigation.ui.login.UserManager;
import com.max.navigation.ui.publish.CaptureActivity;
import com.max.network.ApiResponse;
import com.max.network.ApiService;
import com.max.network.JsonCallback;

import java.util.concurrent.atomic.AtomicInteger;

import static android.widget.Toast.LENGTH_SHORT;

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
    private String filePath;
    private int width;
    private int height;
    private boolean isVideo;
    private LoadingDialog loadingDialog;
    private String coverUrl;
    private String fileUrl;
    private String comment;


    public static CommentDialog newInstance(long itemId) {

        Bundle args = new Bundle();
        args.putLong(KEY_ITEM_ID, itemId);
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


    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_comment_dialog_delete:
                publishComment();
                break;
            case R.id.iv_comment_video_record:
                CaptureActivity.startActivityForResult(getActivity());
                break;
            case R.id.mb_comment_dialog_send:
                filePath = null;
                width = 0;
                height = 0;
                isVideo = false;
                binding.mivCommentDialogCover.setImageDrawable(null);
                binding.flCommentDialogExt.setVisibility(View.GONE);
                binding.ivCommentVideoRecord.setEnabled(true);
                binding.ivCommentVideoRecord.setAlpha(100);
                break;
            default:
                break;
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void publishComment() {
        comment = binding.etCommentDialog.getText().toString().trim();
        if (TextUtils.isEmpty(comment)) {
            return;
        }

        if(isVideo && !TextUtils.isEmpty(filePath)){
            FileUtils.generateVideoCover(filePath).observe(this, new Observer<String>() {
                @Override
                public void onChanged(String coverPath) {
                    uploadFile(coverPath,filePath);
                }
            });
        } else if(!TextUtils.isEmpty(filePath)){
            uploadFile(null,filePath);
        } else {
            publish();
        }


    }

    @SuppressLint("RestrictedApi")
    private void uploadFile(String coverPath, String filePath) {
        showLoadingDialog();
        AtomicInteger count = new AtomicInteger(1);
        if (!TextUtils.isEmpty(coverPath)) {
            count.set(2);
            ArchTaskExecutor.getIOThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    int remain = count.decrementAndGet();
                    coverUrl = FileUploadManager.upload(coverPath);
                    if (remain <= 0) {
                        if (!TextUtils.isEmpty(fileUrl) && !TextUtils.isEmpty(coverUrl)) {
                            publish();
                        } else {
                            dismissLoadingDialog();
                            Toast.makeText(getContext(), getString(R.string.file_upload_failed), LENGTH_SHORT).show();

                        }
                    }
                }

            });
        }
        ArchTaskExecutor.getIOThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                int remain = count.decrementAndGet();
                fileUrl = FileUploadManager.upload(filePath);
                if (remain <= 0) {
                    if (!TextUtils.isEmpty(fileUrl) || !TextUtils.isEmpty(coverPath) && !TextUtils.isEmpty(coverUrl)) {
                        publish();
                    } else {
                        dismissLoadingDialog();
                        Toast.makeText(getContext(), getString(R.string.file_upload_failed), LENGTH_SHORT).show();
                    }
                }
            }
        });



    }


    private void publish() {

        ApiService.post("/comment/addComment")
                .addParam("userId", UserManager.get().getUserId())
                .addParam("itemId", itemId)
                .addParam("commentText", comment)
                .addParam("image_url", isVideo ? coverUrl : fileUrl)
                .addParam("video_url", fileUrl)
                .addParam("width", width)
                .addParam("height", height)
                .execute(new JsonCallback<Comment>() {
                    @Override
                    public void onSuccess(ApiResponse<Comment> response) {
                        onCommonSuccess(response.body);
                        dismissLoadingDialog();
                    }


                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onError(ApiResponse<Comment> response) {
                        dismissLoadingDialog();
                        ArchTaskExecutor.getMainThreadExecutor().execute(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AppGlobals.getApplication(), "评论失败：" + response.message, LENGTH_SHORT).show();
                            }
                        });


                    }
                });


    }

    private void showLoadingDialog() {
        if(loadingDialog == null) {
            loadingDialog = new LoadingDialog(getContext());
        }

        loadingDialog.setLoadingText(getString(R.string.upload));
        loadingDialog.show();
    }

    private void dismissLoadingDialog(){
        if(loadingDialog != null){
            loadingDialog.dismiss();
        }
    }

    @SuppressLint("RestrictedApi")
    private void onCommonSuccess(Comment comment) {
        ArchTaskExecutor.getMainThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(AppGlobals.getApplication(), "评论添加成功", LENGTH_SHORT).show();
            }
        });

        if (mCommentListener != null) {
            mCommentListener.onAddComment(comment);
        }

    }


    public interface addCommentListener {
        void onAddComment(Comment comment);
    }

    public void setAddCommentListener(addCommentListener addCommentListener) {
        mCommentListener = addCommentListener;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CaptureActivity.REQ_CODE && resultCode == Activity.RESULT_OK) {
            filePath = data.getStringExtra(CaptureActivity.RESULT_FILE_PATH);
            width = data.getIntExtra(CaptureActivity.RESULT_FILE_WIDTH, 0);
            height = data.getIntExtra(CaptureActivity.RESULT_FILE_HEIGHT, 0);
            isVideo = data.getBooleanExtra(CaptureActivity.RESULT_FILE_TYPE, false);

            if (!TextUtils.isEmpty(filePath)) {
                binding.flCommentDialogExt.setVisibility(View.VISIBLE);
                binding.mivCommentDialogCover.setBackgroundImageUrl(filePath, 6);
                if (isVideo) {
                    binding.ivCommentDialogPlay.setVisibility(View.VISIBLE);
                }
            }
            binding.ivCommentVideoRecord.setEnabled(false);
            binding.ivCommentVideoRecord.setAlpha(50);

        }
    }
}