package com.yiyou.gamesdk.core.ui.floatview;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobilegamebar.rsdk.outer.util.Log;
import com.yiyou.gamesdk.R;
import com.yiyou.gamesdk.core.CoreManager;
import com.yiyou.gamesdk.core.ui.widget.dialog.CommDialog;
import com.yiyou.gamesdk.core.ui.widget.dialog.MessageTipView;
import com.yiyou.gamesdk.model.AnnouncementInfo;
import com.yiyou.gamesdk.util.CommonUtils;
import com.yiyou.gamesdk.util.SystemHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Win on 2016/8/4.
 */
public class AnnouncementManager {

    protected static final String TAG = "AnnouncementManager";
    private WindowManager tipsWM;
    private WindowManager.LayoutParams tipsLayoutParams;
    private Context mContext;
    private LinearLayout mFloatLayout;
    private static AnnouncementManager instance;
    private TextView htmlText;
    private ImageView closeBtn;
    private boolean isShow = false;
    private Activity mActivity;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private AnnouncementInfo currentAnnouncement;
    private List<AnnouncementInfo> announcementList0 = new ArrayList<>();
    private List<AnnouncementInfo> announcementList1 = new ArrayList<>();
    private List<AnnouncementInfo> announcementWindows = new ArrayList<>();
    private List<CommDialog> dialogs = new ArrayList<>();

    /**1:滚动，可关闭  2:滚动，不可关闭*/
    private int mType = 0;

    public AnnouncementManager(Context context) {
        mContext = context;
        createFloatview();
    }

    public static synchronized AnnouncementManager getInstance(){
        if(instance == null){
            instance = new AnnouncementManager(CoreManager.getContext());
        }
        return instance;
    }

    private void createFloatview(){
        Log.d(TAG, "createFloatview: ");
        tipsWM = (WindowManager) mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        tipsLayoutParams = new WindowManager.LayoutParams();
        if (SystemHelper.Device.isBelowXiaomi2S() || SystemHelper.Device.isHongMi() || SystemHelper.Device.isXiaoMi()) {
            if (Build.VERSION.SDK_INT >= 19) {
                if(Build.VERSION.SDK_INT > 24){
                    tipsLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                }else{
                    tipsLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
                }
            } else {
                tipsLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            }
        } else {
            tipsLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        tipsLayoutParams.format = PixelFormat.RGBA_8888;
        tipsLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        tipsLayoutParams.gravity = Gravity.TOP;
        tipsLayoutParams.x = 0;
        tipsLayoutParams.y = 120;
        tipsLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        tipsLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        mFloatLayout = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.tt_sdk_bar_tips,null);
        htmlText = (TextView) mFloatLayout.findViewById(R.id.tv_tips);
        closeBtn = (ImageView) mFloatLayout.findViewById(R.id.back_icon);
    }

    public void initFloatView(){
        Log.d(TAG, "initFloatView: mType: " + mType);
        if (currentAnnouncement == null){
            if (isShow){
                tipsWM.removeView(mFloatLayout);
                isShow = false;
            }
            return;
        }
        Spannable sp = (Spannable) Html.fromHtml(currentAnnouncement.getContent());
        final URLSpan[] urls = sp.getSpans(0, sp.length(), URLSpan.class);
        if (urls.length > 0){
            currentAnnouncement.setUrl(urls[0].getURL());
        }else{
            currentAnnouncement.setUrl("");
        }
        htmlText.setText(sp);
        htmlText.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        htmlText.setSelected(true);
        htmlText.setFocusable(true);
        htmlText.setFocusableInTouchMode(true);
        htmlText.setClickable(true);
        htmlText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonUtils.handleUrl(currentAnnouncement.getUrl(), mActivity, true)){
                    uninit();
                }
            }
        });
        if(mType ==1){
            closeBtn.setVisibility(View.VISIBLE);
            closeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentAnnouncement = null;
                    if (!nextAnnouncement()){
                        tryUninit();
                    }
                }
            });
        }else if(mType == 2){
            closeBtn.setVisibility(View.GONE);
            mHandler.postDelayed(runable,20000);
        }
    }

    /**
     * 添加公告
     * @param info 公告
     */
    public void addAnnouncement(AnnouncementInfo info){
        if (info.getType() == 3){
            announcementList0.add(info);
        }else if (info.getType() == 4){
            announcementList1.add(info);
        }else if (info.getType() == 1 || info.getType() == 2){
            announcementWindows.add(info);
        }

    }



//    private boolean handleUrl(String url){
//        if (TextUtils.isEmpty(url)){
//            return false;
//        }
//        if (url.contains("tzsy://")){
////            if(!PackageUtil.isPkgInstalled(mContext,"com.sjyx8.tzsy.debug")){
//            if(! (PackageUtil.isPkgInstalled(mContext,"com.sjyx8.tzsy")&&PackageUtil.isPkgInstalled(mContext,"com.sjyx8.tzsy.debug"))){
//                ToastUtils.showMsg("未安装桃子手游APP");
//                return false;
//            }
//            int gameId = ApiFacade.getInstance().getCurrentGameID();
//            try {
//                Uri uri = Uri.parse("tzsy://gameInfo/detail");
//                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                intent.putExtra("game_id", gameId);
//
//                mActivity.startActivity(intent);
//                return true;
//            }catch (Exception e){
//                e.printStackTrace();
//                ToastUtils.showMsg("没有可打开的页面！");
//                return false;
//            }
//        }else {
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setData(Uri.parse(url));
//            intent.addCategory("android.intent.category.DEFAULT");
//            intent.addCategory("android.intent.category.BROWSABLE");
//            mActivity.startActivity(intent);
//            return true;
//        }
//    }

    public boolean nextAnnouncement(){
        boolean flag = updateList();
        initFloatView();
        return flag;
    }


    public void show(Activity activity){
        mActivity = activity;
        if(!isShow && nextAnnouncement()){
            isShow = true;
            tipsWM.addView(mFloatLayout,tipsLayoutParams);
            Log.d(TAG,"show mType: " + mType);
        }
        for (AnnouncementInfo info : announcementWindows){
            popupWindow(info);
        }
    }
    public void hide(){
        if(isShow){
            isShow = false;
            tipsWM.removeView(mFloatLayout);
            Log.d(TAG,"hide");
        }
        for (CommDialog dialog : dialogs){
            dialog.dismiss();
        }
    }

    public boolean isShowing(){
        return isShow;
    }


    public boolean updateList(){
        Log.d(TAG, "updateList: " + announcementList0.size() + "; " + announcementList1.size());
        if (announcementList0.size() > 0){
            currentAnnouncement = announcementList0.get(announcementList0.size()-1);
            announcementList0.remove(announcementList0.size()-1);
            mType = 1;
            return true;
        }else if (announcementList1.size() > 0){
            currentAnnouncement = announcementList1.get(announcementList1.size()-1);
            announcementList1.remove(announcementList1.size()-1);
            mType = 2;
            return true;
        }

        return false;

    }

    public void uninit(){
        hide();
        clearList();
        currentAnnouncement = null;
        isShow = false;
        mHandler.removeCallbacksAndMessages(null);
        instance = null;
        mActivity = null;
        Log.d(TAG,"uninit");
    }

    public void clearList(){
        announcementList0.clear();
        announcementList1.clear();
        announcementWindows.clear();
        Log.d(TAG,"clearList");
    }

    private Runnable runable = new Runnable() {
        @Override
        public void run() {
            currentAnnouncement = null;
            updateList();
            initFloatView();
            if (currentAnnouncement == null){
                mHandler.removeCallbacksAndMessages(null);
                tryUninit();
            }
        }
    };

    private void popupWindow(AnnouncementInfo MessageInfo){
        if (MessageInfo.getType() == 2) {//normal = NORMAL_WITHOUT_BTN
            CommDialog.Builder builder = new CommDialog.Builder(mActivity);
            MessageTipView dialogView = new MessageTipView(mContext, 0, mActivity);
            dialogView.setTitle(MessageInfo.getTitle());
            dialogView.setWebView(MessageInfo.getUrl());
            dialogView.disableclose(false);
            builder.setFullScreen(true);
            dialogView.respondHyperlink(mActivity);
            builder.setView(dialogView);
            final CommDialog msgDialog = builder.create();
            msgDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    dialogs.remove(msgDialog);
                    tryUninit();
                }
            });
            msgDialog.show();
            dialogs.add(msgDialog);
            Log.d(TAG, "MessageInfo NORMAL_WITHOUT_BTN: show ");
        }
        if (MessageInfo.getType() == 1) {//Accurate = SMALL
            final CommDialog accurateDialog;
            CommDialog.Builder builder = new CommDialog.Builder(mActivity);
            MessageTipView dialogView = new MessageTipView(mContext, 1, mActivity);
            dialogView.setTitle(MessageInfo.getTitle());
            dialogView.setWebView(MessageInfo.getUrl());
            dialogView.respondHyperlink(mActivity);
            dialogView.disableclose(false);
            builder.setFullScreen(true);
            builder.setView(dialogView);
            accurateDialog = builder.create();
            accurateDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    dialogs.remove(accurateDialog);
                    tryUninit();
                }
            });
            accurateDialog.show();
            dialogs.add(accurateDialog);
            Log.d(TAG, "MessageInfo SMALL: show ");
        }
    }

    private void tryUninit(){
        Log.d(TAG, "tryUninit: " + announcementList0.size() + "; " + announcementList1.size() + "; "+dialogs.size());
        if (announcementList0.size() == 0 && announcementList1.size() == 0 && dialogs.size() == 0 && currentAnnouncement == null){
            uninit();
        }
    }
}
