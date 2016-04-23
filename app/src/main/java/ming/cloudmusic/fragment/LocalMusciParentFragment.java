package ming.cloudmusic.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import ming.cloudmusic.R;
import ming.cloudmusic.adapter.LocalMusicFragmentPagerAdapter;

/**
 * Created by Lhy on 2016/4/17.
 */
public class LocalMusciParentFragment extends DefaultBaseFragment implements ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener {

    private RadioGroup rgLocalmusic;
    private ViewPager vpLocalmusic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_localmusic, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();

    }

    @Override
    public void initView() {
        View view = getView();
        rgLocalmusic = (RadioGroup) view.findViewById(R.id.rg_localmusic);
        vpLocalmusic = (ViewPager) view.findViewById(R.id.vp_localmusic);

        vpLocalmusic.setAdapter(new LocalMusicFragmentPagerAdapter(getChildFragmentManager()));
        vpLocalmusic.setCurrentItem(0);
        vpLocalmusic.setOffscreenPageLimit(LocalMusicFragmentPagerAdapter.COUNT);

        vpLocalmusic.addOnPageChangeListener(this);
        rgLocalmusic.setOnCheckedChangeListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                rgLocalmusic.check(R.id.rb_single);
                break;
            case 1:
                rgLocalmusic.check(R.id.rb_art);
                break;
            case 2:
                rgLocalmusic.check(R.id.rb_album);
                break;
            case 3:
                rgLocalmusic.check(R.id.rb_file);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_single:
                vpLocalmusic.setCurrentItem(0);
                break;
            case R.id.rb_art:
                vpLocalmusic.setCurrentItem(1);
                break;
            case R.id.rb_album:
                vpLocalmusic.setCurrentItem(2);
                break;
            case R.id.rb_file:
                vpLocalmusic.setCurrentItem(3);
                break;
        }
    }
}
