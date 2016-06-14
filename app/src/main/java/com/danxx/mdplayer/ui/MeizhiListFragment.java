package com.danxx.mdplayer.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.danxx.mdplayer.R;

/**
 * Created by Danxx on 2016/6/14.
 * 图片列表
 */
public class MeizhiListFragment extends Fragment {
    private static final String ARG_PARAM = "url";
    private String mParam1 = "";
    private View rootView;
//    private TextView name;

    public MeizhiListFragment() {
        // Required empty public constructor
    }

    /**
     */
    public static MeizhiListFragment newInstance(String url) {
        MeizhiListFragment fragment = new MeizhiListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_meizhi_list, container, false);
//        name = (TextView) rootView.findViewById(R.id.name);
//        name.setText(mParam1);
        return rootView;
    }
}
