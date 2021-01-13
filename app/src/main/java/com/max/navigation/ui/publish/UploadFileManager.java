package com.max.navigation.ui.publish;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.max.common.utils.FileUploadManager;

/**
 * @author: maker
 * @date: 2021/1/13 9:56
 * @description:
 */
public class UploadFileManager extends Worker {
    public UploadFileManager(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Data inputData = getInputData();
        String filePath = inputData.getString("file");
        String fileUrl = FileUploadManager.upload(filePath);
        if (TextUtils.isEmpty(fileUrl)) {
            return Result.failure();
        } else {
            Data outputData = new Data.Builder().putString("fileUrl", fileUrl)
                    .build();
            return Result.success(outputData);
        }
    }
}