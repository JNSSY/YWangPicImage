package com.wy.lib;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LocalDataSource implements DataSource, LoaderManager.LoaderCallbacks<Cursor> {


    public static final int LOADER_ALL = 0;
    public static final int LOADER_CATEGORY = 1;

    private final String[] IMAGE_PROJECTION = {
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media._ID};

    private OnImagesLoadedListener imagesLoadedListener;
    private Context mContext;
    private ArrayList<ImageSet> mImageSetList = new ArrayList<>();

    public LocalDataSource(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void provideMediaItems(OnImagesLoadedListener loadedListener) {
        this.imagesLoadedListener = loadedListener;
        if (mContext instanceof FragmentActivity) {
            ((FragmentActivity) mContext).getSupportLoaderManager().initLoader(LOADER_ALL, null, this);
        } else {
            throw new RuntimeException("your activity must be instance of FragmentActivity");
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        if (id == LOADER_ALL) {//scan all
            CursorLoader cursorLoader = new CursorLoader(mContext,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                    null, null, IMAGE_PROJECTION[2] + " DESC");
            return cursorLoader;
        } else if (id == LOADER_CATEGORY) {//scan one dir
            CursorLoader cursorLoader = new CursorLoader(mContext,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                    IMAGE_PROJECTION[0] + " like '%" + bundle.getString("path") + "%'", null, IMAGE_PROJECTION[2] + " DESC");
            return cursorLoader;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mImageSetList.clear();
        if (data != null) {
            List<ImageItem> allImages = new ArrayList<>();
            int count = data.getCount();
            if (count <= 0) {
                return;
            }
            data.moveToFirst();
            do {
                String imagePath = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                String imageName = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                long imageAddedTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));

                ImageItem item = new ImageItem(imagePath, imageName, imageAddedTime);
                allImages.add(item);

                File imageFile = new File(imagePath);
                File imageParentFile = imageFile.getParentFile();

                ImageSet imageSet = new ImageSet();
                imageSet.name = imageParentFile.getName();
                imageSet.path = imageParentFile.getAbsolutePath();
                imageSet.cover = item;

                if (!mImageSetList.contains(imageSet)) {
                    List<ImageItem> imageList = new ArrayList<>();
                    imageList.add(item);
                    imageSet.imageItems = imageList;
                    mImageSetList.add(imageSet);
                } else {
                    mImageSetList.get(mImageSetList.indexOf(imageSet)).imageItems.add(item);
                }

            } while (data.moveToNext());

            ImageSet imageSetAll = new ImageSet();
            imageSetAll.name = mContext.getResources().getString(R.string.all_images);
            imageSetAll.cover = allImages.get(0);
            imageSetAll.imageItems = allImages;
            imageSetAll.path = "/";

            if (mImageSetList.contains(imageSetAll)) {
                mImageSetList.remove(imageSetAll);//the first item is "all images"
            }
            mImageSetList.add(0, imageSetAll);

            imagesLoadedListener.onImagesLoaded(mImageSetList);//notify the data changed

            AndroidImagePicker.getInstance().setImageSets(mImageSetList);

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
