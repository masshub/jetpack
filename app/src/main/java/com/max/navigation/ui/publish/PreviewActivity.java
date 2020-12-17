package com.max.navigation.ui.publish;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.view.PreviewView;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.util.Util;
import com.max.navigation.R;
import com.max.navigation.databinding.ActivityPreviewBinding;

import java.io.File;

import javax.sql.DataSource;

/**
 * @author: maker
 * @date: 2020/12/17 21:40
 * @description:
 */
public class PreviewActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityPreviewBinding mBinding;
    public static final String KEY_PREVIEW_URL = "preview_url";
    public static final String KEY_PREVIEW_VIDEO = "preview_video";
    public static final String KEY_PREVIEW_TEXT = "preview_text";
    public static final int CODE_PREVIEW = 10001;
    private SimpleExoPlayer exoPlayer;
    private Uri uri;
    //    private String mPreviewUrl;
//    private Boolean mIsVideo;
//    private String mText;

    public static void startActivityForResult(Activity activity, String previewUrl, boolean isVideo, String text){
        Intent intent = new Intent(activity, PreviewView.class);
        intent.putExtra(KEY_PREVIEW_URL,previewUrl);
        intent.putExtra(KEY_PREVIEW_VIDEO,isVideo);
        intent.putExtra(KEY_PREVIEW_TEXT,text);
        activity.startActivityForResult(intent,CODE_PREVIEW);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_capture);


        String previewUrl = getIntent().getStringExtra(KEY_PREVIEW_URL);
        Boolean isVideo = getIntent().getBooleanExtra(KEY_PREVIEW_VIDEO,false);
        String text = getIntent().getStringExtra(KEY_PREVIEW_TEXT);

        if(TextUtils.isEmpty(text)){
            mBinding.mbPreviewFinish.setVisibility(View.GONE);
        } else {
            mBinding.mbPreviewFinish.setVisibility(View.VISIBLE);
            mBinding.mbPreviewFinish.setText(text);
            mBinding.mbPreviewFinish.setOnClickListener(this);
        }

        mBinding.acivPreviewClose.setOnClickListener(this);

        if(isVideo){
            previewVideo(previewUrl);
        } else {
            previewImage(previewUrl);
        }

    }

    private void previewImage(String url) {
        mBinding.pvPreview.setVisibility(View.VISIBLE);
        Glide.with(this).load(url).into(mBinding.pvPreview);

    }

    private void previewVideo(String url) {
        mBinding.pvPreview.setVisibility(View.VISIBLE);
        exoPlayer = ExoPlayerFactory.newSimpleInstance(this, new DefaultRenderersFactory(this), new DefaultTrackSelector());
        File file = new File(url);
        Uri uri = null;
        if(file.exists()){
            DataSpec dataSpec = new DataSpec(Uri.fromFile(file));
            FileDataSource fileDataSource = new FileDataSource();
            try {
                fileDataSource.open(dataSpec);
                uri = fileDataSource.getUri();
            } catch (FileDataSource.FileDataSourceException e) {
                e.printStackTrace();
            }
        } else {
            Uri.parse(url);
        }

        ProgressiveMediaSource.Factory factory = new ProgressiveMediaSource.Factory(new DefaultDataSourceFactory(this, Util.getUserAgent(this, getPackageName())));
        ProgressiveMediaSource mediaSource = factory.createMediaSource(uri);
        exoPlayer.prepare(mediaSource);
        exoPlayer.setPlayWhenReady(true);
        mBinding.pvPreviewLoading.setPlayer(exoPlayer);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(exoPlayer != null){
            exoPlayer.setPlayWhenReady(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(exoPlayer != null){
            exoPlayer.setPlayWhenReady(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(exoPlayer != null){
            exoPlayer.setPlayWhenReady(false);
            exoPlayer.stop(true);
            exoPlayer.release();
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.aciv_preview_close){
            finish();

        } else if(v.getId() == R.id.mb_preview_finish){

            setResult(RESULT_OK,new Intent());
            finish();

        }

    }
}