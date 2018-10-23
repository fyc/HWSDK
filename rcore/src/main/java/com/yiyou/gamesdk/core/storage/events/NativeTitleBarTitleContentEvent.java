package com.yiyou.gamesdk.core.storage.events;



public class NativeTitleBarTitleContentEvent {

    public static final String TYPE_ON_NATIVE_TITLE_BAR_TITLE_CONTENT = "com.yiyou.gamesdk.events.type.nativetitlebartitlecontent";

    public static class NativeTitleBarTitleContentEventParam {
        public String mTitleContent ;

        public NativeTitleBarTitleContentEventParam() {
        }

        public NativeTitleBarTitleContentEventParam(String titleContent) {
            this.mTitleContent = titleContent;
        }

        @Override
        public String toString() {
            return "NativeTitleBarTitleContentEvent{" +
                    "mTitleContent=" +mTitleContent +
                    '}';
        }
    }
}
