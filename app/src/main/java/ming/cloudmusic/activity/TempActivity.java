package ming.cloudmusic.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import ming.cloudmusic.R;

/**
 * Created by Lhy on 2016/3/20.
 */
public class TempActivity extends DefalutBaseActivity implements OnClickListener {

    private TextView tvLogin;
    private TextView tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);

        initView();

    }

    @Override
    public void initView() {
        tvLogin = (TextView) findViewById(R.id.tv_login);
        tvRegister = (TextView) findViewById(R.id.tv_register);

        tvLogin.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.tv_login:
                intent = new Intent(mContext, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_register:
                intent = new Intent(mContext, RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }

}
