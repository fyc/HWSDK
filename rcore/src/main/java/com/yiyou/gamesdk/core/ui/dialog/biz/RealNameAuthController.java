package com.yiyou.gamesdk.core.ui.dialog.biz;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mobilegamebar.rsdk.outer.event.IDialogParam;
import com.yiyou.gamesdk.R;
import com.yiyou.gamesdk.util.IMEUtil;
import com.yiyou.gamesdk.util.ViewUtils;

public class RealNameAuthController extends BaseAuthViewController {

    private TextView titleTv;
    private EditText edit_real_name_container_name;
    private EditText edit_real_name_container_card_number;
    private Button btn_real_name_container_ensure;
    private Button btn_title_container_close;

    Context mContext;

    public RealNameAuthController(Context context, IDialogParam params) {
        super(context, params);
        mContext = context;
        initView();
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.tt_sdk_container_real_name;
    }

    @Override
    public void onShow() {

    }

    @Override
    public void onHide() {

    }

    private void initView() {
        titleTv = (TextView) findViewById(R.id.tv_title_container_title);
        titleTv.setText(R.string.str_real_name_title);
        edit_real_name_container_name = (EditText) findViewById(R.id.edit_real_name_container_name);
        edit_real_name_container_card_number = (EditText) findViewById(R.id.edit_real_name_container_card_number);
        btn_real_name_container_ensure = (Button) findViewById(R.id.btn_real_name_container_ensure);
        ViewUtils.setViewEnable(btn_real_name_container_ensure, false);
        btn_title_container_close = (Button) findViewById(R.id.btn_title_container_close);
        btn_title_container_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
        btn_real_name_container_ensure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                IMEUtil.hideIME(RealNameAuthController.this);
            }
        });

        addTextWatcher(edit_real_name_container_name, edit_real_name_container_card_number);
    }

    private void addTextWatcher(EditText... editTexts) {
        for (final EditText editText : editTexts) {
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    updateLoginButtonState();
                }
            });
        }
    }

    private void updateLoginButtonState() {
        if (edit_real_name_container_name.length() == 0 || edit_real_name_container_card_number.length() == 0) {
            ViewUtils.setViewEnable(btn_real_name_container_ensure, false);
        } else {
            ViewUtils.setViewEnable(btn_real_name_container_ensure, true);
        }
    }
}
