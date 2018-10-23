package com.yiyou.gamesdk.core.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.EditText;

/**
 * Created by Orange on 15/6/16.
 * Only Compatible with Gravity.CENTER_VERTICAL
 * naive !!!
 */
public final class DrawableEditText extends EditText {
    private static final String TAG = "RSDK:DrawableEditText ";

//    public interface OnLeftDrawableClickListener extends OnDrawableClickListener {}

//    public interface OnRightDrawableClickListener extends OnDrawableClickListener {}

    public interface OnDrawableClickListener {
        void onPressed(DrawableEditText editText);
        void onCancel(DrawableEditText editText);
        void onClick(DrawableEditText editText);
    }

    private boolean isLeftDrawableDown;
    private boolean isLeftDownCanceled;

    private boolean isRightDrawableDown;
    private boolean isRightDownCanceled;

    private OnDrawableClickListener onLeftDrawableClickListener;
    private OnDrawableClickListener onRightDrawableClickListener;

    private Rect leftRect;
    private Rect rightRect;

    public DrawableEditText(Context context) {
        super(context);
        init();
    }

    public DrawableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawableEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public DrawableEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setOnLeftDrawableClickListener(OnDrawableClickListener listener) {
        onLeftDrawableClickListener = listener;
    }

    public void setOnRightDrawableClickListener(OnDrawableClickListener listener) {
        onRightDrawableClickListener = listener;
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        final int x = (int)event.getX();
        final int y = (int)event.getY();

        int action = event.getAction();
        if(action == MotionEvent.ACTION_MOVE) {
            boolean oldLeftDownCanceled = isLeftDownCanceled;
            boolean oldRightDownCanceled = isRightDownCanceled;
            isLeftDownCanceled = isLeftDrawableDown && !checkPointRect(leftRect, x, y);
            isRightDownCanceled = isRightDrawableDown && !checkPointRect(rightRect, x, y);
            if(isLeftDrawableDown && oldLeftDownCanceled != isLeftDownCanceled) {
                if(isLeftDownCanceled) {
                    triggerOnCancel(onLeftDrawableClickListener);
                } else {
                    triggerOnPressed(onLeftDrawableClickListener);
                }
            } else if(isRightDrawableDown && oldRightDownCanceled != isRightDownCanceled) {
                if(isRightDownCanceled) {
                    triggerOnCancel(onRightDrawableClickListener);
                } else {
                    triggerOnPressed(onRightDrawableClickListener);
                }
            }
        } else if(action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_UP) {
            Drawable[] drawables = getCompoundDrawables();//left, top, right, bottom
            //check left drawable
            if (checkLeftDrawableRect(drawables[0], x, y)) {
                if(action == MotionEvent.ACTION_DOWN) {// ACTION_DOWN is the first check
                    isLeftDrawableDown = true;
                    triggerOnPressed(onLeftDrawableClickListener);
                    return true; // prevent EditText onLongClick
                } else { // action up
                    if(isLeftDrawableDown) {
                        triggerOnClick(onLeftDrawableClickListener);
                    }
                }
                return true;
            } else if(isLeftDrawableDown) {
                event.setAction(MotionEvent.ACTION_CANCEL);
            }

            if(checkRightDrawableRect(drawables[2], x, y)) {
                if(action == MotionEvent.ACTION_DOWN) {
                    isRightDrawableDown = true;
                    triggerOnPressed(onRightDrawableClickListener);
                    return true; // prevent EditText onLongClick
                } else { // action up
                    if(isRightDrawableDown) {
                        triggerOnClick(onRightDrawableClickListener);
                    }
                }
                return true;
            } else if(isRightDrawableDown) {
                event.setAction(MotionEvent.ACTION_CANCEL);
            }

            if(action == MotionEvent.ACTION_UP) {
                boolean cancelClick = isLeftDrawableDown || isRightDownCanceled;
                isLeftDrawableDown = false;
                isLeftDownCanceled = false;//to prevent stupid mobile MODEL which will not get ACTION_MOVE when first touch or not moving finger
                isRightDrawableDown = false;
                isLeftDownCanceled = false;
                //这里会导致没任何输入时，点击文本变成长按，弹出复制粘贴。 不是cancel时还是回super吧
                return cancelClick ? cancelClick : super.onTouchEvent(event);
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        leftRect.setEmpty();
        rightRect.setEmpty();
    }

    @Override
    public void setGravity(int gravity) {
        super.setGravity(Gravity.CENTER_VERTICAL);//No matter what you set, useless !!! naive !!!
    }

    public void setDrawableLeft(int resId) {
        Drawable d = getDrawableImpl(resId);
        Drawable[] drawables = getCompoundDrawables();
        drawables[0] = d;
        updateLeftRectByDrawable(leftRect, d);
        setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3]);
    }

    public void setDrawableRight(int resId) {
        Drawable d = getDrawableImpl(resId);
        Drawable[] drawables = getCompoundDrawables();
        drawables[2] = d;
        updateRightRectByDrawable(rightRect, d);
        setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3]);
    }

    @TargetApi(22)
    @SuppressWarnings("deprecation")
    private Drawable getDrawableImpl(int resId) {
        if(resId == 0) {
            return null;
        }
        Drawable d;
        if(Build.VERSION.SDK_INT >= 22) {
            d = getResources().getDrawable(resId, null);
        } else {
            d = getResources().getDrawable(resId);
        }
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        return d;
    }

    private void init() {
        setGravity(Gravity.CENTER_VERTICAL);
        leftRect = new Rect();
        rightRect = new Rect();
    }

    //check moving x,y if is in drawable rect to judge it's clicked or not
    private boolean checkPointRect(Rect rect, int x, int y) {
        return rect != null && rect.contains(x, y);
    }

    private boolean checkLeftDrawableRect(Drawable d, int x, int y) {
        if(onLeftDrawableClickListener == null || d == null) {
            return false;
        }

        if(leftRect.isEmpty()) {
            updateLeftRectByDrawable(leftRect, d);
        }
        return leftRect.contains(x, y);
    }

    private boolean checkRightDrawableRect(Drawable d, int x, int y) {
        if(onRightDrawableClickListener == null || d == null) {
            return false;
        }
//
//        if(rightRect.isEmpty()) {
//            updateRightRectByDrawable(rightRect, d);
//        }
//        return rightRect.contains(x, y);  //横屏触摸点在里面，竖屏不在里面，故用下面的方法，具体原因没时间查
    	return x > getWidth() - getPaddingRight() - d.getIntrinsicWidth() ;
    }

    private void updateLeftRectByDrawable(Rect rect, Drawable d) {
        Rect drawableRect = d.getBounds();
//        int drawableHalfHeight = (drawableRect.height() >> 1);
//        int halfHeight = (getMeasuredHeight() >> 1);
        rect.left = getPaddingLeft();
//        rect.top = halfHeight - drawableHalfHeight;
        rect.right = leftRect.left + drawableRect.width();
//        rect.bottom = halfHeight + drawableHalfHeight;
        //限左右就好了吧， 点击范围那么小。 改一下
        rect.top = getTop();
        rect.bottom = getBottom();
    }

    private void updateRightRectByDrawable(Rect rect, Drawable d) {
        if(d == null) {
            rect.setEmpty();
        } else {
            Rect drawableRect = d.getBounds();
//            int drawableHalfHeight = (drawableRect.height() >> 1);
//            int halfHeight = (getMeasuredHeight() >> 1);
            rect.right = getMeasuredWidth() - getPaddingRight();
//            rect.top = halfHeight - drawableHalfHeight;
            rect.left = rightRect.right - drawableRect.width();
//            rect.bottom = halfHeight + drawableHalfHeight;
            //同左
            rect.top = getTop();
            rect.bottom = getBottom();
        }
    }

    private void triggerOnPressed(OnDrawableClickListener listener) {
        if(listener != null) {
            listener.onPressed(this);
        }
    }

    private void triggerOnCancel(OnDrawableClickListener listener) {
        if(listener != null) {
            listener.onCancel(this);
        }
    }

    private void triggerOnClick(OnDrawableClickListener listener) {
        if(listener != null) {
            listener.onClick(this);
        }
    }
}
