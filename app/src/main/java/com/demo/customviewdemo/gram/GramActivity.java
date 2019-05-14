package com.demo.customviewdemo.gram;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.demo.customviewdemo.R;

import java.util.ArrayList;

public class GramActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gram);
        HistogramView histogramView = findViewById(R.id.histogramview);
        ArrayList<Float> list = new ArrayList<>();
        list.add(400.0f);
        list.add(3000.0f);
        list.add(2600.0f);
        list.add(2500.0f);
        list.add(400.0f);
        list.add(300.0f);
        list.add(800.0f);
        list.add(800.0f);
        list.add(540.0f);
        list.add(890.0f);
        list.add(1230.0f);
        list.add(1560.0f);
        list.add(1800.0f);
        list.add(1700.0f);
        histogramView.setData(list);
    }
}
