package ming.cloudmusic.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ming.cloudmusic.R;

/**
 * Created by Lhy on 2016/3/20.
 */
public class RegisterActivity extends DefalutBaseActivity implements View.OnClickListener, View.OnFocusChangeListener {

    private TextView tvBack;
    private TextView tvSubmit;
    private LinearLayout llViewAccount;
    private ImageView ivLineAcconut;
    private LinearLayout llViewPassword;
    private ImageView ivLinePassword;
    private EditText etAccount;
    private EditText etPassword;

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
        llViewAccount = (LinearLayout) findViewById(R.id.ll_view_account);
        ivLineAcconut = (ImageView) findViewById(R.id.iv_line_acconut);
        llViewPassword = (LinearLayout) findViewById(R.id.ll_view_password);
        ivLinePassword = (ImageView) findViewById(R.id.iv_line_password);
        etAccount = (EditText) findViewById(R.id.et_account);
        etPassword = (EditText) findViewById(R.id.et_password);

        tvBack.setText("注册用户");
        tvBack.setOnClickListener(this);
        tvSubmit.setOnClickListener(this);
        etAccount.setOnFocusChangeListener(this);
        etPassword.setOnFocusChangeListener(this);

    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.et_account:
                if (hasFocus) {
                    llViewAccount.setVisibility(View.GONE);
                    ivLineAcconut.setVisibility(View.VISIBLE);
                } else {
                    llViewAccount.setVisibility(View.VISIBLE);
                    ivLineAcconut.setVisibility(View.GONE);
                }
                break;
            case R.id.et_password:
                if (hasFocus) {
                    llViewPassword.setVisibility(View.GONE);
                    ivLinePassword.setVisibility(View.VISIBLE);
                } else {
                    llViewPassword.setVisibility(View.VISIBLE);
                    ivLinePassword.setVisibility(View.GONE);
                }
                break;
        }
    }
}
