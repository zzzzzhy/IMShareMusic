package com.gxut.edu.imsharemusic.view;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gxut.edu.imsharemusic.R;
import com.gxut.edu.imsharemusic.activity.LoginActivity;
import com.gxut.edu.imsharemusic.config.preference.Preferences;
import com.gxut.edu.imsharemusic.helper.LogoutHelper;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.AuthService;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserInfoFragment extends Fragment {


    public UserInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.user_info_fragment, container, false);
        View logoutBtn = view.findViewById(R.id.settings_button_logout);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        return view;
    }

    /**
     * 注销
     */
    private void logout() {
        removeLoginState();
        LogoutHelper.logout();
        Log.i("TAG", "++++++++++++++++++++++++++++++onLogout运行!!!!!!!!!!");
        // 启动登录
        LoginActivity.start(getActivity());
        getActivity().finish();
        NIMClient.getService(AuthService.class).logout();
    }

    /**
     * 清除登陆状态
     */
    private void removeLoginState() {
        Preferences.saveUserToken("");
    }
}
