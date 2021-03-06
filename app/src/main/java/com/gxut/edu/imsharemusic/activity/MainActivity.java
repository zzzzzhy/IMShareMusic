package com.gxut.edu.imsharemusic.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gxut.edu.imsharemusic.DemoCache;
import com.gxut.edu.imsharemusic.R;
import com.gxut.edu.imsharemusic.adapter.MainSectionsPagerAdapter;
import com.gxut.edu.imsharemusic.helper.ChatRoomMemberCache;
import com.gxut.edu.imsharemusic.view.ChatRoomListFragment;
import com.gxut.edu.imsharemusic.view.ChatRoomMessageFragment;
import com.gxut.edu.imsharemusic.view.ChatRoomTopFragment;
import com.gxut.edu.imsharemusic.view.OnlinePeopleFragment;
import com.gxut.edu.imsharemusic.view.UserInfoFragment;
import com.gxut.edu.imsharemusic.view.ZoomOutPageTransformer;
import com.netease.nim.uikit.LoginSyncDataStatusObserver;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.common.activity.TActivity;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.ChatRoomServiceObserver;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomKickOutEvent;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomStatusChangeData;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomData;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomResultData;

import java.util.ArrayList;

public class MainActivity extends TActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private MainSectionsPagerAdapter mMainSectionsPagerAdapter;
    private ViewPager mViewPager;
    private ArrayList<Fragment> fragmentss = new ArrayList<>();
    private final static String EXTRA_ROOM_ID = "ROOM_ID";
    private static final String TAG = MainActivity.class.getSimpleName();

    private Fragment chatRoomFragment;
    private Fragment chatRoomMessageFragment;
    private Fragment userInfoFragment;
    private OnlinePeopleFragment onlinFragment;

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private View view;
    private Button button_login;
    private Button button_register;
    private TextView userTitle;
    private HeadImageView userImageView;

    /**
     * 聊天室基本信息
     */
    private String roomId;
    private ChatRoomInfo roomInfo;

    private ChatRoomTopFragment fragment;
    private Context context;

    /**
     * 子页面
     */
    private ChatRoomMessageFragment messageFragment;
    private AbortableFuture<EnterChatRoomResultData> enterRequest;


    public static void start(Context context, String roomId) {
        start(context, null, roomId);
    }


    public static void start(Context context, Intent extras, String roomId) {
        Intent intent = new Intent();
        intent.setClass(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(EXTRA_ROOM_ID, roomId);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (extras != null) {
            intent.putExtras(extras);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        // Toolbar toolbar = (Toolbar) findViewById(R.id.bar_panel);
        // setSupportActionBar(toolbar);
        initView();
        mMainSectionsPagerAdapter = new MainSectionsPagerAdapter(getSupportFragmentManager(), fragmentss);
        mViewPager.setAdapter(mMainSectionsPagerAdapter);
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mViewPager.setCurrentItem(1);
        roomId = getIntent().getStringExtra(EXTRA_ROOM_ID);

        // 等待同步数据完成
        boolean syncCompleted = LoginSyncDataStatusObserver.getInstance().observeSyncDataCompletedEvent(new Observer<Void>() {
            @Override
            public void onEvent(Void v) {
                DialogMaker.dismissProgressDialog();
            }
        });

        Log.i(TAG, "sync completed = " + syncCompleted);
        if (!syncCompleted) {
            DialogMaker.showProgressDialog(MainActivity.this, getString(R.string.prepare_data)).setCanceledOnTouchOutside(false);
        }
        Log.i(TAG, "---------------------" + roomId);
        // 注册监听
        registerObservers(true);

        // 登录聊天室
        enterRoom();
        context = this;
//        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.container);
        chatRoomFragment = new ChatRoomListFragment();
        chatRoomMessageFragment = new ChatRoomMessageFragment();
        userInfoFragment = new UserInfoFragment();
        onlinFragment = new OnlinePeopleFragment();
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        fragmentss.add(chatRoomFragment);
        fragmentss.add(chatRoomMessageFragment);
        fragmentss.add(onlinFragment);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        registerObservers(false);
    }

    @Override
    public void onBackPressed() {
        if (messageFragment == null || !messageFragment.onBackPressed()) {
            super.onBackPressed();
        }

        logoutChatRoom();
    }

    private void enterRoom() {
        DialogMaker.showProgressDialog(this, null, "", true, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (enterRequest != null) {
                    enterRequest.abort();
                    onLoginDone();
                    finish();
                }
            }
        }).setCanceledOnTouchOutside(false);
        EnterChatRoomData data = new EnterChatRoomData(roomId);
        enterRequest = NIMClient.getService(ChatRoomService.class).enterChatRoom(data);
        enterRequest.setCallback(new RequestCallback<EnterChatRoomResultData>() {
            @Override
            public void onSuccess(EnterChatRoomResultData result) {
                onLoginDone();
                roomInfo = result.getRoomInfo();
                ChatRoomMember member = result.getMember();
                member.setRoomId(roomInfo.getRoomId());
                ChatRoomMemberCache.getInstance().saveMyMember(member);
                // initChatRoomFragment();
                initMessageFragment();
                Toast.makeText(MainActivity.this, "欢迎进入聊天室", Toast.LENGTH_SHORT).show();
                onlinFragment.onCurrent();
                navigationView.inflateHeaderView(R.layout.nav_header_fragment);
                view = navigationView.getHeaderView(0);
                userTitle = (TextView) view.findViewById(R.id.usertitle);
                userImageView = (HeadImageView) view.findViewById(R.id.userimageView);
                userTitle.setText(NimUserInfoCache.getInstance().getUserDisplayName(DemoCache.getAccount()));
                userImageView.loadBuddyAvatar(DemoCache.getAccount());
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UserProfileSettingActivity.start(MainActivity.this, DemoCache.getAccount());
                    }
                });
            }

            @Override
            public void onFailed(int code) {
                onLoginDone();
                if (code == ResponseCode.RES_CHATROOM_BLACKLIST) {
                    Toast.makeText(MainActivity.this, "你已被拉入黑名单，不能再进入", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "enter chat room failed, code=" + code, Toast.LENGTH_SHORT).show();
                    LoginActivity.start(context);
                    finish();
                }
                navigationView.inflateHeaderView(R.layout.nav_header_fragment2);
                view = navigationView.getHeaderView(0);
                button_register = (Button) view.findViewById(R.id.register);
                button_login = (Button) view.findViewById(R.id.login);
                button_login.setOnClickListener(MainActivity.this);
                button_register.setOnClickListener(MainActivity.this);
                //finish();
            }

            @Override
            public void onException(Throwable exception) {
                onLoginDone();
                Toast.makeText(MainActivity.this, "enter chat room exception, e=" + exception.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void registerObservers(boolean register) {
        NIMClient.getService(ChatRoomServiceObserver.class).observeOnlineStatus(onlineStatus, register);
        NIMClient.getService(ChatRoomServiceObserver.class).observeKickOutEvent(kickOutObserver, register);
    }

    private void logoutChatRoom() {
        NIMClient.getService(ChatRoomService.class).exitChatRoom(roomId);
        clearChatRoom();
    }

    public void clearChatRoom() {
        ChatRoomMemberCache.getInstance().clearRoomCache(roomId);
        finish();
    }

    Observer<ChatRoomStatusChangeData> onlineStatus = new Observer<ChatRoomStatusChangeData>() {
        @Override
        public void onEvent(ChatRoomStatusChangeData chatRoomStatusChangeData) {
            if (chatRoomStatusChangeData.status == StatusCode.CONNECTING) {
                DialogMaker.updateLoadingMessage("连接中...");
            } else if (chatRoomStatusChangeData.status == StatusCode.UNLOGIN) {
                Toast.makeText(MainActivity.this, R.string.nim_status_unlogin, Toast.LENGTH_SHORT).show();
            } else if (chatRoomStatusChangeData.status == StatusCode.LOGINING) {
                DialogMaker.updateLoadingMessage("登录中...");
            } else if (chatRoomStatusChangeData.status == StatusCode.LOGINED) {
                if (fragment != null) {
                    fragment.updateOnlineStatus(true);
                }
            } else if (chatRoomStatusChangeData.status.wontAutoLogin()) {
            } else if (chatRoomStatusChangeData.status == StatusCode.NET_BROKEN) {
                if (fragment != null) {
                    fragment.updateOnlineStatus(false);
                }
                Toast.makeText(MainActivity.this, R.string.net_broken, Toast.LENGTH_SHORT).show();
            }
            LogUtil.i(TAG, "Chat Room Online Status:" + chatRoomStatusChangeData.status.name());
        }
    };

    Observer<ChatRoomKickOutEvent> kickOutObserver = new Observer<ChatRoomKickOutEvent>() {
        @Override
        public void onEvent(ChatRoomKickOutEvent chatRoomKickOutEvent) {
            Toast.makeText(MainActivity.this, "被踢出聊天室，原因:" + chatRoomKickOutEvent.getReason(), Toast.LENGTH_SHORT).show();
            clearChatRoom();
        }
    };

/*    private void initChatRoomFragment() {
        fragment = (ChatRoomTopFragment) getSupportFragmentManager().findFragmentById(R.id.chat_rooms_fragment);
        if (fragment != null) {
            fragment.updateView();
        } else {
            // 如果Fragment还未Create完成，延迟初始化
            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    initChatRoomFragment();
                }
            }, 50);
        }
    }*/

    private void initMessageFragment() {
        messageFragment = (ChatRoomMessageFragment) chatRoomMessageFragment;
        if (messageFragment != null) {
            messageFragment.init(roomId);
            //Log.i("TAG", "-------------------------messageFragment运行!!!!!!!!!!");
        } else {
            //Log.i("TAG", "-------------------------messageFragment没运行!!!!!!!!!!");
            // 如果Fragment还未Create完成，延迟初始化
            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    initMessageFragment();
                    // Log.i("TAG", "++++++++++++++++++++++++++++++messageFragment运行!!!!!!!!!!");
                }
            }, 50);
        }
    }

    private void onLoginDone() {
        enterRequest = null;
        DialogMaker.dismissProgressDialog();
    }

    public ChatRoomInfo getRoomInfo() {
        return roomInfo;
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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_history) {
            // Handle the camera action
            Log.i("1", "yunxing");
            //HistoryActivity.start(MainActivity_Movie.this);

        } else if (id == R.id.nav_collect) {
           // CollectActivity.start(MainActivity_Movie.this);

        } else if (id == R.id.nav_attention) {
           // AttentionActivity.start(MainActivity_Movie.this);

        } else if (id == R.id.nav_about) {
            AboutActivity.start(MainActivity.this);

        } else if (id == R.id.nav_share) {


        } else if (id == R.id.nav_logout) {
//            navigationView.removeHeaderView(navigationView.getHeaderView(0));
//            navigationView.inflateHeaderView(R.layout.nav_header_fragment2);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register:
                Log.i("1", "yunxing");
                break;
            case R.id.login:
                LoginActivity.start(MainActivity.this);
                finish();
//                navigationView.removeHeaderView(navigationView.getHeaderView(0));
//                navigationView.inflateHeaderView(R.layout.nav_header_fragment);
                break;
            default:
                break;
        }
    }
}
