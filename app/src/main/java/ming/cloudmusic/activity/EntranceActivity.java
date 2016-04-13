package ming.cloudmusic.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import ming.cloudmusic.R;
import ming.cloudmusic.util.Constant;

/**
 * Created by Lhy on 2016/3/20.
 */
public class EntranceActivity extends DefalutBaseActivity implements OnClickListener {

    private TextView tvVisitorLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrance);

        initView();

    }

    @Override
    public void initView() {
        tvVisitorLogin = (TextView) findViewById(R.id.tv_visitor_login);

        findViewById(R.id.tv_login).setOnClickListener(this);
        findViewById(R.id.tv_register).setOnClickListener(this);

        tvVisitorLogin.setOnClickListener(this);

        boolean asVisitorLogin = mSharedPrefs.getBooleanSP(Constant.SharedPrefrence.AS_VISITOR_LOGGED, false);
        boolean asUserLogin = mSharedPrefs.getBooleanSP(Constant.SharedPrefrence.AS_USER_LOGGED, false);

        if (asVisitorLogin || asUserLogin) {
            tvVisitorLogin.setVisibility(View.GONE);
        }

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
            case R.id.tv_visitor_login:
                mSharedPrefs.setBooleanSP(Constant.SharedPrefrence.AS_VISITOR_LOGGED, true);
                intent = new Intent(mContext, CloudMusicMainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

}
