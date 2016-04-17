package ming.cloudmusic.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import ming.cloudmusic.R;
import ming.cloudmusic.model.User;
import ming.cloudmusic.util.Constant;
import ming.cloudmusic.util.ToastUtils;

/**
 * Created by Lhy on 2016/3/20.
 */
public class LoginActivity extends DefalutBaseActivity implements View.OnClickListener, View.OnFocusChangeListener {

    private TextView tvBack;
    private TextView tvSubmit;
    private EditText etAccount;
    private EditText etPassword;
    private View vAccount;
    private View vPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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

        tvBack.setText("登录");
        tvBack.setOnClickListener(this);
        tvSubmit.setOnClickListener(this);
        etAccount.setOnFocusChangeListener(this);
        etPassword.setOnFocusChangeListener(this);

    }

    @Override
    public void initData() {

    }

    private boolean canRegister(String account,String password) {

        if (account.length() == 0) {
            ToastUtils.showShort(mContext, "请输入用户名");
            return false;
        }

        if (password.length() == 0) {
            ToastUtils.showShort(mContext, "请输入密码");
            return false;
        }

        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_submit:
                String account = etAccount.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                if (canRegister(account, password))
                    login(account, password);
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

    private void login(final String username, String password) {
        User.loginByAccount(this, username, password, new LogInListener<User>() {

            @Override
            public void done(User user, BmobException e) {
                if (user != null) {
                    mSharedPrefs.setBooleanSP(Constant.SharedPrefrence.AS_USER_LOGGED, true);
                    mSharedPrefs.setStringSP(Constant.SharedPrefrence.USER_ID, user.getObjectId());
                    mSharedPrefs.setStringSP(Constant.SharedPrefrence.USER_NAME, user.getUsername());
                    mSharedPrefs.setBooleanSP(Constant.SharedPrefrence.ISADMIN, user.isAdmin());
                    ToastUtils.showShort(mContext, "登录成功");
                    startActivity(new Intent(mContext, CloudMusicMainActivity.class));
                } else
                    ToastUtils.showShort(mContext, "用户名或密码不正确");
            }
        });
    }

}
