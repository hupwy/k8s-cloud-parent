package com.itartisan.common.core.constant;

public class MsgCodeContants {

    public enum TeachCode {
        /**
         * 信息返回码
         */
        NAME_EXIST("1001"),
        NUM_EXIST("1002"),
        UNIT_KEY_EXIST("1003");

        private String code;

        TeachCode(String code) {
            this.code = code;
        }

        public String code() {
            return this.code;
        }
    }
}
