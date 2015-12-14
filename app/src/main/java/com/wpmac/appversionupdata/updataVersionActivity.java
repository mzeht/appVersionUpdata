package com.wpmac.appversionupdata;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

/**
 * Created by wpmac on 15/12/14.
 */
public class updataVersionActivity extends Activity {

    private Button checkUpdataButton;
    private VersionInfoBean versionInfoBean;
    private int versioncode;
    private Dialog versionUpdataDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);
        initview();
        setlistener();


    }

    Handler vHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ActivityForResultUtil.REQUEST_DATA_SUCCESS:
                    if (versionInfoBean.getSuccess().equals("true")) {
                        try {
//						Constants.CURRENT_VERSION_CODE=2;
                            Constants.CURRENT_VERSION_CODE = Integer.valueOf(versionInfoBean.getData().getVersion());
                            Constants.VERSION_DOWNLOAD_URL = versionInfoBean.getData().getUrl();
                        } catch (Exception e) {
                            Constants.IS_HAVA_NEW_VERSION = false;
                        }
                        try {
                            versioncode = VersionUtil.getVersionCode(getApplicationContext());
                        } catch (Exception e) {
                            Constants.IS_HAVA_NEW_VERSION = false;
                        }
                        Constants.IS_HAVA_NEW_VERSION = Constants.CURRENT_VERSION_CODE > versioncode;
                    } else {
                        Constants.IS_HAVA_NEW_VERSION = false;
                    }
                    System.out.println("local version code:" + versioncode);
                    break;
                case ActivityForResultUtil.REQUEST_DATA_ERROR:

                    if (msg.obj instanceof CustomError) {
                        CustomError fault = (CustomError) msg.obj;
                    }
                    break;
                case ActivityForResultUtil.REQUEST_DATA_FAULT:
                    if (msg.obj instanceof CustomError) {
                        CustomError fault = (CustomError) msg.obj;
                    }
                    break;
            }
            checkVersionUpdate();
        }
    };

    private void checkVersionUpdate() {
        if (Constants.IS_HAVA_NEW_VERSION) {
            L.i("update", "hava new version");
            creatDialog();
        } else {
            L.i("updata", "no new version");
            creatDialog();
        }

    }

    private void creatDialog() {
        versionUpdataDialog = new Dialog(updataVersionActivity.this, R.style.dialog);
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.updata_dialog_layout, null);
        Button updata = (Button) dialogView.findViewById(R.id.verson_update_button);
        Button cancel = (Button) dialogView.findViewById(R.id.verson_cancel_button);
        versionUpdataDialog.setContentView(dialogView);
        versionUpdataDialog.show();

        updata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent updateIntent = new Intent(updataVersionActivity.this, AppUdateService.class);
                updateIntent.putExtra("app_name", Constants.VERSION_DOWNLOAD_NAME);
                updateIntent.putExtra("ver_code", Constants.CURRENT_VERSION_CODE);
                startService(updateIntent);
                versionUpdataDialog.cancel();

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                versionUpdataDialog.cancel();
            }
        });

    }

    private void setlistener() {
        checkUpdataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                requesVesion();

            }
        });
    }

    private void requesVesion() {

        VersionParam param = new VersionParam();
        param.platform = "Android";
        ConnectManager.getInstance().VersionData(param, versioncallback);


    }

    private AbstractRequestListener<VersionInfoBean> versioncallback = new AbstractRequestListener<VersionInfoBean>() {
        @Override
        public void onComplete(VersionInfoBean bean) {
            versionInfoBean = bean;
            vHandler.sendEmptyMessage(ActivityForResultUtil.REQUEST_DATA_SUCCESS);
        }

        @Override
        public void onError(CustomError customError) {
            Message msg = vHandler.obtainMessage();
            msg.obj = customError;
            msg.what = ActivityForResultUtil.REQUEST_DATA_ERROR;
            vHandler.sendMessage(msg);

        }

        @Override
        public void onFault(CustomError fault) {
            Message msg = vHandler.obtainMessage();
            msg.obj = fault;
            msg.what = ActivityForResultUtil.REQUEST_DATA_FAULT;
            vHandler.sendMessage(msg);

        }
    };

    private void initview() {
        checkUpdataButton = (Button) findViewById(R.id.check_updata_button);
    }
}
