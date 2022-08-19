package com.luwu.xgo_robot.mView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.luwu.xgo_robot.R;

public class ButtonView extends View {
    private static final int USELESSPRESS = 0;
    public static final int UPPRESS = 1;
    public static final int DOWNPRESS = 2;
    public static final int RIGHTPRESS = 3;
    public static final int LEFTPRESS = 4;
    private final int DEFAULT_BACKGROUND_SIZE = 400;
    private int measureWidth, measureHeight;
    IButtonViewListener mListener;
    private Bitmap mBackgroundBitmap;
    private Bitmap mUpBitmap;
    private Bitmap mDownBitmap;
    private Bitmap mRightBitmap;
    private Bitmap mLeftBitmap;
    private Bitmap mUseBitmap;
    private Paint mBackgroundPaint;

    public ButtonView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttribute(context, attrs);//拿到自定义属性
        mUseBitmap = mBackgroundBitmap;
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setAntiAlias(true);
    }

    //提供接口
    public void setButtonViewListener(IButtonViewListener mListener) {
        this.mListener = mListener;
    }

    private void initAttribute(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ButtonView);

        Drawable mBackground = typedArray.getDrawable(R.styleable.ButtonView_noPress);
        if (null != mBackground) {
            if (mBackground instanceof BitmapDrawable) {
                mBackgroundBitmap = ((BitmapDrawable) mBackground).getBitmap();
            }
        }
        mBackground = typedArray.getDrawable(R.styleable.ButtonView_upPress);
        if (null != mBackground) {
            if (mBackground instanceof BitmapDrawable) {
                mUpBitmap = ((BitmapDrawable) mBackground).getBitmap();
            }
        }
        mBackground = typedArray.getDrawable(R.styleable.ButtonView_downPress);
        if (null != mBackground) {
            if (mBackground instanceof BitmapDrawable) {
                mDownBitmap = ((BitmapDrawable) mBackground).getBitmap();
            }
        }
        mBackground = typedArray.getDrawable(R.styleable.ButtonView_rightPress);
        if (null != mBackground) {
            if (mBackground instanceof BitmapDrawable) {
                mRightBitmap = ((BitmapDrawable) mBackground).getBitmap();
            }
        }
        mBackground = typedArray.getDrawable(R.styleable.ButtonView_leftPress);
        if (null != mBackground) {
            if (mBackground instanceof BitmapDrawable) {
                mLeftBitmap = ((BitmapDrawable) mBackground).getBitmap();
            }
        }
        typedArray.recycle();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            // 具体的值和match_parent
            measureWidth = widthSize;
        } else {
            // wrap_content
            measureWidth = DEFAULT_BACKGROUND_SIZE;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            measureHeight = heightSize;
        } else {
            measureHeight = DEFAULT_BACKGROUND_SIZE;
        }
        setMeasuredDimension(measureWidth, measureHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();

        //绘制背景
        Rect src = new Rect(0, 0, mUseBitmap.getWidth(), mUseBitmap.getHeight());
        Rect dst = new Rect(0, 0, measuredWidth, measuredHeight);
        canvas.drawBitmap(mUseBitmap, src, dst, mBackgroundPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float moveX = event.getX();
        float moveY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                switch (getPressPosition(new Point((int) moveX, (int) moveY))) {
                    case UPPRESS:
                        if (mUpBitmap != null) {
                            changeBackground(mUpBitmap);
                            if (mListener != null) {
                                mListener.actionDown(UPPRESS);
                            }
                        }
                        break;
                    case DOWNPRESS:
                        if (mDownBitmap != null) {
                            changeBackground(mDownBitmap);
                            if (mListener != null) {
                                mListener.actionDown(DOWNPRESS);
                            }
                        }
                        break;
                    case LEFTPRESS:
                        if (mLeftBitmap != null) {
                            changeBackground(mLeftBitmap);
                            if (mListener != null) {
                                mListener.actionDown(LEFTPRESS);
                            }
                        }
                        break;
                    case RIGHTPRESS:
                        if (mRightBitmap != null) {
                            changeBackground(mRightBitmap);
                            if (mListener != null) {
                                mListener.actionDown(RIGHTPRESS);
                            }
                        }
                        break;
                }
                break;
            case MotionEvent.ACTION_UP:
                changeBackground(mBackgroundBitmap);
                switch (getPressPosition(new Point((int) moveX, (int) moveY))) {
                    case UPPRESS:
                        if (mUpBitmap != null) {
                            if (mListener != null) {
                                mListener.actionUp(UPPRESS);
                            }
                        }
                        break;
                    case DOWNPRESS:
                        if (mDownBitmap != null) {
                            if (mListener != null) {
                                mListener.actionUp(DOWNPRESS);
                            }
                        }
                        break;
                    case LEFTPRESS:
                        if (mLeftBitmap != null) {
                            if (mListener != null) {
                                mListener.actionUp(LEFTPRESS);
                            }
                        }
                        break;
                    case RIGHTPRESS:
                        if (mRightBitmap != null) {
                            if (mListener != null) {
                                mListener.actionUp(RIGHTPRESS);
                            }
                        }
                        break;
                }
                break;
        }
        return true;
    }

    private int getPressPosition(Point touchPoint) {
        Point centerPoint = new Point(measureWidth / 2, measureHeight / 2);
        float r = measureHeight < measureWidth ? measureHeight / 2 : measureWidth / 2;
        // 两点在X轴的距离
        float lenX = (float) (touchPoint.x - centerPoint.x);
        // 两点在Y轴距离
        float lenY = (float) (touchPoint.y - centerPoint.y);
        // 两点距离
        float lenXY = (float) Math.sqrt((double) (lenX * lenX + lenY * lenY));
        // 计算弧度
        double radian = Math.acos(lenX / lenXY) * (touchPoint.y < centerPoint.y ? -1 : 1);
        // 计算角度
        double angle = radian2Angle(radian);
        double percent = lenXY * 100 / r;

        if (percent > 100) {
            percent = percent > 100 ? 100 : percent;
        }
        if (percent > 46) { // 触摸位置在可活动范围内
            if (230 <= angle && angle <= 310) {//上
                return UPPRESS;
            } else if (50 <= angle && angle <= 130) {//下
                return DOWNPRESS;
            } else if (140 <= angle && angle <= 220) {//左
                return LEFTPRESS;
            } else if ((0 <= angle && angle <= 40) || (320 <= angle && angle <= 360)) {//右
                return RIGHTPRESS;
            }
        }
        return USELESSPRESS;
    }

    private double radian2Angle(double radian) {
        double tmp = Math.round(radian / Math.PI * 180);
        return tmp >= 0 ? tmp : 360 + tmp;
    }

    private void changeBackground(Bitmap bitmap) {
        if (bitmap != null) {
            mUseBitmap = bitmap;
            invalidate();
        }
    }

    public interface IButtonViewListener {
        void actionDown(int num);
        void actionUp(int num);
    }

}
