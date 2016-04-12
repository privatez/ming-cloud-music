package ming.cloudmusic.activity;

import android.content.Intent;
import android.os.Bundle;

import ming.cloudmusic.R;

/**
 * Created by Lhy on 2016/3/5.
 */
public class LaunchActivity extends DefalutBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(mContext, CloudMusicMainActivity.class));
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
