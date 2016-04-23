package ming.cloudmusic.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ming.cloudmusic.fragment.LocalMusicChildBaseFragment;

/**
 * Created by Lhy on 2016/4/19.
 */
public class LocalMusicFragmentPagerAdapter extends FragmentPagerAdapter {

    public static final int COUNT = 4;

    public LocalMusicFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = LocalMusicChildBaseFragment.getNewInstance(LocalMusicChildBaseFragment.LOCALMUSIC_SINGLE);
                break;
            case 1:
                fragment = LocalMusicChildBaseFragment.getNewInstance(LocalMusicChildBaseFragment.LOCALMUSIC_ART);
                break;
            case 2:
                fragment = LocalMusicChildBaseFragment.getNewInstance(LocalMusicChildBaseFragment.LOCALMUSIC_ALBUM);
                break;
            case 3:
                fragment = LocalMusicChildBaseFragment.getNewInstance(LocalMusicChildBaseFragment.LOCALMUSIC_FILE);
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return COUNT;
    }
}
