package com.yiyou.gamesdk.testapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gamesdk.shouyouba.tzsy.R;


/**
 * Created by chenshuide on 2/11/15.
 */
public class ShowContentFragment extends Fragment {

    private TextView mTvContent;

    private String content = "";

    public void setData(byte[] data) {
        this.content = new String(data);

        if (mTvContent != null) {
            mTvContent.setText(content);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_show_content, container, false);
        mTvContent = (TextView) layout.findViewById(R.id.content);
        mTvContent.setText(content);
        return layout;
    }
}
