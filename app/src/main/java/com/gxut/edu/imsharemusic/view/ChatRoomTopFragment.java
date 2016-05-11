package com.gxut.edu.imsharemusic.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gxut.edu.imsharemusic.R;
import com.gxut.edu.imsharemusic.activity.MainActivity;
import com.netease.nim.uikit.common.fragment.TFragment;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;

/**
 * 聊天室顶层fragment
 * Created by hzxuwen on 2015/12/14.
 */
public class ChatRoomTopFragment extends TFragment {
    private TextView statusText;
    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.chat_room_topfragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findViews();
    }

    public void updateOnlineStatus(boolean isOnline) {
        statusText.setVisibility(isOnline ? View.GONE : View.VISIBLE);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void findViews() {
        statusText = findView(R.id.online_status);

        final ImageView backImage = findView(R.id.back_arrow);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("TAG", "---------------运行");
                NIMClient.getService(ChatRoomService.class).exitChatRoom(((MainActivity) getActivity()).getRoomInfo().getRoomId());
                ((MainActivity) getActivity()).clearChatRoom();
            }
        });
    }
}
