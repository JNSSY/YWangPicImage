package com.wy.lib;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 普通跳转
     *
     * @param cl
     */
    public void startActivity(Class cl) {
        Intent intent = new Intent(this, cl);
        startActivity(intent);
    }
}
