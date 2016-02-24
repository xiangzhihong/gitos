package net.oschina.gitapp.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kymjs.rxvolley.client.HttpCallback;

import net.oschina.gitapp.AppApplication;
import net.oschina.gitapp.R;
import net.oschina.gitapp.api.GitOSCApi;
import net.oschina.gitapp.bean.ShippingAddress;
import net.oschina.gitapp.common.StringUtils;
import net.oschina.gitapp.common.UIHelper;
import net.oschina.gitapp.dialog.LightProgressDialog;
import net.oschina.gitapp.ui.baseactivity.BaseActivity;
import net.oschina.gitapp.util.JsonUtils;

import java.util.Map;


/**
 * 用户收货地址界面
 *
 * @author 火蚁（http://my.oschina.net/LittleDY）
 * @created 2014-09-02
 */
public class ShippingAddressActivity extends BaseActivity implements View.OnClickListener {

    private ShippingAddress mShippingAddress;

    private View mContent;

    private ProgressBar mLoading;

    private TextView mName;

    private TextView mTel;

    private TextView mAddress;

    private TextView mComment;

    private Button mPub;

    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping_address);
        initView();
        initData();
    }

    private void initView() {
        mContent = findViewById(R.id.shipping_address_content);
        mLoading = (ProgressBar) findViewById(R.id.shipping_address_loading);
        mName = (TextView) findViewById(R.id.name);
        mTel = (TextView) findViewById(R.id.tell);
        mAddress = (TextView) findViewById(R.id.address);
        mComment = (TextView) findViewById(R.id.comment);
        mPub = (Button) findViewById(R.id.shipping_address_pub);

        TextWatcher mWatcher = new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isEmpty()) {
                    mPub.setEnabled(false);
                } else {
                    mPub.setEnabled(true);
                }
            }
        };

        mName.addTextChangedListener(mWatcher);
        mTel.addTextChangedListener(mWatcher);
        mAddress.addTextChangedListener(mWatcher);

        mPub.setOnClickListener(this);
    }

    private boolean isEmpty() {
        boolean res = false;
        if (StringUtils.isEmpty(mName.getText().toString())
                || StringUtils.isEmpty(mTel.getText().toString())
                || StringUtils.isEmpty(mAddress.getText().toString())) {
            res = true;
        }
        return res;
    }

    private void initData() {
        loadingShippingAddress();
    }

    private void loadingShippingAddress() {

        GitOSCApi.getUserShippingAddress(AppApplication.getInstance().getLoginUid() + "", new
                HttpCallback() {
                    @Override
                    public void onSuccess(Map<String, String> headers, byte[] t) {
                        super.onSuccess(headers, t);
                        ShippingAddress address = JsonUtils.toBean(ShippingAddress.class, t);
                        if (address != null) {
                            mShippingAddress = address;
                            mName.setText(mShippingAddress.getName());
                            mTel.setText(mShippingAddress.getTel());
                            mAddress.setText(mShippingAddress.getAddress());
                            mComment.setText(mShippingAddress.getComment());
                        }
                    }


                    @Override
                    public void onPreStart() {
                        super.onPreStart();
                        mLoading.setVisibility(View.VISIBLE);
                        mContent.setVisibility(View.GONE);
                        mPub.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        mLoading.setVisibility(View.GONE);
                        mContent.setVisibility(View.VISIBLE);
                        mPub.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.shipping_address_pub:
                pubShippingAddress();
                break;
        }
    }

    private void pubShippingAddress() {
        if (mShippingAddress == null) {
            mShippingAddress = new ShippingAddress();
        }
        mShippingAddress.setName(mName.getText().toString());
        mShippingAddress.setTel(mTel.getText().toString());
        mShippingAddress.setAddress(mAddress.getText().toString());
        mShippingAddress.setComment(mComment.getText().toString());
        if (mDialog == null) {
            mDialog = new ProgressDialog(ShippingAddressActivity.this);
            mDialog.setMessage("正在提交保存...");
        }

        final AlertDialog pubing = LightProgressDialog.create(this, "正在提交保存...");
        GitOSCApi.updateUserShippingAddress(AppApplication.getInstance().getLoginUid() + "",
                mShippingAddress, new HttpCallback() {
                    @Override
                    public void onSuccess(Map<String, String> headers, byte[] t) {
                        super.onSuccess(headers, t);
                        UIHelper.toastMessage(ShippingAddressActivity.this, "保存成功");
                        ShippingAddressActivity.this.finish();
                    }

                    @Override
                    public void onFailure(int errorNo, String strMsg) {
                        super.onFailure(errorNo, strMsg);
                        UIHelper.toastMessage(ShippingAddressActivity.this, "保存失败");
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
