package com.yiyou.gamesdk.core.ui.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flyco.tablayout.SlidingTabLayout;
import com.yiyou.gamesdk.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Nekomimi on 2017/11/9.
 */

public class GameFragment extends BaseFragment {

    private ViewPager mFragmentVp;
    private SlidingTabLayout mTabLayout;
    private List<BaseFragment> fragmentList;
//    private GiftCenterFragment mGiftCenterFragment;


    @Override
    protected void setFragmentContent(Context content, ViewGroup container, Fragment titleBarFragment) {
        View root = LayoutInflater.from(content).inflate(R.layout.sdk_fragment_game, container, false);
        initChildrenFragment();
        mTabLayout = (SlidingTabLayout) root.findViewById(R.id.game_tab_layout);
        mFragmentVp = (ViewPager) root.findViewById(R.id.vp_game);
        mFragmentVp.setOffscreenPageLimit(2);
        mFragmentVp.setAdapter(new Adapter(getChildFragmentManager(), fragmentList));
        mTabLayout.setViewPager(mFragmentVp);
        container.addView(root);
    }

    public void showViewPagerMsg(int position, int num) {
        if (isAdded() && mTabLayout != null) {
            Log.d(myTag,String.format(Locale.getDefault(), "showViewPagerMsg_ position: %d ,num: %d" , position, num));
            mTabLayout.showMsg(position, num);
        }
    }

    public void hideViewPagerMsg(int position) {
        if (isAdded() && mTabLayout != null) {
            mTabLayout.hideMsg(position);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (mTabLayout!=null && mFragmentVp!=null && fragmentList!=null){
            fragmentList.get(mTabLayout.getCurrentTab()).onHiddenChanged(hidden);
        }
    }

    //    public void updateGiftGetBtn(int packageId, int pkgRemain, int pkgTotal) {
//        if (mGiftCenterFragment != null) {
//            mGiftCenterFragment.updatePkgStatus(packageId,pkgRemain,pkgTotal);
//        }
//    }

    @Override
    protected Fragment getTitleBarFragment() {
        return null;
    }

    private void initChildrenFragment() {
        fragmentList = new ArrayList<>();
        fragmentList.add(new DiscountFragment());
//        mGiftCenterFragment = new GiftCenterFragment();
        fragmentList.add(new GiftCenterFragment());
        fragmentList.add(new AccountTradeFragment());
    }

    static class Adapter extends FragmentPagerAdapter {

        private List<BaseFragment> fragmentList;

        public Adapter(FragmentManager fm, List<BaseFragment> list) {
            super(fm);
            fragmentList = list;
        }

        @Override
        public Fragment getItem(int i) {
            return fragmentList != null ? fragmentList.get(i) : null;
        }

        @Override
        public int getCount() {
            return fragmentList != null ? fragmentList.size() : 0;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentList.get(position).getTabName();
        }
    }
}
