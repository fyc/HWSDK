package com.yiyou.gamesdk.util;

import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by levyyoung on 15/7/23.
 */
public class ViewUtils {
    /**
     *
     * @param view 参照View
     * @return 参照View bottom 到屏幕bottom 的距离
     */
    public static int distanceToScreenBottom(View view) {
        Context ctx = view.getContext();
        DisplayMetrics dm = ctx.getResources().getDisplayMetrics();
        int screenHeight = dm.heightPixels;
        int location[] = new int[2];
        view.getLocationOnScreen(location);
        int viewBottomPosition = location[1]+view.getHeight();
        return screenHeight - viewBottomPosition;
    }

    /**
     *
     * @param view 参照View
     * @return 参照View四边的全局左边
     */
    public static Rect globalPosition(View view) {
        Rect rect = new Rect();
        int location[] = new int[2];
        view.getLocationOnScreen(location);
        int t = location[1];
        int b = location[1] + view.getHeight();
        int l = location[0];
        int r = location[0] + view.getWidth();
        rect.top = t;
        rect.bottom = b;
        rect.left = l;
        rect.right = r;
        return rect;
    }

    public static void setViewEnable(View view, boolean enabled) {
        if (view.isEnabled() != enabled) {
            view.setEnabled(enabled);
        }
    }


    public static void bindEditWithButton(EditText editText, final View button){
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //当actionId == XX_SEND 或者 XX_DONE时都触发
                //或者event.getKeyCode == ENTER 且 event.getAction == ACTION_DOWN时也触发
                //注意，这是一定要判断event != null。因为在某些输入法上会返回null。
                if (actionId == EditorInfo.IME_ACTION_SEND
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                    //处理事件
                    button.performClick();
                }
                return false;
            }
        });
    }

    public static int sp2px(Context context, float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, context.getResources().getDisplayMetrics());
    }

    public static float px2sp(Context context, float pxVal) {
        return (pxVal / context.getResources().getDisplayMetrics().scaledDensity);
    }


    public static int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
