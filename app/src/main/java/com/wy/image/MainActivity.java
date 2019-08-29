package com.wy.image;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements OnItemClickListener {

    private RecyclerView rv;
    private ImgShowAdapter imgShowAdapter;
    private AndroidImagePicker imagePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imagePicker = AndroidImagePicker.getInstance();
        rv = findViewById(R.id.rv);

        checkPerm();

        initAdapter();

    }

    private void checkPerm() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) { //权限没有被授予
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 001);
        }
    }

    private void initAdapter() {
        imgShowAdapter = new ImgShowAdapter();
        rv.setLayoutManager(new GridLayoutManager(this, 3));
        rv.addItemDecoration(new DividerItemDecoration());
        rv.setAdapter(imgShowAdapter);

        imgShowAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 001) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    protected void onDestroy() {
        imagePicker.clearSelectedImages();
        super.onDestroy();
    }

    @Override
    public void onClick(View view, int position) {
        CharSequence description = view.findViewById(R.id.iv_item).getContentDescription();
        if (description != null && "add".equals(description.toString())) {
            imagePicker.pickMulti(MainActivity.this, false, new AndroidImagePicker.OnImagePickCompleteListener() {
                @Override
                public void onImagePickComplete(List<ImageItem> items) {
                    if (items != null && items.size() > 0) {
                        imgShowAdapter.clearData();
                        imgShowAdapter.addData(items);
                    }
                }
            });
        } else {
            Intent intent = new Intent(this, ImagePreviewActivity.class);
            intent.putExtra("position", position);
            intent.putExtra("data", (Serializable) imagePicker.getSelectedImages());
            startActivity(intent);
        }
    }
}
