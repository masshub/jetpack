package com.max.navigation.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.max.navigation.R;
import com.max.navigation.utils.PixUtils;

/**
 * @author: maker
 * @date: 2020/12/17 16:48
 * @description:
 */
public class RecorderView extends View implements View.OnClickListener, View.OnLongClickListener {

    private final int record_radius;
    private final int progressWidth;
    private final int fillColor;
    private final int progressColor;
    private final int maxDuration;
    private int progressMaxValue;
    private static final int PROGRESS_INTERVAL = 100;
    private boolean isRecording;
    private Paint fillPaint;
    private Paint progressPaint;
    private int progressValue;
    private long startRecordTime;
    private onRecordListener mRecordListener;

    public RecorderView(Context context) {
        this(context, null);
    }

    public RecorderView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecorderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }


    public RecorderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RecordView, defStyleAttr, defStyleRes);
        record_radius = typedArray.getDimensionPixelOffset(R.styleable.RecordView_radius, 0);
        progressWidth = typedArray.getDimensionPixelOffset(R.styleable.RecordView_progress_width, PixUtils.dp2px(4));
        fillColor = typedArray.getColor(R.styleable.RecordView_fill_color, Color.WHITE);
        progressColor = typedArray.getColor(R.styleable.RecordView_progress_color, Color.RED);
        maxDuration = typedArray.getInteger(R.styleable.RecordView_duration, 10);
        setMaxDuration(maxDuration);
        typedArray.recycle();

        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setColor(fillColor);
        fillPaint.setStyle(Paint.Style.FILL);

        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setColor(progressColor);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(progressWidth);

        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                progressValue++;
                postInvalidate();
                if (progressValue <= progressMaxValue) {
                    sendEmptyMessageDelayed(0, PROGRESS_INTERVAL);
                } else {
                    finishRecord();
                }
            }
        };


        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    isRecording = true;
                    startRecordTime = System.currentTimeMillis();
                    handler.sendEmptyMessage(0);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    long now = System.currentTimeMillis();
                    if (now - startRecordTime > ViewConfiguration.getLongPressTimeout()) {
                        finishRecord();
                    }

                    handler.removeCallbacksAndMessages(null);
                    isRecording = false;
                    progressValue = 0;
                    startRecordTime = 0;
                    postInvalidate();

                }
                return false;
            }
        });


        setOnClickListener(this);
        setOnLongClickListener(this);


    }

    /**
     * 结束录制
     */
    private void finishRecord() {
        if (mRecordListener != null) {
            mRecordListener.onFinish();
        }

    }

    /**
     * 绘画进度条和录制按钮
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        if (isRecording) {
            canvas.drawCircle(width / 2, height / 2, width / 2, fillPaint);

            int left = 0;
            int top = 0;
            int right = width;
            int bottom = height;
            float sweepAngle = (progressValue / progressMaxValue) * 360;
            canvas.drawArc(left, top, right, bottom, -90, sweepAngle, false, progressPaint);

        } else {
            canvas.drawCircle(width / 2, height / 2, record_radius, fillPaint);
        }
    }

    /**
     * @param maxDuration
     */
    private void setMaxDuration(int maxDuration) {
        this.progressMaxValue = maxDuration * 1000 / PROGRESS_INTERVAL;
    }

    public void setOnRecordListener(onRecordListener onRecordListener) {
        mRecordListener = onRecordListener;
    }

    @Override
    public void onClick(View v) {
        if (mRecordListener != null) {
            mRecordListener.onClick();
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (mRecordListener != null) {
            mRecordListener.onLongClick();
        }

        return true;
    }


    public interface onRecordListener {
        void onClick();

        void onLongClick();

        void onFinish();
    }
}