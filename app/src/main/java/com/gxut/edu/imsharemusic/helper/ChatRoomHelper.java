package com.gxut.edu.imsharemusic.helper;

import com.gxut.edu.imsharemusic.R;

/**
 * Created by hzxuwen on 2016/1/19.
 */
public class ChatRoomHelper {
    public static final int[] imageRes = {R.drawable.chatroom_head_1, R.drawable.chatroom_head_2, R.drawable.chatroom_head_3,
            R.drawable.chatroom_head_4, R.drawable.chatroom_head_5, R.drawable.chatroom_head_6, R.drawable.chatroom_head_7,
            R.drawable.imagechatroom, R.drawable.chatroom_head_8};

    public static void init() {
        ChatRoomMemberCache.getInstance().clear();
        ChatRoomMemberCache.getInstance().registerObservers(true);
    }

    public static void logout() {
        ChatRoomMemberCache.getInstance().registerObservers(false);
        ChatRoomMemberCache.getInstance().clear();
    }
}
