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

    public enum BloodSugarType {
        EMPTY,AFTER_MEAL;
    }

    public BloodSugarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_blood_sugar,this,true);
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
         */
        //空腹状态的下的刻度
        float emptyValue = FloatCalculator.subtract(6.1f,3.9f);
        setData(BloodSugarType.EMPTY,1.0f);
    }

    public void setData(BloodSugarType type,float value) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(indexView,"rotation",0f,270f);
        objectAnimator.setDuration(4000);
        objectAnimator.start();
    }
}
