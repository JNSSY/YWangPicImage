package com.wy.lib;

import android.widget.ImageView;

public interface ImgLoader {
    void onPresentImage(ImageView imageView, String imageUri, int size);
}
