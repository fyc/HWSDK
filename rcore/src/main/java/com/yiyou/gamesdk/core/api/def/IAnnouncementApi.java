package com.yiyou.gamesdk.core.api.def;

import com.mobilegamebar.rsdk.outer.IOperateCallback;
import com.yiyou.gamesdk.model.AnnouncementInfo;

import java.util.List;

/**
 *
 * Created by Nekomimi on 2017/6/10.
 */

public interface IAnnouncementApi extends IApiWrapping {

    void requestAnnouncement(int from, IOperateCallback<List<AnnouncementInfo>> callback);

    List<AnnouncementInfo> getLocalAnnouncement();

}
