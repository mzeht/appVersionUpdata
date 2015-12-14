package com.wpmac.appversionupdata;

import org.json.JSONException;

import java.io.Serializable;

/**
 * 结果进行封装的抽象类
 */
public abstract class ResponseBean implements Serializable {

    public String response;
    //

    public ResponseBean() {

    }

    public ResponseBean(String response) throws JSONException {
        // TODO Auto-generated constructor stub

        if (response == null || response.equals("") || response.equals("null"))
            throw new JSONException("数据解析异常");

        this.response = response;

    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }

    /**
     * 保存
     */
    protected void saveModel() {
        L.i("save_model");
    }

    /**
     * 更新
     */
    protected void updateModel() {
        L.i("update_model");
    }


    /**
     * 删除
     */
    protected void deleteModel() {
        L.i("delete_model");
    }


}
