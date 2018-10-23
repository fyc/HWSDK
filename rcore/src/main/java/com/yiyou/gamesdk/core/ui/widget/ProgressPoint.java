package com.yiyou.gamesdk.core.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import com.yiyou.gamesdk.R;
import com.mobilegamebar.rsdk.outer.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenshuide on 16-3-4.
 */
public class ProgressPoint extends View {
    private static final String TAG = "ProgressPoint";
    private static final int POINT_SPLASH = 1;
    private static final int POINT_NUM = 4;


    private Paint selectPaint, defaultPaint;
    private Handler handler;
    private static int RADIO;//半径
    private List<Point> list = new ArrayList<>();
    private boolean isInit;
    private int pointColor;
    private int selectPoint = 0;
    private int time = 0;

    public ProgressPoint(Context context) {
        super(context);
        init(context);
    }


    public ProgressPoint(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray tr = context.obtainStyledAttributes(attrs, R.styleable.ProgressPoint);
        pointColor = tr.getColor(R.styleable.ProgressPoint_pointColor, getResources().getColor(R.color.dk_color_01d296));
        tr.recycle();
        init(context);
    }

    private void init(Context context) {
        RADIO = context.getResources().getDimensionPixelSize(R.dimen.progress_point_size);
        selectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        selectPaint.setColor(pointColor);

        defaultPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        defaultPaint.setColor(pointColor);
        defaultPaint.setAlpha(100);
        handler = new SplashHandler(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isInit) {
            initPoints();
        }

        for (int i = 0; i < list.size(); i++) {
            Point p = list.get(i);
            canvas.drawCircle(p.x, p.y, RADIO, i == selectPoint ? selectPaint : defaultPaint);

        }

        time++;
        selectPoint = time % POINT_NUM;

        handler.sendEmptyMessageDelayed(POINT_SPLASH, 375);

    }


    private static class SplashHandler extends Handler {
        private WeakReference<View> weakReference;

        public SplashHandler(View view) {
            weakReference = new WeakReference<>(view);
        }


        @Override
        public void handleMessage(Message msg) {
            View view = weakReference.get();
            if (view == null) {
                Log.e(TAG, "handleMessage target view is null  ");
                return;
            }

            switch (msg.what) {

                case POINT_SPLASH:

                    view.invalidate();

            }

        }
    }


    private static class Point {
        private int x;
        private int y;

    }

    private void initPoints() {

        int pointSpace = getWidth() / POINT_NUM;

        for (int i = 0; i < POINT_NUM; i++) {

            Point point = new Point();
            point.x = pointSpace * (2 * i + 1) / 2;
            point.y = getHeight() / 2;

            list.add(point);

        }

        isInit = true;
    }
}
