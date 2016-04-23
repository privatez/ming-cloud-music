package ming.cloudmusic.util;

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

    private List<OnBindActivity> mOnBindActivitys;

    private static FragmentTaskManager sFragmentTaskManager;

    private FragmentTaskManager() {
        mOnBindActivitys = new ArrayList<>();
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
        OnBindActivity onbind = new OnBindActivity(containerViewId, activity.getClass().getSimpleName());
        mOnBindActivitys.add(onbind);
    }

    public void switchFragment(Fragment oldFragment, Class newFragmentClass) {
        FragmentManager fragmentManager = oldFragment.getActivity().getSupportFragmentManager();
        Fragment newFragment = getFragment(newFragmentClass);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.hide(oldFragment).add(mShowingActivityContainerViewId, newFragment).addToBackStack(newFragmentClass.getSimpleName()).commit();
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
        if (mOnBindActivitys == null || mOnBindActivitys.size() == 0) {
            return;
        }
        for (OnBindActivity onBindActivity : mOnBindActivitys) {
            if (activity.getClass().getSimpleName().equals(onBindActivity.activitySimpleName)) {
                mOnBindActivitys.remove(onBindActivity);
            }
        }
        if (mOnBindActivitys.size() > 0) {
            mShowingActivityContainerViewId = mOnBindActivitys.get(mOnBindActivitys.size() - 1).containerViewId;
        }
    }

    private class OnBindActivity {
        public int containerViewId;
        public String activitySimpleName;

        public OnBindActivity(int containerViewId, String activitySimpleName) {
            this.containerViewId = containerViewId;
            this.activitySimpleName = activitySimpleName;
        }
    }
}
