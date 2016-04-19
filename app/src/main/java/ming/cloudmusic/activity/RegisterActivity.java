package ming.cloudmusic.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import cn.bmob.v3.listener.SaveListener;
import ming.cloudmusic.R;
import ming.cloudmusic.model.User;
import ming.cloudmusic.util.LogUtils;
import ming.cloudmusic.util.ToastUtils;

/**
 * Created by Lhy on 2016/3/20.
 */
public class RegisterActivity extends DefalutBaseActivity implements View.OnClickListener, View.OnFocusChangeListener {

    private TextView tvBack;
    private TextView tvSubmit;
    private EditText etAccount;
    private EditText etPassword;
    private View vAccount;
    private View vPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();

    }

    @Override
    public void initView() {

        tvBack = (TextView) findViewById(R.id.tv_back);
        tvSubmit = (TextView) findViewById(R.id.tv_submit);
        etAccount = (EditText) findViewById(R.id.et_account);
        etPassword = (EditText) findViewById(R.id.et_password);
        vAccount = findViewById(R.id.v_account);
        vPassword = findViewById(R.id.v_password);

        tvBack.setText("注册用户");
        tvBack.setOnClickListener(this);
        tvSubmit.setOnClickListener(this);
        etAccount.setOnFocusChangeListener(this);
        etPassword.setOnFocusChangeListener(this);

    }

    @Override
    public void initData() {

    }

    private boolean canRegister() {

        if (etAccount.getText().toString().trim().length() == 0) {
            ToastUtils.showShort(mContext, "请设置用户名");
            return false;
        }

        if (etPassword.getText().toString().trim().length() == 0) {
            ToastUtils.showShort(mContext, "请设置密码");
            return false;
        }

        if (etPassword.getText().toString().trim().length() < 6) {
            ToastUtils.showShort(mContext, "密码最少6位");
            return false;
        }

        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_submit:
                if (canRegister())
                    register(etAccount.getText().toString().trim(), etPassword.getText().toString().trim());
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.et_account:
                if (hasFocus) {
                    vAccount.setBackgroundColor(getResColor(R.color.grey));
                } else {
                    vAccount.setBackgroundColor(getResColor(R.color.grey_line));
                }
                break;
            case R.id.et_password:
                if (hasFocus) {
                    vPassword.setBackgroundColor(getResColor(R.color.grey));
                } else {
                    vPassword.setBackgroundColor(getResColor(R.color.grey_line));
                }
                break;
        }
    }

    private void register(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.signUp(this, new SaveListener() {
            @Override
            public void onSuccess() {
                ToastUtils.showShort(mContext, "注册成功");
                finish();
            }

            @Override
            public void onFailure(int code, String msg) {
                if (code == 202 && msg.contains("taken"))
                    ToastUtils.showShort(mContext, "用户名已被注册");
                else
                    LogUtils.log("注册失败+code:" + code + "....msg:" + msg);
            }
        });
    }

}
