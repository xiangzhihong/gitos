package net.oschina.gitapp.ui.basefragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 碎片基类
 *
 * @author 火蚁（http://my.oschina.net/LittleDY）
 *         更新时间：2014-05-15 上午11：38
 *         最后更新者：火蚁
 * @created 2014-05-12
 */
public class BaseFragment extends SupportFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        return null;
    }
}
