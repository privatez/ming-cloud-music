package ming.cloudmusic.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ming.cloudmusic.R;

/**
 * Created by Lhy on 2016/3/19.
 */
public class HistoryFragment extends DefaultBaseFragment implements View.OnClickListener {

    private TextView tvBack;
    private TextView tvEvent;
    private RelativeLayout rlPlayall;
    private TextView tvMusicsCount;
    private ListView lvMusics;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    @Override
    public void initView() {
        View view = getView();
        lvMusics = (ListView) view.findViewById(R.id.lv_musics);
        tvBack = (TextView) view.findViewById(R.id.tv_back);
        tvEvent = (TextView) view.findViewById(R.id.tv_event);
        rlPlayall = (RelativeLayout) view.findViewById(R.id.rl_playall);
        tvMusicsCount = (TextView) view.findViewById(R.id.tv_musics_count);
        lvMusics = (ListView) view.findViewById(R.id.lv_musics);

        tvBack.setText("最近播放");
        tvEvent.setText("清空");
        tvEvent.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {

    }
}
