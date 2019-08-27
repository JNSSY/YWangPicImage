package com.wy.image;

public interface DataSource {
    void provideMediaItems(OnImagesLoadedListener loadedListener);
}