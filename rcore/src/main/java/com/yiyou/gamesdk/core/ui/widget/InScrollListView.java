package com.yiyou.gamesdk.core.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by BM on 2017/11/13.
 * <p>
 * desc:
 */

public class InScrollListView extends ListView {


    public InScrollListView(Context context) {
        super(context);
    }

    public InScrollListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InScrollListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(1070741823, MeasureSpec.AT_MOST));
    }
}
