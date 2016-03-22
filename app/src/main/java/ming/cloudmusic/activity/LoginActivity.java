package ming.cloudmusic.activity;

import android.os.Bundle;
import android.view.View;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;
import ming.cloudmusic.util.LogUtils;
import ming.cloudmusic.util.ToastUtils;

/**
 * Created by Lhy on 2016/3/19.
 */
public class LoginActivity extends DefalutBaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BmobUser user = new BmobUser();
        user.setUsername("lh1y");
        user.setPassword("123456");
        user.signUp(this, new SaveListener() {
            @Override
            public void onSuccess() {
                LogUtils.log("插入数据成功");
                ToastUtils.showShort(mContext, "插入数据成功");
            }

            @Override
            public void onFailure(int i, String s) {
                LogUtils.log("插入数据失败：" + s);
            }
        });
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {

    }
}
