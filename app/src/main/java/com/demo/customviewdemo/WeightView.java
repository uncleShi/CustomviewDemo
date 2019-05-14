package com.demo.customviewdemo;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Author: shihao
 * Date: 2019/5/9
 * Describe: 一个圆圈表示,最大体重值为120kg,刻度均匀
 */
public class WeightView extends LinearLayout {
    private Context mContext;
    private ImageView indexView;
    /**
     * 能表示的最大体重值为120kg
     */
    private final int MAX_WEIGHT = 120;
    private int intervalAngle;


    public WeightView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;

        LayoutInflater.from(context).inflate(R.layout.view_weight,this,true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        indexView = findViewById(R.id.indexview);

        //最大体重值为120kg,每一千克对应的角度为3度
        intervalAngle = 360 / MAX_WEIGHT;
    }

    /**
     *
     * @param weight
     */
    public void setData(float weight) {
        final ObjectAnimator animator = ObjectAnimator.ofFloat(indexView,"rotation",90f,90 + weight * intervalAngle);
        animator.setDuration(5000);

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animator.cancel();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        animator.start();
    }
}
