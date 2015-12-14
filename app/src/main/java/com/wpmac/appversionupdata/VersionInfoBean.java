/**
 *
 */
package com.wpmac.appversionupdata;


import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author T0227
 * @date 2015-12-1
 */
public class VersionInfoBean extends ResponseBean {


    public String success;
    public String msg;
    public VersionInfoDataBean data;

    public static VersionInfoBean VersionInfoBean(String response) throws JSONException {

        if (response == null || response.equals("") || response.equals("{}") || response.equals("null") || response.equals("[]")) {
            throw new JSONException("数据解析异常");
        }
        Gson gson = new Gson();
        VersionInfoBean model = new VersionInfoBean();
        JSONObject singleObject = new JSONObject(response);
        model = gson.fromJson(singleObject.toString(), VersionInfoBean.class);

        return model;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public VersionInfoDataBean getData() {
        return data;
    }

    public void setData(VersionInfoDataBean data) {
        this.data = data;
    }


}
