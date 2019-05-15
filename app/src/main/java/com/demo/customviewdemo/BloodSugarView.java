package com.demo.customviewdemo;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Author: shihao
 * Date: 2019/5/9
 * Describe: 生理参数--血糖组件    空腹：偏低<3.9<=正常<=6.1<偏高   饭后：偏低<=4<正常<=7.8<偏高
 */
public class BloodSugarView extends LinearLayout {

    private ImageView bgView;
    private ImageView indexView;
    private TextView tv;
    /**
     * 空腹状态
     */
    public static final int TYPE_EMPTY = 1;
    /**
     * 饭后状态
     */
    public static final int TYPE_AFTER_MEAL = 2;
    private float emptyValue_low;
    private float emptyValue_normal;
    private float emptyValue_high;
    private float after_low;
    private float after_normal;
    private float after_high;
    private float angle;

    public enum BloodSugarType {
        //饭前和饭后两种状态
        EMPTY, AFTER_MEAL;
    }

    public BloodSugarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_blood_sugar, this, true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        bgView = findViewById(R.id.bg_iv);
        indexView = findViewById(R.id.index_iv);
        tv = findViewById(R.id.tv);

        /**
         * 空腹：偏低<3.9<=正常<=6.1<偏高   饭后：偏低<=4<正常<=7.8<偏高
         * 因为有两种标准,所以对应的有两种刻度,一种是空腹状态下的刻度,一种是饭后状态下的刻度
         * 将一个圆分为3部分,从-90度的地方开始,一个45度角代表的是偏低状态,一个180度角代表的是正常状态,剩下的角代表的是偏高状态
         */
        //空腹状态下,单位值代表的角度
        emptyValue_low = FloatCalculator.divide(45, 3.9f);
        emptyValue_normal = FloatCalculator.divide(180, 2.2f);
        //最高表示8.5
        emptyValue_high = FloatCalculator.divide(135, 2.4f);


        //饭后状态下,单位值代表的角度
        after_low = FloatCalculator.divide(45, 4.0f);
        after_normal = FloatCalculator.divide(180, 3.8f);
        //最高表示10
        after_high = FloatCalculator.divide(135, 2.2f);


        setData(BloodSugarType.AFTER_MEAL, 7.0f);
    }

    public void setData(BloodSugarType type, float value) {
        float startAngle = 0f;
        float endAngle = 0f;

        //是否是偏低
        boolean isLow = false;

        //首先确定是哪种状态
        if (type == BloodSugarType.EMPTY) {
            //空腹状态,偏低<3.9<=正常<=6.1<偏高
            if (value < 3.9) {
                //偏低
                setViewAndText(1);
                angle = FloatCalculator.multiply(value, emptyValue_low);
                isLow = true;
            } else if (value <= 6.1) {
                //正常
                setViewAndText(2);
                angle = FloatCalculator.multiply(FloatCalculator.subtract(value,3.9f), emptyValue_normal);
                isLow = false;
            } else {
                //偏高
                setViewAndText(3);
                angle = FloatCalculator.multiply(FloatCalculator.subtract(value,6.1f), emptyValue_high);
                if (angle > 135) {
                    angle = 135;
                }
                isLow = false;
            }
        } else if (type == BloodSugarType.AFTER_MEAL) {
            //饭后状态,偏低<=4<正常<=7.8<偏高
            if (value < 4) {
                //偏低
                setViewAndText(1);
                angle = FloatCalculator.multiply(value, after_low);
                isLow = true;
            } else if (value <= 7.8) {
                //正常
                setViewAndText(2);
                angle = FloatCalculator.multiply(FloatCalculator.subtract(value,4.0f), after_normal);
                isLow = false;
            } else {
                //偏高
                setViewAndText(3);
                angle = FloatCalculator.multiply(FloatCalculator.subtract(value,7.8f), after_high);
                if (angle > 135) {
                    angle = 135;
                }
                isLow = false;
            }
        }

        if (isLow) {
            startAngle = -45;
            endAngle = -45 + angle;
        }else{
            startAngle = 0;
            endAngle = angle;
        }

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(indexView, "rotation", startAngle,endAngle);
        objectAnimator.setDuration(100);
        objectAnimator.start();
    }

    /**
     * 改变背景图片和文字
     *
     * @param type 1 代表偏低 , 2代表正常,3代表偏高
     */
    private void setViewAndText(int type) {
        if (type == 1) {
            bgView.setImageResource(R.mipmap.bg_blood_suagr_high);
            indexView.setImageResource(R.mipmap.ic_blood_sugar_high);
            tv.setText("偏低");
        } else if (type == 2) {
            bgView.setImageResource(R.mipmap.bg_blood_sugar_normal);
            indexView.setImageResource(R.mipmap.ic_blood_sugar_normal);
            tv.setText("正常");
        } else if (type == 3) {
            bgView.setImageResource(R.mipmap.bg_blood_suagr_high);
            indexView.setImageResource(R.mipmap.ic_blood_sugar_high);
            tv.setText("偏高");
        }
    }
}
