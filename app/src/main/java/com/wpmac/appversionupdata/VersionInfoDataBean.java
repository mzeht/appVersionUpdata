/**
 *
 */
package com.wpmac.appversionupdata;


/**
 * @author T0227
 * @date 2015-12-1
 */
public class VersionInfoDataBean extends ResponseBean {


    public String app_id;//":"app_id" ,
    public String platform;//": "应用平台",
    public String version;//": "版本号",
    public String url;//": "地址",

    //    "version_desc": "描述",
//    "t_date": "变更日期"
    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


}
