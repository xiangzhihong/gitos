package net.oschina.gitapp.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import com.kymjs.rxvolley.client.HttpCallback;

import net.oschina.gitapp.R;
import net.oschina.gitapp.adapter.CommonAdapter;
import net.oschina.gitapp.adapter.ViewHolder;
import net.oschina.gitapp.api.GitOSCApi;
import net.oschina.gitapp.bean.ProjectMember;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.util.JsonUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by 火蚁 on 15/4/24.
 */
public class ProjectMembersSelectDialog {

    public interface CallBack {
        public void callBack(ProjectMember projectMember);

        public void clear();
    }

    private Context context;

    private CallBack callBack;

    private String pId;

    private AlertDialog.Builder dialog;

    private CommonAdapter<ProjectMember> adapter;

    private List<ProjectMember> members;

    public ProjectMembersSelectDialog(Context context, String pId, final CallBack callBack) {
        this.context = context;
        this.pId = pId;
        this.callBack = callBack;
        this.dialog = new AlertDialog.Builder(this.context);
        dialog.setTitle("指派给");
        dialog.setNegativeButton("取消", null);
        dialog.setPositiveButton("清除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callBack.clear();
            }
        });
    }

    private void load(final String memberId) {
        final AlertDialog loading = LightProgressDialog.create(this.context, "加载项目成员中...");

        GitOSCApi.getProjectMembers(this.pId, new HttpCallback() {

            @Override
            public void onSuccess(Map<String, String> headers, byte[] t) {
                super.onSuccess(headers, t);
                List<ProjectMember> projectMembers = JsonUtils.getList(ProjectMember[].class, t);
                if (projectMembers != null && !projectMembers.isEmpty()) {
                    members = projectMembers;
                    show(memberId);
                } else {
                    Toast.makeText(context, "加载项目成员失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                UIHelper.toastMessage(context, "加载项目成员失败");
            }

            @Override
            public void onPreStart() {
                super.onPreStart();
                loading.show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                loading.dismiss();
            }
        });
    }

    public void show(String memberId) {
        if (members == null || members.isEmpty()) {
            load(memberId);
            return;
        }

        if (adapter == null || dialog == null) {

            adapter = new CommonAdapter<ProjectMember>(this.context, R.layout
                    .list_item_project_member) {
                @Override
                public void convert(ViewHolder vh, ProjectMember item) {
                    vh.setText(R.id.tv_name, item.getName());
                    vh.setImageForUrl(R.id.iv_portrait, item.getNew_portrait());
                }
            };
            adapter.addItem(members);
        }

        int index = -1;
        for (int i = 0; i < members.size(); i++) {
            if (memberId.equals(members.get(i).getId())) {
                index = i;
                break;
            }
        }

        dialog.setSingleChoiceItems(adapter, index, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callBack.callBack(adapter.getItem(which));
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
