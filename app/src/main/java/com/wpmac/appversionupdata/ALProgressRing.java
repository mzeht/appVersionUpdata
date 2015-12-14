package com.wpmac.appversionupdata;
/*
 * author:eity
 * version:2013-3-1
 * description:�����
 * */

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;

public class ALProgressRing {
    private static ProgressDialog pd;
    static Dialog d;
    static Thread thread;

    //��ʾ�����
    public static void onProgeress(Context context, String title, String msg, long time) {
        if (time < 5000) {
            time = 10000;
        }
        final long t = time;
//		if(pd.isShowing())	return;

        pd = ProgressDialog.show(context, title, msg, true, false);
        Runnable runnable = new Runnable() {

            @SuppressWarnings("static-access")
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    thread.sleep(t);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                unProgeress();
            }
        };
        thread = new Thread(runnable);
        thread.start();
    }

    public static void onProgeress(Context context, String title, String msg) {
//		if(pd.isShowing())	return;

        pd = ProgressDialog.show(context, title, msg, true, false);
    }

    //���ؽ����
    public static void unProgeress() {
//		thread.stop();
        if (pd != null) pd.dismiss();
    }
}