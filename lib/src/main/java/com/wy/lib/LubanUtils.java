package com.wy.lib;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;
import top.zibin.luban.OnRenameListener;

public class LubanUtils {

    private ProgressDialog pDialog;
    private Context context;
    private List<File> fileList;
    private int count = 1;

    public LubanUtils(Context context) {
        this.context = context;
//        File file_dir = new File(Environment.getExternalStorageDirectory() + File.separator + "hxx");
//        if (!file_dir.exists()) {
//            file_dir.mkdir();
//        }

        if (pDialog == null) {
            pDialog = new ProgressDialog(context);
            pDialog.setCancelable(false);
        }

        fileList = new ArrayList<>();
    }

    private LubanInterface lubanInterface;

    public void getCompressedFiles(LubanInterface lubanInterface) {
        this.lubanInterface = lubanInterface;
    }

    public void compressImage(Activity activity, final List<File> files) {
        count = 1;
        Luban.with(activity)
                .load(files)
                .ignoreBy(10)
                .setTargetDir(activity.getExternalCacheDir() + File.separator)
                .setFocusAlpha(false)
                .filter(new CompressionPredicate() {
                    @Override
                    public boolean apply(String path) {
                        return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
                    }
                })
                .setRenameListener(new OnRenameListener() {
                    @Override
                    public String rename(String filePath) {
                        try {
                            MessageDigest md = MessageDigest.getInstance("MD5");
                            md.update(filePath.getBytes());
                            return new BigInteger(1, md.digest()).toString(32);
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }
                        return "";
                    }
                })
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        showProgress();
                    }

                    @Override
                    public void onSuccess(File file) {
                        fileList.add(file);
                        if (count++ == files.size()) {
                            lubanInterface.getFiles(fileList);
                            dismissProgress();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissProgress();
                        Toast.makeText(context, "图片出现问题", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }).launch();
    }

    public List<String> getImageBase64List(List<File> fileList) {
        List<String> b64List = new ArrayList<>();
        for (File file : fileList) {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] bytes = baos.toByteArray();
            String string = android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT);
            string = string.replaceAll("\n", "");
            string = string.replaceAll("\r", "");
            b64List.add(string);
        }
        return b64List;
    }

    private void showProgress() {
        pDialog.setMessage("正在处理，请稍后...");
        pDialog.show();
    }

    private void dismissProgress() {
        pDialog.dismiss();
    }

}
