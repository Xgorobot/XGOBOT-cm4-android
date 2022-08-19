package com.luwu.xgo_robot.mView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.luwu.xgo_robot.R;
import com.luwu.xgo_robot.mMothed.PublicMethod;

public class VerticalSeekBar extends View {

    final int DEFAULT_BACKGROUND_Width = 260, DEFAULT_BACKGROUND_Height = 40;
    private Bitmap mBackgroundBitmap;
    private Bitmap mThumbBitmap;
    private GradientDrawable mPastColor;

    private Paint mBackgroundPaint;
    private Paint mThumbPaint;
    private Paint mPastPaint;
    private Paint mWordPaint;
    private int mProgress = 0;//0-100
    private int measuredHeight;
    private int measuredWidth;
    private int wordSize;
    private ISeekBarListener mListener;

    public VerticalSeekBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttribute(context, attrs);//拿到自定义属性

        mBackgroundPaint = new Paint();
        mThumbPaint = new Paint();
        mPastPaint = new Paint();
        mWordPaint = new Paint();
        mBackgroundPaint.setAntiAlias(true);
        mThumbPaint.setAntiAlias(true);
        mPastPaint.setAntiAlias(true);
        mWordPaint.setAntiAlias(true);
    }

    public int getProgress() {
        return this.mProgress;
    }

    public void setProgress(int progress) {
        this.mProgress = progress;
        if (mProgress > 100) {
            mProgress = 100;
        } else if (mProgress < 0) {
            mProgress = 0;
        }
        com.luwu.xgo_robot.mActivity.ControlActivity.progress = mProgress;
    }

    /**
     * update progress with view
     * @param progress
     */
    public void updateProgress(int progress) {
        setProgress(progress);
        invalidate();//view重绘
    }

    public void setListener(ISeekBarListener listener) {
        this.mListener = listener;
    }

    private void initAttribute(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.VerticalSeekBar);

        Drawable mBackground = typedArray.getDrawable(R.styleable.VerticalSeekBar_allBackground);
        if (null != mBackground) {
            // 设置了背景
            if (mBackground instanceof BitmapDrawable) {
                // 设置了一张图片
                mBackgroundBitmap = ((BitmapDrawable) mBackground).getBitmap();
            }
        }
        // 按钮背景
        Drawable mThumb = typedArray.getDrawable(R.styleable.VerticalSeekBar_thumbBackground);
        if (null != mThumb) {
            // 设置了按钮背景
            if (mThumb instanceof BitmapDrawable) {
                // 图片
                mThumbBitmap = ((BitmapDrawable) mThumb).getBitmap();
            }
        }
        // 经过背景
        Drawable mPast = typedArray.getDrawable(R.styleable.VerticalSeekBar_pastBackground);
        if (null != mPast) {
            // 设置了经过背景
            if (mPast instanceof GradientDrawable) {
                // shape.xml
                mPastColor = ((GradientDrawable) mPast);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureWidth, measureHeight;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            // 具体的值和match_parent
            measureWidth = widthSize;
        } else {
            // wrap_content
            measureWidth = DEFAULT_BACKGROUND_Width;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            measureHeight = heightSize;
        } else {
            measureHeight = DEFAULT_BACKGROUND_Height;
        }
        setMeasuredDimension(measureWidth, measureHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        measuredHeight = getMeasuredHeight();
        measuredWidth = getMeasuredWidth();
        wordSize = measuredHeight / 2;

        int r = (int) (measuredHeight / 2);//半径
        int x = (int) ((0.01 * mProgress) * (measuredWidth - 2 * r-2*wordSize)) + r+wordSize;//圆心的x坐标
        int y = (int) (measuredHeight / 2);//圆心y坐标

        // 背景边缘坐标
        int sLeft = r + wordSize;
        int sRight = measuredWidth - r - wordSize;
        int sTop = (int) (measuredHeight * 0.33f);
        int sBottom = (int) (measuredHeight * 0.67f);
        //写字
        mWordPaint.setColor(getResources().getColor(R.color.colorWhite));
        mWordPaint.setTextSize(wordSize);
        switch(PublicMethod.localeLanguage){
            case "zh":
                mWordPaint.setTextAlign(Paint.Align.LEFT);
                canvas.drawText("低",0,0.75f*measuredHeight,mWordPaint);
                mWordPaint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText("高",measuredWidth,0.75f*measuredHeight,mWordPaint);
                break;
            default:
                mWordPaint.setTextAlign(Paint.Align.LEFT);
                canvas.drawText("Low",0,0.75f*measuredHeight,mWordPaint);
                mWordPaint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText("High",measuredWidth,0.75f*measuredHeight,mWordPaint);
        }
        //绘制背景
        Rect src = new Rect(0, 0, mBackgroundBitmap.getWidth(), mBackgroundBitmap.getHeight());
        Rect dst = new Rect(sLeft, sTop, sRight, sBottom);
        canvas.drawBitmap(mBackgroundBitmap, src, dst, mBackgroundPaint);
        //绘制图标区
        mPastPaint.setColor(getResources().getColor(R.color.transparent));
        Rect rect = new Rect(sLeft, sTop, sRight, sBottom);
        canvas.drawRect(rect, mPastPaint);
        //绘制经过背景
        mPastColor.setBounds(new Rect(sLeft, sTop, x, sBottom));
        mPastColor.draw(canvas);
        //绘制圆形
        src = new Rect(0, 0, mThumbBitmap.getWidth(), mThumbBitmap.getHeight());
        dst = new Rect(x - r, y - r, x + r, y + r);
        canvas.drawBitmap(mThumbBitmap, src, dst, mThumbPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float xTouch = event.getX();
        mProgress = (int) (xTouch / measuredWidth * 100);
        if (mProgress > 100) {
            mProgress = 100;
        } else if (mProgress < 0) {
            mProgress = 0;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mListener != null) {
                    mListener.actionDown();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mListener != null) {
                    mListener.actionUp();
                }
                break;
            case MotionEvent.ACTION_MOVE:
//                Log.d("Tag", "onTouchEvent: "+mProgress);
                if (mListener != null) {
                    mListener.actionMove();
                }
                invalidate();
                break;
        }

        return true;
    }

    public interface ISeekBarListener {
        void actionDown();

        void actionUp();

        void actionMove();
    }

}
