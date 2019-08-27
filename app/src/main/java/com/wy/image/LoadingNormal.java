package com.wy.image;

import android.app.ProgressDialog;
import android.content.Context;


public class LoadingNormal {

    private ProgressDialog dialog;

    public LoadingNormal(Context context, boolean cancelAble) {
        if (dialog == null) {
            dialog = new ProgressDialog(context);
            dialog.setCancelable(cancelAble);
        }
    }

    public void showProgress(String msg) {
        if (msg != null) {
            dialog.setMessage(msg);
        }
        dialog.show();
    }

    public void dismissDialog() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
