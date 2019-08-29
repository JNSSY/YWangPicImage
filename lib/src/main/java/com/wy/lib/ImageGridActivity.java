package com.wy.lib;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("StringFormatMatches")
public class ImageGridActivity extends FragmentActivity implements AndroidImagePicker.OnImageSelectedChangeListener, View.OnClickListener {

    private Fragment fragment;
    private AndroidImagePicker androidImagePicker;
    private TextView bt_ok;
    private ImageView iv_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_grid);

        bt_ok = findViewById(R.id.bt_ok);
        iv_back = findViewById(R.id.iv_back);
        bt_ok.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        androidImagePicker = AndroidImagePicker.getInstance();
        androidImagePicker.setMaxSelect(androidImagePicker.selectLimit - androidImagePicker.getSelectImageCount());
        androidImagePicker.setSelectCount(androidImagePicker.getSelectImageCount());
        fragment = new ImageGridFragment();

        getSupportFragmentManager().beginTransaction().
                replace(R.id.container, fragment).commit();




        androidImagePicker.addOnImageSelectedChangeListener(this);

    }


    @Override
    public void onImageSelectChange(int position, ImageItem item, int selectedItemsCount, int maxSelectLimit) {
        if (selectedItemsCount > 0) {
            bt_ok.setEnabled(true);
            //mBtnOk.setText("完成("+selectedItemsCount+"/"+maxSelectLimit+")");
            bt_ok.setTextColor(getResources().getColor(R.color.green));
            bt_ok.setText(getResources().getString(R.string.select_complete, selectedItemsCount, maxSelectLimit));
        } else {
            bt_ok.setTextColor(getResources().getColor(R.color.green_dark));
            bt_ok.setText(getResources().getString(R.string.complete));
            bt_ok.setEnabled(false);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.bt_ok) {
            androidImagePicker.notifyOnImagePickComplete();
            finish();
        } else if (i == R.id.iv_back) {
            finish();
        }
    }
}
