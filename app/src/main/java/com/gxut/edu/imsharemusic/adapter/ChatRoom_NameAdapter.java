package com.gxut.edu.imsharemusic.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gxut.edu.imsharemusic.R;
import com.gxut.edu.imsharemusic.entity.ChatRoom_entity;

import java.util.ArrayList;

/**
 * Created by Taste on 2016/4/16.
 */
public class ChatRoom_NameAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<ChatRoom_entity> datas = new ArrayList<>();

    public ChatRoom_NameAdapter(LayoutInflater inflater, ArrayList<ChatRoom_entity> datas) {
        this.datas = datas;
        this.inflater = inflater;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tv_charNmae;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.chat_room_list_item, null);
        }
        tv_charNmae = (TextView) convertView.findViewById(R.id.char_room_name);
        tv_charNmae.setText(datas.get(position).getCharRoom_name());
        return convertView;
    }
}
