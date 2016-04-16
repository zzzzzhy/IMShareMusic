package com.gxut.edu.imsharemusic.view;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.gxut.edu.imsharemusic.R;
import com.gxut.edu.imsharemusic.adapter.ChatRoom_NameAdapter;
import com.gxut.edu.imsharemusic.entity.ChatRoom_entity;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatRoomFragment extends Fragment {


    public ChatRoomFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.chat_room_fragment, container, false);
        ArrayList<ChatRoom_entity> datas = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ChatRoom_entity name = new ChatRoom_entity("房间号：" + i);
            datas.add(name);
        }
        ListView view1 = (ListView)view.findViewById(R.id.chat_room_fragment);
        ChatRoom_NameAdapter nameAdapter = new ChatRoom_NameAdapter(getLayoutInflater(savedInstanceState), datas);
        view1.setAdapter(nameAdapter);
        return view;

    }

}
