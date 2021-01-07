package com.max.common.utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.RequiresApi;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author: maker
 * @date: 2021/1/7 14:40
 * @description:
 */
public class FileUtils {

    private byte[] bytes;

    @SuppressLint("RestrictedApi")
    @RequiresApi(api = Build.VERSION_CODES.P)
    public static LiveData<String> generateVideoCover(String filePath) {
        final MutableLiveData<String> liveData = new MutableLiveData<>();
        ArchTaskExecutor.getIOThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(filePath);
                Bitmap frame = retriever.getFrameAtIndex(-1);
                if (frame != null) {
                    byte[] bytes = compressBitmap(frame, 200);
                    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), System.currentTimeMillis() + ".jpeg");

                    try {
                        file.createNewFile();
                        FileOutputStream fos = new FileOutputStream(file);
                        fos.write(bytes);
                        fos.flush();
                        fos.close();
                        fos = null;
                        liveData.postValue(file.getAbsolutePath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                } else {
                    liveData.postValue(null);
                }
            }
        });

        return liveData;

    }

    private static byte[] compressBitmap(Bitmap frame, int limit) {
        byte[] bytes = null;
        if (frame != null && limit > 0) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int option = 100;
            frame.compress(Bitmap.CompressFormat.JPEG, option, byteArrayOutputStream);
            while (byteArrayOutputStream.toByteArray().length > limit * 1024) {
                byteArrayOutputStream.reset();
                option -= 5;
                frame.compress(Bitmap.CompressFormat.JPEG, option, byteArrayOutputStream);
            }

            bytes = byteArrayOutputStream.toByteArray();
            if (byteArrayOutputStream != null) {
                try {
                    byteArrayOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                byteArrayOutputStream = null;
            }

        }

        return bytes;
    }


} 