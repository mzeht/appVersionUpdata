package com.wpmac.appversionupdata;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.RemoteViews;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class AppUdateService extends Service {

    private static final int TIMEOUT = 10 * 1000;
    private String down_url = ""; // 下载路径
    private static final int DOWN_OK = 1; // 下载成功标识
    private static final int DOWN_ERROR = 0; // 下载失败标识
    private String app_name; // 应用名称

    private NotificationManager notificationManager; // 通知管理器
    private Notification notification; // 消息通知对象

    private Intent updateIntent; // 更新intent
    private PendingIntent pendingIntent; // intent参数

    private int notification_id = 0; // 通知id


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int codenum = 0;
        if (intent != null) {
            app_name = intent.getStringExtra("app_name");
            codenum = intent.getIntExtra("ver_code", 0);
        } else {
            app_name = Constants.VERSION_DOWNLOAD_NAME;
            codenum = Constants.CURRENT_VERSION_CODE;
        }

        down_url = Constants.VERSION_DOWNLOAD_URL;

        File updatefile = new File(Environment.getExternalStorageDirectory()
                + "/" + Constants.VERSION_DOWNLOAD_DIR + "/" + app_name + ".apk");

        //	if(codenum - Constants.CURRENT_VERSION_CODE > 1){
        //删除安装文件
        if (updatefile != null && updatefile.exists()) {
            updatefile.delete();
        }
        //}

        if (!updatefile.exists()) {                //如果不存在就下载生成
            ALFileUtil.createFile(app_name);// 创建文件
            createNotification();// 首次创建
            createThread();// 线程下载
        } else {                //存在则直接安装
            ALFileUtil.updateDir = new File(Environment.getExternalStorageDirectory()
                    + "/" + Constants.VERSION_DOWNLOAD_DIR);
            ALFileUtil.updateFile = new File(ALFileUtil.updateDir + "/" + app_name + ".apk");

            updateIntent = new Intent(Intent.ACTION_VIEW);
            updateIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

            installAPKFile(false);
        }

        return super.onStartCommand(intent, flags, startId);

    }

    // 创建线程下载
    public void createThread() {
        // 下载成功处理事件
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                ALProgressRing.unProgeress();
                switch (msg.what) {
                    case DOWN_OK:
                        installAPKFile(true);
                        break;
                    case DOWN_ERROR:
                        if (Context.NOTIFICATION_SERVICE != null) {
                            String ns = Context.NOTIFICATION_SERVICE;
                            NotificationManager nMgr = (NotificationManager) getApplicationContext().getSystemService(ns);
                            nMgr.cancel(0);
                        }
                        //notification.setLatestEventInfo(ALUpdateService.this, app_name, "下载失败", pendingIntent);
                        stopService(updateIntent);
                        break;

                    default:
                        stopService(updateIntent);
                        break;
                }

            }

        };

        final Message message = new Message();

        // 创建线程下载
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    long downloadSize = downloadUpdateFile(down_url, ALFileUtil.updateFile.toString());
                    if (downloadSize > 0) {
                        message.what = DOWN_OK;
                        handler.sendMessage(message);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    message.what = DOWN_ERROR;
                    handler.sendMessage(message);
                }

            }
        }).start();
    }

    private void installAPKFile(boolean downloaded) {
        Uri uri = Uri.fromFile(ALFileUtil.updateFile);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");

        pendingIntent = PendingIntent.getActivity(AppUdateService.this, 0, intent, 0);

        if (downloaded) {
//			notification.setLatestEventInfo(AppUdateService.this, app_name, "下载成功，点击安装", pendingIntent);
            notification = new Notification.Builder(getApplicationContext())
                    .setContentTitle(app_name)
                    .setContentText("下载成功，点击安装")
                    .setContentIntent(pendingIntent)
                    .build();
            notificationManager.notify(notification_id, notification);
        }

        stopService(updateIntent);
        // Activity myActivity =
        // ((MyApplication)getApplication()).getInstance();
        // myActivity.finish();
        updateApplication();
    }

    // 更新应用
    private void updateApplication() {
        // Thread thread = new Thread(showUpdate);
        // thread.start();
        // 安装应用
        Intent intent = new Intent(Intent.ACTION_VIEW);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String readNAme = Constants.VERSION_DOWNLOAD_NAME;
        if (!readNAme.contains(".apk"))
            readNAme = Constants.VERSION_DOWNLOAD_DIR + readNAme + ".apk";

        String fileName = Environment.getExternalStorageDirectory() + "/" + readNAme;// 从SdCard中该文件的文件名
        intent.setDataAndType(Uri.parse("file://" + fileName), "application/vnd.android.package-archive");

        startActivity(intent);

//		Activity myActivity = ((EimApplication) getApplication()).getCurrentActivity();
//		if (myActivity != null){
//			myActivity.startActivity(intent);
//		}
    }

    RemoteViews contentView;

    // 创建通知
    public void createNotification() {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notification = new Notification();
        notification.icon = R.mipmap.ic_launcher;// 这个图标必须要设置，不然下面那个RemoteViews不起作用.
        // 这个参数是通知提示闪出来的值.
        notification.tickerText = "开始下载";

//		Activity myActivity = ((EimApplication) getApplication()).getCurrentActivity();
//		if (myActivity != null)
//			ALProgressRing.onProgeress(myActivity, "提示", "正在下载...", 30000);

        // 在这里我们用自定的view来显示Notification
        contentView = new RemoteViews(getPackageName(), R.layout.al_notification_item);
        contentView.setTextViewText(R.id.notificationTitle, "正在下载");
        contentView.setTextViewText(R.id.notificationPercent, "0%");
        contentView.setProgressBar(R.id.notificationProgress, 100, 0, false);

        notification.contentView = contentView;

        //updateIntent = new Intent(this, Login.class);
        updateIntent = new Intent(Intent.ACTION_MAIN);
        updateIntent.addCategory(Intent.CATEGORY_HOME);
        updateIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, updateIntent, 0);

        notification.contentIntent = pendingIntent;

        notificationManager.notify(notification_id, notification);

    }

    /***
     * 下载文件
     *
     * @return
     * @throws MalformedURLException
     */
    public long downloadUpdateFile(String down_url, String file) throws Exception {
        int down_step = 5;// 提示step
        int totalSize;// 文件总大小
        int downloadCount = 0;// 已经下载好的大小
        int updateCount = 0;// 已经上传的文件大小
        InputStream inputStream;
        OutputStream outputStream;

        URL url = new URL(down_url);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setConnectTimeout(TIMEOUT);
        httpURLConnection.setReadTimeout(TIMEOUT);
        // 获取下载文件的size
        totalSize = httpURLConnection.getContentLength();
        if (httpURLConnection.getResponseCode() == 404) {
            throw new Exception("fail!");
        }
        inputStream = httpURLConnection.getInputStream();
        outputStream = new FileOutputStream(file, false);// 文件存在则覆盖掉
        byte buffer[] = new byte[8192];
        int readsize = 0;
        while ((readsize = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, readsize);
            downloadCount += readsize;// 时时获取下载到的大小
            /**
             * 每次增张5%
             */
            if (updateCount == 0 || (downloadCount * 100 / totalSize - down_step) >= updateCount) {
                updateCount += down_step;
                // 改变通知栏
                // notification.setLatestEventInfo(this, "正在下载...", updateCount
                // + "%" + "", pendingIntent);
                contentView.setTextViewText(R.id.notificationPercent, updateCount + "%");
                contentView.setProgressBar(R.id.notificationProgress, 100, updateCount, false);
                // show_view
                notificationManager.notify(notification_id, notification);

            }

        }
        if (httpURLConnection != null) {
            httpURLConnection.disconnect();
        }
        inputStream.close();
        outputStream.close();

        return downloadCount;

    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

    }


    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

}
