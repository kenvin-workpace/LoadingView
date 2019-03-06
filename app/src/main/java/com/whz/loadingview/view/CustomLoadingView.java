package com.whz.loadingview.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.whz.loadingview.R;

/**
 * Created by kevin on 10/16/17.
 */

public class CustomLoadingView extends View {

    private final String aTag = CustomLoadingView.class.getSimpleName();

    //画笔
    private Paint mPaint;
    //中心圆坐标
    private int mCenterX, mCenterY;
    //旋转圆颜色
    private int[] mCircleColors;
    //6小圆角度
    private int mRotateRadius;
    //6小圆半径
    private int mRotateCircleRadius;
    //旋转2s
    private final long ROTATE_TIME = 8000;

    public CustomLoadingView(Context context) {
        this(context, null);
    }

    public CustomLoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initPaint();
        initColors();
    }

    /**
     * 初始化旋转圆颜色
     */
    private void initColors() {
        mCircleColors = getContext().getResources().getIntArray(R.array.circle_colors);
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mCenterX = getMeasuredWidth() / 2;
        mCenterY = getMeasuredHeight() / 2;

        mRotateRadius = mCenterX / 2;
        mRotateCircleRadius = mCenterX / 10;
    }

    private LoadingDraw mLoadingDraw;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画6个旋转圆
        if (mLoadingDraw == null) {
            mLoadingDraw = new RotateCircle();
        }
        mLoadingDraw.draw(canvas);
    }

    abstract class LoadingDraw {
        abstract void draw(Canvas canvas);
    }

    /**
     * 旋转动画
     */
    class RotateCircle extends LoadingDraw {

        private ValueAnimator mRotateAnimator;
        private float mCurRotateValue;

        public RotateCircle() {
            //开启旋转动画
            if (mRotateAnimator == null) {
                mRotateAnimator = ValueAnimator.ofFloat(0f, 2 * (float) Math.PI);
                mRotateAnimator.setDuration(ROTATE_TIME);
                mRotateAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        mCurRotateValue = (float) valueAnimator.getAnimatedValue();
                        invalidate();
                    }
                });
                mRotateAnimator.setInterpolator(new LinearInterpolator());
            }
            mRotateAnimator.setRepeatCount(-1);
            mRotateAnimator.start();
        }

        @Override
        void draw(Canvas canvas) {
            //初始角度
            float preAngle = (float) (2 * Math.PI / mCircleColors.length);

            for (int i = 0; i < mCircleColors.length; i++) {
                //圆颜色
                mPaint.setColor(mCircleColors[i]);
                //旋转角度
                float angle = i * preAngle + mCurRotateValue;
                //小圆中心圆
                int x = (int) (mCenterX + mRotateRadius * Math.cos(angle));
                int y = (int) (mCenterY + mRotateRadius * Math.sin(angle));
                //执行绘画  
                canvas.drawCircle(x, y, mRotateCircleRadius, mPaint);
            }
        }
    }
}
