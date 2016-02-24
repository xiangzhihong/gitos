package net.oschina.gitapp;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.AudioManager;

import com.kymjs.crash.CustomActivityOnCrash;
import com.kymjs.okhttp.OkHttpStack;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.http.RequestQueue;
import com.squareup.okhttp.OkHttpClient;

import net.oschina.gitapp.api.AsyncHttpHelp;
import net.oschina.gitapp.bean.Follow;
import net.oschina.gitapp.bean.User;
import net.oschina.gitapp.common.BroadcastController;
import net.oschina.gitapp.common.CyptoUtils;
import net.oschina.gitapp.common.MethodsCompat;
import net.oschina.gitapp.common.StringUtils;

import java.io.File;
import java.util.Properties;
import java.util.UUID;

import static net.oschina.gitapp.common.Contanst.ACCOUNT_EMAIL;
import static net.oschina.gitapp.common.Contanst.ACCOUNT_PWD;
import static net.oschina.gitapp.common.Contanst.PROP_KEY_BIO;
import static net.oschina.gitapp.common.Contanst.PROP_KEY_BLOG;
import static net.oschina.gitapp.common.Contanst.PROP_KEY_CAN_CREATE_GROUP;
import static net.oschina.gitapp.common.Contanst.PROP_KEY_CAN_CREATE_PROJECT;
import static net.oschina.gitapp.common.Contanst.PROP_KEY_CAN_CREATE_TEAM;
import static net.oschina.gitapp.common.Contanst.PROP_KEY_CREATED_AT;
import static net.oschina.gitapp.common.Contanst.PROP_KEY_EMAIL;
import static net.oschina.gitapp.common.Contanst.PROP_KEY_IS_ADMIN;
import static net.oschina.gitapp.common.Contanst.PROP_KEY_NAME;
import static net.oschina.gitapp.common.Contanst.PROP_KEY_NEWPORTRAIT;
import static net.oschina.gitapp.common.Contanst.PROP_KEY_PORTRAIT;
import static net.oschina.gitapp.common.Contanst.PROP_KEY_PRIVATE_TOKEN;
import static net.oschina.gitapp.common.Contanst.PROP_KEY_STATE;
import static net.oschina.gitapp.common.Contanst.PROP_KEY_THEME_ID;
import static net.oschina.gitapp.common.Contanst.PROP_KEY_UID;
import static net.oschina.gitapp.common.Contanst.PROP_KEY_USERNAME;
import static net.oschina.gitapp.common.Contanst.PROP_KEY_WEIBO;
import static net.oschina.gitapp.common.Contanst.ROP_KEY_FOLLOWERS;
import static net.oschina.gitapp.common.Contanst.ROP_KEY_FOLLOWING;
import static net.oschina.gitapp.common.Contanst.ROP_KEY_STARRED;
import static net.oschina.gitapp.common.Contanst.ROP_KEY_WATCHED;

/**
 * 全局应用程序类：用于保存和调用全局应用配置及访问网络数据
 *
 * @author 火蚁 (http://my.oschina.net/LittleDY)
 * @version 1.0
 * @created 2014-04-22
 */
public class AppApplication extends Application {

    public static final int PAGE_SIZE = 20;// 默认分页大小

    private boolean login = false; // 登录状态
    private int loginUid = 0; // 登录用户的id

    private static AppApplication appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        // 注册App异常崩溃处理器
        CustomActivityOnCrash.install(this);
        File cacheFolder = getCacheDir();
        RxVolley.setRequestQueue(RequestQueue.newRequestQueue(cacheFolder, new
                OkHttpStack(new OkHttpClient())));
        init();
        appContext = this;
    }

    public static AppApplication getInstance() {
        return appContext;
    }

    /**
     * 初始化Application
     */
    private void init() {
        // 初始化用记的登录信息
        User loginUser = getLoginInfo();
        if (null != loginUser && StringUtils.toInt(loginUser.getId()) > 0
                && !StringUtils.isEmpty(getProperty(PROP_KEY_PRIVATE_TOKEN))) {
            // 记录用户的id和状态
            this.loginUid = StringUtils.toInt(loginUser.getId());
            this.login = true;
        }
    }


    public void setProperties(Properties ps) {
        AppConfig.getAppConfig(this).set(ps);
    }

    public Properties getProperties() {
        return AppConfig.getAppConfig(this).get();
    }

    public void setProperty(String key, String value) {
        AppConfig.getAppConfig(this).set(key, value);
    }

    public String getProperty(String key) {
        return AppConfig.getAppConfig(this).get(key);
    }

    public void removeProperty(String... key) {
        AppConfig.getAppConfig(this).remove(key);
    }

    /**
     * 是否是第一次启动App
     */
    public boolean isFristStart() {
        boolean res = false;
        String perf_frist = getProperty(AppConfig.CONF_FRIST_START);
        // 默认是http
        if (StringUtils.isEmpty(perf_frist)) {
            res = true;
            setProperty(AppConfig.CONF_FRIST_START, "false");
        }
        return res;
    }

    /**
     * 设置是否发出提示音
     */
    public void setConfigVoice(boolean b) {
        setProperty(AppConfig.CONF_VOICE, String.valueOf(b));
    }

    /**
     * 是否启动检查更新
     */
    public boolean isCheckUp() {
        String perf_checkup = getProperty(AppConfig.CONF_CHECKUP);
        // 默认是开启
        return StringUtils.isEmpty(perf_checkup) || StringUtils.toBool(perf_checkup);
    }

    /**
     * 设置启动检查更新
     */
    public void setConfigCheckUp(boolean b) {
        setProperty(AppConfig.CONF_CHECKUP, String.valueOf(b));
    }

    /**
     * 检测当前系统声音是否为正常模式
     */
    public boolean isAudioNormal() {
        AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        return mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
    }

    /**
     * 应用程序是否发出提示音
     */
    public boolean isAppSound() {
        return isAudioNormal() && isVoice();
    }

    /**
     * 是否接收通知
     */
    public boolean isReceiveNotice() {
        String perf_notice = getProperty(AppConfig.CONF_RECEIVENOTICE);
        // 默认是开启提示声音
        return StringUtils.isEmpty(perf_notice) || StringUtils.toBool(perf_notice);
    }

    /**
     * 设置是否接收通知
     */
    public void setConfigReceiveNotice(boolean isReceiveNotice) {
        setProperty(AppConfig.CONF_RECEIVENOTICE, String.valueOf(isReceiveNotice));
    }

    /**
     * 是否发出提示音
     */
    public boolean isVoice() {
        String perf_voice = getProperty(AppConfig.CONF_VOICE);
        return StringUtils.isEmpty(perf_voice) || StringUtils.toBool(perf_voice);
    }

    /**
     * 判断当前版本是否兼容目标版本的方法
     */
    public static boolean isMethodsCompat(int VersionCode) {
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        return currentVersion >= VersionCode;
    }

    /**
     * 获取App唯一标识
     */
    public String getAppId() {
        String uniqueID = getProperty(AppConfig.CONF_APP_UNIQUEID);
        if (StringUtils.isEmpty(uniqueID)) {
            uniqueID = UUID.randomUUID().toString();
            setProperty(AppConfig.CONF_APP_UNIQUEID, uniqueID);
        }
        return uniqueID;
    }

    /**
     * 获取App安装包信息
     */
    public PackageInfo getPackageInfo() {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        if (info == null)
            info = new PackageInfo();
        return info;
    }

    /**
     * 获取登录信息
     */
    public User getLoginInfo() {
        User user = new User();
        user.setId(getProperty(PROP_KEY_UID));
        user.setUsername(getProperty(PROP_KEY_USERNAME));
        user.setName(getProperty(PROP_KEY_NAME));
        user.setBio(getProperty(PROP_KEY_BIO));
        user.setWeibo(getProperty(PROP_KEY_WEIBO));
        user.setBlog(getProperty(PROP_KEY_BLOG));
        user.setTheme_id(StringUtils.toInt(getProperty(PROP_KEY_THEME_ID), 1));
        user.setState(getProperty(PROP_KEY_STATE));
        user.setCreated_at(getProperty(PROP_KEY_CREATED_AT));
        user.setPortrait(getProperty(PROP_KEY_PORTRAIT));
        user.setNew_portrait(getProperty(PROP_KEY_NEWPORTRAIT));
        user.setIsAdmin(StringUtils.toBool(getProperty(PROP_KEY_IS_ADMIN)));
        user.setCanCreateGroup(StringUtils
                .toBool(getProperty(PROP_KEY_CAN_CREATE_GROUP)));
        user.setCanCreateProject(StringUtils
                .toBool(getProperty(PROP_KEY_CAN_CREATE_PROJECT)));
        user.setCanCreateTeam(StringUtils
                .toBool(getProperty(PROP_KEY_CAN_CREATE_TEAM)));
        Follow follow = new Follow();
        follow.setFollowers(StringUtils.toInt(getProperty(ROP_KEY_FOLLOWERS)));
        follow.setStarred(StringUtils.toInt(getProperty(ROP_KEY_STARRED)));
        follow.setFollowing(StringUtils.toInt(getProperty(ROP_KEY_FOLLOWING)));
        follow.setWatched(StringUtils.toInt(getProperty(ROP_KEY_WATCHED)));
        user.setFollow(follow);
        return user;
    }

    /**
     * 保存用户的email和pwd
     */
    public void saveAccountInfo(String email, String pwd) {
        setProperty(ACCOUNT_EMAIL, email);
        setProperty(ACCOUNT_PWD, pwd);
    }

    /**
     * 保存登录用户的信息
     */
    @SuppressWarnings("serial")
    public void saveLoginInfo(final User user) {
        if (null == user) {
            return;
        }
        // 保存用户的信息
        this.loginUid = StringUtils.toInt(user.getId());
        this.login = true;
        setProperties(new Properties() {
            {
                setProperty(PROP_KEY_UID, String.valueOf(user.getId()));
                setProperty(PROP_KEY_USERNAME, String.valueOf(user.getUsername()));
                setProperty(PROP_KEY_NAME, String.valueOf(user.getName()));
                setProperty(PROP_KEY_BIO, String.valueOf(user.getBio()));// 个人介绍
                setProperty(PROP_KEY_WEIBO, String.valueOf(user.getWeibo()));
                setProperty(PROP_KEY_BLOG, String.valueOf(user.getBlog()));
                setProperty(PROP_KEY_THEME_ID, String.valueOf(user.getTheme_id()));
                setProperty(PROP_KEY_STATE, String.valueOf(user.getState()));
                setProperty(PROP_KEY_CREATED_AT, String.valueOf(user.getCreated_at()));
                setProperty(PROP_KEY_PORTRAIT, String.valueOf(user.getPortrait()));// 个人头像
                setProperty(PROP_KEY_NEWPORTRAIT, String.valueOf(user.getNew_portrait()));// 个人头像
                setProperty(PROP_KEY_IS_ADMIN, String.valueOf(user.isIsAdmin()));
                setProperty(PROP_KEY_CAN_CREATE_GROUP, String.valueOf(user.isCanCreateGroup()));
                setProperty(PROP_KEY_CAN_CREATE_PROJECT, String.valueOf(user.isCanCreateProject()));
                setProperty(PROP_KEY_CAN_CREATE_TEAM, String.valueOf(user.isCanCreateTeam()));
                setProperty(ROP_KEY_FOLLOWERS, String.valueOf(user.getFollow().getFollowers()));
                setProperty(ROP_KEY_STARRED, String.valueOf(user.getFollow().getStarred()));
                setProperty(ROP_KEY_FOLLOWING, String.valueOf(user.getFollow().getFollowing()));
                setProperty(ROP_KEY_WATCHED, String.valueOf(user.getFollow().getWatched()));
            }
        });
    }

    /**
     * 清除登录信息，用户的私有token也一并清除
     */
    private void cleanLoginInfo() {
        this.loginUid = 0;
        this.login = false;
        removeProperty(PROP_KEY_PRIVATE_TOKEN, PROP_KEY_UID, PROP_KEY_USERNAME, PROP_KEY_EMAIL,
                PROP_KEY_NAME, PROP_KEY_BIO, PROP_KEY_WEIBO, PROP_KEY_BLOG,
                PROP_KEY_THEME_ID, PROP_KEY_STATE, PROP_KEY_CREATED_AT,
                PROP_KEY_PORTRAIT, PROP_KEY_IS_ADMIN,
                PROP_KEY_CAN_CREATE_GROUP, PROP_KEY_CAN_CREATE_PROJECT,
                PROP_KEY_CAN_CREATE_TEAM, ROP_KEY_FOLLOWERS, ROP_KEY_STARRED,
                ROP_KEY_FOLLOWING, ROP_KEY_WATCHED);
    }

    /**
     * 用户是否登录
     */
    public boolean isLogin() {
        return login;
    }

    /**
     * 获取登录用户id
     */
    public int getLoginUid() {
        return this.loginUid;
    }

    /**
     * 用户注销
     */
    public void logout() {
        // 清除已登录用户的信息
        cleanLoginInfo();
        this.login = false;
        this.loginUid = 0;
        // 发送广播通知
        BroadcastController.sendUserChangeBroadcase(this);
    }

    /**
     * 清除app缓存
     */
    public void clearAppCache() {
        deleteDatabase("webview.db");
        deleteDatabase("webview.db-shm");
        deleteDatabase("webview.db-wal");
        deleteDatabase("webviewCache.db");
        deleteDatabase("webviewCache.db-shm");
        deleteDatabase("webviewCache.db-wal");
        // 清除数据缓存
        clearCacheFolder(getFilesDir(), System.currentTimeMillis());
        clearCacheFolder(getCacheDir(), System.currentTimeMillis());
        // 2.2版本才有将应用缓存转移到sd卡的功能
        if (isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
            clearCacheFolder(MethodsCompat.getExternalCacheDir(this),
                    System.currentTimeMillis());
        }
        // 清除编辑器保存的临时内容
        Properties props = getProperties();
        for (Object key : props.keySet()) {
            String _key = key.toString();
            if (_key.startsWith("temp"))
                removeProperty(_key);
        }
    }

    /**
     * 清除缓存目录
     *
     * @param dir     目录
     * @param curTime 当前系统时间
     */
    private int clearCacheFolder(File dir, long curTime) {
        int deletedFiles = 0;
        if (dir != null && dir.isDirectory()) {
            try {
                for (File child : dir.listFiles()) {
                    if (child.isDirectory()) {
                        deletedFiles += clearCacheFolder(child, curTime);
                    }
                    if (child.lastModified() < curTime) {
                        if (child.delete()) {
                            deletedFiles++;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return deletedFiles;
    }

    public static String getToken() {
        String private_token = AppApplication.getInstance().getProperty(AsyncHttpHelp.PRIVATE_TOKEN);
        private_token = CyptoUtils.decode(AsyncHttpHelp.GITOSC_PRIVATE_TOKEN, private_token);
        return private_token;
    }
}
