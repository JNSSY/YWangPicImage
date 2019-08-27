package com.wy.image;

import android.widget.ImageView;

public interface ImgLoader {
    void onPresentImage(ImageView imageView, String imageUri, int size);
}
