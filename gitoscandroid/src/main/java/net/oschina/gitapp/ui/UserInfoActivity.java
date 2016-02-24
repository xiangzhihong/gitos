package net.oschina.gitapp.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ProgressBar;

import net.oschina.gitapp.R;
import net.oschina.gitapp.bean.User;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.ui.baseactivity.BaseActivity;
import net.oschina.gitapp.ui.fragments.UserInfoViewPageFragment;

/**
 * 用户界面
 * @created 2014-05-26 上午10：26
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * 
 * 最后更新：
 * 更新内容：
 * 更新者：
 */
public class UserInfoActivity extends BaseActivity {
	
	private FragmentManager mFragmentManager;
	
	private Bundle mSavedInstanceState;
	
	private User mUser;
	
	private ProgressBar mLoading;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_userinfo_detail);
		this.mSavedInstanceState = savedInstanceState;
		initView();
	}
	
	private void initView() {
		mFragmentManager = getSupportFragmentManager();
		// 拿到传过来的project对象
		Intent intent = getIntent();
		mUser = (User) intent.getSerializableExtra(Contanst.USER);
		mLoading = (ProgressBar) findViewById(R.id.userinfo_loading);
		
		if (mUser == null) {
			loadData();
		} else {
			initViewPage();
		}
	}
	
	private void initViewPage() {
		mTitle = mUser.getName();
        mActionBar.setTitle(mTitle);
		if (null == mSavedInstanceState) {
        	FragmentTransaction ft = mFragmentManager.beginTransaction();
        	ft.replace(R.id.userinfo_content, UserInfoViewPageFragment.newInstance(mUser)).commit();
        }
	}
	
	// 数据加载
	private void loadData() {
    	new AsyncTask<Void, Void, Message>() {

			@Override
			protected Message doInBackground(Void... params) {
				return Message.obtain();
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				mLoading.setVisibility(View.VISIBLE);
			}

			@Override
			protected void onPostExecute(Message msg) {
				super.onPostExecute(msg);
				mLoading.setVisibility(View.GONE);
			}
    		
    	}.execute();
	}
}
