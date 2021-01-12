package com.max.navigation.ui.publish;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.max.libnavannotation.ActivityDestination;
import com.max.navigation.R;
import com.max.navigation.databinding.ActivityPublishBinding;

/**
 * @author: maker
 * @date: 2020/11/3015:27
 * @description:
 */
@ActivityDestination(pageUrl = "main/tabs/publish", needLogin = true)
public class PublishActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityPublishBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_publish);

        mBinding.mbPublish.setOnClickListener(this);
        mBinding.ivPublishAddFile.setOnClickListener(this);
        mBinding.ivPublishDelete.setOnClickListener(this);
        mBinding.ivPublishPlay.setOnClickListener(this);
        mBinding.mbPublishAddTag.setOnClickListener(this);
        mBinding.ivPublishClose.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_publish_close:
                showExitDialog();
                break;
            case R.id.mb_publish:
                break;
            case R.id.mb_publish_add_tag:
                break;
            case R.id.iv_publish_add_file:
                break;
            case R.id.iv_publish_play:
                break;
            case R.id.iv_publish_delete:
                break;
            default:
                break;
        }

    }

    private void showExitDialog() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.publish_exit_tip)
                .setNegativeButton(R.string.cancel,null)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();

                    }
                }).create().show();
    }
}