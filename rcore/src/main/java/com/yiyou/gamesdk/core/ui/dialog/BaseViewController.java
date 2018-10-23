package com.yiyou.gamesdk.core.ui.dialog;

import android.content.Context;
import android.content.res.Configuration;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;

import com.mobilegamebar.rsdk.outer.util.ResourceHelper;

/**
 * Created by Orange on 15/6/11.
 */
public abstract class BaseViewController extends RelativeLayout implements IDialogContent {
    private int orientation;
    public BaseViewController(Context context) {
        super(ResourceHelper.getContextHolder().get());
        orientation = context.getResources().getConfiguration().orientation;
        int layoutRes = getLayoutResourceId();
        if (layoutRes > 0) {
            inflate(ResourceHelper.getContextHolder().get(), getLayoutResourceId(), this);
        }
        setGravity(Gravity.CENTER);
    }

    public abstract int getLayoutResourceId();

    @Override
    public View getContentView() {
        return this;
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(orientation == newConfig.orientation) {
            return;
        }
    }

    public void close() {
        ViewControllerNavigator.getInstance().close();
    }

    public Object getVolley() {
        return hashCode();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

}
