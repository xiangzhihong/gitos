package net.oschina.gitapp.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.blueware.agent.android.BlueWare;
import com.kymjs.rxvolley.client.HttpCallback;
import com.umeng.analytics.MobclickAgent;

import net.oschina.gitapp.AppApplication;
import net.oschina.gitapp.AppManager;
import net.oschina.gitapp.R;
import net.oschina.gitapp.api.GitOSCApi;
import net.oschina.gitapp.bean.ProjectNotificationArray;
import net.oschina.gitapp.common.DoubleClickExitHelper;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.common.UpdateManager;
import net.oschina.gitapp.interfaces.DrawerMenuCallBack;
import net.oschina.gitapp.ui.fragments.ExploreViewPagerFragment;
import net.oschina.gitapp.ui.fragments.MySelfViewPagerFragment;
import net.oschina.gitapp.util.JsonUtils;

import java.util.List;
import java.util.Map;


/**
 * 程序主界面
 *
 * @author 火蚁（http://my.oschina.net/LittleDY）
 *         <p>
 *         最后更新：2014-05-29
 *         更新内容：更改以callBack的方式进行交互
 *         更新者：火蚁
 */
public class MainActivity extends AppCompatActivity implements
        DrawerMenuCallBack {

    final String DRAWER_MENU_TAG = "drawer_menu";

    final String CONTENT_TAG_EXPLORE = "content_explore";
    final String CONTENT_TAG_MYSELF = "content_myself";

    final String CONTENTS[] = {
            CONTENT_TAG_EXPLORE,
            CONTENT_TAG_MYSELF
    };

    final String FRAGMENTS[] = {
            ExploreViewPagerFragment.class.getName(),
            MySelfViewPagerFragment.class.getName()
    };

    final String TITLES[] = {
            "发现",
            "我的"
    };

    private DrawerNavigationMenu mMenu = DrawerNavigationMenu.newInstance();
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private DoubleClickExitHelper mDoubleClickExitHelper;

    private Fragment currentSupportFragment;

    // 当前显示的界面标识
    private String mCurrentContentTag;
    private ActionBar mActionBar;
    private AppApplication mContext;

    private String mTitle;// actionbar标题

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = (AppApplication) getApplicationContext();
        initView(savedInstanceState);
        AppManager.getAppManager().addActivity(this);

        BlueWare.withApplicationToken("A97669647CD7FA558E6076201E5F97B322").start
                (getApplicationContext());
        MobclickAgent.updateOnlineConfig(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mTitle != null) {
            mActionBar.setTitle(mTitle);
        }
        if (mCurrentContentTag != null && mContext != null && mMenu != null) {
            if (mCurrentContentTag.equalsIgnoreCase(CONTENTS[1])) {
                if (!mContext.isLogin()) {
                    onClickExplore();
                    mMenu.highlightExplore();
                }
            }
        }
    }

    private void initView(Bundle savedInstanceState) {

        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setHomeButtonEnabled(true);
        }

        mDoubleClickExitHelper = new DoubleClickExitHelper(this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerListener(new DrawerMenuListener());
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, null, 0, 0);

        if (null == savedInstanceState) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.main_slidingmenu_frame, mMenu, DRAWER_MENU_TAG).commit();

            changeFragment(R.id.main_content, new MySelfViewPagerFragment());
            changeFragment(R.id.main_content, new ExploreViewPagerFragment());

            mTitle = "发现";
            mActionBar.setTitle(mTitle); 
            mCurrentContentTag = CONTENT_TAG_EXPLORE;
        }
    }

    private HttpCallback noticeHandler = new HttpCallback() {
        @Override
        public void onSuccess(Map<String, String> headers, byte[] t) {
            super.onSuccess(headers, t);
            List<ProjectNotificationArray> notificationArrays = JsonUtils.getList
                    (ProjectNotificationArray[].class, t);
            if (notificationArrays == null || notificationArrays.isEmpty()) {
                return;
            }
            int count = 0;
            for (ProjectNotificationArray pna : notificationArrays) {
                count += pna.getProject().getNotifications().size();
            }
            UIHelper.sendBroadCast(MainActivity.this, count);
        }
    };

    /**
     * 轮询通知信息
     */
    private void foreachUserNotice() {
        GitOSCApi.getNotification("", "", "", noticeHandler);
        final boolean isLogin = mContext.isLogin();
        new Thread() {
            public void run() {
                try {
                    // 6秒重新去获取一次通知
                    sleep(60 * 1000);
                    if (isLogin) {
                        foreachUserNotice();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
        View mainContent = findViewById(R.id.main_content);
        if (mainContent != null) {
            mainContent.setAlpha(0);
            mainContent.animate().alpha(1).setDuration(200);
        }

        // 检查新版本
        if (mContext.isCheckUp()) {
            UpdateManager.getUpdateManager().checkAppUpdate(this, false);
        }
        // 启动轮询获取通知信息
        if (mContext.isReceiveNotice()) {
            foreachUserNotice();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_actionbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.main_actionbar_menu_search:
                UIHelper.showSearch(mContext);
                return true;
            case R.id.main_actionbar_menu_notification:
                onClickNotice();
                return true;
            default:
                break;
        }
        return mDrawerToggle.onOptionsItemSelected(item)
                || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 判断菜单是否打开
            if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                mDrawerLayout.closeDrawers();
                return true;
            }
            return mDoubleClickExitHelper.onKeyDown(keyCode, event);
        }
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                mDrawerLayout.closeDrawers();
                return true;
            } else {
                mDrawerLayout.openDrawer(Gravity.LEFT);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 显示内容
     */
    private void showMainContent(int pos) {
        mDrawerLayout.closeDrawers();
        String tag = CONTENTS[pos];
        if (tag.equalsIgnoreCase(mCurrentContentTag)) return;

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_content, Fragment.instantiate(this, FRAGMENTS[pos]))
                .commit();
        mActionBar.setTitle(TITLES[pos]);
        mTitle = TITLES[pos];//记录主界面的标题
        mCurrentContentTag = tag;
    }

    private void showLoginActivity() {
        if (!mContext.isLogin()) {
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);
        } else {
            UIHelper.showMySelfInfoDetail(MainActivity.this);
        }
    }

    @Override
    public void onClickLogin() {
        showLoginActivity();
    }

    @Override
    public void onClickExplore() {
        showMainContent(0);
    }

    @Override
    public void onClickMySelf() {
        if (!mContext.isLogin()) {
            UIHelper.showLoginActivity(this);
        } else {
            showMainContent(1);
        }
    }

    public void onClickNotice() {
        if (!mContext.isLogin()) {
            UIHelper.showLoginActivity(this);
            return;
        }
        Intent intent = new Intent(mContext, NotificationActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClickLanguage() {
        Intent intent = new Intent(mContext, LanguageActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClickShake() {
        Intent intent = new Intent(mContext, ShakeActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClickScan() {
    }

    @Override
    public void onClickSetting() {
        Intent intent = new Intent(mContext, SettingActivity.class);
        startActivity(intent);
    }

    private class DrawerMenuListener implements DrawerLayout.DrawerListener {
        @Override
        public void onDrawerOpened(View drawerView) {
            mDrawerToggle.onDrawerOpened(drawerView);
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            mDrawerToggle.onDrawerClosed(drawerView);
        }

        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
            mDrawerToggle.onDrawerSlide(drawerView, slideOffset);
        }

        @Override
        public void onDrawerStateChanged(int newState) {
            mDrawerToggle.onDrawerStateChanged(newState);
        }
    }

    /**
     * 用Fragment替换视图
     *
     * @param resView        将要被替换掉的视图
     * @param targetFragment 用来替换的Fragment
     */
    public void changeFragment(int resView, Fragment targetFragment) {
        if (targetFragment.equals(currentSupportFragment)) {
            return;
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (!targetFragment.isAdded()) {
//            transaction.replace(resView, targetFragment);
            transaction.add(resView, targetFragment, targetFragment.getClass().getName());
        }
        if (targetFragment.isHidden()) {
            transaction.show(targetFragment);
        }
        if (currentSupportFragment != null && currentSupportFragment.isVisible()) {
            transaction.hide(currentSupportFragment);
        }
        currentSupportFragment = targetFragment;
        transaction.commit();
    }
}
