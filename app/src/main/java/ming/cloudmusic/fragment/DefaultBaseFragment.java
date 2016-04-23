package ming.cloudmusic.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

import ming.cloudmusic.event.EventUtil;
import ming.cloudmusic.util.Constant;
import ming.cloudmusic.util.FragmentTaskManager;
import ming.cloudmusic.util.SharedPrefsUtil;
import ming.cloudmusic.view.OnViewCreateListener;

/**
 * Created by Lhy on 2016/3/19.
 */
public abstract class DefaultBaseFragment extends Fragment implements OnViewCreateListener {

    protected Context mContext;
    protected Fragment mFragment;

    protected SharedPrefsUtil mSharedPrefsUtil;

    protected Map mExtras;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = getActivity();
        mFragment = this;
        mExtras = new HashMap();
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
                if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 1 && event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    getActivity().getSupportFragmentManager().popBackStack();
                    return true;
                }
                return false;
            }
        });
    }

    protected void postEventMsg(String eventMsg) {
        EventUtil.getDefault().postKeyEvent(eventMsg);
    }

    protected void postEventMsgHasExtra(String eventMsg, Map extras) {
        EventUtil.getDefault().postKeyEventHasExtra(eventMsg, extras);
    }

    protected void refreshBackGround(View backGround, View content) {
        backGround.setVisibility(View.GONE);
        content.setVisibility(View.VISIBLE);
    }

    public void switchFragment(Fragment oldFragment, Class newFragmentClass) {
        FragmentTaskManager.getInstance().switchFragment(oldFragment, newFragmentClass);
    }

}
