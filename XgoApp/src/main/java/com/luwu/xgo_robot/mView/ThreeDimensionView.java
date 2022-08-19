package com.luwu.xgo_robot.mView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
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

public class ThreeDimensionView extends View {

    private static final int DEFAULT_BACKGROUND_SIZE = 230;
    private final float ROUND_PERCENT = 0.6f;
    private final float BALLGROUND_PERCENT = 0.33f;
    private final float BALL_PERCENT = 0.1f;
    //设置的属性
    private Bitmap mBackgroundBitmap;
    private Bitmap mRoundBitmap;
    private Bitmap mBallgroundBitmap;
    private Bitmap mBallBitmap;

    private Paint mBackgroundPaint;
    private Paint mRoundPaint;
    private Paint mBallgroundPaint;
    private Paint mBallPaint;

    private int measureWidth, measureHeight;
    private int mBackgroundRadius;
    private Point mBackGroundPoint;
    private Point mBallPosition;

    private int pitch, roll;//-90,90
    private int yaw;//-180-180

    public int getPitch() {
        return pitch;
    }

    public int getRoll() {
        return roll;
    }

    public float getYaw() {
        return yaw;
    }

    public void setThreeDimension(int pitch, int roll, int yaw) {
        this.pitch = pitch;
        this.roll = roll;
        this.yaw = yaw;
        mBallPosition = getRockerPositionPoint(mBackGroundPoint, new Point((int) pitch, (int) roll), mBackgroundRadius * ROUND_PERCENT, mBackgroundRadius * BALL_PERCENT);
        moveRocker(mBallPosition.x, mBallPosition.y);
    }

    public ThreeDimensionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttribute(context, attrs);//拿到自定义属性

        mBackgroundPaint = new Paint();
        mRoundPaint = new Paint();
        mBallgroundPaint = new Paint();
        mBallPaint = new Paint();
        mBackgroundPaint.setAntiAlias(true);
        mRoundPaint.setAntiAlias(true);
        mBallgroundPaint.setAntiAlias(true);
        mBallPaint.setAntiAlias(true);
        mBackGroundPoint = new Point();
        mBallPosition = new Point();

        yaw = 0;
        pitch = 0;
        roll = 0;
    }


    private void initAttribute(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ThreeDimension);

        Drawable mBackground = typedArray.getDrawable(R.styleable.ThreeDimension_dimensionBackground);
        if (null != mBackground) {
            if (mBackground instanceof BitmapDrawable) {
                mBackgroundBitmap = ((BitmapDrawable) mBackground).getBitmap();
            }
        }
        mBackground = typedArray.getDrawable(R.styleable.ThreeDimension_roundBackground);
        if (null != mBackground) {
            if (mBackground instanceof BitmapDrawable) {
                mRoundBitmap = ((BitmapDrawable) mBackground).getBitmap();
            }
        }
        mBackground = typedArray.getDrawable(R.styleable.ThreeDimension_ballBackground);
        if (null != mBackground) {
            if (mBackground instanceof BitmapDrawable) {
                mBallgroundBitmap = ((BitmapDrawable) mBackground).getBitmap();
            }
        }
        mBackground = typedArray.getDrawable(R.styleable.ThreeDimension_ball);
        if (null != mBackground) {
            if (mBackground instanceof BitmapDrawable) {
                mBallBitmap = ((BitmapDrawable) mBackground).getBitmap();
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
        mBackgroundRadius = measureHeight < measureWidth ? measureHeight / 2 : measureWidth / 2;
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
        if (0 == mBallPosition.x || 0 == mBallPosition.y) {
            mBallPosition.set(mBackGroundPoint.x, mBackGroundPoint.y);
        }
        //绘制背景
        Rect src = new Rect(0, 0, mBackgroundBitmap.getWidth(), mBackgroundBitmap.getHeight());
        Rect dst = new Rect(mBackGroundPoint.x - mBackgroundRadius, mBackGroundPoint.y - mBackgroundRadius, mBackGroundPoint.x + mBackgroundRadius, mBackGroundPoint.y + mBackgroundRadius);
        canvas.drawBitmap(mBackgroundBitmap, src, dst, mBackgroundPaint);
        //绘制指针
        int radius = (int) (mBackgroundRadius * ROUND_PERCENT);
        Bitmap rotatingBitmap = rotatingImageView(yaw,mRoundBitmap);
        radius = radius*rotatingBitmap.getWidth()/mRoundBitmap.getWidth();//防止被拉伸
        src = new Rect(0, 0, rotatingBitmap.getWidth(), rotatingBitmap.getHeight());
        dst = new Rect(mBackGroundPoint.x - radius, mBackGroundPoint.y - radius, mBackGroundPoint.x + radius, mBackGroundPoint.y + radius);
        canvas.drawBitmap(rotatingBitmap, src, dst, mRoundPaint);
        //绘制球背景
        radius = (int) (mBackgroundRadius * BALLGROUND_PERCENT);
        src = new Rect(0, 0, mBallgroundBitmap.getWidth(), mBallgroundBitmap.getHeight());
        dst = new Rect(mBackGroundPoint.x - radius, mBackGroundPoint.y - radius, mBackGroundPoint.x + radius, mBackGroundPoint.y + radius);
        canvas.drawBitmap(mBallgroundBitmap, src, dst, mBallgroundPaint);
        //绘制球
        radius = (int) (mBackgroundRadius * BALL_PERCENT);
        src = new Rect(0, 0, mBallBitmap.getWidth(), mBallBitmap.getHeight());
        dst = new Rect(mBallPosition.x - radius, mBallPosition.y - radius, mBallPosition.x + radius, mBallPosition.y + radius);
        canvas.drawBitmap(mBallBitmap, src, dst, mBallPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
    //绕中心旋转图片
    private Bitmap rotatingImageView(int angle, Bitmap bitmap)
    {
        // 旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }
    private Point getRockerPositionPoint(Point centerPoint, Point touchPoint, float backgroundRadius, float rockerRadius) {

        touchPoint.x = (int) ((touchPoint.x + 90) * mBackgroundRadius / 90);
        touchPoint.y = (int) ((90 - touchPoint.y) * mBackgroundRadius / 90);
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
        double percent = lenXY / mBackgroundRadius;

        if (percent > 1)
            percent = percent > 1 ? 1 : percent;

        if (backgroundRadius * percent + rockerRadius <= backgroundRadius) { // 触摸位置在可活动范围内
            int showPointX = (int) (centerPoint.x + (backgroundRadius * percent) * Math.cos(radian));
            int showPointY = (int) (centerPoint.y + (backgroundRadius * percent) * Math.sin(radian));
            return new Point(showPointX, showPointY);
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
        mBallPosition.set((int) x, (int) y);
        invalidate();
    }

}
