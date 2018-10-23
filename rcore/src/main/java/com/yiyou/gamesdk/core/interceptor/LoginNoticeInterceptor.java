package com.yiyou.gamesdk.core.interceptor;

import com.mobilegamebar.rsdk.outer.IOperateCallback;
import com.mobilegamebar.rsdk.outer.consts.TTCodeDef;
import com.yiyou.gamesdk.core.api.ApiFacade;
import com.yiyou.gamesdk.core.ui.floatview.AnnouncementManager;
import com.yiyou.gamesdk.core.ui.widget.dialog.CommDialog;
import com.yiyou.gamesdk.model.AnnouncementInfo;

import java.util.List;

/**
 * login 公告拦截器
 * Created by charles on 12/7/16.
 */
public class LoginNoticeInterceptor implements LoginInterceptor {

    private static final String TAG = "LoginNoticeInterceptor";

    private CommDialog msgDialog;
    private CommDialog LockmsgDialog;
    private CommDialog DownloadDialog;

    @Override
    public void intercept(final Chain<LoginParams> chain) {
        if (chain.getData().getCode() != TTCodeDef.SUCCESS) {
            chain.proceed(chain.getData());
            return;
        }
        ApiFacade.getInstance().requestAnnouncement(1, new IOperateCallback<List<AnnouncementInfo>>() {
            @Override
            public void onResult(int i, List<AnnouncementInfo> announcementInfos) {
                if (announcementInfos != null && announcementInfos.size() > 0){
                    for (AnnouncementInfo announcementInfo : announcementInfos){
                        AnnouncementManager.getInstance().addAnnouncement(announcementInfo);
                    }
                    AnnouncementManager.getInstance().show(chain.getData().getLoginParams().activity);
                }
           }
        });
//
//        List<AnnouncementInfo> tests = new ArrayList<>();
//        AnnouncementInfo info1 = new AnnouncementInfo();
//        info1.setId(1);
//        info1.setTitle("test1");
//        info1.setType(1);
//        info1.setUrl("https://www.baidu.com/");
//        AnnouncementInfo info2 = new AnnouncementInfo();
//        info2.setUrl("https://www.baidu.com/");
//        info2.setTitle("test2");
//        info2.setType(2);
//        info2.setId(2);
//        AnnouncementInfo info3 = new AnnouncementInfo();
//        info3.setId(3);
//        info3.setType(7);
//        info3.setTitle("123456498456456s4af5646a1fd1fa4f65s89d7af2s4f1a3fd4as5d4fa1dfa1df567sa8d7fsa6d1f3a21fd65as7f8sa4fsda4f32sda1f78as64f");
//        AnnouncementInfo info4 = new AnnouncementInfo();
//        info4.setId(4);
//        info4.setType(7);
//        info4.setTitle("12345678");
//        AnnouncementInfo info5 = new AnnouncementInfo();
//        info5.setId(5);
//        info5.setType(8);
//        info5.setTitle("12345678");
//        AnnouncementInfo info6 = new AnnouncementInfo();
//        info6.setId(6);
//        info6.setType(8);
//        info6.setTitle("123456784564sdf65a4fd65asf4d6sa5f4as56dfdsff5dsa32f1ds5af487daf6s4adfsa3d15c87sdaf456ad12z1v5d7sa89d4f65da4df3sd1fz8xv7");
//        tests.add(info1);
//        tests.add(info2);
//        tests.add(info4);
//        tests.add(info3);
//        tests.add(info5);
//        tests.add(info6);
//        for (AnnouncementInfo announcementInfo : tests) {
//            AnnouncementManager.getInstance().addAnnouncement(announcementInfo);
//        }
//        AnnouncementManager.getInstance().show(chain.getData().getLoginParams().activity);


        chain.proceed(chain.getData());
    }


}







