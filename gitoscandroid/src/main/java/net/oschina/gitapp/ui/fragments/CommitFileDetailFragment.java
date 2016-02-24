package net.oschina.gitapp.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kymjs.core.bitmap.client.BitmapCore;
import com.kymjs.rxvolley.client.HttpCallback;

import net.oschina.gitapp.R;
import net.oschina.gitapp.adapter.CommitDiffAdapter;
import net.oschina.gitapp.api.GitOSCApi;
import net.oschina.gitapp.bean.Commit;
import net.oschina.gitapp.bean.CommitDiff;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.common.StringUtils;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.interfaces.OnStatusListener;
import net.oschina.gitapp.ui.basefragment.BaseFragment;
import net.oschina.gitapp.util.JsonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * commit文件详情
 *
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * @created 2014-04-29
 */
public class CommitFileDetailFragment extends BaseFragment implements
        OnStatusListener {

    private Commit mCommit;

    private Project mProject;

    private ImageView mCommitAuthorFace;

    private TextView mCommitAuthorName;

    private TextView mCommitDate;

    private TextView mCmmitMessage;

    private View mLoading;

    private TextView mCommitFileSum;

    private LinearLayout mCommitDiffll;

    private List<CommitDiff> mCommitDiffList = new ArrayList<>();

    private CommitDiffAdapter adapter;

    public static CommitFileDetailFragment newInstance(Project project, Commit commit) {
        CommitFileDetailFragment fragment = new CommitFileDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(Contanst.PROJECT, project);
        args.putSerializable(Contanst.COMMIT, commit);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.commit_detail_file_fragment, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initData();
    }

    private void initView(View view) {
        mCommitAuthorFace = (ImageView) view.findViewById(R.id.commit_author_face);

        mCommitAuthorName = (TextView) view.findViewById(R.id.commit_author_name);

        mCommitDate = (TextView) view.findViewById(R.id.commit_date);

        mCmmitMessage = (TextView) view.findViewById(R.id.commit_message);

        mLoading = view.findViewById(R.id.commit_diff_ll_loading);

        mCommitFileSum = (TextView) view.findViewById(R.id.commit_diff_changefile_sum);

        mCommitDiffll = (LinearLayout) view.findViewById(R.id.commit_diff_file_list);
    }

    protected void initData() {
        Bundle args = getArguments();
        if (args != null) {
            mProject = (Project) args.getSerializable(Contanst.PROJECT);
            mCommit = (Commit) args.getSerializable(Contanst.COMMIT);
        }

        mCommitDate.setText("提交于" + StringUtils.friendly_time(mCommit.getCreatedAt()));
        mCommitAuthorName.setText(mCommit.getAuthor_name());
        mCmmitMessage.setText(mCommit.getTitle());
        loadAuthorFace();
        loadDatasCode(false);
    }

    @Override
    public void onStatus(int status) {

    }

    // 加载提交用户头像
    private void loadAuthorFace() {
        String portrait = mCommit.getAuthor() == null ? null : mCommit.getAuthor().getPortrait();
        if (portrait == null || portrait.endsWith(".gif")) {
            mCommitAuthorFace.setBackgroundResource(R.drawable.mini_avatar);
        } else {
            String faceurl = GitOSCApi.NO_API_BASE_URL + portrait;
            new BitmapCore.Builder().url(faceurl).view(mCommitAuthorFace).doTask();
        }
    }

    private void loadDatasCode(final boolean isRefresh) {
        onStatus(STATUS_LOADING);

        GitOSCApi.getCommitDiffList(mProject.getId(), mCommit.getId(), new HttpCallback() {
            @Override
            public void onSuccess(Map<String, String> headers, byte[] t) {
                super.onSuccess(headers, t);
                List<CommitDiff> commitDiffList = JsonUtils.getList(CommitDiff[].class, t);
                if (commitDiffList != null) {
                    mLoading.setVisibility(View.GONE);
                    onStatus(STATUS_LOADED);
                    mCommitDiffList = commitDiffList;
                    mCommitFileSum.setText(mCommitDiffList.size() + " 个文件发生了变化");
                    adapter = new CommitDiffAdapter(getActivity(), mCommitDiffList, R.layout
                            .commit_diff_listitem, mCommitDiffll);
                    adapter.setData(mProject, mCommit);
                    mCommitDiffll.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                } else {
                    mLoading.setVisibility(View.GONE);
                    UIHelper.toastMessage(getActivity(), "获取commit详情失败");
                    onStatus(STATUS_NONE);
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                UIHelper.toastMessage(getActivity(), "获取commit详情失败");
            }
        });
    }
}
