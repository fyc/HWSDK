package com.yiyou.gamesdk.core.base.http.volley.bean;

public class VerifyCodeBean {

    /**
     * code : 1
     * data : {"ctime":60}
     * msg : 验证码发送成功.
     */

    private int code;
    private DataBean data;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class DataBean {
        /**
         * ctime : 60
         */

        private int ctime;

        public int getCtime() {
            return ctime;
        }

        public void setCtime(int ctime) {
            this.ctime = ctime;
        }
    }
}
