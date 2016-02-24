package net.oschina.gitapp.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import net.oschina.gitapp.R;
import net.oschina.gitapp.ui.baseactivity.BaseActivity;
import net.oschina.gitapp.ui.fragments.NotificaiontViewPagerFragment;

/**
 * 系统通知页面
 *
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * @created 2014-07-31
 */
public class NotificationActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticifation);
        initView();
    }

    private void initView() {
        mTitle = "我的通知";
        mActionBar.setTitle(mTitle);
        FragmentManager mFragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.replace(R.id.notification_content, NotificaiontViewPagerFragment.newInstance()).commit();
    }
}
