package ming.cloudmusic.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import ming.cloudmusic.R;

/**
 * Created by Lhy on 2016/4/17.
 */
public class LocalMusciFragment extends DefaultBaseFragment {

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


    }

    @Override
    public void initData() {

    }

}
