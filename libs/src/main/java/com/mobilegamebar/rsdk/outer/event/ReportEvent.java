package com.mobilegamebar.rsdk.outer.event;

import android.support.v4.util.ArrayMap;

import java.util.Map;

/**
 * Created by levyyoung on 15/6/8.
 */
public class ReportEvent {

    public static final String TYPE_SUBMIT_ROLE_INFO = "com.yiyou.gamesdk.event.type.submitRoleInfo";
    public static final String TYPE_SUBMIT_EXTEND_DATA = "com.yiyou.gamesdk.event.type.submitExtendData";
    public static final String TYPE_REPORT_ACTIVE = "com.yiyou.gamesdk.event.type.report_active";
    public static class ReportEventParam {
        public Map<String, String> data = new ArrayMap<>();

        public ReportEventParam() {
        }

        public ReportEventParam(Map<String, String> data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return "ReportEventParam{" +
                    "data=" + data +
                    '}';
        }
    }
}
