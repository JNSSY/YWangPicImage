package com.wy.lib;

import android.content.Context;

import java.io.File;

public class FileUtils {

    public static void clearLocalPhoto(Context context) {
        //删除本地照片
        String imgCacheDir = context.getExternalCacheDir() + File.separator;
        File file = new File(imgCacheDir);
        if (file.exists()) {
            String[] list = file.list();
            if (list != null && list.length > 0) {
                for (int i = 0; i < list.length; i++) {
                    File f = new File(imgCacheDir + File.separator + list[i]);
                    f.delete();
                }
            }
        }
    }
}
