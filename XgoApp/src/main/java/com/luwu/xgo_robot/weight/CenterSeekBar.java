package com.luwu.xgo_robot.weight;

/**
 * <p>文件描述：<p>
 * <p>作者：zhangyibin<p>
 * <p>创建时间：2022/10/08<p>
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.luwu.xgo_robot.R;

/**
 * 中间向左右滑动的滑动条
 */
public class CenterSeekBar extends View {

    private static final String TAG = CenterSeekBar.class.getSimpleName();
    private static final int CLICK_ON_PRESS = 1;
    /*中间的拖动bar*/
    private Drawable mThumb;
    //默认的背景
    private Drawable mBackgroundDrawable;
    //滑动后的背景
    private Drawable mProgressDrawable;
    private int mSeekBarWidth;
    private int mSeekBarHeight;
    private int mThumbWidth;
    private int mThumbHeight;
    //thumb的中心位置
    private int mThumbCenterPosition = 0;
    private OnSeekBarChangeListener mSeekBarChangeListener;
    private int mFlag;
    int mMinWidth;
    int mMaxWidth;
    int mMinHeight = 12;
    int mMaxHeight;
    protected int mPaddingLeft = 0;
    protected int mPaddingRight = 0;
    protected int mPaddingTop = 0;
    protected int mPaddingBottom = 0;
    private int maxProgress;
    private int minProgress = 0;
    private int progress;

    public CenterSeekBar(Context context) {
        this(context, null);
    }

    public CenterSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CenterSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.CenterSeekBar, defStyleAttr, defStyleAttr);
        mThumb = a.getDrawable(R.styleable.CenterSeekBar_thumb);
        if (mThumb == null) {
            mThumb = getResources().getDrawable(R.drawable.seekbar_thumb);
        }
        mProgressDrawable = a.getDrawable(R.styleable.CenterSeekBar_progressDrawable);
        if (mProgressDrawable == null) {
            mProgressDrawable = getResources().getDrawable(R.drawable.seekbar_progress_drawable);
        }
        mBackgroundDrawable = a.getDrawable(R.styleable.CenterSeekBar_backgroundDrawable);
        if (mBackgroundDrawable == null) {
            mBackgroundDrawable = getResources().getDrawable(R.drawable.seekbar_background);
        }
        progress = a.getInt(R.styleable.CenterSeekBar_progress, 0);
        minProgress = a.getInt(R.styleable.CenterSeekBar_min, 0);
        maxProgress = a.getInt(R.styleable.CenterSeekBar_max, 0);
        mSeekBarHeight = mBackgroundDrawable.getIntrinsicHeight();
        mSeekBarWidth = mBackgroundDrawable.getIntrinsicWidth();
        mThumbHeight = mThumb.getIntrinsicHeight();
        mThumbWidth = mThumb.getIntrinsicWidth();
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final Drawable d = mProgressDrawable;
        int thumbHeight = mThumb == null ? 0 : mThumb.getIntrinsicHeight();
        int dw = 0;
        int dh = 0;
        if (d != null) {
            dw = Math.max(mMinWidth, Math.min(mMaxWidth, d.getIntrinsicWidth()));
            dh = Math.max(mMinHeight, Math.min(mMaxHeight, d.getIntrinsicHeight()));
            dh = Math.max(thumbHeight, dh);

        }
        dw += mPaddingLeft + mPaddingRight;
        dh += mPaddingTop + mPaddingBottom;
        mSeekBarWidth = resolveSizeAndState(dw, widthMeasureSpec, 0);
        mSeekBarHeight = resolveSizeAndState(dh, heightMeasureSpec, 0);
        mThumbCenterPosition = mSeekBarWidth / 2;
        setMeasuredDimension(mSeekBarWidth + mThumbWidth, mSeekBarHeight);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mBackgroundDrawable.setBounds(mThumbWidth / 2, mSeekBarHeight / 2 - mMinHeight / 2, mSeekBarWidth - mThumbWidth / 2, mSeekBarHeight / 2 + mMinHeight / 2);
        mBackgroundDrawable.draw(canvas);
        if (mThumbCenterPosition > mSeekBarWidth / 2) {
            mProgressDrawable.setBounds(mSeekBarWidth / 2, mSeekBarHeight / 2 - mMinHeight / 2, mThumbCenterPosition, mSeekBarHeight / 2 + mMinHeight / 2);
        } else if (mThumbCenterPosition < mSeekBarWidth / 2) {
            mProgressDrawable.setBounds(mThumbCenterPosition, mSeekBarHeight / 2 - mMinHeight / 2, mSeekBarWidth / 2, mSeekBarHeight / 2 + mMinHeight / 2);
        } else {
            mProgressDrawable.setBounds(mThumbCenterPosition + mThumbWidth / 2, mSeekBarHeight / 2 - mMinHeight / 2, mSeekBarWidth / 2, mSeekBarHeight / 2 + mMinHeight / 2);
        }
        mProgressDrawable.draw(canvas);
        mThumb.setBounds(mThumbCenterPosition - mThumbWidth / 2, 0, mThumbCenterPosition + mThumbWidth / 2, mThumbHeight);
        mThumb.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mFlag = CLICK_ON_PRESS;
                setPressed(true); //设置按下的Thumb效果
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                if (mSeekBarChangeListener != null) {
                    mSeekBarChangeListener.onStartTrackingTouch(this);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mFlag == CLICK_ON_PRESS) {
                    float scale;
                    if (event.getX() <= mThumbWidth / 2) {
                        scale = 0;
                    } else if (event.getX() >= (mSeekBarWidth - mThumbWidth / 2)) {
                        scale = 1;
                    } else {
                        scale = (Math.abs(event.getX() - mThumbWidth / 2)) / (mSeekBarWidth - mThumbWidth);
                    }
                    int progressPosition = (int) ((maxProgress - minProgress) * scale);
                    mThumbCenterPosition = progressPosition * (mSeekBarWidth - mThumbWidth) / (maxProgress - minProgress) + mThumbWidth / 2;
                    this.progress = progressPosition + minProgress;
                    if (progressPosition > maxProgress) {
                        progress = maxProgress;
                    } else if (progress < minProgress) {
                        progress = minProgress;
                    }
                    if (mSeekBarChangeListener != null) {
                        mSeekBarChangeListener.onProgressChanged(this, progress, true);
                    }
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                setPressed(false);
                invalidate();
                if (mSeekBarChangeListener != null) {
                    mSeekBarChangeListener.onStopTrackingTouch(this);
                }
                break;
        }
        return true;
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        //设置的Thumb的状态
        final Drawable thumb = mThumb;
        if (thumb != null && thumb.isStateful()
                && thumb.setState(getDrawableState())) {
            invalidateDrawable(thumb);
        }
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }

    public int getMinProgress() {
        return minProgress;
    }

    public void setMinProgress(int minProgress) {
        this.minProgress = minProgress;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        if (progress < maxProgress && progress > minProgress) {
            this.progress = progress;
        } else {
            this.progress = minProgress;
        }
        int progressPosition = progress - minProgress;
        mThumbCenterPosition = progressPosition * mSeekBarWidth / (maxProgress - minProgress);
        invalidate();
    }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener listener) {
        mSeekBarChangeListener = listener;
    }

    public interface OnSeekBarChangeListener {
        void onProgressChanged(CenterSeekBar seekBar, int progress, boolean fromUser);

        void onStartTrackingTouch(CenterSeekBar seekBar);

        void onStopTrackingTouch(CenterSeekBar seekBar);
    }
}