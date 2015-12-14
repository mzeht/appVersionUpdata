package com.wpmac.appversionupdata;

/**
 * 常量定义类
 *
 * @author 陈玉伟
 *         2015-10-10 上午11:43:30
 */
public class Constants {
    //页面标题
    public static final String KEY_FRAGMENT_TITLE = "title";
    public static final int ZOOM_LEVEL = 5;

    public static final String METHOD = "method";
    public static final String SOURCE = "android";
    public static Boolean IS_HAVA_NEW_VERSION = false;
    //服务器地址
    public static final String SERVER_URL = "http://172.16.2.157:8080/xj-pc/";
    public static final String SERVER_HTML_URL = "http://172.16.2.157:8080/xj-wx/";
    //public static final String  SERVER_URL="http://www.junong365.cn/xj-pc/";
    //public static final String  SERVER_HTML_URL="http://www.junong365.cn/";


    //public static final String  SERVER_URL="http://10.10.168.94:8080/xj-pc/";


    //默认数据更新时间 1分钟
    public static final long defaultUpdateIntervalTime = 1 * 60 * 1000;


    public static final int TILE_SIZE = 256;

    /**
     * 地图模式
     *
     * @author Administrator
     */
    public enum ModeMap {
        MODE_RADAR, //雷达模式
        MODE_STATION//站点模式
    }


    /**
     * 无效值
     */
    public static final int INVALID_VALUE = 10000;
    public static final String DEFAULT_INVALID_VALUE = "10000";
    public static final String WEATHER_INVALID = "--";


    public static final String ACTION_ROOM_CHOOSE_PICTURE = "com.gd.hixin.im.intent.action.choose_picture";

    /**
     * 验证码类型
     */
    public static final String MODIFYCODE = "modifyCode";
    public static final String RECEIPTCODE = "receiptCode";
    public static final String BACKCODE = "backCode";

    /**
     * 下载文件名称
     */
    public static String VERSION_DOWNLOAD_NAME = "JuNong";
    /**
     * 版本下载存放位置
     */
    public static final String VERSION_DOWNLOAD_DIR = "JuNong/";

    /**
     * 更新文件下载路径
     */
    public static String VERSION_DOWNLOAD_URL = "";

    /**
     * 版本名称
     */
    public static int CURRENT_VERSION_CODE = 1;

    /**
     * 新版本描述
     */
    public static String VERSION_DESCRIPTION = "";


}
