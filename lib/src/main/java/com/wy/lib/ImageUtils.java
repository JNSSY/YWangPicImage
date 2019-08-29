package com.wy.lib;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 图片压缩工具类
 * Created by Administrator on 2018/8/6.
 */

public class ImageUtils {
    //压缩后的图片文件
    private static File cmp_file = null;
    private static Bitmap cmp_bitmap;
    private static List<Bitmap> cmp_listBitmap;

    /**
     * 图片质量压缩
     *
     * @param format    PNG，JPEG，WEBP
     * @param quality   图片的质量 1-100
     * @param uncmp_img 压缩前的图片
     * @param file_path 压缩后的图片路径
     */
    public static Bitmap qualityCompress(Bitmap.CompressFormat format, int quality, File uncmp_img, String file_path) {
        File file = new File(file_path);
        if (!file.exists()) {
            file.mkdir();
        }
        Log.e("img", "压缩前的图片大小 " + uncmp_img.length() / 1024 + "\t" + "KB");
        cmp_bitmap = null;
        FileInputStream is = null;
        FileOutputStream os = null;
        try {
            cmp_file = new File(file_path, System.currentTimeMillis() + ".jpg");
            is = new FileInputStream(uncmp_img);
            os = new FileOutputStream(cmp_file);
            cmp_bitmap = BitmapFactory.decodeStream(is);
            cmp_bitmap.compress(format, quality, os);
            Log.e("img", "压缩后的图片大小 " + cmp_file.length() / 1024 + "\t" + "KB");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return cmp_bitmap;
    }

    public static List<Bitmap> qualityCompress(Bitmap.CompressFormat format, int quality, List<File> fileList, String file_path) {

        cmp_listBitmap = new ArrayList<>();

        File file = new File(file_path);
        if (!file.exists()) {
            file.mkdirs();
        }
        FileInputStream is = null;
        FileOutputStream os = null;
        try {
            for (int i = 0; i < fileList.size(); i++) {
                Log.e("img", "压缩前的图片 " + i + " 大小 " + fileList.get(i).length() / 1024 + "\t" + "KB");
                cmp_bitmap = null;
                cmp_file = new File(file_path, System.currentTimeMillis() + ".jpg");
                is = new FileInputStream(fileList.get(i));
                os = new FileOutputStream(cmp_file);
                cmp_bitmap = BitmapFactory.decodeStream(is);
                cmp_bitmap.compress(format, quality, os);
                cmp_listBitmap.add(cmp_bitmap);
                Log.e("img", "压缩后的图片 " + i + " 大小 " + cmp_file.length() / 1024 + "\t" + "KB");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return cmp_listBitmap;
    }

    /**
     * 图片尺寸压缩
     *
     * @param format    PNG，JPEG，WEBP
     * @param quality   图片的质量 1-100
     * @param uncmp_img 压缩前的图片
     * @param file_path 压缩后的图片路径
     */
    public static Bitmap sizeCompress(Bitmap.CompressFormat format, int quality, File uncmp_img, String file_path) {
        File file = new File(file_path);
        if (!file.exists()) {
            file.mkdir();
        }
        Log.e("img", "压缩前的图片大小 " + uncmp_img.length() / 1024 + "\t" + "KB");
        cmp_bitmap = null;
        FileInputStream is = null;
        FileOutputStream os = null;
        // 尺寸压缩倍数,值越大，图片尺寸越小
        int ratio = 2;
        try {
            cmp_file = new File(file_path, "cmp_img.jpeg");
            is = new FileInputStream(uncmp_img);
            os = new FileOutputStream(cmp_file);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            cmp_bitmap = Bitmap.createBitmap(bitmap.getWidth() / ratio, bitmap.getHeight() / 2, Bitmap.Config.ARGB_8888);
            Rect rect = new Rect(0, 0, bitmap.getWidth() / ratio, bitmap.getHeight() / 2);
            Canvas canvas = new Canvas(cmp_bitmap);
            canvas.drawBitmap(bitmap, null, rect, null);
            cmp_bitmap.compress(format, quality, os);
            Log.e("img", "压缩后的图片大小 " + cmp_file.length() / 1024 + "\t" + "KB");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return cmp_bitmap;
    }

}
