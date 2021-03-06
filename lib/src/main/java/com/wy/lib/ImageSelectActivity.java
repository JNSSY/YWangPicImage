package com.wy.lib;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ImageSelectActivity extends BaseActivity implements OnItemClickListener, View.OnDragListener, View.OnClickListener {

    private RecyclerView rv;
    private ImgShowAdapter imgShowAdapter;
    private AndroidImagePicker imagePicker;
    private TextView tv_delete;
    private int index = -1;
    private TextView tv_submit;
    private LubanUtils lubanUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_select);

        imagePicker = AndroidImagePicker.getInstance();
        lubanUtils = new LubanUtils(this);
        rv = findViewById(R.id.rv);
        tv_submit = findViewById(R.id.tv_submit);
        tv_delete = findViewById(R.id.tv_delete);

        tv_submit.setOnClickListener(this);
        tv_delete.setOnDragListener(this);

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
        imgShowAdapter.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public void onLongClick(View v, int position) {
                index = position;
                tv_delete.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    v.startDragAndDrop(null, new View.DragShadowBuilder(v), v, View.DRAG_FLAG_GLOBAL);
                } else {
                    //View.DRAG_FLAG_OPAQUE 该属性 表示不使用半透明属性
                    v.startDrag(null, new View.DragShadowBuilder(v), v, View.DRAG_FLAG_GLOBAL);
                }
            }
        });
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
        FileUtils.clearLocalPhoto(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View view, int position) {
        CharSequence description = view.findViewById(R.id.iv_item).getContentDescription();
        if (description != null && "add".equals(description.toString())) {
            imagePicker.pickMulti(ImageSelectActivity.this, false, new AndroidImagePicker.OnImagePickCompleteListener() {
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


    @Override
    public boolean onDrag(View v, DragEvent event) {
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED: //开始拖动
                tv_delete.setText("拖动到此处删除");
                tv_delete.setBackgroundColor(getResources().getColor(R.color.red));
                break;
            case DragEvent.ACTION_DRAG_EXITED://拖动的View从TextView上移除
                tv_delete.setText("拖动到此处删除");
                break;
            case DragEvent.ACTION_DRAG_ENTERED: // 拖动的View进入到的TextView上
                tv_delete.setText("松手即可删除");
                tv_delete.setBackgroundColor(getResources().getColor(R.color.red_press));
                break;
            case DragEvent.ACTION_DROP:// 在TextView上释放操作
                imgShowAdapter.deleteData(index);
                ImageItem item = imagePicker.getSelectedImages().get(index);
                imagePicker.deleteSelectedImageItem(index, item);
                Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
                break;
            case DragEvent.ACTION_DRAG_ENDED://结束拖动事件
                tv_delete.setBackgroundColor(getResources().getColor(R.color.red));
                tv_delete.setVisibility(View.GONE);
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {

        compressFiles();

        lubanUtils.getCompressedFiles(new LubanInterfaces() {
            @Override
            public void getFiles(List<File> files) {
//                for (File file : files) {
//                    Log.e("wy", file.length() / 1024 + " KB");
//                }
                List<String> base64List = lubanUtils.getImageBase64List(files);
                Log.e("wy", "b64 " + base64List.size());
            }
        });

    }

    private void compressFiles() {
        List<ImageItem> selectedImages = imagePicker.getSelectedImages();
        List<File> files = new ArrayList<>();
        for (ImageItem imageItem : selectedImages) {
            files.add(imageItem.getFile());
        }
        if (selectedImages.size() > 0) {
            lubanUtils.compressImages(this, files);
        } else {
            Toast.makeText(this, "请先选择图片", Toast.LENGTH_SHORT).show();
        }
    }

}
