package net.oschina.gitapp.api;

import android.text.TextUtils;

import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;
import com.kymjs.rxvolley.client.HttpParams;

import net.oschina.gitapp.AppApplication;
import net.oschina.gitapp.common.CyptoUtils;


/**
 * 获取一个httpClient
 * Created by 火蚁 on 15/4/13.
 */
public class AsyncHttpHelp {
    public final static String PRIVATE_TOKEN = "private_token";
    public final static String GITOSC_PRIVATE_TOKEN = "git@osc_token";

    public static void get(String url, HttpCallback handler) {
        RxVolley.get(url, handler);
    }

    public static void get(String url, HttpParams params, HttpCallback handler) {
        new RxVolley.Builder().url(url).params(params).cacheTime(15).callback(handler).doTask();
    }

    public static void post(String url, HttpParams params, HttpCallback handler) {
        RxVolley.post(url, params, handler);
    }

    /**
     * 获得UserAgent
     *
     * @return
     */
    private static String getUserAgent() {
        AppApplication appContext = AppApplication.getInstance();
        StringBuilder ua = new StringBuilder("Git@OSC.NET");
        ua.append('/' + appContext.getPackageInfo().versionName + '_' + appContext.getPackageInfo
                ().versionCode);//App版本
        ua.append("/Android");//手机系统平台
        ua.append("/" + android.os.Build.VERSION.RELEASE);//手机系统版本
        ua.append("/" + android.os.Build.MODEL); //手机型号
        ua.append("/" + AppApplication.getInstance().getAppId());//客户端唯一标识
        return ua.toString();
    }

    public static HttpParams getPrivateTokenWithParams() {
        HttpParams params = new HttpParams();
        params.putHeaders("User-Agent", getUserAgent());
        String private_token = AppApplication.getInstance().getProperty(PRIVATE_TOKEN);
        private_token = CyptoUtils.decode(GITOSC_PRIVATE_TOKEN, private_token);
        if (!TextUtils.isEmpty(private_token))
            params.put(PRIVATE_TOKEN, private_token);
        return params;
    }

    public static HttpParams getHttpParams() {
        return getPrivateTokenWithParams();
    }
}
