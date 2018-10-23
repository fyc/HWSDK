package com.yiyou.gamesdk.core.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.yiyou.gamesdk.R;

/**
 * Created by chenshuide on 15/6/23.
 */
public class MultiStateEditText extends RelativeLayout {
    private int normalBg, errorBg, hintres, drawableleft;
    private Button button;
    private View stateView;
    private DrawableEditText editText;
    private ImageView iv_delete;
    private int customRes = 0;
    public static final int TEXT_PASSWD = 10001;
    public static final int TEXT_NORMAL = 10002;
    public static final int TEXT_NUMBER = 10003;
    public static final int TEXT_PHONE = 10004;
    public static final int TEXT_VERIFY_CODE = 10005;   //验证码
    private int textType = TEXT_NORMAL;//default

    public MultiStateEditText(Context context) {
        this(context, null);
    }

    public MultiStateEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        normalBg = R.drawable.tt_sdk_account_input_normal;
        errorBg = R.drawable.tt_sdk_account_input_error;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MultiStateEditText);
        hintres = a.getResourceId(R.styleable.MultiStateEditText_hint, 0);
        drawableleft = a.getResourceId(R.styleable.MultiStateEditText_drawableLeft, 0);
        a.recycle();

        init();
    }


    public void setTextType(int type){
        textType = type;

        switch (type){
            case TEXT_NORMAL:
                editText.setInputType(EditorInfo.TYPE_CLASS_TEXT);
                break;

            case TEXT_PASSWD:
                editText.setInputType(EditorInfo.TYPE_CLASS_TEXT|EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
                break;
            case TEXT_NUMBER:
                editText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
                break;

            case TEXT_VERIFY_CODE:
                editText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
                editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
                break;

            case TEXT_PHONE:
                editText.setInputType(EditorInfo.TYPE_CLASS_PHONE);
                editText.setFilters(new InputFilter[]{
                        new InputFilter.LengthFilter(11)

                });
                break;
        }
    }


    public EditText obtainEdit() {
        return editText;
    }

    private void init() {

        inflate(getContext(), R.layout.tt_sdk_view_account_multistate_edittext, this);
        editText = (DrawableEditText) findViewById(R.id.input);
        iv_delete = (ImageView) findViewById(R.id.iv_delete);
        if (hintres != 0)
            editText.setHint(hintres);

        if (drawableleft != 0)
            editText.setDrawableLeft(drawableleft);



        iv_delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });

        editText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int length = s.length();

                iv_delete.setVisibility(length > 0 ? VISIBLE : INVISIBLE);

                if (length == 0 && stateView != null)
                    setNormal(stateView,customRes);


                if (button == null)
                    return;

                button.setEnabled(length > 0);


            }
        });

    }

    public void setButton(Button button) {
        this.button = button;
    }

    public void setNormal(View stateView) {
        setNormal(stateView, 0);
    }

    public void setNormal(View stateView, int res) {
        this.stateView = stateView;
        customRes = res;
        stateView.setBackgroundResource(res == 0 ? normalBg : res);
    }

    public void setError(View stateView) {
        setError(stateView, 0);
    }

    public void setError(View stateView, int res) {
        stateView.setBackgroundResource(res == 0 ? errorBg : res);
    }


}
