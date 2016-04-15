package ming.cloudmusic.fragment;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import java.util.HashMap;

import ming.cloudmusic.event.EventUtil;
import ming.cloudmusic.util.Constant;
import ming.cloudmusic.util.SharedPrefsUtil;
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

    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (getActivity().getFragmentManager().getBackStackEntryCount() > 1 && event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    getActivity().getFragmentManager().popBackStack();
                    return true;
                }
                return false;
            }
        });
    }

    protected void postEventMsg(String msg) {
        EventUtil.getDefault().postEventMsg(msg, EventUtil.KEY);
    }

    protected void postEventMsgHasExtra(String msg, HashMap mExtras) {
        EventUtil.getDefault().postEventMsgHasExtra(msg, mExtras, EventUtil.KEY);
    }

    protected void refreshBackGround(View backGround, View content) {
        backGround.setVisibility(View.GONE);
        content.setVisibility(View.VISIBLE);
    }

}
