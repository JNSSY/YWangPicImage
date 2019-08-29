package com.wy.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ImagePreviewActivity extends AppCompatActivity {

    private ArrayList<ImageItem> data;
    private List<View> views;
    private int position;
    private TextView current_index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img_preview);


        initData();
        initView();

    }

    private void initData() {
        views = new ArrayList<>();
        data = (ArrayList<ImageItem>) getIntent().getSerializableExtra("data");
        position = getIntent().getIntExtra("position", 0);
        if (data != null) {
            for (int i = 0; i < data.size(); i++) {

                ImageView imageView = new ImageView(this);
                Bitmap bitmap = BitmapFactory.decodeFile(data.get(i).getPath());
                imageView.setImageBitmap(bitmap);
                views.add(imageView);
            }
        }

    }

    private void initView() {
        current_index = findViewById(R.id.current_index);
        TextView total_index = findViewById(R.id.total_index);

        current_index.setText(String.valueOf(position + 1));
        total_index.setText(String.valueOf(data.size()));

        ViewPager vp = findViewById(R.id.vp);
        vp.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return data.size();
            }


            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(views.get(position));
                return views.get(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(views.get(position));
            }

            @Override
            public boolean isViewFromObject(View view, Object o) {
                return view == o;
            }
        });

        vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                current_index.setText(String.valueOf(i + 1));
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        vp.setCurrentItem(position);
    }
}
