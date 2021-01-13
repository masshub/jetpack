package com.max.navigation.ui.publish;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkContinuation;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.alibaba.fastjson.JSONObject;
import com.max.common.dialog.LoadingDialog;
import com.max.common.utils.FileUtils;
import com.max.libnavannotation.ActivityDestination;
import com.max.navigation.R;
import com.max.navigation.databinding.ActivityPublishBinding;
import com.max.navigation.model.Feed;
import com.max.navigation.model.TagList;
import com.max.navigation.ui.login.UserManager;
import com.max.network.ApiResponse;
import com.max.network.ApiService;
import com.max.network.JsonCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author: maker
 * @date: 2020/11/3015:27
 * @description:
 */
@ActivityDestination(pageUrl = "main/tabs/publish", needLogin = true)
public class PublishActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityPublishBinding mBinding;
    private int width;
    private int height;
    private String filePath;
    private boolean isVideo;
    private String mCoverPath;
    private UUID coverUUID;
    private UUID fileUUID;
    private String coverUploadUrl;
    private String fileUploadUrl;
    private TagList tag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_publish);

        mBinding.mbPublish.setOnClickListener(this);
        mBinding.ivPublishAddFile.setOnClickListener(this);
        mBinding.ivPublishDelete.setOnClickListener(this);
        mBinding.ivPublishPlay.setOnClickListener(this);
        mBinding.mbPublishAddTag.setOnClickListener(this);
        mBinding.ivPublishClose.setOnClickListener(this);

    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_publish_close:
                showExitDialog();
                break;
            case R.id.mb_publish:
                publish();
                break;
            case R.id.mb_publish_add_tag:
                TagBottomSheetFragment fragment = new TagBottomSheetFragment();
                fragment.setTagItemSelectedListener(new TagBottomSheetFragment.TagItemSelectedListener() {
                    @Override
                    public void onTagItemSelected(TagList tagList) {
                        mBinding.mbPublishAddTag.setText(tagList.title);
                        tag = tagList;

                    }
                });

                fragment.show(getSupportFragmentManager(), "PublishTag");
                break;
            case R.id.iv_publish_add_file:
                CaptureActivity.startActivityForResult(this);
                break;
            case R.id.iv_publish_delete:
                mBinding.flPublishPreview.setVisibility(View.GONE);
                mBinding.ivPublishAddFile.setVisibility(View.VISIBLE);
                mBinding.mivPublishCover.setImageDrawable(null);
                filePath = null;
                width = 0;
                height = 0;
                isVideo = false;
                break;
            default:
                break;
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void publish() {
        showLoadingDialog();
        List<OneTimeWorkRequest> workRequests = new ArrayList<>();
        if (!TextUtils.isEmpty(filePath)) {
            if (isVideo) {
                FileUtils.generateVideoCover(filePath).observe(this, new Observer<String>() {
                    @SuppressLint({"RestrictedApi", "EnqueueWork"})
                    @Override
                    public void onChanged(String coverPath) {
                        mCoverPath = coverPath;
                        OneTimeWorkRequest request = getOneTimeWorkRequest(mCoverPath);
                        coverUUID = request.getId();
                        workRequests.add(request);
                        enqueue(workRequests);
                    }
                });
            }

            OneTimeWorkRequest oneTimeWorkRequest = getOneTimeWorkRequest(filePath);
            fileUUID = oneTimeWorkRequest.getId();
            workRequests.add(oneTimeWorkRequest);
            if (!isVideo) {
                enqueue(workRequests);
            }

        } else {
            publishTag();
        }
    }

    private void enqueue(List<OneTimeWorkRequest> workRequests) {
        WorkContinuation workContinuation = WorkManager.getInstance(PublishActivity.this).beginWith(workRequests);
        workContinuation.enqueue();
        workContinuation.getWorkInfosLiveData().observe(PublishActivity.this, new Observer<List<WorkInfo>>() {
            @Override
            public void onChanged(List<WorkInfo> workInfos) {
                int complete = 0;
                for (WorkInfo workInfo : workInfos) {
                    WorkInfo.State state = workInfo.getState();
                    UUID id = workInfo.getId();
                    if (state == WorkInfo.State.FAILED) {
                        if (id.equals(coverUUID)) {
                            showToast(getString(R.string.upload_cover_failed));
                        } else if (id.equals(fileUUID)) {
                            showToast(getString(R.string.upload_file_failed));
                        }
                    } else if (state == WorkInfo.State.SUCCEEDED) {
                        Data outputData = workInfo.getOutputData();
                        String fileUrl = outputData.getString("fileUrl");
                        if (id.equals(coverUUID)) {
                            coverUploadUrl = fileUrl;
                        } else if (id.equals(fileUUID)) {
                            fileUploadUrl = fileUrl;
                        }
                    }

                    complete++;
                }

                if (complete >= workInfos.size()) {
                    publishTag();
                }
            }
        });
    }

    private void publishTag() {
        ApiService.post("/feeds/publish")
                .addParam("coverUrl", coverUploadUrl)
                .addParam("fileUrl", fileUploadUrl)
                .addParam("fileWidth", width)
                .addParam("fileHeight", height)
                .addParam("userId", UserManager.get().getUserId())
                .addParam("tagId", tag == null ? 0 : tag.tagId)
                .addParam("tagTitle", tag == null ? "" : tag.title)
                .addParam("feedText", mBinding.etPublishContent.getText().toString())
                .addParam("feedType", isVideo ? Feed.TYPE_VIDEO : Feed.TYPE_IMAGE)
                .execute(new JsonCallback<JSONObject>() {
                    @Override
                    public void onSuccess(ApiResponse<JSONObject> response) {
                        showToast(getString(R.string.publish_success));
                        PublishActivity.this.finish();
                        dismissLoadingDialog();

                    }

                    @Override
                    public void onError(ApiResponse<JSONObject> response) {
                        showToast(response.message);
                        dismissLoadingDialog();

                    }
                });

    }

    private LoadingDialog mLoadingDialog = null;

    public void showLoadingDialog(){
        if(mLoadingDialog == null){
            mLoadingDialog = new LoadingDialog(this);
            mLoadingDialog.setLoadingText(getString(R.string.publish_tip));
            mLoadingDialog.show();
        }
    }

    private void dismissLoadingDialog(){
        if(Looper.myLooper() == Looper.getMainLooper()) {
            if (mLoadingDialog != null) {
                mLoadingDialog.dismiss();
            }
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mLoadingDialog != null) {
                        mLoadingDialog.dismiss();
                    }
                }
            });
        }
    }

    private void showToast(String toast) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(PublishActivity.this, toast, Toast.LENGTH_SHORT).show();
                }
            });

        }


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint({"RestrictedApi", "NewApi"})
    private OneTimeWorkRequest getOneTimeWorkRequest(String filePath) {
        Data inputData = new Data.Builder()
                .putString("file", mCoverPath)
                .build();

        @SuppressLint("RestrictedApi") Constraints constraints = new Constraints();
        // 不计流量，wifi下进行
        constraints.setRequiredNetworkType(NetworkType.UNMETERED);
        // 充电时执行
        constraints.setRequiresCharging(true);
        // >15%电量时才会被执行
        constraints.setRequiresStorageNotLow(true);
        // 设备空闲的时候执行
        constraints.setRequiresDeviceIdle(true);
        // >15%存储时才会被执行
        constraints.setRequiresStorageNotLow(true);
        // 只有传入的url发生变化时才能被执行
        constraints.setContentUriTriggers(null);
        // url变化时执行延时
        constraints.setTriggerContentUpdateDelay(0);
        // url变化时最大延迟时间
        constraints.setTriggerMaxContentDelay(0);


        return new OneTimeWorkRequest.Builder(UploadFileManager.class)
                // 参数
                .setInputData(inputData)
                // 约束
                .setConstraints(constraints)
                .setInputMerger(null) // 设置拦截器，修改传参
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.SECONDS) // 失败重试策略
                // 执行延迟时间
                .setInitialDelay(10, TimeUnit.SECONDS)
                // 失败尝试次数
                .setInitialRunAttemptCount(2)
                // 开始执行时间 System。。。。。
                .setPeriodStartTime(0, TimeUnit.SECONDS)
                // 该任务被调度时间
                .setScheduleRequestedAt(0, TimeUnit.SECONDS)
                // 当任务finish，且没有后续观察者消费结果，该结果会被保存内存中的时间，超过该时间该结果
                // 会被存储再数据库中，若下次需要查询该结果，可以通过uuid查询该结果，查询时会触发workmanager的
                // 数据库进行查询
                .keepResultsForAtLeast(10, TimeUnit.SECONDS)
                .build();
    }

    private void showExitDialog() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.publish_exit_tip)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();

                    }
                }).create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == CaptureActivity.REQ_CODE && data != null) {
            width = data.getIntExtra(CaptureActivity.RESULT_FILE_WIDTH, 0);
            height = data.getIntExtra(CaptureActivity.RESULT_FILE_HEIGHT, 0);
            filePath = data.getStringExtra(CaptureActivity.RESULT_FILE_PATH);
            isVideo = data.getBooleanExtra(CaptureActivity.RESULT_FILE_TYPE, false);
            showFileThumbnail();
        }
    }

    private void showFileThumbnail() {
        if (TextUtils.isEmpty(filePath)) return;
        mBinding.ivPublishAddFile.setVisibility(View.GONE);
        mBinding.flPublishPreview.setVisibility(View.VISIBLE);
        mBinding.mivPublishCover.setBackgroundImageUrl(filePath, 0);
        mBinding.ivPublishPlay.setVisibility(isVideo ? View.VISIBLE : View.GONE);
        mBinding.mivPublishCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreviewActivity.startActivityForResult(PublishActivity.this, filePath, isVideo, null);

            }
        });


    }
}