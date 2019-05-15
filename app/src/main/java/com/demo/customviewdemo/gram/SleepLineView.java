package com.demo.customviewdemo.gram;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.demo.customviewdemo.DensityUtils;

/**
 * Author: shihao
 * Date: 2019/5/15
 * Describe: 生理参数-- 睡眠 折线图
 */
public class SleepLineView extends View {
    private Context mContext;
    //坐标轴画笔
    private Paint paint_xy;
    //文字画笔
    private Paint paint_text;
    //虚线画笔
    private Paint paint_dash;
    //折线画笔
    private Paint paint_line;
    //坐标轴线的颜色
    private int color_xy = Color.parseColor("#333333");
    //文字颜色
    private int color_text = Color.parseColor("#AAAFB7");
    //图标折线颜色
    private int color_line = Color.parseColor("#13D0AC");

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

    public SleepLineView(Context context, AttributeSet attrs) {
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
        paint_text.setColor(color_text);
        paint_text.setStyle(Paint.Style.FILL);
        paint_text.setTextSize(35);

        paint_line = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint_line.setColor(color_line);
        paint_line.setStyle(Paint.Style.FILL);
        paint_line.setStrokeWidth(1);

        paint_dash = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint_dash.setColor(color_xy);
        paint_dash.setStyle(Paint.Style.FILL);
        paint_dash.setStrokeWidth(1);
        //画虚线的方式,表示画20像素的实线,留10像素的空白,以此往复
        paint_dash.setPathEffect(new DashPathEffect(new float[]{20, 10}, 0));

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
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        drawXY(canvas);
    }

    //绘制坐标轴
    private void drawXY(Canvas canvas) {
        //绘制X轴
        canvas.drawLine(xOri,yOri,width - DensityUtils.dp2px(mContext,10),yOri,paint_xy);

        //绘制Y轴
        canvas.drawLine(xOri,DensityUtils.dp2px(mContext,10),xOri,yOri,paint_xy);

        //绘制虚线,有三段虚线,每一段虚线的高度
        float itemHeight = (yOri - DensityUtils.dp2px(mContext,10)) * 0.9f / 3;
        for (int i = 1; i <= 3; i++) {
            canvas.drawLine(xOri, yOri - i * itemHeight,
                    width - DensityUtils.dp2px(mContext,10), yOri - i * itemHeight, paint_dash);
        }
    }
}
