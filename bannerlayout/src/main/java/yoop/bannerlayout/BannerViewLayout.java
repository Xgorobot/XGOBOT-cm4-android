package yoop.bannerlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.makeramen.roundedimageview.RoundedImageView;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class BannerViewLayout extends LinearLayout implements ViewPager.OnPageChangeListener {

    private static final String TAG = BannerViewLayout.class.getSimpleName();

    private LayoutInflater mInflater;

    private BannerViewPager mViewPager;

    private int mVpMarginLeft, mVpMarginRight, mVpPageMargin;
    private int mCount;
    private int mCurrentItem;
    private int mStartItem = 0;
    private int mImageCorner = 18;
    private boolean isScroll = true;

    private List<Drawable> mImageUrls;
    private List<View> mBannerViews;

    private ViewPager.OnPageChangeListener mOnPageChangeListener;
    private OnBannerListener mListener;
    private BannerPageAdapter mPageAdapter;
    private BannerViewLayoutScroller mScroller;

    private Handler mInnerHandler = InnerHandler.getInstance();

    public BannerViewLayout(Context context) {
        this(context, null);
    }

    public BannerViewLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerViewLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(LinearLayout.VERTICAL);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BannerViewLayout);
        mVpMarginLeft = ta.getDimensionPixelSize(R.styleable.BannerViewLayout_bl_vp_margin_left, 0);
        mVpMarginRight = ta.getDimensionPixelSize(R.styleable.BannerViewLayout_bl_vp_margin_right, 0);
        mVpPageMargin = ta.getDimensionPixelSize(R.styleable.BannerViewLayout_bl_vp_page_margin, 0);
        ta.recycle();
        mInflater = LayoutInflater.from(context);
        View view = mInflater.inflate(R.layout.layout_banner_lay, this, true);
        mViewPager = view.findViewById(R.id.view_pager);
        mViewPager.setPageMargin(mVpPageMargin);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mViewPager.getLayoutParams();
        layoutParams.leftMargin = mVpMarginLeft;
        layoutParams.rightMargin = mVpMarginRight;
        mViewPager.setLayoutParams(layoutParams);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setBackgroundColor(getResources().getColor(R.color.transparent));
        mImageUrls = new ArrayList<>();
        mBannerViews = new ArrayList<>();
        initViewPagerScroll();

    }


    private void initViewPagerScroll() {
        try {
            Field mField = ViewPager.class.getDeclaredField("mScroller");
            mField.setAccessible(true);
            mScroller = new BannerViewLayoutScroller(mViewPager.getContext());
            mField.set(mViewPager, mScroller);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public int getVpMarginLeft() {
        return mVpMarginLeft;
    }

    public void setVpMarginLeft(int vpMarginLeft) {
        mVpMarginLeft = vpMarginLeft;
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mViewPager.getLayoutParams();
        layoutParams.leftMargin = mVpMarginLeft;
        mViewPager.setLayoutParams(layoutParams);
    }

    public int getVpMarginRight() {
        return mVpMarginRight;
    }

    public void setVpMarginRight(int vpMarginRight) {
        mVpMarginRight = vpMarginRight;
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mViewPager.getLayoutParams();
        layoutParams.rightMargin = mVpMarginRight;
        mViewPager.setLayoutParams(layoutParams);
    }

    public int getVpPageMargin() {
        return mVpPageMargin;
    }

    public void setVpPageMargin(int vpPageMargin) {
        mVpPageMargin = vpPageMargin;
        mViewPager.setPageMargin(mVpPageMargin);
    }

    public BannerViewLayout setStartView(int startView) {
        this.mStartItem = startView % mCount;
        return this;
    }

    public BannerViewLayout setOffscreenPageLimit(int limit) {
        if (mViewPager != null) {
            mViewPager.setOffscreenPageLimit(limit);
        }
        return this;
    }

    public BannerViewLayout setPageTransformer(boolean reverseDrawingOrder, ViewPager.
            PageTransformer transformer) {
        mViewPager.setPageTransformer(reverseDrawingOrder, transformer);
        return this;
    }

    public BannerViewLayout setImageCorner(int imageCorner) {
        mImageCorner = imageCorner;
        return this;
    }

    public BannerViewLayout setBannerView(List<Drawable> imageUrls) {
        this.mImageUrls = imageUrls;
        this.mCount = imageUrls.size();
        return this;
    }

    public BannerViewLayout start() {
        setImageView();
        setData();
        return this;
    }

    private void setImageView() {
        for (int i = 0; i < mCount; i++) {
            RoundedImageView imageView = new RoundedImageView(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setCornerRadius(mImageCorner);
            imageView.setImageDrawable(mImageUrls.get(i));
            mBannerViews.add(imageView);

        }
    }

    private void setData() {
        mCurrentItem = mStartItem;
        if (mPageAdapter == null) {
            mPageAdapter = new BannerPageAdapter();
            mViewPager.addOnPageChangeListener(this);
        }
        mViewPager.setAdapter(mPageAdapter);
        mViewPager.setFocusable(true);
        mViewPager.setCurrentItem(mStartItem);
        mViewPager.setPageTransformer(false, new ScaleTransformer());
        if (isScroll && mCount > 1) {
            mViewPager.setScrollable(true);
        } else {
            mViewPager.setScrollable(false);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        mCurrentItem = position;
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageSelected(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrollStateChanged(state);
        }
        switch (state) {
            case 0://No operation
                break;
            case 1://start Sliding
                break;
            case 2://end Sliding
                break;
        }
    }

    private class BannerPageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mBannerViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            container.addView(mBannerViews.get(position));
            View view = mBannerViews.get(position);
            if (mListener != null) {
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.OnBannerClick(position);
                    }
                });
            }
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    public class ScaleTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.70f;
        private static final float MIN_ALPHA = 0.5f;

        @Override
        public void transformPage(View page, float position) {
            if (position < -1 || position > 1) {
                page.setAlpha(MIN_ALPHA);
                page.setScaleX(MIN_SCALE);
                page.setScaleY(MIN_SCALE);
            } else if (position <= 1) { // [-1,1]
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                if (position < 0) {
                    float scaleX = 1 + 0.3f * position;
                    page.setScaleX(scaleX);
                    page.setScaleY(scaleX);
                } else {
                    float scaleX = 1 - 0.3f * position;
                    page.setScaleX(scaleX);
                    page.setScaleY(scaleX);
                }
                page.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA));
            }
        }
    }


    public void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        mOnPageChangeListener = onPageChangeListener;
    }

    public BannerViewLayout setOnBannerListener(OnBannerListener listener) {
        this.mListener = listener;
        return this;
    }

    public interface OnBannerListener {
        void OnBannerClick(int position);
    }

    private static class InnerHandler extends Handler {
        private static WeakReference<InnerHandler> sInstanceRef;

        private InnerHandler() {
            super();
        }

        public static Handler getInstance() {
            if (sInstanceRef != null && sInstanceRef.get() != null) {
                return sInstanceRef.get();
            }
            InnerHandler instance = new InnerHandler();
            sInstanceRef = new WeakReference<>(instance);
            return instance;
        }
    }

}
