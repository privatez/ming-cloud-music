package ming.cloudmusic.fragment;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import java.util.HashMap;

import ming.cloudmusic.event.EventUtil;
import ming.cloudmusic.util.Constant;
import ming.cloudmusic.util.SharedPrefsUtil;
import ming.cloudmusic.util.ToastUtils;
import ming.cloudmusic.view.OnViewCreateListener;

/**
 * Created by Lhy on 2016/3/19.
 */
public abstract class DefaultBaseFragment extends Fragment implements OnViewCreateListener {

    protected Context mContext;

    protected SharedPrefsUtil mSharedPrefsUtil;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = getActivity();
        mSharedPrefsUtil = new SharedPrefsUtil(mContext, Constant.SharedPrefrence.SHARED_NAME);
    }

    protected void postEventMsg(String msg) {
        EventUtil.getDefault().postEventMsg(msg, EventUtil.KEY);
    }

    protected void postEventMsgHasExtra(String msg, HashMap mExtras) {
        EventUtil.getDefault().postEventMsgHasExtra(msg, mExtras, EventUtil.KEY);
    }

    public void onBackView() {
        ToastUtils.showShort(mContext, "返回");
    }

    protected void refreshBackGround(View backGround, View content) {
        backGround.setVisibility(View.GONE);
        content.setVisibility(View.VISIBLE);
    }

}
