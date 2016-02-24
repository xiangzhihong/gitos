package net.oschina.gitapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuItem;

import net.oschina.gitapp.AppApplication;
import net.oschina.gitapp.R;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.ui.baseactivity.BaseActivity;
import net.oschina.gitapp.ui.fragments.ProjectCommitsFragment;
import net.oschina.gitapp.ui.fragments.ProjectIssuesFragment;

/**
 * 显示项目的一些列表信息
 * 如：issues、commits
 * @created 2014-07-17
 * @author 火蚁（http://my.oschina.net/LittleDY）
 *
 */
public class ProjectSomeInfoListActivity extends BaseActivity {
	
	public final static int PROJECT_LIST_TYPE_ISSUES = 0;
	public final static int PROJECT_LIST_TYPE_COMMITS = 1;
	
	private final int MENU_CREATE_ID = 0;
	
	private FragmentManager mFragmentManager;
	
	private Bundle mSavedInstanceState;
	
	private Project mProject;
	
	private int mListType;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.base_activity_fragment);
		this.mSavedInstanceState = savedInstanceState;
		initView();
	}
	
	private void initView() {
		mFragmentManager = getSupportFragmentManager();
		
		Intent intent = getIntent();
		if (intent != null) {
			mProject = (Project) intent.getSerializableExtra(Contanst.PROJECT);
			mListType = intent.getIntExtra("project_list_type", 0);
			
			mTitle = getTitle(mListType);
            setActionBarTitle(mTitle);
			mSubTitle = mProject.getOwner().getName() + "/" + mProject.getName();
            setActionBarSubTitle(mSubTitle);
		}
		
		if (null == mSavedInstanceState) {
			setFragmentCommit(mListType);
        }
	}
	
	private String getTitle(int type) {
		String title = "";
		switch (type) {
		case PROJECT_LIST_TYPE_ISSUES:
			title = "问题列表";
			break;
		case PROJECT_LIST_TYPE_COMMITS:
			title = "提交列表";
			break;
		}
		return title;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (mListType == PROJECT_LIST_TYPE_ISSUES) {
			MenuItem createOption = menu.add(0, MENU_CREATE_ID, MENU_CREATE_ID, "创建Issue");
			createOption.setIcon(R.drawable.action_create);
			MenuItemCompat.setShowAsAction(createOption, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
		}
		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case MENU_CREATE_ID:
			// 新增issue
			UIHelper.showIssueEditOrCreate(AppApplication.getInstance(), mProject, null);
			break;
		}
		return super.onOptionsItemSelected(item); 
	}
	
	private void setFragmentCommit(int type) {
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		switch (type) {
		case PROJECT_LIST_TYPE_ISSUES:
			ft.replace(R.id.content, ProjectIssuesFragment.newInstance(mProject)).commit();
			break;
		case PROJECT_LIST_TYPE_COMMITS:
			ft.replace(R.id.content, ProjectCommitsFragment.newInstance(mProject)).commit();
			break;
		}
	}
}
