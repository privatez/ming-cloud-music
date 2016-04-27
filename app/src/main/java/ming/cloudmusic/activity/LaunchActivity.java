package ming.cloudmusic.activity;

import android.content.Intent;
import android.os.Bundle;

import ming.cloudmusic.R;
import ming.cloudmusic.util.Constant;

/**
 * Created by Lhy on 2016/3/5.
 */
public class LaunchActivity extends DrawableBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        final boolean asVisitorLogin = mSharedPrefs.getBooleanSP(Constant.SharedPrefrence.AS_VISITOR_LOGGED, false);
        final boolean asUserLogin = mSharedPrefs.getBooleanSP(Constant.SharedPrefrence.AS_USER_LOGGED, false);

        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if (asVisitorLogin || asUserLogin) {
                    intent = new Intent(mContext, CloudMusicMainActivity.class);
                } else {
                    intent = new Intent(mContext, EntranceActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, 1000);

    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }
}
