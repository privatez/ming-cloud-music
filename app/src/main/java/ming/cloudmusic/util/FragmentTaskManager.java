package ming.cloudmusic.util;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lihaiye on 16/4/22.
 */
public class FragmentTaskManager {

    private int mShowingActivityContainerViewId;

    private List<OnRegisterActivity> OnRegisterActivitys;

    private static FragmentTaskManager sFragmentTaskManager;

    private FragmentTaskManager() {
        mShowingActivityContainerViewId = -1;
        OnRegisterActivitys = new ArrayList<>();
    }

    public static FragmentTaskManager getInstance() {
        if (sFragmentTaskManager == null) {
            synchronized (FragmentTaskManager.class) {
                if (sFragmentTaskManager == null) {
                    sFragmentTaskManager = new FragmentTaskManager();
                }
            }
        }
        return sFragmentTaskManager;
    }

    public void register(FragmentActivity activity, int containerViewId) {
        mShowingActivityContainerViewId = containerViewId;
        OnRegisterActivity onRegisterActivity = new OnRegisterActivity(containerViewId, activity.getClass().getSimpleName());
        OnRegisterActivitys.add(onRegisterActivity);
    }

    public void switchFragment(Fragment oldFragment, Class newFragmentClass) {
        switchFragment(oldFragment, newFragmentClass, null);
    }

    public void switchFragment(Fragment oldFragment, Class newFragmentClass, Bundle bundle) {
        if (mShowingActivityContainerViewId == -1) {
            LogUtils.log("mShowingActivityContainerViewId is -1!");
            return;
        }
        FragmentManager fragmentManager = oldFragment.getActivity().getSupportFragmentManager();
        Fragment newFragment = getFragment(newFragmentClass);
        if (bundle != null) {
            newFragment.setArguments(bundle);
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.hide(oldFragment).add(mShowingActivityContainerViewId, newFragment)
                .addToBackStack(newFragmentClass.getSimpleName()).commit();
    }

    private Fragment getFragment(Class fragmentClass) {
        String fragmentName = fragmentClass.getName();
        Fragment fragment = null;
        try {
            Class c = Class.forName(fragmentName);
            fragment = (Fragment) c.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return fragment;
    }

    public void unregister(FragmentActivity activity) {
        if (OnRegisterActivitys == null || OnRegisterActivitys.isEmpty()) {
            LogUtils.log("unregister OnRegisterActivitys isEmpty");
            return;
        }
        for (OnRegisterActivity onRegisterActivity : OnRegisterActivitys) {
            if (activity.getClass().getSimpleName().equals(onRegisterActivity.activitySimpleName)) {
                OnRegisterActivitys.remove(onRegisterActivity);
            }
        }
        if (!OnRegisterActivitys.isEmpty()) {
            mShowingActivityContainerViewId = OnRegisterActivitys.get(OnRegisterActivitys.size() - 1).containerViewId;
        }
    }

    private class OnRegisterActivity {
        public int containerViewId;
        public String activitySimpleName;

        public OnRegisterActivity(int containerViewId, String activitySimpleName) {
            this.containerViewId = containerViewId;
            this.activitySimpleName = activitySimpleName;
        }
    }
}
