package net.oschina.gitapp.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.kymjs.rxvolley.client.HttpCallback;

import net.oschina.gitapp.R;
import net.oschina.gitapp.api.GitOSCApi;
import net.oschina.gitapp.bean.Issue;
import net.oschina.gitapp.bean.Project;
import net.oschina.gitapp.common.Contanst;
import net.oschina.gitapp.common.StringUtils;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.dialog.LightProgressDialog;
import net.oschina.gitapp.ui.baseactivity.BaseActivity;

import java.util.Map;


/**
 * 评论issue界面
 *
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * @created 2014-08-25
 */
public class IssueCommentActivity extends BaseActivity implements OnClickListener {

    private Project mProject;

    private Issue mIssue;

    private EditText mCommentContent;

    private Button mCommentPub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_comment);
        initView();
        initData();
    }

    private void initView() {
        mCommentContent = (EditText) findViewById(R.id.issue_comment_content);
        mCommentPub = (Button) findViewById(R.id.issue_comment_pub);
        TextWatcher mTextWatcher = new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mProject == null || mIssue == null) return;
                if (StringUtils.isEmpty(mCommentContent.getText().toString())) {
                    mCommentPub.setEnabled(false);
                } else {
                    mCommentPub.setEnabled(true);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        mCommentContent.addTextChangedListener(mTextWatcher);
        mCommentPub.setOnClickListener(this);
    }

    private void initData() {
        Intent intent = getIntent();
        if (null != intent) {
            mProject = (Project) intent.getSerializableExtra(Contanst.PROJECT);
            mIssue = (Issue) intent.getSerializableExtra(Contanst.ISSUE);
        }

        if (null != mProject && mIssue != null) {
            mTitle = "评论 Issue #" + mIssue.getIid();
            mSubTitle = mProject.getOwner().getName() + "/" + mProject.getName();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.issue_comment_pub:
                pubComment();
                break;

            default:
                break;
        }
    }

    private void pubComment() {
        if (mProject == null || mIssue == null) {
            return;
        }

        final AlertDialog pubing = LightProgressDialog.create(this, "提交评论中...");

        GitOSCApi.pubIssueComment(mProject.getId(), mIssue.getId(), mCommentContent.getText()
                .toString(), new HttpCallback() {
            @Override
            public void onSuccess(Map<String, String> headers, byte[] t) {
                super.onSuccess(headers, t);
                UIHelper.toastMessage(IssueCommentActivity.this, "评论成功");
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                UIHelper.toastMessage(IssueCommentActivity.this, "评论失败");
            }

            @Override
            public void onPreStart() {
                super.onPreStart();
                pubing.show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                pubing.dismiss();
            }
        });
    }
}
