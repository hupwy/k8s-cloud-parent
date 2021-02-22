package com.itartisan.common.core.constant;

public class Constants {
    /**
     * 成功标记
     */
    public static final Integer SUCCESS = 200;

    /**
     * http请求
     */
    public static final String HTTP = "http://";

    /**
     * https请求
     */
    public static final String HTTPS = "https://";

    /**
     * 失败标记
     */
    public static final Integer FAIL = 500;

    /**
     * 登录成功
     */
    public static final String LOGIN_SUCCESS = "Success";

    /**
     * 注销
     */
    public static final String LOGOUT = "Logout";

    /**
     * 注册
     */
    public static final String REGISTER = "Register";

    /**
     * 登录失败
     */
    public static final String LOGIN_FAIL = "Error";

    /**
     * 可见性：公有
     */
    public static final String SCOPE_PUBLIC = "1";

    /**
     * 可见性：私有
     */
    public static final String SCOPE_PRIVATE = "0";

    /**
     * 令牌有效期（分钟）
     */
    public final static long TOKEN_EXPIRE = 720;

    /**
     * 字典管理 cache key
     */
    public static final String SYS_DICT_KEY = "sys_dict:";

    /**
     * 讨论区业务前缀
     */
    public static final String DISCUSSION_KEY = "discussion:";

    /**
     * 讨论区-浏览量上限
     */
    public static final int DISCUSSION_COUNT = 50;

    /**
     * 站内信状态 未读
     */
    public static final String SYS_MESSAGE_STATUS_UNREAD = "0";

    /**
     * 站内信状态 已读
     */
    public static final String SYS_MESSAGE_STATUS_READ = "1";

    /**
     * 逻辑删除 未删除
     */
    public static final String NOT_DELETED_FLAG = "0";

    /**
     * 逻辑删除 已删除
     */
    public static final String DELETED_FLAG = "2";
    /**
     * 校验成功
     */
    public static final String VLID_SUCCESS = "1";
    /**
     * 校验失败
     */
    public static final String VLID_ERROR = "0";

    /**
     * 教学模块
     */
    public enum Teach {
        /**
         * 课程状态
         */
        COURSE_STATUS_STORAGE("0", "暂存"),
        COURSE_STATUS_RELEASE("1", "发布"),
        COURSE_STATUS_SOLDOUT("2", "下架"),

        /**
         * 授课状态
         */
        TRACHING_STATUS_CREATED("0", "已创建"),
        TRACHING_STATUS_RUNNING("1", "授课中"),
        TRACHING_STATUS_OVER("2", "已结束"),

        /**
         * 学习任务状态
         */
        TRACHING_TASK_CREATED("0", "未开始"),
        TRACHING_TASK_RUNNING("1", "学习中"),
        TRACHING_TASK_OVER("2", "已完成"),

        /**
         * 课程课件范围
         */
        PUBLIC_COURSE("0", "公共课程"),
        PRIVATE_COURSE("1", "私有课程"),
        /**
         * 树节点类型标识（叶子/非叶子节点）
         */
        TREE_NODE_TYPE_LEAF("1", "是"),
        TREE_NODE_TYPE_UNLEAF("0", "否"),
        /**
         * 实验类型
         */
        MATERIAL_TYPE_LAB("0", "实验"),
        MATERIAL_TYPE_DOC("1", "文档"),
        MATERIAL_TYPE_VIDEO("2", "视频"),
        MATERIAL_TYPE_EXAM("3", "考试"),
        /**
         * 初始化实验版本
         */
        INIT_LAB_VERSION("1", "初始化实验版本");

        private String code;

        private String desc;

        Teach(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public String code() {
            return this.code;
        }

        public String desc() {
            return this.desc;
        }
    }

}
