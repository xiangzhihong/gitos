package net.oschina.gitapp.ui.fragments;

import android.os.Bundle;

import net.oschina.gitapp.AppApplication;
import net.oschina.gitapp.R;
import net.oschina.gitapp.adapter.ViewPageFragmentAdapter;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.ui.basefragment.BaseViewPagerFragment;

/**
 * 用户主界面
 * 
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * @created 2014-04-29
 */
public class MySelfViewPagerFragment extends BaseViewPagerFragment {
	
    public static MySelfViewPagerFragment newInstance() {
        return new MySelfViewPagerFragment();
    }
    
	@Override
	protected void onSetupTabAdapter(ViewPageFragmentAdapter adapter) {
		String[] title = getResources().getStringArray(R.array.myself_title_array);
		adapter.addTab(title[0], "event", MySelfEventsFragment.class, null);
		adapter.addTab(title[1], "project", MySelfProjectsFragment.class, null);
		Bundle bundle = new Bundle();
		bundle.putSerializable(Contanst.USER, AppApplication.getInstance().getLoginInfo());
		adapter.addTab(title[2], "star_projects", UserStarProjectFragment.class, bundle);
		adapter.addTab(title[3], "watch_projects", UserWatchProjectsFragment.class, bundle);
	}
}
