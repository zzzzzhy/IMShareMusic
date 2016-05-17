package com.gxut.edu.imsharemusic.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.gxut.edu.imsharemusic.BuildConfig;
import com.gxut.edu.imsharemusic.R;


public class AboutActivity extends AppCompatActivity {
    private TextView versionGit;
    private TextView versionDate;

    public static void start(Context context, Intent extras) {
        Intent intent = new Intent();
        intent.setClass(context, AboutActivity.class);
        if (extras != null) {
            intent.putExtras(extras);
        }
        context.startActivity(intent);
    }

    public static void start(Context context) {
        start(context, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        findViews();
        initViewData();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void findViews() {
        versionGit = (TextView) findViewById(R.id.version_detail_git);
        versionDate = (TextView) findViewById(R.id.version_detail_date);

        //        CustomActions.customButton((Button) findViewById(R.id.about_custom_button_1));
    }

    private void initViewData() {
        // 如果使用的IDE是Eclipse， 将该函数体注释掉。这里使用了Android Studio编译期添加BuildConfig字段的特性
        versionGit.setText("Git Version: " + BuildConfig.GIT_REVISION);
        versionDate.setText("Build Date:" + BuildConfig.BUILD_DATE);
    }
}
