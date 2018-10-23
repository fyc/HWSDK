package com.yiyou.gamesdk;

import android.support.v4.app.Fragment;

/**
 * Created by chenshuide on 15/8/9.
 */
public class CoreFragment {

    private String fragmentName;
    private Fragment mFragment;

    public String getFragmentName() {
        return fragmentName;
    }

    public void setFragmentName(String fragmentName) {
        this.fragmentName = fragmentName;
    }

    public Fragment getFragment() {
        return mFragment;
    }

    public void setFragment(Fragment fragment) {
        mFragment = fragment;
    }

    @Override
    public String toString() {
        return "CoreFragment{" +
                "fragmentName='" + fragmentName + '\'' +
                ", mFragment=" + mFragment +
                '}';
    }
}
