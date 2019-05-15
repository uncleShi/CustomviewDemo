package com.demo.customviewdemo.gram;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.demo.customviewdemo.DensityUtils;
import com.demo.customviewdemo.R;
import com.demo.customviewdemo.bean.HistogramBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: shihao
 * Date: 2019/5/10
 * Describe: 直方图
 */
public class HistogramView extends View {
    /**
     * 直方图线的画笔
     */
    private Paint paint_line;
    /**
     * 坐标系的画笔
     */
    private Paint paint_xy;
    /**
     * 坐标系刻度文字的画笔
     */
    private Paint paint_scale;
    /**
     * 虚线的画笔
     */
    private Paint paint_dash;
    /**
     * 直方图线的颜色
     */
    private int color_line = getContext().getResources().getColor(R.color.hehe);
    /**
     * 坐标系线的颜色
     */
    private int color_xy = getContext().getResources().getColor(R.color.black);
    /**
     * 坐标系刻度文字的颜色
     */
    private int color_scale = getContext().getResources().getColor(R.color.black);
    //直方图的宽度
    private int mStrokeWidth = DensityUtils.dp2px(getContext(), 50);
    //直方图之间的间隔大小
    private int intervalWidth = DensityUtils.dp2px(getContext(), 35);
    //Y轴等分数量
    private int y_divide_number;
    //X轴等分数量
    private int x_divide_number = 7;
    /**
     * Y轴的最大刻度
     */
    private float Y_MAX_SCALE = 3000;
    /**
     * Y轴的最小刻度值
     */
    private float Y_MIN_SCALE = 250;
    //文本的范围矩形
    private Rect mTextBound;
    //数据源
    ArrayList<HistogramBean> mDatas = new ArrayList<>();
    //Y轴刻度值所占的宽度
    private int y_scale_width;
    //X轴刻度值所占的高度
    private int x_scale_height;
    /**
     * y轴刻度单位
     */
    private String y_unit = "步";
    //整个视图的宽高
    private int width;
    private int height;
    //X轴上第一个坐标点的初始坐标
    private float xInit;
    //X轴上第一个坐标点的最小值
    private float minXInit;
    //X轴上第一个坐标点的最大值
    private float maxXInit;
    //原点坐标
    private int xOri;
    private int yOri;
    private Context mContext;
    /**
     * 被点击选中的直方图的位置
     */
    private int selectedX = -1;

    public HistogramView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        paint_line = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint_line.setColor(color_line);
        paint_line.setStyle(Paint.Style.FILL);
        paint_line.setStrokeWidth(mStrokeWidth);

        paint_xy = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint_xy.setColor(color_xy);
        paint_xy.setStyle(Paint.Style.FILL);
        paint_xy.setStrokeWidth(1);

        paint_scale = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint_scale.setColor(Color.parseColor("#333333"));
        paint_scale.setStyle(Paint.Style.FILL);
        paint_scale.setTextSize(DensityUtils.sp2px(mContext, 12));

        paint_dash = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint_dash.setColor(color_xy);
        paint_dash.setStyle(Paint.Style.STROKE);
        paint_dash.setStrokeWidth(1);
        //画虚线的方式,表示画20像素的实线,留10像素的空白,以此往复
        paint_dash.setPathEffect(new DashPathEffect(new float[]{20, 10}, 0));

        //Y轴等分数量
        y_divide_number = (int) (Y_MAX_SCALE / Y_MIN_SCALE);

        //计算出Y轴左边的刻度文本所占的宽度
        mTextBound = new Rect();
        paint_scale.getTextBounds(Y_MAX_SCALE + "", 0, String.valueOf(Y_MAX_SCALE + "").length(), mTextBound);
        y_scale_width = mTextBound.width();
        x_scale_height = mTextBound.height();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed) {
            //得到整个视图的宽高
            width = getWidth();
            height = getHeight();

            //确定原点坐标以及第一个点的坐标,以及第一个点的最大,最小值
            xOri = DensityUtils.dp2px(mContext, 2) * 2 + y_scale_width;
            yOri = height - DensityUtils.dp2px(mContext, 2) * 2 - x_scale_height;

            //确定第一个点的初始坐标,可能的最大及最小坐标点的位置
            //原点坐标
            xInit = xOri + intervalWidth + mStrokeWidth / 2;
            //减去的这0.1,是因为X轴上的最后一个刻度与最右端的距离是整个X轴长度的0.1
            minXInit = width - (width - xOri) * 0.1f - (mDatas.size() - 1) * (intervalWidth + mStrokeWidth) - mStrokeWidth / 2;
            maxXInit = xInit;
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        drawXy(canvas);
        drawHistogram(canvas);
    }

    /**
     * 绘制直方图
     *
     * @param canvas
     */
    private void drawHistogram(Canvas canvas) {
        //画直方图
        for (int i = 0; i < mDatas.size(); i++) {
            //X坐标点大于0的才绘制
            float x = xInit + i * (intervalWidth + mStrokeWidth);
            if (x < xOri + mStrokeWidth / 2) {
                continue;
            }

            canvas.drawLine(xInit + i * (intervalWidth + mStrokeWidth),
                    yOri * 0.1f + yOri * 0.9f * (1 - mDatas.get(i).percent),
                    xInit + i * (intervalWidth + mStrokeWidth),
                    yOri - 1,
                    paint_line);

            //画直方图上方的值
            if (i == selectedX) {
                String value = "100";
                paint_scale.getTextBounds(value, 0, String.valueOf(value).length(), mTextBound);
                canvas.drawText(value,
                        xInit + i * (intervalWidth + mStrokeWidth) - mTextBound.width() / 2,
                        yOri * 0.1f + yOri * 0.9f * (1 - mDatas.get(i).percent) - 10,
                        paint_scale);
            }

            //画X轴上的刻度值
            String value = 2000 + "";
            paint_scale.getTextBounds(value, 0, String.valueOf(value).length(), mTextBound);
            canvas.drawText(value,
                    xInit + i * (intervalWidth + mStrokeWidth) - mTextBound.width() / 2,
                    yOri + DensityUtils.dp2px(mContext, 2) + mTextBound.height(),
                    paint_scale);
        }
    }

    /**
     * 绘制坐标轴以及虚线,刻度等
     *
     * @param canvas
     */
    private void drawXy(Canvas canvas) {
        //绘制X轴
        canvas.drawLine(xOri, yOri, width, yOri, paint_xy);
        //绘制Y轴
//        canvas.drawLine(xOri, 0, xOri, yOri, paint_xy);

        //绘制原点的表示文字
        String text = "0";
        paint_scale.getTextBounds(text, 0, String.valueOf(text).length(), mTextBound);
        canvas.drawText("0",
                xOri - DensityUtils.dp2px(mContext, 2) - mTextBound.width(),
                yOri + mTextBound.height() / 2,
                paint_scale);

        //画Y轴的虚线,等分为 y_divide_number 份
        //每一份占的高度为
        float itemHeight = yOri * 0.9f / y_divide_number;
        for (int i = 1; i <= y_divide_number; i++) {
            //绘制虚线
            canvas.drawLine(xOri, yOri - i * itemHeight, width, yOri - i * itemHeight, paint_dash);
            //绘制Y轴上的刻度值,每两个刻度绘制一个值
            if (i % 2 == 0) {
                String value = Y_MIN_SCALE * i + "";
                paint_scale.getTextBounds(value, 0, String.valueOf(value).length(), mTextBound);
                canvas.drawText(value,
                        xOri - DensityUtils.dp2px(mContext, 2) - mTextBound.width(),
                        yOri - i * itemHeight + mTextBound.height() / 2,
                        paint_scale);
            }
        }
    }

    private float startX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                //当屏幕的宽度不足以展示全部数据时
                if ((intervalWidth + mStrokeWidth) * mDatas.size() > width - xOri) {
                    float distance = event.getX() - startX;
                    startX = event.getX();
                    if (xInit + distance > maxXInit) {
                        xInit = maxXInit;
                    } else if (xInit + distance < minXInit) {
                        xInit = minXInit;
                    } else {
                        xInit = xInit + distance;
                    }
                    //重绘
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                clickAction(event);
                break;
        }
        return true;
    }

    /**
     * 点击事件
     * @param event
     */
    private void clickAction(MotionEvent event) {
        float x = event.getX();
        for (int i = 0; i < mDatas.size(); i++) {
            float goalX = xInit + i * (intervalWidth + mStrokeWidth);
            if (x >= goalX - mStrokeWidth / 2 && x <= goalX + mStrokeWidth / 2) {
                selectedX = i;
                break;
            }
        }
        invalidate();
    }

    public void setData(ArrayList<Float> datas) {
        mDatas.clear();

        for (int i = 0; i < datas.size(); i++) {
            HistogramBean bean = new HistogramBean();
            bean.percent = datas.get(i) / 3000;
            mDatas.add(bean);
        }

        invalidate();
    }
}
