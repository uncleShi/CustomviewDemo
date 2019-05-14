package com.demo.customviewdemo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

/**
 * Author: shihao
 * Date: 2019/5/9
 * Describe: 生理参数--睡眠,深睡比例示意图
 */
public class DeepSleepView extends View {
    /**
     * 圆环半径
     */
    private int mRadius = 160;
    /**
     * 背景圆环画笔
     */
    private Paint bgPaint;
    /**
     * 实际圆环画笔
     */
    private Paint circlePaint;
    /**
     * 圆环的宽度
     */
    private int mStrokeWidth = 20;
    /**
     * 圆环画笔的默认颜色
     */
    private int circleColor = Color.parseColor("#13D0AC");
    /**
     * 内容文字画笔
     */
    private Paint textPaint;
    /**
     * 单位文字画笔
     */
    private Paint unitPaint;
    /**
     * 文字的默认颜色
     */
    private int textColor = Color.parseColor("#13D0AC");
    //外切圆环的矩形
    private RectF mRectF;
    //内容文本的范围矩形
    private Rect mContentTextBound;
    //默认的文本内容
    private String textValue = "0";
    //文本文字大小
    private int textSize = 45;
    //单位文字大小
    private int unitSize = 30;
    /**
     * 实际圆环所占比例
     */
    private float mPercent = 0;


    public DeepSleepView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(circleColor);
        bgPaint.setStyle(Paint.Style.STROKE);
        bgPaint.setStrokeWidth(3);
        bgPaint.setStrokeCap(Paint.Cap.ROUND);

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(circleColor);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(mStrokeWidth);
        circlePaint.setStrokeCap(Paint.Cap.ROUND);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(textColor);
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setTextSize(textSize);

        unitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        unitPaint.setColor(textColor);
        unitPaint.setStyle(Paint.Style.STROKE);
        unitPaint.setTextSize(unitSize);

        mRectF = new RectF();
        mContentTextBound = new Rect();
    }

    /**
     * 设置圆环的参数
     * @param radius 圆环半径(外圆)
     * @param circleColor 圆环颜色
     * @param strokeWidth 圆环宽度
     */
    public void initParameter(int radius,int circleColor,int strokeWidth,int textSize,int textColor) {
        this.mRadius = DensityUtils.dp2px(getContext(),radius);
        this.circleColor = circleColor;
        this.mStrokeWidth = DensityUtils.dp2px(getContext(),strokeWidth);
        this.textSize = DensityUtils.sp2px(getContext(),textSize);
        this.textColor = textColor;

        circlePaint.setColor(circleColor);
        circlePaint.setStrokeWidth(this.mStrokeWidth);

        bgPaint.setColor(circleColor);

        textPaint.setColor(textColor);
        textPaint.setTextSize(this.textSize);

        unitPaint.setColor(textColor);
        unitPaint.setTextSize((int) (this.textSize * 0.7));
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
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //圆环外切矩形
        mRectF.set((float) getWidth() / 2 - mRadius + mStrokeWidth / 2,
                (float) getHeight() / 2 - mRadius + mStrokeWidth / 2,
                (float) getWidth() / 2 + mRadius - mStrokeWidth / 2,
                (float) getHeight() / 2 + mRadius - mStrokeWidth / 2);

        //画背景圆环
        canvas.drawArc(mRectF, 0, 360, false, bgPaint);

        //画实际圆环
        canvas.drawArc(mRectF, -90, 360 * mPercent, false, circlePaint);

        //绘制文本
        textPaint.getTextBounds(textValue, 0, String.valueOf(textValue).length(), mContentTextBound);
        canvas.drawText(textValue, getWidth() / 2 - mContentTextBound.width() / 2, getHeight() / 2 + mContentTextBound.height() / 2, textPaint);

        //绘制单位符号
        canvas.drawText("%", getWidth() / 2 + mContentTextBound.width() / 2 + 10, getHeight() / 2 + mContentTextBound.height() / 2, unitPaint);
    }

    /**
     * 设置比例
     *
     * @param percent
     */
    public void setData(float percent) {
        ValueAnimator animator = ValueAnimator.ofFloat(0, percent);
        animator.setDuration(5000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mPercent = (float) animation.getAnimatedValue();
                textValue = (int) (mPercent * 100) + "";
                invalidate();
            }
        });
        animator.start();
    }
}
