package net.oschina.gitapp.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.TextView;

import com.kymjs.rxvolley.client.HttpCallback;

import net.oschina.gitapp.R;
import net.oschina.gitapp.adapter.CommonAdapter;
import net.oschina.gitapp.adapter.ViewHolder;
import net.oschina.gitapp.api.GitOSCApi;
import net.oschina.gitapp.bean.Branch;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.util.JsonUtils;
import net.oschina.gitapp.util.TypefaceUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by 火蚁 on 15/4/23.
 */
public class ProjectRefSelectDialog {

    public interface CallBack {
        public void onCallBack(Branch branch);
    }

    private Context context;

    private static String pId;

    private AlertDialog.Builder dialog;

    private CommonAdapter<Branch> adapter;

    private List<Branch> branches = new ArrayList<>();

    private CallBack callBack;

    public ProjectRefSelectDialog(Context context, String pId, CallBack callBack) {
        this.context = context;
        this.pId = pId;
        dialog = new AlertDialog.Builder(context);
        dialog.setTitle("选择分支或者标签");
        dialog.setPositiveButton("取消", null);
        this.callBack = callBack;
    }

    public void load(final String branch) {
        final AlertDialog loading = LightProgressDialog.create(context, "加载分支和标签中...");
        loading.show();

        GitOSCApi.getProjectBranchs(pId, new HttpCallback() {
            @Override
            public void onSuccess(Map<String, String> headers, byte[] t) {
                super.onSuccess(headers, t);
                List<Branch> branches = JsonUtils.getList(Branch[].class, t);
                if (branches != null && !branches.isEmpty()) {
                    for (Branch b : branches) {
                        b.setType(Branch.TYPE_BRANCH);
                    }
                    ProjectRefSelectDialog.this.branches.addAll(branches);
                    GitOSCApi.getProjectTags(pId, new HttpCallback() {
                        @Override
                        public void onSuccess(Map<String, String> headers, byte[] t) {
                            super.onSuccess(headers, t);
                            List<Branch> branches = JsonUtils.getList(Branch[].class, t);
                            if (branches != null && !branches.isEmpty()) {
                                for (Branch b : branches) {
                                    b.setType(Branch.TYPE_TAG);
                                }
                                ProjectRefSelectDialog.this.branches.addAll(branches);
                            }
                            show(branch);
                        }

                        @Override
                        public void onFinish() {
                            super.onFinish();
                            loading.dismiss();
                        }
                    });
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                UIHelper.toastMessage(context, "加载分支和标签失败");
            }

            @Override
            public void onFinish() {
                super.onFinish();
                loading.dismiss();
            }
        });
    }

    public void show(final String branch) {
        if (branches == null || branches.isEmpty()) {
            load(branch);
            return;
        }
        if (adapter == null || dialog == null) {
            adapter = new CommonAdapter<Branch>(context, R.layout.list_item_ref) {
                @Override
                public void convert(ViewHolder vh, Branch item) {
                    vh.setText(R.id.tv_flag, item.getIconRes());
                    TypefaceUtils.setOcticons((TextView) vh.getView(R.id.tv_flag));
                    vh.setText(R.id.tv_name, item.getName());
                }
            };
            adapter.addItem(branches);
        }
        int index = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).getName().equals(branch)) {
                index = i;
                break;
            }
        }

        dialog.setSingleChoiceItems(adapter, index, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (adapter.getItem(which).getName().equals(branch)) {
                    return;
                }
                callBack.onCallBack(adapter.getItem(which));
            }
        });
        dialog.show();
    }
}
