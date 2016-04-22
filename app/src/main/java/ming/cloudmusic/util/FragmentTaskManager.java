package ming.cloudmusic.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * Created by lihaiye on 16/4/22.
 */
public class FragmentTaskManager {

    private FragmentManager mFragmentManager;

    private int mContainerViewId;

    private static FragmentTaskManager sFragmentTaskManager;

    private FragmentTaskManager() {

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

    public void bind(FragmentManager fragmentManager, int containerViewId) {
        mFragmentManager = fragmentManager;
        mContainerViewId = containerViewId;
    }

    public void switchFragment(Fragment oldFragment, Class newFragmentClass) {
        if (mFragmentManager == null) {
            throw new UnInitException("FragmentManager is null !");
        }

        Fragment newFragment = getFragment(newFragmentClass);
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.hide(oldFragment).add(mContainerViewId, newFragment).addToBackStack(newFragmentClass.getSimpleName()).commit();
        mFragmentManager.executePendingTransactions();
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

}
