package com.yiyou.gamesdk.core.ui.floatview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.mobilegamebar.rsdk.outer.event.EventDispatcherAgent;
import com.mobilegamebar.rsdk.outer.event.StartActivityEvent;
import com.mobilegamebar.rsdk.outer.util.Log;
import com.yiyou.gamesdk.R;
import com.yiyou.gamesdk.core.ui.fragment.GameFragment;
import com.yiyou.gamesdk.util.FloatWinCompatHelper;

class FloatViewController implements IViewController {
    private static final String TAG = "RSDK:FloatViewController";

    public static final int HEDE_FLOAT_VIEW_TIME = 3000;//靠边隐藏时间
    private View moveView;
    private ImageView imgFloatView;
    private Context context;
    private int x;
    private int y;
    private int startX;
    private int startY;
    private int controlledSpace = 20;
    private int screenWidth;
    private int screenHeigth;
    boolean isShow = false;
    private OnClickListener mClickListener;
    private WindowManager windowManager;
    private LayoutParams windowManagerParams;
    private Animation animationleft;
    private Animation animationright;
    private boolean ismove = false;
    private int deltaX;
    private int deltaY;
    private int fristx = 0;
    private int fristy = 200;
    private int lastx = 0;
    private int lasty = 0;
    private int totoalx = 0;
    private int totoaly = 0;
    private int floatMoveViewWith;
    private static final int MES_ANIMA_LEFT = 0;
    private static final int MES_ANIMA_RIGHT = 1;
    private static final int VIEW_GONE = 2;
    private static final int VIEW_Transparent = 3;


    private Handler handler = new Handler(Looper.getMainLooper()) {

        public void handleMessage(Message msg) {

            switch (msg.what) {

                case MES_ANIMA_LEFT:
                    animationleft = AnimationUtils.loadAnimation(context,
                            R.anim.tt_sdk_anim_float_view_left);
                    animationleft.setFillAfter(true);
                    imgFloatView.startAnimation(animationleft);
                    animationleft.setAnimationListener(ainimaLeft);

                    break;

                case MES_ANIMA_RIGHT:
                    animationright = AnimationUtils.loadAnimation(context,
                            R.anim.tt_sdk_anim_float_view_right);
                    animationright.setFillAfter(true);
                    imgFloatView.startAnimation(animationright);

                    animationright.setAnimationListener(ainimaRigth);


                    break;

//                case VIEW_GONE:
//
//                    imgFloatView.setVisibility(View.GONE);
//                    Log.d(TAG, "handleMessage: bingo");
//                    break;

                case VIEW_Transparent:
                    imgFloatView.setImageResource(R.drawable.sdk_float_window_transparent);
                    break;

                default:
                    break;
            }

        }
    };

    private AnimationListener ainimaLeft = new AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            imgFloatView.setImageResource(R.drawable.tt_sdk_floatview_close_left);
        }
    };

    private AnimationListener ainimaRigth = new AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            imgFloatView.setImageResource(R.drawable.tt_sdk_floatview_close_right);
        }
    };

    public FloatViewController(Context mContext) {
        this.context = mContext;
        initeWindowManager();
        mClickListener = new MyOnClickListener();
        inite(mContext);

    }

    @SuppressLint("InflateParams")
    public void inite(Context context) {
        moveView = LayoutInflater.from(context).inflate(R.layout.tt_sdk_float_view, null);
        imgFloatView = (ImageView) moveView.findViewById(R.id.img_floatview);

        int w1 = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h1 = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        moveView.measure(w1, h1);
        floatMoveViewWith = moveView.getMeasuredWidth();
        moveView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                screenWidth = windowManager.getDefaultDisplay().getWidth();
                screenHeigth = windowManager.getDefaultDisplay().getHeight();
                totoalx = lastx - fristx;
                totoaly = lasty - fristy;
                x = (int) event.getRawX();
                y = (int) event.getRawY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        lastx = (int) event.getRawX();
                        lasty = (int) event.getRawY();
                        fristx = lastx;
                        fristy = lasty;
                        setFloatViewNormalImg();
                        imgFloatView.clearAnimation();
                        removeRunable();
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        deltaX = (int) event.getRawX() - lastx;
                        deltaY = (int) event.getRawY() - lasty;
                        lastx = (int) event.getRawX();
                        lasty = (int) event.getRawY();
                        if (ismove || Math.abs(totoalx) >= 10
                                || Math.abs(totoaly) >= 10) {
                            ismove = true;
                            updateViewPosition();
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        ismove = false;
                        if (Math.abs(x - startX) < controlledSpace
                                && Math.abs(y - startY) < controlledSpace) {
                            if (mClickListener != null) {
                                mClickListener.onClick(imgFloatView);
                            }
                        }

                        if (isMoveToSide()) {
                            if (x <= screenWidth / 2) {
                                windowManagerParams.x = 0;
                            } else {
                                windowManagerParams.x = screenWidth - floatMoveViewWith;
                            }
                            windowManager.updateViewLayout(moveView, windowManagerParams);
                        }

                        if (!ismove) {
                            viewAnima();
                        }
                        break;
                    }
                }
                return true;
            }
        });

    }
    @Override
    public View getViewContainer() {
        return moveView;
    }


    // 隐藏该窗体
    public void hide() {
        Log.d(TAG, "hide: ");
        if (isShow) {
            removeRunable();
            windowManager.removeView(moveView);
            isShow = false;
        }
    }

    // 显示该窗体
    public void show() {
        Log.d(TAG, "show: ");
        if (!isShow) {
            Log.d(TAG, "show: " + isShow);
            setFloatViewNormalImg();
            windowManager.addView(moveView, windowManagerParams);
            isShow = true;
            viewAnima();
        }
    }

    private void updateViewPosition() {
        // 更新浮动窗口位置参数
        windowManagerParams.x += (int) deltaX;
        windowManagerParams.y += (int) deltaY;
        windowManager.updateViewLayout(moveView, windowManagerParams); // 刷新显示
        setFloatViewNormalImg();
    }




    private class MyOnClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {

            if (v.getId() == R.id.img_floatview) {
                EventDispatcherAgent.defaultAgent().broadcast(StartActivityEvent.TYPE_START_ACTIVITY_ENVENT,
                        getFragmentParam(GameFragment.class.getName()));
                FloatViewManager.getInstance().hide();
//                handler.postDelayed(runnableViewGone, 50);
            }
        }

    }

    public Runnable myRunnableLeft = new Runnable() {
        public void run() {

            Message message = handler.obtainMessage();
            message.what = MES_ANIMA_LEFT;
            handler.sendMessage(message);

        }
    };

    public Runnable myRunnableRigth = new Runnable() {
        public void run() {

            Message message = handler.obtainMessage();
            message.what = MES_ANIMA_RIGHT;
            handler.sendMessage(message);

        }
    };

//    public Runnable runnableViewGone = new Runnable() {
//        public void run() {
//
//            Message message = handler.obtainMessage();
//            message.what = VIEW_GONE;
//            handler.sendMessage(message);
//
//        }
//    };

    public Runnable runnableTransparent = new Runnable() {
        @Override
        public void run() {
            Message message = handler.obtainMessage();
            message.what = VIEW_Transparent;
            handler.sendMessage(message);
        }
    };

    private void viewAnima() {

        if (windowManagerParams.x < 25) {
            windowManagerParams.x = 0;
            windowManager.updateViewLayout(moveView,windowManagerParams); // 刷新显示
            handler.postDelayed(myRunnableLeft, HEDE_FLOAT_VIEW_TIME);
        } else if (windowManagerParams.x > (screenWidth - floatMoveViewWith - 25)) {
            windowManagerParams.x = screenWidth - floatMoveViewWith;
            windowManager.updateViewLayout(moveView,windowManagerParams); // 刷新显示
            handler.postDelayed(myRunnableRigth, HEDE_FLOAT_VIEW_TIME);
        }

    }

    private void removeRunable() {
        handler.removeCallbacksAndMessages(null);
    }


    private void initeWindowManager() {
        windowManager = (WindowManager) context.getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        screenWidth = windowManager.getDefaultDisplay().getWidth();
        screenHeigth = windowManager.getDefaultDisplay().getHeight();
        windowManagerParams = new LayoutParams();
        windowManagerParams.alpha = 0.9f;
        if (Build.VERSION.SDK_INT<19 || Build.VERSION.SDK_INT>24 || FloatWinCompatHelper.isDeviceNotSupportNewWindowType()){
            windowManagerParams.type = LayoutParams.TYPE_PHONE;
        }else{
            windowManagerParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        }
        windowManagerParams.format = PixelFormat.RGBA_8888;
        windowManagerParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
                | LayoutParams.FLAG_NOT_FOCUSABLE
                | LayoutParams.FLAG_FULLSCREEN;
        windowManagerParams.gravity = Gravity.LEFT | Gravity.TOP;
        windowManagerParams.x = fristx;
        windowManagerParams.y = fristy;
        windowManagerParams.width = LayoutParams.WRAP_CONTENT;
        windowManagerParams.height = LayoutParams.WRAP_CONTENT;
    }


    private boolean isMoveToSide() {
        boolean isMoveToSide = false;
        if (screenHeigth > screenWidth) {
            isMoveToSide = true;
        }
        return isMoveToSide;

    }

    private void setFloatViewNormalImg() {
        imgFloatView.setImageResource(R.drawable.sdk_float_window_normal);
        handler.postDelayed(runnableTransparent, 2000);
    }


    private StartActivityEvent.FragmentParam getFragmentParam(String fragmentName) {
        return new StartActivityEvent.FragmentParam(0, context,
                StartActivityEvent.DISPLAY_TYPE_FULLSCREEN,
                null,fragmentName, null);
    }
}
