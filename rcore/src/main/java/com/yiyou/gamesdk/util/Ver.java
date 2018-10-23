package com.yiyou.gamesdk.util;

import android.content.Context;

/**
 * Created by levyyoung on 14/7/17.
 */
public class Ver {
    public int mMajor;
    public int mMinor;
    public int mBuild;
    public boolean isSnapshot;

    public boolean bigThan(Ver v) {
        return (mMajor > v.mMajor) || ((mMajor == v.mMajor) && (mMinor > v.mMinor))
                || ((mMajor == v.mMajor) && (mMinor == v.mMinor) && (mBuild > v.mBuild));
    }

    public boolean smallThan(Ver v) {
        return (mMajor < v.mMajor) || ((mMajor == v.mMajor) && (mMinor < v.mMinor))
                || ((mMajor == v.mMajor) && (mMinor == v.mMinor) && (mBuild < v.mBuild));
    }


    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        Ver v = (Ver) o;
        return (mMajor == v.mMajor) && (mMinor == v.mMinor)
                && (mBuild == v.mBuild);
    }


    public String toString(Context ctx) {
        if (isSnapshot) {
            return String.format("%d.%d.%d(SNAPSHOT, Build %s)", mMajor, mMinor, mBuild, VersionUtil.getVersionCode(ctx));
        }
        return String.format("%d.%d.%d", mMajor, mMinor, mBuild);
    }

    public int[] toVerCode() {
        int[] ver = new int[4];
        ver[0] = mMajor;
        ver[1] = mMinor;
        ver[2] = mBuild;
        ver[3] = isSnapshot ? 1 : 0;

        return ver;
    }

    public String getVersionNameWithoutSnapshot() {
        return String.format("%d.%d.%d", mMajor, mMinor, mBuild);
    }

}
