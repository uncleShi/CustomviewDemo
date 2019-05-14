package com.demo.customviewdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

/**
 * Author: shihao
 * Date: 2019/4/30
 * Describe:从-90度开始的顺时针绘制的不同颜色的状态表示圆环(最多绘制三种颜色)
 */
public class StateCircleView extends View {

    /**
     * 圆环宽度,默认20
     */
    private float mStrokeWidth = 20.0f;
    /**
     * 默认圆环半径
     */
    private int mRadius = 140;

    //圆环画笔
    private Paint paint_circle;

    //圆环中心线以上文字画笔
    private Paint textPaint1;
    //圆环中心线以下文字画笔
    private Paint textPaint2;
    //外切圆环的矩形
    private RectF mRectF;
    //文本的范围矩形
    private Rect mTextBound;

    //为了视觉效果而产生的间隔角度
    private float intervalAngle;

    private String textAbove;
    private int textColor_above = Color.parseColor("#AAAFB7");
    private int textSize_above = 35;
    private String textBelow;
    private int textColor_below = Color.parseColor("#333333");
    private int textSize_below = 43;

    /**
     * 圆环颜色集合
     */
    private ArrayList<Integer> colors = new ArrayList<>();

    private float mPercent;

    private ArrayList<Float> mPercents = new ArrayList<>();
    //圆环最小占比
    private float minPercent;

    public StateCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * 初始化
     */
    public void initParameter(int radius,float strokeWidth,String textAbove,int textColor_above,int textSize_above,String textBelow,int textColor_below,int textSize_below) {
        this.mRadius = radius;
        this.mStrokeWidth = strokeWidth;
        this.textAbove = textAbove;
        this.textColor_above = textColor_above;
        this.textSize_above = textSize_above;
        this.textBelow = textBelow;
        this.textColor_below = textColor_below;
        this.textSize_below = textSize_below;
    }

    private void init() {
        paint_circle = new Paint();
        paint_circle.setStyle(Paint.Style.STROKE);
        paint_circle.setStrokeWidth(mStrokeWidth);
        paint_circle.setAntiAlias(true);
        paint_circle.setStrokeCap(Paint.Cap.ROUND);

        textPaint1 = new Paint();
        textPaint1.setColor(textColor_above);
        textPaint1.setStyle(Paint.Style.STROKE);
        textPaint1.setTextSize(textSize_above);
        textPaint1.setAntiAlias(true);

        textPaint2 = new Paint();
        textPaint2.setColor(textColor_below);
        textPaint2.setStyle(Paint.Style.STROKE);
        textPaint2.setTextSize(textSize_below);
        textPaint2.setAntiAlias(true);

        mRectF = new RectF();
        mTextBound = new Rect();

        //因为圆弧末端的半圆是追加上去的,计算出追加上去的半圆所对应的角度,这里为图简便将间隔的角度 intervalAngle 也赋值为因为追加半圆而增加的角度
        //追加上去的圆弧的长度近似等于圆环宽度的一半
        intervalAngle = FloatCalculator.divide(mStrokeWidth / 2 * 360, (float) (2 * Math.PI * mRadius));

        //三个间隔角度对应的圆环占比,即表现在圆环上的数据的最小占比
        minPercent = FloatCalculator.divide(intervalAngle * 3,360);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureDimension(widthMeasureSpec), measureDimension(heightMeasureSpec));
    }

    private int measureDimension(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode( measureSpec);
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

        float percentSum = 0;
        for (int i = 0; i < colors.size(); i++) {
            paint_circle.setColor(colors.get(i));

            mPercent = mPercents.get(i);

            if (mPercent == 0) {
                //占比为0,不进行绘制
                continue;
            }

            if (mPercent == 1) {
                //占比为1,直接绘制一个圆环,退出循环
                canvas.drawArc(mRectF,0,360,false, paint_circle);
                break;
            }

            canvas.drawArc(mRectF,
                    270 + FloatCalculator.add(FloatCalculator.multiply(1.5f,intervalAngle),FloatCalculator.multiply(360,percentSum)),
                    FloatCalculator.subtract(FloatCalculator.multiply(360,mPercent),FloatCalculator.multiply(3,intervalAngle)),
                    false,
                    paint_circle);

            percentSum = FloatCalculator.add(percentSum,mPercent);
        }

        //绘制文本
        //计算文本宽高
        if (!TextUtils.isEmpty(textAbove)) {
            textPaint1.getTextBounds(textAbove, 0, String.valueOf(textAbove).length(), mTextBound);
            canvas.drawText(textAbove, getWidth() / 2 - mTextBound.width() / 2, getHeight() / 2 - 10, textPaint1);
        }

        if (!TextUtils.isEmpty(textBelow)) {
            textPaint2.getTextBounds(textBelow, 0, String.valueOf(textBelow).length(), mTextBound);
            canvas.drawText(textBelow, getWidth() / 2 - mTextBound.width() / 2, getHeight() / 2 + mTextBound.height() + 10, textPaint2);
        }
    }

    /**
     * 设置值
     * @param datas   数据源,希望绘制几种圆环,就传入几个数据
     * @param colors  颜色源,与数据源的数量要一一对应
     */
    public void setData(ArrayList<Float> datas,ArrayList<Integer> colors) {
        if (datas.isEmpty() || colors.isEmpty()) {
            return;
        }

        if (datas.size() != colors.size()) {
            return;
        }

        this.colors = colors;

        //计算出总数
        float sumData = 0;
        for (Float data: datas) {
            sumData += data;
        }

        //将除了最后一个数据的占比放入集合中
        mPercents.clear();
        for (int i = 0; i < datas.size(); i++) {
            if (i != datas.size() - 1) {
                Float percent = FloatCalculator.divide(datas.get(i),sumData);
                //如果小于最小占比
                if (percent <= minPercent && percent > 0) {
                    //多赋值0.36度
                    percent = FloatCalculator.add(minPercent,0.001f);
                }
                mPercents.add(percent);
            }
        }

        //这是为了保证占比的总和 ,总为1
        float percentSum = 0;
        for (Float data : mPercents) {
            percentSum = FloatCalculator.add(percentSum,data);
        }

        mPercents.add(FloatCalculator.subtract(1,percentSum));

        invalidate();
    }

}
