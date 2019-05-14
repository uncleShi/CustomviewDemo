package com.demo.customviewdemo.gram;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.demo.customviewdemo.DensityUtils;
import com.demo.customviewdemo.R;

/**
 * Author: shihao
 * Date: 2019/5/14
 * Describe:生理参数--血糖 点图
 */
public class PointView extends View {
    private Context mContext;
    private Paint paint_xy;
    private Paint paint_text;
    private Paint paint_point;
    private int color_xy = Color.parseColor("#333333");
    private int color_point = getContext().getResources().getColor(R.color.colorPrimaryDark);
    //整体view的宽高
    private int width;
    private int height;
    private Rect mTextBounds;
    //原点坐标
    private int xOri;
    private int yOri;
    //Y轴文字距画布左侧的距离
    private int y_text_left_interval = DensityUtils.dp2px(getContext(),19);
    //Y轴文字距Y轴的距离
    private int y_text_right_interval = DensityUtils.dp2px(getContext(),4);
    //X轴文字距画布底边的距离
    private int x_text_bottom_interval = DensityUtils.dp2px(getContext(),9);
    //Y轴上的刻度有5个,分为4段
    private int y_divide_number = 4;
    //X轴上的刻度有7个,分为6段
    private int x_divide_number = 6;

    public PointView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        paint_xy = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint_xy.setColor(color_xy);
        paint_xy.setStyle(Paint.Style.FILL);
        paint_xy.setStrokeWidth(1);

        paint_text = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint_text.setColor(color_xy);
        paint_text.setStyle(Paint.Style.FILL);
        paint_text.setTextSize(DensityUtils.sp2px(mContext,12));

        paint_point = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint_point.setColor(color_point);
        paint_point.setStyle(Paint.Style.STROKE);
        paint_point.setStrokeCap(Paint.Cap.ROUND);
        paint_point.setStrokeWidth(30);

        mTextBounds = new Rect();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed) {
            width = getWidth();
            height = getHeight();

            String value = "10.0";
            paint_text.getTextBounds(value,0,value.length(),mTextBounds);

            xOri = y_text_left_interval + mTextBounds.width() + y_text_right_interval;
            yOri = height - x_text_bottom_interval - y_text_right_interval - mTextBounds.height();

        }
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawXY(canvas);
        drawPoints(canvas);
    }

    /**
     * 绘制点
     * @param canvas
     */
    private void drawPoints(Canvas canvas) {
        canvas.drawPoint(xOri + 100,yOri - 100,paint_point);
    }

    /**
     * 绘制坐标轴
     * @param canvas
     */
    private void drawXY(Canvas canvas) {
        //绘制X轴
        canvas.drawLine(xOri,yOri,width - DensityUtils.dp2px(mContext,8),yOri,paint_xy);
        //绘制X轴上的刻度值
        float itemWidth = (width - DensityUtils.dp2px(mContext,8) - xOri - DensityUtils.dp2px(mContext,20)) / x_divide_number;
        for (int i = 0; i <= x_divide_number; i++) {
            String value = 6 + i + "";
            paint_text.getTextBounds(value,0,value.length(),mTextBounds);
            canvas.drawText(value,
                    xOri + itemWidth * i - mTextBounds.width() / 2,
                    yOri + y_text_right_interval + mTextBounds.height(),
                    paint_text);
        }

        //绘制Y轴
        canvas.drawLine(xOri,DensityUtils.dp2px(mContext,40),xOri,yOri,paint_xy);
        //绘制Y轴上的刻度
        //每段刻度对应的高度
        float itemHeight = (yOri - DensityUtils.dp2px(mContext,40) - 30) / y_divide_number;

        for (int i = 1; i <= y_divide_number; i++) {
            String value = 2.5f * i + "";
            paint_text.getTextBounds(value,0,value.length(),mTextBounds);
            canvas.drawText(value,
                    xOri - y_text_right_interval - mTextBounds.width(),
                    yOri - i * itemHeight + mTextBounds.height() / 2,
                    paint_text);
        }

        
    }

}
