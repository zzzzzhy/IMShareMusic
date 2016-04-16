package com.gxut.edu.imsharemusic.view;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gxut.edu.imsharemusic.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlaceholderFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    public PlaceholderFragment() {
    }

    public static PlaceholderFragment newInstance(int sectionNumber) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.textview);
        switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
            case 1:
                textView.setBackgroundColor(Color.BLACK);
                break;
            case 3:
                textView.setBackgroundColor(Color.RED);
                break;
            default:
                break;
        }

        return rootView;
    }

}
