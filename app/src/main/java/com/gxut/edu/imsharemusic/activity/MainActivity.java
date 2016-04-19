package com.gxut.edu.imsharemusic.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.gxut.edu.imsharemusic.R;
import com.gxut.edu.imsharemusic.adapter.MainSectionsPagerAdapter;
import com.gxut.edu.imsharemusic.view.ChatRoomListFragment;
import com.gxut.edu.imsharemusic.view.PlaceholderFragment;
import com.gxut.edu.imsharemusic.view.UserInfoFragment;
import com.gxut.edu.imsharemusic.view.ZoomOutPageTransformer;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private MainSectionsPagerAdapter mMainSectionsPagerAdapter;
    private ViewPager mViewPager;
    private ArrayList<Fragment> fragmentss = new ArrayList<>();

    public static void start(Context context) {
        start(context, null);
    }
    public static void start(Context context, Intent extras) {
        Intent intent = new Intent();
        intent.setClass(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (extras != null) {
            intent.putExtras(extras);
        }
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // Toolbar toolbar = (Toolbar) findViewById(R.id.bar_panel);
       // setSupportActionBar(toolbar);
        Fragment chatRoomFragment = new ChatRoomListFragment();
        Fragment placeholderFragment = PlaceholderFragment.newInstance(2);
        Fragment userInfoFragment = new UserInfoFragment();
        fragmentss.add(chatRoomFragment);
        fragmentss.add(placeholderFragment);
        fragmentss.add(userInfoFragment);
        mMainSectionsPagerAdapter = new MainSectionsPagerAdapter(getSupportFragmentManager(), fragmentss);
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mMainSectionsPagerAdapter);
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mViewPager.setCurrentItem(1);


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
