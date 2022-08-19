package com.luwu.xgo_robot.mView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.luwu.xgo_robot.R;

public class RockerView extends View {

    private static final int DEFAULT_BACKGROUND_SIZE = 400;
    private static final float DEFAULT_ROCKER_RADIUSPERSENT = 0.4f;
    //设置的属性
    private Bitmap mBackgroundBitmap;
    private int mBackgroundColor;
    private Bitmap mRockerBitmap;
    private int mRockerColor;
    private int mBackgroundRadius;
    private int mRockerRadius;
    private float mRockerRadiusPersent;

    final private int isPhoto = 0, isColor = 1, isDefault = 2;//设置的背景模式 0：图片 1：颜色 2默认模式
    private int mBackgroundMode = isDefault, mRockerMode = isDefault;

    private Paint mBackgroundPaint;
    private Paint mRockerPaint;

    private Point mBackGroundPoint;
    private Point mRockerPosition;

    private IRockViewListener mListener;
    private Point speedPersent;
    //降低机器狗运动速度
    private float speedRatio = 0.8f;
    //提供给外部的数据 -100< .x <100  -100< .y <100
    public Point getSpeed(){
        return new Point((int)(this.speedPersent.x*speedRatio), (int)(this.speedPersent.y*speedRatio));
    }
    //提供给外部的事件接口
    public void setRockViewListener(IRockViewListener mListener) {
        this.mListener = mListener;
    }

    public RockerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttribute(context, attrs);//拿到自定义属性

        mBackgroundPaint = new Paint();
        mRockerPaint = new Paint();
        mBackgroundPaint.setAntiAlias(true);
        mRockerPaint.setAntiAlias(true);
        mBackGroundPoint = new Point();
        mRockerPosition = new Point();

        speedPersent = new Point(0, 0);
    }


    private void initAttribute(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RockerView);

        Drawable mBackground = typedArray.getDrawable(R.styleable.RockerView_areaBackground);
        if (null != mBackground) {
            // 设置了背景
            if (mBackground instanceof BitmapDrawable) {
                // 设置了一张图片
                mBackgroundBitmap = ((BitmapDrawable) mBackground).getBitmap();
                mBackgroundMode = isPhoto;
            } else if (mBackground instanceof ColorDrawable) {
                // 色值
                mBackgroundColor = ((ColorDrawable) mBackground).getColor();
                mBackgroundMode = isColor;
            } else {
                // 其他形式
                mBackgroundMode = isDefault;
            }
        } else {
            // 没有设置背景
            mBackgroundMode = isDefault;
        }
        // 摇杆背景
        Drawable mRockre = typedArray.getDrawable(R.styleable.RockerView_rockerBackground);
        if (null != mRockre) {
            // 设置了摇杆背景
            if (mRockre instanceof BitmapDrawable) {
                // 图片
                mRockerBitmap = ((BitmapDrawable) mRockre).getBitmap();
                mRockerMode = isPhoto;
            } else if (mRockre instanceof ColorDrawable) {
                // 色值
                mRockerColor = ((ColorDrawable) mRockre).getColor();
                mRockerMode = isColor;
            } else {
                // 其他形式
                mRockerMode = isDefault;
            }
        } else {
            // 没有设置摇杆背景
            mRockerMode = isDefault;
        }
        // 半径
        mRockerRadiusPersent = typedArray.getFloat(R.styleable.RockerView_rockerRadiusPersent, DEFAULT_ROCKER_RADIUSPERSENT);
        typedArray.recycle();
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

        int cx = measuredWidth / 2;
        int cy = measuredHeight / 2;
        // 中心点
        mBackGroundPoint.set(cx, cy);
        // 摇杆位置
        if (0 == mRockerPosition.x || 0 == mRockerPosition.y) {
            mRockerPosition.set(mBackGroundPoint.x, mBackGroundPoint.y);
        }
        // 可移动区域的半径
        mBackgroundRadius = (measuredWidth <= measuredHeight) ? cx : cy;
//摇杆半径
        mRockerRadius = (int) (mBackgroundRadius * mRockerRadiusPersent);
        //绘制背景
        if (mBackgroundMode == isColor) {
            mBackgroundPaint.setColor(mBackgroundColor);
            canvas.drawCircle(mBackGroundPoint.x, mBackGroundPoint.y, mBackgroundRadius, mBackgroundPaint);
        } else if (mBackgroundMode == isPhoto) {
            Rect src = new Rect(0, 0, mBackgroundBitmap.getWidth(), mBackgroundBitmap.getHeight());
            Rect dst = new Rect(mBackGroundPoint.x - mBackgroundRadius, mBackGroundPoint.y - mBackgroundRadius, mBackGroundPoint.x + mBackgroundRadius, mBackGroundPoint.y + mBackgroundRadius);
            canvas.drawBitmap(mBackgroundBitmap, src, dst, mBackgroundPaint);
        } else if (mBackgroundMode == isDefault) {
            mBackgroundPaint.setColor(Color.RED);
            canvas.drawCircle(mBackGroundPoint.x, mBackGroundPoint.y, mBackgroundRadius - 5, mBackgroundPaint);
            mBackgroundPaint.setColor(0xff808080);
            canvas.drawCircle(mBackGroundPoint.x, mBackGroundPoint.y, mBackgroundRadius - 10, mBackgroundPaint);
        }
        //绘制摇杆
        if (mRockerMode == isColor) {
            mRockerPaint.setColor(mRockerColor);
            canvas.drawCircle(mRockerPosition.x, mRockerPosition.y, mRockerRadius, mRockerPaint);
        } else if (mRockerMode == isPhoto) {
            Rect src = new Rect(0, 0, mRockerBitmap.getWidth(), mRockerBitmap.getHeight());
            Rect dst = new Rect(mRockerPosition.x - mRockerRadius, mRockerPosition.y - mRockerRadius, mRockerPosition.x + mRockerRadius, mRockerPosition.y + mRockerRadius);
            canvas.drawBitmap(mRockerBitmap, src, dst, mRockerPaint);
        } else if (mRockerMode == isDefault) {
            mRockerPaint.setColor(0xff03eb9c);
            canvas.drawCircle(mRockerPosition.x, mRockerPosition.y, mRockerRadius, mRockerPaint);
            mRockerPaint.setColor(Color.WHITE);
            canvas.drawCircle(mRockerPosition.x, mRockerPosition.y, mRockerRadius - 10, mRockerPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float moveX = event.getX();
        float moveY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                Log.d("Tag", "ACTION_DOWN: ");
                if (mListener != null) {
                    mListener.actionDown();
                }
                //刷新
                mRockerPosition = getRockerPositionPoint(mBackGroundPoint, new Point((int) moveX, (int) moveY), mBackgroundRadius, mRockerRadius);
                moveRocker(mRockerPosition.x, mRockerPosition.y);
                break;
            case MotionEvent.ACTION_UP:
//                Log.d("Tag", "ACTION_UP: ");
                if (mListener != null) {
                    mListener.actionUp();
                }
                //刷新
                mRockerPosition = getRockerPositionPoint(mBackGroundPoint, mBackGroundPoint, mBackgroundRadius, mRockerRadius);
                moveRocker(mRockerPosition.x, mRockerPosition.y);
                break;
            case MotionEvent.ACTION_MOVE:
//                Log.d("Tag", "ACTION_MOVE: ");
                if (mListener != null) {
                    mListener.actionMove();
                }
                //刷新
                mRockerPosition = getRockerPositionPoint(mBackGroundPoint, new Point((int) moveX, (int) moveY), mBackgroundRadius, mRockerRadius);
                moveRocker(mRockerPosition.x, mRockerPosition.y);
                break;
        }
        //计算speed
        speedPersent = new Point((int) ((mRockerPosition.x - mBackGroundPoint.x) * 100.0f / (mBackgroundRadius - mRockerRadius)), (int) ((mRockerPosition.y - mBackGroundPoint.y) * 100.0f / (mBackgroundRadius - mRockerRadius)));
        return true;
    }

    private Point getRockerPositionPoint(Point centerPoint, Point touchPoint, float backgroundRadius, float rockerRadius) {
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

        double percent = lenXY * 100 / (backgroundRadius - rockerRadius);

        if (percent > 100)
            percent = percent > 100 ? 100 : percent;

        if (lenXY + rockerRadius <= backgroundRadius) { // 触摸位置在可活动范围内
            return touchPoint;
        } else { // 触摸位置在可活动范围以外
            int showPointX = (int) (centerPoint.x + (backgroundRadius - rockerRadius) * Math.cos(radian));
            int showPointY = (int) (centerPoint.y + (backgroundRadius - rockerRadius) * Math.sin(radian));
            return new Point(showPointX, showPointY);
        }
    }

    private double radian2Angle(double radian) {
        double tmp = Math.round(radian / Math.PI * 180);
        return tmp >= 0 ? tmp : 360 + tmp;
    }

    private void moveRocker(float x, float y) {
        mRockerPosition.set((int) x, (int) y);
        invalidate();
    }

    public interface IRockViewListener {
        void actionDown();

        void actionUp();

        void actionMove();
    }

}
