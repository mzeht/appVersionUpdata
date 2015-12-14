package com.wpmac.appversionupdata;

import org.json.JSONException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wpmac on 15/12/14.
 */
public class ConnectManager {
    private static ConnectManager mControlManager;

    public boolean mConnectionState = false;

    public Object lock = new Object();
    // 线程池
    private ExecutorService exector;

    private String json_error = "获取数据失败!", network_error = "网络异常!";

    private ConnectManager() {
        exector = Executors.newFixedThreadPool(5);

    }

    /**
     * 控制器单例
     *
     * @return
     */
    public static ConnectManager getInstance() {
        if (mControlManager == null) {
            mControlManager = new ConnectManager();
        }
        return mControlManager;
    }

    /**
     * 请求版本信息
     *
     * @param params
     * @param callback
     */
    public void VersionData(
            final VersionParam params,
            final AbstractRequestListener<VersionInfoBean> callback) {

        exector.execute(new Runnable() {

            @Override
            public void run() {

                try {
                    String method = params.getParam().getString("method");
                    String response = HttpConnectionUtil.openUrl(
                            Constants.SERVER_URL + method, "GET", params.getParam());
                    VersionInfoBean bean = VersionInfoBean.VersionInfoBean(response);
                    if (callback != null) {
                        callback.onComplete(bean);
                    }
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    if (callback != null) {
                        callback.onFault(new CustomError(network_error));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (callback != null) {
                        callback.onError(new CustomError(json_error));
                    }
                }
            }
        });
    }


}
