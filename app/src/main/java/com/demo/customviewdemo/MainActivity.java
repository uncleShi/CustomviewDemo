package com.demo.customviewdemo;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.demo.customviewdemo.gram.GramActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CircleView mCircleView = findViewById(R.id.circleview);
        mCircleView.setData(1,1,5);

        StateCircleView fuckView = findViewById(R.id.fuckview);
        ArrayList<Float> datas = new ArrayList<>();
        datas.add(1.0f);
        datas.add(3.0f);
        datas.add(13.0f);
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#F42852"));
        colors.add(Color.parseColor("#5CD9C1"));
        colors.add(Color.parseColor("#0563FF"));
        fuckView.setData(datas,colors);

        WeightView weightView = findViewById(R.id.weightview);
        weightView.setData(60);

        DeepSleepView deepSleepView = findViewById(R.id.deepsleepview);
        deepSleepView.initParameter(60,getColor(R.color.colorPrimary),8,16,getColor(R.color.colorPrimaryDark));
        deepSleepView.setData(0.8f);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, GramActivity.class));
            }
        });
    }
}
