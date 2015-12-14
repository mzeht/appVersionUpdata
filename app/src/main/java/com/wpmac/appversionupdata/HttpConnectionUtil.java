package com.wpmac.appversionupdata;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * 网络工具类
 *
 * @author 陈玉伟
 *         2015-9-24 下午2:11:38
 */
public class HttpConnectionUtil {

    public static final String LOG_TAG = "Connection:";


    private static HttpURLConnection openConn(String url, String method,
                                              Bundle params) {
        if (method.equals("GET")) {
            url = url + "?" + encodeUrl(params);
        }
        try {
            Log.d(LOG_TAG, method + " URL: " + url);
            HttpURLConnection conn = (HttpURLConnection) new URL(url)
                    .openConnection();
            conn.setConnectTimeout(20000);
            if (!method.equals("GET")) {
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.getOutputStream().write(
                        encodeUrl(params).toString().getBytes());
            }
            return conn;
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 发送http请求
     *
     * @param url
     * @param method GET 或 POST
     * @param params
     * @return
     */
    public static String openUrl(String url, String method, Bundle params) {
        if (method.equals("GET")) {
            if (params != null) {
                url = url + "?" + encodeUrl(params);
            }
        }
        String response = "";

        try {
//			L.i(method + " URL: " + url);
            HttpClient httpClient = HttpConnectionUtil.getNewHttpClient();
            HttpGet httpGet = new HttpGet(url);
            //连接超时
            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
            // 读取超时
            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000);

            HttpResponse resp = httpClient.execute(httpGet);

            if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                L.i(LOG_TAG, "httpGet fail, status code = " + resp.getStatusLine().getStatusCode());

            } else {
                response = EntityUtils.toString(resp.getEntity());

            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        L.i("LOG_TAG" + response);
        return response;
    }


    /**
     * 发送http请求
     *
     * @param url
     * @param method GET 或 POST
     * @param params
     * @return
     */
    public static String openUrlPost(String url, String method, Bundle params) {
        if (method.equals("POST")) {
            if (params != null) {
                url = url + "?" + encodeUrl(params);
            }
        }
        String response = "";

        try {
            L.i(method + " URL: " + url);
            HttpClient httpClient = HttpConnectionUtil.getNewHttpClient();
            HttpPost httpPost = new HttpPost(url);
            //连接超时
            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
            // 读取超时
            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000);

            HttpResponse resp = httpClient.execute(httpPost);

            if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                L.i(LOG_TAG, "httpPost fail, status code = " + resp.getStatusLine().getStatusCode());

            } else {
                response = EntityUtils.toString(resp.getEntity());

            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        L.i("LOG_TAG" + response);
        return response;
    }

    /**
     * post请求
     *
     * @param js
     * @param html
     * @return
     * @author 陈玉伟
     * 2015-10-28 上午9:19:21
     */

    public static String openUrlByPost(String url, String method, Bundle params) {
        // 注意Post地址中是不带参数的，所以newURL的时候要注意不能加上后面的参数 
        String response = "";
        try {
            url = url + "?source=" + params.getString("source") + "&user_id=" + params.getString("user_id");
            L.i(method + " URL: " + url);
            // Post方式提交的时候参数和URL是分开提交的，参数形式是这样子的：name=y&age=6
            HttpClient httpClient = HttpConnectionUtil.getNewHttpClient();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            List<NameValuePair> paramList = new ArrayList<NameValuePair>();
            params.remove(Constants.METHOD);
            params.remove("source");
            params.remove("user_id");

            for (String key : params.keySet()) {
                paramList.add(new BasicNameValuePair(key, params.get(key).toString()));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(paramList, "UTF-8"));
            //连接超时
            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
            // 读取超时
            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000);
            HttpResponse resp = httpClient.execute(httpPost);

            if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                L.i(LOG_TAG, "httpGet fail, status code = " + resp.getStatusLine().getStatusCode());

            } else {
                response = EntityUtils.toString(resp.getEntity());

            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        L.i("LOG_TAG" + response);
        return response;
    }


    public static String submitOrderByPost(String url, Bundle params, String json) {
        // 注意Post地址中是不带参数的，所以newURL的时候要注意不能加上后面的参数 
        String response = "";
        try {
            url = url + "?source=" + params.getString("source") + "&user_id=" + params.getString("user_id");
            L.i(" URL: " + url);

            // Post方式提交的时候参数和URL是分开提交的，参数形式是这样子的：name=y&age=6
            HttpClient httpClient = HttpConnectionUtil.getNewHttpClient();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");
            StringEntity reqEntity = new StringEntity(json, HTTP.UTF_8);
            httpPost.setEntity(reqEntity);
            //连接超时
            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
            // 读取超时
            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000);
            HttpResponse resp = httpClient.execute(httpPost);

            if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                L.i(LOG_TAG, "httpGet fail, status code = " + resp.getStatusLine().getStatusCode());

            } else {
                response = EntityUtils.toString(resp.getEntity());

            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        L.i("LOG_TAG" + response);
        return response;
    }

    public static String cancelOrderByPost(String url, Bundle params, String json) {
        // 注意Post地址中是不带参数的，所以newURL的时候要注意不能加上后面的参数 
        String response = "";
        try {
            url = url + "?source=" + params.getString("source") + "&id=" + params.getString("id") + "&user_id=" + params.getString("user_id") + "&backCode";
            L.i(" URL: " + url);

            // Post方式提交的时候参数和URL是分开提交的，参数形式是这样子的：name=y&age=6
            HttpClient httpClient = HttpConnectionUtil.getNewHttpClient();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");
            StringEntity reqEntity = new StringEntity(json, HTTP.UTF_8);
            httpPost.setEntity(reqEntity);
            //连接超时
            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
            // 读取超时
            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000);
            HttpResponse resp = httpClient.execute(httpPost);

            if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                L.i(LOG_TAG, "httpGet fail, status code = " + resp.getStatusLine().getStatusCode());

            } else {
                response = EntityUtils.toString(resp.getEntity());

            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        L.i("LOG_TAG" + response);
        return response;
    }

    public static String updataOrderByPost(String url, Bundle params, String json) {
        // 注意Post地址中是不带参数的，所以newURL的时候要注意不能加上后面的参数
        String response = "";
        try {
            url = url + "?source=" + params.getString("source") + "&user_id=" + params.getString("user_id");
            L.i(" URL: " + url);

            // Post方式提交的时候参数和URL是分开提交的，参数形式是这样子的：name=y&age=6
            HttpClient httpClient = HttpConnectionUtil.getNewHttpClient();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");
            StringEntity reqEntity = new StringEntity(json, HTTP.UTF_8);
            httpPost.setEntity(reqEntity);
            //连接超时
            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
            // 读取超时
            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000);
            HttpResponse resp = httpClient.execute(httpPost);

            if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                L.i(LOG_TAG, "httpGet fail, status code = " + resp.getStatusLine().getStatusCode());

            } else {
                response = EntityUtils.toString(resp.getEntity());

            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        L.i("LOG_TAG" + response);
        return response;
    }

    public static String sendGoodsByPost(String url, Bundle params, String json) {
        // 注意Post地址中是不带参数的，所以newURL的时候要注意不能加上后面的参数
        String response = "";
        try {
            url = url + "?source=" + params.getString("source") + "&user_id=" + params.getString("user_id");
            L.i(" URL: " + url);

            // Post方式提交的时候参数和URL是分开提交的，参数形式是这样子的：name=y&age=6
            HttpClient httpClient = HttpConnectionUtil.getNewHttpClient();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");
            StringEntity reqEntity = new StringEntity(json, HTTP.UTF_8);
            httpPost.setEntity(reqEntity);
            //连接超时
            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
            // 读取超时
            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000);
            HttpResponse resp = httpClient.execute(httpPost);

            if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                L.i(LOG_TAG, "httpGet fail, status code = " + resp.getStatusLine().getStatusCode());

            } else {
                response = EntityUtils.toString(resp.getEntity());

            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        L.i("LOG_TAG" + response);
        return response;
    }


    /**
     * 发送http请求
     *
     * @param url
     * @param method GET 或 POST
     * @param params
     * @return
     */
    public static String uploadImagePost(String url, String method, Bundle params, String file) {
        url = url + "?source=" + params.getString("source") + "&user_id=" + params.getString("user_id");
        String response = "";

        try {
            L.i(method + " URL: " + url);
            HttpClient httpClient = HttpConnectionUtil.getNewHttpClient();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("encType", "multipart/form-data;charset=UTF-8");
//			 List<NameValuePair> paramList = new ArrayList<NameValuePair>();
//			 params.remove(Constants.METHOD);
//			 params.remove("source");
//			 params.remove("user_id"); 
            //连接超时
            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
            // 读取超时
            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000);
            File upfile = new File(file);

            //  MultipartEntity entity3 = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, null, Charset.forName("UTF-8"));
            MultipartEntity entity3 = new MultipartEntity();
            entity3.addPart("sss", new FileBody(upfile, "1.jpg", "image/jpeg", "utf-8"));
            /**
             for (String key : params.keySet()) {
             // paramList.add(new BasicNameValuePair(key, params.get(key).toString()));
             }
             httpPost.setEntity(new UrlEncodedFormEntity(paramList,"UTF-8"));
             **/
            httpPost.setEntity(entity3);
            HttpResponse resp = httpClient.execute(httpPost);
            if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                L.i(LOG_TAG, "httpGet fail, status code = " + resp.getStatusLine().getStatusCode());

            } else {
                response = EntityUtils.toString(resp.getEntity());

            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        L.i("LOG_TAG" + response);
        return response;
    }


    private static String read(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line = "";
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        in.close();
        return sb.toString();
    }

    public static byte[] getBytes(String url, Bundle params) {
        try {
            HttpURLConnection conn = openConn(url, "post", params);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            InputStream is = conn.getInputStream();
            for (int i = 0; (i = is.read(buf)) > 0; ) {
                os.write(buf, 0, i);
            }
            is.close();
            os.close();
            return os.toByteArray();
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 将KEY 转换成 URL
     *
     * @param parameters
     * @return
     */
    public static String encodeUrl(Bundle parameters) {

        parameters.remove(Constants.METHOD);
        if (parameters == null) {
            return null;
        } else {
            StringBuffer sb = new StringBuffer();
            for (String key : parameters.keySet()) {

                sb.append(key);
                sb.append("=");
                sb.append(parameters.get(key));
                sb.append("&");
            }
            if (sb.length() == 0) {
                return sb.toString();
            }
            return sb.substring(0, sb.length() - 1);
        }
    }

    /**
     * 将用&号链接的URL参数转换成key-value形式。
     *
     * @param s
     * @return
     */
    public static Bundle decodeUrl(String s) {
        Bundle params = new Bundle();
        if (s != null) {
            params.putString("url", s);
            String array[] = s.split("&");
            for (String parameter : array) {
                String v[] = parameter.split("=");
                if (v.length > 1) {
                    params.putString(v[0], URLDecoder.decode(v[1]));
                }
            }
        }
        return params;
    }

    /**
     * 解析URL中的查询串转换成key-value
     *
     * @param url
     * @return
     */
    public static Bundle parseUrl(String url) {
        url = url.replace("rrconnect", "http");
        url = url.replace("#", "?");
        try {
            URL u = new URL(url);
            Bundle b = decodeUrl(u.getQuery());
            b.putAll(decodeUrl(u.getRef()));
            return b;
        } catch (MalformedURLException e) {
            return new Bundle();
        }
    }

    public static void clearCookies(Context context) {
        @SuppressWarnings("unused")
        CookieSyncManager cookieSyncMngr = CookieSyncManager
                .createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
    }


    private static HttpClient getNewHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }


    private static class SSLSocketFactoryEx extends SSLSocketFactory {

        SSLContext sslContext = SSLContext.getInstance("TLS");

        public SSLSocketFactoryEx(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
            super(truststore);

            TrustManager tm = new X509TrustManager() {

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {
                }
            };

            sslContext.init(null, new TrustManager[]{tm}, null);
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException {
            return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
        }

        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }
    }

}
