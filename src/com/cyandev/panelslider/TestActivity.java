package com.cyandev.panelslider;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by unixzii on 16/1/9.
 */
public class TestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test);

        PanelSlider slider = (PanelSlider) findViewById(R.id.panelSlider);
        slider.setInitialHeight(500);
        slider.setOnProgressListener(new PanelSlider.OnProgressChangeListener() {
            @Override
            public void onProgressChange(float progress) {
                final View view = findViewById(R.id.view);

                view.setScaleX(1 - progress * 0.2f);
                view.setScaleY(1 - progress * 0.2f);
                view.setPivotY(0);
                view.setRotationX(-progress * 5);
            }
        });

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setBackgroundColor(Color.WHITE);
        listView.setOnTouchListener(new View.OnTouchListener() {

            private float mLastY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mLastY = event.getY();
                    slider.disallowInterceptChildTouchEvent();
                }

                boolean cond1 = listView.getChildCount() == 0;
                if (cond1) {
                    slider.allowInterceptChildTouchEvent();
                } else {
                    boolean cond2 = listView.getFirstVisiblePosition() == 0;
                    boolean cond3 = listView.getChildAt(0).getTop() == 0;
                    boolean cond4 = mLastY - event.getY() < 0;

                    if (cond2 && cond3 && cond4) {
                        slider.allowInterceptChildTouchEvent();
                    }
                }

                mLastY = event.getY();

                return false;
            }
        });
        listView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return 50;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(TestActivity.this).inflate(android.R.layout.simple_list_item_1, parent, false);
                    ((TextView) convertView.findViewById(android.R.id.text1)).setText("---- List item ----");
                    ((TextView) convertView.findViewById(android.R.id.text1)).setTextColor(Color.DKGRAY);
                }

                return convertView;
            }
        });
    }
}
