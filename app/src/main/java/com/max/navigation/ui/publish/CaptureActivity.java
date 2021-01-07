package com.max.navigation.ui.publish;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.core.VideoCapture;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.google.common.util.concurrent.ListenableFuture;
import com.max.navigation.R;
import com.max.navigation.databinding.ActivityCaptureBinding;
import com.max.navigation.view.RecorderView;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


/**
 * @author: maker
 * @date: 2020/12/17 17:32
 * @description:
 */

public class CaptureActivity extends AppCompatActivity {

    private ActivityCaptureBinding mBinding;
    private static final String[] PERMISSIONS = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int PERMISSION_CODE = 10000;
    private ArrayList<String> deniedPermissions = new ArrayList<>();
    private CameraSelector defaultCameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
    private int defaultRotation = Surface.ROTATION_0;
    private Size defaultResolution = new Size(1280, 720);
    private int defaultRatio = AspectRatio.RATIO_16_9;
    private Preview preview;
    private ImageCapture imageCapture;
    private VideoCapture videoCapture;
    private boolean takePicture;
    private final String TAG = "CaptureActivity";

    public static final String RESULT_FILE_PATH = "result_file_path";
    public static final String RESULT_FILE_WIDTH = "result_file_width";
    public static final String RESULT_FILE_HEIGHT = "result_file_height";
    public static final String RESULT_FILE_TYPE = "result_file_type";
    private String path;
    public static final int REQ_CODE = 10001;

    public static void startActivityForResult(Activity activity){
        Intent intent = new Intent(activity,CaptureActivity.class);
        activity.startActivityForResult(intent,REQ_CODE);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_capture);

        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_CODE);

        mBinding.rvCapture.setOnRecordListener(new RecorderView.onRecordListener() {
            @Override
            public void onClick() {
                takePicture = true;
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), System.currentTimeMillis() + "jpg");
                ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();
                imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(CaptureActivity.this), new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        onFileSaved(outputFileResults.getSavedUri());

                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Toast.makeText(CaptureActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onLongClick() {
                takePicture = false;
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), System.currentTimeMillis() + "mp4");
                VideoCapture.OutputFileOptions outputFileOptions = new VideoCapture.OutputFileOptions.Builder(file).build();

                videoCapture.startRecording(outputFileOptions, ContextCompat.getMainExecutor(CaptureActivity.this), new VideoCapture.OnVideoSavedCallback() {
                    @Override
                    public void onVideoSaved(@NonNull VideoCapture.OutputFileResults outputFileResults) {
                        onFileSaved(outputFileResults.getSavedUri());

                    }

                    @Override
                    public void onError(int videoCaptureError, @NonNull String message, @Nullable Throwable cause) {
                        Toast.makeText(CaptureActivity.this, message, Toast.LENGTH_SHORT).show();

                    }
                });

            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onFinish() {
                videoCapture.stopRecording();

            }
        });

    }

    private void onFileSaved(Uri uri) {
        path = null;
        File file = null;
        String mineType = takePicture ? "image/jpe" : "video/mp4";
        if ("file".equals(uri.getScheme())) {
            path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=").append("'" + path + "'").append(")");
                Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA}, buff.toString(), null, null);
                int index = 0;
                int dataIdx = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                    index = cur.getInt(index);
                    dataIdx = cur.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    path = cur.getString(dataIdx);
                }
                cur.close();
                if (index == 0) {
                } else {
                    Uri u = Uri.parse("content://media/external/images/media/" + index);
                    System.out.println("temp uri is :" + u);
                }
            }
            if (path != null) {
                file = new File(path);
            }
        } else if ("content".equals(uri.getScheme())) {
            // 4.2.2以后
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                path = cursor.getString(columnIndex);
            }
            cursor.close();

            file = new File(path);
        } else {
            Log.i(TAG, "Uri Scheme:" + uri.getScheme());
        }

        MediaScannerConnection.scanFile(this, new String[]{path}, new String[]{mineType}, new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String path, Uri uri) {

            }
        });

        PreviewActivity.startActivityForResult(this, path, !takePicture, "完成");


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            deniedPermissions.clear();
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int result = grantResults[i];
                if (result != PackageManager.PERMISSION_GRANTED) {
                    deniedPermissions.add(permission);
                }
            }

            if (deniedPermissions.isEmpty()) {
                bindCameraX();

            } else {
                new AlertDialog.Builder(this)
                        .setMessage(R.string.capture_permission_warning)
                        .setNegativeButton(R.string.capture_permission_deny, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        })
                        .setPositiveButton(R.string.capture_permission_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String[] denied = new String[deniedPermissions.size()];
                                ActivityCompat.requestPermissions(CaptureActivity.this, deniedPermissions.toArray(denied), PERMISSION_CODE);

                            }
                        })
                        .create()
                        .show();
            }


        }
    }

    @SuppressLint("RestrictedApi")
    private void bindCameraX() {
        preview = new Preview.Builder()
                .setCameraSelector(defaultCameraSelector)
                .setTargetRotation(defaultRotation)
                .setDefaultResolution(defaultResolution)
                .setTargetAspectRatio(defaultRatio)
                .build();

        imageCapture = new ImageCapture.Builder()
                .setCameraSelector(defaultCameraSelector)
                .setTargetRotation(defaultRotation)
                .setTargetResolution(defaultResolution)
                .setTargetAspectRatio(defaultRatio)
                .build();

        videoCapture = new VideoCapture.Builder()
                .setCameraSelector(defaultCameraSelector)
                .setTargetRotation(defaultRotation)
                .setTargetResolution(defaultResolution)
                .setTargetAspectRatio(defaultRatio)
                .setBitRate(3 * 1024 * 1024)
                .setIFrameInterval(26)
                .build();


        ListenableFuture<ProcessCameraProvider> cameraProviderListenableFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderListenableFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderListenableFuture.get();
                    cameraProvider.unbindAll();
                    cameraProvider.bindToLifecycle(CaptureActivity.this, defaultCameraSelector, preview, imageCapture, videoCapture);
                    preview.setSurfaceProvider(mBinding.ttvCapture.getSurfaceProvider());
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        }, ContextCompat.getMainExecutor(this));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PreviewActivity.CODE_PREVIEW && resultCode == RESULT_OK) {
            Intent intent = new Intent();
            intent.putExtra(RESULT_FILE_PATH, path);
            intent.putExtra(RESULT_FILE_WIDTH, defaultResolution.getHeight());
            intent.putExtra(RESULT_FILE_HEIGHT, defaultResolution.getWidth());
            intent.putExtra(RESULT_FILE_TYPE, !takePicture);
            setResult(RESULT_OK, intent);
            finish();

        }
    }
}