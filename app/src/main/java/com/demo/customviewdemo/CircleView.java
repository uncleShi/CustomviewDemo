package com.demo.customviewdemo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Author: shihao
 * Date: 2019/4/30
 * Describe:从-90度开始的顺时针绘制的不同颜色的状态表示圆环(最多绘制三种颜色)
 */
public class CircleView extends View {

    /**
     * 圆环宽度,默认40
     */
    private float mStrokeWidth = 20.0f;
    /**
     * 圆环半径
     */
    private int mRadius = 140;

    private Paint paint_circle_one;
    private Paint paint_circle_two;
    private Paint paint_circle_three;

    private Paint textPaint1;
    private Paint textPaint2;
    //外切圆环的矩形
    private RectF mRectF;
    //文本的范围矩形
    private Rect mTextBound;

    //为了视觉效果而产生的间隔角度
    private int intervalAngle;

    /**
     * 第一段圆弧占比(为了动画实现,实时变化)
     */
    private float mPercent_one;
    /**
     * 第一段圆弧目标占比
     */
    private float mPercent_one_goal;
    /**
     * 第二段圆弧占比(为了动画实现,实时变化)
     */
    private float mPercent_two;
    /**
     * 第二段圆弧目标占比
     */
    private float mPercent_two_goal;
    /**
     * 第三段圆弧占比(为了动画实现,实时变化)
     */
    private float mPercent_three;
    /**
     * 第三段圆弧目标占比
     */
    private float mPercent_three_goal;

    //红色
    private int circleColor_one = Color.parseColor("#F42852");
    //青色
    private int circleColor_two = Color.parseColor("#5CD9C1");
    //蓝色
    private int circleColor_three = Color.parseColor("#0563FF");

    public CircleView(Context context) {
        super(context, null);
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * 初始化
     */
    public void initParameter(int radius,float strokeWidth,int circleColor_one,int circleColor_two) {
        this.mRadius = radius;
        this.mStrokeWidth = strokeWidth;
        this.circleColor_one = circleColor_one;
        this.circleColor_two = circleColor_two;
    }

    private void init() {
        paint_circle_one = new Paint();
        paint_circle_one.setColor(circleColor_one);
        paint_circle_one.setStyle(Paint.Style.STROKE);
        paint_circle_one.setStrokeWidth(mStrokeWidth);
        paint_circle_one.setAntiAlias(true);
        paint_circle_one.setStrokeCap(Paint.Cap.ROUND);

        paint_circle_two = new Paint();
        paint_circle_two.setColor(circleColor_two);
        paint_circle_two.setStyle(Paint.Style.STROKE);
        paint_circle_two.setStrokeWidth(mStrokeWidth);
        paint_circle_two.setAntiAlias(true);
        paint_circle_two.setStrokeCap(Paint.Cap.ROUND);

        paint_circle_three = new Paint();
        paint_circle_three.setColor(circleColor_three);
        paint_circle_three.setStyle(Paint.Style.STROKE);
        paint_circle_three.setStrokeWidth(mStrokeWidth);
        paint_circle_three.setAntiAlias(true);
        paint_circle_three.setStrokeCap(Paint.Cap.ROUND);

        textPaint1 = new Paint();
        textPaint1.setColor(Color.parseColor("#AAAFB7"));
        textPaint1.setStyle(Paint.Style.STROKE);
        textPaint1.setTextSize(35);
        textPaint1.setAntiAlias(true);

        textPaint2 = new Paint();
        textPaint2.setColor(Color.parseColor("#333333"));
        textPaint2.setStyle(Paint.Style.STROKE);
        textPaint2.setTextSize(43);
        textPaint2.setAntiAlias(true);

        mRectF = new RectF();
        mTextBound = new Rect();

        //因为圆弧末端的半圆是追加上去的,计算出追加上去的半圆所对应的角度,这里为图简便将间隔的角度 intervalAngle 也赋值为因为追加半圆而增加的角度
        //追加上去的圆弧的长度近似等于圆环宽度的一半
        intervalAngle = (int) ((int) (mStrokeWidth / 2 * 360) / (2 * Math.PI * mRadius));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureDimension(widthMeasureSpec), measureDimension(heightMeasureSpec));
    }

    private int measureDimension(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            //精确地，代表宽高为定值或者match_parent时
            result = specSize;
        } else {
            result = 2 * mRadius;
            if (specMode == MeasureSpec.AT_MOST) {
                //最大地，代表宽高为wrap_content时
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //圆环外切矩形
        mRectF.set((float) getWidth() / 2 - mRadius + mStrokeWidth / 2,
                (float) getHeight() / 2 - mRadius + mStrokeWidth / 2,
                (float) getWidth() / 2 + mRadius - mStrokeWidth / 2,
                (float) getHeight() / 2 + mRadius - mStrokeWidth / 2);

        if (mPercent_one_goal == 0) {
            //数据1 为空
            canvas.drawArc(mRectF,0,360 * mPercent_two,false, paint_circle_two);
        } else if (mPercent_one_goal == 1) {
            //数据2 为空
            canvas.drawArc(mRectF,0,360 * mPercent_one,false, paint_circle_one);
        } else {
            //绘制第一段圆弧
            canvas.drawArc(mRectF, (float) (270 + 1.5 * intervalAngle), mPercent_one * 360 - 3 * intervalAngle > 0 ? mPercent_one * 360 - 3 * intervalAngle : 0, false, paint_circle_one);
            if (mPercent_one == mPercent_one_goal) {
                //绘制第二段圆弧
                canvas.drawArc(mRectF, (float) (270 + 1.5 * intervalAngle + mPercent_one * 360),
                        360 * mPercent_two - 3 * intervalAngle > 0 ? 360 * mPercent_two - 3 * intervalAngle : 0,
                        false, paint_circle_two);
            }
            //绘制第三段圆弧
            if (mPercent_two == mPercent_two_goal) {
                canvas.drawArc(mRectF, (float) (270 + 1.5 * intervalAngle + (mPercent_one + mPercent_two) * 360),
//                        360 * mPercent_three - 3 * intervalAngle > 0 ? 360 * mPercent_three - 3 * intervalAngle : 0,
                        0.1f,
                        false, paint_circle_three);
            }
        }
        //绘制文本
        String text1 = "总次数";
        String text2 = "100";
        //计算文本宽高
        textPaint1.getTextBounds(text1, 0, String.valueOf(text1).length(), mTextBound);
        canvas.drawText(text1, getWidth() / 2 - mTextBound.width() / 2, getHeight() / 2 - 10, textPaint1);

        textPaint2.getTextBounds(text2, 0, String.valueOf(text2).length(), mTextBound);
        canvas.drawText(text2, getWidth() / 2 - mTextBound.width() / 2, getHeight() / 2 + mTextBound.height() + 10, textPaint2);
    }

    /**
     * 设置数据
     * @param data1
     * @param data2
     */
    public void setData(float data1, float data2,float data3) {
        if (data1 == 0 && data2 == 0) {
            return;
        }
        if (data1 < 0 || data2 < 0) {
            return;
        }

        //第一段圆弧目标占比
        mPercent_one_goal = data1 / (data1 + data2 + data3);

        if (mPercent_one_goal > 0 && mPercent_one_goal < 0.1) {
            mPercent_one_goal = 0.1f;
        } else if (mPercent_one_goal > 0.9 && mPercent_one_goal < 1) {
            mPercent_one_goal = 0.9f;
        }

        //第一段圆弧动画值
        float animatorValue = 0;
        if (mPercent_one_goal == 0 || mPercent_one_goal == 1) {
            animatorValue = 1;
        }else{
            animatorValue = mPercent_one_goal;
        }


        //第二段圆弧目标占比
//        mPercent_two_goal = 1 - mPercent_one_goal;
        mPercent_two_goal = data2 / (data1 + data2 + data3);

        if (mPercent_two_goal > 0 && mPercent_two_goal < 0.1) {
            mPercent_two_goal = 0.1f;
        } else if (mPercent_two_goal > 0.9 && mPercent_two_goal < 1) {
            mPercent_two_goal = 0.9f;
        }

        //第二段圆弧动画值
        float animatorValue_two = 0;
        if (mPercent_two_goal == 0 || mPercent_two_goal == 1) {
            animatorValue_two = 1;
        }else{
            animatorValue_two = mPercent_two_goal;
        }

        //第三段圆弧目标占比
        mPercent_three_goal = 1 - mPercent_one_goal - mPercent_two_goal;

        if (mPercent_three_goal > 0 && mPercent_three_goal < 0.1) {
            mPercent_three_goal = 0.1f;
        } else if (mPercent_three_goal > 0.9 && mPercent_three_goal < 1) {
            mPercent_three_goal = 0.9f;
        }

        //第三段圆弧动画值
        float animatorValue_three = 0;
        if (mPercent_three_goal == 0 || mPercent_three_goal == 1) {
            animatorValue_three = 1;
        }else{
            animatorValue_three = mPercent_three_goal;
        }

        //第三部分的动画
        final ValueAnimator animator_three = ValueAnimator.ofFloat(0, animatorValue_three);
        animator_three.setDuration(1000);
        animator_three.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mPercent_three = (float)animation.getAnimatedValue();
                invalidate();
            }
        });

        //第二部分的动画
        final ValueAnimator animator = ValueAnimator.ofFloat(0, animatorValue_two);
        animator.setDuration(1000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mPercent_two = (float)animation.getAnimatedValue();
                invalidate();
                if (mPercent_two == mPercent_two_goal) {
                    animator_three.start();
                }
            }
        });

        //第一段圆弧的动画
        ValueAnimator animator_one = ValueAnimator.ofFloat(0, animatorValue);
        animator_one.setDuration(1000);
        animator_one.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mPercent_one = (float) animation.getAnimatedValue();
                invalidate();
                if (mPercent_one == mPercent_one_goal) {
                    animator.start();
                }
            }
        });
        animator_one.start();


    }
}
